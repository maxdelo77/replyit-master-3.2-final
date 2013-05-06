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

package com.sapienter.jbilling.server.util;

import org.apache.log4j.Logger;

import org.springframework.aop.ThrowsAdvice;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.common.SessionInternalError;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Re-throws any exceptions from the API as SessionInternalErrors to
 * prevent server exception classes being required on the client. 
 * Useful for remoting protocols such as Hessian which propagate the 
 * exception stack trace from the server to the client. 
 */
public class WSExceptionAdvice implements ThrowsAdvice {

    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(WSExceptionAdvice.class));

    public void afterThrowing(Method method, Object[] args, Object target, Exception throwable) {
    	// Avoid catching automatic validation exceptions
    	if (throwable instanceof SessionInternalError) {
    		String messages[] = ((SessionInternalError)throwable).getErrorMessages();
    		if (messages != null && messages.length > 0) {
    			LOG.debug("Validation errors:" + Arrays.toString(messages));
    			return;
    		}
    	}
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        pw.close();

        LOG.debug(throwable.getMessage() + "\n" + sw.toString());

        String message = "Error calling jBilling API. Method: " + method.getName();

        throw new SessionInternalError(message, throwable);
    }
}
