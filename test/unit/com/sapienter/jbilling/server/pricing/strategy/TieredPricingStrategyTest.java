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
import com.sapienter.jbilling.server.order.Usage;
import com.sapienter.jbilling.server.pricing.db.PriceModelDTO;
import com.sapienter.jbilling.server.pricing.db.PriceModelStrategy;

import java.math.BigDecimal;

/**
 * @author Vikas Bodani
 * @since 15-03-2011
 */
public class TieredPricingStrategyTest extends BigDecimalTestCase {

    // class under test
    private PricingStrategy strategy = new TieredPricingStrategy();

    public TieredPricingStrategyTest() {
    }

    public TieredPricingStrategyTest(String name) {
        super(name);
    }

    /**
     * Convenience test method to build a usage object for the given quantity.
     * @param quantity quantity
     * @return usage object
     */
    private Usage getUsage(BigDecimal quantity) {
        Usage usage = new Usage();
        usage.setQuantity(quantity);

        return usage;
    }

    public void testApplyToNoAttributes() throws Exception {
        PriceModelDTO planPrice = new PriceModelDTO();
        planPrice.setType(PriceModelStrategy.TIERED);

        PricingResult result = new PricingResult(1, 2, 3);

        // 10 purchased, 1000 usage... but no tiers defined, price should be zero
        strategy.applyTo(null, result, null, planPrice, new BigDecimal(10), getUsage(new BigDecimal(1000)), false);
        assertEquals(BigDecimal.ZERO, result.getPrice());
    }

    public void testTieredNoUsage() throws Exception {
        PriceModelDTO planPrice = new PriceModelDTO();
        planPrice.setType(PriceModelStrategy.TIERED);

        planPrice.addAttribute("0",  "4.00");   // 0 - 500     = 500 @ $4
        planPrice.addAttribute("500",  "3.00"); // 500 - 1000  = 500 @ $3
        planPrice.addAttribute("1000", "2.00"); // 1000 - 1500 = 500 @ $2
        planPrice.addAttribute("1500", "1.00"); // > 1500 @ $1

        PricingResult result = new PricingResult(1, 2, 3);

        // entire quantity in first tier
        strategy.applyTo(null, result, null, planPrice, new BigDecimal(500), getUsage(BigDecimal.ZERO), false);
        assertEquals(new BigDecimal("4"), result.getPrice());

        // quantity falls into middle tier
        // 500 * $4 = $2000
        // 250 * $3 = $750
        // $2750 total, or $3.66666/unit
        strategy.applyTo(null, result, null, planPrice, new BigDecimal(750), getUsage(BigDecimal.ZERO), false);
        assertEquals(new BigDecimal("3.67"), result.getPrice());

        // quantity exceeds max
        // 500 * $4 = $2000
        // 500 * $3 = $1500
        // 500 * $2 = $1000
        // 500 * $1 = $500
        // $5000 total, or 2.50/unit
        strategy.applyTo(null, result, null, planPrice, new BigDecimal(2000), getUsage(BigDecimal.ZERO), false);
        assertEquals(new BigDecimal("2.50"), result.getPrice());
    }

    public void testTieredWithUsage() throws Exception {
        PriceModelDTO planPrice = new PriceModelDTO();
        planPrice.setType(PriceModelStrategy.TIERED);

        planPrice.addAttribute("0",  "4.00");   // 0 - 500     = 500 @ $4
        planPrice.addAttribute("500",  "3.00"); // 500 - 1500  = 1000 @ $3
        planPrice.addAttribute("1500", "2.00"); // 1500 - 3000 = 1500 @ $2
        planPrice.addAttribute("3000", "1.00"); // 3000 - 5000 = 2000 @ $1
        planPrice.addAttribute("5000", "0.05"); // > 5000 @ $0.05

        PricingResult result = new PricingResult(1, 2, 3);

        // first 1500 units exist in previous usage, 100 purchased now
        // 500 * $4  = $2000
        // 1000 * $3 = $3000
        // existing $5000 total, or $3.333333/unit
        // -------------------------------------
        // 100 * $2 = $200
        // $200 total, or $2/unit
        strategy.applyTo(null, result, null, planPrice, new BigDecimal(100), getUsage(new BigDecimal(1500)), false);
        assertEquals(new BigDecimal("2.00"), result.getPrice());

        // first 500 units exist in previous usage, 2000 purchased now (hits multiple tiers)
        // 500 * $4  = $2000
        // existing $2000 total, or $4.00/unit
        // -------------------------------------
        // 1000 * $3 = $3000
        // 1000 * $2 = $2000
        // $5000 total, or $2.50/unit
        strategy.applyTo(null, result, null, planPrice, new BigDecimal(2000), getUsage(new BigDecimal(500)), false);
        assertEquals(new BigDecimal("2.50"), result.getPrice());

        // half of a tier covered by previous usage, 2000 purchased now (hits multiple tiers)
        // 500 * $4 = $2000
        // 200 * $3 = $600
        // existing $2600 total, or $3.714/unit
        // -------------------------------------
        // 800 * $3 = $2400   (800 + existing 200 fills the tier)
        // 1200 * $2 = $2400
        // $4800 total, or $2.40/unit
        strategy.applyTo(null, result, null, planPrice, new BigDecimal(2000), getUsage(new BigDecimal(700)), false);
        assertEquals(new BigDecimal("2.40"), result.getPrice());

        // existing quantity already exceeds max
        // 500 * $4  = $2000
        // 1000 * $3 = $3000
        // 1500 * $2 = $3000
        // 2000 * $1 = $2000
        // existing $10000 total, or $2.00/unit
        // -------------------------------------
        // 500 * $0.05 = $25
        // $25 total, or $0.05/unit
        strategy.applyTo(null, result, null, planPrice, new BigDecimal(500), getUsage(new BigDecimal(5000)), false);
        assertEquals(new BigDecimal("0.05"), result.getPrice());
    }
}
