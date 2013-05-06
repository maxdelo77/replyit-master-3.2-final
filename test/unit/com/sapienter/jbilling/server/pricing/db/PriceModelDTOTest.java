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

package com.sapienter.jbilling.server.pricing.db;

import com.sapienter.jbilling.common.CommonConstants;
import com.sapienter.jbilling.server.BigDecimalTestCase;
import com.sapienter.jbilling.server.item.tasks.PricingResult;
import com.sapienter.jbilling.server.pricing.PriceModelBL;
import com.sapienter.jbilling.server.pricing.PriceModelWS;
import com.sapienter.jbilling.server.pricing.strategy.FlatPricingStrategy;
import org.joda.time.DateMidnight;

import java.math.BigDecimal;
import java.util.Date;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author Brian Cowdery
 * @since 09-08-2010
 */
public class PriceModelDTOTest extends BigDecimalTestCase {

    public PriceModelDTOTest() {
    }

    public PriceModelDTOTest(String name) {
        super(name);
    }

    public void testAddAttributeWildcards() {
        PriceModelDTO planPrice = new PriceModelDTO();
        planPrice.addAttribute("null_attr", null);
        planPrice.addAttribute("attr", "some value");

        // null replaced with wildcard character
        assertNotNull(planPrice.getAttributes().get("null_attr"));
        assertEquals(PriceModelDTO.ATTRIBUTE_WILDCARD, planPrice.getAttributes().get("null_attr"));

        // non-null attributes left un-changed
        assertNotNull(planPrice.getAttributes().get("attr"));
        assertEquals("some value", planPrice.getAttributes().get("attr"));
    }

    public void testSetAttributesWildcards() {
        SortedMap<String, String> attributes = new TreeMap<String, String>();
        attributes.put("null_attr", null);
        attributes.put("attr", "some value");

        PriceModelDTO planPrice = new PriceModelDTO();
        planPrice.setAttributes(attributes);

        // null replaced with wildcard character
        assertNotNull(planPrice.getAttributes().get("null_attr"));
        assertEquals(PriceModelDTO.ATTRIBUTE_WILDCARD, planPrice.getAttributes().get("null_attr"));

        // non-null attributes left un-changed
        assertNotNull(planPrice.getAttributes().get("attr"));
        assertEquals("some value", planPrice.getAttributes().get("attr"));
    }

    public void testGetStrategy() {
        PriceModelDTO planPrice = new PriceModelDTO();

        // get strategy doesn't error if no type set
        assertNull(planPrice.getStrategy());

        // strategy defined by the set type
        planPrice.setType(PriceModelStrategy.FLAT);
        assertTrue(planPrice.getStrategy() instanceof FlatPricingStrategy);
    }

    public void testApplyTo() {
        PriceModelDTO planPrice = new PriceModelDTO();
        planPrice.setType(PriceModelStrategy.METERED);
        planPrice.setRate(new BigDecimal("0.7"));

        PricingResult result = new PricingResult(1, 2, 3);         // order, quantity, resutl, field, usage, false, date
        planPrice.applyTo(null, null, result, null, null, false, null);
        assertEquals(planPrice.getRate(), result.getPrice());
    }

    public void testPositiveChaining() {
        PriceModelDTO planPrice = new PriceModelDTO();
        planPrice.setType(PriceModelStrategy.METERED);
        planPrice.setRate(new BigDecimal("10.00"));

        PriceModelDTO next = new PriceModelDTO();
        next.setType(PriceModelStrategy.PERCENTAGE);
        next.addAttribute("percentage", "0.70");

        planPrice.setNext(next);

        PricingResult result = new PricingResult(1, 2, 3);
        planPrice.applyTo(null, null, result, null, null, false, null);

        // The price will remain the same as the root price model because the logic of chaining is in the PriceModelPricingTask now.
        assertEquals(new BigDecimal("10.00"), result.getPrice());
    }

    public void testNegativeChaining() {
        PriceModelDTO planPrice = new PriceModelDTO();
        planPrice.setType(PriceModelStrategy.METERED);
        planPrice.setRate(new BigDecimal("10.00"));

        PriceModelDTO next = new PriceModelDTO();
        next.setType(PriceModelStrategy.PERCENTAGE);
        next.addAttribute("percentage", "-0.70");

        planPrice.setNext(next);

        PricingResult result = new PricingResult(1, 2, 3);
        planPrice.applyTo(null, null, result, null, null, false, null);

        // The price will remain the same as the root price model because the logic of chaining is in the PriceModelPricingTask now.
        assertEquals(new BigDecimal("10.00"), result.getPrice());
    }

    public void testFromWS() {
        SortedMap<String, String> attributes = new TreeMap<String, String>();
        attributes.put("null_attr", null);
        attributes.put("attr", "some value");

        PriceModelWS ws = new PriceModelWS();
        ws.setId(1);
        ws.setType("METERED");
        ws.setAttributes(attributes);
        ws.setRate(new BigDecimal("0.7"));

        // convert to PriceModelDTO
        PriceModelDTO dto = new PriceModelDTO(ws, null);

        assertEquals(ws.getId(), dto.getId());
        assertEquals(PriceModelStrategy.METERED, dto.getType());
        assertEquals(ws.getRateAsDecimal(), dto.getRate());

        assertNotSame(ws.getAttributes(), dto.getAttributes());
        assertEquals(PriceModelDTO.ATTRIBUTE_WILDCARD, dto.getAttributes().get("null_attr"));
        assertEquals("some value", dto.getAttributes().get("attr"));
    }

    public void testGetPriceForDate() {
        SortedMap<Date, PriceModelDTO> prices = new TreeMap<Date, PriceModelDTO>();
        prices.put(CommonConstants.EPOCH_DATE, new PriceModelDTO(PriceModelStrategy.METERED, new BigDecimal("0.10"), null));
        prices.put(new DateMidnight(2011, 6, 1).toDate(), new PriceModelDTO(PriceModelStrategy.METERED, new BigDecimal("0.20"), null));
        prices.put(new DateMidnight(2011, 8, 1).toDate(), new PriceModelDTO(PriceModelStrategy.METERED, new BigDecimal("0.30"), null));

        // a price sometime after 1970-1-1 (epoch)
        PriceModelDTO model = PriceModelBL.getPriceForDate(prices, new DateMidnight(1985, 2, 4).toDate());
        assertEquals("should get epoch pricing", new BigDecimal("0.10"), model.getRate());

        // price model the day before the june price takes effect
        model = PriceModelBL.getPriceForDate(prices, new DateMidnight(2011, 5, 31).toDate());
        assertEquals("should get epoch pricing", new BigDecimal("0.10"), model.getRate());

        // price model on the day that the june price takes effect (date/times equal)
        model = PriceModelBL.getPriceForDate(prices, new DateMidnight(2011, 6, 1).toDate());
        assertEquals("should get june pricing", new BigDecimal("0.20"), model.getRate());

        // price model some day in august
        model = PriceModelBL.getPriceForDate(prices, new DateMidnight(2011, 8, 15).toDate());
        assertEquals("should get august pricing", new BigDecimal("0.30"), model.getRate());
    }
}
