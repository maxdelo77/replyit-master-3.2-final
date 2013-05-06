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

import org.apache.log4j.Logger;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.server.pluggableTask.ProcessorAlarm;
import com.sapienter.jbilling.server.system.event.Event;
import com.sapienter.jbilling.server.system.event.EventProcessor;
import com.sapienter.jbilling.server.util.Constants;

public class GatewayAlarmEventProcessor extends EventProcessor<ProcessorAlarm> {
    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(GatewayAlarmEventProcessor.class));
    
    @Override
    public void process(Event event) {
        if (false == event instanceof AbstractPaymentEvent){
            return;
        }
        
        // the alarm does not care about entered payments. Filter them out
        if (event instanceof PaymentSuccessfulEvent) {
            PaymentSuccessfulEvent success = (PaymentSuccessfulEvent) event;
            if (new Integer(success.getPayment().getPaymentResult().getId()).equals(Constants.RESULT_ENTERED)) {
                return;
            }
        }
        
        AbstractPaymentEvent paymentEvent = (AbstractPaymentEvent)event;
        ProcessorAlarm alarm = getPluggableTask(event.getEntityId(), 
                Constants.PLUGGABLE_TASK_PROCESSOR_ALARM);
        
        if (alarm == null){
            // it is OK not to have an alarm configured
            LOG.info("Alarm not present for entity " + event.getEntityId());
            return;
        }
        
        String paymentProcessor = paymentEvent.getPaymentProcessor();
        if (paymentProcessor == null){
            LOG.warn("Payment event without payment processor id : " + event);
            return;
        }
        alarm.init(paymentProcessor, event.getEntityId());
        if (event instanceof PaymentFailedEvent){
            alarm.fail();
        } else if (event instanceof PaymentProcessorUnavailableEvent){
            alarm.unavailable();
        } else if (event instanceof PaymentSuccessfulEvent){
            alarm.successful();
        }
    }
    
    
}
