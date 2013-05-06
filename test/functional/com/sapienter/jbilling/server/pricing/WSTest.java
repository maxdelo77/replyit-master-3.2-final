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

package com.sapienter.jbilling.server.pricing;

import com.sapienter.jbilling.common.CommonConstants;
import com.sapienter.jbilling.common.SessionInternalError;
import com.sapienter.jbilling.server.item.ItemDTOEx;
import com.sapienter.jbilling.server.item.PlanItemBundleWS;
import com.sapienter.jbilling.server.item.PlanItemWS;
import com.sapienter.jbilling.server.item.PlanWS;
import com.sapienter.jbilling.server.order.OrderLineWS;
import com.sapienter.jbilling.server.order.OrderWS;
import com.sapienter.jbilling.server.pricing.db.PriceModelStrategy;
import com.sapienter.jbilling.server.user.ContactWS;
import com.sapienter.jbilling.server.user.UserDTOEx;
import com.sapienter.jbilling.server.user.UserWS;
import com.sapienter.jbilling.server.util.Constants;
import com.sapienter.jbilling.server.util.api.JbillingAPI;
import com.sapienter.jbilling.server.util.api.JbillingAPIFactory;
import org.joda.time.DateMidnight;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static com.sapienter.jbilling.test.Asserts.*;
import static org.testng.AssertJUnit.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


/**
 * @author Brian Cowdery
 * @since 06-08-2010
 */
@Test(groups = { "web-services", "pricing" })
public class WSTest {

    private static final Integer MONTHLY_PERIOD = 2;

    /*
        Testing plan "Crazy Brian's Discount Plan"
        Prices Item 2602 (Lemonade) at $0.05
     */

    private static final Integer PLAN_ID = 1;
    private static final Integer PLAN_ITEM_ID = 3000;               // crazy brian's discount plan
    private static final Integer PLAN_AFFECTED_ITEM_ID = 2602;      // lemonade

    private static final Integer NON_PLAN_ITEM_ID = 2800;           // long distance call

    /**
     * Tests that the get all plans API method only returns plans for the caller company with
     * all of the fields and values intact.
     *
     * @throws Exception possible api exception
     */
    @Test
    public void testGetAllPlans() throws Exception {
        JbillingAPI api = JbillingAPIFactory.getAPI();

        List<PlanWS> plans = api.getAllPlans();
        assertNotNull("plans is not null", plans);
        assertTrue("more than one plan returned", plans.size() > 1);

        // the first plan in the list is Crazy Brian's Discount Plan
        // validate the details to verify that getAllPlans returns populated objects
        PlanWS plan = plans.get(0);
        assertEquals(PLAN_ID, plan.getId());
        assertEquals(PLAN_ITEM_ID, plan.getItemId());
        assertEquals("Discount lemonade", plan.getDescription());
        assertEquals(2, plan.getPeriodId().intValue());

        PlanItemWS planItem = plan.getPlanItems().get(0);
        assertEquals(PLAN_AFFECTED_ITEM_ID, planItem.getAffectedItemId());

        PriceModelWS model = planItem.getModel();
        assertEquals("METERED", model.getType());
        assertEquals(new BigDecimal("0.5"), model.getRateAsDecimal());
    }

    /**
     * Tests subscription / un-subscription to a plan by creating and deleting an order
     * containing the plan item.
     *
     * @throws Exception possible api exception
     */
    @Test
    public void testCreateDeleteOrder() throws Exception {
        JbillingAPI api = JbillingAPIFactory.getAPI();

        UserWS user = new UserWS();
        user.setUserName("plan-test-01-" + new Date().getTime());
        user.setPassword("password");
        user.setLanguageId(1);
        user.setCurrencyId(1);
        user.setMainRoleId(5);
        user.setStatusId(UserDTOEx.STATUS_ACTIVE);
        user.setBalanceType(Constants.BALANCE_NO_DYNAMIC);

        ContactWS contact = new ContactWS();
        contact.setEmail(user.getUserName() + "@test.com");
        contact.setFirstName("Plan Test");
        contact.setLastName("Create Order (subscribe)");
        user.setContact(contact);
        
        user.setUserId(api.createUser(user)); // create user
        assertNotNull("customer created", user.getUserId());

        OrderWS order = new OrderWS();
    	order.setUserId(user.getUserId());
        order.setBillingTypeId(Constants.ORDER_BILLING_POST_PAID);
        order.setPeriod(MONTHLY_PERIOD);
        order.setCurrencyId(1);
        order.setActiveSince(new Date());

        // subscribe to plan item
        OrderLineWS line = new OrderLineWS();
        line.setTypeId(Constants.ORDER_LINE_TYPE_ITEM);
        line.setItemId(PLAN_ITEM_ID);
        line.setUseItem(true);
        line.setQuantity(1);
        order.setOrderLines(new OrderLineWS[] { line });

        order.setId(api.createOrder(order)); // create order
        order = api.getOrder(order.getId());
        assertNotNull("order created", order.getId());


        // verify customer price creation with API calls.
        assertTrue("Customer should be subscribed to plan.", api.isCustomerSubscribed(PLAN_ID, user.getUserId()));

        PlanItemWS price = api.getCustomerPrice(user.getUserId(), PLAN_AFFECTED_ITEM_ID);
        assertEquals("Affected item should be discounted.", new BigDecimal("0.50"), price.getModel().getRateAsDecimal());

        // delete order that subscribes the user to the plan
        api.deleteOrder(order.getId());

        // verify customer price removal with API calls.
        assertFalse("Customer no longer subscribed to plan.", api.isCustomerSubscribed(PLAN_ID, user.getUserId()));

        price = api.getCustomerPrice(user.getUserId(), PLAN_AFFECTED_ITEM_ID);
        assertNull("Customer no longer subscribed to plan.", price);

        // cleanup
        api.deleteUser(user.getUserId());
    }

