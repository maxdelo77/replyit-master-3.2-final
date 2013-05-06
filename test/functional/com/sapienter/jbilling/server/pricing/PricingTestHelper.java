package com.sapienter.jbilling.server.pricing;

import com.sapienter.jbilling.server.item.ItemDTOEx;
import com.sapienter.jbilling.server.order.OrderLineWS;
import com.sapienter.jbilling.server.order.OrderWS;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskWS;
import com.sapienter.jbilling.server.user.ContactWS;
import com.sapienter.jbilling.server.user.MainSubscriptionWS;
import com.sapienter.jbilling.server.user.UserDTOEx;
import com.sapienter.jbilling.server.user.UserWS;
import com.sapienter.jbilling.server.util.Constants;
import com.sapienter.jbilling.server.util.api.JbillingAPI;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

/**
 * Helper for writing pricing tests. Provides constants and factory methods to produce
 * web-service objects used in integration tests.
 *
 * @author Brian Cowdery
 * @since 10-Jul-2012
 */
public class PricingTestHelper {

    private static final DateTimeFormatter TS = DateTimeFormat.forPattern("-HHmmss");

    public static final Integer MONTHLY_PERIOD = 2;
    // plug-in configuration
    private static final Integer PRICING_PLUGIN_ID = 410;
    private static final Integer RULES_PRICING_PLUGIN_TYPE_ID = 61; // RulesPricingTask2
    private static final Integer MODEL_PRICING_PLUGIN_TYPE_ID = 79; // PriceModelPricingTask

    private static String timestamp() {
        return TS.print(new LocalTime());
    }

    public static UserWS buildUser(String username) {
        UserWS user = new UserWS();
        user.setUserName(username + timestamp());
        user.setPassword("password");
        user.setLanguageId(1);
        user.setCurrencyId(1);
        user.setMainRoleId(5);
        user.setStatusId(UserDTOEx.STATUS_ACTIVE);
        user.setBalanceType(Constants.BALANCE_NO_DYNAMIC);
        user.setMainSubscription(new MainSubscriptionWS(MONTHLY_PERIOD, new Date().getDate()));

        ContactWS contact = new ContactWS();
        contact.setEmail("test" + System.currentTimeMillis() + "@test.com");
        contact.setFirstName("Pricing Test");
        contact.setLastName(username);
        user.setContact(contact);

        return user;
    }

    public static OrderWS buildMonthlyOrder(Integer userId) {
        OrderWS order = new OrderWS();
        order.setUserId(userId);
        order.setBillingTypeId(Constants.ORDER_BILLING_POST_PAID);
        order.setStatusId(Constants.ORDER_STATUS_ACTIVE);
        order.setPeriod(MONTHLY_PERIOD);
        order.setCurrencyId(1);
        order.setActiveSince(new Date());

        return order;
    }

    public static OrderWS buildOneTimeOrder(Integer userId) {
        OrderWS order = new OrderWS();
        order.setUserId(userId);
        order.setBillingTypeId(Constants.ORDER_BILLING_POST_PAID);
        order.setPeriod(Constants.ORDER_PERIOD_ONCE);
        order.setCurrencyId(1);
        order.setActiveSince(new Date());

        return order;
    }

    public static OrderLineWS buildOrderLine(Integer itemId, Integer quantity) {
        OrderLineWS line = new OrderLineWS();
        line.setTypeId(Constants.ORDER_LINE_TYPE_ITEM);
        line.setItemId(itemId);
        line.setUseItem(true);
        line.setQuantity(quantity);

        return line;
    }

    public static ItemDTOEx buildItem(String number, String desc) {
        ItemDTOEx item = new ItemDTOEx();
        item.setNumber(number);
        item.setDescription(desc);
        item.setTypes(new Integer[]{1});

        return item;
    }


    //Enable/disable the PricingModelPricingTask plug-in.
    public void enablePricingPlugin(JbillingAPI api) {
        PluggableTaskWS plugin = api.getPluginWS(PRICING_PLUGIN_ID);
        plugin.setTypeId(MODEL_PRICING_PLUGIN_TYPE_ID);

        api.updatePlugin(plugin);
    }

    public void disablePricingPlugin(JbillingAPI api) {
        PluggableTaskWS plugin = api.getPluginWS(PRICING_PLUGIN_ID);
        plugin.setTypeId(RULES_PRICING_PLUGIN_TYPE_ID);

        api.updatePlugin(plugin);
    }
}
