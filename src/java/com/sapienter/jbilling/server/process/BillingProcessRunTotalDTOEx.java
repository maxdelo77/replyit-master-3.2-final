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

import java.math.BigDecimal;
import java.util.Hashtable;

import com.sapienter.jbilling.server.process.db.ProcessRunTotalDTO;
import com.sapienter.jbilling.server.util.db.CurrencyDTO;

/**
 * @author Emil
 */
public class BillingProcessRunTotalDTOEx extends ProcessRunTotalDTO {

    private Hashtable pmTotals = null;
    private String currencyName = null;
    
    public BillingProcessRunTotalDTOEx() {
        super();
        pmTotals = new Hashtable();
    }

    public BillingProcessRunTotalDTOEx(Integer id, CurrencyDTO currency, BigDecimal totalInvoiced,
                                       BigDecimal totalPaid, BigDecimal totalNotPaid) {
        super((id == null ?  0 : id), null, currency, totalInvoiced, totalPaid, totalNotPaid);
        pmTotals = new Hashtable();
    }

    public Hashtable getPmTotals() {
        return pmTotals;
    }

    public void setPmTotals(Hashtable pmTotals) {
        this.pmTotals = pmTotals;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    @Override
    public String toString() {
        StringBuffer ret = new StringBuffer(super.toString());
        ret.append(" currencyName: ")
                .append(currencyName)
                .append(" pmTotals ")
                .append(pmTotals);

        return ret.toString();
    }
}
