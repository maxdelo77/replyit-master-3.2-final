package com.sapienter.jbilling.server.mediation.task;

import com.sapienter.jbilling.server.item.PricingField;
import com.sapienter.jbilling.server.order.OrderBL;
import com.sapienter.jbilling.server.order.db.OrderDTO;
import com.sapienter.jbilling.server.order.db.OrderLineDTO;
import com.sapienter.jbilling.server.user.UserBL;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Example mediation plug-in used by jbilling tests.
 *
 * @author Brian Cowdery
 * @since 29-Jun-2012
 */
public class ExampleMediationTask extends AbstractResolverMediationTask {

    private static final Logger LOG = Logger.getLogger(ExampleMediationTask.class);

    private static final Integer LD_CALL_ITEM_ID = 2800;
    private static final Integer INTERSTATE_CALL_ITEM_ID = 2801;


    @Override
    public void resolveUserCurrencyAndDate(MediationResult result, List<PricingField> fields) {
        // resolve event date
        PricingField start = find(fields, "start");
        if (start != null) result.setEventDate(start.getDateValue());

        PricingField startTime = find(fields, "start_time");
        if (startTime != null) result.setEventDate(startTime.getDateValue());

        LOG.debug("Set result date " + result.getEventDate());


        // resolve user
        PricingField username = find(fields, "userfield");

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
        // validate call duration
        PricingField duration = find(fields, "duration");
        if (duration == null || duration.getIntValue().intValue() < 0) {
            result.setDone(true);
            result.addError("ERR-DURATION");
            LOG.debug("Incorrect call duration for record " + result.getRecordKey());
            return false;
        }

        // discard unanswered calls
        PricingField disposition = find(fields, "disposition");
        if (disposition == null || !disposition.getStrValue().equals("ANSWERED")) {
            result.setDone(true);
            LOG.debug("Not a billable record " + result.getRecordKey());
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
        PricingField duration = find(fields, "duration");
        PricingField destination = find(fields, "dst");

        OrderLineDTO line = newLine(LD_CALL_ITEM_ID, duration.getDecimalValue());
        result.getLines().add(line);

        LOG.debug("Number called = " + destination.getStrValue() + ", " + duration.getStrValue() + " minutes");
        result.setDescription("Phone call to " + destination.getStrValue());
    }


    // example beforeSave method that adds tax and automatically recalculates the order total
    /*
    private static final Integer TAX_ITEM_ID = 1;

    @Override
    public void beforeSave(MediationResult result) {
        LOG.debug("Recalculating taxes for order");

        OrderLineDTO taxLine = find(result.getCurrentOrder(), TAX_ITEM_ID);
        if (taxLine == null) {
            OrderLineBL.addItem(result.getCurrentOrder(), TAX_ITEM_ID);
        }

        OrderBL orderBL = new OrderBL(result.getCurrentOrder());
        orderBL.recalculate(getEntityId());
    }
    */
}
