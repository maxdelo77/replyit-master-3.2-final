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
import com.sapienter.jbilling.server.pricing.db.ChainPosition;
import com.sapienter.jbilling.server.pricing.db.PriceModelDTO;
import org.apache.log4j.Logger;
import org.joda.time.LocalTime;

import java.math.BigDecimal;
import java.util.List;
import java.util.SortedMap;

/**
 * TimeOfDayPercentagePricingStrategy
 *
 * @author Brian Cowdery
 * @since 07/02/11
 */
public class TimeOfDayPercentageStrategy extends TimeOfDayPricingStrategy {

    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(TimeOfDayPricingStrategy.class));

    public TimeOfDayPercentageStrategy() {
        super();

        setChainPositions(
                ChainPosition.MIDDLE,
                ChainPosition.END
        );
    }

    /**
     * This pricing strategy works exactly the same as the TimeOfDayPricingStrategy, only a
     * percentage is applied instead of a flat rate. This is a MIDDLE or END of chain strategy,
     * meaning that it expects the result to already have a price.
     *
     * The time-of-day rate is handled as a decimal percentage, a rate of "0.80" would
     * be applied as 80%, "1.25" as %125 and so on.
     *
     * @see TimeOfDayPricingStrategy
     *
     * @param pricingOrder target order for this pricing request (not used by this strategy)
     * @param result pricing result to apply pricing to
     * @param fields pricing fields
     * @param planPrice the plan price to apply
     * @param quantity quantity of item being priced
     * @param usage total item usage for this billing period
     */
    @Override
    public void applyTo(OrderDTO pricingOrder, PricingResult result, List<PricingField> fields,
                        PriceModelDTO planPrice, BigDecimal quantity, Usage usage, boolean singlePurchase) {

        if (result.getPrice() != null) {
            // parse time ranges and prices
            SortedMap<LocalTime, BigDecimal> percentages = getPrices(planPrice.getAttributes());
            LOG.debug("Time-of-day percentage: " + percentages);

            // get the current time from the pricing fields
            String fieldName = planPrice.getAttributes().get("date_field");
            LocalTime now = getTime(fieldName, fields);

            // find the price
            BigDecimal percentage = null;
            for (LocalTime time : percentages.keySet()) {
                if (now.isEqual(time) || now.isAfter(time)) {
                    percentage = percentages.get(time);
                }
            }

            if (percentage != null) {
                result.setPrice(result.getPrice().multiply(percentage).setScale(Constants.BIGDECIMAL_SCALE, Constants.BIGDECIMAL_ROUND));
                LOG.debug("Price for " + now + ": " + result.getPrice());
            }
        }
    }
}
