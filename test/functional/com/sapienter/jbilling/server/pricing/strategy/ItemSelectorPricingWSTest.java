package com.sapienter.jbilling.server.pricing.strategy;

import com.sapienter.jbilling.common.CommonConstants;
import com.sapienter.jbilling.server.item.ItemDTOEx;
import com.sapienter.jbilling.server.order.OrderLineWS;
import com.sapienter.jbilling.server.order.OrderWS;
import com.sapienter.jbilling.server.pricing.PriceModelWS;
import com.sapienter.jbilling.server.pricing.PricingTestHelper;
import com.sapienter.jbilling.server.pricing.db.PriceModelStrategy;
import com.sapienter.jbilling.server.user.UserWS;
import com.sapienter.jbilling.server.util.api.JbillingAPI;
import com.sapienter.jbilling.server.util.api.JbillingAPIFactory;
import org.hamcrest.Matchers;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.Arrays;

import static com.sapienter.jbilling.test.Asserts.*;
import static com.sapienter.jbilling.test.Asserts.assertEquals;
import static org.testng.AssertJUnit.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * @author Brian Cowdery
 * @since 10-Jul-2012
 */
@Test(groups = { "web-services", "pricing", "item-selector" })
public class ItemSelectorPricingWSTest {

    JbillingAPI api;

    @BeforeTest
    public void getAPI() throws Exception {
        api = JbillingAPIFactory.getAPI();
    }

    @Test
    public void testItemSelectorStrategy() {
        // new item selector
        PriceModelWS selector = new PriceModelWS(PriceModelStrategy.ITEM_SELECTOR.name(), new BigDecimal("1.00"), 1);
        selector.addAttribute("typeId", "1"); // purchases from item type 1 trigger items to be added
        selector.addAttribute("1", "2800");      // add item 2800 when 1 purchased
        selector.addAttribute("10", "2801");     // add item 2801 when > 10 purchased
        selector.addAttribute("20", "2900");     // add item 2900 when > 20 purchased

        ItemDTOEx item = PricingTestHelper.buildItem("TEST-ITEM-SEL", "Item selector test");
        item.addDefaultPrice(CommonConstants.EPOCH_DATE, selector);

        item.setId(api.createItem(item));
        assertNotNull("item created", item.getId());



        // item that we can purchase to trigger the item selector
        ItemDTOEx triggerItem = PricingTestHelper.buildItem("TRIGGER", "Trigger item");
        triggerItem.addDefaultPrice(CommonConstants.EPOCH_DATE,
                                    new PriceModelWS(PriceModelStrategy.METERED.name(), new BigDecimal("1.00"), 1));

        triggerItem.setId(api.createItem(triggerItem));
        assertNotNull("item created", triggerItem.getId());



        // create user to test pricing with
        UserWS user = PricingTestHelper.buildUser("item-selector");
        user.setUserId(api.createUser(user));
        assertNotNull("customer created", user.getUserId());

        // order to be rated to test pricing
        OrderWS order = PricingTestHelper.buildMonthlyOrder(user.getUserId());
        OrderLineWS line = PricingTestHelper.buildOrderLine(item.getId(), 1);
        OrderLineWS triggerLine = PricingTestHelper.buildOrderLine(triggerItem.getId(), 1);
        order.setOrderLines(new OrderLineWS[] { line, triggerLine });

        // 1 trigger item, should result in item 2800 being added
        order = api.rateOrder(order);

        assertThat(order.getOrderLines().length, is(3));
        assertThat(order.getOrderLines()[2].getItemId(), is(2800));

        // 11 trigger items, should result in item 2801 being added
        line.setQuantity(11);
        order.setOrderLines(new OrderLineWS[] { line, triggerLine });
        order = api.rateOrder(order);

        assertThat(order.getOrderLines().length, is(3));
        assertThat(order.getOrderLines()[2].getItemId(), is(2801));

        // 21 trigger items, should result in item 2802 being added
        line.setQuantity(21);
        order.setOrderLines(new OrderLineWS[] { line, triggerLine });
        order = api.rateOrder(order);

        assertThat(order.getOrderLines().length, is(3));
        assertThat(order.getOrderLines()[2].getItemId(), is(2900));


        // cleanup
        api.deleteItem(item.getId());
        api.deleteItem(triggerItem.getId());
        api.deleteUser(user.getUserId());
    }
}
