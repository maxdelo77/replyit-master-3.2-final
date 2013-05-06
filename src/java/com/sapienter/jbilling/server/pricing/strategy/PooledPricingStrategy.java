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
import com.sapienter.jbilling.server.order.Usage;
import com.sapienter.jbilling.server.order.UsageBL;
import com.sapienter.jbilling.server.order.db.OrderDTO;
import com.sapienter.jbilling.server.pricing.db.AttributeDefinition;
import com.sapienter.jbilling.server.pricing.db.ChainPosition;
import com.sapienter.jbilling.server.pricing.db.PriceModelDTO;
import com.sapienter.jbilling.server.pricing.util.AttributeUtils;
import com.sapienter.jbilling.server.pricing.util.ItemPoolUtils;
import org.apache.log4j.Logger;

import java.math.BigDecimal;

import static com.sapienter.jbilling.server.pricing.db.AttributeDefinition.Type.DECIMAL;
import static com.sapienter.jbilling.server.pricing.db.AttributeDefinition.Type.INTEGER;

/**
 * PooledPricingStrategy
 *
 * Calculates a price based off of the number of subscribed "pool items". The size of the
 * pool dictates the calculated price (incremental volume pricing).
 *
 * @author Brian Cowdery
 * @since 23/03/11
 */
public class PooledPricingStrategy extends GraduatedPricingStrategy {

    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(PooledPricingStrategy.class));

    public PooledPricingStrategy() {
        setAttributeDefinitions(
                new AttributeDefinition("pool_item_id", INTEGER, true),
                new AttributeDefinition("multiplier", DECIMAL, true)
        );

        setChainPositions(
                ChainPosition.START
        );

        setRequiresUsage(true);
    }

    /**
     * Calculates the included quantity based on the number of pool items purchased on the
     * users main subscription order. A multiplier is applied to to the pooled quantity to
     * obtain the total included quantity.
     *
     *      Included Quantity = Number of Pool Items * Multiplier
     *
     * @param pricingOrder target order for this pricing request
     * @param planPrice the plan price to apply
     * @return included quantity
     */
    @Override
    public BigDecimal getIncludedQuantity(OrderDTO pricingOrder, PriceModelDTO planPrice, Usage usage) {
        if (usage != null && usage.getUserId() != null) {
            Integer poolItemId = AttributeUtils.getInteger(planPrice.getAttributes(), "pool_item_id");
            BigDecimal multiplier = AttributeUtils.getDecimal(planPrice.getAttributes(), "multiplier");

            LOG.debug("Calculating pool size for user " + usage.getUserId() + " and pool item " + poolItemId);
            return ItemPoolUtils.getPoolSize(usage.getUserId(), poolItemId, multiplier);
        }

        LOG.debug("Cannot calculate pool size without current usage, setting pool size to zero.");
        return BigDecimal.ZERO;
    }
}
