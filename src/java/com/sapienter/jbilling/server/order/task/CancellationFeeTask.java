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
package com.sapienter.jbilling.server.order.task;

import java.math.BigDecimal;
import java.util.Date;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.common.SessionInternalError;
import com.sapienter.jbilling.server.item.ItemBL;
import com.sapienter.jbilling.server.item.ItemDecimalsException;
import com.sapienter.jbilling.server.item.db.ItemDAS;
import com.sapienter.jbilling.server.item.db.ItemDTO;
import com.sapienter.jbilling.server.order.OrderBL;
import com.sapienter.jbilling.server.order.db.OrderDAS;
import com.sapienter.jbilling.server.order.db.OrderDTO;
import com.sapienter.jbilling.server.order.db.OrderLineDTO;
import com.sapienter.jbilling.server.order.db.OrderLineTypeDAS;
import com.sapienter.jbilling.server.order.db.OrderPeriodDAS;
import com.sapienter.jbilling.server.order.event.NewActiveUntilEvent;
import com.sapienter.jbilling.server.order.event.NewQuantityEvent;
import com.sapienter.jbilling.server.pluggableTask.PluggableTask;
import com.sapienter.jbilling.server.pluggableTask.admin.ParameterDescription;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskException;
import com.sapienter.jbilling.server.system.event.Event;
import com.sapienter.jbilling.server.system.event.task.IInternalEventsTask;
import com.sapienter.jbilling.server.user.UserBL;
import com.sapienter.jbilling.server.util.Constants;

/**
 * Cancellation fee task
 *
 * parameters:
 *      product_cancelled       = item id of the product that triggers the fee when the order is cancelled
 *      product_fee             = item id of the fee to add
 *      use_cancelled_quantity  = if true, the cancellation quantity fee will match the trigger product
 *      pro_date                = if true, the cancellation fee quantity will pro-rated to cover the remaining period
 *
 * @author Emiliano Conde
 */
