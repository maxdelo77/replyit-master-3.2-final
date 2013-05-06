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

import com.sapienter.jbilling.server.order.db.OrderDTO;

/**
 * Defines the methods for those tasks that will be configured in the
 * order_processing_task table, allowing each entity to have its own
 * sets of processing rules for orders.
 * 
 * @author emilc
 *
 */


public interface OrderProcessingTask {
    public void doProcessing(OrderDTO order) throws TaskException;
}
