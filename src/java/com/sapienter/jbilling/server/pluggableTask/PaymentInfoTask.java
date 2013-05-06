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

import com.sapienter.jbilling.server.payment.PaymentDTOEx;

/*
 * This task gathers the information necessary to process a payment.
 * Since each customer and entity can have different payment methods
 * this is better placed in a pluggable task.
 * The result of the process call is the payment dto with all the info
 * to later send the payment to the live processor. The methos of the
 * payment has to be also set
 */
public interface PaymentInfoTask {
    
    PaymentDTOEx getPaymentInfo(Integer userId) throws TaskException;
    
}
