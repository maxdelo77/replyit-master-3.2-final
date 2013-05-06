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

package com.sapienter.jbilling.server.user;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Result object for validatePurchase API method.
 */
public class ValidatePurchaseWS implements Serializable {

    private Boolean success = true;
    private Boolean authorized = true;
    private String quantity = "0.0";
    private List<String> message = new ArrayList<String>();

    public ValidatePurchaseWS() {
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Boolean getAuthorized() {
        return authorized;
    }

    public void setAuthorized(Boolean authorized) {
        this.authorized = authorized;
    }

    public String getQuantity() {
        return quantity;
    }

    public BigDecimal getQuantityAsDecimal() {
        return quantity == null ? null : new BigDecimal(quantity);
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public void setQuantity(Double quantity) {
        this.setQuantity(new BigDecimal(quantity));
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = (quantity != null ? quantity.toString() : null);
    }

    public String[] getMessage() {
        return message.toArray(new String[message.size()]);
    }

    public void setMessage(String[] message) {
        this.message = Arrays.asList(message);
    }

    public void addMessage(String message) {
        this.message.add(message);
    }

    @Override
    public String toString() {
        return "ValidatePurchaseWS{" +
                "success=" + success +
                ", authorized=" + authorized +
                ", quantity='" + quantity + '\'' +
                '}';
    }
}
