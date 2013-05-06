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
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;
import javax.persistence.Version;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.server.process.BillingProcessWS;
import org.apache.log4j.Logger;

import com.sapienter.jbilling.server.invoice.db.InvoiceDTO;
import com.sapienter.jbilling.server.order.db.OrderProcessDTO;
import com.sapienter.jbilling.server.process.BillingProcessBL;
import com.sapienter.jbilling.server.user.db.CompanyDTO;
import com.sapienter.jbilling.server.util.Constants;

import javax.persistence.OneToOne;

import org.hibernate.annotations.*;

@Entity
@TableGenerator(name = "billing_process_GEN", 
                table = "jbilling_seqs", 
                pkColumnName = "name", 
                valueColumnName = "next_id", 
                pkColumnValue = "billing_process", 
                allocationSize = 10)
@Table(name = "billing_process")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class BillingProcessDTO implements Serializable {

    private int id;
    private PeriodUnitDTO periodUnitDTO;
    private PaperInvoiceBatchDTO paperInvoiceBatch;
    private CompanyDTO entity;
    private Date billingDate;
    private int periodValue;
    private int isReview;
    private int retriesToDo;
    private Set<OrderProcessDTO> orderProcesses = new HashSet<OrderProcessDTO>(
            0);
    private Set<InvoiceDTO> invoices = new HashSet<InvoiceDTO>(0);
    private Set<ProcessRunDTO> processRuns = new HashSet<ProcessRunDTO>(0);
    private int versionNum;
    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(BillingProcessDTO.class));

    public BillingProcessDTO() {
    }

    public BillingProcessDTO(BillingProcessWS ws) {
        this.id = ws.getId() != null ? ws.getId() : 0;
        this.billingDate = ws.getBillingDate();
        this.periodValue = ws.getPeriodValue() != null ? ws.getPeriodValue() : 0;
        this.isReview = ws.getReview() != null ? ws.getReview() : 0;
        this.retriesToDo = ws.getRetriesToDo() != null ? ws.getRetriesToDo() : 0;

        if (ws.getPeriodUnitId() != null) this.periodUnitDTO = new PeriodUnitDTO(ws.getPeriodUnitId());
        if (ws.getEntityId() != null) this.entity = new CompanyDTO(ws.getEntityId());
    }

    public BillingProcessDTO(int id, PeriodUnitDTO periodUnitDTO,
            CompanyDTO entity, Date billingDate, int periodValue, int isReview,
            int retriesToDo) {
        this.id = id;
        this.periodUnitDTO = periodUnitDTO;
        this.entity = entity;
        this.billingDate = billingDate;
        this.periodValue = periodValue;
        this.isReview = isReview;
        this.retriesToDo = retriesToDo;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "billing_process_GEN")
    @Column(name = "id", unique = true, nullable = false)
    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "period_unit_id", nullable = false)
    public PeriodUnitDTO getPeriodUnit() {
        return this.periodUnitDTO;
    }

    public void setPeriodUnit(PeriodUnitDTO periodUnitDTO) {
        this.periodUnitDTO = periodUnitDTO;
    }


    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="paper_invoice_batch_id")
    public PaperInvoiceBatchDTO getPaperInvoiceBatch() {
        return this.paperInvoiceBatch;
    }
    
    public void setPaperInvoiceBatch(PaperInvoiceBatchDTO paperInvoiceBatch) {
        this.paperInvoiceBatch = paperInvoiceBatch;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entity_id", nullable = false)
    public CompanyDTO getEntity() {
        return this.entity;
    }

    public void setEntity(CompanyDTO entity) {
        this.entity = entity;
    }

    @Column(name = "billing_date", nullable = false, length = 13)
    public Date getBillingDate() {
        return this.billingDate;
    }

    public void setBillingDate(Date billingDate) {
        this.billingDate = billingDate;
    }

    /**
     * This function looks at period unit and period value and adds the same 
     * to billing process date (or start date), subtracts 1 day 
     * to arrive at billing period end date.
     * Marking it transient as we are not storing this to db.
    */
    @Transient
    public Date getBillingPeriodEndDate() {
    	Calendar calendar = Calendar.getInstance();
    	// first add the period value in the given period units
		calendar.setTime(new BillingProcessBL().getEndOfProcessPeriod(this));
		// subtract 1 day
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		return calendar.getTime();
	}

	@Column(name = "period_value", nullable = false)
    public int getPeriodValue() {
        return this.periodValue;
    }

    public void setPeriodValue(int periodValue) {
        this.periodValue = periodValue;
    }

    @Column(name = "is_review", nullable = false)
    public int getIsReview() {
        return this.isReview;
    }

    public void setIsReview(int isReview) {
        this.isReview = isReview;
    }

    @Column(name = "retries_to_do", nullable = false)
    public int getRetriesToDo() {
        return this.retriesToDo;
    }

    public void setRetriesToDo(int retriesToDo) {
        this.retriesToDo = retriesToDo;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "billingProcess")
    public Set<OrderProcessDTO> getOrderProcesses() {
        return this.orderProcesses;
    }

    public void setOrderProcesses(Set<OrderProcessDTO> orderProcesses) {
        this.orderProcesses = orderProcesses;
    }

    // this is useful for the cascade, but any call to it will be very expensive and even
    // inaccurate. USE InvoiceDAS.findByProcess instead
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "billingProcess")
    @NotFound(action = NotFoundAction.IGNORE)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    public Set<InvoiceDTO> getInvoices() {
        return this.invoices;
    }

    public void setInvoices(Set<InvoiceDTO> invoices) {
        this.invoices = invoices;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "billingProcess")
    @OrderBy( clause = "id")
    public Set<ProcessRunDTO> getProcessRuns() {
        return this.processRuns;
    }

    public void setProcessRuns(Set<ProcessRunDTO> processRuns) {
        this.processRuns = processRuns;
    }

    @Version
    @Column(name = "OPTLOCK")
    public int getVersionNum() {
        return versionNum;
    }

    public void setVersionNum(int versionNum) {
        this.versionNum = versionNum;
    }

    @Override
    public String toString() {
        StringBuffer ret = new StringBuffer("BillingProcessDTO: id: " + id + " periodUint " + periodUnitDTO + " paperInvoiceBatch "
                + paperInvoiceBatch  + " entity " + entity + " billingDate " + billingDate
                + " periodValue " + periodValue + " isReview " + isReview + " retriesToDo " + retriesToDo);
        ret.append(" orderProcesses (count) " + orderProcesses.size());
        ret.append(" invoices (count) " + invoices.size());//note, cached association
        ret.append(" processRuns ");
        for (ProcessRunDTO run: processRuns) {
            ret.append(run.toString());
        }
        return ret.toString();
    }
}
