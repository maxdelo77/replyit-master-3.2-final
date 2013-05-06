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

package com.sapienter.jbilling.server.user.balance;

import com.sapienter.jbilling.server.user.db.CustomerDTO;
import java.math.BigDecimal;

/**
 *
 * @author emilc
 */
public class ValidatorNone implements  IUserBalanceValidation {

    public BigDecimal validate(CustomerDTO customer, BigDecimal amount) {
        return new BigDecimal("1000.0"); // always ok
    }
}
