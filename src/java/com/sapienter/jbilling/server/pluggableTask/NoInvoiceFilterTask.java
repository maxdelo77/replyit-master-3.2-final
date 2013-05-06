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

/*
 * Created on Apr 28, 2003
 */
package com.sapienter.jbilling.server.pluggableTask;

import com.sapienter.jbilling.server.invoice.db.InvoiceDTO;
import com.sapienter.jbilling.server.process.db.BillingProcessDTO;

/**
 * For those entities that never want to get an invoice delegated
 */
public class NoInvoiceFilterTask extends PluggableTask
    implements InvoiceFilterTask {

    public boolean isApplicable(InvoiceDTO invoice, 
            BillingProcessDTO process) throws TaskException {

        return false;
    }

}
