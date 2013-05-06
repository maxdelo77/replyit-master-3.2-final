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

package com.sapienter.jbilling.server.item;

import com.sapienter.jbilling.common.SessionInternalError;
import com.sapienter.jbilling.server.customer.CustomerBL;
import com.sapienter.jbilling.server.item.db.PlanDTO;
import com.sapienter.jbilling.server.item.db.PlanItemBundleDTO;
import com.sapienter.jbilling.server.item.db.PlanItemDTO;
import com.sapienter.jbilling.server.order.db.OrderLineDTO;
import com.sapienter.jbilling.server.order.db.OrderPeriodDAS;
import com.sapienter.jbilling.server.order.db.OrderPeriodDTO;
import com.sapienter.jbilling.server.user.db.CustomerDTO;
import com.sapienter.jbilling.server.user.db.UserDTO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * PlanItemBundleBL
 *
 * @author Brian Cowdery
 * @since 25/03/11
 */
public class PlanItemBundleBL {

    /**
     * Convert a given PlanItemBundleWS web-service object into a PlanItemBundleDTO.
     *
     * The PlanItemWS web-service object must have a valid period id or an exception
     * will be thrown.
     *
     * @param ws ws object to convert
     * @return converted DTO object
     * @throws SessionInternalError if required field is missing
     */
    public static PlanItemBundleDTO getDTO(PlanItemBundleWS ws) {
       if (ws != null) {

            if (ws.getPeriodId() == null)
                throw new SessionInternalError("PlanItemBundleWS must have a period.");
            if (ws.getQuantityAsDecimal().compareTo(BigDecimal.ZERO) < 0)
            {
                throw new SessionInternalError("Quantity should not be non negative " ,
                        new String[] { "PlanItemBundleWS,quantity,validation.error.nonnegative" });

            }

            OrderPeriodDTO period = ws.getPeriodId() != null ? new OrderPeriodDAS().find(ws.getPeriodId()) : null;

            return new PlanItemBundleDTO(ws, period);
        }
        return null;
    }

    /**
     * Convert a given PlanItemBundleDTO into a PlanItemBundleWS web-service object.
     *
     * @param dto dto to convert
     * @return converted web-service object
     */
    public static PlanItemBundleWS getWS(PlanItemBundleDTO dto) {
        return dto != null ? new PlanItemBundleWS(dto) : null;
    }

    public static UserDTO getTargetUser(PlanItemBundleDTO.Customer target, CustomerDTO customer) {
        if (target != null && customer != null) {
            switch (target) {
                case SELF:
                    return customer.getBaseUser();

                case BILLABLE:
                    CustomerDTO parent = new CustomerBL(customer).getInvoicableParent();
                    return parent != null ? parent.getBaseUser() : null;
            }
        }
        return null;
    }
}
