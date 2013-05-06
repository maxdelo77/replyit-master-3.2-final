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

package com.sapienter.jbilling.server.pluggableTask;

import java.io.Serializable;

import com.sapienter.jbilling.server.payment.db.PaymentAuthorizationDTO;


public class AuthorizeNetResponseDTO implements Serializable {
    private PaymentAuthorizationDTO dbRow = null;
    
    public AuthorizeNetResponseDTO(String rawResponse) {
        // wow, how easy this is ?!! :)
        String fields[] = rawResponse.split(",", -1);
        dbRow = new PaymentAuthorizationDTO();
        
        dbRow.setCode1(fields[0]); // code 
        dbRow.setCode2(fields[1]); // subcode
        dbRow.setCode3(fields[2]); // reason code
        dbRow.setResponseMessage(fields[3]); // a string with plain text with a reason for this result
        dbRow.setApprovalCode(fields[4]); 
        dbRow.setAvs(fields[5]);
        dbRow.setTransactionId(fields[6]);
        dbRow.setMD5(fields[37]);
        dbRow.setCardCode(fields[38]);        
    }
    
    public String toString() {
        return "[" +
            "code=" + dbRow.getCode1() + "," +
        "subCode=" + dbRow.getCode2() + "," +
        "reasonCode=" + dbRow.getCode3() + "," +
        "reasonText=" + dbRow.getResponseMessage() + "," +
        "approvalCode=" + dbRow.getApprovalCode() + "," +
        "AVSResultCode=" + dbRow.getAvs() + "," +
        "transactionId=" + dbRow.getTransactionId() + "," +
        "MD5Hash=" + dbRow.getMD5() + "," +
        "cardCode=" + dbRow.getCardCode() + 
        "]";
    }
    
    public PaymentAuthorizationDTO getPaymentAuthorizationDTO() {
        return dbRow;
    }

}
