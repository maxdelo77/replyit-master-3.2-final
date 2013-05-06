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

import com.sapienter.jbilling.common.Constants;
import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.server.item.PricingField;
import com.sapienter.jbilling.server.item.tasks.PricingResult;
import com.sapienter.jbilling.server.order.Usage;
import com.sapienter.jbilling.server.order.db.OrderDTO;
import com.sapienter.jbilling.server.pricing.db.AttributeDefinition;
import com.sapienter.jbilling.server.pricing.db.ChainPosition;
import com.sapienter.jbilling.server.pricing.db.PriceModelDTO;
import com.sapienter.jbilling.server.pricing.util.AttributeUtils;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.List;

import static com.sapienter.jbilling.server.pricing.db.AttributeDefinition.Type.DECIMAL;

/**
 * Graduated pricing strategy.
 * <p/>
 * Only usage over the included quantity will be billed.
 *
 * @author Brian Cowdery
 * @since 05-08-2010
 */
public class GraduatedPricingStrategy extends AbstractPricingStrategy {

    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(GraduatedPricingStrategy.class));

    public GraduatedPricingStrategy() {
        setAttributeDefinitions(
                new AttributeDefinition("included", DECIMAL, true)
        );

        setChainPositions(
                ChainPosition.START,
                ChainPosition.MIDDLE,
                ChainPosition.END
        );

        setRequiresUsage(true);
    }

    /**
     * Sets the price per minute to zero if the current total usage plus the quantity
     * being purchased is less than this plan's included quantity. The plan rate is set
     * only when the customer runs out of included items.
     * <p/>
     * This method applies a weighted scale to the set rate if only some of the purchased
     * usage runs over the number of included quantity.
     * <p/>
     * <code>
     * rated_qty = (purchased_qty + current usage) - included
     * percent = rated_qty / purchased_qty
     * <p/>
     * price = percent * rate
     * </code>
     *
     * @param pricingOrder target order for this pricing request (not used by this strategy)
     * @param result       pricing result to apply pricing to
     * @param fields       pricing fields (not used by this strategy)
     * @param planPrice    the plan price to apply
     * @param quantity     quantity of item being priced
     * @param usage        total item usage for this billing period
     */
    public void applyTo(OrderDTO pricingOrder, PricingResult result, List<PricingField> fields,
                        PriceModelDTO planPrice, BigDecimal quantity, Usage usage, boolean singlePurchase) {

        if (usage == null || usage.getQuantity() == null) {
            throw new IllegalArgumentException("Usage quantity cannot be null for GraduatedPricingStrategy.");
        }

        // Get the price from the result if it has been set by another chained price model.
        BigDecimal chainedPrice = null;
        if (result.getPrice() != null && result.getPrice().compareTo(BigDecimal.ZERO) > 0) {
            chainedPrice = result.getPrice();
        }

        /*
            Usage quantity normally includes the quantity being purchased because we roll in the order
            lines. If there is no pricing order (populating a single ItemDTO price), add the quantity
            being purchased to the usage calc to get the total quantity.
         */
        BigDecimal total = getTotalQuantity(pricingOrder, usage, quantity, singlePurchase);

        /*
            Calculate the past(existing) usage quantity and current usage quantity
         */
        BigDecimal existingUsageQuantity = getExistingQuantity(pricingOrder, usage, quantity, singlePurchase);
        BigDecimal currentUsageQuantity = usage.getCurrentQuantity();
        assert existingUsageQuantity.add(currentUsageQuantity).equals(total);

        BigDecimal included = getIncludedQuantity(pricingOrder, planPrice, usage);

        LOG.debug("Graduated pricing for " + included + " units included, " + total + " purchased ... from which "
                + existingUsageQuantity + " past usage units and " + currentUsageQuantity + " current usage units");

        if (existingUsageQuantity.compareTo(included) >= 0) {
            // included usage exceeded by already existing past usage
            result.setPrice(chainedPrice == null ? planPrice.getRate() : chainedPrice);
            LOG.debug("Included quantity exceeded by existing past usage, applying plan rate of " + result.getPrice());

        } else if (total.compareTo(included) > 0) {
            // current usage quantity exceeds included
            // determine the percentage rate for minutes used OVER the included.

            BigDecimal rated = total.subtract(included);
            // When this is called by the mediation the currentUsage for the first purchase of the item would be zero because
            // the current order's quantity wasn't been added as the order didn't have any lines in UsageBL.java @ line 480
            // method addWorkingOrder(). So we use the quantity by default.
            BigDecimal percent = rated.divide(currentUsageQuantity.compareTo(BigDecimal.ONE) > 0 && currentUsageQuantity.compareTo(quantity) < 0 ? currentUsageQuantity : quantity, Constants.BIGDECIMAL_SCALE, Constants.BIGDECIMAL_ROUND);
            result.setPrice(percent.multiply(chainedPrice == null ? planPrice.getRate() : chainedPrice).setScale(Constants.BIGDECIMAL_SCALE, Constants.BIGDECIMAL_ROUND));

            LOG.debug("Purchased quantity + existing usage exceeds included quantity, applying a partial rate of " + result.getPrice());

        } else {
            // purchase within included usage
            result.setPrice(BigDecimal.ZERO);

            LOG.debug("Purchase within included usage, applying a zero (0.00) rate.");
        }
    }

    public BigDecimal getIncludedQuantity(OrderDTO pricingOrder, PriceModelDTO planPrice, Usage usage) {
        return AttributeUtils.getDecimal(planPrice.getAttributes(), "included");
    }

}