    /**
     * Tests subscription to a plan by updating an order.
     *
     * @throws Exception possible api exception
     */
    @Test
    public void testUpdateOrderSubscribe() throws Exception {
        JbillingAPI api = JbillingAPIFactory.getAPI();

        // create user
        UserWS user = new UserWS();
        user.setUserName("plan-test-02-" + new Date().getTime());
        user.setPassword("password");
        user.setLanguageId(1);
        user.setCurrencyId(1);
        user.setMainRoleId(5);
        user.setStatusId(UserDTOEx.STATUS_ACTIVE);
        user.setBalanceType(Constants.BALANCE_NO_DYNAMIC);

        ContactWS contact = new ContactWS();
        contact.setEmail(user.getUserName() + "@test.com");
        contact.setFirstName("Plan Test");
        contact.setLastName("Update Order (subscribe)");
        user.setContact(contact);
        
        user.setUserId(api.createUser(user)); // create user
        assertNotNull("customer created", user.getUserId());


        // create order
        OrderWS order = new OrderWS();
    	order.setUserId(user.getUserId());
        order.setBillingTypeId(Constants.ORDER_BILLING_POST_PAID);
        order.setPeriod(MONTHLY_PERIOD);
        order.setCurrencyId(1);
        order.setActiveSince(new Date());

        // subscribe to a non-plan junk item
        OrderLineWS line = new OrderLineWS();
        line.setTypeId(Constants.ORDER_LINE_TYPE_ITEM);
        line.setItemId(NON_PLAN_ITEM_ID);
        line.setUseItem(true);
        line.setQuantity(1);
        order.setOrderLines(new OrderLineWS[] { line });

        order.setId(api.createOrder(order)); // create order
        order = api.getOrder(order.getId());
        assertNotNull("order created", order.getId());


        // verify customer prices still empty with API calls.
        assertFalse("Customer should not subscribed to plan.", api.isCustomerSubscribed(PLAN_ID, user.getUserId()));

        PlanItemWS price = api.getCustomerPrice(user.getUserId(), PLAN_AFFECTED_ITEM_ID);
        assertNull("Order does not subscribe the customer to a plan. No price change.", price);

        // subscribe to plan item
        OrderLineWS planLine = new OrderLineWS();
        planLine.setTypeId(Constants.ORDER_LINE_TYPE_ITEM);
        planLine.setItemId(PLAN_ITEM_ID);
        planLine.setUseItem(true);
        planLine.setQuantity(1);
        order.setOrderLines(new OrderLineWS[] { line, planLine });

        api.updateOrder(order); // update order

        // verify price creation with API calls.
        assertTrue("Customer should be subscribed to plan.", api.isCustomerSubscribed(PLAN_ID, user.getUserId()));

        price = api.getCustomerPrice(user.getUserId(), PLAN_AFFECTED_ITEM_ID);
        assertEquals("Affected item should be discounted.", new BigDecimal("0.50"), price.getModel().getRateAsDecimal());

        // cleanup
        api.deleteOrder(order.getId());
        api.deleteUser(user.getUserId());
    }

    /**
     * Tests un-subscription from a plan by updating an order.
     *
     * @throws Exception possible api exception
     */
    @Test
    public void testUpdateOrderUnsubscribe() throws Exception {
        JbillingAPI api = JbillingAPIFactory.getAPI();

        // create user
        UserWS user = new UserWS();
        user.setUserName("plan-test-03-" + new Date().getTime());
        user.setPassword("password");
        user.setLanguageId(1);
        user.setCurrencyId(1);
        user.setMainRoleId(5);
        user.setStatusId(UserDTOEx.STATUS_ACTIVE);
        user.setBalanceType(Constants.BALANCE_NO_DYNAMIC);

        ContactWS contact = new ContactWS();
        contact.setEmail(user.getUserName() + "@test.com");
        contact.setFirstName("Plan Test");
        contact.setLastName("Update Order (un-subscribe)");
        user.setContact(contact);
        
        user.setUserId(api.createUser(user)); // create user
        assertNotNull("customer created", user.getUserId());


        // create order
        OrderWS order = new OrderWS();
    	order.setUserId(user.getUserId());
        order.setBillingTypeId(Constants.ORDER_BILLING_POST_PAID);
        order.setPeriod(MONTHLY_PERIOD);
        order.setCurrencyId(1);
        order.setActiveSince(new Date());

        // subscribe to plan item
        OrderLineWS planLine = new OrderLineWS();
        planLine.setTypeId(Constants.ORDER_LINE_TYPE_ITEM);
        planLine.setItemId(PLAN_ITEM_ID);
        planLine.setUseItem(true);
        planLine.setQuantity(1);
        order.setOrderLines(new OrderLineWS[] { planLine });

        order.setId(api.createOrder(order)); // create order
        order = api.getOrder(order.getId());
        assertNotNull("order created", order.getId());


        // verify price creation with API calls.
        assertTrue("Customer should be subscribed to plan.", api.isCustomerSubscribed(PLAN_ID, user.getUserId()));

        PlanItemWS price = api.getCustomerPrice(user.getUserId(), PLAN_AFFECTED_ITEM_ID);
        assertEquals("Affected item should be discounted.", new BigDecimal("0.50"), price.getModel().getRateAsDecimal());

        // remove plan item
        OrderLineWS oldLine = order.getOrderLines()[0];
        oldLine.setDeleted(1);

        // replace with non-plan junk item
        OrderLineWS line = new OrderLineWS();
        line.setTypeId(Constants.ORDER_LINE_TYPE_ITEM);
        line.setItemId(NON_PLAN_ITEM_ID);
        line.setUseItem(true);
        line.setQuantity(1);

        order.setOrderLines(new OrderLineWS[] { oldLine, line });
        api.updateOrder(order); // update order

        // verify price removed
        assertFalse("Customer no longer subscribed to plan.", api.isCustomerSubscribed(PLAN_ID, user.getUserId()));

        price = api.getCustomerPrice(user.getUserId(), PLAN_AFFECTED_ITEM_ID);
        assertNull("Order does not subscribe the customer to a plan. No price change.", price);

        // cleanup
        api.deleteOrder(order.getId());
        api.deleteUser(user.getUserId());
    }

