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

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.server.payment.PaymentDTOEx;
import com.sapienter.jbilling.server.payment.db.PaymentMethodDAS;
import com.sapienter.jbilling.server.user.AchBL;
import com.sapienter.jbilling.server.user.CreditCardBL;
import com.sapienter.jbilling.server.user.UserBL;
import com.sapienter.jbilling.server.user.db.AchDTO;
import com.sapienter.jbilling.server.user.db.CreditCardDTO;
import com.sapienter.jbilling.server.util.Constants;

/**
 * This creates payment dto. It now only goes and fetches the credit card
 * of the given user. It doesn't need to initialize the rest of the payment
 * information (amount, etc), only the info for the payment processor,
 * usually cc info but it could be electronic cheque, etc...
 * This task should consider that the user is a partner and is being paid
 * (like a refund) and therefore fetch some other information, as getting
 * paid with a cc seems not to be the norm.
 * @author Emil
 */
public class BasicPaymentInfoTask
        extends PluggableTask implements PaymentInfoTask {

    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(BasicPaymentInfoTask.class));
    /**
     * This will return an empty payment dto with only the credit card/ach set
     * if a valid credit card is found for the user. Otherwise null.
     * It will check the customer's preference for the automatic payment type.
     */
    public PaymentDTOEx getPaymentInfo(Integer userId)
            throws TaskException {
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
                retValue = processCreditCardInfo(userBL, ccBL);
            } else if (method.equals(Constants.AUTO_PAYMENT_TYPE_ACH)) {
                retValue = processACHInfo(userBL);
            }
        } catch (Exception e) {
            throw new TaskException(e);
        }
        if (retValue == null) {
            LOG.debug("Could not find payment instrument for user " + userId);
        }
        return retValue;
    }

    /**
     * Process credit card and returns the payment info
     *
     * @param userBL
     * @param ccBL
     * @param retValue
     * @return PaymentDTOEx if user contains CC info; null otherwise
     */
    protected PaymentDTOEx processCreditCardInfo(UserBL userBL, CreditCardBL ccBL) {

        if (userBL.getEntity().getCreditCards().isEmpty()) {
            return null;
        } else {
            // go around the provided cards and get one that is sendable
            // to the processor
            for (Iterator it = userBL.getEntity().getCreditCards().
                    iterator(); it.hasNext(); ) {
                ccBL.set(((CreditCardDTO) it.next()).getId());
                if (ccBL.validate()) {
                    PaymentDTOEx retValue = new PaymentDTOEx();
                    retValue.setCreditCard(ccBL.getDTO());
                    retValue.setPaymentMethod(new PaymentMethodDAS().find(ccBL.getPaymentMethod()));
                    return retValue;
                }
            }

        }

        return null;
    }

    /**
     * Process ACH and returns the payment info
     *
     * @param userBL
     * @param retValue
     * @return PaymentDTOEx if user contains ACH info; null otherwise
     */
    protected PaymentDTOEx processACHInfo(UserBL userBL) {

        AchDTO ach =  null;
        if (userBL.getEntity().getAchs().size() > 0) {
            // take the default customer ACH record
            // if not exist take the ACH info from the last payment
            ach = userBL.getEntity().getAchWithoutPayment();
            if (ach == null) {
                AchBL bl = new AchBL(((AchDTO)userBL.getEntity().getAchs().toArray()[0]).getId());
                ach = bl.getEntity();
            }
        }
        if (ach == null) {
            // no info available
            return null;
        } else {
            PaymentDTOEx retValue = new PaymentDTOEx();
            retValue.setAch(new AchDTO(0, ach.getAbaRouting(),
                    ach.getBankAccount(), ach.getAccountType(),
                    ach.getBankName(), ach.getAccountName(), ach.getGatewayKey()));
            retValue.setPaymentMethod(new PaymentMethodDAS().find(Constants.PAYMENT_METHOD_ACH));
            return retValue;
        }
    }
}
