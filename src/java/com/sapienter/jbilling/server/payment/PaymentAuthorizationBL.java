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
import java.util.Collection;

import org.apache.log4j.Logger;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.common.SessionInternalError;
import com.sapienter.jbilling.common.Util;
import com.sapienter.jbilling.server.payment.db.PaymentAuthorizationDAS;
import com.sapienter.jbilling.server.payment.db.PaymentAuthorizationDTO;
import com.sapienter.jbilling.server.payment.db.PaymentDAS;
import com.sapienter.jbilling.server.payment.db.PaymentDTO;

public class PaymentAuthorizationBL {
    private PaymentAuthorizationDAS paymentAuthorizationDas = null;
    private PaymentAuthorizationDTO paymentAuthorization = null;
    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(PaymentAuthorizationBL.class)); 

    public PaymentAuthorizationBL(Integer paymentAuthorizationId) {
        init();
        set(paymentAuthorizationId);
    }
    
    public PaymentAuthorizationBL(PaymentAuthorizationDTO entity) {
        init();
        paymentAuthorization = entity;
    }

    public PaymentAuthorizationBL() {
        init();
    }

    private void init() {
        paymentAuthorizationDas = new PaymentAuthorizationDAS();
    }

    public PaymentAuthorizationDTO getEntity() {
        return paymentAuthorization;
    }
    
    public void set(Integer id) {
        paymentAuthorization = paymentAuthorizationDas.find(id);
    }
    
    public void create(PaymentAuthorizationDTO dto, Integer paymentId) {
        // create the record, there's no need for an event to be logged 
        // since the timestamp and the user are already in the paymentAuthorization row
        paymentAuthorization = paymentAuthorizationDas.create(
                dto.getProcessor(), dto.getCode1());
            
        paymentAuthorization.setApprovalCode(dto.getApprovalCode());
        paymentAuthorization.setAvs(dto.getAvs());
        paymentAuthorization.setCardCode(dto.getCardCode());
        paymentAuthorization.setCode2(dto.getCode2());
        paymentAuthorization.setCode3(dto.getCode3());
        paymentAuthorization.setMD5(dto.getMD5());
        paymentAuthorization.setTransactionId(dto.getTransactionId());
        paymentAuthorization.setResponseMessage(Util.truncateString(dto.getResponseMessage(),200));
        
        // all authorization have to be linked to a payment
        try {
            PaymentBL payment = new PaymentBL(paymentId);
            paymentAuthorization.setPayment(payment.getEntity());
        } catch (Exception e) {
            throw new SessionInternalError(e);
        }

        // original dto would like to know the created id and the payment id
        dto.setId(paymentAuthorization.getId());
        dto.setPayment(new PaymentDTO(paymentId));
    }
    
    public PaymentAuthorizationDTO getDTO() {
        PaymentAuthorizationDTO dto = new PaymentAuthorizationDTO();
        dto.setApprovalCode(paymentAuthorization.getApprovalCode());
        dto.setAvs(paymentAuthorization.getAvs());
        dto.setCardCode(paymentAuthorization.getCardCode());
        dto.setCode1(paymentAuthorization.getCode1());
        dto.setCode2(paymentAuthorization.getCode2());
        dto.setCode3(paymentAuthorization.getCode3());
        dto.setMD5(paymentAuthorization.getMD5());
        dto.setId(paymentAuthorization.getId());
        dto.setProcessor(paymentAuthorization.getProcessor());        
        dto.setTransactionId(paymentAuthorization.getTransactionId());
        dto.setCreateDate(paymentAuthorization.getCreateDate());
        dto.setResponseMessage(paymentAuthorization.getResponseMessage());
        return dto;
    }
        
    public PaymentAuthorizationDTO getPreAuthorization(Integer userId) {
        PaymentAuthorizationDTO auth = null;
        try {
            PaymentDAS paymentHome = new PaymentDAS();

            Collection payments = paymentHome.findPreauth(userId);
            // at the time, use the very first one
            if (!payments.isEmpty()) {
                PaymentDTO payment = (PaymentDTO) payments.toArray()[0];
                Collection auths = payment.getPaymentAuthorizations();
                if (!auths.isEmpty()) {
                    paymentAuthorization = 
                            (PaymentAuthorizationDTO) auths.toArray()[0];
                    auth = getDTO();
                } else {
                    LOG.warn("Auth payment found, but without auth record?");
                }
            }
        } catch (Exception e) {
            LOG.warn("Exceptions finding a pre authorization", e);
        }
        LOG.debug("Looking for preauth for " + userId + " result " + auth);
        return auth;
    }

    public void markAsUsed(PaymentDTOEx user) {
        paymentAuthorization.getPayment().setBalance(BigDecimal.ZERO);
        // this authorization got used by a real payment. Link them
        try {
            PaymentBL payment = new PaymentBL(user.getId());
            paymentAuthorization.getPayment().setPayment(payment.getEntity());
        } catch (Exception e) {
            throw new SessionInternalError("linking authorization to user payment",
                    PaymentAuthorizationBL.class, e);
        } 
    }
}
