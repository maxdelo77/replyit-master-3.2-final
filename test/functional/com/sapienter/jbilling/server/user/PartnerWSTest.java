/*
 jBilling - The Enterprise Open Source Billing System
 Copyright (C) 2003-2011 Enterprise jBilling Software Ltd. and Emiliano Conde

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

package com.sapienter.jbilling.server.user;

import com.sapienter.jbilling.common.SessionInternalError;
import com.sapienter.jbilling.server.entity.PaymentInfoChequeDTO;
import com.sapienter.jbilling.server.payment.PaymentWS;
import com.sapienter.jbilling.server.process.db.PeriodUnitDTO;
import com.sapienter.jbilling.server.user.partner.PartnerPayoutWS;
import com.sapienter.jbilling.server.user.partner.PartnerWS;
import com.sapienter.jbilling.server.util.Constants;
import com.sapienter.jbilling.server.util.api.JbillingAPI;
import com.sapienter.jbilling.server.util.api.JbillingAPIFactory;
import org.joda.time.DateMidnight;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import static com.sapienter.jbilling.test.Asserts.*;
import static org.testng.AssertJUnit.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Test of Partner web-service API
 *
 * See also the partner integration test, test/org-integration/com/sapienter/jbilling/server/user/PartnerTest
 *
 * @author Brian Cowdery
 * @since 31-Oct-2011
 */
@Test(groups = { "web-services", "partner" })
public class PartnerWSTest {

    private static final Integer ADMIN_USER_ID = 2;
    private static final Integer PARTNER_1_USER_ID = 10;
    private static final Integer PARTNER_2_USER_ID = 11;
    private static final Integer PARTNER_3_USER_ID = 12;

    private static final Integer PARTNER_ROLE_ID = 4;

    @Test
    public void testCreatePartner() throws Exception {
        JbillingAPI api = JbillingAPIFactory.getAPI();

        // new partner
        UserWS user = new UserWS();
        user.setUserName("partner-01-" + new Date().getTime());
        user.setPassword("password");
        user.setLanguageId(1);
        user.setCurrencyId(1);
        user.setMainRoleId(PARTNER_ROLE_ID);
        user.setStatusId(UserDTOEx.STATUS_ACTIVE);
        user.setBalanceType(Constants.BALANCE_NO_DYNAMIC);

        ContactWS contact = new ContactWS();
        contact.setEmail(user.getUserName() + "@test.com");
        contact.setFirstName("Partner Test");
        contact.setLastName("create new");
        user.setContact(contact);

        PartnerWS partner = new PartnerWS();
        partner.setRelatedClerkUserId(ADMIN_USER_ID);
        partner.setBalance("0.00");
        partner.setPercentageRate("5.00"); // 5%
        partner.setReferralFee("10.00");   // $10.00 per referral
        partner.setFeeCurrencyId(1);
        partner.setOneTime(false);         // referral fee is recurring (not one time)
        partner.setPeriodUnitId(PeriodUnitDTO.MONTH); // payout once monthly
        partner.setPeriodValue(1);
        partner.setNextPayoutDate(new DateMidnight(2011, 1, 1).toDate()); // January 1, 2011
        partner.setAutomaticProcess(true);


        // create partner
        Integer partnerId = api.createPartner(user, partner);
        partner = api.getPartner(partnerId);

        assertNotNull("partner created", partner);
        assertNotNull("partner has an id", partner.getId());

        // validate partner attributes to make sure it saved correctly
        assertEquals(BigDecimal.ZERO, partner.getBalanceAsDecimal());
        assertEquals(new BigDecimal("10.00"), partner.getReferralFeeAsDecimal());
        assertEquals(1, partner.getFeeCurrencyId().intValue());
        assertEquals(PeriodUnitDTO.MONTH, partner.getPeriodUnitId().intValue());
        assertEquals(1, partner.getPeriodValue().intValue());
        assertEquals(new DateMidnight(2011, 1, 1).toDate(), partner.getNextPayoutDate());
        assertFalse(partner.getOneTime());
        assertTrue(partner.getAutomaticProcess());


        // cleanup
        api.deletePartner(partnerId);
    }

