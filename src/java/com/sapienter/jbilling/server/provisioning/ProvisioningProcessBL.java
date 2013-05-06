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



package com.sapienter.jbilling.server.provisioning;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.sapienter.jbilling.common.Constants;
import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.server.order.db.OrderDAS;
import com.sapienter.jbilling.server.order.db.OrderDTO;
import com.sapienter.jbilling.server.order.db.OrderLineDTO;
import com.sapienter.jbilling.server.provisioning.event.SubscriptionActiveEvent;
import com.sapienter.jbilling.server.provisioning.event.SubscriptionInactiveEvent;
import com.sapienter.jbilling.server.system.event.EventManager;


/**
 * @author othman
 *
 */
public class ProvisioningProcessBL {
    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(ProvisioningProcessBL.class));

    /**
     * calls method activateOrder(OrderDTO) For each order where the condition OrderDTO.activeSince <= today <
     * OrderDTO.activeUntil is true
     */
    public void activateOrders() {
        OrderDAS orders = new OrderDAS();

        // get list of orders where OrderDTO.activeSince <= today <OrderDTO.activeUntil
        List<OrderDTO> toActivateOrders = orders.findToActivateOrders();

        if (toActivateOrders == null) {
            LOG.debug("toActivate orders list =null");

            return;
        }

        for (Iterator<OrderDTO> it = toActivateOrders.iterator(); it.hasNext(); ) {
            OrderDTO toActivateOrder = (OrderDTO) it.next();

            this.activateOrder(toActivateOrder);
        }
    }

    /**
     * calls method deActivateOrder(OrderDTO) For each order where the condition OrderDTO.activeSince > today ||
     * OrderDTO.activeUntil <= today is true
     */
    public void deActivateOrders() {
        OrderDAS orders = new OrderDAS();

        // get list of orders where OrderDTO.activeSince > today || OrderDTO.activeUntil <= today
        List<OrderDTO> toDeActivateOrders = orders.findToDeActiveOrders();

        if (toDeActivateOrders == null) {
            LOG.debug("toDeActivate orders list =null");

            return;
        }

        for (Iterator<OrderDTO> it = toDeActivateOrders.iterator(); it.hasNext(); ) {
            OrderDTO toDeActivateOrder = (OrderDTO) it.next();

            this.deActivateOrder(toDeActivateOrder);
        }
    }

    /**
     * each order line will be checked to see if any OrderLineDTO.provisioningStatus == PROVISIONING_STATUS_INACTIVE.
     *  If there are any matches, generate a SubscriptionActiveEvent on that order
     * @param order
     */
    private void activateOrder(OrderDTO order) {
        LOG.debug("active Order " + order.getId());

        boolean            doActivate = false;
        List<OrderLineDTO> orderLines = order.getLines();

        if (orderLines == null) {
            return;
        }

        for (Iterator<OrderLineDTO> it = orderLines.iterator(); it.hasNext(); ) {
            OrderLineDTO line = (OrderLineDTO) it.next();

            if ((line != null) && (line.getProvisioningStatusId() != null)
                    && line.getProvisioningStatusId().equals(Constants.PROVISIONING_STATUS_INACTIVE)) {
                LOG.debug(line + ": order line status is PROVISIONING_STATUS_INACTIVE-> Activate it!");
                doActivate = true;

                break;
            }
        }

        if (doActivate) {

            // generate SubscriptionActiveEvent on order
            Integer                 entityId = order.getUser().getCompany().getId();
            SubscriptionActiveEvent newEvent = new SubscriptionActiveEvent(entityId, order);

            EventManager.process(newEvent);
            LOG.debug("generated SubscriptionActiveEvent for order: " + order);
        }
    }

    /**
     * each order line will be checked to see if any
     * OrderLineDTO.provisioningStatus == PROVISIONING_STATUS_ACTIVE.
     * If there are any matches, generate a SubscriptionInactiveEvent on that order.
     * @param order
     */
    private void deActivateOrder(OrderDTO order) {
        LOG.debug("inactive Order " + order.getId());

        boolean            doInActivate = false;
        List<OrderLineDTO> orderLines   = order.getLines();

        if (orderLines == null) {
            return;
        }

        for (Iterator<OrderLineDTO> it = orderLines.iterator(); it.hasNext(); ) {
            OrderLineDTO line = (OrderLineDTO) it.next();

            if (line == null) {
                continue;
            }

            if ((line != null) && (line.getProvisioningStatusId() != null)
                    && line.getProvisioningStatusId().equals(Constants.PROVISIONING_STATUS_ACTIVE)) {
                LOG.debug(line + ": order line status is PROVISIONING_STATUS_ACTIVE-> DeActivate it!");
                doInActivate = true;

                break;
            }
        }

        if (doInActivate) {

            // generate SubscriptionInActiveEvent on order
            Integer                   entityId = order.getUser().getCompany().getId();
            SubscriptionInactiveEvent newEvent = new SubscriptionInactiveEvent(entityId, order);

            EventManager.process(newEvent);
            LOG.debug("generated SubscriptionInActiveEvent for order: " + order);
        }
    }
}