    /**
     * Tests that the plan can be queried by the subscription item.
     *
     * @throws Exception possible api exception
     */
    @Test
    public void testGetPlanBySubscriptionItem() throws Exception {
        JbillingAPI api = JbillingAPIFactory.getAPI();

        Integer[] planIds = api.getPlansBySubscriptionItem(PLAN_ITEM_ID);
        assertEquals("Should only be 1 plan.", 1, planIds.length);
        assertEquals("Should be 'crazy brian's discount plan'", PLAN_ID, planIds[0]);
    }

    /**
     * Tests that the plan can be queried by the item's it affects.
     *
     * @throws Exception possible api exception
     */
    @Test
    public void testGetPlansByAffectedItem() throws Exception {
        JbillingAPI api = JbillingAPIFactory.getAPI();

        Integer[] planIds = api.getPlansByAffectedItem(PLAN_AFFECTED_ITEM_ID);
        assertEquals("Should only be 3 plans.", 3, planIds.length);
        assertEquals("Should be 'crazy brian's discount plan'", PLAN_ID, planIds[0]);
    }

    /**
     * Tests plan CRUD API calls.
     *
     * @throws Exception possible api exception
     */
    @Test
    public void testCreateUpdateDeletePlan() throws Exception {
        JbillingAPI api = JbillingAPIFactory.getAPI();

        final Integer LONG_DISTANCE_CALL = 2800;            // long distance call
        final Integer LONG_DISTANCE_CALL_GENERIC = 2900;    // long distance call - generic

        // subscription item for plan
        ItemDTOEx item = new ItemDTOEx();
        item.setDescription("Test Long Distance Plan");
        item.setNumber("TEST-LD-PLAN-01");
        item.setPrice("10.00");
        item.setTypes(new Integer[] { 1 });

        item.setId(api.createItem(item));


        // create plan
        PlanItemWS callPrice = new PlanItemWS();
        callPrice.setItemId(LONG_DISTANCE_CALL);
        callPrice.getModels().put(CommonConstants.EPOCH_DATE,
                                  new PriceModelWS(PriceModelStrategy.METERED.name(), new BigDecimal("0.10"), 1));

        PlanWS plan = new PlanWS();
        plan.setItemId(item.getId());
        plan.setDescription("Discount long distance calls.");
        plan.setPeriodId(MONTHLY_PERIOD);
        plan.addPlanItem(callPrice);

        plan.setId(api.createPlan(plan));

        // verify creation
        PlanWS fetchedPlan = api.getPlanWS(plan.getId());
        assertEquals(item.getId(), fetchedPlan.getItemId());
        assertEquals("Discount long distance calls.", fetchedPlan.getDescription());

        PlanItemWS fetchedPrice = fetchedPlan.getPlanItems().get(0);
        assertEquals(LONG_DISTANCE_CALL, fetchedPrice.getItemId());
        assertEquals(PriceModelStrategy.METERED.name(), fetchedPrice.getModel().getType());
        assertEquals(new BigDecimal("0.10"), fetchedPrice.getModel().getRateAsDecimal());
        assertEquals(1, fetchedPrice.getModel().getCurrencyId().intValue());


        // update the plan
        // update the description and add a price for the generic LD item
        PlanItemWS genericPrice = new PlanItemWS();
        genericPrice.setItemId(LONG_DISTANCE_CALL_GENERIC);
        genericPrice.getModels().put(CommonConstants.EPOCH_DATE,
                                     new PriceModelWS(PriceModelStrategy.METERED.name(), new BigDecimal("0.25"), 1));

        fetchedPlan.setDescription("Updated description.");
        fetchedPlan.addPlanItem(genericPrice);

        api.updatePlan(fetchedPlan);

        // verify update
        fetchedPlan = api.getPlanWS(fetchedPlan.getId());
        assertEquals(item.getId(), fetchedPlan.getItemId());
        assertEquals("Updated description.", fetchedPlan.getDescription());

        PlanItemWS distanceCallItem = null;
        PlanItemWS distanceCallGenericItem = null;

        for (PlanItemWS planItem : fetchedPlan.getPlanItems()) {
           if (planItem.getItemId().equals(LONG_DISTANCE_CALL)) {
               distanceCallItem = planItem;
               continue;
           }
           if (planItem.getItemId().equals(LONG_DISTANCE_CALL_GENERIC)) {
               distanceCallGenericItem = planItem;
               continue;
           }
        }

        assertNotNull("Distance Call Item is not presented in plan", distanceCallItem);
        assertNotNull("Distance Call Generic Item is not presented in plan", distanceCallGenericItem);

        // validate long distance call item  again
        assertEquals(LONG_DISTANCE_CALL, distanceCallItem.getItemId());
        assertEquals(PriceModelStrategy.METERED.name(), distanceCallItem.getModel().getType());
        assertEquals(new BigDecimal("0.10"), distanceCallItem.getModel().getRateAsDecimal());
        assertEquals(1, distanceCallItem.getModel().getCurrencyId().intValue());

        // validate long distance call generic item
        assertEquals(LONG_DISTANCE_CALL_GENERIC, distanceCallGenericItem.getItemId());
        assertEquals(PriceModelStrategy.METERED.name(), distanceCallGenericItem.getModel().getType());
        assertEquals(new BigDecimal("0.25"), distanceCallGenericItem.getModel().getRateAsDecimal());
        assertEquals(1, distanceCallGenericItem.getModel().getCurrencyId().intValue());


        // delete the plan
        api.deletePlan(fetchedPlan.getId());
        api.deleteItem(item.getId());

        try {
            api.getPlanWS(fetchedPlan.getId());
            fail("plan deleted, should throw an exception.");
        } catch (SessionInternalError e) {
            assertTrue(e.getMessage().contains("No row with the given identifier exists"));
        }
    }

