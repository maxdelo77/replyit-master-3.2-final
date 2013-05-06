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

package com.sapienter.jbilling.server.order.validator;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.log4j.Logger;
import org.springmodules.cache.util.Reflections;

import com.sapienter.jbilling.common.FormatLogger;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * DateRangeValidator
 *
 * @author Brian Cowdery
 * @since 26/01/11
 */
public class DateRangeValidator implements ConstraintValidator<DateRange, Object> {

    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(DateRangeValidator.class));

    private String startDateFieldName;
    private String endDateFieldName;

    public void initialize(final DateRange dateRange) {
        startDateFieldName = dateRange.start();
        endDateFieldName = dateRange.end();
    }

    public boolean isValid(Object object, ConstraintValidatorContext constraintValidatorContext) {
        try {
            Class klass = object.getClass();

            Date startDate = (Date) getAccessorMethod(klass, startDateFieldName).invoke(object);
            Date endDate = (Date) getAccessorMethod(klass, endDateFieldName).invoke(object);

            return startDate == null || endDate == null || startDate.before(endDate);

        } catch (IllegalAccessException e) {
            LOG.debug("Illegal access to the date range property fields.");
        } catch (NoSuchMethodException e) {
            LOG.debug("Date range property missing JavaBeans getter/setter methods.");
        } catch (InvocationTargetException e) {
            LOG.debug("Date property field cannot be accessed.");
        } catch (ClassCastException e) {
            LOG.debug("Property does not contain a java.util.Date object.");
        }

        return false;
    }

    /**
     * Returns the accessor method for the given property name. This assumes
     * that the property follows normal getter/setter naming conventions so that
     * the method name can be resolved introspectively.
     *
     * @param klass class of the target object
     * @param propertyName property name
     * @return accessor method
     */
    public Method getAccessorMethod(Class klass, String propertyName) throws NoSuchMethodException {
        return klass.getMethod("get" + WordUtils.capitalize(propertyName));
    }
}
