/*
 jBilling - The Enterprise Open Source Billing System
 Copyright (C) 2003-2012 Enterprise jBilling Software Ltd. and Emiliano Conde

 This file is part of jbilling.

 jbilling is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 jbilling is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with jbilling.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.sapienter.jbilling.server.mediation;

import com.sapienter.jbilling.server.order.OrderLineWS;
import com.sapienter.jbilling.server.order.OrderWS;
import com.sapienter.jbilling.server.util.api.JbillingAPI;
import com.sapienter.jbilling.server.util.api.JbillingAPIFactory;
import junit.framework.TestCase;
import org.testng.annotations.Test;

import java.lang.Integer;
import java.lang.System;
import java.lang.Thread;
import java.math.BigDecimal;

import static com.sapienter.jbilling.test.Asserts.*;
import static org.testng.AssertJUnit.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


@Test(groups = { "integration", "mediation" })
public class SubscriptionTest {

    private static final Integer TEST_USER_ID = 10790;

    private static final Integer LONG_DISTANCE_CALL_GEN = 2900;

    private static final Integer UPDATED_ORDER_ID = 107900;

    private boolean triggered = false;

    public void trigger (JbillingAPI api) {
        if (!triggered) {
            triggered = true;
            api.triggerMediation();
        }
    }

    @Test
    public void testSubscription () throws Exception {
        JbillingAPI api = JbillingAPIFactory.getAPI();
        trigger(api); // trigger mediation process if it hasn't already ran

        while (api.isMediationProcessRunning()) {
            System.out.println("Waiting for mediation to finish");
            Thread.currentThread().sleep(1000);//sleep for 1000 ms
        }

        Integer[] orderIds = api.getLastOrders(TEST_USER_ID, 1);
        assertEquals("1 order created", 1, orderIds.length);


        // Created order
        System.out.println("VALIDATING CREATED ORDER, ID: " + orderIds[0]);
        OrderWS order = api.getOrder(orderIds[0]);

        // verify item quantities
        boolean longDistanceCallGen = false;

        for (OrderLineWS line : order.getOrderLines()) {
            System.out.println("LineItemId: " + line.getItemId());
            if (line.getItemId().equals(LONG_DISTANCE_CALL_GEN)) {
                assertEquals("long distance call generic quantity should be 10", new BigDecimal("10.00"), line.getQuantityAsDecimal().setScale(2, BigDecimal.ROUND_HALF_UP));
                //assertEquals("product a price should be $0.00", new BigDecimal("0.00"), line.getAmountAsDecimal().setScale(2,BigDecimal.ROUND_HALF_UP));
                longDistanceCallGen = true;
            }
        }

        assertTrue("product 'long distance call' not found", longDistanceCallGen);

        assertEquals("expected 1 lines", 1, order.getOrderLines().length);

        //Updated order
        System.out.println("VALIDATING UPDATED ORDER, ID: " + UPDATED_ORDER_ID);
        order = api.getOrder(UPDATED_ORDER_ID);

        longDistanceCallGen = false;

        for (OrderLineWS line : order.getOrderLines()) {
            System.out.println("LineItemId: " + line.getItemId());
            if (line.getItemId().equals(LONG_DISTANCE_CALL_GEN)) {
                assertEquals("long distance call generic quantity should be 20", new BigDecimal("20.00"), line.getQuantityAsDecimal().setScale(2, BigDecimal.ROUND_HALF_UP));
                //assertEquals("product a price should be $0.00", new BigDecimal("0.00"), line.getAmountAsDecimal().setScale(2,BigDecimal.ROUND_HALF_UP));
                longDistanceCallGen = true;
            }
        }

        assertTrue("product 'long distance call' not found", longDistanceCallGen);

        assertEquals("expected 1 lines", 1, order.getOrderLines().length);
    }

}
