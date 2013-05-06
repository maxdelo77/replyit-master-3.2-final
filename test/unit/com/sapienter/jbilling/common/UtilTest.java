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

package com.sapienter.jbilling.common;

import junit.framework.TestCase;

/**
 * @author Alexander Aksenov
 * @since 26.02.12
 */
public class UtilTest extends TestCase {

    public void testGetPaymentMethod() {
        String ccNumber = "340000000000001";
        assertEquals("Incorrect payment type, AMEX expected",
                Constants.PAYMENT_METHOD_AMEX, Util.getPaymentMethod(ccNumber)
        );
        ccNumber = "370000000000001";
        assertEquals("Incorrect payment type, AMEX expected",
                Constants.PAYMENT_METHOD_AMEX, Util.getPaymentMethod(ccNumber)
        );

        ccNumber = "30000000000001";
        assertEquals("Incorrect payment type, Diners Club Carte Blanche expected",
                Constants.PAYMENT_METHOD_DINERS, Util.getPaymentMethod(ccNumber)
        );
        ccNumber = "30100000000001";
        assertEquals("Incorrect payment type, Diners Club Carte Blanche expected",
                Constants.PAYMENT_METHOD_DINERS, Util.getPaymentMethod(ccNumber)
        );
        ccNumber = "30500000000001";
        assertEquals("Incorrect payment type, Diners Club Carte Blanche expected",
                Constants.PAYMENT_METHOD_DINERS, Util.getPaymentMethod(ccNumber)
        );

        ccNumber = "36500000000001";
        assertEquals("Incorrect payment type, Diners Club International expected",
                Constants.PAYMENT_METHOD_DINERS, Util.getPaymentMethod(ccNumber)
        );

        ccNumber = "5450000000000111";
        assertEquals("Incorrect payment type, MASTERCARD expected",
                Constants.PAYMENT_METHOD_MASTERCARD, Util.getPaymentMethod(ccNumber)
        );
        ccNumber = "5550000000000111";
        assertEquals("Incorrect payment type, MASTERCARD expected",
                Constants.PAYMENT_METHOD_MASTERCARD, Util.getPaymentMethod(ccNumber)
        );

        ccNumber = "6011000000000111";
        assertEquals("Incorrect payment type, Discover Card expected",
                Constants.PAYMENT_METHOD_DISCOVERY, Util.getPaymentMethod(ccNumber)
        );
        ccNumber = "6221260000000111";
        assertEquals("Incorrect payment type, Discover Card expected",
                Constants.PAYMENT_METHOD_DISCOVERY, Util.getPaymentMethod(ccNumber)
        );
        ccNumber = "6229250000000111";
        assertEquals("Incorrect payment type, Discover Card expected",
                Constants.PAYMENT_METHOD_DISCOVERY, Util.getPaymentMethod(ccNumber)
        );
        ccNumber = "6228150000000111";
        assertEquals("Incorrect payment type, Discover Card expected",
                Constants.PAYMENT_METHOD_DISCOVERY, Util.getPaymentMethod(ccNumber)
        );
        ccNumber = "6440000000000111";
        assertEquals("Incorrect payment type, Discover Card expected",
                Constants.PAYMENT_METHOD_DISCOVERY, Util.getPaymentMethod(ccNumber)
        );
        ccNumber = "6470000000000111";
        assertEquals("Incorrect payment type, Discover Card expected",
                Constants.PAYMENT_METHOD_DISCOVERY, Util.getPaymentMethod(ccNumber)
        );
        ccNumber = "6490000000000111";
        assertEquals("Incorrect payment type, Discover Card expected",
                Constants.PAYMENT_METHOD_DISCOVERY, Util.getPaymentMethod(ccNumber)
        );
        ccNumber = "6510000000000111";
        assertEquals("Incorrect payment type, Discover Card expected",
                Constants.PAYMENT_METHOD_DISCOVERY, Util.getPaymentMethod(ccNumber)
        );

        ccNumber = "6370000000000111";
        assertEquals("Incorrect payment type, InstaPayment expected",
                Constants.PAYMENT_METHOD_INSTAL_PAYMENT, Util.getPaymentMethod(ccNumber)
        );
        ccNumber = "6380000000000111";
        assertEquals("Incorrect payment type, InstaPayment expected",
                Constants.PAYMENT_METHOD_INSTAL_PAYMENT, Util.getPaymentMethod(ccNumber)
        );
        ccNumber = "6390000000000111";
        assertEquals("Incorrect payment type, InstaPayment expected",
                Constants.PAYMENT_METHOD_INSTAL_PAYMENT, Util.getPaymentMethod(ccNumber)
        );

        ccNumber = "3528000000000111";
        assertEquals("Incorrect payment type, JCB expected",
                Constants.PAYMENT_METHOD_JCB, Util.getPaymentMethod(ccNumber)
        );
        ccNumber = "3589000000000111";
        assertEquals("Incorrect payment type, JCB expected",
                Constants.PAYMENT_METHOD_JCB, Util.getPaymentMethod(ccNumber)
        );
        ccNumber = "3569000000000111";
        assertEquals("Incorrect payment type, JCB expected",
                Constants.PAYMENT_METHOD_JCB, Util.getPaymentMethod(ccNumber)
        );

        ccNumber = "6304000000000111";
        assertEquals("Incorrect payment type, MAESTRO (=LASER) expected",
                Constants.PAYMENT_METHOD_MAESTRO, Util.getPaymentMethod(ccNumber)
        );
        ccNumber = "6706000000000111";
        assertEquals("Incorrect payment type, LASER expected",
                Constants.PAYMENT_METHOD_LASER, Util.getPaymentMethod(ccNumber)
        );
        ccNumber = "6771000000000111";
        assertEquals("Incorrect payment type, LASER expected",
                Constants.PAYMENT_METHOD_LASER, Util.getPaymentMethod(ccNumber)
        );
        ccNumber = "6709000000000111";
        assertEquals("Incorrect payment type, LASER expected",
                Constants.PAYMENT_METHOD_LASER, Util.getPaymentMethod(ccNumber)
        );

        ccNumber = "5018000000000111";
        assertEquals("Incorrect payment type, MAESTRO expected",
                Constants.PAYMENT_METHOD_MAESTRO, Util.getPaymentMethod(ccNumber)
        );
        ccNumber = "5020000000000111";
        assertEquals("Incorrect payment type, MAESTRO expected",
                Constants.PAYMENT_METHOD_MAESTRO, Util.getPaymentMethod(ccNumber)
        );
        ccNumber = "5038000000000111";
        assertEquals("Incorrect payment type, MAESTRO expected",
                Constants.PAYMENT_METHOD_MAESTRO, Util.getPaymentMethod(ccNumber)
        );
        ccNumber = "6304000000000111";
        assertEquals("Incorrect payment type, MAESTRO expected",
                Constants.PAYMENT_METHOD_MAESTRO, Util.getPaymentMethod(ccNumber)
        );
        ccNumber = "6759000000000111";
        assertEquals("Incorrect payment type, MAESTRO expected",
                Constants.PAYMENT_METHOD_MAESTRO, Util.getPaymentMethod(ccNumber)
        );
        ccNumber = "6761000000000111";
        assertEquals("Incorrect payment type, MAESTRO expected",
                Constants.PAYMENT_METHOD_MAESTRO, Util.getPaymentMethod(ccNumber)
        );
        ccNumber = "6762000000000111";
        assertEquals("Incorrect payment type, MAESTRO expected",
                Constants.PAYMENT_METHOD_MAESTRO, Util.getPaymentMethod(ccNumber)
        );
        ccNumber = "6763000000000111";
        assertEquals("Incorrect payment type, MAESTRO expected",
                Constants.PAYMENT_METHOD_MAESTRO, Util.getPaymentMethod(ccNumber)
        );

        ccNumber = "5100000000000111";
        assertEquals("Incorrect payment type, MasterCard expected",
                Constants.PAYMENT_METHOD_MASTERCARD, Util.getPaymentMethod(ccNumber)
        );
        ccNumber = "5200000000000111";
        assertEquals("Incorrect payment type, MasterCard expected",
                Constants.PAYMENT_METHOD_MASTERCARD, Util.getPaymentMethod(ccNumber)
        );
        ccNumber = "5300000000000111";
        assertEquals("Incorrect payment type, MasterCard expected",
                Constants.PAYMENT_METHOD_MASTERCARD, Util.getPaymentMethod(ccNumber)
        );
        ccNumber = "5400000000000111";
        assertEquals("Incorrect payment type, MasterCard expected",
                Constants.PAYMENT_METHOD_MASTERCARD, Util.getPaymentMethod(ccNumber)
        );
        ccNumber = "5500000000000111";
        assertEquals("Incorrect payment type, MasterCard expected",
                Constants.PAYMENT_METHOD_MASTERCARD, Util.getPaymentMethod(ccNumber)
        );

        ccNumber = "4200000000000111";
        assertEquals("Incorrect payment type, VISA expected",
                Constants.PAYMENT_METHOD_VISA, Util.getPaymentMethod(ccNumber)
        );

        ccNumber = "4026000000000111";
        assertEquals("Incorrect payment type, VISA ELECTRON expected",
                Constants.PAYMENT_METHOD_VISA_ELECTRON, Util.getPaymentMethod(ccNumber)
        );
        ccNumber = "4175000000000111";
        assertEquals("Incorrect payment type, VISA ELECTRON expected",
                Constants.PAYMENT_METHOD_VISA_ELECTRON, Util.getPaymentMethod(ccNumber)
        );
        ccNumber = "4508000000000111";
        assertEquals("Incorrect payment type, VISA ELECTRON expected",
                Constants.PAYMENT_METHOD_VISA_ELECTRON, Util.getPaymentMethod(ccNumber)
        );
        ccNumber = "4844000000000111";
        assertEquals("Incorrect payment type, VISA ELECTRON expected",
                Constants.PAYMENT_METHOD_VISA_ELECTRON, Util.getPaymentMethod(ccNumber)
        );
        ccNumber = "4913000000000111";
        assertEquals("Incorrect payment type, VISA ELECTRON expected",
                Constants.PAYMENT_METHOD_VISA_ELECTRON, Util.getPaymentMethod(ccNumber)
        );
        ccNumber = "4917000000000111";
        assertEquals("Incorrect payment type, VISA ELECTRON expected",
                Constants.PAYMENT_METHOD_VISA_ELECTRON, Util.getPaymentMethod(ccNumber)
        );

        ccNumber = "************0111";
        assertEquals("Incorrect payment type, GatewayKey expected",
                Constants.PAYMENT_METHOD_GATEWAY_KEY, Util.getPaymentMethod(ccNumber)
        );
    }
}
