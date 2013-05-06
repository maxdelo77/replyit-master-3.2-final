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
package com.sapienter.jbilling.server.process.event;

import java.util.Date;

import com.sapienter.jbilling.server.system.event.Event;

public class NoNewInvoiceEvent implements Event {
    private final Integer entityId;
    private final Integer userId;
    private final Date billingProcess;
    private final Integer subscriberStauts; // helps determine if the event has to be processed
    
    public NoNewInvoiceEvent(Integer entityId, Integer userId, 
            Date billingProcess, Integer subscriberStatus) {
        this.entityId = entityId;
        this.userId = userId;
        this.billingProcess = billingProcess;
        this.subscriberStauts = subscriberStatus;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public String getName() {
        return "Billing process produced no new invoices for this user";
    }

    public final Date getBillingProcess() {
        return billingProcess;
    }

    public final Integer getSubscriberStatus() {
        return subscriberStauts;
    }

    public final Integer getUserId() {
        return userId;
    }

}
