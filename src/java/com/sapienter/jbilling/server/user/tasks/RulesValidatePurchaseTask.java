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

package com.sapienter.jbilling.server.user.tasks;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.server.rule.RulesBaseTask;
import org.apache.log4j.Logger;
import org.drools.KnowledgeBase;
import org.drools.runtime.StatelessKnowledgeSession;

import com.sapienter.jbilling.server.item.PricingField;
import com.sapienter.jbilling.server.item.db.ItemDTO;
import com.sapienter.jbilling.server.item.tasks.Subscription;
import com.sapienter.jbilling.server.order.OrderBL;
import com.sapienter.jbilling.server.order.db.OrderDTO;
import com.sapienter.jbilling.server.order.db.OrderLineDTO;
import com.sapienter.jbilling.server.pluggableTask.PluggableTask;
import com.sapienter.jbilling.server.pluggableTask.TaskException;
import com.sapienter.jbilling.server.user.ValidatePurchaseWS;
import com.sapienter.jbilling.server.user.ContactBL;
import com.sapienter.jbilling.server.user.ContactDTOEx;
import com.sapienter.jbilling.server.user.db.CustomerDTO;
import java.util.ArrayList;

/**
 * Pluggable task allows running rules for validatePurchase API method.
 */
@Deprecated
public class RulesValidatePurchaseTask extends RulesBaseTask
        implements IValidatePurchaseTask {

    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(RulesValidatePurchaseTask.class));
    protected FormatLogger getLog() { return LOG; }

    public ValidatePurchaseWS validate(CustomerDTO customer, 
            List<ItemDTO> items, List<BigDecimal> amounts, 
            ValidatePurchaseWS result, List<List<PricingField>> fields) 
            throws TaskException {

        if (!result.getAuthorized()) {
            return result;
        }

        BigDecimal amount = BigDecimal.ZERO;
        for (BigDecimal a : amounts) {
            amount = amount.add(a);
        }

        Integer userId = customer.getBaseUser().getId();

        KnowledgeBase knowledgeBase;
        try {
            knowledgeBase = readKnowledgeBase();
        } catch (Exception e) {
            throw new TaskException(e);
        }
        StatelessKnowledgeSession mySession = knowledgeBase.newStatelessKnowledgeSession();
        List<Object> rulesMemoryContext = new ArrayList<Object>();

        // add any pricing fields
        if (fields != null && !fields.isEmpty()) {
            for (List<PricingField> pricingFields : fields) {
                rulesMemoryContext.addAll(pricingFields);
            }
        }

        // add the data
        rulesMemoryContext.add(customer);
        rulesMemoryContext.add(customer.getBaseUser());
        rulesMemoryContext.add(result);
        for (ItemDTO item : items) {
            rulesMemoryContext.add(item);
        }

        // add user contact info
        ContactBL contact = new ContactBL();
        contact.set(userId);
        ContactDTOEx contactDTO = contact.getDTO();
        rulesMemoryContext.add(contactDTO);

        // add the subscriptions
        OrderBL order = new OrderBL();
        for (OrderDTO myOrder : order.getActiveRecurringByUser(userId)) {
            for (OrderLineDTO myLine : myOrder.getLines()) {
                rulesMemoryContext.add(new Subscription(myLine));
            }
        }

        // add the current order
        OrderDTO currentOrder = order.getCurrentOrder(userId, new Date());
        if (currentOrder != null) {
            rulesMemoryContext.add(currentOrder);
            for (OrderLineDTO line : currentOrder.getLines()) {
                rulesMemoryContext.add(line);
            }
        }

        // add the helper
        ValidatePurchase helper = new ValidatePurchase(amount);
        mySession.setGlobal("validatePurchase", helper);

        // execute the rules
        mySession.execute(rulesMemoryContext);

        // add any messages
        List<String> messages = helper.getMessages();
        if (messages.size() > 0) {
            String[] originalArray = result.getMessage();
            if (originalArray == null) {
                result.setMessage(messages.toArray(new String[0]));
            } else {
                String[] newArray = Arrays.copyOf(originalArray, 
                        originalArray.length + messages.size());
                int i = originalArray.length;
                for (String s : messages) {
                    newArray[i] = s;
                    i++;
                }
                result.setMessage(newArray);
            }
        }

        return result;
    }

    public static class ValidatePurchase {
        private BigDecimal amount;
        private List<String> messages;

        public ValidatePurchase(BigDecimal amount) {
            this.amount = amount;
            messages = new LinkedList<String>();
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void addMessage(String message) {
            messages.add(message);
        }

        public List<String> getMessages() {
            return messages;
        }
    }
}
