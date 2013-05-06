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

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.server.rule.Result;
import java.math.BigDecimal;
import org.apache.log4j.Logger;

/**
 *
 * @author emilc
 */
public class PricingResult extends Result {

    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(PricingResult.class));

    private final Integer itemId;
    private final Integer userId;
    private final Integer currencyId;
    private BigDecimal price;
    private BigDecimal quantity;
    private long pricingFieldsResultId;

    public PricingResult(Integer itemId, Integer userId, Integer currencyId) {
        this.itemId = itemId;
        this.userId = userId;
        this.currencyId = currencyId;
    }

    public PricingResult(Integer itemId, BigDecimal quantity, Integer userId, Integer currencyId) {
        this.itemId = itemId;
        this.quantity = quantity;
        this.userId = userId;
        this.currencyId = currencyId;
    }    

    public Integer getCurrencyId() {
        return currencyId;
    }

    public Integer getItemId() {
        return itemId;
    }

    public Integer getUserId() {
        return userId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        LOG.debug("Setting price. Result fields id %s item %s price %s", pricingFieldsResultId, itemId, price);
        this.price = price;
    }

    public void setPrice(String price) {
        setPrice(new BigDecimal(price));
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public long getPricingFieldsResultId() {
        return pricingFieldsResultId;
    }

    public void setPricingFieldsResultId(long pricingFieldsResultId) {
        this.pricingFieldsResultId = pricingFieldsResultId;
    }

    public String toString() {
        return  "PricingResult:" +
                "itemId=" + itemId + " " +
                "userId=" + userId + " " +
                "currencyId=" + currencyId + " " +
                "price=" + price + " " +
                "pricing fields result id=" + pricingFieldsResultId + " " +
                super.toString();
    }

}
