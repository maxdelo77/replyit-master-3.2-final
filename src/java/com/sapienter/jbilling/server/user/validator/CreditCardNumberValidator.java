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

package com.sapienter.jbilling.server.user.validator;

import org.hibernate.validator.constraints.impl.LuhnValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.io.Serializable;

/**
 * CreditCardNumberValidator
 *
 * @author Brian Cowdery
 * @since 07/06/11
 */
public class CreditCardNumberValidator implements ConstraintValidator<CreditCardNumber, String>, Serializable {

    private static final long serialVersionUID = 1L;

    private LuhnValidator luhnValidator;

    public CreditCardNumberValidator() {
        this.luhnValidator = new LuhnValidator(2);
    }

    public void initialize(CreditCardNumber annotation) {
    }

    public boolean isValid(String value, ConstraintValidatorContext constraintContext) {
        if (value == null || value.startsWith("*")) {
            return true;
        }

        return luhnValidator.passesLuhnTest(value);
    }
}
