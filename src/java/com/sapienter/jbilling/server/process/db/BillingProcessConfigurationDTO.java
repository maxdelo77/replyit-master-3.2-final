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
package com.sapienter.jbilling.server.process.db;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;
import javax.persistence.Version;

import com.sapienter.jbilling.server.process.BillingProcessConfigurationWS;
import com.sapienter.jbilling.server.user.db.CompanyDTO;

@Entity
@TableGenerator(
        name = "billing_process_configuration_GEN", 
        table = "jbilling_seqs", 
        pkColumnName = "name", 
        valueColumnName = "next_id", 
        pkColumnValue = "billing_process_configuration", 
        allocationSize = 100)
@Table(name = "billing_process_configuration")
public class BillingProcessConfigurationDTO implements Serializable {

    private int id;
    private CompanyDTO entity;
    private Date nextRunDate;
    private Integer generateReport;
    private Integer retries;
    private Integer daysForRetry;
    private Integer daysForReport;
    private int reviewStatus;
    private int dueDateUnitId;
    private int dueDateValue;
    private Integer dfFm;
    private Integer onlyRecurring;
    private Integer invoiceDateProcess;
    private Integer autoPayment;
    private int maximumPeriods;
    private int autoPaymentApplication;
    private int versionNum;

    public BillingProcessConfigurationDTO() {
    }

    public BillingProcessConfigurationDTO(int id,
            Date nextRunDate, Integer generateReport, int reviewStatus,
            int dueDateUnitId, int dueDateValue,
            Integer onlyRecurring, Integer invoiceDateProcess, Integer autoPayment,
            int maximumPeriods, int autoPaymentApplication) {
        this.id = id;
        this.nextRunDate = nextRunDate;
        this.generateReport = generateReport;
        this.reviewStatus = reviewStatus;
        this.dueDateUnitId = dueDateUnitId;
        this.dueDateValue = dueDateValue;
        this.onlyRecurring = onlyRecurring;
        this.invoiceDateProcess = invoiceDateProcess;
        this.autoPayment = autoPayment;
        this.maximumPeriods = maximumPeriods;
        this.autoPaymentApplication = autoPaymentApplication;
    }

    public BillingProcessConfigurationDTO(int id,
            CompanyDTO entity, Date nextRunDate, Integer generateReport,
            Integer retries, Integer daysForRetry, Integer daysForReport,
            int reviewStatus, int dueDateUnitId,
            int dueDateValue, Integer dfFm, Integer onlyRecurring,
            Integer invoiceDateProcess, Integer autoPayment, int maximumPeriods,
            int autoPaymentApplication) {
        this.id = id;
        this.entity = entity;
        this.nextRunDate = nextRunDate;
        this.generateReport = generateReport;
        this.retries = retries;
        this.daysForRetry = daysForRetry;
        this.daysForReport = daysForReport;
        this.reviewStatus = reviewStatus;
        this.dueDateUnitId = dueDateUnitId;
        this.dueDateValue = dueDateValue;
        this.dfFm = dfFm;
        this.onlyRecurring = onlyRecurring;
        this.invoiceDateProcess = invoiceDateProcess;
        this.autoPayment = autoPayment;
        this.maximumPeriods = maximumPeriods;
        this.autoPaymentApplication = autoPaymentApplication;
    }

