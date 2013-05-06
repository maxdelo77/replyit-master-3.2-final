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

package com.sapienter.jbilling.server.process;

import java.util.Comparator;

import com.sapienter.jbilling.server.order.db.OrderProcessDTO;

/**
 * @author Emil
 */
public class OrderProcessIdComparator implements Comparator {

    /* (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(Object arg0, Object arg1) {
        OrderProcessDTO perA = (OrderProcessDTO) arg0;
        OrderProcessDTO perB = (OrderProcessDTO) arg1;
        
        if (perA.getId() == perB.getId()) return 0;
        if (perA.getId() < perB.getId()) return -1;
        else return 1;
    }

}
