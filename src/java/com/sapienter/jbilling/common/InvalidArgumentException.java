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
package com.sapienter.jbilling.common;

import org.apache.log4j.Logger;

public class InvalidArgumentException extends RuntimeException {
    private final Integer code;
    private final Exception e;
    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(InvalidArgumentException.class));
    
    public InvalidArgumentException(String message, Integer code, Exception e) {
        super(message);
        this.code = code;
        this.e = e;
        LOG.debug(message + ((e == null) ? "" : " - " + e.getMessage()));
    }

    public InvalidArgumentException(String message, Integer code) {
        this(message, code, null);
    }

    public InvalidArgumentException(InvalidArgumentException e) {
        this(e.getMessage(), e.getCode(), e.getException());
    }
    
    public Integer getCode() {
        return code;
    }
    public Exception getException() {
        return e;
    }
}
