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

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.server.item.PricingField;
import com.sapienter.jbilling.server.item.tasks.BasicItemManager;
import com.sapienter.jbilling.server.item.tasks.IItemPurchaseManager;
import com.sapienter.jbilling.server.mediation.Record;
import com.sapienter.jbilling.server.order.db.OrderDTO;
import com.sapienter.jbilling.server.pluggableTask.TaskException;
import com.sapienter.jbilling.server.rule.RulesBaseTask;
import com.sapienter.jbilling.server.user.ContactBL;
import com.sapienter.jbilling.server.user.ContactDTOEx;
import com.sapienter.jbilling.server.user.UserDTOEx;
import com.sapienter.jbilling.server.util.DTOFactory;
import com.sapienter.jbilling.server.util.db.CurrencyDAS;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * This plug-in does item management rules that are compatible with the
 * mediation process.
 * This means that it is not called by the mediation process.
 * It does call the basic item manager, which in turn runs pricing rules.
 * @author emilc
 */
@Deprecated
public class RulesItemManager2 extends RulesBaseTask implements IItemPurchaseManager {

    protected FormatLogger getLog() {
        return new FormatLogger(Logger.getLogger(RulesItemManager2.class));
    }

    public void addItem(Integer itemID, BigDecimal quantity, Integer language,
            Integer userId, Integer entityId, Integer currencyId,
            OrderDTO order, List<Record> records) throws TaskException {
        // start by calling the standard plug-in
        BasicItemManager manager = new BasicItemManager();
        manager.addItem(itemID, quantity, language, userId, entityId, currencyId, order, records);

        processRules(order, userId);
    }

    protected void processRules(OrderDTO order, Integer userId) throws TaskException {

        rulesMemoryContext.add(order);

        // add OrderDTO to rules memory context
        order.setCurrency(new CurrencyDAS().find(order.getCurrency().getId()));
        if (order.getCreateDate() == null) {
            order.setCreateDate(new Date());
        }

        // needed for calls to 'rateOrder'
        if (order.getPricingFields() != null) {
            for(PricingField field: order.getPricingFields()) {
                rulesMemoryContext.add(field);
            }
        }

        try {
            UserDTOEx user = DTOFactory.getUserDTOEx(userId);
            rulesMemoryContext.add(user);
            ContactBL contact = new ContactBL();
            contact.set(userId);
            ContactDTOEx contactDTO = contact.getDTO();
            rulesMemoryContext.add(contactDTO);
        } catch (Exception e) {
            throw new TaskException(e);
        }

        executeRules();
    }
}
