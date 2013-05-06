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

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.server.invoice.db.InvoiceDTO;
import com.sapienter.jbilling.server.payment.PaymentDTOEx;
import com.sapienter.jbilling.server.pluggableTask.PaymentTask;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskException;

/**
 * Routes payments to other processor plug-ins based on currency. 
 * To configure the routing, set the parameter name to the currency 
 * code and the parameter value to the processor plug-in id.
 */
public class PaymentRouterCurrencyTask extends AbstractPaymentRouterTask {
    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(
            PaymentRouterCurrencyTask.class));

    @Override
    protected PaymentTask selectDelegate(PaymentDTOEx paymentInfo)
            throws PluggableTaskException {
        String currencyCode = paymentInfo.getCurrency().getCode();
        Integer selectedTaskId = null;

        try {
            // try to get the task id for this currency
            selectedTaskId = intValueOf(parameters.get(currencyCode));
        } catch (NumberFormatException e) {
            throw new PluggableTaskException("Invalid task id for currency " +
                    "code: " + currencyCode);
        }
        if (selectedTaskId == null) {
            LOG.warn("Could not find processor for " + parameters.get(currencyCode));
            return null;
        }

        LOG.debug("Delegating to task id " + selectedTaskId);
        PaymentTask selectedTask = instantiateTask(selectedTaskId);

        return selectedTask;
    }

    @Override
    public Map<String, String> getAsyncParameters(InvoiceDTO invoice) 
            throws PluggableTaskException {
        String currencyCode = invoice.getCurrency().getCode();
        Map<String, String> parameters = new HashMap<String, String>(1);
        parameters.put("currency", currencyCode);
        return parameters;
    }
}
