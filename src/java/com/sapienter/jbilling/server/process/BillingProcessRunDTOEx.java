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
 * Created on Oct 12, 2003
 *
 */
package com.sapienter.jbilling.server.process;

import java.util.Date;
import java.util.List;

import com.sapienter.jbilling.server.process.db.ProcessRunDTO;
import java.util.ArrayList;

/**
 * @author Emil
 */
public class BillingProcessRunDTOEx extends ProcessRunDTO {

    List<BillingProcessRunTotalDTOEx> totals = null;
    String statusStr;
    Long usersSucceeded;
    Long usersFailed;
    /**
     * 
     */
    public BillingProcessRunDTOEx() {
        super();
        totals = new ArrayList<BillingProcessRunTotalDTOEx>();
        setInvoicesGenerated(0);
    }

    /**
     * @param id
     * @param tryNumber
     * @param started
     * @param finished
     * @param invoiceGenerated
     * @param totalInvoiced
     * @param totalPaid
     * @param totalNotPaid
     */
    public BillingProcessRunDTOEx(Integer id, Date runDate, Date started,
            Date finished, Date paymentFinished, Integer invoiceGenerated) {
        setId(id);
        setRunDate(runDate);
        setStarted(started);
        setFinished(finished);
        setPaymentFinished(paymentFinished);
        setInvoicesGenerated(invoiceGenerated);
        
        totals = new ArrayList<BillingProcessRunTotalDTOEx>();
    }

    public List<BillingProcessRunTotalDTOEx> getTotals() {
        return totals;
    }

    public void setTotals(List<BillingProcessRunTotalDTOEx> totals) {
        this.totals = totals;
    }

    public String getStatusStr() {
        return statusStr;
    }

    public void setStatusStr(String statusStr) {
        this.statusStr = statusStr;
    }

    public Long getUsersSucceeded() {
        return usersSucceeded;
    }

    public void setUsersSucceeded(Long usersSucceeded) {
        this.usersSucceeded = usersSucceeded;
    }

    public Long getUsersFailed() {
        return usersFailed;
    }

    public void setUsersFailed(Long usersFailed) {
        this.usersFailed = usersFailed;
    }

    @Override
    public String toString() {
        StringBuffer ret = new StringBuffer(super.toString());
        ret.append(" totals: ");
        for (BillingProcessRunTotalDTOEx x : totals) {
            ret.append(x.toString());
        }

        return ret.toString();
    }
}
