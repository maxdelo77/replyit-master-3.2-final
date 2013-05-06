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

import org.apache.log4j.Logger;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.server.item.PricingField;
import com.sapienter.jbilling.server.order.db.OrderDTO;
import com.sapienter.jbilling.server.pluggableTask.TaskException;
import com.sapienter.jbilling.server.rule.RulesBaseTask;
import com.sapienter.jbilling.server.user.ContactBL;
import com.sapienter.jbilling.server.user.ContactDTOEx;
import com.sapienter.jbilling.server.user.UserDTOEx;
import com.sapienter.jbilling.server.util.DTOFactory;

/**
 *
 * @author emilc
 */
@Deprecated
public class RulesPricingTask2 extends RulesBaseTask implements IPricing {

    protected FormatLogger getLog() {
        return new FormatLogger(Logger.getLogger(RulesPricingTask2.class));
    }
    
    public BigDecimal getPrice(Integer itemId, BigDecimal quantity, Integer userId, Integer currencyId,
            List<PricingField> fields, BigDecimal defaultPrice, OrderDTO pricingOrder, boolean singlePurchase)
            throws TaskException {

        // the result goes in the memory context
        PricingResult result = new PricingResult(itemId, quantity, userId, currencyId);
        rulesMemoryContext.add(result);

        if (fields != null && !fields.isEmpty()) {
            // bind the pricing fields to this result
            result.setPricingFieldsResultId(result.getId());
            for (PricingField field : fields) {
                field.setResultId(result.getId());
            }
            rulesMemoryContext.addAll(fields);
        }

        try {
            if (userId != null) {
                UserDTOEx user = DTOFactory.getUserDTOEx(userId);
                rulesMemoryContext.add(user);
                ContactBL contact = new ContactBL();
                contact.set(userId);
                ContactDTOEx contactDTO = contact.getDTO();
                rulesMemoryContext.add(contactDTO);
            }
        } catch (Exception e) {
            throw new TaskException(e);
        }

        executeRules();

        // the rules might not have any price for this. Use the default then.
        if (result.getPrice() == null) {
            result.setPrice(defaultPrice); // set the default
        }
        return result.getPrice();
    }
}
