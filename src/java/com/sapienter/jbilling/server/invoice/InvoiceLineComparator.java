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

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.server.invoice.db.InvoiceLineDTO;
import java.util.Comparator;

import org.apache.log4j.Logger;

import com.sapienter.jbilling.server.item.ItemBL;
import com.sapienter.jbilling.server.util.Constants;

/**
 * @author Emil
 */
public class InvoiceLineComparator implements Comparator<InvoiceLineDTO> {

    /* (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(InvoiceLineDTO perA, InvoiceLineDTO perB) {
        int retValue;
        
        // the line type should tell first
        if (perA.getOrderPosition() == perB.getOrderPosition()) {
            
            try {
                if (perA.getTypeId() ==
                        Constants.INVOICE_LINE_TYPE_SUB_ACCOUNT &&
                        perB.getTypeId() ==
                            Constants.INVOICE_LINE_TYPE_SUB_ACCOUNT) {
                    // invoice lines have to be grouped by user
                    // find out both users
                    retValue = perA.getSourceUserId().compareTo(perB.getSourceUserId());
                    /*
                    new FormatLogger(Logger.getLogger(InvoiceLineComparator.class).debug(
                            "Testing two sub account lines. a.userid " + 
                            perA.getSourceUserId() + " b.userid " + perB.getSourceUserId() +
                            " result " + retValue);
                            */
                    if (retValue != 0) {
                        // these are lines for two different users, so 
                        // they are different enough now
                        return retValue;
                    }
                } 
                // use the number
                if (perA.getItem() != null && perB.getItem() != null) {
                    ItemBL itemA = new ItemBL(perA.getItem());
                    ItemBL itemB = new ItemBL(perB.getItem());
                    if (itemA.getEntity().getNumber() == null &&
                            itemB.getEntity().getNumber() == null) {
                        retValue = new Integer(perA.getItem().getId()).compareTo(
                                new Integer(perB.getItem().getId()));
                    } else if (itemA.getEntity().getNumber() == null) {
                        retValue = 1;
                    } else if (itemB.getEntity().getNumber() == null) {
                        retValue = -1;
                    } else {
                        // none are null
                        retValue = itemA.getEntity().getNumber().compareTo(
                                itemB.getEntity().getNumber());
                    }
                } else {
                    retValue = 0;
                }
            } catch (Exception e) {
                new FormatLogger(Logger.getLogger(InvoiceLineComparator.class)).error(
                        "Comparing invoice lines " + perA + " " + perB, e);
                retValue = 0;
            }
        } else {
            retValue = new Integer(perA.getOrderPosition()).compareTo(perB.getOrderPosition());
        }
/*        
        new FormatLogger(Logger.getLogger(InvoiceLineComparator.class).debug(
                "Comparing " + perA.getId() + " " + perB.getId() +
                " result " + retValue);
*/        
        return retValue;
    }

}