    /**
     * Tests that the customer is un-subscribed when the plan is deleted.
     *
     * @throws Exception possible api exception
     */
    @Test
    public void testUnsubscribePlanDelete() throws Exception {
        JbillingAPI api = JbillingAPIFactory.getAPI();

        final Integer LONG_DISTANCE_CALL = 2800;            // long distance call


        // subscription item for plan
        ItemDTOEx item = new ItemDTOEx();
        item.setDescription("Test Long Distance Plan");
        item.setNumber("TEST-LD-PLAN-02");
        item.setPrice("10.00");
        item.setTypes(new Integer[] { 1 });

        item.setId(api.createItem(item));


        // create plan
        PlanItemWS callPrice = new PlanItemWS();
        callPrice.setItemId(LONG_DISTANCE_CALL);
        callPrice.getModels().put(CommonConstants.EPOCH_DATE,
                                  new PriceModelWS(PriceModelStrategy.METERED.name(), new BigDecimal("0.10"), 1));

        PlanWS plan = new PlanWS();
        plan.setItemId(item.getId());
        plan.setDescription("Discount long distance calls.");
        plan.setPeriodId(MONTHLY_PERIOD);
        plan.addPlanItem(callPrice);

        plan.setId(api.createPlan(plan));
        assertNotNull("plan created", plan.getId());


        // subscribe a customer to the plan
        UserWS user = new UserWS();
        user.setUserName("plan-test-04-" + new Date().getTime());
        user.setPassword("password");
        user.setLanguageId(1);
        user.setCurrencyId(1);
        user.setMainRoleId(5);
        user.setStatusId(UserDTOEx.STATUS_ACTIVE);
        user.setBalanceType(Constants.BALANCE_NO_DYNAMIC);

        ContactWS contact = new ContactWS();
        contact.setEmail(user.getUserName() + "@test.com");
        contact.setFirstName("Plan Test");
        contact.setLastName("Delete plan (un-subscribe)");
        user.setContact(contact);
        
        user.setUserId(api.createUser(user)); // create user
        assertNotNull("customer created", user.getUserId());

        OrderWS order = new OrderWS();
    	order.setUserId(user.getUserId());
        order.setBillingTypeId(Constants.ORDER_BILLING_POST_PAID);
        order.setPeriod(MONTHLY_PERIOD);
        order.setCurrencyId(1);
        order.setActiveSince(new Date());

        OrderLineWS line = new OrderLineWS();
        line.setTypeId(Constants.ORDER_LINE_TYPE_ITEM);
        line.setItemId(item.getId());
        line.setUseItem(true);
        line.setQuantity(1);
        order.setOrderLines(new OrderLineWS[] { line });

        order.setId(api.createOrder(order)); // create order
        order = api.getOrder(order.getId());
        assertNotNull("order created", order.getId());


        // verify that customer is subscribed
        assertTrue("Customer should be subscribed to plan.", api.isCustomerSubscribed(plan.getId(), user.getUserId()));

        // delete plan
        api.deletePlan(plan.getId());

        // verify that customer is no longer subscribed
        assertFalse("Customer should no longer be subscribed to plan.", api.isCustomerSubscribed(plan.getId(), user.getUserId()));

        // cleanup
        api.deleteOrder(order.getId());
        api.deleteUser(user.getUserId());
        api.deleteItem(item.getId());
    }

