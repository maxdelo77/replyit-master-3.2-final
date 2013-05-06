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
 * Created by Roman Liberov, 03/05/2010
 */
public enum CreditCardType {

    VISA("Visa"), MASTER_CARD("MasterCard"), DISCOVER("Discover"), AMEX("Amex");

    private String value;

    private CreditCardType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
