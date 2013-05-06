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

import com.sapienter.jbilling.server.util.NanoStopWatch;

/**
 * @author Vikas Bodani
 * @since Dec 12, 2011
 * Logging aspect that uses NanoStopWatch to calculate time elapsed in nano-seconds
 */
public class APIStopWatchAspect implements MethodBeforeAdvice, AfterReturningAdvice {

    private static final Logger LOG = Logger.getLogger(APIStopWatchAspect.class);

    private NanoStopWatch stopWatch= null;
    
    public NanoStopWatch getStopWatch() {
        return stopWatch;
    }

    public void setStopWatch(NanoStopWatch stopWatch) {
        this.stopWatch = stopWatch;
    }

    public void before(Method method, Object[] args, Object target) throws Throwable {
        stopWatch.setName(method.getName());
        stopWatch.start();
    }

    public void afterReturning(Object ret, Method method, Object[] args, Object target) throws Throwable {
        stopWatch.stop();
//        log.debug("The method " + method.getName()
//                + "() ends");
        LOG.debug(target.getClass().getSimpleName() + "." + stopWatch.getName()
                + " took: " + stopWatch.getElapsedMilliseconds()
                + " ms.");
    }
}
