package com.sapienter.jbilling.server.util;

import junit.framework.TestCase;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * User: Nikhil
 * Date: 10/15/12
 */
public class JBillingTestUtils extends TestCase {

    public static void assertEquals(String message, BigDecimal expected, BigDecimal actual) {
        assertEquals(message,
                (Object) (expected == null ? null : expected.setScale(2, RoundingMode.HALF_UP)),
                (Object) (actual == null ? null : actual.setScale(2, RoundingMode.HALF_UP)));
    }

}
