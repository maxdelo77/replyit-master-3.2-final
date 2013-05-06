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
 *
 * @author emilc
 */
public class NewOrderEvent implements Event {
    private Integer entityId;
    private OrderDTO order;

    public NewOrderEvent(Integer entityId, OrderDTO order) {
        this.entityId = entityId;
        this.order = order;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public String getName() {
        return "Order deleted event";
    }

    public OrderDTO getOrder() {
        return order;
    }

    public String toString() {
        return "NewOrderEvent: entityId = " + entityId + " order = " + order;
    }

}
