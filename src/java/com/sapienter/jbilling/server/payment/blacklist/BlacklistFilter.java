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
package com.sapienter.jbilling.server.payment.blacklist;

import com.sapienter.jbilling.server.payment.PaymentDTOEx;

/**
 * Blacklist filter interface.
 */
public interface BlacklistFilter {
    /**
     * Checks if a payment is blacklisted
     */
    public Result checkPayment(PaymentDTOEx paymentInfo);

    /**
     * Checks if a user is blacklisted
     */
    public Result checkUser(Integer userId);

    /**
     * Returns the filter name to place on the authorization record for
     * blacklisted payments/users.
     */
    public String getName();

    /**
     * Used to return the result of blacklisted payments/users
     */
    static final class Result {
        private final boolean isBlacklisted;
        private final String message;

        public Result(boolean isBlacklisted, String message) {
            this.isBlacklisted = isBlacklisted;
            this.message = message;
        }

        public boolean isBlacklisted() {
            return isBlacklisted;
        }

        public String getMessage() {
            return message;
        }
    }
}