    @Test
    public void testUpdatePartner() throws Exception {
        JbillingAPI api = JbillingAPIFactory.getAPI();

        // new partner
        UserWS user = new UserWS();
        user.setUserName("partner-02-" + new Date().getTime());
        user.setPassword("password");
        user.setLanguageId(1);
        user.setCurrencyId(1);
        user.setMainRoleId(PARTNER_ROLE_ID);
        user.setStatusId(UserDTOEx.STATUS_ACTIVE);
        user.setBalanceType(Constants.BALANCE_NO_DYNAMIC);

        ContactWS contact = new ContactWS();
        contact.setEmail(user.getUserName() + "@test.com");
        contact.setFirstName("Partner Test");
        contact.setLastName("update");
        user.setContact(contact);

        PartnerWS partner = new PartnerWS();
        partner.setRelatedClerkUserId(ADMIN_USER_ID);
        partner.setBalance("0.00");
        partner.setPercentageRate("5.00"); // 5%
        partner.setReferralFee("10.00");   // $10.00 per referral
        partner.setFeeCurrencyId(1);
        partner.setOneTime(false);         // referral fee is recurring (not one time)
        partner.setPeriodUnitId(PeriodUnitDTO.MONTH); // payout once monthly
        partner.setPeriodValue(1);
        partner.setNextPayoutDate(new DateMidnight(2011, 1, 1).toDate()); // January 1, 2011
        partner.setAutomaticProcess(true);


        // create partner
        Integer partnerId = api.createPartner(user, partner);
        partner = api.getPartner(partnerId);

        assertNotNull("partner created", partner);


        // update some attributes and save
        partner.setBalance("10.00");
        partner.setReferralFee("99.00");
        partner.setOneTime(true);
        partner.setNextPayoutDate(new DateMidnight(2011, 11, 1).toDate()); // November 1, 2011

        // just save changes to partner, nothing changes on the base user
        api.updatePartner(null, partner);
        partner = api.getPartner(partnerId);


        // validate partner attributes to make sure it saved correctly
        assertEquals(new BigDecimal("10.00"), partner.getBalanceAsDecimal());
        assertEquals(new BigDecimal("99.00"), partner.getReferralFeeAsDecimal());
        assertEquals(new DateMidnight(2011, 11, 1).toDate(), partner.getNextPayoutDate());
        assertTrue(partner.getOneTime());


        // cleanup
        api.deletePartner(partnerId);
    }

    @Test
    public void testDeletePartner() throws Exception {
        JbillingAPI api = JbillingAPIFactory.getAPI();

        // new partner
        UserWS user = new UserWS();
        user.setUserName("partner-03-" + new Date().getTime());
        user.setPassword("password");
        user.setLanguageId(1);
        user.setCurrencyId(1);
        user.setMainRoleId(PARTNER_ROLE_ID);
        user.setStatusId(UserDTOEx.STATUS_ACTIVE);
        user.setBalanceType(Constants.BALANCE_NO_DYNAMIC);

        ContactWS contact = new ContactWS();
        contact.setEmail(user.getUserName() + "@test.com");
        contact.setFirstName("Partner Test");
        contact.setLastName("delete");
        user.setContact(contact);

        PartnerWS partner = new PartnerWS();
        partner.setRelatedClerkUserId(ADMIN_USER_ID);
        partner.setBalance("0.00");
        partner.setPercentageRate("5.00"); // 5%
        partner.setReferralFee("10.00");   // $10.00 per referral
        partner.setFeeCurrencyId(1);
        partner.setOneTime(false);         // referral fee is recurring (not one time)
        partner.setPeriodUnitId(PeriodUnitDTO.MONTH); // payout once monthly
        partner.setPeriodValue(1);
        partner.setNextPayoutDate(new DateMidnight(2011, 1, 1).toDate()); // January 1, 2011
        partner.setAutomaticProcess(true);


        // create partner
        Integer partnerId = api.createPartner(user, partner);
        partner = api.getPartner(partnerId);

        assertNotNull("partner created", partner);


        // delete partner
        api.deletePartner(partner.getId());


        // verify that partner cannot be fetched after deleting
        try {
            api.getPartner(partner.getId());
            fail("deleted partner should throw exception");
        } catch (SessionInternalError e) {
            assertTrue(e.getMessage().contains("No row with the given identifier exists"));
        }

        // verify that the base user was deleted with the partner
        UserWS deletedUser = api.getUserWS(partner.getUserId());
        assertEquals(1, deletedUser.getDeleted());
    }

