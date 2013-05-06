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
import java.util.Set;

import com.sapienter.jbilling.server.metafields.db.MetaField;
import com.sapienter.jbilling.server.metafields.db.MetaFieldDAS;
import com.sapienter.jbilling.server.metafields.db.MetaFieldValue;
import com.sapienter.jbilling.server.payment.PaymentDTOEx;
import com.sapienter.jbilling.server.payment.blacklist.db.BlacklistDAS;
import com.sapienter.jbilling.server.payment.blacklist.db.BlacklistDTO;
import com.sapienter.jbilling.server.user.db.UserDAS;
import com.sapienter.jbilling.server.user.db.UserDTO;
import com.sapienter.jbilling.server.util.Util;

/**
 * Filters by custom contact field: IP Address.
 */
public class IpAddressFilter implements BlacklistFilter {

    private Integer ipAddressCcf; // custom contact field id for ip address

    public IpAddressFilter(Integer ipAddressCcf) {
        if (ipAddressCcf == null) {
            throw new IllegalArgumentException("IP Address CCF id is null");
        }
        this.ipAddressCcf = ipAddressCcf;
    }

    public Result checkPayment(PaymentDTOEx paymentInfo) {
        return checkUser(paymentInfo.getUserId());
    }

    public Result checkUser(Integer userId) {
        UserDTO user = new UserDAS().find(userId);

        if (user == null || user.getCustomer() == null) {
            return new Result(false, null);
        }

        String ipAddress = null;
        MetaField metaField = new MetaFieldDAS().find(ipAddressCcf);
        if (metaField != null) {
            MetaFieldValue metaFieldValue = user.getCustomer().getMetaField(metaField.getName());
            if (metaFieldValue != null) {
                ipAddress = (String) metaFieldValue.getValue();
            }
        }

        // user has no ip address custom contact field
        if (ipAddress == null) {
            return new Result(false, null);
        }

        Integer entityId = new UserDAS().find(userId).getCompany().getId();
        List<BlacklistDTO> blacklist = new BlacklistDAS().filterByIpAddress(
                entityId, ipAddress, ipAddressCcf);

        if (!blacklist.isEmpty()) {
            ResourceBundle bundle = Util.getEntityNotificationsBundle(userId);
            return new Result(true, 
                    bundle.getString("payment.blacklist.ip_address_filter"));
        }

        return new Result(false, null);
    }

    public String getName() {
        return "IP address blacklist filter";
    }
}
