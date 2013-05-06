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
 * Created on Nov 18, 2004
 *
 */
package com.sapienter.jbilling.server.order;

import java.io.Serializable;
import java.util.Comparator;

import com.sapienter.jbilling.server.order.db.OrderLineDTO;

/**
 * @author Emil
 *
 */
public class OrderLineComparator implements Comparator<OrderLineDTO>, Serializable {

    /* (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(OrderLineDTO o1, OrderLineDTO o2) {
        int retValue = 0;
        OrderLineDTO perA = (OrderLineDTO) o1;
        OrderLineDTO perB = (OrderLineDTO) o2;
        
        if (perA != null && perA.getItem() != null && 
                perA.getItem().getNumber() != null &&
                perB != null && perB.getItem() != null && 
                    perB.getItem().getNumber() != null) {
            retValue = perA.getItem().getNumber().compareTo(
                    perB.getItem().getNumber());
        } else if (perA != null && perA.getItem() != null && 
                    perA.getItem().getNumber() != null) {
                retValue = -1;
        } else if (perB != null && perB.getItem() != null && 
                perB.getItem().getNumber() != null) {
            retValue = 1;
        }
        
        return retValue;
    }

}
