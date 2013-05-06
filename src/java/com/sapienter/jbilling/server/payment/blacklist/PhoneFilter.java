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

import java.util.List;
import java.util.ResourceBundle;

import com.sapienter.jbilling.server.payment.PaymentDTOEx;
import com.sapienter.jbilling.server.payment.blacklist.db.BlacklistDAS;
import com.sapienter.jbilling.server.payment.blacklist.db.BlacklistDTO;
import com.sapienter.jbilling.server.user.contact.db.ContactDAS;
import com.sapienter.jbilling.server.user.contact.db.ContactDTO;
import com.sapienter.jbilling.server.user.db.UserDAS;
import com.sapienter.jbilling.server.util.Util;

/**
 * Filters contact phone numbers.
 */
public class PhoneFilter implements BlacklistFilter {

    public Result checkPayment(PaymentDTOEx paymentInfo) {
        return checkUser(paymentInfo.getUserId());
    }

    public Result checkUser(Integer userId) {
        ContactDTO contact = new ContactDAS().findPrimaryContact(userId);

        if (contact == null) {
            return new Result(false, null);
        }

        if (contact.getPhoneCountryCode() == null && 
                contact.getPhoneAreaCode() == null &&
                contact.getPhoneNumber() == null) {
            return new Result(false, null);
        }

        Integer entityId = new UserDAS().find(userId).getCompany().getId();
        List<BlacklistDTO> blacklist = new BlacklistDAS().filterByPhone(entityId, 
                contact.getPhoneCountryCode(), contact.getPhoneAreaCode(), 
                contact.getPhoneNumber());

        if (!blacklist.isEmpty()) {
            ResourceBundle bundle = Util.getEntityNotificationsBundle(userId);
            return new Result(true, 
                    bundle.getString("payment.blacklist.phone_filter"));
        }

        return new Result(false, null);
    }

    public String getName() {
        return "Phone blacklist filter";
    }
}
