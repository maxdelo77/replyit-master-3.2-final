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

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.server.payment.PaymentDTOEx;
import com.sapienter.jbilling.server.payment.blacklist.db.BlacklistDAS;
import com.sapienter.jbilling.server.payment.blacklist.db.BlacklistDTO;
import com.sapienter.jbilling.server.user.db.CompanyDAS;
import com.sapienter.jbilling.server.user.db.UserDAS;

public class TestBlacklistFilter implements BlacklistFilter {

    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(TestBlacklistFilter.class));

    public Result checkPayment(PaymentDTOEx paymentInfo) {
        return checkUser(paymentInfo.getUserId());
    }

    public Result checkUser(Integer userId) {
        // checks if the userId is blacklisted (which would probably be set by
        // customer service through the GUI)
        List<BlacklistDTO> blacklist = new BlacklistDAS().findByUserType(
                userId, BlacklistDTO.TYPE_USER_ID);

        if (!blacklist.isEmpty()) {
            // id blacklisted, but first lets test blacklist db functionality
            if (!testDB()) {
                return new Result(true, "Problems with blacklist db test");
            }
            return new Result(true, "This user is blacklisted");
        }
        return new Result(false, null);
    }

    // returns true if success
    private boolean testDB() {
        BlacklistDAS blacklistDas = new BlacklistDAS();

        // try adding a blacklist entry
        BlacklistDTO entry = new BlacklistDTO();
        entry.setCompany(new CompanyDAS().find(1));
        entry.setCreateDate(new Date());
        entry.setType(BlacklistDTO.TYPE_USER_ID); 
        entry.setSource(BlacklistDTO.SOURCE_EXTERNAL_UPLOAD);
        entry.setUser(new UserDAS().find(1001)); 
        blacklistDas.save(entry);
        blacklistDas.flush();

        // try getting it back
        List<BlacklistDTO> blacklist = blacklistDas.findByEntityType(1, 
                BlacklistDTO.TYPE_USER_ID);

        if (blacklist.size() != 2) {
            // didn't work
            LOG.debug("Returned blacklist size is: " + blacklist.size());
            return false;
        }

        entry = blacklist.get(1);
        if (entry.getType() != BlacklistDTO.TYPE_USER_ID) {
            LOG.debug("Blacklist entry type is: " + entry.getType());
            return false;
        }

        if (entry.getUser().getId() != 1001) {
            LOG.debug("user id is: " + entry.getUser().getId());
            return false;
        }

        return true;
    }

    public String getName() {
        return "Test blacklist filter";
    }
}
