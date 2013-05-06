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
package com.sapienter.jbilling.server.order.event;

import com.sapienter.jbilling.server.order.db.OrderDTO;
import com.sapienter.jbilling.server.system.event.Event;

/**
 * This event is triggered when an new active until is entered in an order that
 * is before (smaller) than the next invoice date. This is, the order is being
 * cancelled for a date that has been already invoiced.
 * 
 * @author emilc
 * 
 */
public class PeriodCancelledEvent implements Event {

    private final Integer entityId;
    private final Integer executorId;
    private final OrderDTO order;
    
    public PeriodCancelledEvent(OrderDTO order, Integer entityId, Integer executorId) {
        this.entityId = entityId;
        this.order = order;
        this.executorId = executorId;
    }
    public Integer getEntityId() {
        return entityId;
    }
    
    public Integer getExecutorId() {
        return executorId;
    }
    
    public OrderDTO getOrder() {
        return order;
    }

    public String getName() {
        return "Perdiod Cancelled Event - entity " + entityId;
    }
    
    public String toString() {
        return getName() + " - entity " + entityId;
    }

}
