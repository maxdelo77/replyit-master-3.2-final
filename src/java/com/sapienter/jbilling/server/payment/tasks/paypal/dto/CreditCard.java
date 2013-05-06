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
package com.sapienter.jbilling.server.payment.tasks.paypal.dto;

/**
 * Created by Roman Liberov
 */

public class CreditCard {
    private final String type;
    private final String account;
    private final String expirationDate;
    private final String cvv2;

    public CreditCard(String type, String account, String expirationDate, String cvv2) {
        this.type = type;
        this.account = account;
        this.expirationDate = expirationDate;
        this.cvv2 = cvv2;
    }

    public String getType() {
        return type;
    }

    public String getAccount() {
        return account;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public String getCvv2() {
        return cvv2;
    }
}
