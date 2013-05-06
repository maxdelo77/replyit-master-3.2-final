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

import java.util.Map;

import javax.jms.MapMessage;

import com.sapienter.jbilling.server.invoice.InvoiceBL;
import com.sapienter.jbilling.server.invoice.db.InvoiceDTO;
import com.sapienter.jbilling.server.pluggableTask.PluggableTask;
import com.sapienter.jbilling.server.pluggableTask.TaskException;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskManager;
import com.sapienter.jbilling.server.util.Constants;

public class RouterAsyncParameters extends PluggableTask implements IAsyncPaymentParameters  {

    public void addParameters(MapMessage message) throws TaskException {
        try {
            InvoiceBL invoiceBl = new InvoiceBL(message.getInt("invoiceId"));
            Integer entityId = invoiceBl.getEntity().getBaseUser().getEntity().getId();
            InvoiceDTO invoice = invoiceBl.getDTO();
            
            PluggableTaskManager taskManager = new PluggableTaskManager(entityId, 
                    Constants.PLUGGABLE_TASK_PAYMENT);

            // search for PaymentRouterTask in the payment chain
            AbstractPaymentRouterTask router = null;
            Object task = taskManager.getNextClass();
            while (task != null) {
                if (task instanceof AbstractPaymentRouterTask) {
                    router = (AbstractPaymentRouterTask) task;
                    break;
                }
                task = taskManager.getNextClass();
            }
            
            if (router == null) {
                throw new TaskException("Can not find router task");
            }

            Map<String, String> parameters = router.getAsyncParameters(invoice);
            for(Map.Entry<String, String> parameter : parameters.entrySet()) {
                message.setStringProperty(parameter.getKey(), 
                        parameter.getValue());
            }
        } catch (Exception e) {
            throw new TaskException(e);
        } 
    }

}
