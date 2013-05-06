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

import com.sapienter.jbilling.common.SessionInternalError;
import com.sapienter.jbilling.server.invoice.db.InvoiceDTO;
import com.sapienter.jbilling.server.payment.blacklist.CsvProcessor;
import com.sapienter.jbilling.server.payment.db.PaymentDTO;
import org.springframework.dao.EmptyResultDataAccessException;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;

/**
 *
 * This is the session facade for the payments in general. It is a statless
 * bean that provides services not directly linked to a particular operation
 *
 * @author emilc
 */
public interface IPaymentSessionBean {

   /**
    * This method goes over all the over due invoices for a given entity and
    * generates a payment record for each of them.
    */
    public void processPayments(Integer entityId) throws SessionInternalError;

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
            throws SessionInternalError;
    
    /**
     * This method soft deletes a payment
     * 
     * @param paymentId
     * @throws SessionInternalError
     */
    public void deletePayment(Integer paymentId) throws SessionInternalError;

    /**
     * It creates the payment record, makes the calls to the authorization
     * processor and updates the invoice if successfull.
     * 
     * @param dto
     * @param invoice
     * @throws SessionInternalError
     */
    public Integer processAndUpdateInvoice(PaymentDTOEx dto, InvoiceDTO invoice, Integer executorUserId)
            throws SessionInternalError;
    
    /**
     * This is called from the client to process real-time a payment, usually
     * cc. 
     * 
     * @param dto
     * @param invoiceId
     * @throws SessionInternalError
     */
    public Integer processAndUpdateInvoice(PaymentDTOEx dto, Integer invoiceId,
            Integer entityId, Integer executorUserId) throws SessionInternalError;

    /**
     * This is called from the client to apply an existing payment to 
     * an invoice. 
     */
    public void applyPayment(Integer paymentId, Integer invoiceId);

    /**
     * Applys a payment to an invoice, updating the invoices fields with
     * this payment.
     * @param payment
     * @param invoice
     * @param success
     * @throws SessionInternalError
     */
    public BigDecimal applyPayment(PaymentDTO payment, InvoiceDTO invoice,
            boolean success) throws SQLException;

    /**
     * This method is called from the client, when a payment needs only to 
     * be applyed without realtime authorization by a processor
     * Finds this invoice entity, creates the payment record and calls the 
     * apply payment  
     * Id does suport invoiceId = null because it is possible to get a payment
     * that is not paying a specific invoice, a deposit for prepaid models.
     */
    public Integer applyPayment(PaymentDTOEx payment, Integer invoiceId, Integer executorUserId)  
            throws SessionInternalError;
    
    public PaymentDTOEx getPayment(Integer id, Integer languageId) 
            throws SessionInternalError;
    
    public boolean isMethodAccepted(Integer entityId, Integer paymentMethodId) 
            throws SessionInternalError;
    
    public Integer processPayout(PaymentDTOEx payment, Date start, Date end, 
            Integer partnerId, Boolean process) throws SessionInternalError;

    public Boolean processPaypalPayment(Integer invoiceId, String entityEmail,
            BigDecimal amount, String currency, Integer paramUserId, 
            String userEmail) throws SessionInternalError;
    
    /** 
     * Clients with the right priviliges can update payments with result
     * 'entered' that are not linked to an invoice
     */
    public void update(Integer executorId, PaymentDTOEx dto) 
            throws SessionInternalError, EmptyResultDataAccessException;
    
    /** 
     * Removes a payment-invoice link
     */
    public void removeInvoiceLink(Integer mapId);

    /** 
     * Processes the blacklist CSV file specified by filePath.
     * It will either add to or replace the existing uploaded 
     * blacklist for the given entity (company). Returns the number
     * of new blacklist entries created.
     */
    public int processCsvBlacklist(String filePath, boolean replace, 
            Integer entityId) throws CsvProcessor.ParseException;
}
