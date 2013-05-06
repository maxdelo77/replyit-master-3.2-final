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

import com.sapienter.jbilling.server.payment.PaymentAuthorizationBL;
import com.sapienter.jbilling.server.payment.PaymentDTOEx;
import com.sapienter.jbilling.server.payment.db.PaymentAuthorizationDTO;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskException;

public abstract class PaymentTaskBase extends PluggableTask implements PaymentTask {

    protected final String ensureGetParameter(String key) throws PluggableTaskException {
        Object value = parameters.get(key);
        if (false == value instanceof String) {
            throw new PluggableTaskException("Missed or wrong parameter for: " + key + ", string expected: " + value);
        }

        return (String) value;
    }

    protected final String getOptionalParameter(String key, String valueIfNull) {
        Object value = parameters.get(key);
        return (value instanceof String) ? (String) value : valueIfNull;
    }

    protected final boolean getBooleanParameter(String key) {
        return Boolean.parseBoolean(getOptionalParameter(key, "false"));
    }

    protected final void storeProcessedAuthorization(PaymentDTOEx paymentInfo,
            PaymentAuthorizationDTO auth) throws PluggableTaskException {

        new PaymentAuthorizationBL().create(auth, paymentInfo.getId());
        paymentInfo.setAuthorization(auth);
    }

    /**
     * Usefull for processors that want to use the same template method for
     * process() and preauth() methods
     */
    protected static final class Result {

        private final boolean myCallOtherProcessors;
        private final PaymentAuthorizationDTO myAuthorizationData;

        public Result(PaymentAuthorizationDTO data, boolean shouldCallOthers) {
            myAuthorizationData = data;
            myCallOtherProcessors = shouldCallOthers;
        }

        public PaymentAuthorizationDTO getAuthorizationData() {
            return myAuthorizationData;
        }

        public boolean shouldCallOtherProcessors() {
            return myCallOtherProcessors;
        }

        public String toString() {
            return "Result: myCallOtherProcessors " + myCallOtherProcessors +
                    " data " + myAuthorizationData;
        }
    }
    protected static final Result NOT_APPLICABLE = new Result(null, true);

    protected String getString(String str) {
        if (str == null) {
            return "";
        } else {
            return str;
        }
    }
}
