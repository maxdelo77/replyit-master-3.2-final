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

package com.sapienter.jbilling.server.item.tasks;

import java.math.BigDecimal;
import java.util.Date;

import com.sapienter.jbilling.server.order.db.OrderLineDTO;


@Deprecated /*  Replaced by SubscriptionResult to be used with version 2 of the rules tasks. */
public class Subscription {

    private final Integer itemId;
    private final Integer periodId;
    private final Date activeSince;
    private final Date activeUntil;
    private final BigDecimal quantity;

    public Subscription(OrderLineDTO line) {
        periodId = line.getPurchaseOrder().getOrderPeriod().getId();
        activeSince = line.getPurchaseOrder().getActiveSince();
        activeUntil = line.getPurchaseOrder().getActiveUntil();
        itemId = line.getItemId();
        quantity = line.getQuantity();
    }

    public Date getActiveSince() {
        return activeSince;
    }

    public Date getActiveUntil() {
        return activeUntil;
    }

    public Integer getItemId() {
        return itemId;
    }

    public Integer getPeriodId() {
        return periodId;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }
}
