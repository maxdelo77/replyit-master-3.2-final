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
package com.sapienter.jbilling.server;

import junit.framework.TestCase;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author Brian Cowdery
 * @since 29-04-2010
 */
public class BigDecimalTestCase extends TestCase { // todo: move base test case so it's available to all testing suites

    public static final Integer COMPARISON_SCALE = 2;
    public static final RoundingMode COMPARISON_ROUNDING_MODE = RoundingMode.HALF_UP;

    public BigDecimalTestCase() {
    }

    public BigDecimalTestCase(String name) {
        super(name);
    }

    /**
     * Asserts that 2 given BigDecimal numbers are equivalent to 2 decimal places.
     * 
     * @param expected expected BigDecimal value
     * @param actual actual BigDecimal value
     */
    public static void assertEquals(BigDecimal expected, BigDecimal actual) {
        assertEquals(null, expected, actual);
    }

    /**
     * @see #assertEquals(java.math.BigDecimal, java.math.BigDecimal)
     *
     * @param message error message if assert fails
     * @param expected expected BigDecimal value
     * @param actual actual BigDecimal value
     */
    public static void assertEquals(String message, BigDecimal expected, BigDecimal actual) {
        assertEquals(message,
                     (Object) (expected == null ? null : expected.setScale(COMPARISON_SCALE, COMPARISON_ROUNDING_MODE)),
                     (Object) (actual == null ? null : actual.setScale(COMPARISON_SCALE, COMPARISON_ROUNDING_MODE)));
    }
}
