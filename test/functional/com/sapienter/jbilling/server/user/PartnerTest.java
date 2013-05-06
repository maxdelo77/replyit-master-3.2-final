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

package com.sapienter.jbilling.server.user;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.List;

import com.sapienter.jbilling.server.payment.PaymentWS;
import com.sapienter.jbilling.server.user.partner.PartnerPayoutWS;
import com.sapienter.jbilling.server.user.partner.PartnerWS;
import com.sapienter.jbilling.server.util.api.JbillingAPI;
import com.sapienter.jbilling.server.util.api.JbillingAPIFactory;
import com.sapienter.jbilling.server.util.Constants;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.sapienter.jbilling.test.Asserts.*;
import static org.testng.AssertJUnit.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * @author Emil
 */
@Test(groups = { "integration", "partner" })
public class PartnerTest {

    private JbillingAPI api;

    @BeforeClass
    protected void setUp() throws Exception {
        api = JbillingAPIFactory.getAPI();
    }


    public void testPartnerGeneral() {
        try {            
            Calendar cal = Calendar.getInstance();
            cal.clear();

            /* 
             *  first run
             */
            cal.set(2009, Calendar.MARCH, 15);
            api.triggerPartnerPayoutProcess(cal.getTime());

            // partner 1
            PartnerWS partner = api.getPartner(10);

            // no payouts
            assertEquals("No new payouts for 1", 0, partner.getPartnerPayouts().size());

            cal.set(2009, Calendar.APRIL, 1);
            assertEquals("1:next payout still apr 1", partner.getNextPayoutDate().getTime(), cal.getTime().getTime());

            // partner 2
            partner = api.getPartner(11);

            // no payouts, this guy doens't get paid in the batch
            assertEquals("No new payouts for 2", 0, partner.getPartnerPayouts().size());

            // still she should get paid
            // note: value should come from the ranged commission
            assertEquals("2: due payout ", new BigDecimal("2.3"), partner.getDuePayoutAsDecimal());
            cal.set(2009, Calendar.MARCH, 1);
            assertEquals("2:next payout mar 1", partner.getNextPayoutDate().getTime(), cal.getTime().getTime());

            // partner 3
            partner = api.getPartner(12);

            // a new payout
            List<PartnerPayoutWS> payouts = partner.getPartnerPayouts();
            assertEquals("3: one payout", 1, payouts.size());

            PartnerPayoutWS payout = payouts.get(0);
            assertNotNull("Payout", payout);

            PaymentWS payment = api.getPayment(payout.getPaymentId());
            assertNotNull("Payout payment", payment);
            assertEquals("3: payout total", new BigDecimal("2.5"), payment.getAmountAsDecimal());
            assertEquals("3: sucessful payment in new payout", Constants.RESULT_OK, payment.getResultId());
            assertEquals("3 due payout zero", BigDecimal.ZERO, partner.getDuePayoutAsDecimal());

            cal.set(2009, Calendar.MARCH, 25);
            assertEquals("3:next payout 10 days later ", partner.getNextPayoutDate().getTime(), cal.getTime().getTime());

            /*
             * second run
             */
            cal.set(2009, Calendar.APRIL, 1);
            api.triggerPartnerPayoutProcess(cal.getTime());

            // partner 1
            partner = api.getPartner(10);

            // new payout
            payouts = partner.getPartnerPayouts();
            assertEquals("1:New payout", 1, payouts.size());
            payout = payouts.get(0);

            assertNotNull("Payout", payout);
            payment = api.getPayment(payout.getPaymentId());
            assertNotNull("Payout payment", payment);
            assertEquals("1: payout total", new BigDecimal("5"), payment.getAmountAsDecimal());
            assertEquals("1: payout payments total", new BigDecimal("10"), payout.getPaymentsAmountAsDecimal());
            assertEquals("1: payout refunds total", new BigDecimal("5"), payout.getRefundsAmountAsDecimal());
            assertEquals("1: sucessful payment in new payout", Constants.RESULT_OK, payment.getResultId());
            assertEquals("1 due payout zero", BigDecimal.ZERO, partner.getDuePayoutAsDecimal());

            // partner 2
            partner = api.getPartner(11);

            // no payouts, this guy doens't get paid in the batch
            assertEquals("No new payouts for 2", 0, partner.getPartnerPayouts().size());

            // still she should get paid
            assertEquals("2: due payout ", new BigDecimal("2.3"), partner.getDuePayoutAsDecimal());
            cal.set(2009, Calendar.MARCH, 1);
            assertEquals("2:next payout mar 1", partner.getNextPayoutDate().getTime(), cal.getTime().getTime());

            // partner 3
            partner = api.getPartner(12);

            // a new payout
            payouts = partner.getPartnerPayouts();
            assertEquals("3: two payout", 2, payouts.size());

            // check the latest payout
            payout = payouts.get(payouts.size() - 1);
            payment = api.getPayment(payout.getPaymentId());

            assertNotNull("Payout payment", payment);
            //assertEquals("3: payout total", BigDecimal.ZERO, payment.getAmountAsDecimal());
            //assertEquals("3 due payout zero", BigDecimal.ZERO, partner.getDuePayoutAsDecimal());

            cal.set(2009, Calendar.MARCH, 25);
            cal.add(Calendar.DATE, 10);
            assertEquals("3 (2):next payout ", partner.getNextPayoutDate().getTime(), cal.getTime().getTime());
          
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception caught:" + e);
        }
    }
}