    @Test
    public void testGetPartner() throws Exception {
        JbillingAPI api = JbillingAPIFactory.getAPI();

        // partner that does not exist throws exception
        try {
            api.getPartner(999);
            fail("non-existent partner should throw exception");
        } catch (SessionInternalError e) {
            assertTrue(e.getMessage().contains("No row with the given identifier exists"));
        }

        // partner belonging to a different entity throws a security exception
        try {
            api.getPartner(20); // belongs to entity 2
            fail("partner does not belong to entity 1, should throw security exception.");
        } catch (SecurityException e) {
            assertTrue(e.getMessage().contains("Unauthorized access to entity 2"));
        }
    }

    @Test
    public void testReferralFee() throws Exception {
        JbillingAPI api = JbillingAPIFactory.getAPI();

        // new partner
        UserWS user = new UserWS();
        user.setUserName("partner-04-" + new Date().getTime());
        user.setPassword("password");
        user.setLanguageId(1);
        user.setCurrencyId(1);
        user.setMainRoleId(PARTNER_ROLE_ID);
        user.setStatusId(UserDTOEx.STATUS_ACTIVE);
        user.setBalanceType(Constants.BALANCE_NO_DYNAMIC);

        ContactWS contact = new ContactWS();
        contact.setEmail(user.getUserName() + "@test.com");
        contact.setFirstName("Partner Test");
        contact.setLastName("referral fee");
        user.setContact(contact);

        PartnerWS partner = new PartnerWS();
        partner.setRelatedClerkUserId(ADMIN_USER_ID);
        partner.setBalance("0.00");
        partner.setPercentageRate((BigDecimal) null);   // referral fee OR percentage rate, cannot set both
        partner.setReferralFee("10.00");                // $10.00 per referral
        partner.setFeeCurrencyId(1);
        partner.setOneTime(false);                      // referral fee is recurring (not one time)
        partner.setPeriodUnitId(PeriodUnitDTO.MONTH);   // payout once monthly
        partner.setPeriodValue(1);
        partner.setNextPayoutDate(new DateMidnight(2011, 1, 1).toDate()); // January 1, 2011
        partner.setAutomaticProcess(true);


        // create partner
        Integer partnerId = api.createPartner(user, partner);
        partner = api.getPartner(partnerId);

        assertNotNull("partner created", partner);


        // create a linked customer
        UserWS customer = new UserWS();
        customer.setUserName("partner-linked-01-" + new Date().getTime());
        customer.setPassword("password");
        customer.setLanguageId(1);
        customer.setCurrencyId(1);
        customer.setMainRoleId(5);
        customer.setPartnerId(partner.getId());
        customer.setStatusId(UserDTOEx.STATUS_ACTIVE);
        customer.setBalanceType(Constants.BALANCE_NO_DYNAMIC);

        ContactWS customerContact = new ContactWS();
        customerContact.setEmail(customer.getUserName() + "@test.com");
        customerContact.setFirstName("Partner Customer");
        customerContact.setLastName("referral fee");
        customer.setContact(customerContact);

        customer.setUserId(api.createUser(customer)); // create user
        assertNotNull("customer created", customer.getUserId());


        // create a payment for the linked customer
        // a partner isn't paid anything until their referral generates some revenue
        PaymentWS payment = new PaymentWS();
        payment.setAmount(new BigDecimal("100.00"));
        payment.setIsRefund(0);
        payment.setMethodId(Constants.PAYMENT_METHOD_CHEQUE);
        payment.setPaymentDate(new Date());
        payment.setResultId(Constants.RESULT_ENTERED);
        payment.setCurrencyId(1);
        payment.setUserId(customer.getUserId());
        payment.setPaymentNotes("Payment for partner referral");
        payment.setPaymentPeriod(1);

        PaymentInfoChequeDTO cheque = new PaymentInfoChequeDTO();
        cheque.setBank("ws bank");
        cheque.setDate(new Date());
        cheque.setNumber("2232-2323-2323");
        payment.setCheque(cheque);

        Integer paymentId = api.applyPayment(payment, null);


        // run payout
        // validate that the partner was paid the referral fee
        api.processPartnerPayout(partner.getId());
        partner = api.getPartner(partnerId);

        assertEquals(1, partner.getPartnerPayouts().size());
        assertEquals(new BigDecimal("10.00"), partner.getDuePayoutAsDecimal());

        PartnerPayoutWS payout = partner.getPartnerPayouts().get(0);
        assertEquals(new BigDecimal("10.00"), payout.getPaymentsAmountAsDecimal());
        assertEquals(BigDecimal.ZERO, payout.getRefundsAmountAsDecimal());
        assertEquals(BigDecimal.ZERO, payout.getBalanceLeftAsDecimal());


        // cleanup
        api.deletePayment(paymentId);
        api.deleteUser(customer.getUserId());
        api.deletePartner(partner.getId());
    }

