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

package com.sapienter.jbilling.server.pricing.strategy;

import com.sapienter.jbilling.server.item.PricingField;
import com.sapienter.jbilling.server.item.tasks.PricingResult;
import com.sapienter.jbilling.server.order.Usage;
import com.sapienter.jbilling.server.order.db.OrderDTO;
import com.sapienter.jbilling.server.pricing.db.ChainPosition;
import com.sapienter.jbilling.server.pricing.db.PriceModelDTO;

import java.math.BigDecimal;
import java.util.List;

/**
 * Flat pricing strategy.
 *
 * Prices the item at $0/unit. Flat pricing assumes that the user has already paid for all
 * usage as part of the cost of the plan.
 *
 * @author Brian Cowdery
 * @since 05-08-2010
 */
public class FlatPricingStrategy extends AbstractPricingStrategy {

    public FlatPricingStrategy() {
        setChainPositions(
                ChainPosition.START
        );
    }


    /**
     * Sets the price to zero. The price for all usage is included in the cost of the
     * monthly plan subscription, so the customer is not charged per unit.
     *
     * @param pricingOrder target order for this pricing request (not used by this strategy)
     * @param result pricing result to apply pricing to
     * @param fields pricing fields (not used by this strategy)
     * @param planPrice the plan price to apply (not used by this strategy)
     * @param quantity quantity of item being priced (not used by this strategy)
     * @param usage total item usage for this billing period
     */
    public void applyTo(OrderDTO pricingOrder, PricingResult result, List<PricingField> fields,
                        PriceModelDTO planPrice, BigDecimal quantity, Usage usage, boolean singlePurchase) {

        result.setPrice(BigDecimal.ZERO);
    }
}
