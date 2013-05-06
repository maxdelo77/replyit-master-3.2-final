/*
 * JBILLING CONFIDENTIAL
 * _____________________
 *
 * [2003] - [2012] Enterprise jBilling Software Ltd.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Enterprise jBilling Software.
 * The intellectual and technical concepts contained
 * herein are proprietary to Enterprise jBilling Software
 * and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden.
 */

package com.sapienter.jbilling.server.payment;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;

import com.sapienter.jbilling.server.order.OrderBL;
import com.sapienter.jbilling.server.payment.db.*;
import com.sapienter.jbilling.server.payment.db.*;

import org.apache.log4j.Logger;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.sapienter.jbilling.common.CommonConstants;
import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.common.SessionInternalError;
import com.sapienter.jbilling.server.invoice.InvoiceBL;
import com.sapienter.jbilling.server.invoice.db.InvoiceDTO;
import com.sapienter.jbilling.server.item.CurrencyBL;
import com.sapienter.jbilling.server.notification.INotificationSessionBean;
import com.sapienter.jbilling.server.notification.MessageDTO;
import com.sapienter.jbilling.server.notification.NotificationBL;
import com.sapienter.jbilling.server.payment.blacklist.CsvProcessor;
import com.sapienter.jbilling.server.payment.event.PaymentFailedEvent;
import com.sapienter.jbilling.server.payment.event.PaymentSuccessfulEvent;
import com.sapienter.jbilling.server.process.AgeingBL;
import com.sapienter.jbilling.server.process.ConfigurationBL;
import com.sapienter.jbilling.server.system.event.EventManager;
import com.sapienter.jbilling.server.user.UserBL;
import com.sapienter.jbilling.server.user.db.CustomerDTO;
import com.sapienter.jbilling.server.user.db.UserDAS;
import com.sapienter.jbilling.server.user.db.UserDTO;
import com.sapienter.jbilling.server.user.partner.PartnerBL;
import com.sapienter.jbilling.server.user.partner.db.Partner;
import com.sapienter.jbilling.server.util.Constants;
import com.sapienter.jbilling.server.util.Context;
import com.sapienter.jbilling.server.util.PreferenceBL;
import com.sapienter.jbilling.server.util.audit.EventLogger;
import com.sapienter.jbilling.server.util.db.CurrencyDAS;

/**
 *
 * This is the session facade for the payments in general. It is a statless
 * bean that provides services not directly linked to a particular operation
 *
 * @author emilc
 */
@Transactional( propagation = Propagation.REQUIRED )
public class PaymentSessionBean implements IPaymentSessionBean {

    private final FormatLogger LOG = new FormatLogger(Logger.getLogger(PaymentSessionBean.class));

   /**
    * This method goes over all the over due invoices for a given entity and
    * generates a payment record for each of them.
    */
    public void processPayments(Integer entityId) throws SessionInternalError {
        try {
            entityId.intValue(); // just to avoid the warning ;)
        } catch (Exception e) {
            throw new SessionInternalError(e);
        }
    }

