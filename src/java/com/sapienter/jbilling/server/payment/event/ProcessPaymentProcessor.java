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
package com.sapienter.jbilling.server.payment.event;

import javax.jms.Destination;

import com.sapienter.jbilling.common.SessionInternalError;
import com.sapienter.jbilling.server.payment.tasks.IAsyncPaymentParameters;
import com.sapienter.jbilling.server.system.event.AsynchronousEventProcessor;
import com.sapienter.jbilling.server.system.event.Event;
import com.sapienter.jbilling.server.util.Constants;
import com.sapienter.jbilling.server.util.Context;

/*
 * All this class has to do is to populate the 'message' field.
 * The actual posting on the queue is done by the parent/caller
 */
public class ProcessPaymentProcessor extends AsynchronousEventProcessor<IAsyncPaymentParameters> {

    private Integer entityId;
    
    public ProcessPaymentProcessor() {
        super();
    }

    @Override
    public void doProcess(Event event) {
        entityId = event.getEntityId();
        if (event instanceof ProcessPaymentEvent) {
            ProcessPaymentEvent pEvent;
            pEvent = (ProcessPaymentEvent) event;
            processPayment(pEvent);
        } else if (event instanceof EndProcessPaymentEvent) {
            EndProcessPaymentEvent endEvent = (EndProcessPaymentEvent) event;
            processEnd(endEvent);
        } else {
            throw new SessionInternalError("Can only process payment events");
        }
    }
    
    private void processPayment(ProcessPaymentEvent event) {
        // transform the event into map message fields
        try {
            message.setInt("invoiceId", event.getInvoiceId());
            message.setInt("processId", (event.getProcessId() == null) ? -1 : event.getProcessId());
            message.setInt("runId", (event.getRunId() == null) ? -1 : event.getRunId());
            message.setStringProperty("type", "payment");
            
            // add additional fields from the associated plug-in
            IAsyncPaymentParameters task = getPluggableTask(entityId, 
                    Constants.PLUGGABLE_TASK_ASYNC_PAYMENT_PARAMS);
            task.addParameters(message);
        } catch (Exception e) {
            throw new SessionInternalError("Error transforming message ", 
                    this.getClass(), e);
        }
    }
    
    private void processEnd(EndProcessPaymentEvent event) {
        try {
            message.setInt("runId", event.getRunId());
            message.setStringProperty("type", "ender");
        } catch (Exception e) {
            throw new SessionInternalError("Error transforming message ", 
                    this.getClass(), e);
        }
    }
    
    @Override
    protected int getEntityId() {
        return entityId.intValue();
    }
    
    @Override
    protected Destination getDestination() {
        return (Destination) Context.getBean(
                Context.Name.PROCESSORS_DESTINATION);
    }
    
}
