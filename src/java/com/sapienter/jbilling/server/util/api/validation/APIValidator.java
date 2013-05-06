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

package com.sapienter.jbilling.server.util.api.validation;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.common.SessionInternalError;
import org.apache.log4j.Logger;
import org.springframework.aop.MethodBeforeAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 *
 * @author emilc
 */
public class APIValidator implements MethodBeforeAdvice {

    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(APIValidator.class));
    
    private Validator validator;
    private Set<String> objectsToTest = null;

	public void setValidator(Validator validator) {
		this.validator = validator;
	}

    public Validator getValidator() {
		return validator;
	}

	public void setObjectsToTest(Set<String> objectsToTest) {
		this.objectsToTest = objectsToTest;
	}

	public Set<String> getObjectsToTest() {
		return objectsToTest;
	}

	public void before(Method method, Object[] args, Object target) throws Throwable {
		ArrayList<String> errors = new ArrayList<String>();
		
        for (Object arg: args) {
            if (arg != null) {

                // object name from the argument
                String objectName = getObjectName(arg);

                // argument is an array, object name from contents
            	if (arg.getClass().isArray()) {
                    Object[] array = (Object[]) arg;
                    if (array.length > 0) {
                        objectName = getObjectName(array[0]);
                        LOG.debug("Object name: '" + objectName + "'");
                    }
            	}

                boolean testThisObject = false;
                for (String test: objectsToTest) {
                    if (objectName.endsWith(test)) {
                        testThisObject = true;
                        break;
                    }
                }

                if (testThisObject) {
                	if (arg.getClass().isArray()) { 
                		Object[] objArr = (Object[]) arg;
                		for (Object o : objArr) {
                			errors.addAll(validateObject(method, objectName, o));
                		}
                		
                	} else {
                		errors.addAll(validateObject(method, objectName, arg));
                	}
                }
            }
        }
        
        if (!errors.isEmpty()) {
        	throw new SessionInternalError("Validation of '" + method.getName() + "()' arguments failed.",
                                           errors.toArray(new String[errors.size()]));
        }
    }

    private String getObjectName(Object object) {
        return object.getClass().getSimpleName();
    }


    private List<String> getErrorMessages(Set<ConstraintViolation<Object>> constraintViolations, String objectName) {
        List<String> errors = new ArrayList<String>(constraintViolations.size());

        if (!constraintViolations.isEmpty()) {
            for (ConstraintViolation<Object> violation: constraintViolations) {
                String path = violation.getPropertyPath().toString().replaceAll("\\[\\d+\\]", ""); // strip array indices from path
                errors.add(objectName + "," + path + "," + violation.getMessage());
            }
        }

        return errors;
    }

    /**
     * Validates a method call argument, returning a list of error messages to be thrown
     * as part of a SessionInternalError.
     *
     * @param method method to validate
     * @param objectName object name of method argument to validate
     * @param arg method argument to validate
     * @return error messages
     */
	private List<String> validateObject(Method method, String objectName, Object arg) {
        // validate all common validations
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(arg);

        // validate "create" or "update" group validations
        if (method.getName().startsWith("create")) {
            constraintViolations.addAll(validator.validate(arg, CreateValidationGroup.class));
        } else if (method.getName().startsWith("update")) {
            constraintViolations.addAll(validator.validate(arg, UpdateValidationGroup.class));
        }

        // build error messages
        return getErrorMessages(constraintViolations, objectName);
	}

    /**
     * Run validations for the given object.
     *
     * If a group is specified then only the validations for the given group will be run.
     *
     * @param object object to validate
     * @param validationGroups groups to run
     * @throws SessionInternalError if validation failed
     */
    public void validateObject(Object object, Class... validationGroups) throws SessionInternalError{
       validateObjects(Arrays.asList(object), validationGroups);
    }

    /**
     * Validate all objects in the given list and throw a SessionInternalError if any
     * constraints have been violated.
     *
     * If a group is specified then only the validations for the given group will be run.
     *
     * @param objects objects to validate
     * @param validationGroups groups to run
     * @throws SessionInternalError if validation failed
     */
    public void validateObjects(List<Object> objects, Class... validationGroups) throws SessionInternalError {
        List<String> errors = new ArrayList<String>();

        for (Object object : objects) {
            // run validations
            Set<ConstraintViolation<Object>> constraintViolations;
            if (validationGroups != null && validationGroups.length > 0) {
                constraintViolations = getValidator().validate(object, validationGroups);
            } else {
                constraintViolations = getValidator().validate(object);
            }

            // build error messages
            String objectName = getObjectName(object);
            errors.addAll(getErrorMessages(constraintViolations, objectName));
        }

        // throw exception if error messages returned
        if (!errors.isEmpty()) {
            throw new SessionInternalError("Validations failed.", errors.toArray(new String[errors.size()]));
        }
    }
}
