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
import com.sapienter.jbilling.server.pricing.db.PriceModelDTO;
import junit.framework.TestCase;
import org.joda.time.LocalTime;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * TimeOfDayPricingStrategyTest
 *
 * @author Brian Cowdery
 * @since 03/02/11
 */
public class TimeOfDayPricingStrategyTest extends TestCase {

    // class under test
    private PricingStrategy strategy = new TimeOfDayPricingStrategy();

    public TimeOfDayPricingStrategyTest() {
        super();
    }

    public TimeOfDayPricingStrategyTest(String name) {
        super(name);
    }

    /** Convenience method to produce a time for the given hours and minutes */
    private Date getTime(int hours, int minutes) {
        return new LocalTime(hours, minutes).toDateTimeToday().toDate();
    }

    /**
     * Test that the current time is used if no pricing fields are given
     */
    public void testCurrentTime() {
        PriceModelDTO model = new PriceModelDTO();
        model.addAttribute("00:00", "10.00");
        model.addAttribute("23:59", "99.99");

        // can't really test this without duplicating a bunch of time calculations
        // simply check to make sure that no exception occurs if the time isn't part of the pricing fields

        PricingResult result = new PricingResult(1, 2, 3);
        strategy.applyTo(null, result, null, model, null, null, false);
    }

    /**
     * Test that pricing field dates can be used for pricing
     */
    public void testPricingFieldTime() {
        PriceModelDTO model = new PriceModelDTO();
        model.addAttribute("date_field", "event_date");
        model.addAttribute("00:00", "10.00");
        model.addAttribute("12:00", "20.00");

        // test price at 1:00 (first time range)
        List<PricingField> fields = Arrays.asList(new PricingField("event_date", getTime(1, 0)));
        PricingResult result = new PricingResult(1, 2, 3);
        strategy.applyTo(null, result, fields, model, null, null, false);

        assertEquals(new BigDecimal("10.00"), result.getPrice());

        // test price at 12:00 (second time range)
        List<PricingField> fields2 = Arrays.asList(new PricingField("event_date", getTime(13, 0)));
        PricingResult result2 = new PricingResult(1, 2, 3);
        strategy.applyTo(null, result2, fields2, model, null, null, false);

        assertEquals(new BigDecimal("20.00"), result2.getPrice());
    }

    /**
     * Test upper and lower boundaries of time ranges
     */
    public void testPricing() {
        PriceModelDTO model = new PriceModelDTO();
        model.addAttribute("date_field", "event_date");
        model.addAttribute("00:00", "10.00");
        model.addAttribute("12:00", "20.00");
        model.addAttribute("18:00", "25.00");

        PricingResult result = new PricingResult(1, 2, 3);

        // test price at exactly 00:00 (equal to boundary)
        List<PricingField> fields = Arrays.asList(new PricingField("event_date", getTime(0, 0)));
        strategy.applyTo(null, result, fields, model, null, null, false);

        assertEquals(new BigDecimal("10.00"), result.getPrice());

        // greater than lower boundary
        fields = Arrays.asList(new PricingField("event_date", getTime(0, 1)));
        strategy.applyTo(null, result, fields, model, null, null, false);

        assertEquals(new BigDecimal("10.00"), result.getPrice());

        // 1 second before next time range (12:00)
        fields = Arrays.asList(new PricingField("event_date", getTime(11, 59)));
        strategy.applyTo(null, result, fields, model, null, null, false);

        assertEquals(new BigDecimal("10.00"), result.getPrice());

        // equal to lower boundary of 12:00 range
        fields = Arrays.asList(new PricingField("event_date", getTime(12, 0)));
        strategy.applyTo(null, result, fields, model, null, null, false);

        assertEquals(new BigDecimal("20.00"), result.getPrice());

        // greater than lower boundary of 12:00 range
        fields = Arrays.asList(new PricingField("event_date", getTime(12, 1)));
        strategy.applyTo(null, result, fields, model, null, null, false);

        assertEquals(new BigDecimal("20.00"), result.getPrice());
    }

}
