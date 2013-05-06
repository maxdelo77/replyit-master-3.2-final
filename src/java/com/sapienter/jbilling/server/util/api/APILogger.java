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

package com.sapienter.jbilling.server.util.api;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Arrays;
import org.apache.log4j.Logger;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.aop.MethodBeforeAdvice;

import com.sapienter.jbilling.common.FormatLogger;

/**
 *
 * @author emilc
 */
public class APILogger implements MethodBeforeAdvice, AfterReturningAdvice {

    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(APILogger.class));

    public void before(Method method, Object[] args, Object target) throws Throwable {
        LOG.debug("Call to " + method.getName() + " parameters: " + Arrays.toString(args));
    }

    public void afterReturning(Object ret, Method method, Object[] args, Object target) throws Throwable {
        StringBuffer retStr = new StringBuffer();
        if (ret != null) {
            if (ret.getClass().isArray()) {
                for (int f = 0; f < Array.getLength(ret); f++) {
                    Object val = Array.get(ret, f);
                    retStr.append('[');
                    retStr.append(val == null ? "null" : Array.get(ret, f).toString());
                    retStr.append(']');
                }
            } else {
                retStr.append(ret.toString());
            }
        } else {
            retStr.append("null");
        }
        LOG.debug("Done call to " + method.getName() + " returning: " + retStr);
    }
}
