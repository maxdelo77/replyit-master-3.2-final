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
 * Created on Oct 11, 2004
 *
 */
package com.sapienter.jbilling.server.pluggableTask;

import java.util.Calendar;
import java.util.GregorianCalendar;

import com.sapienter.jbilling.server.invoice.NewInvoiceDTO;

/**
 * @author Emil
 *
 */
public class CalculateDueDateDfFm extends CalculateDueDate {
    public void apply(NewInvoiceDTO invoice, Integer userId) throws TaskException {
        // make the normal calculations first
        super.apply(invoice, null);
        // then get into the Df Fm: last day of the month
        if (invoice.getDueDatePeriod().getDf_fm() != null && 
                invoice.getDueDatePeriod().getDf_fm().booleanValue()) {
            GregorianCalendar cal = new GregorianCalendar();
            cal.setTime(invoice.getDueDate());
            int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
            cal.set(Calendar.DAY_OF_MONTH, lastDay);
            invoice.setDueDate(cal.getTime());
        }
    }
        
}
