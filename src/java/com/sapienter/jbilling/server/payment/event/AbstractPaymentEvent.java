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

import com.sapienter.jbilling.server.payment.PaymentDTOEx;
import com.sapienter.jbilling.server.payment.db.PaymentAuthorizationDTO;
import com.sapienter.jbilling.server.system.event.Event;
import com.sapienter.jbilling.server.util.Constants;

public abstract class AbstractPaymentEvent implements Event {
    private final PaymentDTOEx payment;
    private final Integer entityId;
    
    public static AbstractPaymentEvent forPaymentResult(Integer entityId, PaymentDTOEx payment){
        Integer result = payment.getPaymentResult().getId();
        AbstractPaymentEvent event = null;
        if (Constants.RESULT_UNAVAILABLE.equals(result)){
            event = new PaymentProcessorUnavailableEvent(entityId, payment);
        } else if (Constants.RESULT_OK.equals(result)){
            event = new PaymentSuccessfulEvent(entityId, payment);
        } else if (Constants.RESULT_FAIL.equals(result)){
            event = new PaymentFailedEvent(entityId, payment);
        } else if (Constants.RESULT_NULL.equals(result)){
           // some processors don't do anything (fake), only pass to the next
           // processor in the chain
            event = null;
        }
        return event;
    }
    
    public AbstractPaymentEvent(Integer entityId, PaymentDTOEx payment) {
        this.payment = payment;
        this.entityId = entityId;
    }
    
    public final Integer getEntityId() {
        return entityId;
    }
    
    public final PaymentDTOEx getPayment() {
        return payment;
    }

    public String toString() {
        return "Event " + getName() + " payment: " + payment + " entityId: " + entityId;
    }
    
    public String getPaymentProcessor(){
        PaymentAuthorizationDTO auth = payment.getAuthorization();
        return auth == null ? null : auth.getProcessor();
    }

}
