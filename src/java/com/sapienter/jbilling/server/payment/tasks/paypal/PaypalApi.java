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
package com.sapienter.jbilling.server.payment.tasks.paypal;

import com.paypal.sdk.profiles.APIProfile;
import com.paypal.sdk.profiles.ProfileFactory;
import com.paypal.sdk.exceptions.PayPalException;
import com.paypal.sdk.core.nvp.NVPEncoder;
import com.paypal.sdk.core.nvp.NVPDecoder;
import com.paypal.sdk.services.NVPCallerServices;
import com.sapienter.jbilling.server.payment.tasks.paypal.dto.*;

/**
 * Created by Roman Liberov, 03/02/2010
 */
public class PaypalApi {

    public static final String DO_DIRECT_PAYMENT_METHOD = "DoDirectPayment";
    public static final String DO_CAPTURE_METHOD = "DoCapture";
    public static final String DO_REFERENCE_TRANSACTION_METHOD = "DoReferenceTransaction";
    public static final String REFUND_TRANSACTION_METHOD = "RefundTransaction";
    public static final String DO_VOID_METHOD = "DoVoid";

    private final APIProfile profile;

    public PaypalApi(String userId, String password, String signature,
                     String environment, String subject, int timeout)
            throws PayPalException {

        profile = ProfileFactory.createSignatureAPIProfile();
        profile.setAPIUsername(userId);
        profile.setAPIPassword(password);
        profile.setSignature(signature);
        profile.setEnvironment(environment);
        profile.setSubject(subject);
        profile.setTimeout(timeout);
    }

    public PaypalResult doDirectPayment(
            PaymentAction paymentAction, Payer payer, CreditCard creditCard, Payment payment)
            throws PayPalException {

        NVPEncoder encoder = new NVPEncoder();
        NVPDecoder decoder = new NVPDecoder();

        NVPCallerServices caller = new NVPCallerServices();
        caller.setAPIProfile(profile);

        encoder.add("METHOD", DO_DIRECT_PAYMENT_METHOD);
        encoder.add("PAYMENTACTION", paymentAction.toString());

        encoder.add("CREDITCARDTYPE", creditCard.getType());
        encoder.add("ACCT", creditCard.getAccount());
        encoder.add("EXPDATE", creditCard.getExpirationDate());
        encoder.add("CVV2", creditCard.getCvv2());

        encoder.add("FIRSTNAME", payer.getFirstName());
        encoder.add("LASTNAME", payer.getLastName());
        encoder.add("STREET", payer.getStreet());
        encoder.add("CITY", payer.getCity());
        encoder.add("STATE", payer.getState());
        encoder.add("ZIP", payer.getZip());
        encoder.add("COUNTRYCODE", payer.getCountryCode());

        encoder.add("AMT", payment.getAmount());
        encoder.add("CURRENCYCODE", payment.getCurrencyCode());

        String NVPRequest = encoder.encode();
        String NVPResponse = caller.call(NVPRequest);
        decoder.decode(NVPResponse);

        PaypalResult result = new PaypalResult();

        if(decoder.get("ACK").equals("Failure")) {
            result.setSucceseeded(false);
            result.setErrorCode(decoder.get("L_ERRORCODE0"));
            return result;

        } else {
            result.setSucceseeded(true);
            result.setTransactionId(decoder.get("TRANSACTIONID"));
            result.setAvs(decoder.get("AVSCODE"));
            return result;
        }
    }

