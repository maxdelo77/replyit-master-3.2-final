package com.sapienter.jbilling.server.pricing.strategy;

import com.sapienter.jbilling.common.CommonConstants;
import com.sapienter.jbilling.server.item.ItemDTOEx;
import com.sapienter.jbilling.server.item.PricingField;
import com.sapienter.jbilling.server.order.OrderLineWS;
import com.sapienter.jbilling.server.order.OrderWS;
import com.sapienter.jbilling.server.pricing.PriceModelWS;
import com.sapienter.jbilling.server.pricing.PricingTestHelper;
import com.sapienter.jbilling.server.pricing.db.PriceModelStrategy;
import com.sapienter.jbilling.server.user.UserWS;
import com.sapienter.jbilling.server.util.Constants;
import com.sapienter.jbilling.server.util.api.JbillingAPI;
import com.sapienter.jbilling.server.util.api.JbillingAPIFactory;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.sapienter.jbilling.test.Asserts.*;
import static com.sapienter.jbilling.test.Asserts.assertEquals;
import static org.testng.AssertJUnit.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Quick rate card test.
 *
 * There are more extensive rate card tests as part of the order.WSTest and the integration MediationTest.
 *
 * @see com.sapienter.jbilling.server.order.WSTest
 * @see com.sapienter.jbilling.server.mediation.MediationTest
 *
 * @author Brian Cowdery
 * @since 10-Jul-2012
 */
@Test(groups = { "web-services", "pricing", "rate-card" })
public class RateCardPricingStrategyWSTest {

    JbillingAPI api;

    @BeforeTest
    public void getAPI() throws Exception {
        api = JbillingAPIFactory.getAPI();
    }

    @Test
    public void testRateCard() {

        // long distance call uses the rate card
        // see the test db for details
        final int LONG_DISTANCE_CALL = 2800;

        // create user to test pricing with
        UserWS user = PricingTestHelper.buildUser("rate-card");
        user.setUserId(api.createUser(user));
        assertNotNull("customer created", user.getUserId());


        // rate a 100 minute call to 55999 @ 0.33/min
        // 100 x 0.33 = $33.00
        PricingField[] pf = {
            new PricingField("dst", "55999"),
            new PricingField("duration", 1),
            new PricingField("disposition", "ANSWERED")
        };

        OrderWS order = PricingTestHelper.buildMonthlyOrder(user.getUserId());
        order.setPricingFields(PricingField.setPricingFieldsValue(pf));

        OrderLineWS line = PricingTestHelper.buildOrderLine(LONG_DISTANCE_CALL, 100);
        order.setOrderLines(new OrderLineWS[] { line });

        order = api.rateOrder(order);
        assertThat(order.getOrderLines().length, is(1));
        assertEquals(new BigDecimal("33.00"), order.getOrderLines()[0].getAmountAsDecimal());


        // rate a 100 minute call to 55000 @ 0.08/min
        // 100 x 0.08 = $8.00
        pf = new PricingField[] {
            new PricingField("dst", "55000"),
            new PricingField("duration", 1),
            new PricingField("disposition", "ANSWERED")
        };

        order.setPricingFields(PricingField.setPricingFieldsValue(pf));
        order.setOrderLines(new OrderLineWS[] { line });

        order = api.rateOrder(order);
        assertThat(order.getOrderLines().length, is(1));
        assertEquals(new BigDecimal("8.00"), order.getOrderLines()[0].getAmountAsDecimal());


        // clean up
        api.deleteUser(user.getUserId());
    }
}
