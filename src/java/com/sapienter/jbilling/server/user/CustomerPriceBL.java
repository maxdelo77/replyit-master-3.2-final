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

package com.sapienter.jbilling.server.user;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.server.item.db.PlanItemDAS;
import com.sapienter.jbilling.server.item.db.PlanItemDTO;
import com.sapienter.jbilling.server.user.db.CustomerDTO;
import com.sapienter.jbilling.server.user.db.CustomerPriceDAS;
import com.sapienter.jbilling.server.user.db.CustomerPriceDTO;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Business logic for customer to plan item (plan item pricing) mappings.
 *
 * This class handles the application of plan pricing to a customer. Plan item prices can
 * be added and removed from a customer to either grant or revoke access to the plans
 * special pricing for an item.
 *
 * Customer specific pricing can be added by saving a {@link PlanItemDTO} that has no
 * association to a plan. 
 *
 * @see com.sapienter.jbilling.server.pricing.tasks.PriceModelPricingTask
 *
 * @author Brian Cowdery
 * @since 30-08-2010
 */
public class CustomerPriceBL {
    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(CustomerPriceBL.class));

    private CustomerPriceDAS customerPriceDas;
    private UserBL userBl;

    private CustomerDTO customer;
    private Integer userId;
    private CustomerPriceDTO price;


    public CustomerPriceBL() {
        _init();
    }
    
    public CustomerPriceBL(Integer userId) {
        _init();
        setUserId(userId);
    }

    public CustomerPriceBL(CustomerDTO customer) {
        _init();
        this.customer = customer;
        this.userId = customer.getBaseUser().getId();
    }

    public CustomerPriceBL(Integer userId, Integer planItemId) {
        this(userId);
        setCustomerPrice(planItemId);
    }

    public CustomerPriceBL(CustomerDTO customer, Integer planItemId) {
        this(customer);
        setCustomerPrice(planItemId);

    }

    private void _init() {
        customerPriceDas = new CustomerPriceDAS();
        userBl = new UserBL();
    }

    public void setUserId(Integer userId) {
        userBl.set(userId);
        this.customer = userBl.getEntity().getCustomer();
        this.userId = userId;
    }

    public void setCustomerPrice(Integer planItemId) {
        this.price = customerPriceDas.find(userId, planItemId);

    }

    public void flush() {
        customerPriceDas.flush();
    }

    public CustomerDTO getCustomer() {
        return customer;
    }

    public Integer getUserId() {
        return userId;
    }

    public CustomerPriceDTO getEntity() {
        return price;
    }


    /**
     * Adds the given list of plan item prices to this customer, effectively
     * subscribing the customer to a plan and applying special item pricing.
     *
     * @param planItems plan items to add
     * @return list of saved customer prices
     */
    public List<CustomerPriceDTO> addPrices(List<PlanItemDTO> planItems) {
        List<CustomerPriceDTO> saved = new ArrayList<CustomerPriceDTO>(planItems.size());
        for (PlanItemDTO planItem : planItems)
            saved.add(addPrice(planItem));

        LOG.debug("Saved " + saved.size() + " customer price entries.");
        return saved;
    }

    /**
     * Add a plan item price to this customer, applying a special price for an item.
     *
     * If the given PlanItemDTO is not linked to a PlanDTO, then this price
     * will be treated as a customer-specific price that applies only to
     * this customer.
     *
     * @param planItem plan item to add
     * @return saved customer price
     */
    public CustomerPriceDTO addPrice(PlanItemDTO planItem) {
        return create(planItem);
    }

    public CustomerPriceDTO create(PlanItemDTO planItem) {
        CustomerPriceDTO dto = new CustomerPriceDTO();
        dto.setCustomer(customer);

        planItem = new PlanItemDAS().save(planItem);
        dto.setPlanItem(planItem);

        this.price = customerPriceDas.save(dto);
        return this.price;
    }

    public void update(PlanItemDTO planItem) {
        if (price != null) {
            planItem = new PlanItemDAS().save(planItem);
            price.setPlanItem(planItem);

            customerPriceDas.save(price);
        } else {

            LOG.error("Cannot update, CustomerPriceDTO not found or not set!");
        }
    }

    public void delete() {
        if (price != null) {
            customerPriceDas.delete(price);
        } else {
            LOG.error("Cannot delete, CustomerPriceDTO not found or not set!");
        }

    }

    /**
     * Removes all plan item prices from this customer for the given plan id.
     *
     * @param planId id of plan
     */
    public void removePrices(Integer planId) {
        // batch delete by plan id, only executes 1 query
        int deleted = customerPriceDas.deletePrices(userId, planId);
        LOG.debug("Removed " + deleted + " customer price entries for plan " + planId);
    }

    /**
     * Removes the given list of plan item prices from this customer, effectively
     * un-subscribing the customer from a plan and revoking the special item pricing.
     *
     * @param planItems plan items to remove
     */
    public void removePrices(List<PlanItemDTO> planItems) {
        // executes multiple queries to delete each plan item from the customer price map
        int deleted = customerPriceDas.deletePrices(userId, planItems);
        LOG.debug("Removed " + deleted + " customer price entries for " + planItems.size() + " plan items.");
    }

    /**
     * Removes a plan item price from this customer, revoking the special price for an item.
     *
     * @param planItem plan item to remove
     */
    public void removePrice(PlanItemDTO planItem) {
        int deleted = customerPriceDas.deletePrice(userId, planItem.getId());
        LOG.debug("Removed " + deleted + " customer price entries for plan item: " + planItem);
    }

    /**
     * Judiciously removes all prices from the customer pricing table, ensuring that no
     * customer subscriptions, orphaned prices and foreign keys exist on the given list
     * of plan items.
     *
     * @param planItems plan items to remove from customer pricing
     */
    public void removeAllPrices(List<PlanItemDTO> planItems) {
        int deleted = customerPriceDas.deletePricesByItems(planItems);
        LOG.debug("Removed " + deleted + " customer price entries for " + planItems.size() + " plan items.");
    }

    /**
     * Returns the customer's price for the given item. This method returns null
     * If the customer does not have any special pricing for the given item (customer
     * is not subscribed to a plan affecting the items price, or no customer-specific
     * price found).
     *
     * @param itemId item to price
     * @return customer price, null if no special price found
     */
    public PlanItemDTO getPrice(Integer itemId) {
        return customerPriceDas.findPriceByItem(userId, itemId);
    }

    /**
     * Returns a list of all customer-specific prices that apply only to this customer.
     * @return list of prices, empty list if none
     */
    public List<PlanItemDTO> getCustomerSpecificPrices() {
        return customerPriceDas.findAllCustomerSpecificPrices(userId);
    }

    /**
     * Returns a list of all prices for this customer. This will include customer-specific prices
     * and prices applied because the customer has subscribed to a plan.
     *
     * @return list of prices, empty list if none
     */
    public List<PlanItemDTO> getCustomerPrices() {
        return customerPriceDas.findAllCustomerPrices(userId);
    }

    /**
     * Returns a list of all prices for this customer and the given item id. This will include customer-specific
     * prices and prices applied because the customer has subscribed to a plan.
     *
     * @param itemId item id
     * @return list of prices, empty list if none
     */
    public List<PlanItemDTO> getCustomerPrices(Integer itemId) {
        return customerPriceDas.findAllCustomerPricesByItem(userId, itemId);
    }


    /**
     * Returns the customer's price for the given item and pricing attributes.
     *
     * @see CustomerPriceDAS#findPriceByAttributes(Integer, Integer, java.util.Map, Integer)
     *
     * @param itemId id of item being priced
     * @param attributes attributes of pricing to match
     * @return list of found customer prices, empty list if none found.
     */
    public List<PlanItemDTO> getPricesByAttributes(Integer itemId, Map<String, String> attributes) {
        return customerPriceDas.findPriceByAttributes(userId, itemId, attributes, null);
    }

    /**
     * Returns the customer's price for the given item and pricing attributes, limiting the number
     * of results returned (queried from database).
     *
     * @see CustomerPriceDAS#findPriceByAttributes(Integer, Integer, java.util.Map, Integer)
     *
     * @param itemId id of item being priced
     * @param attributes attributes of pricing to match
     * @param maxResults limit database query return results
     * @return list of found customer prices, empty list if none found.
     */
    public List<PlanItemDTO> getPricesByAttributes(Integer itemId, Map<String, String> attributes, Integer maxResults) {
        return customerPriceDas.findPriceByAttributes(userId, itemId, attributes, maxResults);
    }

    /**
     * Returns the customer's price for the given item and pricing attributes, allowing for wildcard
     * matches of pricing attributes.
     *
     * @see CustomerPriceDAS#findPriceByWildcardAttributes(Integer, Integer, java.util.Map, Integer)
     *
     * @param itemId id of item being priced
     * @param attributes attributes of pricing to match
     * @return list of found customer prices, empty list if none found.
     */
    public List<PlanItemDTO> getPricesByWildcardAttributes(Integer itemId, Map<String, String> attributes) {
        return customerPriceDas.findPriceByWildcardAttributes(userId, itemId, attributes, null);
    }

    /**
     * Returns the customer's price for the given item and pricing attributes, allowing for wildcard
     * matches of plan attributes and limiting the number of results returned (queried from database).
     *
     * @see CustomerPriceDAS#findPriceByWildcardAttributes(Integer, Integer, java.util.Map, Integer)
     *
     * @param itemId id of item being priced
     * @param attributes attributes of plan pricing to match
     * @param maxResults limit database query return results
     * @return list of found customer prices, empty list if none found.
     */
    public List<PlanItemDTO> getPricesByWildcardAttributes(Integer itemId, Map<String, String> attributes, Integer maxResults) {
        return customerPriceDas.findPriceByWildcardAttributes(userId, itemId, attributes, maxResults);
    }
}
