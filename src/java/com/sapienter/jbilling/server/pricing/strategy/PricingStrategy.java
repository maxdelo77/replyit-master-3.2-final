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
import com.sapienter.jbilling.server.pricing.db.AttributeDefinition;
import com.sapienter.jbilling.server.pricing.db.ChainPosition;
import com.sapienter.jbilling.server.pricing.db.PriceModelDTO;

import java.math.BigDecimal;
import java.util.List;

/**
 * Interface for pricing strategies. All implemented strategies must be accessible via
 * the {@link com.sapienter.jbilling.server.pricing.db.PriceModelStrategy} mapping enum.
 *
 * @author Brian Cowdery
 * @since 05-08-2010
 */
public interface PricingStrategy {

    /**
     * Returns a list of attribute definitions for this pricing strategy. This method should
     * return an empty list if there are no attributes.
     *
     * @return attribute definitions. empty list if none.
     */
    public List<AttributeDefinition> getAttributeDefinitions();

    /**
     * Returns a list of positions that a price model using strategy is allowed to occupy when chained.
     *
     * @return list of allowed chain positions
     */
    public List<ChainPosition> getChainPositions();

    /**
     * Returns true if this strategy requires the current usage of the item
     * to properly calculate the the price.
     *
     * @return true if this strategy requires the current usage of the item being priced.
     */
    public boolean requiresUsage();

    /**
     * Applies the plan's pricing strategy to the given pricing request.
     *
     * The usage totals will already include quantity being priced if pricing an order
     * during a create/update or order rating call.
     *
     * @param pricingOrder target order for this pricing request (may be null)
     * @param result pricing result to apply pricing to
     * @param fields pricing fields
     * @param planPrice the plan price to apply
     * @param quantity quantity of item being priced
     * @param usage total item usage for this billing period
     * @param singlePurchase true if pricing a single purchase/addition to an order, false if pricing a quantity that already exists on the pricingOrder
     * @throws IllegalArgumentException if strategy requires usage, and usage was given as null
     */
    public void applyTo(OrderDTO pricingOrder, PricingResult result, List<PricingField> fields,
                        PriceModelDTO planPrice, BigDecimal quantity, Usage usage, boolean singlePurchase);
}
