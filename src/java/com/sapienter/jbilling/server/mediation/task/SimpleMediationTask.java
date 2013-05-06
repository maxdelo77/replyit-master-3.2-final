package com.sapienter.jbilling.server.mediation.task;

import com.sapienter.jbilling.server.item.PricingField;
import com.sapienter.jbilling.server.item.db.ItemDAS;
import com.sapienter.jbilling.server.item.db.ItemDTO;
import com.sapienter.jbilling.server.order.OrderBL;
import com.sapienter.jbilling.server.order.db.OrderDTO;
import com.sapienter.jbilling.server.order.db.OrderLineDTO;
import com.sapienter.jbilling.server.user.UserBL;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * User: Nikhil
 * Date: 10/12/12
 * Written to work with file mediation-sample.csv with format
 * <?xml version="1.0" encoding="UTF-8"?>
 <!DOCTYPE format SYSTEM "mediation.dtd" >
 <format>
 <field>
 <name>event_id</name>
 <type>string</type>
 <isKey/>
 </field>
 <field>
 <name>event_date</name>
 <type>date</type>
 </field>
 <field>
 <name>username</name>
 <type>string</type>
 </field>
 <field>
 <name>item_number</name>
 <type>string</type>
 </field>
 <field>
 <name>quantity</name>
 <type>integer</type>
 </field>
 </format>
 */

public class SimpleMediationTask extends AbstractResolverMediationTask {

    private static final Logger LOG = Logger.getLogger(ExampleMediationTask.class);

    @Override
    public void resolveUserCurrencyAndDate(MediationResult result, List<PricingField> fields) {
        // resolve event date
        PricingField start = find(fields, "event_date");
        if (start != null) result.setEventDate(start.getDateValue());


        LOG.debug("Set result date " + result.getEventDate());


        // resolve user
        PricingField username = find(fields, "username");

        if (username != null) {
            LOG.debug("Resolving username: " + username.getStrValue());
            UserBL user = new UserBL(username.getStrValue(), getEntityId());

            if (user.getEntity() != null) {
                result.setUserId(user.getEntity().getUserId());
                result.setCurrencyId(user.getEntity().getCurrencyId());
            }

            LOG.debug("Set result user id " + result.getUserId() + ", currency id " + result.getCurrencyId());
        }
    }

    @Override
    public boolean isActionable(MediationResult result, List<PricingField> fields) {

        // validate quantity
        PricingField quantity = find(fields, "quantity");
        if (quantity == null && quantity.getIntValue().compareTo(0) > 0 ) {
            result.setDone(true);
            result.addError("ERR-QUANTITY");
            LOG.debug("Incorrect QUANTITY for record " + result.getRecordKey());
            return false;
        }

        // validate Item Number
        PricingField duration = find(fields, "item_number");
        if (duration == null) {
            result.setDone(true);
            result.addError("ERR-ITEM_NUMBER");
            LOG.debug("Incorrect ITEM Number for record " + result.getRecordKey());
            return false;
        }

        // validate that Item is Present in Jbilling
        PricingField itemNumber = find(fields, "item_number");
        ItemDTO item = new ItemDAS().findItemByInternalNumber(itemNumber.getStrValue());
        if(item==null) {
            result.setDone(true);
            result.addError("ERR-ITEM_NOT-FOUND");
            LOG.debug("ITEM NOT FOUND FOR RECORD " + result.getRecordKey());
            return false;
        }

        // validate that we were able to resolve the billable user, currency and date
        if (result.getCurrentOrder() == null) {
            if (result.getUserId() != null
                    && result.getCurrencyId() != null
                    && result.getEventDate() != null) {

                OrderDTO currentOrder = OrderBL.getOrCreateCurrentOrder(result.getUserId(),
                        result.getEventDate(),
                        result.getCurrencyId(),
                        result.getPersist());

                result.setCurrentOrder(currentOrder);

                LOG.debug("Mediation result " + result.getId() + " is actionable, resolving item ...");
                return true;
            }
        }

        LOG.debug("Mediation result " + result.getId() + " cannot be processed!");
        return false;
    }

    @Override
    public void doEventAction(MediationResult result, List<PricingField> fields) {
        PricingField quantity = find(fields, "quantity");
        PricingField itemNumber = find(fields, "item_number");

        ItemDTO item = new ItemDAS().findItemByInternalNumber(itemNumber.getStrValue());

        OrderLineDTO line = newLine(item.getId() , quantity.getDecimalValue());
        result.getLines().add(line);

        LOG.debug("Item Resolved = " + item.getInternalNumber() + ", for quantity" + quantity.getIntValue() + " minutes");
        LOG.debug("Item Resolved = " + item.getInternalNumber() + ", for quantity" + quantity.getIntValue() + " minutes");
        result.setDescription("Item resolved to " + item.getInternalNumber()+" for quantity "+quantity);
    }
}

