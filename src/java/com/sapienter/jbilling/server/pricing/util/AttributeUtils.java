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

package com.sapienter.jbilling.server.pricing.util;

import com.sapienter.jbilling.common.SessionInternalError;
import com.sapienter.jbilling.server.pricing.db.AttributeDefinition;
import com.sapienter.jbilling.server.pricing.strategy.PricingStrategy;
import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalTime;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

/**
 * Simple utilities for parsing price model attributes.
 *
 * @author Brian Cowdery
 * @since 02/02/11
 */
public class AttributeUtils {

    /**
     * Validates that all the required attributes of the given strategy are present and of the
     * correct type.
     *
     * @param attributes attribute map
     * @param strategy   strategy to validate against
     * @throws SessionInternalError if attributes are missing or of an incorrect type
     */
    public static void validateAttributes(SortedMap<String, String> attributes, PricingStrategy strategy)
            throws SessionInternalError {

        String strategyName = strategy.getClass().getSimpleName();
        List<String> errors = new ArrayList<String>();

        for (AttributeDefinition definition : strategy.getAttributeDefinitions()) {
            String name = definition.getName();
            String value = attributes.get(name);

            // validate required attributes
            if (definition.isRequired() && (value == null || value.trim().equals(""))) {
                errors.add(strategyName + "," + name + ",validation.error.is.required");
            } else {
                // validate attribute types
                try {
                    switch (definition.getType()) {
                        case STRING:
                            // a string is a string...
                            break;
                        case TIME:
                            parseTime(value);
                            break;
                        case INTEGER:
                            parseInteger(value);
                            break;
                        case DECIMAL:
                            parseDecimal(value);
                            break;
                    }
                } catch (SessionInternalError validationException) {
                    errors.add(strategyName + "," + name + "," + validationException.getErrorMessages()[0]);
                }
            }
        }

        // throw new validation exception with complete error list
        if (!errors.isEmpty()) {
            throw new SessionInternalError(strategyName + " attributes failed validation.",
                    errors.toArray(new String[errors.size()]));
        }
    }


    public static LocalTime getTime(Map<String, String> attributes, String name) {
        return parseTime(attributes.get(name));
    }

    /**
     * Parses the given value as LocalTime. If the value cannot be parsed, an exception will be thrown.
     *
     * @param value value to parse
     * @return parsed LocalTime
     * @throws SessionInternalError if value cannot be parsed as LocalTime
     */
    public static LocalTime parseTime(String value) {
        String[] time = value.split(":");

        if (time.length != 2)
            throw new SessionInternalError("Cannot parse attribute value '" + value + "' as a time of day.",
                    new String[]{"validation.error.not.time.of.day"});

        try {
            return new LocalTime(Integer.valueOf(time[0]), Integer.valueOf(time[1]));
        } catch (NumberFormatException e) {
            throw new SessionInternalError("Cannot parse attribute value '" + value + "' as a time of day.",
                    new String[]{"validation.error.not.time.of.day"});
        }
    }

    public static Integer getInteger(Map<String, String> attributes, String name) {
        return parseInteger(attributes.get(name));
    }

    /**
     * Parses the given value as an Integer. If the value cannot be parsed, an exception will be thrown.
     *
     * @param value value to parse
     * @return parsed integer
     * @throws SessionInternalError if value cannot be parsed as an integer
     */
    public static Integer parseInteger(String value) {
        if (value != null) {
            try {
                return Integer.valueOf(value);
            } catch (NumberFormatException e) {
                throw new SessionInternalError("Cannot parse attribute value '" + value + "' as an integer.",
                        new String[]{"validation.error.not.a.integer"});
            }
        }
        return null;
    }

    public static BigDecimal getDecimal(Map<String, String> attributes, String name) {
        return parseDecimal(attributes.get(name));
    }

    /**
     * Parses the given value as a BigDecimal. If the value cannot be parsed, an exception will be thrown.
     *
     * @param value value to parse
     * @return parsed integer
     * @throws SessionInternalError if value cannot be parsed as an BigDecimal
     */
    public static BigDecimal parseDecimal(String value) {
        if (value != null) {
            try {
                if (StringUtils.isEmpty(value)) {
                    return null;
                } else {
                    return new BigDecimal(value);
                }
            } catch (NumberFormatException e) {
                throw new SessionInternalError("Cannot parse attribute value '" + value + "' as a decimal number.",
                        new String[]{"validation.error.not.a.number"});
            }
        }
        return null;
    }


}
