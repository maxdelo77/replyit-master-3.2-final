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

package com.sapienter.jbilling.server.pricing.tasks;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.server.item.ItemBL;
import com.sapienter.jbilling.server.item.PricingField;
import com.sapienter.jbilling.server.item.db.PlanItemDTO;
import com.sapienter.jbilling.server.item.tasks.IPricing;
import com.sapienter.jbilling.server.item.tasks.PricingResult;
import com.sapienter.jbilling.server.order.OrderBL;
import com.sapienter.jbilling.server.order.Usage;
import com.sapienter.jbilling.server.order.UsageBL;
import com.sapienter.jbilling.server.order.db.OrderDTO;
import com.sapienter.jbilling.server.pluggableTask.PluggableTask;
import com.sapienter.jbilling.server.pluggableTask.TaskException;
import com.sapienter.jbilling.server.pluggableTask.admin.ParameterDescription;
import com.sapienter.jbilling.server.pricing.PriceModelBL;
import com.sapienter.jbilling.server.pricing.db.PriceModelDTO;
import com.sapienter.jbilling.server.user.CustomerPriceBL;
import com.sapienter.jbilling.server.user.UserBL;
import com.sapienter.jbilling.server.user.db.CustomerDTO;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import static com.sapienter.jbilling.server.pluggableTask.admin.ParameterDescription.Type.*;

/**
 * Pricing plug-in that calculates prices using the customer price map and PriceModelDTO
 * pricing strategies. This plug-in allows for complex pricing strategies to be applied
 * based on a customers subscribed plans, quantity purchased and the current usage.
 *
 * @author Brian Cowdery
 * @since 16-08-2010
 */
public class PriceModelPricingTask extends PluggableTask implements IPricing {

    /**
     * Type of usage calculation
     */
    private enum UsageType {
        /** Count usage from the user making the pricing request */
        USER,

        /** Count usage from the user that holds the price */
        PRICE_HOLDER;

