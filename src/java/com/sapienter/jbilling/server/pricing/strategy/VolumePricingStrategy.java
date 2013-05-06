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

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.server.item.PricingField;
import com.sapienter.jbilling.server.item.tasks.PricingResult;
import com.sapienter.jbilling.server.order.Usage;
import com.sapienter.jbilling.server.order.db.OrderDTO;
import com.sapienter.jbilling.server.pricing.db.AttributeDefinition;
import com.sapienter.jbilling.server.pricing.db.ChainPosition;
import com.sapienter.jbilling.server.pricing.db.PriceModelDTO;
import com.sapienter.jbilling.server.pricing.util.AttributeUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import static com.sapienter.jbilling.server.pricing.db.AttributeDefinition.Type.*;

/**
 * Volume pricing strategy.
 *
 * @author Brian Cowdery
 * @since 14/07/11
 */
public class VolumePricingStrategy extends AbstractPricingStrategy {

    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(VolumePricingStrategy.class));

    public VolumePricingStrategy() {
        setAttributeDefinitions(
                new AttributeDefinition("0", DECIMAL, true)
        );

        setChainPositions(
                ChainPosition.START
        );

        setRequiresUsage(true);
    }

    /**
     * Calculates a price based on the total volume being purchased. The price per unit is selected
     * from the defined pricing tiers based on the total quantity purchased.
     *
     * Example:
     *  0 - 500    @ $2
     *  500 - 1000 @ $1
     *  > 1000     @ $0.5
     *
     *  If 0 - 500 units are purchased, the price would be $2/unit
     *  If 500 - 1000 units are purchased, the price would be $1/unit
     *  If greater than 1000 units are purchased the price would be $0.05/unit
     *
     * @param pricingOrder target order for this pricing request (may be null)
     * @param result pricing result to apply pricing to
     * @param fields pricing fields
     * @param planPrice the plan price to apply
     * @param quantity quantity of item being priced
     * @param usage total item usage for this billing period
     */
    public void applyTo(OrderDTO pricingOrder, PricingResult result, List<PricingField> fields,
                        PriceModelDTO planPrice, BigDecimal quantity, Usage usage, boolean singlePurchase) {

        if (usage == null || usage.getQuantity() == null)
            throw new IllegalArgumentException("Usage quantity cannot be null for VolumePricingStrategy.");

        // parse pricing tiers
        SortedMap<BigDecimal, BigDecimal> tiers = getTiers(planPrice.getAttributes());
        LOG.debug("Volume pricing: " + tiers);
        LOG.debug("Selecting volume price for usage level " + usage.getQuantity());

        // find matching tier
        // the usage quantity already includes the quantity being purchased as it rolls in the
        // lines from the order being worked on. In this case we only care about the TOTAL quantity, not
        // the individual amount being purchased for this one order.
        BigDecimal price = tiers.get(BigDecimal.ZERO);
        for (BigDecimal tier : tiers.keySet()) {
            if (usage.getQuantity().compareTo(tier) >= 0) {
                price = tiers.get(tier);
            }
        }

        if (price != null) {
            result.setPrice(price);
        } else {
            LOG.debug("No volume price for usage level " + usage.getQuantity());
        }
    }

    /**
     * Parses the price model attributes and returns a map of tier quantities and corresponding
     * prices for each tier. The map is sorted in ascending order by quantity (smallest first).
     *
     * @param attributes attributes to parse
     * @return tiers of quantities and prices
     */
    protected SortedMap<BigDecimal, BigDecimal> getTiers(Map<String, String> attributes) {
        SortedMap<BigDecimal, BigDecimal> tiers = new TreeMap<BigDecimal, BigDecimal>();

        for (Map.Entry<String, String> entry : attributes.entrySet()) {
            if (NumberUtils.isNumber(entry.getKey())) {
                tiers.put(AttributeUtils.parseDecimal(entry.getKey()), AttributeUtils.parseDecimal(entry.getValue()));
            }
        }

        return tiers;
    }

}
