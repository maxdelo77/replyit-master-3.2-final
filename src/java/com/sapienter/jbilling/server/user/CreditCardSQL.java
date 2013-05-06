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

/*
 * Created on Jan 15, 2005
 *
 */
package com.sapienter.jbilling.server.user;

/**
 * @author Emil
 *
 */
public interface CreditCardSQL {
    static final String expiring =
        "select bu.id, cc.id " +
        " from base_user bu, credit_card cc, user_credit_card_map uccm " +
        "where bu.deleted = 0 " +
        "  and bu.status_id < " + UserDTOEx.STATUS_SUSPENDED +
        "  and cc.deleted = 0 " +
        "  and bu.id = uccm.user_id " +
        "  and cc.id = uccm.credit_card_id " +
        "  and cc.cc_expiry <= ?";
}
