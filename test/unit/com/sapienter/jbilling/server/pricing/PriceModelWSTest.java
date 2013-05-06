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

package com.sapienter.jbilling.server.pricing;

import com.sapienter.jbilling.server.pricing.db.PriceModelDTO;
import com.sapienter.jbilling.server.pricing.db.PriceModelStrategy;
import com.sapienter.jbilling.server.util.db.CurrencyDTO;
import junit.framework.TestCase;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author Brian Cowdery
 * @since 09-08-2010
 */
public class PriceModelWSTest extends TestCase {

    public PriceModelWSTest() {
    }

    public PriceModelWSTest(String name) {
        super(name);
    }

    public void testFromDTO() {
        SortedMap<String, String> attributes = new TreeMap<String, String>();
        attributes.put("null_attr", null);
        attributes.put("attr", "some value");

        PriceModelDTO dto = new PriceModelDTO();
        dto.setId(1);
        dto.setType(PriceModelStrategy.METERED);
        dto.setAttributes(attributes);
        dto.setRate(new BigDecimal("0.7"));
        dto.setCurrency(new CurrencyDTO(1));

        // convert to PriceModelWS
        PriceModelWS ws = new PriceModelWS(dto);

        assertEquals(dto.getId(), ws.getId());
        assertEquals("METERED", ws.getType());
        assertEquals(dto.getRate(), ws.getRateAsDecimal());
        assertEquals(dto.getCurrency().getId(), ws.getCurrencyId().intValue());

        assertNotSame(dto.getAttributes(), ws.getAttributes());
        assertEquals(PriceModelDTO.ATTRIBUTE_WILDCARD, ws.getAttributes().get("null_attr"));
        assertEquals("some value", ws.getAttributes().get("attr"));

        // convert to PriceModelWS with null currency & type (should be safe, won't throw an exception)
        dto.setType(null);
        dto.setCurrency(null);

        ws = new PriceModelWS(dto);

        assertNull(ws.getType());
        assertNull(ws.getCurrencyId());
    }
}
