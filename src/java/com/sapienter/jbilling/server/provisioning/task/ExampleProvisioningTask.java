package com.sapienter.jbilling.server.provisioning.task;

import com.sapienter.jbilling.server.order.db.OrderDTO;
import com.sapienter.jbilling.server.order.db.OrderLineDTO;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.List;
/**
 * Example provisioning plug-in for jbilling tests.
 *
 * @author Brian Cowdery
 * @since 06-Jul-2012
 */
public class ExampleProvisioningTask extends AbstractProvisioningTask {
    private static final Logger LOG = Logger.getLogger(ExampleProvisioningTask.class);

    private static final BigDecimal ONE = new BigDecimal(1);
    private static final BigDecimal TWO = new BigDecimal(2);
    private static final BigDecimal THREE = new BigDecimal(3);

    private static final Integer LEMONADE_ITEM_ID = 2;
    private static final Integer SETUP_FEE_ITEM_ID = 251;


    @Override
    void activate(OrderDTO order, List<OrderLineDTO> lines, CommandManager c) {

        OrderLineDTO lemonade = findLine(lines, LEMONADE_ITEM_ID);
        if (lemonade != null && lemonade.getProvisioningStatusId().equals(2)) {
           // provisioning activate
            c.addCommand("activate_user", lemonade.getId());
            c.addParameter("msisdn", "12345");
            c.addParameter("imsi", "11111");

            LOG.debug("Added activation commands for order line " + lemonade.getId());
        }


        OrderLineDTO setupFee = findLine(lines, SETUP_FEE_ITEM_ID);
        if (setupFee != null) {
            // external provisioning test
            if (setupFee.getQuantity().compareTo(ONE) == 0 && setupFee.getProvisioningStatusId().equals(2)) {
                c.addCommand("result_test", setupFee.getId());
                // returns 'success' then 'unavailable'
                c.addParameter("msisdn", "98765");
                c.addCommand("result_test", setupFee.getId());
                // should return 'fail'
                c.addParameter("msisdn", "54321");

                LOG.debug("Added external provisioning commands for order line " + setupFee.getId());
            }

            // cai test
            if (setupFee.getQuantity().compareTo(TWO) == 0 && setupFee.getProvisioningStatusId().equals(2)) {
                c.addCommand("cai_test", setupFee.getId());
                c.addParameter("msisdn", "98765");
                // should be removed from command
                c.addParameter("imsi", "VOID");

                LOG.debug("Added CAI provisioning commands for order line " + setupFee.getId());
            }

            // mmsc test
            if (setupFee.getQuantity().compareTo(THREE) == 0 && setupFee.getProvisioningStatusId().equals(2)) {
                c.addCommand("mmsc_test", setupFee.getId());
                c.addParameter("msisdn", "99777");
                c.addParameter("subscriptionType", "HK");

                LOG.debug("Added MMSC provisioning commands for order line " + setupFee.getId());
            }
        }
    }

    @Override
    void deactivate(OrderDTO order, List<OrderLineDTO> lines, CommandManager c) {

        OrderLineDTO lemonade = findLine(lines, LEMONADE_ITEM_ID);
        if (lemonade != null && lemonade.getProvisioningStatusId().equals(1)) {
            // provisioning deactivate
            c.addCommand("activate_user", lemonade.getId());
            c.addParameter("msisdn", "12345");

            LOG.debug("Added activation commands for order line " + lemonade.getId());
        }
    }

}