    public BillingProcessConfigurationDTO(BillingProcessConfigurationWS ws, CompanyDTO entity) {
        this.id = ws.getId();
        this.entity = entity;
        this.nextRunDate = ws.getNextRunDate();
        this.generateReport = ws.getGenerateReport();
        this.retries = ws.getRetries();
        this.daysForRetry = ws.getDaysForRetry();
        this.daysForReport = ws.getDaysForReport();
        this.reviewStatus = ws.getReviewStatus();
        this.dueDateUnitId = ws.getDueDateUnitId();
        this.dueDateValue = ws.getDueDateValue();
        this.dfFm = ws.getDfFm();
        this.onlyRecurring = ws.getOnlyRecurring();
        this.invoiceDateProcess = ws.getInvoiceDateProcess();
        this.autoPayment = ws.getAutoPayment();
        this.maximumPeriods = ws.getMaximumPeriods();
        this.autoPaymentApplication = ws.getAutoPaymentApplication();                        
    }

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "billing_process_configuration_GEN")
    @Column(name = "id", unique = true, nullable = false)
    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entity_id")
    public CompanyDTO getEntity() {
        return this.entity;
    }

    public void setEntity(CompanyDTO entity) {
        this.entity = entity;
    }

    @Column(name = "next_run_date", nullable = false, length = 13)
    public Date getNextRunDate() {
        return this.nextRunDate;
    }

    public void setNextRunDate(Date nextRunDate) {
        this.nextRunDate = nextRunDate;
    }

    @Column(name = "generate_report", nullable = false)
    public Integer getGenerateReport() {
        return this.generateReport;
    }

    public void setGenerateReport(Integer generateReport) {
        this.generateReport = generateReport;
    }

    @Column(name = "retries")
    public Integer getRetries() {
        return this.retries;
    }

    public void setRetries(Integer retries) {
        this.retries = retries;
    }

    @Column(name = "days_for_retry")
    public Integer getDaysForRetry() {
        return this.daysForRetry;
    }

    public void setDaysForRetry(Integer daysForRetry) {
        this.daysForRetry = daysForRetry;
    }

    @Column(name = "days_for_report")
    public Integer getDaysForReport() {
        return this.daysForReport;
    }

    public void setDaysForReport(Integer daysForReport) {
        this.daysForReport = daysForReport;
    }

    @Column(name = "review_status", nullable = false)
    public int getReviewStatus() {
        return this.reviewStatus;
    }

    public void setReviewStatus(int reviewStatus) {
        this.reviewStatus = reviewStatus;
    }

    @Column(name = "due_date_unit_id", nullable = false)
    public int getDueDateUnitId() {
        return this.dueDateUnitId;
    }

    public void setDueDateUnitId(int dueDateUnitId) {
        this.dueDateUnitId = dueDateUnitId;
    }

    @Column(name = "due_date_value", nullable = false)
    public int getDueDateValue() {
        return this.dueDateValue;
    }

    public void setDueDateValue(int dueDateValue) {
        this.dueDateValue = dueDateValue;
    }

    @Column(name = "df_fm")
    public Integer getDfFm() {
        return this.dfFm;
    }

    public void setDfFm(Integer dfFm) {
        this.dfFm = dfFm;
    }

    @Column(name = "only_recurring", nullable = false)
    public Integer getOnlyRecurring() {
        return this.onlyRecurring;
    }

    public void setOnlyRecurring(Integer onlyRecurring) {
        this.onlyRecurring = onlyRecurring;
    }

    @Column(name = "invoice_date_process", nullable = false)
    public Integer getInvoiceDateProcess() {
        return this.invoiceDateProcess;
    }

    public void setInvoiceDateProcess(Integer invoiceDateProcess) {
        this.invoiceDateProcess = invoiceDateProcess;
    }

    @Column(name = "auto_payment", nullable = false)
    public Integer getAutoPayment() {
        return this.autoPayment;
    }

    public void setAutoPayment(Integer autoPayment) {
        this.autoPayment = autoPayment;
    }

    @Column(name = "maximum_periods", nullable = false)
    public int getMaximumPeriods() {
        return this.maximumPeriods;
    }

    public void setMaximumPeriods(int maximumPeriods) {
        this.maximumPeriods = maximumPeriods;
    }

    @Column(name = "auto_payment_application", nullable = false)
    public int getAutoPaymentApplication() {
        return this.autoPaymentApplication;
    }

    public void setAutoPaymentApplication(int autoPaymentApplication) {
        this.autoPaymentApplication = autoPaymentApplication;
    }

    @Version
    @Column(name="OPTLOCK")
    public int getVersionNum() {
        return versionNum;
    }

    public void setVersionNum(int versionNum) {
        this.versionNum = versionNum;
    }
}