    /** 
    * This is meant to be called from the billing process, where the information
    * about how the payment is going to be done is not known. This method will
    * call a pluggable task that finds this information (usually a cc) before
    * calling the realtime processing.
    * Later, this will have to be changed for some file creation with all the
    * payment information to be sent in a batch mode to the processor at the 
    * end of the billing process. 
    * This is called only if the user being process has as a preference to 
    * process the payment with billing process, meaning that a payment has
    * to be created and processed real-time.
    * @return If the payment was not successful for any reason, null, 
    * otherwise the payment method used for the payment
    */
    public Integer generatePayment(InvoiceDTO invoice) 
            throws SessionInternalError {
        
        LOG.debug("Generating payment for invoice %s", invoice.getId());
        // go fetch the entity for this invoice
        Integer userId = invoice.getBaseUser().getUserId();
        UserDAS userDas = new UserDAS();
        Integer entityId = userDas.find(userId).getCompany().getId();
        Integer retValue = null;
        // create the dto with the information of the payment to create
        try {
            // get this payment information. Now we only expect one pl.tsk
            // to get the info, I don't see how more could help
            PaymentDTOEx dto = PaymentBL.findPaymentInstrument(entityId,
                    invoice.getBaseUser().getUserId());
            
            boolean noInstrument = false;
            if (dto == null) {
                noInstrument = true;
                dto = new PaymentDTOEx();
            }

            dto.setIsRefund(new Integer(0)); //it is not a refund
            dto.setUserId(userId);
            dto.setAmount(invoice.getBalance());
            dto.setCurrency(new CurrencyDAS().find(invoice.getCurrency().getId()));
            dto.setAttempt(new Integer(invoice.getPaymentAttempts() + 1));
            // when the payment is generated by the system (instead of
            // entered manually by a user), the payment date is sysdate
            dto.setPaymentDate(Calendar.getInstance().getTime());

            LOG.debug("Prepared payment %s", dto);
            // it could be that the user doesn't have a payment 
            // instrument (cc) in the db, or that is invalid (expired).
            if (!noInstrument) {
                Integer result = processAndUpdateInvoice(dto, invoice, null);
                LOG.debug("After processing. Result= %s", result);
                if (result != null && result.equals(Constants.RESULT_OK)) {
                    retValue = dto.getPaymentMethod().getId();
                }
            } else {
                // audit that this guy was about to get a payment
                EventLogger logger = new EventLogger();
                logger.auditBySystem(entityId, userId, Constants.TABLE_BASE_USER, userId, 
                        EventLogger.MODULE_PAYMENT_MAINTENANCE, EventLogger.PAYMENT_INSTRUMENT_NOT_FOUND,
                        null, null, null);
                // update the invoice attempts
                invoice.setPaymentAttempts(dto.getAttempt() == null ? 
                        new Integer(1) : dto.getAttempt());
                // treat this as a failed payment
                PaymentFailedEvent event = new PaymentFailedEvent(entityId, dto);
                EventManager.process(event);
            }
            
        } catch (Exception e) {
            LOG.fatal("Problems generating payment.", e);
            throw new SessionInternalError(
                "Problems generating payment.");
        } 
        
        LOG.debug("Done. Returning:%s", retValue);
        return retValue;
    }
    
