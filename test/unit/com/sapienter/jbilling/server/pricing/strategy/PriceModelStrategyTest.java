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

import com.sapienter.jbilling.server.pricing.PriceModelWS;
import com.sapienter.jbilling.server.pricing.db.PriceModelStrategy;
import junit.framework.TestCase;

/**
 * @author Brian Cowdery
 * @since 09-08-2010
 */
public class PriceModelStrategyTest extends TestCase {

    public PriceModelStrategyTest() {
    }

    public PriceModelStrategyTest(String name) {
        super(name);
    }

    public void testGetStrategy() {
        assertTrue(PriceModelStrategy.FLAT.getStrategy() instanceof FlatPricingStrategy);
        assertTrue(PriceModelStrategy.METERED.getStrategy() instanceof MeteredPricingStrategy);
        assertTrue(PriceModelStrategy.GRADUATED.getStrategy() instanceof GraduatedPricingStrategy);
        assertTrue(PriceModelStrategy.CAPPED_GRADUATED.getStrategy() instanceof CappedGraduatedPricingStrategy);
        assertTrue(PriceModelStrategy.TIME_OF_DAY.getStrategy() instanceof TimeOfDayPricingStrategy);
        assertTrue(PriceModelStrategy.PERCENTAGE.getStrategy() instanceof PercentageStrategy);
        assertTrue(PriceModelStrategy.TIME_OF_DAY_PERCENTAGE.getStrategy() instanceof TimeOfDayPercentageStrategy);
        assertTrue(PriceModelStrategy.TIERED.getStrategy() instanceof TieredPricingStrategy);
        assertTrue(PriceModelStrategy.POOLED.getStrategy() instanceof PooledPricingStrategy);

    }
}