    /**
     * Tests that the price is affected by the subscription to a plan
     *
     * @throws Exception possible api exception
     */
    @Test
    public void testRateOrder() throws Exception {
        JbillingAPI api = JbillingAPIFactory.getAPI();


        UserWS user = new UserWS();
        user.setUserName("plan-test-05-" + new Date().getTime());
        user.setPassword("password");
        user.setLanguageId(1);
        user.setCurrencyId(1);
        user.setMainRoleId(5);
        user.setStatusId(UserDTOEx.STATUS_ACTIVE);
        user.setBalanceType(Constants.BALANCE_NO_DYNAMIC);

        ContactWS contact = new ContactWS();
        contact.setEmail(user.getUserName() + "@test.com");
        contact.setFirstName("Plan Test");
        contact.setLastName("Rate order test");
        user.setContact(contact);
        
        user.setUserId(api.createUser(user)); // create user
        assertNotNull("customer created", user.getUserId());

        OrderWS order = new OrderWS();
    	order.setUserId(user.getUserId());
        order.setBillingTypeId(Constants.ORDER_BILLING_POST_PAID);
        order.setPeriod(MONTHLY_PERIOD);
        order.setCurrencyId(1);
        order.setActiveSince(new Date());

        OrderLineWS line = new OrderLineWS();
        line.setTypeId(Constants.ORDER_LINE_TYPE_ITEM);
        line.setItemId(PLAN_ITEM_ID);
        line.setUseItem(true);
        line.setQuantity(1);
        order.setOrderLines(new OrderLineWS[] { line });

        order.setId(api.createOrder(order)); // create order
        order = api.getOrder(order.getId());
        assertNotNull("order created", order.getId());


        // verify customer price creation with API calls.
        assertTrue("Customer should be subscribed to plan.", api.isCustomerSubscribed(PLAN_ID, user.getUserId()));

        PlanItemWS price = api.getCustomerPrice(user.getUserId(), PLAN_AFFECTED_ITEM_ID);
        assertEquals("Affected item should be discounted.", new BigDecimal("0.50"), price.getModel().getRateAsDecimal());


        // test order using the affected plan item
        OrderWS testOrder = new OrderWS();
    	testOrder.setUserId(user.getUserId());
        testOrder.setBillingTypeId(Constants.ORDER_BILLING_POST_PAID);
        testOrder.setPeriod(MONTHLY_PERIOD);
        testOrder.setCurrencyId(1);
        testOrder.setActiveSince(new Date());

        OrderLineWS testLine = new OrderLineWS();
        testLine.setTypeId(Constants.ORDER_LINE_TYPE_ITEM);
        testLine.setItemId(PLAN_AFFECTED_ITEM_ID);
        testLine.setUseItem(true);
        testLine.setQuantity(1);
        testOrder.setOrderLines(new OrderLineWS[] { testLine });


        // rate order and verify price
        testOrder = api.rateOrder(testOrder);
        assertEquals("Order line should be priced at $0.50.", new BigDecimal("0.50"), testOrder.getOrderLines()[0].getPriceAsDecimal());

        // cleanup
        api.deleteOrder(order.getId());
        api.deleteUser(user.getUserId());
    }

    /**
     * Tests that subscribing to a plan with bundled quantity also adds the bundled items
     * to the subscription order. Bundled items should be added at the plan price.
     *
     * @throws Exception possible api exception
     */
    @Test
    public void testBundledQuantity() throws Exception {
        JbillingAPI api = JbillingAPIFactory.getAPI();


        final Integer LONG_DISTANCE_CALL = 2800;            // long distance call


        // subscription item for plan
        ItemDTOEx item = new ItemDTOEx();
        item.setDescription("Test Long Distance Plan");
        item.setNumber("TEST-LD-PLAN-03");
        item.setPrice("10.00");
        item.setTypes(new Integer[] { 1 });

        item.setId(api.createItem(item));


        // create user
        UserWS user = new UserWS();
        user.setUserName("plan-test-02-" + new Date().getTime());
        user.setPassword("password");
        user.setLanguageId(1);
        user.setCurrencyId(1);
        user.setMainRoleId(5);
        user.setStatusId(UserDTOEx.STATUS_ACTIVE);
        user.setBalanceType(Constants.BALANCE_NO_DYNAMIC);

        ContactWS contact = new ContactWS();
        contact.setEmail(user.getUserName() + "@test.com");
        contact.setFirstName("Plan Test");
        contact.setLastName("Update Order (subscribe)");
        user.setContact(contact);
        
        user.setUserId(api.createUser(user)); // create user
        assertNotNull("customer created", user.getUserId());


        // create plan
        // includes 10 bundled "long distance call" items
        PlanItemWS callPrice = new PlanItemWS();
        callPrice.setItemId(LONG_DISTANCE_CALL);
        callPrice.getModels().put(CommonConstants.EPOCH_DATE,
                                  new PriceModelWS(PriceModelStrategy.METERED.name(), new BigDecimal("0.10"), 1));

        PlanItemBundleWS bundle = new PlanItemBundleWS();
        bundle.setPeriodId(Constants.ORDER_PERIOD_ONCE);
        bundle.setTargetCustomer(PlanItemBundleWS.TARGET_SELF);
        bundle.setQuantity(new BigDecimal("10"));

        callPrice.setBundle(bundle);


        PlanWS plan = new PlanWS();
        plan.setItemId(item.getId());
        plan.setDescription("Discount long distance calls.");
        plan.setPeriodId(MONTHLY_PERIOD);
        plan.addPlanItem(callPrice);

        plan.setId(api.createPlan(plan));


        // subscribe to the created plan
        OrderWS order = new OrderWS();
    	order.setUserId(user.getUserId());
        order.setBillingTypeId(Constants.ORDER_BILLING_POST_PAID);
        order.setPeriod(MONTHLY_PERIOD);
        order.setCurrencyId(1);
        order.setActiveSince(new Date());

        OrderLineWS planLine = new OrderLineWS();
        planLine.setTypeId(Constants.ORDER_LINE_TYPE_ITEM);
        planLine.setItemId(item.getId());
        planLine.setUseItem(true);
        planLine.setQuantity(1);
        order.setOrderLines(new OrderLineWS[] { planLine });

        order.setId(api.createOrder(order)); // create order
        order = api.getOrder(order.getId());
        assertNotNull("order created", order.getId());


        // verify that a new one-time order was created using the original order as a template
        Integer[] orderIds = api.getLastOrders(user.getUserId(), 2);
        assertEquals("extra order created", 2, orderIds.length);

        OrderWS bundledOrder = api.getOrder(orderIds[1]);
        assertNotNull("bundled order created", bundledOrder);

        assertEquals(Constants.ORDER_PERIOD_ONCE, bundledOrder.getPeriod());
        assertEquals(order.getCurrencyId(), bundledOrder.getCurrencyId());
        assertEquals(order.getActiveSince(), bundledOrder.getActiveSince());
        assertEquals(order.getActiveUntil(), bundledOrder.getActiveUntil());

        // verify bundled item quantity added to a new one-time order
        boolean found = false;
        for (OrderLineWS line : bundledOrder.getOrderLines()) {
            if (line.getItemId().equals(LONG_DISTANCE_CALL)) {
                found = true;
                assertEquals("includes 10 bundled call items", new BigDecimal("10"), line.getQuantityAsDecimal());
            }
        }
        assertTrue("Found line for bundled quantity", found);


        // cleanup
        api.deleteOrder(order.getId());
        api.deleteOrder(bundledOrder.getId());
        api.deleteUser(user.getUserId());
        api.deletePlan(plan.getId());
        api.deleteItem(item.getId());
    }