    /**
     * This method soft deletes a payment
     * 
     * @param paymentId
     * @throws SessionInternalError
     */
    public void deletePayment(Integer paymentId) throws SessionInternalError {

        try {
            PaymentBL bl = new PaymentBL(paymentId);
            bl.delete();

        } catch (Exception e) {
            LOG.warn("Problem deleteing payment.", e);
            throw new SessionInternalError("Problem deleteing payment");
        }
    }
    
    
    /**
     * It creates the payment record, makes the calls to the authorization
     * processor and updates the invoice if successfull.
     * 
     * @param dto
     * @param invoice
     * @throws SessionInternalError
     */
    public Integer  processAndUpdateInvoice(PaymentDTOEx dto,
            InvoiceDTO invoice, Integer executorUserId) throws SessionInternalError {
        try {
            if(invoice.getIsReview().compareTo(1)==0){
                LOG.debug("Invoice is a review invoice, can not process payment against it.");
                throw new SessionInternalError("Invoice is a review invoice, can not process payment against it.");
            }

            PaymentBL bl = new PaymentBL();
            Integer entityId = invoice.getBaseUser().getEntity().getId();
            
            // set the attempt
            if (dto.getIsRefund() == 0) {
                // take the attempt from the invoice
                dto.setAttempt(new Integer(invoice.getPaymentAttempts() + 1));
            } else { // is a refund
                dto.setAttempt(new Integer(1));
            } 
                
            // payment notifications require some fields from the related
            // invoice
            dto.getInvoiceIds().add(invoice.getId());
                
            // process the payment (will create the db record as well, if
            // there is any actual processing). Do not process negative
            // payments (from negative invoices), unless allowed.
            Integer result = null;
            if (dto.getAmount().compareTo(BigDecimal.ZERO) > 0) {
                result = bl.processPayment(entityId, dto, executorUserId);
            } else {
                // only process if negative payments are allowed
                PreferenceBL preferenceBL = new PreferenceBL();
                try {
                    preferenceBL.set(entityId, 
                            Constants.PREFERENCE_ALLOW_NEGATIVE_PAYMENTS);
                } catch (EmptyResultDataAccessException fe) { 
                    // use default
                }
                if (preferenceBL.getInt() == 1) {
                    LOG.warn("Processing payment with negative amount %s",
                            dto.getAmount());
                    result = bl.processPayment(entityId, dto, executorUserId);
                } else {
                    LOG.warn("Skiping payment processing. Payment with negative amount %s", dto.getAmount());
                }
            }

            LOG.debug("Processed payment with result %d", result);
            // only if there was any processing at all
            if (result != null) {
                // update the dto with the created id
                dto.setId(bl.getEntity().getId());
                // the balance will be the same as the amount
                // if the payment failed, it won't be applied to the invoice
                // so the amount will be ignored
                dto.setBalance(dto.getAmount());
                
                // after the process, update the payment record
                bl.getEntity().setPaymentResult(new PaymentResultDAS().find(result));

                // Note: I could use the return of the last call to fetch another
                // dto with a different cc number to retry the payment
                    
                // get all the invoice's fields updated with this payment
                BigDecimal paid = applyPayment(dto, invoice, result.equals(Constants.RESULT_OK));
                if (dto.getIsRefund() == 0) {
                    // now update the link between invoice and payment
                    bl.createMap(invoice, paid);
                }
            }
            return result;
        } catch (Exception e) {
            throw new SessionInternalError(e);
        }
    }
    
