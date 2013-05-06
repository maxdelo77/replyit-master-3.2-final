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
package com.sapienter.jbilling.server.user.tasks;

import com.sapienter.jbilling.common.Constants;
import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.server.payment.IPaymentSessionBean;
import com.sapienter.jbilling.server.payment.PaymentBL;
import com.sapienter.jbilling.server.payment.PaymentDTOEx;
import com.sapienter.jbilling.server.payment.PaymentSessionBean;
import com.sapienter.jbilling.server.pluggableTask.PluggableTask;
import com.sapienter.jbilling.server.pluggableTask.TaskException;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskException;
import com.sapienter.jbilling.server.system.event.Event;
import com.sapienter.jbilling.server.system.event.task.IInternalEventsTask;
import com.sapienter.jbilling.server.user.UserBL;
import com.sapienter.jbilling.server.user.db.CustomerDTO;
import com.sapienter.jbilling.server.user.db.UserDTO;
import com.sapienter.jbilling.server.user.event.DynamicBalanceChangeEvent;
import com.sapienter.jbilling.server.util.PreferenceBL;

import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Automatic payment task designed to "top up" a customers pre-paid balance with a user
 * configured amount whenever the balance drops below a company wide threshold (configured
 * as a preference).
 *
 * This task subscribes to the {@link DynamicBalanceChangeEvent} which is fired whenever
 * the customers balance changes.
 *
 * @see com.sapienter.jbilling.server.user.balance.DynamicBalanceManagerTask
 *
 * @author Brian Cowdery
 * @since  10-14-2009
 */
public class AutoRechargeTask extends PluggableTask implements IInternalEventsTask {

    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(AutoRechargeTask.class));

    @SuppressWarnings("unchecked")
    private static final Class<Event>[] events = new Class[]{
        DynamicBalanceChangeEvent.class
    };

    public Class<Event>[] getSubscribedEvents() {
        return events;
    }

    public void process(Event event) throws PluggableTaskException {
        if (!(event instanceof DynamicBalanceChangeEvent)) {
            throw new PluggableTaskException("Cannot process event " + event);
        }

        DynamicBalanceChangeEvent balanceEvent = (DynamicBalanceChangeEvent) event;
        UserDTO user = new UserBL(balanceEvent.getUserId()).getDto();
        CustomerDTO customer = user.getCustomer();

        LOG.debug("Processing " + event);

        if (!isEventProcessable(balanceEvent.getNewBalance(), user, customer)) {
            LOG.debug("Conditions not met, no recharge");
            return;
        }

        PaymentDTOEx payment = null;
        try {
            payment = PaymentBL.findPaymentInstrument(event.getEntityId(), user.getId());
        } catch (TaskException e) {
            throw new PluggableTaskException(e);
        }

        if (payment != null) {
            payment.setIsRefund(0);
            payment.setAttempt(1);
            payment.setAmount(customer.getAutoRecharge());
            payment.setCurrency(user.getCurrency());
            payment.setUserId(user.getId());
            payment.setPaymentDate(new Date());

            LOG.debug("Making automatic payment of $" + payment.getAmount() + " for user " + payment.getUserId());

            // can't use the managed bean, a new transaction will cause the CustomerDTO to get an
            // optimistic lock: this transaction and the new payment one both changing the same customer.dynamic_balance
            IPaymentSessionBean paymentSession = new PaymentSessionBean(); 

            Integer result = paymentSession.processAndUpdateInvoice(payment,
                                                                    null,
                                                                    balanceEvent.getEntityId(),
                                                                    balanceEvent.getUserId());

            LOG.debug("Payment created with result: " + result);
        } else {
            LOG.debug("No payment instrument, no recharge");
        }
    }

    /**
     * Returns true if the auto-recharge criteria has been met and this event can be processed.
     *
     * @param newBalance new dynamic balance of the user
     * @param user user to validate
     * @param customer customer to validate
     * @return true if event can be processed, false if not.
     * @throws PluggableTaskException
     */
    private boolean isEventProcessable(BigDecimal newBalance, UserDTO user, CustomerDTO customer) {
        if (customer == null || customer.getAutoRecharge().compareTo(BigDecimal.ZERO) <= 0) {
            LOG.debug("Not a customer, or auto recharge value <= 0");
            return false;
        }

        BigDecimal threshold = getAutoRechargeThreshold(user.getEntity().getId());
        if (threshold == null ) {             
        	LOG.debug("Company does not have a recharge preference.");
        	return false;
        }
        
        LOG.debug("Threshold = " + threshold + ", New Balance=" + newBalance);

        if (Constants.BALANCE_PRE_PAID.equals(customer.getBalanceType()) ) {
        	LOG.debug("Customer Recharge Amt: " + customer.getAutoRecharge() + ", Type: Pre-paid");
        	if (threshold.compareTo(newBalance) > 0) {
        	} else { 
        		LOG.debug("threshold not reached yet.");
        		return false;
        	}
        } else if (Constants.BALANCE_CREDIT_LIMIT.equals(customer.getBalanceType())) {
        	LOG.debug("Credit Limit: " + customer.getCreditLimit() + ", Customer Recharge Amt: " + customer.getAutoRecharge() + ", Type: Credit Limit");
        	if (customer.getCreditLimit() != null && customer.getCreditLimit().compareTo(newBalance) > 0) {
        		LOG.debug("customer has enough credit limit.");
        		return false;
        	} 
        } else {
        	LOG.debug("No Dynamic");
        	return false;
        }
        
        return true;
    }

    /**
     * Returns the set auto-recharge threshold for the given entity id, or null
     * if the company does not have a configured threshold.
     *
     * @param entityId entity id
     * @return auto-recharge threshold or null if not set
     */
    private BigDecimal getAutoRechargeThreshold(Integer entityId) {
        PreferenceBL preference = new PreferenceBL();
        try {
            preference.set(entityId, Constants.PREFERENCE_AUTO_RECHARGE_THRESHOLD);
        } catch (EmptyResultDataAccessException e) {            
            return null; // no threshold set
        }
        return new BigDecimal(preference.getFloat());
    }
    
}