    /**
     * Tests that sub-accounts flagged with "use parent pricing" can inherit the price from
     * a parent if they do not have a plan price for the product.
     *
     * @throws Exception possible api exception
     */
    @Test
    public void testSubAccountPricing() throws Exception {
        JbillingAPI api = JbillingAPIFactory.getAPI();


        // create parent user
        UserWS parentUser = new UserWS();
        parentUser.setUserName("sub-account-test-01-parent" + new Date().getTime());
        parentUser.setPassword("password");
        parentUser.setLanguageId(1);
        parentUser.setCurrencyId(1);
        parentUser.setMainRoleId(5);
        parentUser.setStatusId(UserDTOEx.STATUS_ACTIVE);
        parentUser.setBalanceType(Constants.BALANCE_NO_DYNAMIC);
        parentUser.setIsParent(true);

        ContactWS parentContact = new ContactWS();
        parentContact.setEmail(parentUser.getUserName() + "@test.com");
        parentContact.setFirstName("Sub-account Pricing Test");
        parentContact.setLastName("Parent Account");
        parentUser.setContact(parentContact);

        Integer parentUserId = api.createUser(parentUser); // create user
        parentUser = api.getUserWS(parentUserId);
        assertNotNull("parent customer created", parentUser.getUserId());


        // create child user
        UserWS childUser = new UserWS();
        childUser.setUserName("sub-account-test-01-child" + new Date().getTime());
        childUser.setPassword("password");
        childUser.setLanguageId(1);
        childUser.setCurrencyId(1);
        childUser.setMainRoleId(5);
        childUser.setStatusId(UserDTOEx.STATUS_ACTIVE);
        childUser.setBalanceType(Constants.BALANCE_NO_DYNAMIC);
        childUser.setParentId(parentUser.getUserId()); // sub-account of parent user created above
        childUser.setUseParentPricing(true);           // inherit pricing from parent

        ContactWS childContact = new ContactWS();
        childContact.setEmail(childUser.getUserName() + "@test.com");
        childContact.setFirstName("Sub-account Pricing Test");
        childContact.setLastName("Child Account");
        childUser.setContact(childContact);

        Integer childUserId = api.createUser(childUser); // create user
        childUser = api.getUserWS(childUserId);
        assertNotNull("child customer created", childUser.getUserId());
        assertTrue("child customer should use parent pricing", childUser.useParentPricing());


        // subscribe the parent account to the plan
        OrderWS parentOrder = new OrderWS();
    	parentOrder.setUserId(parentUser.getUserId());
        parentOrder.setBillingTypeId(Constants.ORDER_BILLING_POST_PAID);
        parentOrder.setPeriod(MONTHLY_PERIOD);
        parentOrder.setCurrencyId(1);
        parentOrder.setActiveSince(new Date());

        OrderLineWS planLine = new OrderLineWS();
        planLine.setTypeId(Constants.ORDER_LINE_TYPE_ITEM);
        planLine.setItemId(PLAN_ITEM_ID);
        planLine.setUseItem(true);
        planLine.setQuantity(1);
        parentOrder.setOrderLines(new OrderLineWS[] { planLine });

        parentOrder.setId(api.createOrder(parentOrder)); // create order
        parentOrder = api.getOrder(parentOrder.getId());
        assertNotNull("order created", parentOrder.getId());


        // verify parent account is subscribed to plan
        assertTrue("Parent account should be subscribed to plan.", api.isCustomerSubscribed(PLAN_ID, parentUser.getUserId()));


        // check that the parent subscribed plan price applies to the sub-account
        OrderWS childOrder = new OrderWS();
    	childOrder.setUserId(childUser.getUserId());
        childOrder.setBillingTypeId(Constants.ORDER_BILLING_POST_PAID);
        childOrder.setPeriod(MONTHLY_PERIOD);
        childOrder.setCurrencyId(1);
        childOrder.setActiveSince(new Date());

        OrderLineWS affectedItemLine = new OrderLineWS();
        affectedItemLine.setTypeId(Constants.ORDER_LINE_TYPE_ITEM);
        affectedItemLine.setItemId(PLAN_AFFECTED_ITEM_ID);
        affectedItemLine.setUseItem(true);
        affectedItemLine.setQuantity(1);
        childOrder.setOrderLines(new OrderLineWS[] { affectedItemLine });

        childOrder = api.rateOrder(childOrder);


        // verify that price matches the parent accounts plan price
        assertEquals("Discounted price for lemonade", new BigDecimal("0.50"), childOrder.getOrderLines()[0].getPriceAsDecimal());


        // cleanup
        api.deleteOrder(parentOrder.getId());
        api.deleteUser(childUser.getUserId());
        api.deleteUser(parentUser.getUserId());
    }

