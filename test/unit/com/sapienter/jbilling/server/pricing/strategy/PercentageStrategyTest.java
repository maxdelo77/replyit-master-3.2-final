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

import com.sapienter.jbilling.server.BigDecimalTestCase;
import com.sapienter.jbilling.server.item.tasks.PricingResult;
import com.sapienter.jbilling.server.pricing.db.PriceModelDTO;
import com.sapienter.jbilling.server.pricing.db.PriceModelStrategy;

import java.math.BigDecimal;

/**
 * PercentageStrategyTest
 *
 * @author Brian Cowdery
 * @since 07/02/11
 */
public class PercentageStrategyTest extends BigDecimalTestCase {

    public PercentageStrategyTest() {
    }

    public PercentageStrategyTest(String name) {
        super(name);
    }

    /**
     * Tests that a percentage can be applied to a previously determined price.
     */
    public void testPositivePercentage() {
        PriceModelDTO planPrice = new PriceModelDTO();
        planPrice.setType(PriceModelStrategy.PERCENTAGE);
        planPrice.addAttribute("percentage", "0.80"); // %80

        PricingResult result = new PricingResult(1, 2, 3);
        result.setPrice(new BigDecimal("10.00"));

        // test that the price has been reduced by %80
        // $10 * 0.80 = $8
        planPrice.applyTo(null, null, result, null, null, false, null);
        assertEquals(new BigDecimal("18.00"), result.getPrice());
    }

    /**
     * Tests that a percentage can be applied to a previously determined price.
     */
    public void testNegativePercentage() {
        PriceModelDTO planPrice = new PriceModelDTO();
        planPrice.setType(PriceModelStrategy.PERCENTAGE);
        planPrice.addAttribute("percentage", "-0.80"); // %80

        PricingResult result = new PricingResult(1, 2, 3);
        result.setPrice(new BigDecimal("10.00"));

        // test that the price has been reduced by %80
        // $10 * 0.80 = $8
        planPrice.applyTo(null, null, result, null, null, false, null);
        assertEquals(new BigDecimal("2.00"), result.getPrice());
    }

    /**
     * Tests that the price must be set to apply a percentage. A null price should not
     * cause an exception or other unwanted behaviour.
     */
    public void testNullPrice() {
        PriceModelDTO planPrice = new PriceModelDTO();
        planPrice.setType(PriceModelStrategy.PERCENTAGE);
        planPrice.addAttribute("percentage", "0.80"); // %80

        // result without price
        PricingResult result = new PricingResult(1, 2, 3);

        // applying without a price shouldn't cause an exception
        // price should still be null
        planPrice.applyTo(null, null, result, null,  null, false, null);
        assertNull(result.getPrice());
    }
}
