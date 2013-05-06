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

package com.sapienter.jbilling.server.customer;

import org.apache.log4j.Logger;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.common.SessionInternalError;
import com.sapienter.jbilling.server.user.ContactBL;
import com.sapienter.jbilling.server.user.ContactDTOEx;

@Transactional( propagation = Propagation.REQUIRED )
public class CustomerSessionBean implements ICustomerSessionBean {

    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(
            CustomerSessionBean.class));

    public ContactDTOEx getPrimaryContactDTO(Integer userId)
            throws SessionInternalError {
        try {
            ContactBL bl = new ContactBL();
            bl.set(userId);
            return bl.getDTO();
        } catch (Exception e) {
            LOG.error("Exception retreiving the customer contact", e);
            throw new SessionInternalError("Customer primary contact");
        }
    }
}