    /**
     * This is called from the client to process real-time a payment, usually cc. 
     * 
     * @param dto
     * @param invoiceId
     * @throws SessionInternalError
     */
    public Integer processAndUpdateInvoice(PaymentDTOEx dto, 
            Integer invoiceId, Integer entityId, Integer executorUserId) throws SessionInternalError {
        try {
        	//for refunds, invoiceIds must be populated
        	if (dto.getIsRefund() == 1) {
        		PaymentBL linkedPayment = new PaymentBL(dto.getPayment().getId());

        		if (!linkedPayment.getEntity().getInvoicesMap().isEmpty()) {
        			LOG.debug("The refunds linked payment has some paid Invoices.");
                    for (PaymentInvoiceMapDTO entry : linkedPayment.getEntity().getInvoicesMap()) {
                        dto.getPayment().getInvoiceIds().add(entry.getInvoiceEntity().getId());
                    }
        		}
        	}
        	
            if (dto.getIsRefund() == 0 && invoiceId != null) {
                InvoiceBL bl = new InvoiceBL(invoiceId);
                List<Integer> inv = new ArrayList<Integer>();
                inv.add(invoiceId);
                dto.setInvoiceIds(inv);
                return processAndUpdateInvoice(dto, bl.getEntity(), executorUserId);

            } else {
            	
            	if (dto.getIsRefund() == 1
                        && dto.getPayment() != null ) {
            	    /* && !dto.getPayment().getInvoiceIds().isEmpty()){*/
                    /*InvoiceBL bl = new InvoiceBL((Integer) dto.getPayment().getInvoiceIds().get(0));
                    return processAndUpdateInvoice(dto, bl.getEntity(), executorUserId);*/
                    LOG.debug("We changed the rules, you can't refund the Payment amount linked to Invoice. So no need to involve invoices in Refunds.");
                } 
                // without an invoice, it's just creating the payment row
                // and calling the processor
                LOG.info("The payment may be a refund and its linked payment has no invoices connected to it");
                
                PaymentBL bl = new PaymentBL();
                Integer result = bl.processPayment(entityId, dto, executorUserId);

                if (result != null) {
                    bl.getEntity().setPaymentResult(new PaymentResultDAS().find(result));
                }

                if (result != null && result.equals(Constants.RESULT_OK)) {
                	
                	LOG.debug("A successful Refund. But original payment not linked to Invoices.");
                	//Therefore, will not be auto-applied to other Invoices 
                	//The refund then in-fact must reduce the Payment Balance it is linked to.
                	if (dto.getIsRefund() == 1) {
                		LOG.debug("Linked payment balance after refund application %s", bl.getEntity().getPayment().getBalance());
                	} else {
                		// if the configured, pay any unpaid invoices
                		ConfigurationBL config = new ConfigurationBL(entityId);
                		if (config.getEntity().getAutoPaymentApplication() == 1) {
                			bl.automaticPaymentApplication();
                		}
                	}
                }

                return result;
            }
        } catch (Exception e) {
            throw new SessionInternalError(e);
        }
    }
    /**
     * This is called from the client to apply an existing payment to 
     * an invoice. 
     */
    public void applyPayment(Integer paymentId, Integer invoiceId) {
        LOG.debug("Applying payment %s to invoice %s", paymentId, invoiceId);
        if (paymentId == null || invoiceId == null) {
            LOG.warn("Got null parameters to apply a payment");
            return;
        }

        try {
            PaymentBL payment = new PaymentBL(paymentId);
            InvoiceBL invoice = new InvoiceBL(invoiceId);
            
            BigDecimal paid = applyPayment(payment.getEntity(), invoice.getEntity(), true);
            
            // link it with the invoice
            payment.createMap(invoice.getEntity(), paid);
        } catch (Exception e) {
            throw new SessionInternalError(e);
        } 
                
    }
    /**
     * Applys a payment to an invoice, updating the invoices fields with
     * this payment.
     * @param payment
     * @param invoice
     * @param success
     * @throws SessionInternalError
     */
    public BigDecimal applyPayment(PaymentDTO payment, InvoiceDTO invoice, boolean success) throws SQLException {
        BigDecimal totalPaid = BigDecimal.ZERO;

        if (invoice != null) {
            // set the attempt of the invoice
            LOG.debug("applying payment to invoice %s", invoice.getId());
            if (payment.getIsRefund() == 0) {
                //invoice can't take nulls. Default to 1 if so.
                invoice.setPaymentAttempts(payment.getAttempt() == null ? new Integer(1) : payment.getAttempt());
            }

            if (success) {
                // update the invoice's balance if applicable
                BigDecimal balance = invoice.getBalance();
                // get current invoice balance
                LOG.debug("current invoice balance is %s", balance);
                if (balance != null) {
                    boolean balanceSign = (balance.compareTo(BigDecimal.ZERO) < 0) ? false : true;
                    LOG.debug("balance sign is %s", balanceSign);
                    BigDecimal newBalance = null;
                    if (payment.getIsRefund() == 0) {
                        LOG.debug("payment is a normal payment");
                        newBalance = balance.subtract(payment.getBalance());
                        LOG.debug("new balance is %s",newBalance);
                        // I need the payment record to update its balance
                        if (payment.getId() == 0) {
                            throw new SessionInternalError("The ID of the payment to has to be present in the DTO");
                        }
                        PaymentBL paymentBL = new PaymentBL(payment.getId());
                        BigDecimal paymentBalance = payment.getBalance().subtract(balance);
                        LOG.debug("payment balance is %s",paymentBalance);
                        // payment balance cannot be negative, must be at least zero
                        if (BigDecimal.ZERO.compareTo(paymentBalance) > 0) {
                            LOG.debug("setting the paymentBalance which was %s to ZERO", paymentBalance);
                            paymentBalance = BigDecimal.ZERO;
                        }

                        totalPaid = payment.getBalance().subtract(paymentBalance);

                        paymentBL.getEntity().setBalance(paymentBalance);
                        payment.setBalance(paymentBalance);
                        
                        // only level the balance if the original balance wasn't negative
                        if (newBalance.compareTo(Constants.BIGDECIMAL_ONE_CENT) < 0 && balanceSign) {
                            LOG.debug("new balance is "+newBalance+ " and BIGDECIMAL_ONE_CENT is "+Constants.BIGDECIMAL_ONE_CENT+ " and balance sign is "+balanceSign);
                            LOG.debug("setting the new balance to ZERO");
                            // the payment balance was greater than the invoice's
                            newBalance = BigDecimal.ZERO;
                        }
                        
                        invoice.setBalance(newBalance);
                        LOG.debug("Set invoice balance to: " + invoice.getBalance());
                                            
                        if (BigDecimal.ZERO.compareTo(newBalance) == 0) {
                            // update the to_process flag if the balance is 0
                            invoice.setToProcess(new Integer(0));
                        } else {
                            // a refund might make this invoice payabale again
                            invoice.setToProcess(new Integer(1));
                        }

                    } else { // refunds add to the invoice                        
                    	LOG.debug("Refunds do not add to Invoice anymore. You cannot refund a Payment that is linked to an Invoce. First un-link it.");
                    }
                        
                } else {
                    // with no balance, we assume the the invoice got all paid
                    LOG.debug("The balance of the invoice is %s",balance);
                    invoice.setToProcess(new Integer(0));
                }

                // if the user is in the ageing process, she should be out
                if (new Integer(invoice.getToProcess()).equals(new Integer(0))) {
                    AgeingBL ageing = new AgeingBL();
                    ageing.out(invoice.getBaseUser(), invoice.getId());
                }

                // update the partner if this customer belongs to one
                CustomerDTO customer = invoice.getBaseUser().getCustomer();
                if (customer != null && customer.getPartner() != null) {
                    Partner partner = customer.getPartner();
                    BigDecimal pBalance = partner.getBalance();
                    BigDecimal paymentAmount = payment.getAmount();

                    if (payment.getIsRefund() == 0) {
                        pBalance = pBalance.add(paymentAmount);
                        paymentAmount = paymentAmount.add(partner.getTotalPayments());
                        partner.setTotalPayments(paymentAmount);
                        
                    } else {
                        pBalance = pBalance.subtract(paymentAmount);
                        paymentAmount = paymentAmount.add(partner.getTotalRefunds());
                        partner.setTotalRefunds(paymentAmount);
                    }
                    partner.setBalance(pBalance);
                } 
            }
        }
        return totalPaid;
    }

