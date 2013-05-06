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


import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.sapienter.jbilling.server.invoice.db.InvoiceDTO;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.OneToOne;
import javax.persistence.TableGenerator;
import javax.persistence.Version;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@TableGenerator(name = "paper_invoice_batch_GEN", 
                table = "jbilling_seqs", 
                pkColumnName = "name", 
                valueColumnName = "next_id", 
                pkColumnValue = "paper_invoice_batch", 
                allocationSize = 100)
@Table(name="paper_invoice_batch")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class PaperInvoiceBatchDTO implements java.io.Serializable {

    private int id;
    private int totalInvoices;
    private Date deliveryDate;
    private int isSelfManaged;
    private BillingProcessDTO billingProcesses = null;
    private Set<InvoiceDTO> invoices = new HashSet<InvoiceDTO>(0);
    private int versionNum;

    public PaperInvoiceBatchDTO() {
    }

    public PaperInvoiceBatchDTO(int id, int totalInvoices, int isSelfManaged) {
        this.id = id;
        this.totalInvoices = totalInvoices;
        this.isSelfManaged = isSelfManaged;
    }

    public PaperInvoiceBatchDTO(int id, int totalInvoices, Date deliveryDate, int isSelfManaged, BillingProcessDTO billingProcesses, Set<InvoiceDTO> invoices) {
        this.id = id;
        this.totalInvoices = totalInvoices;
        this.deliveryDate = deliveryDate;
        this.isSelfManaged = isSelfManaged;
        this.billingProcesses = billingProcesses;
        this.invoices = invoices;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "paper_invoice_batch_GEN")
    @Column(name = "id", unique = true, nullable = false)
    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "total_invoices", nullable = false)
    public int getTotalInvoices() {
        return this.totalInvoices;
    }

    public void setTotalInvoices(int totalInvoices) {
        this.totalInvoices = totalInvoices;
    }

    @Column(name = "delivery_date", length = 13)
    public Date getDeliveryDate() {
        return this.deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    @Column(name = "is_self_managed", nullable = false)
    public int getIsSelfManaged() {
        return this.isSelfManaged;
    }

    public void setIsSelfManaged(int isSelfManaged) {
        this.isSelfManaged = isSelfManaged;
    }
    
    @OneToOne(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="paperInvoiceBatch")
    public BillingProcessDTO getProcess() {
        return this.billingProcesses;
    }
    
    public void setProcess(BillingProcessDTO billingProcesses) {
        this.billingProcesses = billingProcesses;
    }
    
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "paperInvoiceBatch")
    public Set<InvoiceDTO> getInvoices() {
        return this.invoices;
    }

    public void setInvoices(Set<InvoiceDTO> invoices) {
        this.invoices = invoices;
    }
    
    @Version
    @Column(name = "OPTLOCK")
    public int getVersionNum() {
        return versionNum;
    }

    public void setVersionNum(int versionNum) {
        this.versionNum = versionNum;
    }
}


