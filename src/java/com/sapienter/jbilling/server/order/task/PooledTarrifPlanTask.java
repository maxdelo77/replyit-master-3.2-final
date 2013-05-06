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

import com.sapienter.jbilling.common.FormatLogger;
//import org.apache.log4j.Logger;
import com.sapienter.jbilling.server.pricing.PriceModelBL;
import org.apache.log4j.Logger;
import com.sapienter.jbilling.server.pricing.db.PriceModelStrategy;

import com.sapienter.jbilling.server.item.db.ItemDTO;
import com.sapienter.jbilling.server.item.db.PlanDTO;
import com.sapienter.jbilling.server.item.db.PlanItemDTO;
import com.sapienter.jbilling.server.order.db.OrderDTO;
import com.sapienter.jbilling.server.order.db.OrderLineDTO;
import com.sapienter.jbilling.server.order.event.NewQuantityEvent;
import com.sapienter.jbilling.server.order.event.NewOrderEvent;
import com.sapienter.jbilling.server.pluggableTask.PluggableTask;
import com.sapienter.jbilling.server.pluggableTask.admin.ParameterDescription;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskException;
import com.sapienter.jbilling.server.pricing.db.PriceModelDTO;
import com.sapienter.jbilling.server.pricing.util.AttributeUtils;
import com.sapienter.jbilling.server.system.event.Event;
import com.sapienter.jbilling.server.system.event.task.IInternalEventsTask;
import com.sapienter.jbilling.server.user.db.CustomerDTO;
import com.sapienter.jbilling.server.user.db.UserDTO;
import com.sapienter.jbilling.server.order.OrderLineBL;
import com.sapienter.jbilling.server.order.db.OrderDAS;

/**
 * @author Vikas Bodani
 * @since 17 March 2011
 */
public class PooledTarrifPlanTask extends PluggableTask implements IInternalEventsTask {

