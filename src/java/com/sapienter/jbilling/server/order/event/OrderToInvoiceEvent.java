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

import java.util.Date;

import com.sapienter.jbilling.server.order.db.OrderDTO;
import com.sapienter.jbilling.server.system.event.Event;

/**
 * This event is triggered BEFORE an order is added to an invoice.
 * Allows for the order to be modified before going on an invoice.
 *
 * @author Mary Krylenko
 *
 */
public class OrderToInvoiceEvent implements Event {

    private Integer entityId;
    private Integer userId;
    private Integer orderId;
    private OrderDTO order;
    private Date start;
    private Date end;

    public OrderToInvoiceEvent(Integer entityId, Integer userId, OrderDTO order) {
        this.entityId = entityId;
        this.userId = userId;
        this.orderId = order.getId();
        this.order = order;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public String getName() {
        return "Order to Invoice for Entity " + entityId;
    }

    public Integer getUserId() {
        return userId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    /**
     * Warning, the order returned is in the hibernate session.
     * Any changes will be reflected in the database.
     */
    public OrderDTO getOrder() {
        return order;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public String toString() {
        return "OrderToInvoice event=userId " + userId + " orderId " + orderId + " order " + order + " start " + start + " end " + end;
    }
}
