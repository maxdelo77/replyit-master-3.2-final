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

import com.sapienter.jbilling.server.process.db.ProcessRunTotalDTO;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * ProcessRunTotalWS
 *
 * @author Brian Cowdery
 * @since 25-10-2010
 */
public class ProcessRunTotalWS implements Serializable {

    private Integer id;
    private Integer processRunId;
    private Integer currencyId;
    private String totalInvoiced;
    private String totalPaid;
    private String totalNotPaid;

    public ProcessRunTotalWS() {
    }

    public ProcessRunTotalWS(ProcessRunTotalDTO dto) {
        this.id = dto.getId();
        this.processRunId = dto.getProcessRun() != null ? dto.getProcessRun().getId() : null;
        this.currencyId = dto.getCurrency() != null ? dto.getCurrency().getId() : null;
        setTotalInvoiced(dto.getTotalInvoiced());
        setTotalPaid(dto.getTotalPaid());
        setTotalNotPaid(dto.getTotalNotPaid());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getProcessRunId() {
        return processRunId;
    }

    public void setProcessRunId(Integer processRunId) {
        this.processRunId = processRunId;
    }

    public Integer getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Integer currencyId) {
        this.currencyId = currencyId;
    }

    public String getTotalInvoiced() {
        return totalInvoiced;
    }

    public BigDecimal getTotalInvoicedAsDecimal() {
        return totalInvoiced != null ? new BigDecimal(totalInvoiced) : null;
    }

    public void setTotalInvoiced(String totalInvoiced) {
        this.totalInvoiced = totalInvoiced;
    }

    public void setTotalInvoiced(BigDecimal totalInvoiced) {
        this.totalInvoiced = (totalInvoiced != null ? totalInvoiced.toString() : null);
    }

    public String getTotalPaid() {
        return totalPaid;
    }

    public BigDecimal getTotalPaidAsDecimal() {
        return totalPaid != null ? new BigDecimal(totalPaid) : null;
    }

    public void setTotalPaid(String totalPaid) {
        this.totalPaid = totalPaid;
    }

    public void setTotalPaid(BigDecimal totalPaid) {
        this.totalPaid = (totalPaid != null ? totalPaid.toString() : null);
    }

    public String getTotalNotPaid() {
        return totalNotPaid;
    }

    public BigDecimal getTotalNotPaidAsDecimal() {
        return totalNotPaid != null ? new BigDecimal(totalNotPaid) : null;
    }

    public void setTotalNotPaid(String totalNotPaid) {
        this.totalNotPaid = totalNotPaid;
    }

    public void setTotalNotPaid(BigDecimal totalNotPaid) {
        this.totalNotPaid = (totalNotPaid != null ? totalNotPaid.toString() : null);
    }
    
    @Override
    public String toString() {
        return "ProcessRunTotalWS{"
               + "id=" + id
               + ", processRunId=" + processRunId
               + ", currencyId=" + currencyId
               + ", totalInvoiced=" + totalInvoiced
               + ", totalPaid=" + totalPaid
               + ", totalNotPaid=" + totalNotPaid
               + '}';
    }
}
