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

package com.sapienter.jbilling.server.util.converter;

import org.apache.commons.beanutils.Converter;

import java.math.BigDecimal;

/**
 * BigDecimalConverter
 *
 * @author Brian Cowdery
 * @since 13/05/11
 */
public class BigDecimalConverter implements Converter {

    public BigDecimalConverter() {
    }

    public Object convert(Class type, Object value) {
        if (value == null) {
            return null;
        }

        BigDecimal decimal = (BigDecimal) value;
        if (decimal.compareTo(BigDecimal.ZERO) == 0) {
            return "0.00";
        } else {
            return decimal.toString();
        }
    }
}
