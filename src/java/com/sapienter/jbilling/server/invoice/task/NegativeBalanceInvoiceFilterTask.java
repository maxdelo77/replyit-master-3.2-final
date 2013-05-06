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

package com.sapienter.jbilling.server.invoice.task;

import com.sapienter.jbilling.server.invoice.db.InvoiceDTO;
import com.sapienter.jbilling.server.pluggableTask.InvoiceFilterTask;
import com.sapienter.jbilling.server.pluggableTask.PluggableTask;
import com.sapienter.jbilling.server.pluggableTask.TaskException;
import com.sapienter.jbilling.server.process.db.BillingProcessDTO;

import java.math.BigDecimal;

/**
 * Only allows invoices with a negative balance to be carried over.
 */
public class NegativeBalanceInvoiceFilterTask extends PluggableTask
        implements InvoiceFilterTask {

    public boolean isApplicable(InvoiceDTO invoice, BillingProcessDTO process) 
            throws TaskException {

        return BigDecimal.ZERO.compareTo(invoice.getBalance()) > 0;
    }
}