    /**
     * Tests that a plan can have prices with different start dates, and that a purchase after
     * that start date will use the appropriate price from the plan.
     *
     * @throws Exception possible api exception
     */
    @Test
    public void testPricingTimeLine() throws Exception {
        JbillingAPI api = JbillingAPIFactory.getAPI();


        final Integer LONG_DISTANCE_CALL = 2800;            // long distance call


        // subscription item for plan
        ItemDTOEx item = new ItemDTOEx();
        item.setDescription("Test Long Distance Plan");
        item.setNumber("TEST-LD-PLAN-03");
        item.setPrice("10.00");
        item.setTypes(new Integer[] { 1 });

        item.setId(api.createItem(item));


        // starting price
        PlanItemWS planItem = new PlanItemWS();
        planItem.setItemId(LONG_DISTANCE_CALL);

        // price starting at epoch date - used as default when no other prices set in timeline
        planItem.addModel(CommonConstants.EPOCH_DATE,
                          new PriceModelWS(PriceModelStrategy.METERED.name(), new BigDecimal("0.10"), 1));

        // price for june
        planItem.addModel(new DateMidnight(2011, 6, 1).toDate(),
                          new PriceModelWS(PriceModelStrategy.METERED.name(), new BigDecimal("0.20"), 1));

        // price for august
        planItem.addModel(new DateMidnight(2011, 8, 1).toDate(),
                          new PriceModelWS(PriceModelStrategy.METERED.name(), new BigDecimal("0.30"), 1));


        // create plan
        PlanWS plan = new PlanWS();
        plan.setItemId(item.getId());
        plan.setDescription("Discount long distance calls.");
        plan.setPeriodId(MONTHLY_PERIOD);
        plan.addPlanItem(planItem);

        plan.setId(api.createPlan(plan)); // create plan
        assertNotNull("plan created", plan.getId());



        // create user
        UserWS user = new UserWS();
        user.setUserName("plan-test-06-" + new Date().getTime());
        user.setPassword("password");
        user.setLanguageId(1);
        user.setCurrencyId(1);
        user.setMainRoleId(5);
        user.setStatusId(UserDTOEx.STATUS_ACTIVE);
        user.setBalanceType(Constants.BALANCE_NO_DYNAMIC);

        ContactWS contact = new ContactWS();
        contact.setEmail(user.getUserName() + "@test.com");
        contact.setFirstName("Plan Test");
        contact.setLastName("Pricing Time-line");
        user.setContact(contact);
        
        user.setUserId(api.createUser(user)); // create user
        assertNotNull("customer created", user.getUserId());


        // subscribe to plan
        OrderWS order = new OrderWS();
        order.setUserId(user.getUserId());
        order.setBillingTypeId(Constants.ORDER_BILLING_POST_PAID);
        order.setPeriod(MONTHLY_PERIOD);
        order.setCurrencyId(1);
        order.setActiveSince(new Date());

        OrderLineWS line = new OrderLineWS();
        line.setTypeId(Constants.ORDER_LINE_TYPE_ITEM);
        line.setItemId(plan.getItemId());
        line.setUseItem(true);
        line.setQuantity(1);
        order.setOrderLines(new OrderLineWS[] { line });

        order.setId(api.createOrder(order)); // create order
        assertNotNull("order created", order.getId());


        // verify customer price creation with API calls.
        assertTrue("Customer should be subscribed to plan.", api.isCustomerSubscribed(plan.getId(), user.getUserId()));

        PlanItemWS price = api.getCustomerPrice(user.getUserId(), LONG_DISTANCE_CALL);
        assertEquals("Price for item should have 3 different dates", 3, price.getModels().size());


        // test order using the affected plan item
        OrderWS testOrder = new OrderWS();
        testOrder.setUserId(user.getUserId());
        testOrder.setBillingTypeId(Constants.ORDER_BILLING_POST_PAID);
        testOrder.setPeriod(MONTHLY_PERIOD);
        testOrder.setCurrencyId(1);


        OrderLineWS testLine = new OrderLineWS();
        testLine.setTypeId(Constants.ORDER_LINE_TYPE_ITEM);
        testLine.setItemId(LONG_DISTANCE_CALL);
        testLine.setUseItem(true);
        testLine.setQuantity(1);


        // rate order and verify starting price
        testOrder.setActiveSince(new DateMidnight(1971, 1, 1).toDate()); // after the epoch date
        testOrder.setOrderLines(new OrderLineWS[] { testLine });         // should give us the starting price

        testOrder = api.rateOrder(testOrder);
        assertEquals("Order line should use starting price at $0.10.", new BigDecimal("0.10"), testOrder.getOrderLines()[0].getPriceAsDecimal());

        // update order to june, rate order and verify new price
        testOrder.setActiveSince(new DateMidnight(2011, 6, 1).toDate()); // 1st day of june, same as june price start date
        testOrder.setOrderLines(new OrderLineWS[] { testLine });         // should give us the june price

        testOrder = api.rateOrder(testOrder);
        assertEquals("Order line should use june price at $0.20.", new BigDecimal("0.20"), testOrder.getOrderLines()[0].getPriceAsDecimal());

        // update order to august, rate order and verify new price
        testOrder.setActiveSince(new DateMidnight(2011, 8, 2).toDate()); // 2nd day of august, after august price start date
        testOrder.setOrderLines(new OrderLineWS[] { testLine });         // should give us the august price

        testOrder = api.rateOrder(testOrder);
        assertEquals("Order line should use august price at $0.30.", new BigDecimal("0.30"), testOrder.getOrderLines()[0].getPriceAsDecimal());


        // cleanup
        api.deleteOrder(order.getId());
        api.deleteUser(user.getUserId());
        api.deletePlan(plan.getId());
        api.deleteItem(item.getId());
    }