	private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(PooledTarrifPlanTask.class));

	private static final ParameterDescription PARAMETER_POOL_ADD_PRODUCT_ID 
		= new ParameterDescription("pool_add_product_id", true, ParameterDescription.Type.STR);
	private static final ParameterDescription PARAMETER_POOL_RECEIVE_PRODUCT_ID = 
		new ParameterDescription("pool_receive_product_id", true, ParameterDescription.Type.STR);
	private static final ParameterDescription PARAMETER_POOL_USER_PRODUCT_ID = 
		new ParameterDescription("pool_user_product_id", true, ParameterDescription.Type.STR);
	private static final ParameterDescription PARAMETER_QUANTITY_MULTIPLIER = 
		new ParameterDescription("quantity_multiplier", true, ParameterDescription.Type.STR);

	//initializer for pluggable params
    { 
    	descriptions.add(PARAMETER_POOL_RECEIVE_PRODUCT_ID);
    	descriptions.add(PARAMETER_POOL_USER_PRODUCT_ID);
    	descriptions.add(PARAMETER_QUANTITY_MULTIPLIER);
    	descriptions.add(PARAMETER_POOL_ADD_PRODUCT_ID);
    }
    
	
	@SuppressWarnings("unchecked")
    private static final Class<Event> events[] = new Class[] { 
        NewQuantityEvent.class,
        NewOrderEvent.class
    };
	
	
	public Class<Event>[] getSubscribedEvents() {
		return events;
	}

	/**
	 * 
	 */
	public void process(Event event) throws PluggableTaskException {
		LOG.debug(PARAMETER_POOL_ADD_PRODUCT_ID + " value is " + parameters.get(PARAMETER_POOL_ADD_PRODUCT_ID.getName()));
		LOG.debug(PARAMETER_POOL_USER_PRODUCT_ID + " value is " + parameters.get(PARAMETER_POOL_USER_PRODUCT_ID.getName()));
		LOG.debug(PARAMETER_POOL_RECEIVE_PRODUCT_ID + " value is " + parameters.get(PARAMETER_POOL_RECEIVE_PRODUCT_ID.getName()));
		LOG.debug(PARAMETER_QUANTITY_MULTIPLIER + " value is " + parameters.get(PARAMETER_QUANTITY_MULTIPLIER.getName()));
		
		if (event instanceof NewQuantityEvent) {
			LOG.debug("NewQuantityEvent");
            NewQuantityEvent nq = (NewQuantityEvent) event;
            //The purpose of using getOrderLine
            //is to strictly find the matching Item ID
            handleUpdateAction(nq.getOrderLine(), event);
		} else if (event instanceof NewOrderEvent) {
			LOG.debug("NewOrderEvent");
			NewOrderEvent no= (NewOrderEvent) event;
			for(OrderLineDTO orderLine: no.getOrder().getLines()) {
				handleUpdateAction(orderLine, event); 
			}
		}
	}
	
	/**
	 * 
	 * @param orderLine
	 * @param event
	 * @throws PluggableTaskException
	 */
	private void handleUpdateAction(OrderLineDTO orderLine, Event event) throws PluggableTaskException { 
		try { 
			LOG.debug("Order Line ID %s", orderLine.getId());
			LOG.debug("orderLine.getItemId()=%s", orderLine.getItemId());
            //check if the item of this Order matches the item that needs to be pooled/added
            if (orderLine.getItemId().equals(new Integer((String) parameters.get(PARAMETER_POOL_ADD_PRODUCT_ID.getName())))) {
            	updatePooledQuantity(orderLine, event);
            } else if (orderLine.getItemId().equals(new Integer((String) parameters.get(PARAMETER_POOL_USER_PRODUCT_ID.getName())))) {
            	passUsageToParentAccount(orderLine, event);
            }
        } catch (NumberFormatException e) {
        	throw new PluggableTaskException("PooledTarrifPlanTask parameter values must be an integer!", e);
        }
	}
	
	/**
	 * 
	 * @param orderLine
	 * @param event
	 */
	private void updatePooledQuantity(OrderLineDTO orderLine, Event event) {
		LOG.debug("updatePooledQuantity");

		UserDTO user= getUserFromEvent(event);
		
		//get customer 
		CustomerDTO customer= user.getCustomer();
		
		//if its okay to invoice this customer, return
		if (customer.getParent() == null || customer.getInvoiceChild() == null ||
                customer.getInvoiceChild().intValue() > 0 ) {
			//do nothing, the customer is invoiced as child
			LOG.debug("Returning, doing nothing. Customer is invoiced as child.");
			return;
		}
		//else invoice to parent's current order
		CustomerDTO parent= customer.getParent();
		user= parent.getBaseUser();
		
    	if (null != user) { 
    		//find if one of the items of the user's orders matches the item that receives the pool
    		outer:
    		for (OrderDTO order: user.getOrders()) {
    			LOG.debug("User %s order %s", user.getId(), order.getId());
                Date pricingDate = order.getActiveSince() != null ? order.getActiveSince() : order.getCreateDate();

    			for(OrderLineDTO line: order.getLines()) {
    				LOG.debug("Line Item iD: %s", line.getItemId() );
    				ItemDTO lineItem= line.getItem();
    				if ( null != lineItem && lineItem.getPlans().size() > 0 ) {
    					//we need to match the receive item it with the item id of the PlanItem of the orders items
    					for (PlanDTO plan: lineItem.getPlans()) {
    						for (PlanItemDTO planItem: plan.getPlanItems()) {
    							LOG.debug("Plan Items Item ID: %s", planItem.getItem().getId());
    							//check if planItems itemid matches the receive product id
    							if (planItem.getItem().getId() == Integer.parseInt(parameters.get(PARAMETER_POOL_RECEIVE_PRODUCT_ID.getName())) ) {
                                    PriceModelDTO model = PriceModelBL.getPriceForDate(planItem.getModels(), pricingDate);

    								LOG.debug("Found matching item id to update included quantity");
    								LOG.debug("PlanItem has PriceModelDTO. %s", model.getType());

    								//check if the plan item has graduated pricing
    								if (model.getType().equals(PriceModelStrategy.GRADUATED)) {
    									LOG.debug("Strategy Graduated");
    									//check if Item Id matches
    	        						BigDecimal multiPlierQty= new BigDecimal((String) parameters.get(PARAMETER_QUANTITY_MULTIPLIER.getName()));
    	        						BigDecimal included = AttributeUtils.getDecimal(model.getAttributes(), "included");
    	        						
    	        						//careful with the call below, the orderLine passed is used to 
    	        						//determine additional quantity added in case of NewOrderEvent
    	        						BigDecimal addlQty= determineAdditionalQuantity(event, orderLine);
    	        						
    	        						LOG.debug("Adding amount %s", multiPlierQty.multiply( addlQty ));
    	        						LOG.debug("included was %s", included );
    	        						
    	        						//add or delete from pool in units of multiplierQuantity for each quantity added or deleted
    	        						included= included.add( multiPlierQty.multiply( addlQty ) );
    	        						
    	        						LOG.debug("included is %s", included );
    	        						
    	        						//set new included quantity to the existing model
    	        						model.getAttributes().put("included", included.toPlainString());
    	        						break outer;
    								}
    							}
    						}
    					}
    				}
    			}
    		}
    	}
	}

	/**
	 * 
	 * @param line
	 * @param event
	 */
	private void passUsageToParentAccount(OrderLineDTO line, Event event) {
		LOG.debug("Method: passUsageToParentAccount");
		
		//get the user for this order
		UserDTO user= getUserFromEvent(event);
		
		//get customer 
		CustomerDTO customer= user.getCustomer();
		
		//if its okay to invoice this customer, return
		if ( customer.getInvoiceChild().intValue() > 0 ) {
			//do nothing, the customer is invoiced as child
			LOG.debug("Returning, doing nothing. Customer is invoiced as child.");
			return;
		}
		//else invoice to parent's current order
		CustomerDTO parent= customer.getParent();
		user= parent.getBaseUser();
	
		//get this use's graduated pricing order which contains a pooled tarrif plan
		OrderDTO order= getUsersGraduatedPricingOrder(user);
		if (null != order) {
			BigDecimal addlQty= determineAdditionalQuantity(event, line);
			if (addlQty.compareTo(BigDecimal.ZERO) > 0 )
			{
				//below, toAdd line is same as Order Line for NewOrderEvent.
				//Whereas in case of NewQuantityEvent, we have to be careful
				//to get the right OrderLineDTO
				OrderLineDTO toAdd= line;
				if (event instanceof NewQuantityEvent) {
					NewQuantityEvent nq = (NewQuantityEvent) event;
					if (nq.getOldQuantity().intValue() == 0 ) { 
						toAdd=nq.getOrderLine();
					} else {
						toAdd= nq.getNewOrderLine();
					}
				}
				LOG.debug("Item ID: %s", toAdd.getItemId());
				boolean orderExists= false;
				for(OrderLineDTO orderLine: order.getLines()) {
					LOG.debug("Order Line Item: %s", (null != orderLine? orderLine.getItemId() : null) );
					if (orderLine.getItemId().intValue() == Integer.parseInt((String) parameters.get(PARAMETER_POOL_USER_PRODUCT_ID.getName()))) {
						//found an existing orderLine with the usage item, update its quantity
						orderExists=true;
						orderLine.setQuantity(orderLine.getQuantity().add(addlQty));
						LOG.debug("New Quantity set to %s", orderLine.getQuantity());
					}
				}
				//if a matching OrderLineItem was not found, add this line to the order 
				if (!orderExists) { 
					addLineToOrder(order, toAdd);
				}
			}
		}
	}
	
	/**
	 * Get the Order that we want to update for both pool expansion/contraction
	 * as well as for reflecting the sum of Usage of sub-accounts 
	 * @param user
	 * @return
	 */
	private OrderDTO getUsersGraduatedPricingOrder(UserDTO user) {
		
		if (null != user) { 
    		//find if one of the items of the user's orders matches the item that receives the pool
    		outer:
    		for (OrderDTO order: user.getOrders()) {
    			LOG.debug("User %s order %s", user.getId(), order.getId());
                Date pricingDate = order.getActiveSince() != null ? order.getActiveSince() : order.getCreateDate();

    			for(OrderLineDTO line: order.getLines()) {
    				LOG.debug("Line Item iD: %s", line.getItemId() );
    				ItemDTO lineItem= line.getItem();
    				if ( null != lineItem && lineItem.getPlans().size() > 0 ) {
    					//we need to match the receive item it with the item id of the PlanItem of the orders items
    					for (PlanDTO plan: lineItem.getPlans()) {
    						for (PlanItemDTO planItem: plan.getPlanItems()) {
    							LOG.debug("Plan Items Item ID: %s", planItem.getItem().getId());
                                PriceModelDTO model = PriceModelBL.getPriceForDate(planItem.getModels(), pricingDate);


    							//check if planItems itemid matches the receive product id
    							if (planItem.getItem().getId() == Integer.parseInt(parameters.get(PARAMETER_POOL_RECEIVE_PRODUCT_ID.getName())) ) {
    								LOG.debug("Found matching item id to update included quantity");
    								LOG.debug("PlanItem has PriceModelDTO. %s", model.getType());
    								//check if the plan item has graduated pricing
    								if (model.getType().equals(PriceModelStrategy.GRADUATED)) {
    									return order;
    								}
    							}
    						}
    					}
    				}
    			}
    		}
		}
		
		return null;
	}
	
	/**
	 * 
	 */
	private static UserDTO getUserFromEvent(Event event) {
		UserDTO user= null;
		if (event instanceof NewQuantityEvent) {
            NewQuantityEvent nq = (NewQuantityEvent) event;
            OrderDTO order= new OrderDAS().find(nq.getOrderId());
            user= order.getBaseUserByUserId();
		} else if (event instanceof NewOrderEvent) {
			user= ((NewOrderEvent)event).getOrder().getBaseUserByUserId();
		}
		return user;
	}
	
 	/**
	 * 
	 * @param order
	 * @param toAdd
	 */
	private static void addLineToOrder(OrderDTO order, OrderLineDTO toAdd) {
		OrderLineDTO temp= new OrderLineDTO();
		temp.setOrderLineType(toAdd.getOrderLineType());
        temp.setItem(toAdd.getItem());
        temp.setAmount(toAdd.getAmount());
        temp.setQuantity(toAdd.getQuantity());
        temp.setPrice(toAdd.getPrice());
        temp.setCreateDatetime(new java.util.Date());
        LOG.debug("Adding new Order Line.. %s", temp);
		OrderLineBL.addLine(order, temp, false);
	}
	
	
	/**
	 * 
	 * @param event
	 * @param line line passed here is utilized only in case of a NewOrderEvent
	 * @return
	 */
	//Only in a new Order event, the quantity will come from the Order Line itself.
	private static BigDecimal determineAdditionalQuantity(Event event, OrderLineDTO line) {
		BigDecimal retVal= null;
		if (event instanceof NewQuantityEvent) {
            NewQuantityEvent nq = (NewQuantityEvent) event;
            LOG.debug("New quantity: %s Old Quantity: %s", nq.getNewQuantity(), nq.getOldQuantity());
            retVal= nq.getNewQuantity().subtract(nq.getOldQuantity());
		} else if (event instanceof NewOrderEvent) {
			retVal= line.getQuantity();
		}
		return retVal;
	}
	
}
