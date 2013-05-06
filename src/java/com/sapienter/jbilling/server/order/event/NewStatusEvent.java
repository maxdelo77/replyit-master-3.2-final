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

import org.apache.log4j.Logger;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.server.order.OrderBL;
import com.sapienter.jbilling.server.system.event.Event;

public class NewStatusEvent implements Event {

    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(NewStatusEvent.class)); 
    private Integer entityId;
    private Integer userId;
    private Integer orderId;
    private Integer orderType;
    private Integer oldStatusId;
    private Integer newStatusId;
    
    public NewStatusEvent(Integer orderId, Integer oldStatusId, Integer newStatusId) {
        try {
            OrderBL order = new OrderBL(orderId);
            
            this.entityId = order.getEntity().getUser().getEntity().getId();
            this.userId = order.getEntity().getUser().getUserId();
            this.orderType = order.getEntity().getOrderPeriod().getId();
            this.oldStatusId = oldStatusId;
            this.newStatusId = newStatusId;
        } catch (Exception e) {
            LOG.error("Handling order in event", e);
        } 
        this.orderId = orderId;
    }
    
    public Integer getEntityId() {
        return entityId;
    }

    public String getName() {
        return "New status";
    }

    public String toString() {
        return getName();
    }
    public Integer getOrderId() {
        return orderId;
    }
    public Integer getUserId() {
        return userId;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public Integer getNewStatusId() {
        return newStatusId;
    }

    public Integer getOldStatusId() {
        return oldStatusId;
    }

    
}
