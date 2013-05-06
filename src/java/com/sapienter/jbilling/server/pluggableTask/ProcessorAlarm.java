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
 * Alarm for notification of payment processor fail/unavailable
 * payment results.
 * @author Lucas Pickstone
 */
public interface ProcessorAlarm {
    /**
     * Initialize before fail, unavailable or successful is called.
     * @param processorName The payment processor used.
     * @param entityId The entity (company) id of the payment.
     */
    public void init(String processorName, Integer entityId);

    /**
     * Payment processed, but failed/declined.
     */
    public void fail();

    /**
     * Processor was unavailable.
     */
    public void unavailable();

    /**
     * Payment processed and successful.
     */
    public void successful();
}
