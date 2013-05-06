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

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.server.payment.PaymentDTOEx;
import com.sapienter.jbilling.server.user.CreditCardBL;
import com.sapienter.jbilling.server.user.UserBL;
import com.sapienter.jbilling.server.util.Constants;
import org.apache.log4j.Logger;

/**
 * Created with IntelliJ IDEA.
 *
 *  This will check the preferred payment method by default
 *  If preferred method is not available it will check the next available payment method info
 *
 * @author Panche.Isajeski
 * @since 17/05/12
 */
public class AlternativePaymentInfoTask extends BasicPaymentInfoTask {

    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(AlternativePaymentInfoTask.class));

    @Override
    public PaymentDTOEx getPaymentInfo(Integer userId) throws TaskException {
        PaymentDTOEx retValue = null;
        try {
            Integer method = Constants.AUTO_PAYMENT_TYPE_CC; // def to cc
            UserBL userBL = new UserBL(userId);
            CreditCardBL ccBL = new CreditCardBL();
            if (userBL.getEntity().getCustomer() != null) {
                // now non-customers only use credit cards
                method = userBL.getEntity().getCustomer().getAutoPaymentType();
                if (method == null) {
                    method = Constants.AUTO_PAYMENT_TYPE_CC;
                }
            }

            if (method.equals(Constants.AUTO_PAYMENT_TYPE_CC)) {
                if (null == (retValue = processCreditCardInfo(userBL, ccBL))) {
                    retValue = processACHInfo(userBL);
                }
            } else if (method.equals(Constants.AUTO_PAYMENT_TYPE_ACH)) {
                if (null == (retValue = processACHInfo(userBL))) {
                    retValue = processCreditCardInfo(userBL, ccBL);
                }
            }
        } catch (Exception e) {
            throw new TaskException(e);
        }
        if (retValue == null) {
            LOG.debug("Could not find payment instrument for user " + userId);
        }
        return retValue;
    }
}
