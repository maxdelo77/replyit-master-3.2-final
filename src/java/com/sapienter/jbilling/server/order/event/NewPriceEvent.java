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

import java.math.BigDecimal;

import com.sapienter.jbilling.server.system.event.Event;

/**
 *
 * @author Deepak Pande
 * 
 */
public class NewPriceEvent implements Event {

    private final Integer entityId;
    private final BigDecimal newPrice;
    private final BigDecimal oldPrice;
    private final BigDecimal newAmount;
    private final BigDecimal oldAmount;
    private final Integer orderLineId;
    private final Integer orderId;
    
    public NewPriceEvent(Integer entityId, BigDecimal oldPrice, BigDecimal newPrice,
                         BigDecimal oldAmount, BigDecimal newAmount, Integer orderId,
                         Integer orderLineId) {
        this.entityId = entityId;
        this.newPrice = newPrice;
        this.oldPrice = oldPrice;
        this.newAmount = newAmount;
        this.oldAmount = oldAmount;
        this.orderLineId = orderLineId;
        this.orderId = orderId;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public BigDecimal getNewPrice() {
        return newPrice;
    }

    public BigDecimal getOldPrice() {
        return oldPrice;
    }

    public BigDecimal getNewAmount() {
        return newAmount;
    }

    public BigDecimal getOldAmount() {
        return oldAmount;
    }

    public Integer getOrderLineId() {
        return orderLineId;
    }

    public Integer getOrderId() {
        return orderId;
    }
    
    public String getName() {
        return "New Price Event - entity " + entityId;
    }
    
	@Override
	public String toString() {
		return String
				.format("NewPriceEvent [entityId=%s, newPrice=%s, oldPrice=%s, oldAmount=%s, newAmount=%s, orderLineId=%s, orderId=%s]",
						entityId, newPrice, oldPrice, oldAmount, newAmount, orderLineId, orderId);
	}

}
