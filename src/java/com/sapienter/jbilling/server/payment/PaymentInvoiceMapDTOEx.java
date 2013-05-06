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

package com.sapienter.jbilling.server.payment;

import java.math.BigDecimal;
import java.util.Date;

import com.sapienter.jbilling.server.payment.db.PaymentInvoiceMapDTO;



public class PaymentInvoiceMapDTOEx extends PaymentInvoiceMapDTO {
    private Integer paymentId;
    private Integer invoiceId;
    private Integer currencyId;

    public PaymentInvoiceMapDTOEx(Integer id, BigDecimal amount, Date create) {
        super(id, amount, create);
    }
    public Integer getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Integer paymentId) {
        this.paymentId = paymentId;
    }

    public int getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Integer invoiceId) {
        this.invoiceId = invoiceId;
    }
    public Integer getCurrencyId() {
        return currencyId;
    }
    public void setCurrencyId(Integer currencyId) {
        this.currencyId = currencyId;
    }
    
    public String toString() {
        return "id = " + getId() +
                " paymentId=" + paymentId + 
                " invoiceId=" + invoiceId +
                " currencyId=" + currencyId +
                " amount=" + getAmount() +
                " date=" + getCreateDatetime();
    }
}
