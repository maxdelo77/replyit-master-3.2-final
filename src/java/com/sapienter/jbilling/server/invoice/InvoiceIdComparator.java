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

package com.sapienter.jbilling.server.invoice;

import java.util.Comparator;

import com.sapienter.jbilling.server.invoice.db.InvoiceDTO;


/**
 * @author Emil
 */
public class InvoiceIdComparator implements Comparator {

    /* (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(Object arg0, Object arg1) {
        InvoiceDTO perA = (InvoiceDTO) arg0;
        InvoiceDTO perB = (InvoiceDTO) arg1;
        
        return new Integer(perA.getId()).compareTo(new Integer(perB.getId()));
    }

}