public class CancellationFeeTask extends PluggableTask implements IInternalEventsTask {
    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(CancellationFeeTask.class));

    public static final ParameterDescription PARAMETER_PRODUCT_CANCELLED = new ParameterDescription("product_cancelled", true, ParameterDescription.Type.STR);
    public static final ParameterDescription PARAMETER_PRODUCT_FEE = new ParameterDescription("product_fee", true, ParameterDescription.Type.STR);
    public static final ParameterDescription PARAMETER_USE_CANCELLED_QUANTITY = new ParameterDescription("use_cancelled_quantity", false, ParameterDescription.Type.STR);
    public static final ParameterDescription PARAMETER_PRO_RATE = new ParameterDescription("pro_rate", false, ParameterDescription.Type.STR);

    {
        descriptions.add(PARAMETER_PRODUCT_CANCELLED);
        descriptions.add(PARAMETER_PRODUCT_FEE);
        descriptions.add(PARAMETER_PRO_RATE);
        descriptions.add(PARAMETER_USE_CANCELLED_QUANTITY);
    }

    private enum EventType {
	    NEW_ACTIVE_UNTIL_EVENT,
        NEW_QUANTITY_EVENT
    }

    @SuppressWarnings("unchecked")
    private static final Class<Event> events[] = new Class[] {
	        NewActiveUntilEvent.class,
            NewQuantityEvent.class
    };

    public Class<Event>[] getSubscribedEvents() {
	    return events;
    }

    private Date newActiveUntil = null;
    private Date oldActiveUntil = null;
    private OrderDTO order = null;
    private Integer entityId = null;
    private Integer cancelled_id = null;
    private Integer fee_id = null;
    private boolean do_prorating = false;
    private boolean use_cancelled_quantity = false;

    public void process(Event event) throws PluggableTaskException {
		
		EventType eventType;

		// validate the type of the event
		if (event instanceof NewActiveUntilEvent) {
			NewActiveUntilEvent myEvent = (NewActiveUntilEvent) event;

			// if the new active until is later than the old one
			// or the new one is null don't process
			if (myEvent.getNewActiveUntil() == null
					|| (myEvent.getOldActiveUntil() != null && !myEvent.getNewActiveUntil().before(myEvent.getOldActiveUntil()))) {
				LOG.debug("New active until is not earlier than old one. Skipping cancellation fees. "
						+ "Order id" + myEvent.getOrderId());
				return;
			}

			order = new OrderDAS().find(myEvent.getOrderId());
			eventType = EventType.NEW_ACTIVE_UNTIL_EVENT;

		} else if (event instanceof NewQuantityEvent) {
			NewQuantityEvent myEvent = (NewQuantityEvent) event;

			// don't process if new quantity has increased instead of decreased
			if (myEvent.getNewQuantity().compareTo(myEvent.getOldQuantity()) > 0) {
				return;
			}

			// Create a copy of the order that had a line quantity changed
			// and add the changed line (with canceled quantity) to it.
			OrderDTO changedOrder = new OrderDAS().find(myEvent.getOrderId());
			order = new OrderDTO(changedOrder);

			// clear the order lines
			order.getLines().clear();

			// add the changed line
			OrderLineDTO line = new OrderLineDTO(myEvent.getOrderLine());
			line.setPurchaseOrder(order);
			order.getLines().add(line);

			// set quantity as the difference between the old and new quantities
			BigDecimal quantity = myEvent.getOldQuantity().subtract(myEvent.getNewQuantity());
			line.setQuantity(quantity);

			eventType = EventType.NEW_QUANTITY_EVENT;

		} else {
			throw new SessionInternalError("Can't process anything but a new active until event");
		}

		LOG.debug("Processing event " + event + " for cancellation fee");

		if (event != null && eventType == EventType.NEW_ACTIVE_UNTIL_EVENT) {
			NewActiveUntilEvent myEvent = (NewActiveUntilEvent) event;
			setNewActiveUntil(myEvent.getNewActiveUntil());
			setOldActiveUntil(myEvent.getOldActiveUntil());

		} else if (eventType == EventType.NEW_QUANTITY_EVENT) {
			// default to now. This is needed to calculate the number of periods canceled
			setNewActiveUntil(new Date());
			setOldActiveUntil(order.getActiveUntil());
		}
		
		entityId = event.getEntityId();
		validateParameters();
		
		// apply the fee
		for (OrderLineDTO line: order.getLines()) {
		    if (line.getItem().getId() == cancelled_id) {
			    applyFee(fee_id, line);
		    }
		}
	}

    private void validateParameters() {
        try {
            cancelled_id = Integer.parseInt(parameters.get(PARAMETER_PRODUCT_CANCELLED.getName()));
            fee_id = Integer.parseInt(parameters.get(PARAMETER_PRODUCT_FEE.getName()));
            do_prorating = Boolean.parseBoolean(parameters.get(PARAMETER_PRO_RATE.getName()));
            use_cancelled_quantity = Boolean.parseBoolean(parameters.get(PARAMETER_USE_CANCELLED_QUANTITY.getName()));

        } catch (NumberFormatException e) {
            LOG.error("Invalid paramters, they should be integers", e);
            throw new SessionInternalError("Invalid parameters for Cancellation fee plug-in. They should be integers", e);
        }

        if (new ItemDAS().findNow(cancelled_id) == null || new ItemDAS().findNow(fee_id) == null) {
            String message = "Invalid parameters, items " + cancelled_id + " and " + fee_id + " do not exist.";
            LOG.error(message);
            throw new SessionInternalError(message);
        }
	
    	LOG.debug("Parameters set to cancel = " + cancelled_id + " fee = " + fee_id);
    }

    private void applyFee(Integer itemId, Double quantity, Integer daysInPeriod, OrderLineDTO line) {
	    BigDecimal qty = new BigDecimal(quantity).setScale(Constants.BIGDECIMAL_SCALE, Constants.BIGDECIMAL_ROUND);
	    applyFee(itemId, qty, daysInPeriod, line);
    }

    private void applyFee(Integer itemId, BigDecimal quantity, Integer daysInPeriod, OrderLineDTO line) {
        ResourceBundle bundle;
        UserBL userBL;
        try {
            userBL = new UserBL(order.getBaseUserByUserId().getId());
            bundle = ResourceBundle.getBundle("entityNotifications", userBL.getLocale());
        } catch (Exception e) {
            throw new SessionInternalError("Error when doing credit", RefundOnCancelTask.class, e);
        }


        // calculate the number of periods that have been canceled
        BigDecimal periods;

        if (oldActiveUntil == null) {
            periods = new BigDecimal(1);
            LOG.info("Old active until not present. Period will be 1.");
        } else {
            long totalMills = oldActiveUntil.getTime() - newActiveUntil.getTime();
            BigDecimal periodMills = new BigDecimal(daysInPeriod)
                    .multiply(new BigDecimal(24))
                    .multiply(new BigDecimal(60))
                    .multiply(new BigDecimal(60))
                    .multiply(new BigDecimal(1000));

            periods = new BigDecimal(totalMills).divide(periodMills, Constants.BIGDECIMAL_SCALE, Constants.BIGDECIMAL_ROUND);

            if (!do_prorating) {
                periods = new BigDecimal(periods.intValue());
            }
        }

        if (BigDecimal.ZERO.equals(periods)) {
            LOG.debug("Not a single complete period cancelled: " + oldActiveUntil + " " + newActiveUntil);
            return;
        }

        if (use_cancelled_quantity) {
            quantity = line.getQuantity();
        }

        if (quantity == null || do_prorating) {
            quantity = new BigDecimal(1);
        }

        if (!do_prorating) {
            quantity = quantity.multiply(periods);
        }

        // now create a new order for the fee:
        // - one time
        // - item from the parameter * number of periods being cancelled
        OrderDTO feeOrder = new OrderDTO();
        feeOrder.setMetaFields(order.getMetaFields()); // get the metafields from the original order
        feeOrder.setBaseUserByUserId(order.getBaseUserByUserId());
        feeOrder.setCurrency(order.getCurrency());
        feeOrder.setNotes(bundle.getString("order.cancelationFee.notes") + " " + order.getId());
        feeOrder.setOrderPeriod(new OrderPeriodDAS().find(Constants.ORDER_PERIOD_ONCE));

        // now the line
        ItemDTO feeItem = new ItemDAS().find(itemId);
        OrderLineDTO feeLine = new OrderLineDTO();
        feeLine.setDeleted(0);
        feeLine.setDescription(feeItem.getDescription(userBL.getEntity().getLanguageIdField()));
        feeLine.setItem(feeItem);
        feeLine.setOrderLineType(new OrderLineTypeDAS().find(Constants.ORDER_LINE_TYPE_ITEM));
        feeLine.setPurchaseOrder(feeOrder);
        feeOrder.getLines().add(feeLine);
        feeLine.setQuantity(quantity);

        ItemBL itemBL = new ItemBL(itemId);
        BigDecimal price = itemBL.getPrice(order.getBaseUserByUserId().getId(), order.getCurrencyId(), quantity, entityId);
        if (do_prorating) {
            // when we pro-rate, we use quantity one and price equal to the whole
            // cancellation fee, including the fraction of a period
            feeLine.setPrice(price.multiply(periods));
        } else {
            feeLine.setPrice(price);
        }

        OrderBL orderBL = new OrderBL(feeOrder);
        try {
            orderBL.recalculate(entityId);
        } catch (ItemDecimalsException e) {
            throw new SessionInternalError(e);
        }

        Integer feeOrderId = orderBL.create(entityId, null, feeOrder);
        LOG.debug("New fee order created: " + feeOrderId + " for cancel of " + order.getId());
    }

    // convenience method for cancellation fee quantity of 1 and period of 30
    // days
    public void applyFee(Integer itemId, OrderLineDTO line) {
        applyFee(itemId, 1.0, 30, line);
    }

    public Date getNewActiveUntil() {
        return newActiveUntil;
    }

    public void setNewActiveUntil(Date activeSince) {
        this.newActiveUntil = activeSince;
    }

    public Date getOldActiveUntil() {
        return oldActiveUntil;
    }

    public void setOldActiveUntil(Date activeUntil) {
        this.oldActiveUntil = activeUntil;
    }
}
