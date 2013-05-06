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

package com.sapienter.jbilling.server.provisioning;

import com.sapienter.jbilling.common.SessionInternalError;
import com.sapienter.jbilling.server.order.OrderLineWS;
import com.sapienter.jbilling.server.order.OrderWS;
import com.sapienter.jbilling.server.util.Constants;
import com.sapienter.jbilling.server.util.api.JbillingAPI;
import com.sapienter.jbilling.server.util.api.JbillingAPIFactory;
import junit.framework.TestCase;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.sapienter.jbilling.test.Asserts.*;
import static org.testng.AssertJUnit.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Test(groups = { "integration", "provisioning" })
public class ProvisioningTest {

    private static final int           ORDER_LINES_COUNT  = 6;
    private static final int           USER_ID            = 1000;
    private static Integer[]           itemIds            = {
        1, 2, 3, 24, 240, 14
    };
    private static Integer[]           provisioningStatus = new Integer[6];
    JbillingAPI                        api;

    /**
     *
     * @see junit.framework.TestCase#setUp()
     */
    @BeforeClass
    protected void setUp() throws Exception {
        api = JbillingAPIFactory.getAPI();
    }

    private void pause(long t) {
        System.out.println("pausing for " + t);
        try {
            Thread.sleep(t);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test001NewQuantityEvent() {
        try {
            provisioningStatus[0] = Constants.PROVISIONING_STATUS_ACTIVE;
            provisioningStatus[1] = Constants.PROVISIONING_STATUS_INACTIVE;
            provisioningStatus[2] = null;
            provisioningStatus[3] = Constants.PROVISIONING_STATUS_PENDING_ACTIVE;
            provisioningStatus[4] = Constants.PROVISIONING_STATUS_PENDING_INACTIVE;
            provisioningStatus[5] = null;

            OrderWS newOrder = createMockOrder(USER_ID, ORDER_LINES_COUNT, new BigDecimal("77"));

            // create order through api
            Integer ret = api.createOrder(newOrder);

            System.out.println("Created order." + ret);
            assertNotNull("The order was not created", ret);
            System.out.println("running provisioning batch process..");
            //pause(2000);
            api.triggerProvisioning();
            pause(2000);
            System.out.println("Getting back new quantity provisioning order " + ret);

            OrderWS retOrder = api.getOrder(ret);

            System.out.println("got order: " + retOrder);

            OrderLineWS[] retLine = retOrder.getOrderLines();

            for (int i = 0; i < retLine.length; i++) {
                if (i == 0) {
                    assertEquals("order line " + (i + 1) + "", retLine[i].getProvisioningStatusId(),
                                 Constants.PROVISIONING_STATUS_ACTIVE);
                }

                if (i == 1) {
                    assertEquals("order line " + (i + 1) + "", retLine[i].getProvisioningStatusId(),
                                 Constants.PROVISIONING_STATUS_ACTIVE);
                }

                if (i == 2) {
                    assertEquals("order line " + (i + 1) + "", retLine[i].getProvisioningStatusId(),
                                 Constants.PROVISIONING_STATUS_INACTIVE); // default
                }

                if (i == 3) {
                    assertEquals("order line " + (i + 1) + "", retLine[i].getProvisioningStatusId(),
                                 Constants.PROVISIONING_STATUS_PENDING_ACTIVE);
                }

                if (i == 4) {
                    assertEquals("order line " + (i + 1) + "", retLine[i].getProvisioningStatusId(),
                                 Constants.PROVISIONING_STATUS_PENDING_INACTIVE);
                }

                if (i == 5) {
                    assertEquals("order line " + (i + 1) + "", retLine[i].getProvisioningStatusId(),
                                 Constants.PROVISIONING_STATUS_INACTIVE); // default

                }
            }
        } catch (SessionInternalError e) {
            e.printStackTrace();
            fail("Exception!" + e.getMessage());
        }
    }

    @Test
    public void test002SubscriptionActiveEvent() {
        try {
            provisioningStatus[0] = Constants.PROVISIONING_STATUS_ACTIVE;
            provisioningStatus[1] = Constants.PROVISIONING_STATUS_INACTIVE;
            provisioningStatus[2] = null;
            provisioningStatus[3] = Constants.PROVISIONING_STATUS_PENDING_ACTIVE;
            provisioningStatus[4] = Constants.PROVISIONING_STATUS_PENDING_INACTIVE;
            provisioningStatus[5] = null;

            OrderWS newOrder = createMockOrder(USER_ID, ORDER_LINES_COUNT, new BigDecimal("77"));

            // newOrder.setActiveSince(weeksFromToday(1));
            Calendar cal = Calendar.getInstance();

            cal.clear();
            cal.set(2008, 9, 29, 0, 0, 0);
            newOrder.setActiveSince(cal.getTime());

            // create order through api
            Integer ret = api.createOrder(newOrder);

            System.out.println("Created order." + ret);
            assertNotNull("The order was not created", ret);
            System.out.println("running provisioning batch process..");
            //pause(2000);
            api.triggerProvisioning();
            pause(2000);
            System.out.println("Getting back subscription activation provisioning order " + ret);

            OrderWS retOrder = api.getOrder(ret);

            System.out.println("got order: " + retOrder);

            OrderLineWS[] retLine = retOrder.getOrderLines();

            for (int i = 0; i < retLine.length; i++) {
                if (i == 0) {
                    assertEquals("order line " + (i + 1) + "", retLine[i].getProvisioningStatusId(),
                                 Constants.PROVISIONING_STATUS_ACTIVE);
                }

                if (i == 1) {
                    assertEquals("order line " + (i + 1) + "", retLine[i].getProvisioningStatusId(),
                                 Constants.PROVISIONING_STATUS_ACTIVE);
                }

                if (i == 2) {
                    assertEquals("order line " + (i + 1) + "", retLine[i].getProvisioningStatusId(),
                                 Constants.PROVISIONING_STATUS_INACTIVE); // default
                }

                if (i == 3) {
                    assertEquals("order line " + (i + 1) + "", retLine[i].getProvisioningStatusId(),
                                 Constants.PROVISIONING_STATUS_PENDING_ACTIVE);
                }

                if (i == 4) {
                    assertEquals("order line " + (i + 1) + "", retLine[i].getProvisioningStatusId(),
                                 Constants.PROVISIONING_STATUS_PENDING_INACTIVE);
                }

                if (i == 5) {
                    assertEquals("order line " + (i + 1) + "", retLine[i].getProvisioningStatusId(),
                                 Constants.PROVISIONING_STATUS_INACTIVE); // default
                }
            }
        } catch (SessionInternalError e) {
            e.printStackTrace();
            fail("Exception!" + e.getMessage());
        }
    }

    @Test
    public void test003SubscriptionInActiveEvent() {
        try {
            provisioningStatus[0] = Constants.PROVISIONING_STATUS_INACTIVE;
            provisioningStatus[1] = Constants.PROVISIONING_STATUS_ACTIVE;
            provisioningStatus[2] = null;
            provisioningStatus[3] = Constants.PROVISIONING_STATUS_PENDING_ACTIVE;
            provisioningStatus[4] = Constants.PROVISIONING_STATUS_PENDING_INACTIVE;
            provisioningStatus[5] = null;

            OrderWS newOrder = createMockOrder(USER_ID, ORDER_LINES_COUNT, new BigDecimal("77"));

            // newOrder.setActiveSince(weeksFromToday(1));
            Calendar cal = Calendar.getInstance();

            cal.clear();
            cal.set(2008, 9, 29, 0, 0, 0);
            newOrder.setActiveUntil(cal.getTime());

            cal.add(Calendar.MONTH, -1);
            newOrder.setActiveSince(cal.getTime());

            // create order through api
            Integer ret = api.createOrder(newOrder);

            System.out.println("Created order." + ret);
            assertNotNull("The order was not created", ret);
            System.out.println("running provisioning batch process..");
            //pause(2000);
            api.triggerProvisioning();
            pause(2000);
            System.out.println("Getting back subscription deactivate provisioning order " + ret);

            OrderWS retOrder = api.getOrder(ret);

            System.out.println("got order: " + retOrder);

            OrderLineWS[] retLine = retOrder.getOrderLines();

            for (int i = 0; i < retLine.length; i++) {
                if (i == 0) {
                    assertEquals("order line " + (i + 1) + "", retLine[i].getProvisioningStatusId(),
                                 Constants.PROVISIONING_STATUS_INACTIVE);
                }

                if (i == 1) {
                    assertEquals("order line " + (i + 1) + "", retLine[i].getProvisioningStatusId(),
                                 Constants.PROVISIONING_STATUS_INACTIVE);
                }

                if (i == 2) {
                    assertEquals("order line " + (i + 1) + "", retLine[i].getProvisioningStatusId(),
                                 Constants.PROVISIONING_STATUS_INACTIVE); // default
                }

                if (i == 3) {
                    assertEquals("order line " + (i + 1) + "", retLine[i].getProvisioningStatusId(),
                                 Constants.PROVISIONING_STATUS_PENDING_ACTIVE);
                }

                if (i == 4) {
                    assertEquals("order line " + (i + 1) + "", retLine[i].getProvisioningStatusId(),
                                 Constants.PROVISIONING_STATUS_PENDING_INACTIVE);
                }

                if (i == 5) {
                    assertEquals("order line " + (i + 1) + "", retLine[i].getProvisioningStatusId(),
                                 Constants.PROVISIONING_STATUS_INACTIVE); // default
                }
            }
        } catch (SessionInternalError e) {
            e.printStackTrace();
            fail("Exception!" + e.getMessage());
        }
    }

    private static OrderWS createMockOrder(int userId, int orderLinesCount, BigDecimal linePrice) {
        OrderWS order = new OrderWS();

        order.setUserId(userId);
        order.setBillingTypeId(Constants.ORDER_BILLING_PRE_PAID);
        order.setPeriod(1);    // once
        order.setCurrencyId(1);
        order.setActiveSince(new Date());

        ArrayList<OrderLineWS> lines = new ArrayList<OrderLineWS>(orderLinesCount);

        for (int i = 0; i < orderLinesCount; i++) {
            OrderLineWS nextLine = new OrderLineWS();

            nextLine.setTypeId(Constants.ORDER_LINE_TYPE_ITEM);
            nextLine.setDescription("Order line: " + i);
            nextLine.setItemId(itemIds[i]);
            nextLine.setQuantity(1);
            nextLine.setPrice(linePrice);
            nextLine.setAmount(nextLine.getQuantityAsDecimal().multiply(linePrice));
            nextLine.setProvisioningStatusId(provisioningStatus[i]);
            lines.add(nextLine);
        }

        order.setOrderLines(lines.toArray(new OrderLineWS[lines.size()]));

        return order;
    }

    /*private Date weeksFromToday(int weekNumber) {
        Calendar calendar = new GregorianCalendar();

        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.WEEK_OF_YEAR, weekNumber);

        return calendar.getTime();
    }*/

    @Test
    public void test004ExternalProvisioning() {
        try {
            // create the order
            OrderWS order = new OrderWS();
            order.setUserId(USER_ID);
            order.setBillingTypeId(Constants.ORDER_BILLING_PRE_PAID);
            order.setPeriod(1);
            order.setCurrencyId(1);
            order.setActiveSince(new Date());

            OrderLineWS line = new OrderLineWS();
            line.setItemId(251);
            line.setQuantity(1); // trigger 'external_provisioning_test' rule
            line.setTypeId(Constants.ORDER_LINE_TYPE_ITEM);
            line.setUseItem(true);
            line.setProvisioningStatusId(Constants.PROVISIONING_STATUS_INACTIVE);

            order.setOrderLines(new OrderLineWS[] { line });

            System.out.println("Creating order ...");
            Integer ret = api.createOrder(order);
            assertNotNull("The order was not created", ret);

            // this order line produces multiple provisioning results that get switched in sequence.
            // If you don't wait long enough (may require tweaking if you have a fast machine) you could
            // get the wrong status.
            //
            //      result msg 1: 'success'
            //      result msg 2: 'unavailable'
            //      result msg 3: 'failed'

            pause(4000); // wait for MDBs to complete
            System.out.println("Getting back external provisioning order " + ret);

            // note that this relies heavily on how fast the messages reach the TextExternalProvisioningMDB processor,
            // if the processor ITSELF (not the above pause!) executes too quickly it will misscount the number of
            // messages and return the wrong status. After a sufficient wait this test should ALWAYS return 'Failed'.
            OrderWS retOrder = api.getOrder(ret);
            System.out.println("Got order: " + retOrder);
            OrderLineWS orderLine = retOrder.getOrderLines()[0];
            assertEquals("Order status should be failed. Check log output " +
                    "from TestExternalProvisioningMDB in jbilling.log for " + 
                    "exact error.", Constants.PROVISIONING_STATUS_FAILED,
                    orderLine.getProvisioningStatusId());

        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception caught:" + e);
        }

    }

    @Test
    public void test005CAIProvisioning() {
        try {
            // create the order
            OrderWS order = new OrderWS();
            order.setUserId(USER_ID);
            order.setBillingTypeId(Constants.ORDER_BILLING_PRE_PAID);
            order.setPeriod(1);
            order.setCurrencyId(1);
            order.setActiveSince(new Date());

            OrderLineWS line = new OrderLineWS();
            line.setItemId(251);
            line.setQuantity(2); // trigger 'cai_test' rule
            line.setTypeId(Constants.ORDER_LINE_TYPE_ITEM);
            line.setUseItem(true);
            line.setProvisioningStatusId(Constants.PROVISIONING_STATUS_INACTIVE);

            order.setOrderLines(new OrderLineWS[] { line });

            System.out.println("Creating order ...");
            Integer ret = api.createOrder(order);
            assertNotNull("The order was not created", ret);

            pause(2000); // wait for MDBs to complete
            System.out.println("Getting back cia provisioning order " + ret);

            // check TestExternalProvisioningMDB was successful
            OrderWS retOrder = api.getOrder(ret); 
            OrderLineWS orderLine = retOrder.getOrderLines()[0];
            assertEquals("Order status should be active. Check log output " +
                    "from TestExternalProvisioningMDB in jbilling.log for " + 
                    "exact error.", Constants.PROVISIONING_STATUS_ACTIVE,
                    orderLine.getProvisioningStatusId());

        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception caught:" + e);
        }
    }

    @Test
    public void test006MMSCProvisioning() {
        try {
            // create the order
            OrderWS order = new OrderWS();
            order.setUserId(USER_ID);
            order.setBillingTypeId(Constants.ORDER_BILLING_PRE_PAID);
            order.setPeriod(1);
            order.setCurrencyId(1);
            order.setActiveSince(new Date());

            OrderLineWS line = new OrderLineWS();
            line.setItemId(251);
            line.setQuantity(3); // trigger 'mmsc_test' rule
            line.setTypeId(Constants.ORDER_LINE_TYPE_ITEM);
            line.setUseItem(true);
            line.setProvisioningStatusId(Constants.PROVISIONING_STATUS_INACTIVE);

            order.setOrderLines(new OrderLineWS[] { line });

            System.out.println("Creating order ...");
            Integer ret = api.createOrder(order);
            assertNotNull("The order was not created", ret);

            pause(2000); // wait for MDBs to complete
            System.out.println("Getting back mmsc provisioning order " + ret);

            // check TestExternalProvisioningMDB was successful
            OrderWS retOrder = api.getOrder(ret); 
            OrderLineWS orderLine = retOrder.getOrderLines()[0];
            assertEquals("Order status should be active. Check log output " +
                    "from TestExternalProvisioningMDB in jbilling.log for " + 
                    "exact error.", Constants.PROVISIONING_STATUS_ACTIVE,
                    orderLine.getProvisioningStatusId());

        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception caught:" + e);
        }
    }
}
