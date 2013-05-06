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
 * @author Brian Cowdery
 * @since 30-07-2010
 */
public class GraduatedPricingStrategyTest extends BigDecimalTestCase {

    // class under test
    private PricingStrategy strategy = new GraduatedPricingStrategy();

    public GraduatedPricingStrategyTest() {
    }

    public GraduatedPricingStrategyTest(String name) {
        super(name);
    }

    /**
     * Convenience test method to build a usage object for the given quantity.
     * @param usageQuantity quantity
	 * @param purchasedQuantity quantity
     * @return usage object
     */
    private Usage getUsage(BigDecimal usageQuantity, BigDecimal purchasedQuantity) {
        Usage usage = new Usage();
        usage.setQuantity(usageQuantity);
        usage.setCurrentQuantity(purchasedQuantity);

        return usage;
    }

	public void testApplyToOverIncluded() throws Exception {
		PriceModelDTO planPrice = new PriceModelDTO();
		planPrice.setType(PriceModelStrategy.GRADUATED);

		BigDecimal rate = new BigDecimal("0.07");
		planPrice.setRate(rate);
		planPrice.addAttribute("included", "1000");

		// included minutes already exceeded by current usage
		PricingResult result = new PricingResult(1, 2, 3);
		// total quantity = purchase quantity + usage = 1001
		BigDecimal purchaseQuantity = new BigDecimal(2);
		BigDecimal usageQuantity = new BigDecimal(1000);

		// 1 purchased, 1000 usage = 1001 total quantity, 1000 included ==> 1
		// rated with billable = rate
		strategy.applyTo(null, result, null, planPrice, purchaseQuantity,
				getUsage(usageQuantity, purchaseQuantity), false);

		// single price = rate
		assertEquals(rate, result.getPrice());
	}

	public void testApplyToUnderIncluded() throws Exception {
		PriceModelDTO planPrice = new PriceModelDTO();
		planPrice.setType(PriceModelStrategy.GRADUATED);

		BigDecimal purchaseQuantity = new BigDecimal(10);
		BigDecimal rate = new BigDecimal("0.07");
		planPrice.setRate(rate);
		planPrice.addAttribute("included", "1000");

		// included minutes already exceeded by current usage
		PricingResult result = new PricingResult(1, 2, 3);
		strategy.applyTo(null, result, null, planPrice, purchaseQuantity,
				getUsage(BigDecimal.ZERO, purchaseQuantity), false); // 10
																		// purchased,
																		// 0

		assertEquals(BigDecimal.ZERO, result.getPrice());
	}

	public void testApplyToPartialOverIncluded() throws Exception {
		PriceModelDTO planPrice = new PriceModelDTO();
		planPrice.setType(PriceModelStrategy.GRADUATED);

		BigDecimal rate = new BigDecimal("1.00"); // round numbers for easy math
													// :)
		planPrice.setRate(rate);
		planPrice.addAttribute("included", "1000");

		// 20 purchased, 990 usage
		// total quantity = purchase quantity + usage = 1010
		BigDecimal purchaseQuantity = new BigDecimal(20);
		BigDecimal usageQuantity = new BigDecimal(990);

		PricingResult result = new PricingResult(1, 2, 3);
		strategy.applyTo(null, result, null, planPrice, purchaseQuantity,
				getUsage(usageQuantity, purchaseQuantity), false); // 10 minutes
																	// include

		// half of the call exceeds the included minutes
		// rate should be 50% of the plan rate
		assertEquals(new BigDecimal("0.50"), result.getPrice());

		// 100 purchased, 980 usage
		purchaseQuantity = new BigDecimal(100);
		usageQuantity = new BigDecimal(980);

		PricingResult result2 = new PricingResult(1, 2, 3);
		strategy.applyTo(null, result2, null, planPrice, purchaseQuantity,
				getUsage(usageQuantity, purchaseQuantity), false); // 20 minutes
																	// includ

		// 80% of the call exceeds the included minutes
		// rate should be 80% of the plan rate
		assertEquals(new BigDecimal("0.80"), result2.getPrice());
	}

}
