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

import java.io.Serializable;

import com.sapienter.jbilling.server.entity.PaymentAuthorizationDTO;


/**
 * @author Emil
 */
public class PaymentAuthorizationDTOEx extends PaymentAuthorizationDTO implements Serializable {
    private Boolean result;
    
    public PaymentAuthorizationDTOEx() {
        super();
    }
    
    public PaymentAuthorizationDTOEx(PaymentAuthorizationDTO dto) {
        super(dto);
    }

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public String toString() {
        return super.toString() + " result=" + result;
    }
}
