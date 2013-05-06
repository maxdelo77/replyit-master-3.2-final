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

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.log4j.Logger;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.server.util.Context;

public abstract class AsynchronousEventProcessor<TType> extends EventProcessor<TType> {
    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(AsynchronousEventProcessor.class)); 

    protected MapMessage message;
    
    protected AsynchronousEventProcessor() {
    }
    
    public void process(final Event event) {
        JmsTemplate jmsTemplate = (JmsTemplate) Context.getBean(
                Context.Name.JMS_TEMPLATE);

        jmsTemplate.send(getDestination(), new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
                message = session.createMapMessage();
                doProcess(event);
                message.setIntProperty("entityId", getEntityId());
                return message;
            }
        });
    }

    protected abstract void doProcess(Event event);
    protected abstract Destination getDestination();
    protected abstract int getEntityId();
}
