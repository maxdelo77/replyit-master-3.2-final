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

import org.apache.log4j.Logger;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.server.order.OrderBL;
import com.sapienter.jbilling.server.system.event.Event;

public class NewActiveUntilEvent implements Event {
    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(NewActiveUntilEvent.class)); 
    private Integer entityId;
    private Integer userId;
    private Integer orderId;
    private Date newActiveUntil; 
    private Date oldActiveUntil;
    private Integer orderType;
    private Integer statusId;
    
    public NewActiveUntilEvent(Integer orderId, 
            Date newActiveUntil, Date oldActiveUntil) {
        try {
            OrderBL order = new OrderBL(orderId);
            
            this.entityId = order.getEntity().getUser().getEntity().getId();
            this.userId = order.getEntity().getUser().getUserId();
            this.orderType = order.getEntity().getOrderPeriod().getId();
            this.statusId = order.getEntity().getStatusId();
        } catch (Exception e) {
            LOG.error("Handling order in event", e);
        } 
        this.orderId = orderId;
        this.newActiveUntil = newActiveUntil;
        this.oldActiveUntil = oldActiveUntil;
    }
    
    public Integer getEntityId() {
        return entityId;
    }

    public String getName() {
        return "New active until";
    }

    public String toString() {
        return getName();
    }
    public Date getNewActiveUntil() {
        return newActiveUntil;
    }
    public Date getOldActiveUntil() {
        return oldActiveUntil;
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

    public Integer getStatusId() {
        return statusId;
    }
}
