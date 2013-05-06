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

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * DateBetween
 * This annotation checks that the field marked by it is between the two dates passed as parameter.
 *
 * @author Juan Vidal
 * @since 03/01/2012
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateBetweenValidator.class)
@Documented

public @interface DateBetween {

    String message() default "validation.date.between";

    /**
     * Start Date
     *
     * @return start date field
     */
    String start();

    /**
     * End Date
     *
     * @return end date field
     */
    String end();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
