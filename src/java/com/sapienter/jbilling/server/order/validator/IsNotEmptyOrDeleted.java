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
 * IsNotEmptyOrDeleted
 * This annotation checks to see if the order lines list is empty and if it's not it checks to see if every element on
 * the list is deleted. In either case it returns <b>false</b>, otherwise <b>true</b>.
 *
 * @author Juan Vidal
 * @since 04/01/2012
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = IsNotEmptyOrDeletedValidator.class)
@Documented

public @interface IsNotEmptyOrDeleted {

    String message() default "validation.error.empty.lines";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
