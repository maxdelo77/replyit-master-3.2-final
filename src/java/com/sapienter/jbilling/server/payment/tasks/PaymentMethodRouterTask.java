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

package com.sapienter.jbilling.server.payment.tasks;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.server.payment.PaymentDTOEx;
import com.sapienter.jbilling.server.pluggableTask.PaymentTask;
import com.sapienter.jbilling.server.pluggableTask.admin.ParameterDescription;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskDTO;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskException;

/**
 * Routes payments to a suitable gateway according to the payment method
 * registered for the customer (either Credit Card or ACH). This task
 * determines the appropriate payment processor task to which payment
 * should be routed according to the payment type registered in the
 * payment information or customer record.
 * 
 * @author emirc
 */
public class PaymentMethodRouterTask extends AbstractPaymentRouterTask {

    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(
            PaymentMethodRouterTask.class));
    
    private static final ParameterDescription CREDIT_CARD_DELEGATE = 
    	new ParameterDescription("cc_payment_task", false, ParameterDescription.Type.STR);
    private static final ParameterDescription ACH_DELEGATE = 
    	new ParameterDescription("ach_payment_task", false, ParameterDescription.Type.STR);
    
    //initializer for pluggable params
    { 
    	descriptions.add(CREDIT_CARD_DELEGATE);
        descriptions.add(ACH_DELEGATE);
    }

    
    @Override
    public void initializeParamters(PluggableTaskDTO task)
            throws PluggableTaskException {
        super.initializeParamters(task);
        LOG.debug("Delegate task for credit card payments: " + 
                parameters.get(CREDIT_CARD_DELEGATE));
        LOG.debug("Delegate task for ACH payments: " + 
                parameters.get(ACH_DELEGATE));
    }
    
    @Override
    protected PaymentTask selectDelegate(PaymentDTOEx paymentInfo)
            throws PluggableTaskException {
        
        Integer selectedTaskId = null;
        
        if (paymentInfo.getCreditCard() != null) {
            // Credit card data is present in payment record
            selectedTaskId = new Integer((String)parameters.get(CREDIT_CARD_DELEGATE));
            LOG.debug("Delegating to credit card payment processor");
        } else if (paymentInfo.getAch() != null) {
            // ACH data is present in payment record
            selectedTaskId = new Integer((String)parameters.get(ACH_DELEGATE));
            LOG.debug("Delegating to ACH payment processor");
        }
        
        if (selectedTaskId == null) {
            LOG.warn("Payment data unavailable, unable to route payment");
            return null;
        }
        LOG.debug("Delegating to task id " + selectedTaskId);
        PaymentTask selectedTask = instantiateTask(selectedTaskId);
        return selectedTask;
    }

}
