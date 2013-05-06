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

package com.sapienter.jbilling.server.order;

import com.sapienter.jbilling.server.order.db.OrderProcessDTO;

import java.io.Serializable;
import java.util.Date;

/**
 * OrderProcessWS
 *
 * @author Brian Cowdery
 * @since 25-10-2010
 */
public class OrderProcessWS implements Serializable {

    private Integer id;
    private Integer billingProcessId;
    private Integer orderId;
    private Integer invoiceId;
    private Integer periodsIncluded;
    private Date periodStart;
    private Date periodEnd;
    private Integer isReview;
    private Integer origin;

    public OrderProcessWS() {
    }

    public OrderProcessWS(OrderProcessDTO dto) {
        this.id = dto.getId();
        this.billingProcessId = dto.getBillingProcess() != null ? dto.getBillingProcess().getId() : null;
        this.orderId = dto.getPurchaseOrder() != null ? dto.getPurchaseOrder().getId() : null;
        this.invoiceId = dto.getInvoice() != null ? dto.getInvoice().getId() : null;
        this.periodsIncluded = dto.getPeriodsIncluded();
        this.periodStart = dto.getPeriodStart();
        this.periodEnd = dto.getPeriodEnd();
        this.isReview = dto.getIsReview();
        this.origin = dto.getOrigin();
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

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Integer invoiceId) {
        this.invoiceId = invoiceId;
    }

    public Integer getPeriodsIncluded() {
        return periodsIncluded;
    }

    public void setPeriodsIncluded(Integer periodsIncluded) {
        this.periodsIncluded = periodsIncluded;
    }

    public Date getPeriodStart() {
        return periodStart;
    }

    public void setPeriodStart(Date periodStart) {
        this.periodStart = periodStart;
    }

    public Date getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(Date periodEnd) {
        this.periodEnd = periodEnd;
    }

    public Integer getReview() {
        return isReview;
    }

    public void setReview(Integer review) {
        isReview = review;
    }

    public Integer getOrigin() {
        return origin;
    }

    public void setOrigin(Integer origin) {
        this.origin = origin;
    }

    @Override
    public String toString() {
        return "OrderProcessWS{"
               + "id=" + id
               + ", orderId=" + orderId
               + ", invoiceId=" + invoiceId
               + ", periodsIncluded=" + periodsIncluded
               + ", periodStart=" + periodStart
               + ", periodEnd=" + periodEnd
               + ", isReview=" + isReview
               + ", origin=" + origin
               + '}';
    }
}
