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
 * Created on Nov 22, 2004
 *
 */
package com.sapienter.jbilling.server.order;

import java.util.Comparator;

import com.sapienter.jbilling.server.order.db.OrderDTO;

/**
 * @author Emil
 */
public class OrderEntityComparator implements Comparator {

    /* (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(Object o1, Object o2) {
        OrderDTO parA = (OrderDTO) o1;
        OrderDTO parB = (OrderDTO) o2;
        
        return parA.getId().compareTo(parB.getId());
    }

}