    public PaypalResult doReferenceTransaction(
            String transactionId, PaymentAction paymentAction, Payment payment)
            throws PayPalException {

        NVPEncoder encoder = new NVPEncoder();
        NVPDecoder decoder = new NVPDecoder();

        NVPCallerServices caller = new NVPCallerServices();
        caller.setAPIProfile(profile);

        encoder.add("METHOD", DO_REFERENCE_TRANSACTION_METHOD);
        encoder.add("REFERENCEID", transactionId);
        encoder.add("PAYMENTACTION", paymentAction.toString());
        encoder.add("AMT", payment.getAmount());
        encoder.add("CURRENCYCODE", payment.getCurrencyCode());

        String NVPRequest = encoder.encode();
        String NVPResponse = caller.call(NVPRequest);
        decoder.decode(NVPResponse);

        PaypalResult result = new PaypalResult();

        if(decoder.get("ACK").equals("Failure")) {
            result.setSucceseeded(false);
            result.setErrorCode(decoder.get("L_ERRORCODE0"));
            return result;

        } else {
            result.setSucceseeded(true);
            result.setTransactionId(decoder.get("TRANSACTIONID"));
            return result;
        }
    }

    public PaypalResult doCapture(String authorizationId, Payment payment, CompleteType completeType)
            throws PayPalException {

        NVPEncoder encoder = new NVPEncoder();
        NVPDecoder decoder = new NVPDecoder();

        NVPCallerServices caller = new NVPCallerServices();
        caller.setAPIProfile(profile);

        encoder.add("METHOD", DO_CAPTURE_METHOD);
        encoder.add("AUTHORIZATIONID", authorizationId);
        encoder.add("COMPLETETYPE", completeType.toString());
        encoder.add("AMT", payment.getAmount());
        encoder.add("CURRENCYCODE", payment.getCurrencyCode());

        String NVPRequest = encoder.encode();
        String NVPResponse = caller.call(NVPRequest);
        decoder.decode(NVPResponse);

        PaypalResult result = new PaypalResult();

        if(decoder.get("ACK").equals("Failure")) {
            result.setSucceseeded(false);
            result.setErrorCode(decoder.get("L_ERRORCODE0"));
            return result;

        } else {
            result.setSucceseeded(true);
            result.setTransactionId(decoder.get("AUTHORIZATIONID"));
            return result;
        }
    }

    public PaypalResult doVoid(String transactionId)
            throws PayPalException {

        NVPEncoder encoder = new NVPEncoder();
        NVPDecoder decoder = new NVPDecoder();

        NVPCallerServices caller = new NVPCallerServices();
        caller.setAPIProfile(profile);

        encoder.add("METHOD", DO_VOID_METHOD);
        encoder.add("AUTHORIZATIONID", transactionId);

        String NVPRequest = encoder.encode();
        String NVPResponse = caller.call(NVPRequest);
        decoder.decode(NVPResponse);

        PaypalResult result = new PaypalResult();

        if(decoder.get("ACK").equals("Failure")) {
            result.setSucceseeded(false);
            result.setErrorCode(decoder.get("L_ERRORCODE0"));
            return result;

        } else {
            result.setSucceseeded(true);
            result.setTransactionId(decoder.get("AUTHORIZATIONID"));
            return result;
        }
    }

    public PaypalResult refundTransaction(String transactionId, String amount, RefundType refundType)
            throws PayPalException {

        NVPEncoder encoder = new NVPEncoder();
        NVPDecoder decoder = new NVPDecoder();

        NVPCallerServices caller = new NVPCallerServices();
        caller.setAPIProfile(profile);

        encoder.add("METHOD", REFUND_TRANSACTION_METHOD);
        encoder.add("TRANSACTIONID", transactionId);
        encoder.add("REFUNDTYPE", refundType.toString());
        if(!RefundType.FULL.equals(refundType)) {
            encoder.add("AMT", amount);
        }

        String NVPRequest = encoder.encode();
        String NVPResponse = caller.call(NVPRequest);
        decoder.decode(NVPResponse);

        PaypalResult result = new PaypalResult();

        if(decoder.get("ACK").equals("Failure")) {
            result.setSucceseeded(false);
            result.setErrorCode(decoder.get("L_ERRORCODE0"));
            return result;

        } else {
            result.setSucceseeded(true);
            result.setTransactionId(decoder.get("REFUNDTRANSACTIONID"));
            return result;
        }
    }
}
