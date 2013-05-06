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
package com.sapienter.jbilling.server.user.tasks;

import java.util.Date;

import com.sapienter.jbilling.server.payment.PaymentDTOEx;

public interface ISubscriptionStatusManager {
    public void paymentFailed(Integer entityId, PaymentDTOEx payment);
    public void paymentSuccessful(Integer entityId, PaymentDTOEx payment);
    public void subscriptionEnds(Integer userId, Date newActiveUntil, 
            Date oldActiveUntil);
    public void subscriptionEnds(Integer userId, Date date);
}
