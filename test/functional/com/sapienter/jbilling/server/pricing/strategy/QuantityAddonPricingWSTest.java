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
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.math.BigDecimal;

import static com.sapienter.jbilling.test.Asserts.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.testng.AssertJUnit.*;

/**
 * @author Brian Cowdery
 * @since 10-Jul-2012
 */
@Test(groups = {"web-services", "pricing", "quantity-addon"})
public class QuantityAddonPricingWSTest {

    JbillingAPI api;

    @BeforeTest
    public void getAPI() throws Exception {
        api = JbillingAPIFactory.getAPI();
    }

    @Test
    public void testQuantityAddon() {
        // item that we can purchase to increment the quantity addon
        ItemDTOEx triggerItem = PricingTestHelper.buildItem("TRIGGER", "Trigger item");
        triggerItem.addDefaultPrice(CommonConstants.EPOCH_DATE,
                new PriceModelWS(PriceModelStrategy.METERED.name(), new BigDecimal("0.20"), 1));

        triggerItem.setId(api.createItem(triggerItem));
        assertNotNull("item created", triggerItem.getId());

        // new item with quantity addon
        // the included number of items is calculated from the number of trigger items purchased
        PriceModelWS addonPrice = new PriceModelWS(PriceModelStrategy.QUANTITY_ADDON.name(), new BigDecimal("1.00"), 1);
        addonPrice.addAttribute(String.valueOf(triggerItem.getId()), "10");  // +10 units included for every purchase of the trigger item

        PriceModelWS graduatedPrice = new PriceModelWS(PriceModelStrategy.GRADUATED.name(), new BigDecimal("1.00"), 1);
        graduatedPrice.addAttribute("included", "10"); // start with 10 included

        addonPrice.setNext(graduatedPrice);

        ItemDTOEx item = PricingTestHelper.buildItem("TEST-ADDON", "Quantity addon test");
        item.addDefaultPrice(CommonConstants.EPOCH_DATE, addonPrice);

        item.setId(api.createItem(item));
        assertNotNull("item created", item.getId());


        // create user to test pricing with
        UserWS user = PricingTestHelper.buildUser("addon-pricing");
        user.setUserId(api.createUser(user));
        assertNotNull("customer created", user.getUserId());

        // order to be rated to test pricing
        OrderWS order = PricingTestHelper.buildMonthlyOrder(user.getUserId());
        OrderLineWS line = PricingTestHelper.buildOrderLine(item.getId(), 11);
        order.setOrderLines(new OrderLineWS[]{line});

        // no purchase of the addon item, start with 10 included
        // 11 units purchased, quantity exceeded by 1 priced at $1.00 unit over 10
        order = api.rateOrder(order);

        assertThat(order.getOrderLines().length, is(1));
        assertEquals(new BigDecimal("1.00"), order.getOrderLines()[0].getAmountAsDecimal());


        // add a unit of the quantity addon trigger item, should increase the included qty by +10
        // quantity of 21 exceeds included quantity, priced at $1.00 per unit over 20
        OrderLineWS triggerLine = PricingTestHelper.buildOrderLine(triggerItem.getId(), 1);
        line.setQuantity(21);
        order.setOrderLines(new OrderLineWS[]{triggerLine, line});
        order = api.rateOrder(order);

        assertThat(order.getOrderLines().length, is(2));
        assertThat(order.getOrderLines()[0].getItemId(), is(triggerItem.getId()));

        assertThat(order.getOrderLines()[1].getItemId(), is(item.getId()));
        assertEquals(new BigDecimal("1.00"), order.getOrderLines()[1].getAmountAsDecimal());

        // Reset the Graduated included quantity
        graduatedPrice = new PriceModelWS(PriceModelStrategy.GRADUATED.name(), new BigDecimal("1.00"), 1);
        graduatedPrice.addAttribute("included", "10"); // reset the included quantity to 10
        addonPrice.setNext(graduatedPrice);
        item.setDefaultPrice(addonPrice);
        item.setHasDecimals(0);
        api.updateItem(item);

        // add ANOTHER unit of the quantity addon trigger item, should increase the included qty by +20
        // quantity of 31 exceeds included quantity, priced at $1.00 per unit over 30
        triggerLine.setQuantity(2);
        line = PricingTestHelper.buildOrderLine(item.getId(), 31);
        order.setOrderLines(new OrderLineWS[]{triggerLine, line});
        order = api.rateOrder(order);

        assertThat(order.getOrderLines().length, is(2));
        assertThat(order.getOrderLines()[0].getItemId(), is(triggerItem.getId()));

        assertThat(order.getOrderLines()[1].getItemId(), is(item.getId()));
        assertEquals(new BigDecimal("1.00"), order.getOrderLines()[1].getAmountAsDecimal());


        // cleanup
        api.deleteItem(item.getId());
        api.deleteItem(triggerItem.getId());
        api.deleteUser(user.getUserId());
    }
}
