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
package com.sapienter.jbilling.server.order.db;


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
import javax.persistence.Version;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.sapienter.jbilling.server.invoice.db.InvoiceDTO;
import com.sapienter.jbilling.server.process.db.BillingProcessDTO;

@Entity
@TableGenerator(
        name="order_process_GEN",
        table="jbilling_seqs",
        pkColumnName = "name",
        valueColumnName = "next_id",
        pkColumnValue="order_process",
        allocationSize = 100
        )
@Table(name="order_process")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class OrderProcessDTO  implements java.io.Serializable {


     private int id;
     private BillingProcessDTO billingProcessDTO;
     private OrderDTO orderDTO;
     private InvoiceDTO invoiceDTO;
     private Integer periodsIncluded;
     private Date periodStart;
     private Date periodEnd;
     private int isReview;
     private Integer origin;
     private Integer versionNum;


    public OrderProcessDTO() {
    }

    
    public OrderProcessDTO(int id, int isReview) {
        this.id = id;
        this.isReview = isReview;
    }
    public OrderProcessDTO(int id, BillingProcessDTO billingProcessDTO, OrderDTO orderDTO, InvoiceDTO invoice, 
            Integer periodsIncluded, Date periodStart, Date periodEnd, int isReview, Integer origin) {
       this.id = id;
       this.billingProcessDTO = billingProcessDTO;
       this.orderDTO = orderDTO;
       this.invoiceDTO = invoice;
       this.periodsIncluded = periodsIncluded;
       this.periodStart = periodStart;
       this.periodEnd = periodEnd;
       this.isReview = isReview;
       this.origin = origin;
    }
   
    @Id @GeneratedValue(strategy=GenerationType.TABLE, generator="order_process_GEN")
    @Column(name="id", unique=true, nullable=false)
    public int getId() {
        return this.id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="billing_process_id")
    public BillingProcessDTO getBillingProcess() {
        return this.billingProcessDTO;
    }
    
    public void setBillingProcess(BillingProcessDTO billingProcessDTO) {
        this.billingProcessDTO = billingProcessDTO;
    }
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="order_id")
    public OrderDTO getPurchaseOrder() {
        return this.orderDTO;
    }
    
    public void setPurchaseOrder(OrderDTO orderDTO) {
        this.orderDTO = orderDTO;
    }
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="invoice_id")
    public InvoiceDTO getInvoice() {
        return this.invoiceDTO;
    }
    
    public void setInvoice(InvoiceDTO invoice) {
        this.invoiceDTO = invoice;
    }
    
    @Column(name="periods_included")
    public Integer getPeriodsIncluded() {
        return this.periodsIncluded;
    }
    
    public void setPeriodsIncluded(Integer periodsIncluded) {
        this.periodsIncluded = periodsIncluded;
    }
    
    @Column(name="period_start", length=13)
    public Date getPeriodStart() {
        return this.periodStart;
    }
    
    public void setPeriodStart(Date periodStart) {
        this.periodStart = periodStart;
    }
    
    @Column(name="period_end", length=13)
    public Date getPeriodEnd() {
        return this.periodEnd;
    }
    
    public void setPeriodEnd(Date periodEnd) {
        this.periodEnd = periodEnd;
    }
    
    @Column(name="is_review", nullable=false)
    public int getIsReview() {
        return this.isReview;
    }
    
    public void setIsReview(int isReview) {
        this.isReview = isReview;
    }
    
    @Column(name="origin")
    public Integer getOrigin() {
        return this.origin;
    }
    
    public void setOrigin(Integer origin) {
        this.origin = origin;
    }

    @Version
    @Column(name="OPTLOCK")
    public Integer getVersionNum() {
        return versionNum;
    }
    protected void setVersionNum(Integer versionNum) {
        this.versionNum = versionNum;
    }
    
}


