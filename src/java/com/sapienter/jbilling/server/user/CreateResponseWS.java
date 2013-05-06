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

/*
 * Created on Feb 4, 2005
 *
 */
package com.sapienter.jbilling.server.user;

import java.io.Serializable;

import com.sapienter.jbilling.server.payment.PaymentAuthorizationDTOEx;

/**
 * @author Emil
 */
public class CreateResponseWS implements Serializable {
    private Integer userId = null;
    private Integer orderId = null;
    private Integer invoiceId = null;
    private Integer paymentId = null;
    private PaymentAuthorizationDTOEx paymentResult = null;
    
    public Integer getInvoiceId() {
        return invoiceId;
    }
    public void setInvoiceId(Integer invoiceId) {
        this.invoiceId = invoiceId;
    }
    public Integer getOrderId() {
        return orderId;
    }
    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }
    public Integer getPaymentId() {
        return paymentId;
    }
    public void setPaymentId(Integer paymentId) {
        this.paymentId = paymentId;
    }
    public PaymentAuthorizationDTOEx getPaymentResult() {
        return paymentResult;
    }
    public void setPaymentResult(PaymentAuthorizationDTOEx paymentResult) {
        this.paymentResult = paymentResult;
    }
    public Integer getUserId() {
        return userId;
    }
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    
    public String toString() {
        return "user id = " + userId + " invoice id = " + invoiceId +
                " order id = " + orderId + " paymentId = " + paymentId +
                " payment result = " + paymentResult;
    }
}