        public static UsageType valueOfIgnoreCase(String value) {
            return UsageType.valueOf(value.trim().toUpperCase());
        }
    }

    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(PriceModelPricingTask.class));

    private static final Integer MAX_RESULTS = 1;

    private static ParameterDescription USE_ATTRIBUTES = new ParameterDescription("use_attributes", false, BOOLEAN);
    private static ParameterDescription USE_WILDCARDS = new ParameterDescription("use_wildcards", false, BOOLEAN);
    private static ParameterDescription USAGE_TYPE = new ParameterDescription("usage_type", false, STR);
    private static ParameterDescription SUB_ACCOUNT_USAGE = new ParameterDescription("include_sub_account_usage", false, BOOLEAN);
    private static ParameterDescription USE_NEXT_INVOICE_DATE = new ParameterDescription("use_next_invoice_date", false, BOOLEAN);

    private static final boolean DEFAULT_USE_ATTRIBUTES = false;
    private static final boolean DEFAULT_USE_WILDCARDS = false;
    private static final String DEFAULT_USAGE_TYPE = UsageType.PRICE_HOLDER.name();
    private static final boolean DEFAULT_SUB_ACCOUNT_USAGE = false;
    private static final boolean DEFAULT_USE_NEXT_INVOICE_DATE = false;

    {
        descriptions.add(USE_ATTRIBUTES);
        descriptions.add(USE_WILDCARDS);
        descriptions.add(USAGE_TYPE);
        descriptions.add(SUB_ACCOUNT_USAGE);
    }


    public BigDecimal getPrice(Integer itemId,
                               BigDecimal quantity,
                               Integer userId,
                               Integer currencyId,
                               List<PricingField> fields,
                               BigDecimal defaultPrice,
                               OrderDTO pricingOrder,
                               boolean singlePurchase) throws TaskException {

        LOG.debug("Calling PriceModelPricingTask with pricing order: %s", pricingOrder);
        LOG.debug("Pricing item %s, quantity %s - for user %s", itemId, quantity, userId);

        if (userId != null) {
            // get customer pricing model, use fields as attributes
            Map<String, String> attributes = getAttributes(fields);

            // price for customer making the pricing request
            SortedMap<Date, PriceModelDTO> models = getCustomerPriceModel(userId, itemId, attributes);

            // iterate through parents until a price is found.
            UserBL user = new UserBL(userId);
            CustomerDTO customer = user.getEntity() != null ? user.getEntity().getCustomer() : null;
            if (customer != null && customer.useParentPricing()) {
                while (customer.getParent() != null && (models == null || models.isEmpty())) {
                    customer = customer.getParent();

                    LOG.debug("Looking for price from parent user %s", customer.getBaseUser().getId());
                    models = getCustomerPriceModel(customer.getBaseUser().getId(), itemId, attributes);

                    if (models != null && !models.isEmpty()) LOG.debug("Found price from parent user: %s", models);
                }
            }

            // no customer price, this means the customer has not subscribed to a plan affecting this
            // item, or does not have a customer specific price set. Use the item default price.
            if (models == null || models.isEmpty()) {
                LOG.debug("No customer price found, using item default price model.");
                models = new ItemBL(itemId).getEntity().getDefaultPrices();
            }

            Date pricingDate = getPricingDate(pricingOrder);
            LOG.debug("Price date: %s", pricingDate);

            // apply price model
            if (models != null && !models.isEmpty()) {
                PriceModelDTO model = PriceModelBL.getPriceForDate(models, pricingDate);
                LOG.debug("Applying price model %s", model);

                Usage usage = null;
                PricingResult result = new PricingResult(itemId, quantity, userId, currencyId);
                for (PriceModelDTO next = model; next != null; next = next.getNext()) {
                    // fetch current usage of the item if the pricing strategy requires it
                    if (next.getStrategy().requiresUsage()) {
                        UsageType type = UsageType.valueOfIgnoreCase(getParameter(USAGE_TYPE.getName(), DEFAULT_USAGE_TYPE));
                        Integer priceUserId = customer != null ? customer.getBaseUser().getId() : userId;
                        usage = getUsage(type, itemId, userId, priceUserId, pricingOrder);

                        LOG.debug("Current usage of item %s : %s", itemId, usage);
                    } else {
                        LOG.debug("Pricing strategy %s does not require usage.", next.getType());
                    }

                    next.applyTo(pricingOrder, result.getQuantity(), result, fields, usage, singlePurchase, pricingDate);
                    LOG.debug("Price discovered: %s", result.getPrice());
                }

                return result.getPrice();
            }
        }

        LOG.debug("No price model found, using default price.");
        return defaultPrice;
    }

    /**
     * Fetches a price model for the given pricing request.
     *
     * If the parameter "use_attributes" is set, the given pricing fields will be used as
     * query attributes to determine the pricing model.
     *
     * If the parameter "use_wildcards" is set, the price model lookup will allow matches
     * on wildcard attributes (stored in the database as "*").
     *
     * @param userId id of the user pricing the item
     * @param itemId id of the item to price
     * @param attributes attributes from pricing fields
     * @return found list of dated pricing models, or null if none found
     */
    public SortedMap<Date, PriceModelDTO> getCustomerPriceModel(Integer userId, Integer itemId, Map<String, String> attributes) {
        CustomerPriceBL customerPriceBl = new CustomerPriceBL(userId);

        if (getParameter(USE_ATTRIBUTES.getName(), DEFAULT_USE_ATTRIBUTES) && !attributes.isEmpty()) {
            if (getParameter(USE_WILDCARDS.getName(), DEFAULT_USE_WILDCARDS)) {
                LOG.debug("Fetching customer price using wildcard attributes: %s", attributes);
                List<PlanItemDTO> items = customerPriceBl.getPricesByWildcardAttributes(itemId, attributes, MAX_RESULTS);
                return !items.isEmpty() ? items.get(0).getModels() : null;

            } else {
                LOG.debug("Fetching customer price using attributes: %s", attributes);
                List<PlanItemDTO> items = customerPriceBl.getPricesByAttributes(itemId, attributes, MAX_RESULTS);
                return !items.isEmpty() ? items.get(0).getModels() : null;
            }

        } else {
            // not configured to query prices with attributes, or no attributes given
            // determine customer price normally
            LOG.debug("Fetching customer price without attributes (no PricingFields given or 'use_attributes' = false)");
            PlanItemDTO item = customerPriceBl.getPrice(itemId);
            return item != null ? item.getModels() : null;
        }
    }

    /**
     * Returns the total usage of the given item for the set UsageType, and optionally include charges
     * made to sub-accounts in the usage calculation.
     *
     * @param type usage type to query, may use either USER or PRICE_HOLDER to determine usage
     * @param itemId item id to get usage for
     * @param userId user id making the price request
     * @param priceUserId user holding the pricing plan
     * @param pricingOrder working order (order being edited/created)
     * @return usage for customer and usage type
     */
    private Usage getUsage(UsageType type, Integer itemId, Integer userId, Integer priceUserId, OrderDTO pricingOrder) {
        UsageBL usage;
        switch (type) {
            case USER:
                usage = new UsageBL(userId, pricingOrder);
                break;

            default:
            case PRICE_HOLDER:
                usage = new UsageBL(priceUserId, pricingOrder);
                break;
        }

        // include usage from sub account?
        if (getParameter(SUB_ACCOUNT_USAGE.getName(), DEFAULT_SUB_ACCOUNT_USAGE)) {
            return usage.getSubAccountItemUsage(itemId);
        } else {
            return usage.getItemUsage(itemId);
        }
    }

    /**
     * Convert pricing fields into price model query attributes.
     *
     * @param fields pricing fields to convert
     * @return map of string attributes
     */
    public Map<String, String> getAttributes(List<PricingField> fields) {
        Map<String, String> attributes = new HashMap<String, String>();
        if (fields != null) {
            for (PricingField field : fields)
                attributes.put(field.getName(), field.getStrValue());
        }
        return attributes;
    }

    /**
     * Return the date of this pricing request. The pricing date will be the "active" date of the pricing
     * order, or the next invoice date if the "use_next_invoice_date" parameter is set to true.
     *
     * If pricing order is null, then today's date will be used.
     *
     * @param pricingOrder pricing order
     * @return date to use for this pricing request
     */
    public Date getPricingDate(OrderDTO pricingOrder) {
        if (pricingOrder != null) {
            if (getParameter(USE_NEXT_INVOICE_DATE.getName(), DEFAULT_USE_NEXT_INVOICE_DATE)) {
                // use next invoice date of this order
                return new OrderBL(pricingOrder).getInvoicingDate();

            } else {
                // use order active since date, or created date if no active since
                return pricingOrder.getPricingDate();
            }

        } else {
            // no pricing order, use today
            return new Date();
        }
    }
}
