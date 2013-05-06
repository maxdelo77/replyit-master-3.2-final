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
package com.sapienter.jbilling.server.user.event;

import com.sapienter.jbilling.server.system.event.Event;

import java.math.BigDecimal;

/**
 * Event representing dynamic balance changes of a customer.
 *
 * @author Brian Cowdery
 * @since  10-14-2009
 */
public class DynamicBalanceChangeEvent implements Event {
        
    private Integer entityId;
    private Integer userId;
    private BigDecimal newBalance;
    private BigDecimal oldBalance;

    public DynamicBalanceChangeEvent(Integer entityId, Integer userId, BigDecimal newBalance, BigDecimal oldBalance) {
        this.entityId = entityId;
        this.userId = userId;
        this.newBalance = newBalance;
        this.oldBalance = oldBalance;
    }

    public String getName() {
        return "Dynamic Balance Change Event";
    }

    public Integer getUserId() {
        return userId;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public BigDecimal getNewBalance() {
        return newBalance;
    }

    public BigDecimal getOldBalance() {
        return oldBalance;
    }

    @Override
    public String toString() {
        return "DynamicBalanceChangeEvent{" +
                "entityId=" + entityId +
                ", userId=" + userId +
                ", newBalance=" + newBalance +
                ", oldBalance=" + oldBalance +
                '}';
    }
}
