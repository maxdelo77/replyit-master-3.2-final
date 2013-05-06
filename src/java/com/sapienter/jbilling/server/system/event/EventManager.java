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
package com.sapienter.jbilling.server.system.event;

import java.util.Hashtable;

import org.apache.log4j.Logger;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.common.SessionInternalError;
import com.sapienter.jbilling.server.order.event.NewActiveUntilEvent;
import com.sapienter.jbilling.server.payment.event.EndProcessPaymentEvent;
import com.sapienter.jbilling.server.payment.event.GatewayAlarmEventProcessor;
import com.sapienter.jbilling.server.payment.event.PaymentFailedEvent;
import com.sapienter.jbilling.server.payment.event.PaymentProcessorUnavailableEvent;
import com.sapienter.jbilling.server.payment.event.PaymentSuccessfulEvent;
import com.sapienter.jbilling.server.payment.event.ProcessPaymentEvent;
import com.sapienter.jbilling.server.payment.event.ProcessPaymentProcessor;
import com.sapienter.jbilling.server.process.event.NoNewInvoiceEvent;
import com.sapienter.jbilling.server.user.event.SubscriptionStatusEventProcessor;


/**
 * This class provides a link between an event and a pluggable task.
 * PTs subscribe to observe events. When an event happens, the manager
 * will call the observing PTs through the event processors.
 * A PT subscribing has to do so by adding an event processor to the list. 
 * @author Emiliano Conde
 */
public final class EventManager {
    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(EventManager.class)); 

    // this represents the subscriptions of processors to events
    static Hashtable<Class, Class[]> subscriptions;
    static {
        subscriptions = new Hashtable<Class, Class[]>();
        // PaymentFailedEvent
        subscriptions.put(PaymentFailedEvent.class, 
                new Class[] { 
                    SubscriptionStatusEventProcessor.class,
                    GatewayAlarmEventProcessor.class,
                } );
        // PaymentSuccessful
        subscriptions.put(PaymentSuccessfulEvent.class,
                new Class[] { 
                    SubscriptionStatusEventProcessor.class,
                    GatewayAlarmEventProcessor.class,
                } );
        // PaymentProcessorUnavailable
        subscriptions.put(PaymentProcessorUnavailableEvent.class,
                new Class[] { 
                    SubscriptionStatusEventProcessor.class,
                    GatewayAlarmEventProcessor.class,
                } );
        // NewActiveUntil (orders)
        subscriptions.put(NewActiveUntilEvent.class,
                new Class[] { SubscriptionStatusEventProcessor.class, } );
        // No new invoice after billing process
        subscriptions.put(NoNewInvoiceEvent.class,
                new Class[] { SubscriptionStatusEventProcessor.class, } );
        // Process a payment asynchronously
        subscriptions.put(ProcessPaymentEvent.class,
                new Class[] { ProcessPaymentProcessor.class, } );
        // Mark payment processing as finished
        subscriptions.put(EndProcessPaymentEvent.class,
                new Class[] { ProcessPaymentProcessor.class, } );
    }

    public static final void process(Event event){
        LOG.debug("processing event " + event);
        
        // always call the general event processor
        new InternalEventProcessor().process(event);

        Class processors[] = (Class[]) subscriptions.get(event.getClass());
        if (processors == null) {
            LOG.info("No processors for class " + event.getClass());
            return;
        }
        for (int f = 0; f < processors.length; f++) {
            // create a new processor
            EventProcessor processor;
            try {
                processor = (EventProcessor) processors[f].newInstance();
                LOG.debug("Now processing with " + processor);
                processor.process(event);
            } catch (Exception e) {
                throw new SessionInternalError("Error processing an event " + event, 
                        EventManager.class, e);
            }                
        }
        
    }
}
