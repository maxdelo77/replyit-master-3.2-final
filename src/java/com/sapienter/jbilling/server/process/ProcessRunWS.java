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

import com.sapienter.jbilling.server.process.db.ProcessRunDTO;
import com.sapienter.jbilling.server.process.db.ProcessRunTotalDTO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * ProcessRunWS
 *
 * @author Brian Cowdery
 * @since 25-10-2010
 */
public class ProcessRunWS implements Serializable {

    private Integer id;
    private Integer billingProcessId;
    private Date runDate;
    private Date started;
    private Date finished;
    private Integer invoicesGenerated;
    private Date paymentFinished;
    private List<ProcessRunTotalWS> processRunTotals = new ArrayList<ProcessRunTotalWS>(0);
    private Integer statusId;
    private String statusStr;

    public ProcessRunWS() {
    }

    public ProcessRunWS(ProcessRunDTO dto) {
        this.id = dto.getId();
        this.billingProcessId = dto.getBillingProcess() != null ? dto.getBillingProcess().getId() : null;
        this.runDate = dto.getRunDate();
        this.started = dto.getStarted();
        this.finished = dto.getFinished();
        this.invoicesGenerated = dto.getInvoicesGenerated();
        this.paymentFinished = dto.getPaymentFinished();
        this.statusId = dto.getStatus() != null ? dto.getStatus().getId() : null;

        // billing process run totals
        if (!dto.getProcessRunTotals().isEmpty()) {
            processRunTotals = new ArrayList<ProcessRunTotalWS>(dto.getProcessRunTotals().size());
            for (ProcessRunTotalDTO runTotal : dto.getProcessRunTotals())
                processRunTotals.add(new ProcessRunTotalWS(runTotal));
        }
    }

    public ProcessRunWS(BillingProcessRunDTOEx ex) {
        this((ProcessRunDTO) ex);

        this.statusStr = ex.getStatusStr();

        if (!ex.getTotals().isEmpty()) {
            processRunTotals = new ArrayList<ProcessRunTotalWS>(ex.getTotals().size());
            for (BillingProcessRunTotalDTOEx runTotal : ex.getTotals())
                processRunTotals.add(new ProcessRunTotalWS(runTotal));
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBillingProcessId() {
        return billingProcessId;
    }

    public void setBillingProcessId(Integer billingProcessId) {
        this.billingProcessId = billingProcessId;
    }

    public Date getRunDate() {
        return runDate;
    }

    public void setRunDate(Date runDate) {
        this.runDate = runDate;
    }

    public Date getStarted() {
        return started;
    }

    public void setStarted(Date started) {
        this.started = started;
    }

    public Date getFinished() {
        return finished;
    }

    public void setFinished(Date finished) {
        this.finished = finished;
    }

    public Integer getInvoicesGenerated() {
        return invoicesGenerated;
    }

    public void setInvoicesGenerated(Integer invoicesGenerated) {
        this.invoicesGenerated = invoicesGenerated;
    }

    public Date getPaymentFinished() {
        return paymentFinished;
    }

    public void setPaymentFinished(Date paymentFinished) {
        this.paymentFinished = paymentFinished;
    }

    public List<ProcessRunTotalWS> getProcessRunTotals() {
        return processRunTotals;
    }

    public void setProcessRunTotals(List<ProcessRunTotalWS> processRunTotals) {
        this.processRunTotals = processRunTotals;
    }

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }

    public String getStatusStr() {
        return statusStr;
    }

    public void setStatusStr(String statusStr) {
        this.statusStr = statusStr;
    }

    @Override
    public String toString() {
        return "ProcessRunWS{"
               + "id=" + id
               + ", billingProcessId=" + billingProcessId
               + ", runDate=" + runDate
               + ", started=" + started
               + ", finished=" + finished
               + ", invoicesGenerated=" + invoicesGenerated
               + ", paymentFinished=" + paymentFinished
               + ", processRunTotals=" + (processRunTotals != null ? processRunTotals.size() : null)
               + ", statusId=" + statusId
               + ", statusStr='" + statusStr + '\''
               + '}';
    }
}
