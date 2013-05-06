package com.sapienter.jbilling.server.mediation.task;

/*
 JBILLING CONFIDENTIAL
 _____________________

 [2003] - [2012] Enterprise jBilling Software Ltd.
 All Rights Reserved.

 NOTICE:  All information contained herein is, and remains
 the property of Enterprise jBilling Software.
 The intellectual and technical concepts contained
 herein are proprietary to Enterprise jBilling Software
 and are protected by trade secret or copyright law.
 Dissemination of this information or reproduction of this material
 is strictly forbidden.
 */

import com.sapienter.jbilling.server.item.PricingField;
import com.sapienter.jbilling.server.order.OrderBL;
import com.sapienter.jbilling.server.order.OrderLineWS;
import com.sapienter.jbilling.server.order.db.OrderDTO;
import com.sapienter.jbilling.server.order.db.OrderLineDTO;
import com.sapienter.jbilling.server.pluggableTask.admin.ParameterDescription;
import com.sapienter.jbilling.server.user.db.UserDAS;
import com.sapienter.jbilling.server.user.db.UserDTO;
import com.sapienter.jbilling.server.util.Constants;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class SubscriptionEventProcessor extends AbstractResolverMediationTask {

    private String billingType;
    private Integer billingTypeId;

    private static final Logger LOG = Logger.getLogger(SubscriptionEventProcessor.class);

    public static final ParameterDescription PARAMETER_BILLING_TYPE =
            new ParameterDescription("billing_type", true, ParameterDescription.Type.STR);

    //initializer for pluggable params
    {
        descriptions.add(PARAMETER_BILLING_TYPE);
    }

    private static final String BILLING_TYPE_PRE_PAID = "pre";
    private static final String BILLING_TYPE_POST_PAID = "post";
    private static final Integer LONG_DISTANCE_CALL_GEN = 2900;

    /**
     * Resolve the date that the call event occurred.
     *
     * @param result mediation record
     * @param fields pricing fields
     */
    public void resolveDate (MediationResult result, List<PricingField> fields) {
        PricingField start = find(fields, "initiated");
        result.setEventDate(start.getDateValue());

        LOG.debug("Set result date to " + result.getEventDate());
    }

    /**
     * Resolve the billable user (customer) for this mediation record.
     *
     * @param result mediation record
     * @param fields pricing fields
     */
    public void resolveUser (MediationResult result, List<PricingField> fields) {
        String username = find(fields, "user_name").getStrValue();

        UserDTO user = new UserDAS().findByUserName(username, getEntityId());

        if (result.getUserId() == null && user != null) {
            result.setUserId(user.getUserId());
            result.setCurrencyId(user.getCurrencyId());
        }

        LOG.debug("Set result user id " + result.getUserId() + ", currency id " + result.getCurrencyId());
    }

    @Override
    public void resolveUserCurrencyAndDate (MediationResult result, List<PricingField> fields) {
        resolveUser(result, fields);
        resolveDate(result, fields);
    }

    /**
     * Return true if the mediation data is valid and can be processed.
     *
     * @param result mediation result
     * @param fields pricing fields
     * @return true if data can be processed into a call event on an order.
     */
    @Override
    public boolean isActionable (MediationResult result, List<PricingField> fields) {

        // continue to call identification if we have the user, currency, and a valid event date.
        if (result.getUserId() != null
                && result.getCurrencyId() != null
                && result.getEventDate() != null) {

            LOG.debug("Mediation result " + result.getId() + " is actionable, resolving item ...");
            return true;

        } else {
            LOG.debug("Mediation result " + result.getId() + " cannot be processed!");
            return false;
        }
    }

    /**
     * Resolve the call item and add it to the current order.
     *
     * @param result mediation result
     * @param fields pricing fields
     */
    @Override
    public void doEventAction (MediationResult result, List<PricingField> fields) {
        String productCode = find(fields, "product_code").getStrValue();

        OrderLineDTO orderLine = null;

        // get the quantity.
        BigDecimal quantity = find(fields, "quantity").getDecimalValue();
        // get the active until until date.
        Date activeUntil = find(fields, "active_until").getDateValue();

        if (productCode.equals(LONG_DISTANCE_CALL_GEN.toString())) {
            orderLine = newLine(LONG_DISTANCE_CALL_GEN, quantity);
            orderLine.setTypeId(Constants.ORDER_LINE_TYPE_ITEM);
            result.setDescription("Long distance call generic");
            LOG.debug("added product 'long distance call': " + result.getRecordKey());
        } else {
            LOG.debug("No product could be resolved.");
            result.addError("UNKNOWN-PRODUCT");
            return;
        }

        // get the billing type to use in case we need to create the order. If no value is found the as default we set it
        // to Post-Paid.
        billingType = getParameter(PARAMETER_BILLING_TYPE.getName(), BILLING_TYPE_POST_PAID);
        if (billingType.equals(BILLING_TYPE_PRE_PAID)) {
            billingTypeId = Constants.ORDER_BILLING_PRE_PAID;
        } else if (billingType.equals(BILLING_TYPE_POST_PAID)) {
            billingTypeId = Constants.ORDER_BILLING_POST_PAID;
        }

        OrderDTO currentOrder = OrderBL.getOrCreateRecurringOrder(result.getUserId(), orderLine.getItem().getId(),
                result.getEventDate(), result.getCurrencyId(), result.getPersist(), billingTypeId);

        if (currentOrder == null) {
            LOG.debug("Could not create a recurring order.");
            result.addError("Could not create a recurring order.");
            return;
        }

        // set the new active until date.
        currentOrder.setActiveUntil(activeUntil);
        result.setCurrentOrder(currentOrder);

        // update the order line depending on if it's new or an existing one.
        OrderLineDTO currentOrderLine = currentOrder.getLine(orderLine.getItem().getId());
        if (currentOrderLine != null) {
            OrderLineWS ws = new OrderLineWS(currentOrderLine.getId(), currentOrderLine.getItemId(),
                    currentOrderLine.getDescription(), currentOrderLine.getAmount(), quantity,
                    currentOrderLine.getPrice(), currentOrderLine.getCreateDatetime(), currentOrderLine.getDeleted(),
                    currentOrderLine.getTypeId(), currentOrderLine.getEditable(), currentOrder.getId(), currentOrderLine.getUseItem(),
                    currentOrderLine.getVersionNum(), currentOrderLine.getProvisioningStatusId(), currentOrderLine.getProvisioningRequestId());

            OrderBL bl = new OrderBL();
            bl.updateOrderLine(ws);
        } else {
            result.getLines().add(orderLine);
        }
    }
}
