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
package com.sapienter.jbilling.server.user.db;

import com.sapienter.jbilling.server.util.db.AbstractDAS;

public class AchDAS extends AbstractDAS<AchDTO> {

    public AchDTO create(UserDTO baseUser, String abaRouting, String bankAccount,
            Integer accountType, String bankName, String accountName, String gatewayKey) {
        
        AchDTO ach = new AchDTO();
        ach.setBaseUser(baseUser);
        ach.setAbaRouting(abaRouting);
        ach.setBankAccount(bankAccount);
        ach.setAccountType(accountType);
        ach.setBankName(bankName);
        ach.setAccountName(accountName);
        ach.setGatewayKey(gatewayKey);
        return save(ach);
    }

}
