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
 * Created on Oct 7, 2003
 *
 */
package com.sapienter.jbilling.server.process;

import java.util.Date;

import com.sapienter.jbilling.server.process.db.BillingProcessDTO;
import com.sapienter.jbilling.server.process.db.ProcessRunDTO;
import java.util.List;



/**
 * @author Emil
 */
public class BillingProcessDTOEx extends BillingProcessDTO {

    private List<BillingProcessRunDTOEx> runs = null;
    private BillingProcessRunDTOEx grandTotal = null;
    private Integer retries = null;
    private Date billingDateEnd = null;

    // the number of orders included in this process
    private Integer ordersProcessed = null;

    /**
     * 
     */
    public BillingProcessDTOEx() {
        super();
    }

    /**
     * @param id
     * @param entityId
     * @param billingDate
     * @param periodUnitId
     * @param periodValue
     */
//    public BillingProcessDTOEx(Integer id, Integer entityId, Date billingDate,
//            Integer periodUnitId, Integer periodValue, Integer isReview,
//            Integer retries) {
//        super(id, entityId, billingDate, periodUnitId, periodValue, isReview,
//                retries);
//    }

    /**
     * @param otherValue
     */
//    public BillingProcessDTOEx(BillingProcessDTO otherValue) {
//        super(otherValue);
//    }

    /**
     * @return
     */
    public List<BillingProcessRunDTOEx> getRuns() {
        return runs;
    }

    /**
     * @param runs
     */
    public void setRuns(List<BillingProcessRunDTOEx> runs) {
        this.runs = runs;
    }

    /**
     * @return
     */
    public BillingProcessRunDTOEx getGrandTotal() {
        return grandTotal;
    }

    /**
     * @param grandTotal
     */
    public void setGrandTotal(BillingProcessRunDTOEx grandTotal) {
        this.grandTotal = grandTotal;
    }

    public Integer getRetries() {
        return retries;
    }

    public void setRetries(Integer retries) {
        this.retries = retries;
    }

    public Date getBillingDateEnd() {
        return billingDateEnd;
    }

    public void setBillingDateEnd(Date billingDateEnd) {
        this.billingDateEnd = billingDateEnd;
    }
    
    /**
     * The process will go over orders. This will happen only in the first run.
     * Subsequent runs will go over only invoices, not orders. Thus, we include
     * the number of orders in the process instead of the run
     * @return
     */
    public Integer getOrdersProcessed() {
        return ordersProcessed;
    }

    /**
     * @param ordersProcessed
     */
    public void setOrdersProcessed(Integer ordersProcessed) {
        this.ordersProcessed = ordersProcessed;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(super.toString() + " grandTotal = " + grandTotal +
                " retries: " + retries + " date end: " + billingDateEnd +
                " ordersProcessed: " + ordersProcessed);
        
        for (BillingProcessRunDTOEx run: runs) {
            ret.append(run.toString());
        }

        return ret.toString();
    }

}
