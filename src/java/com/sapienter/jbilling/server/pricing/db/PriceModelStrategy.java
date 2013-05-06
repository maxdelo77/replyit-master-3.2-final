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

package com.sapienter.jbilling.server.pricing.db;

import com.sapienter.jbilling.server.pricing.strategy.QuantityAddonPricingStrategy;
import com.sapienter.jbilling.server.pricing.strategy.CappedGraduatedPricingStrategy;
import com.sapienter.jbilling.server.pricing.strategy.FlatPricingStrategy;
import com.sapienter.jbilling.server.pricing.strategy.GraduatedPricingStrategy;
import com.sapienter.jbilling.server.pricing.strategy.ItemPercentageSelectorStrategy;
import com.sapienter.jbilling.server.pricing.strategy.ItemSelectorStrategy;
import com.sapienter.jbilling.server.pricing.strategy.MeteredPricingStrategy;
import com.sapienter.jbilling.server.pricing.strategy.PercentageStrategy;
import com.sapienter.jbilling.server.pricing.strategy.PooledPricingStrategy;
import com.sapienter.jbilling.server.pricing.strategy.PricingStrategy;
import com.sapienter.jbilling.server.pricing.strategy.RateCardPricingStrategy;
import com.sapienter.jbilling.server.pricing.strategy.TimeOfDayPercentageStrategy;
import com.sapienter.jbilling.server.pricing.strategy.TimeOfDayPricingStrategy;
import com.sapienter.jbilling.server.pricing.strategy.TieredPricingStrategy;
import com.sapienter.jbilling.server.pricing.strategy.VolumePricingStrategy;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Simple mapping enum for PricingStrategy implementations. This class is used
 * to produce PricingStrategy instances for modeled prices.
 *
 * Enum type strings are also mapped in the {@link com.sapienter.jbilling.server.pricing.PriceModelWS}
 * class for convenience when using the Web Services API.
 *
 * @author Brian Cowdery
 * @since 05-08-2010
 */
public enum PriceModelStrategy {

    /** Flat pricing strategy, always sets price to ZERO */
    FLAT                        (new FlatPricingStrategy()),

    /** Metered pricing strategy, sets a configurable $/unit rate */
    METERED                     (new MeteredPricingStrategy()),

    /** Graduated pricing strategy, allows a set number of included units before enforcing a $/unit rate */
    GRADUATED                   (new GraduatedPricingStrategy()),

    /** Graduated pricing strategy with a maximum total usage $ cap */
    CAPPED_GRADUATED            (new CappedGraduatedPricingStrategy()),

    /** Pricing strategy that uses the current time (or time of a mediated event) to determine the price */
    TIME_OF_DAY                 (new TimeOfDayPricingStrategy()),

    /** MIDDLE or END of chain pricing strategy that applies a percentage to a previously calculated rate */
    PERCENTAGE                  (new PercentageStrategy()),

    /** MIDDLE or END of chain, time-of-day strategy that applies a percentage to a previously calculated rate */
    TIME_OF_DAY_PERCENTAGE      (new TimeOfDayPercentageStrategy()),
    
    TIERED                      (new TieredPricingStrategy()),

    /** Pricing based on the quantity purchased. */
    VOLUME_PRICING              (new VolumePricingStrategy()),

    /** Graduated pricing strategy that counts a users subscription to an item as the "pooled" included quantity */
    POOLED                      (new PooledPricingStrategy()),

    /** Strategy that adds another item to the order based on the level of usage within a specific item type */
    ITEM_SELECTOR               (new ItemSelectorStrategy()),

    /** Strategy that adds another item to the order based on the percentage used of one item type over another */
    ITEM_PERCENTAGE_SELECTOR    (new ItemPercentageSelectorStrategy()),

    /** START of chain pricing strategy that increases the "included" quantity of a Graduated price using other purchased add-on items */
    QUANTITY_ADDON              (new QuantityAddonPricingStrategy()),

    /** Pricing strategy that queries the price from a cached rating table using the value from a provided pricing field. */
    RATE_CARD                   (new RateCardPricingStrategy());


    private final PricingStrategy strategy;
    PriceModelStrategy(PricingStrategy strategy) { this.strategy = strategy; }
    public PricingStrategy getStrategy() { return strategy; }

    public static Set<PriceModelStrategy> getStrategyByChainPosition(ChainPosition ...chainPositions) {
        Set<PriceModelStrategy> strategies = new LinkedHashSet<PriceModelStrategy>();
        for (PriceModelStrategy strategy : values()) {
            for (ChainPosition position : chainPositions) {
                if (strategy.getStrategy().getChainPositions().contains(position)) {
                    strategies.add(strategy);
                }
            }
        }
        return strategies;
    }
}