    /**
     * Tests that plans and prices can only be accessed by the entity that owns the subscription/affected
     * plan item ids.
     *
     * @throws Exception possible api exception.
     */
    @Test
    public void testWSSecurity() throws Exception {
        JbillingAPI api = JbillingAPIFactory.getAPI();

        final Integer BAD_ITEM_ID = 4;        // item belonging to entity 2
        final Integer BAD_USER_ID = 13;       // user belonging to entity 2
        final Integer ENTITY_TWO_PLAN_ID = 2; // plan belonging to entity 2

        // getPlanWS
        try {
            api.getPlanWS(ENTITY_TWO_PLAN_ID);
            fail("Should not be able to get plan from another entity");
        } catch (SecurityException e) {
            assertTrue("Could not get plan for entity 2 subscription item", true);
        }

        // createPlan
        PlanWS createPlan = new PlanWS();
        createPlan.setItemId(BAD_ITEM_ID);
        createPlan.setDescription("Create plan with a bad item.");

        try {
            api.createPlan(createPlan);
            fail("Should not be able to create plans using items from another entity.");
        } catch (SecurityException e) {
            assertTrue("Could not create plan using item belonging to entity 2", true);
        }

        // updatePlan
        PlanWS updatePlan = api.getPlanWS(PLAN_ID);
        updatePlan.setItemId(BAD_ITEM_ID);

        try {
            api.updatePlan(updatePlan);
            fail("Should not be able to update plan using items from another entity.");
        } catch (SecurityException e) {
            assertTrue("Could not update plan using an item belonging to entity 2", true);
        }

        // deletePlan
        try {
            api.deletePlan(ENTITY_TWO_PLAN_ID);
            fail("Should not be able to delete a plan using an item from another entity.");
        } catch (SecurityException e) {
            assertTrue("Could not delete plan for entity 2 subscription item", true);
        }

        // addPlanPrice
        PlanItemWS addPlanPrice = new PlanItemWS();
        addPlanPrice.setItemId(BAD_ITEM_ID);
        addPlanPrice.getModels().put(CommonConstants.EPOCH_DATE,
                                     new PriceModelWS(PriceModelStrategy.METERED.name(), new BigDecimal("1.00"), 1));

        try {
            // cannot add to a plan we don't own
            api.addPlanPrice(ENTITY_TWO_PLAN_ID, addPlanPrice);
            fail("Should not be able to delete");
        } catch (SecurityException e) {
            assertTrue("Could not add price to a plan for entity 2 subscription item", true);
        }

        try {
            // cannot add a price for an item we don't own
            api.addPlanPrice(PLAN_ID, addPlanPrice);
            fail("Should not be able to add price for item from another entity.");
        } catch (SecurityException e) {
            assertTrue("Could not add price for an item belonging to entity 2", true);
        }

        // isCustomerSubscribed
        try {
            api.isCustomerSubscribed(ENTITY_TWO_PLAN_ID, 2); // entity 2 plan, for gandalf (entity 1 user)
            fail("Should not be able to check subscription status for a plan from another entity.");
        } catch (SecurityException e) {
            assertTrue("Could not check subscription status for plan belonging to entity 2", true);
        }

        // getSubscribedCustomers
        try {
            api.getSubscribedCustomers(ENTITY_TWO_PLAN_ID);
            fail("Should not be able to get subscribed customers for a plan from another entity.");
        } catch (SecurityException e) {
            assertTrue("Could not get subscribed customers  for plan belonging to entity 2", true);
        }

        // getPlansbySubscriptionItem
        try {
            api.getPlansBySubscriptionItem(BAD_ITEM_ID);
            fail("Should not be able to get plans using for item belonging to another entity.");
        } catch (SecurityException e) {
            assertTrue("Could not get plans by subscription item belonging to entity 2", true);
        }

        // getPlansByAffectedItem
        try {
            api.getPlansByAffectedItem(BAD_ITEM_ID);
            fail("Should not be able to get plans using for item belonging to another entity.");
        } catch (SecurityException e) {
            assertTrue("Could not get plans by affected item belonging to entity 2", true);
        }

        // getCustomerPrice
        try {
            api.getCustomerPrice(BAD_USER_ID, PLAN_AFFECTED_ITEM_ID);
            fail("Should not be able to get price for a user belonging to another entity.");
        } catch (SecurityException e) {
            assertTrue("Could not get price for user belonging to entity 2", true);

        }
    }
}