    @Test
    public void testCommission() throws Exception {
        JbillingAPI api = JbillingAPIFactory.getAPI();

        // new partner
        UserWS user = new UserWS();
        user.setUserName("partner-05-" + new Date().getTime());
        user.setPassword("password");
        user.setLanguageId(1);
        user.setCurrencyId(1);
        user.setMainRoleId(PARTNER_ROLE_ID);
        user.setStatusId(UserDTOEx.STATUS_ACTIVE);
        user.setBalanceType(Constants.BALANCE_NO_DYNAMIC);

        ContactWS contact = new ContactWS();
        contact.setEmail(user.getUserName() + "@test.com");
        contact.setFirstName("Partner Test");
        contact.setLastName("commission rate");
        user.setContact(contact);

        PartnerWS partner = new PartnerWS();
        partner.setRelatedClerkUserId(ADMIN_USER_ID);
        partner.setBalance("0.00");
        partner.setPercentageRate("5.00");              // 5% referral fee
        partner.setReferralFee((BigDecimal) null);      // referral fee OR percentage rate, cannot set both
        partner.setFeeCurrencyId(1);
        partner.setOneTime(false);                      // referral fee is recurring (not one time)
        partner.setPeriodUnitId(PeriodUnitDTO.MONTH);   // payout once monthly
        partner.setPeriodValue(1);
        partner.setNextPayoutDate(new DateMidnight(2011, 1, 1).toDate()); // January 1, 2011
        partner.setAutomaticProcess(true);


        // create partner
        Integer partnerId = api.createPartner(user, partner);
        partner = api.getPartner(partnerId);

        assertNotNull("partner created", partner);


        // create a linked customer
        UserWS customer = new UserWS();
        customer.setUserName("partner-linked-02-" + new Date().getTime());
        customer.setPassword("password");
        customer.setLanguageId(1);
        customer.setCurrencyId(1);
        customer.setMainRoleId(5);
        customer.setPartnerId(partner.getId());
        customer.setStatusId(UserDTOEx.STATUS_ACTIVE);
        customer.setBalanceType(Constants.BALANCE_NO_DYNAMIC);

        ContactWS customerContact = new ContactWS();
        customerContact.setEmail(customer.getUserName() + "@test.com");
        customerContact.setFirstName("Partner Customer");
        customerContact.setLastName("commission rate");
        customer.setContact(customerContact);

        customer.setUserId(api.createUser(customer)); // create user
        assertNotNull("customer created", customer.getUserId());


        // create a payment for the linked customer
        // a partner isn't paid anything until their referral generates some revenue
        PaymentWS payment = new PaymentWS();
        payment.setAmount(new BigDecimal("100.00"));
        payment.setIsRefund(0);
        payment.setMethodId(Constants.PAYMENT_METHOD_CHEQUE);
        payment.setPaymentDate(new Date());
        payment.setResultId(Constants.RESULT_ENTERED);
        payment.setCurrencyId(1);
        payment.setUserId(customer.getUserId());
        payment.setPaymentNotes("Payment for partner commission");
        payment.setPaymentPeriod(1);

        PaymentInfoChequeDTO cheque = new PaymentInfoChequeDTO();
        cheque.setBank("ws bank");
        cheque.setDate(new Date());
        cheque.setNumber("2232-2323-2323");
        payment.setCheque(cheque);

        Integer paymentId = api.applyPayment(payment, null);


        // run payout
        // validate that the partner was paid the %5 commission
        api.processPartnerPayout(partner.getId());
        partner = api.getPartner(partnerId);

        // $100.00 customer payment * 0.05 = $5.00 payout
        assertEquals(1, partner.getPartnerPayouts().size());
        assertEquals(new BigDecimal("5.00"), partner.getDuePayoutAsDecimal());

        PartnerPayoutWS payout = partner.getPartnerPayouts().get(0);
        assertEquals(new BigDecimal("5.00"), payout.getPaymentsAmountAsDecimal());
        assertEquals(BigDecimal.ZERO, payout.getRefundsAmountAsDecimal());
        assertEquals(BigDecimal.ZERO, payout.getBalanceLeftAsDecimal());

        // cleanup
        api.deletePayment(paymentId);
        api.deleteUser(customer.getUserId());
        api.deletePartner(partner.getId());
    }
}
