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

package com.sapienter.jbilling.server.pluggableTask;

/**
 * @author emilc
 *
 */
public class TaskException extends Exception {

    /**
     * Constructor for TaskException.
     */
    public TaskException() {
        super();
    }

    public TaskException(Exception e) {
        super(e);
    }

    /**
     * Constructor for TaskException.
     * @param message
     */
    public TaskException(String message) {
        super(message);
    }

}
