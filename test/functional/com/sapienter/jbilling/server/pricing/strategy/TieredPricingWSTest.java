package com.sapienter.jbilling.server.pricing.strategy;

import com.sapienter.jbilling.common.CommonConstants;
import com.sapienter.jbilling.common.SessionInternalError;
import com.sapienter.jbilling.server.item.PlanItemWS;
import com.sapienter.jbilling.server.item.PlanWS;
import com.sapienter.jbilling.server.pricing.PriceModelWS;
import com.sapienter.jbilling.server.pricing.db.PriceModelStrategy;
import com.sapienter.jbilling.server.util.api.JbillingAPI;
import com.sapienter.jbilling.server.util.api.JbillingAPIFactory;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static com.sapienter.jbilling.test.Asserts.*;
import static com.sapienter.jbilling.test.Asserts.assertEquals;
import static org.testng.AssertJUnit.*;

@Test(groups = { "web-services", "pricing", "tiered" })
public class TieredPricingWSTest {

    private static final Integer MONTHLY_PERIOD = 2;

    JbillingAPI api;

    @BeforeTest
    public void getAPI() throws Exception {
        api = JbillingAPIFactory.getAPI();
    }

    //Test issue #3665
    @Test
    public void testCreateTieredPlan() throws Exception {
        final Integer LONG_DISTANCE_PLAN_ITEM = 2700;
        final Integer LONG_DISTANCE_CALL = 2800;

        JbillingAPI api = JbillingAPIFactory.getAPI();
        //    enablePricingPlugin(api);

        PriceModelWS tieredPrice = new PriceModelWS(PriceModelStrategy.TIERED.name(), null, 1);
        tieredPrice.addAttribute("0", "*breakme&");

        PlanItemWS callPrice = new PlanItemWS();
        callPrice.setItemId(LONG_DISTANCE_CALL);
        callPrice.getModels().put(CommonConstants.EPOCH_DATE, tieredPrice);

        PlanWS plan = new PlanWS();
        plan.setItemId(LONG_DISTANCE_PLAN_ITEM);
        plan.setDescription("Tiered calls.");
        plan.setPeriodId(MONTHLY_PERIOD);
        plan.addPlanItem(callPrice);

        try {
            plan.setId(api.createPlan(plan)); // create plan
            fail("Validation error expected");
        } catch (SessionInternalError e) {
            assertContainsError(e, "TieredPricingStrategy,0,validation.error.not.a.number", null);
        }

        tieredPrice.addAttribute("0", "4");

        plan.setId(api.createPlan(plan)); // create plan
        assertNotNull("plan created", plan.getId());

        // cleanup
        api.deletePlan(plan.getId());
    }
}
