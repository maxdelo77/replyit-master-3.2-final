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
import org.apache.log4j.Logger;
import org.joda.time.LocalTime;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import static com.sapienter.jbilling.server.pricing.db.AttributeDefinition.Type.*;


/**
 * TimeOfDayPricingStrategy
 *
 * @author Brian Cowdery
 * @since 03/02/11
 */
public class TimeOfDayPricingStrategy extends AbstractPricingStrategy {

    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(TimeOfDayPricingStrategy.class));

    public TimeOfDayPricingStrategy() {
        setAttributeDefinitions(
                new AttributeDefinition("date_field", STRING, false),   // pricing field name holding the date
                new AttributeDefinition("00:00", DECIMAL, true)         // price at start of day
        );

        setChainPositions(
                ChainPosition.START
        );
    }

    /**
     * Sets the price depending on the current time. The time can be pulled from a pricing field
     * when applied through mediation, or if not found (not running mediation), the current time.
     *
     * This strategy uses attributes to define the time slices.
     *
     * For example, the attributes:
     * <code>
     *      "00:00" = 10.00
     *      "12:00" = 20.00
     * </code>
     *
     * Are handled as:
     * <code>
     *      Between 00:00 and 12:00 = $10.00
     *      After 12:00 = $12.00
     * </code>
     *
     * @param pricingOrder target order for this pricing request (not used by this strategy)
     * @param result pricing result to apply pricing to
     * @param fields pricing fields
     * @param planPrice the plan price to apply
     * @param quantity quantity of item being priced
     * @param usage total item usage for this billing period
     */
    public void applyTo(OrderDTO pricingOrder, PricingResult result, List<PricingField> fields,
                        PriceModelDTO planPrice, BigDecimal quantity, Usage usage, boolean singlePurchase) {

        // parse time ranges and prices
        SortedMap<LocalTime, BigDecimal> prices = getPrices(planPrice.getAttributes());
        LOG.debug("Time-of-day pricing: " + prices);

        // get the current time from the pricing fields
        String fieldName = planPrice.getAttributes().get("date_field");
        LocalTime now = getTime(fieldName, fields);

        // find the price
        for (LocalTime time : prices.keySet()) {
            if (now.isEqual(time) || now.isAfter(time)) {
                result.setPrice(prices.get(time));
            }
        }
        LOG.debug("Price for " + now + ": " + result.getPrice());
    }

    /**
     * Parses the price model attributes and returns a map of times and corresponding
     * prices. The map is sorted in ascending order by time (earliest times first).
     *
     * @param attributes attributes to parse
     * @return times and prices
     */
    protected SortedMap<LocalTime, BigDecimal> getPrices(Map<String, String> attributes) {
        SortedMap<LocalTime, BigDecimal> prices = new TreeMap<LocalTime, BigDecimal>();

        for (Map.Entry<String, String> entry : attributes.entrySet()) {
            if (entry.getKey().contains(":")) {
                prices.put(AttributeUtils.parseTime(entry.getKey()), AttributeUtils.parseDecimal(entry.getValue()));
            }
        }

        return prices;
    }

    /**
     * Searches through the list of pricing fields and extracts the time for this price
     * calculation. If the field is not found, the current system time will be returned.
     *
     * @param fieldName pricing field name of the pricing date
     * @param fields pricing fields
     * @return pricing time
     */
    protected LocalTime getTime(String fieldName, List<PricingField> fields) {
        LocalTime now = new LocalTime();
        if (fields != null && fieldName != null) {
            for (PricingField field : fields) {
                if (field.getName().equals(fieldName)) {
                    now = LocalTime.fromDateFields(field.getDateValue());
                    break;
                }
            }
        }

        return now;
    }

}