    /**
     * This method is called from the client, when a payment needs only to 
     * be applyed without realtime authorization by a processor
     * Finds this invoice entity, creates the payment record and calls the 
     * apply payment  
     * Id does suport invoiceId = null because it is possible to get a payment
     * that is not paying a specific invoice, a deposit for prepaid models.
     */
    public Integer applyPayment(PaymentDTOEx payment, Integer invoiceId, Integer executorUserId)  
            throws SessionInternalError {
        LOG.debug("payment dtoex contains %s",payment);
        try {
            // create the payment record
            PaymentBL paymentBl = new PaymentBL();
            // set the attempt to an initial value, if the invoice is there,
            // it's going to be updated
            payment.setAttempt(new Integer(1));
            // a payment that is applied, has always the same result
            payment.setPaymentResult(new PaymentResultDAS().find(Constants.RESULT_ENTERED));
            payment.setBalance(payment.getAmount());
            paymentBl.create(payment, executorUserId);
            // this is necessary for the caller to get the Id of the
            // payment just created
            payment.setId(paymentBl.getEntity().getId());
            
            boolean wasPaymentApplied= false;
            
            if (payment.getIsRefund() == 0) { // normal payment
                if (invoiceId != null) {
                    // find the invoice
                    InvoiceBL invoiceBl = new InvoiceBL(invoiceId);
                    // set the attmpts from the invoice
                    payment.setAttempt(new Integer(invoiceBl.getEntity().getPaymentAttempts() + 1));
                    // apply the payment to the invoice
                    BigDecimal paid = applyPayment(payment, invoiceBl.getEntity(), true);
                    // link it with the invoice
                    paymentBl.createMap(invoiceBl.getEntity(), paid);
                    
                    //payment was applied successfully
                    wasPaymentApplied= true;
                } else {
                    // this payment was done without an explicit invoice
                    // We'll try to link it to invoices with balances then provided automatic payment is set
                	Integer userId= payment.getUserId();
                	UserDTO userDTO= new UserDAS().find(userId);
                	if (null != userDTO) {
	                	ConfigurationBL config = new ConfigurationBL(userDTO.getCompany().getId());
	                    if (config.getEntity().getAutoPaymentApplication() == 1) {
	                        wasPaymentApplied= paymentBl.automaticPaymentApplication();
	                    }
                	}
                }
                // let know about this payment with an event
                PaymentSuccessfulEvent event = new PaymentSuccessfulEvent(
                        paymentBl.getEntity().getBaseUser().getEntity().getId(),payment);
                EventManager.process(event);
            } else {
                LOG.debug("payment is linked to payment %s and may be linked with invoice", payment.getPayment());
                
                // fetch the linked payment from database
                PaymentDTO linkedPayment = new PaymentBL(payment.getPayment().getId()).getEntity();
                
                if ( null != linkedPayment ) {
	                /*
	                 * Since payment is not linked to any invoice now, 
	                 * we must subtract the payment balance with that of the refund payment value
                	*/
	                linkedPayment.setBalance(linkedPayment.getBalance().subtract(payment.getAmount()));
                }
                else {
                    LOG.debug("This refund is not linked with any payment which is wrong");
                    //maybe throw exception
                }
            }
            
            //should we notify the customer of this payment
            if (wasPaymentApplied) {
                //this notification prevents multiple notifications sent for each application of the payment to an Invoice
                LOG.debug("Invoking Payment notification for the Payment Entered since it was applied to atleast 1 Invoice.");
                paymentBl.sendNotification(payment, new UserDAS().find(payment.getUserId()).getCompany().getId(), true);
            }
            return paymentBl.getEntity().getId();
        } catch (Exception e) {
            throw new SessionInternalError(e);
        }
    }  
    
