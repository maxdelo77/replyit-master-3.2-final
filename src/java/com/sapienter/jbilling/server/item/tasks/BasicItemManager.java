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
package com.sapienter.jbilling.server.item.tasks;

import java.math.BigDecimal;
import java.util.List;

import com.sapienter.jbilling.common.Constants;
import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.server.item.ItemBL;
import com.sapienter.jbilling.server.item.ItemDecimalsException;
import com.sapienter.jbilling.server.item.PricingField;
import com.sapienter.jbilling.server.item.db.ItemDTO;
import com.sapienter.jbilling.server.mediation.Record;
import com.sapienter.jbilling.server.order.OrderBL;
import com.sapienter.jbilling.server.order.db.OrderDTO;
import com.sapienter.jbilling.server.order.db.OrderLineDTO;
import com.sapienter.jbilling.server.pluggableTask.PluggableTask;
import com.sapienter.jbilling.server.pluggableTask.TaskException;
import com.sapienter.jbilling.server.rule.RulesBaseTask;
import org.apache.log4j.Logger;

import java.util.ArrayList;

public class BasicItemManager extends RulesBaseTask implements IItemPurchaseManager {

    // for the rules task, needed due to some class hierarchy problems further up
    // todo: remove when all rules plug-ins are deleted.
    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(BasicItemManager.class));
    protected FormatLogger getLog() { return LOG; }

    private ItemDTO item = null;
    private OrderLineDTO latestLine = null;

    public void addItem(Integer itemId, BigDecimal quantity, Integer languageId, Integer userId, Integer entityId,
                        Integer currencyId, OrderDTO order, List<Record> records) throws TaskException {

        LOG.debug("Adding %s of item %s to order %s", quantity, itemId, order);

        ItemBL item = new ItemBL(itemId);

        // validate decimal quantity
        if (quantity.remainder(Constants.BIGDECIMAL_ONE).compareTo(BigDecimal.ZERO) > 0) {
            if (item.getEntity().getHasDecimals().equals(0)) {
                latestLine = null;
                throw new ItemDecimalsException("Item " + itemId + " does not allow decimal quantities.");
            }
        }

        // build the order line
        OrderLineDTO newLine = getOrderLine(itemId, languageId, userId, currencyId, quantity, entityId, order, records);

        // check if line already exists on the order & update
        OrderLineDTO oldLine = order.getLine(itemId);
        if (oldLine == null) {
            addNewLine(order, newLine);
        } else {
            updateExistingLine(order, newLine, oldLine);
        }
    }

    /**
     * Add a new line to the order
     *
     * @param order order
     * @param newLine new line to add
     */
    protected void addNewLine(OrderDTO order, OrderLineDTO newLine) {
        LOG.debug("Adding new line to order: %s", newLine);

        newLine.setPurchaseOrder(order);
        order.getLines().add(newLine);

        this.latestLine = newLine;
    }

    /**
     * Update an existing line on the order with the quantity and dollar amount of the new line.
     *
     * @param order order
     * @param newLine new order line
     * @param oldLine existing order line to be updated
     */
    protected void updateExistingLine(OrderDTO order, OrderLineDTO newLine, OrderLineDTO oldLine) {
        LOG.debug("Updating existing order with line quantity & amount: %s", newLine);

        BigDecimal quantity = oldLine.getQuantity().add(newLine.getQuantity());
        oldLine.setQuantity(quantity);

        BigDecimal amount = oldLine.getAmount().add(newLine.getAmount());
        oldLine.setAmount(amount);

        this.latestLine = oldLine;
    }

    /**
     * Builds a new order line for the given item, currency and user. The item will be priced according
     * to the quantity purchased, the order it is being added to and the user's own prices.
     *
     * @see ItemBL#getDTO(Integer, Integer, Integer, Integer, java.math.BigDecimal, com.sapienter.jbilling.server.order.db.OrderDTO)
     * @see ItemBL#getPrice(Integer, Integer, java.math.BigDecimal, Integer, com.sapienter.jbilling.server.order.db.OrderDTO)
     *
     * @param itemId item id
     * @param languageId language id
     * @param userId user id
     * @param currencyId currency id
     * @param quantity quantity being purchased
     * @param entityId entity id
     * @param order order the line will be added to
     * @param records mediation records containing pricing fields
     * @return new order line
     */
    protected OrderLineDTO getOrderLine(Integer itemId, Integer languageId, Integer userId, Integer currencyId,
                                        BigDecimal quantity, Integer entityId, OrderDTO order, List<Record> records) {

        // item BL with pricing fields
        ItemBL itemBl = new ItemBL(itemId);
        if (records != null) {
            List<PricingField> fields = new ArrayList<PricingField>();
            for (Record record : records) {
                fields.addAll(record.getFields());
            }

            LOG.debug("Including %d field(s) for pricing.", fields.size());
            itemBl.setPricingFields(fields);
        }

        // get the item with the price populated for the quantity being purchased
        this.item = itemBl.getDTO(languageId, userId, entityId, currencyId, quantity, order);
        LOG.debug("Item %s priced as %s", itemId, item.getPrice());

        // build the order line
        OrderLineDTO line = new OrderLineDTO();
        line.setItem(item);
        line.setDescription(item.getDescription());
        line.setQuantity(quantity != null ? quantity : BigDecimal.ZERO);

        // set line price
        if (item.getPercentage() != null) {
            line.setPrice(item.getPercentage());
        } else {
            line.setPrice(item.getPrice());
        }

        // calculate total line dollar amount
        if (item.getPercentage() != null) {
            line.setAmount(item.getPercentage());
        } else {
            line.setAmount(line.getPrice().multiply(line.getQuantity()));
        }

        // round dollar amount
        line.setAmount(line.getAmount().setScale(Constants.BIGDECIMAL_SCALE, Constants.BIGDECIMAL_ROUND));

        line.setDeleted(0);
        line.setTypeId(item.getOrderLineTypeId());
        line.setEditable(OrderBL.lookUpEditable(item.getOrderLineTypeId()));
        line.setDefaults();

        LOG.debug("Built new order line: %s", line);

        return line;
    }

    public ItemDTO getItem() {
        return item;
    }

    public OrderLineDTO getLatestLine() {
        return latestLine;
    }
}
