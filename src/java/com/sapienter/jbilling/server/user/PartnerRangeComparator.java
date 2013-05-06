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
 * Created on May 11, 2005
 *
 */
package com.sapienter.jbilling.server.user;

import java.util.Comparator;

import com.sapienter.jbilling.server.user.partner.db.PartnerRange;

/**
 * @author Emil
 *
 */
public class PartnerRangeComparator implements Comparator {

    /* (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(Object arg0, Object arg1) {
        PartnerRange perA = (PartnerRange) arg0;
        PartnerRange perB = (PartnerRange) arg1;
        
        return new Integer(perA.getRangeFrom()).compareTo(perB.getRangeFrom());
    }

}