    public PaymentDTOEx getPayment(Integer id, Integer languageId) 
            throws SessionInternalError {
        try {
            PaymentBL bl = new PaymentBL(id);
            return bl.getDTOEx(languageId);
        } catch (Exception e) {
            throw new SessionInternalError(e);
        }
    }
    
    public boolean isMethodAccepted(Integer entityId, 
            Integer paymentMethodId) 
            throws SessionInternalError {
        if (paymentMethodId == null) {
            // if this is a credit card and it has not been
            // identified by the first digit, the method will be null
            return false;
        }
        try {
            PaymentBL bl = new PaymentBL();
            
            return bl.isMethodAccepted(entityId, paymentMethodId);
        } catch (Exception e) {
            throw new SessionInternalError(e);
        }
    } 
    
    @Transactional( propagation = Propagation.REQUIRES_NEW )
    public Integer processPayout(PaymentDTOEx payment, 
            Date start, Date end, Integer partnerId, Boolean process) 
            throws SessionInternalError {
        try {
            PartnerBL partner = new PartnerBL();
            return partner.processPayout(partnerId, start, end, payment,
                    process);
        } catch (Exception e) {
            throw new SessionInternalError(e);
        }
    }

    @Transactional( propagation = Propagation.REQUIRES_NEW )
    public Boolean processPaypalPayment(Integer invoiceId, String entityEmail,
            BigDecimal amount, String currency, Integer paramUserId, String userEmail) 
            throws SessionInternalError {
        
        if (userEmail == null && invoiceId == null && paramUserId == null) {
            LOG.debug("Too much null, returned");
            return false;
        }
        try {
            boolean ret = false;
            InvoiceBL invoice = null;
            Integer entityId = null;
            Integer userId = null;
            CurrencyBL curr = null;
            if (invoiceId != null) {
                invoice = new InvoiceBL(invoiceId);
                entityId = invoice.getEntity().getBaseUser().getEntity().getId();
                userId = invoice.getEntity().getBaseUser().getUserId();
                curr = new CurrencyBL(
                        invoice.getEntity().getCurrency().getId());
            } else {
                UserBL user = new UserBL();
                // identify the user some other way
                if (paramUserId != null) {
                    // easy
                    userId = paramUserId;
                } else {
                    // find a user by the email address
                    userId = user.getByEmail(userEmail);
                    if (userId == null) {
                        LOG.debug("Could not find a user for email %s", userEmail);
                        return false;
                    }
                }
                user = new UserBL(userId);
                entityId = user.getEntityId(userId);
                curr = new CurrencyBL(user.getCurrencyId());
            }
            
            // validate the entity
            PreferenceBL pref = new PreferenceBL();
            pref.set(entityId, Constants.PREFERENCE_PAYPAL_ACCOUNT);
            String paypalAccount = pref.getString();
            if (paypalAccount != null && paypalAccount.equals(entityEmail)) {
                // now the currency
                if (curr.getEntity().getCode().equals(currency)) {
                    // all good, make the payment
                    PaymentDTOEx payment = new PaymentDTOEx();
                    payment.setAmount(amount);
                    payment.setPaymentMethod(new PaymentMethodDAS().find(Constants.PAYMENT_METHOD_PAYPAL));
                    payment.setUserId(userId);
                    payment.setCurrency(curr.getEntity());
                    payment.setCreateDatetime(Calendar.getInstance().getTime());
                    payment.setPaymentDate(Calendar.getInstance().getTime());
                    payment.setIsRefund(new Integer(0));
                    applyPayment(payment, invoiceId, null);
                    ret = true;
                    
                    // notify the customer that the payment was received
                    NotificationBL notif = new NotificationBL();
                    MessageDTO message = notif.getPaymentMessage(entityId, 
                            payment, true);
                    INotificationSessionBean notificationSess = 
                            (INotificationSessionBean) Context.getBean(
                            Context.Name.NOTIFICATION_SESSION);
                    notificationSess.notify(payment.getUserId(), message);
                    
                    // link to unpaid invoices
                    // TODO avoid sending two emails
                    PaymentBL bl = new PaymentBL(payment);
                    bl.automaticPaymentApplication();
                } else {
                    LOG.debug("wrong currency %s", currency);
                }
            } else {
                LOG.debug("wrong entity paypal account %s %s", paypalAccount, entityEmail);
            }
            
            return new Boolean(ret);
        } catch (Exception e) {
            throw new SessionInternalError(e);
        }
    }
    
