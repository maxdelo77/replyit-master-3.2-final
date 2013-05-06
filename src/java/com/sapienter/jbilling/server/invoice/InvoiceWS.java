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

package com.sapienter.jbilling.server.invoice;

import com.sapienter.jbilling.server.entity.InvoiceLineDTO;
import com.sapienter.jbilling.server.metafields.MetaFieldValueWS;
import com.sapienter.jbilling.server.security.WSSecured;

import javax.validation.Valid;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;


/**
 * @author Emil
 */
public class InvoiceWS implements WSSecured, Serializable {

    private Integer delegatedInvoiceId = null;
    private Integer payments[] = null;
    private Integer userId = null;
    private InvoiceLineDTO invoiceLines[] = null;
    private Integer orders[] = null;

    // original DTO
    private Integer id;
    private Date createDateTime;
    private Date createTimeStamp;
    private Date lastReminder;
    private Date dueDate;
    private String total;
    private Integer toProcess;
    private Integer statusId;
    private String balance;
    private String carriedBalance;
    private Integer inProcessPayment;
    private Integer deleted;
    private Integer paymentAttempts;
    private Integer isReview;
    private Integer currencyId;
    private String customerNotes;
    private String number;
    private Integer overdueStep;
    @Valid
    private MetaFieldValueWS[] metaFields;

    //additional fields for the new gui
    private String statusDescr;
    
    public InvoiceWS() {
        super();
    }
  
    public String getStatusDescr() {
		return statusDescr;
	}

	public void setStatusDescr(String statusDescr) {
		this.statusDescr = statusDescr;
	}

	public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getCreateDateTime() {
        return this.createDateTime;
    }

    public void setCreateDateTime(Date createDateTime) {
        this.createDateTime = createDateTime;
    }

    public Date getCreateTimeStamp() {
        return this.createTimeStamp;
    }

    public void setCreateTimeStamp(Date createTimeStamp) {
        this.createTimeStamp = createTimeStamp;
    }

    public Date getLastReminder() {
        return this.lastReminder;
    }

    public void setLastReminder(Date lastReminder) {
        this.lastReminder = lastReminder;
    }

    public Date getDueDate() {
        return this.dueDate;
    }

    public void setDueDate(java.util.Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getTotal() {
        return this.total;
    }

    public BigDecimal getTotalAsDecimal() {
        return total == null ? null : new BigDecimal(total);
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public void setTotal(BigDecimal total) {
        this.total = (total != null ? total.toString() : null);
    }

    public Integer getToProcess() {
        return this.toProcess;
    }

    public void setToProcess(Integer toProcess) {
        this.toProcess = toProcess;
    }

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }

    public String getBalance() {
        return this.balance;
    }

    public BigDecimal getBalanceAsDecimal() {
        return balance == null ? null : new BigDecimal(balance);
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = (balance != null ? balance.toString() : null);
    }

    public String getCarriedBalance() {
        return this.carriedBalance;
    }

    public BigDecimal getCarriedBalanceAsDecimal() {
        return carriedBalance == null ? null : new BigDecimal(carriedBalance);
    }

    public void setCarriedBalance(String carriedBalance) {
        this.carriedBalance = carriedBalance;
    }

    public void setCarriedBalance(BigDecimal carriedBalance) {
        this.carriedBalance = (carriedBalance != null ? carriedBalance.toString() : null);
    }

    public Integer getInProcessPayment() {
        return this.inProcessPayment;
    }

    public void setInProcessPayment(Integer inProcessPayment) {
        this.inProcessPayment = inProcessPayment;
    }

    public Integer getDeleted() {
        return this.deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    public Integer getPaymentAttempts() {
        return this.paymentAttempts;
    }

    public void setPaymentAttempts(Integer paymentAttempts) {
        this.paymentAttempts = paymentAttempts;
    }

    public Integer getIsReview() {
        return this.isReview;
    }

    public void setIsReview(Integer isReview) {
        this.isReview = isReview;
    }

    public Integer getCurrencyId() {
        return this.currencyId;
    }

    public void setCurrencyId(Integer currencyId) {
        this.currencyId = currencyId;
    }

    public String getCustomerNotes() {
        return this.customerNotes;
    }

    public void setCustomerNotes(String customerNotes) {
        this.customerNotes = customerNotes;
    }

    public String getNumber() {
        return this.number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Integer getOverdueStep() {
        return this.overdueStep;
    }

    public void setOverdueStep(Integer overdueStep) {
        this.overdueStep = overdueStep;
    }

    public Integer getUserId() {
        return this.userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getDelegatedInvoiceId() {
        return delegatedInvoiceId;
    }

    public void setDelegatedInvoiceId(Integer delegatedInvoiceId) {
        this.delegatedInvoiceId = delegatedInvoiceId;
    }

    public InvoiceLineDTO[] getInvoiceLines() {
        return invoiceLines;
    }

    public void setInvoiceLines(InvoiceLineDTO[] invoiceLines) {
        this.invoiceLines = invoiceLines;
    }

    public Integer[] getOrders() {
        return orders;
    }

    public void setOrders(Integer[] orders) {
        this.orders = orders;
    }

    public Integer[] getPayments() {
        return payments;
    }

    public void setPayments(Integer[] payments) {
        this.payments = payments;
    }

    public MetaFieldValueWS[] getMetaFields() {
        return metaFields;
    }

    public void setMetaFields(MetaFieldValueWS[] metaFields) {
        this.metaFields = metaFields;
    }

    /**
     * Unsupported, web-service security enforced using {@link #getOwningUserId()}
     * @return null
     */
    public Integer getOwningEntityId() {
        return null;
    }

    public Integer getOwningUserId() {
        return getUserId();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("InvoiceWS [balance=");
        builder.append(balance);
        builder.append(", carriedBalance=");
        builder.append(carriedBalance);
        builder.append(", createDateTime=");
        builder.append(createDateTime);
        builder.append(", createTimeStamp=");
        builder.append(createTimeStamp);
        builder.append(", currencyId=");
        builder.append(currencyId);
        builder.append(", customerNotes=");
        builder.append(customerNotes);
        builder.append(", delegatedInvoiceId=");
        builder.append(delegatedInvoiceId);
        builder.append(", deleted=");
        builder.append(deleted);
        builder.append(", dueDate=");
        builder.append(dueDate);
        builder.append(", id=");
        builder.append(id);
        builder.append(", inProcessPayment=");
        builder.append(inProcessPayment);
        builder.append(", invoiceLines=");
        builder.append(Arrays.toString(invoiceLines));
        builder.append(", isReview=");
        builder.append(isReview);
        builder.append(", lastReminder=");
        builder.append(lastReminder);
        builder.append(", number=");
        builder.append(number);
        builder.append(", orders=");
        builder.append(Arrays.toString(orders));
        builder.append(", overdueStep=");
        builder.append(overdueStep);
        builder.append(", paymentAttempts=");
        builder.append(paymentAttempts);
        builder.append(", payments=");
        builder.append(Arrays.toString(payments));
        builder.append(", statusDescr=");
        builder.append(statusDescr);
        builder.append(", statusId=");
        builder.append(statusId);
        builder.append(", toProcess=");
        builder.append(toProcess);
        builder.append(", total=");
        builder.append(total);
        builder.append(", userId=");
        builder.append(userId);
        builder.append(", metaField=");
        builder.append(Arrays.toString(metaFields));
        builder.append(']');
        return builder.toString();
    }

    
}
