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

import com.sapienter.jbilling.client.util.Constants;
import com.sapienter.jbilling.common.FormatLogger;

import java.math.BigDecimal;
import org.apache.log4j.Logger;

/**
 *
 * @author emilc
 */
public class PricingManager {

    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(PricingManager.class));
    private final Integer itemId;
    private final Integer userId;
    private final Integer currencyId;
    private BigDecimal price; // it is all about setting the value of this field ...

    public PricingManager(Integer itemId, Integer userId,
            Integer currencyId, BigDecimal price) {
        this.itemId = itemId;
        this.userId = userId;
        this.currencyId = currencyId;
        setPrice(price);
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(double defaultPrice) {
        LOG.debug("Setting price of item %s to %s", itemId, defaultPrice);
        this.price = new BigDecimal(defaultPrice);
    }

    public void setPrice(BigDecimal defaultPrice) {
        this.price = defaultPrice;
    }

    public void setPrice(int price) {
        setPrice((double) price);
    }

    public void setByPercentage(double percentage) {
        this.price = price.add(price.divide(new BigDecimal(100), Constants.BIGDECIMAL_SCALE,
                Constants.BIGDECIMAL_ROUND).multiply(new BigDecimal(percentage)));
    }

    public void setByPercentage(int percentage) {
        setByPercentage((double) percentage);
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

    public String toString() {
        return "PricingManages=currencyId: " + currencyId + " itemId: " + itemId +
                " price " + price + " userId " + userId;
    }
}