    /** 
     * Clients with the right priviliges can update payments with result
     * 'entered' that are not linked to an invoice
     */
    public void update(Integer executorId, PaymentDTOEx dto) 
            throws SessionInternalError, EmptyResultDataAccessException {
        if (dto.getId() == 0) {
            throw new SessionInternalError("ID missing in payment to update");
        }
        
        LOG.debug("updateting payment %s", dto.getId());
        PaymentBL bl = new PaymentBL(dto.getId());
        if (new Integer(bl.getEntity().getPaymentResult().getId()).equals(Constants.RESULT_ENTERED)) {
                
        } else {
            throw new SessionInternalError("Payment update only available" +
                    " for entered payments");
        }
            
        bl.update(executorId, dto);
    }
    
    /** 
     * Removes a payment-invoice link
     */
    public void removeInvoiceLink(Integer mapId) {
        PaymentBL payment = new PaymentBL();
        payment.removeInvoiceLink(mapId);
    }

    /** 
     * Processes the blacklist CSV file specified by filePath.
     * It will either add to or replace the existing uploaded 
     * blacklist for the given entity (company). Returns the number
     * of new blacklist entries created.
     */
    public int processCsvBlacklist(String filePath, boolean replace, 
            Integer entityId) throws CsvProcessor.ParseException {
        CsvProcessor processor = new CsvProcessor();
        return processor.process(filePath, replace, entityId);
    }
}
