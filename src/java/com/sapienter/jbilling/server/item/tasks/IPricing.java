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

import java.util.List;

import com.sapienter.jbilling.server.item.PricingField;
import com.sapienter.jbilling.server.order.db.OrderDTO;
import com.sapienter.jbilling.server.pluggableTask.TaskException;
import java.math.BigDecimal;

public interface IPricing {

    /**
     * Get the price for the given item, user, and quantity being purchased. Pricing fields can be
     * provided to define specific pricing scenarios to be handled by the implementing class.
     *
     * @param itemId item id being purchased
     * @param quantity quantity being purchased 
     * @param userId user purchasing the item
     * @param currencyId currency of user
     * @param fields pricing fields
     * @param defaultPrice default price if no other price could be determined.
     * @param pricingOrder target order for this pricing request (may be null)
     * @param singlePurchase true if pricing a single purchase/addition to an order, false if pricing a quantity that already exists on the given order.
     * @return price
     * @throws TaskException checked exception if a problem occurs.
     */
    public BigDecimal getPrice(Integer itemId, BigDecimal quantity, Integer userId, Integer currencyId,
                               List<PricingField> fields, BigDecimal defaultPrice, OrderDTO pricingOrder, boolean singlePurchase)
            throws TaskException;
}
