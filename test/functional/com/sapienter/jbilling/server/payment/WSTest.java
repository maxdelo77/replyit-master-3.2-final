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

package com.sapienter.jbilling.server.payment;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.sapienter.jbilling.server.metafields.MetaFieldValueWS;
import com.sapienter.jbilling.server.process.BillingProcessConfigurationWS;
import junit.framework.TestCase;

import com.sapienter.jbilling.server.entity.AchDTO;
import com.sapienter.jbilling.server.entity.CreditCardDTO;
import com.sapienter.jbilling.server.entity.PaymentInfoChequeDTO;
import com.sapienter.jbilling.server.invoice.InvoiceWS;
import com.sapienter.jbilling.server.order.OrderLineWS;
import com.sapienter.jbilling.server.order.OrderWS;
import com.sapienter.jbilling.server.user.ContactWS;
import com.sapienter.jbilling.server.user.UserDTOEx;
import com.sapienter.jbilling.server.user.UserWS;
import com.sapienter.jbilling.server.util.Constants;
import com.sapienter.jbilling.server.util.api.JbillingAPI;
import com.sapienter.jbilling.server.util.api.JbillingAPIFactory;
import org.joda.time.DateMidnight;
import org.junit.Ignore;
import org.testng.annotations.Test;

import static com.sapienter.jbilling.test.Asserts.*;
import static org.testng.AssertJUnit.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * @author Emil
 */
@Test(groups = { "web-services", "payment" })
public class WSTest {

    @Test
    public void testApplyGet() {
        try {

            JbillingAPI api = JbillingAPIFactory.getAPI();

            /*
             * apply payment
             */
            PaymentWS payment = new PaymentWS();
            payment.setAmount(new BigDecimal("15.00"));
            payment.setIsRefund(new Integer(0));
            payment.setMethodId(Constants.PAYMENT_METHOD_CHEQUE);
            payment.setPaymentDate(Calendar.getInstance().getTime());
            payment.setResultId(Constants.RESULT_ENTERED);
            payment.setCurrencyId(new Integer(1));
            payment.setUserId(new Integer(2));
            payment.setPaymentNotes("Notes");
            payment.setPaymentPeriod(new Integer(1));


            PaymentInfoChequeDTO cheque = new PaymentInfoChequeDTO();
            cheque.setBank("ws bank");
            cheque.setDate(Calendar.getInstance().getTime());
            cheque.setNumber("2232-2323-2323");
            payment.setCheque(cheque);

            System.out.println("Applying payment");
            Integer ret = api.applyPayment(payment, new Integer(35));
            System.out.println("Created payemnt " + ret);
            assertNotNull("Didn't get the payment id", ret);

            /*
             * get
             */
            //verify the created payment
            System.out.println("Getting created payment");
            PaymentWS retPayment = api.getPayment(ret);
            assertNotNull("didn't get payment ", retPayment);
            assertEquals("created payment result", retPayment.getResultId(), payment.getResultId());
            assertEquals("created payment cheque ", retPayment.getCheque().getNumber(), payment.getCheque().getNumber());
            assertEquals("created payment user ", retPayment.getUserId(),  payment.getUserId());
            assertEquals("notes", retPayment.getPaymentNotes(), payment.getPaymentNotes());
            assertEquals("period", retPayment.getPaymentPeriod(), payment.getPaymentPeriod());


            System.out.println("Validated created payment and paid invoice");
            assertNotNull("payment not related to invoice", retPayment.getInvoiceIds());
            assertTrue("payment not related to invoice", retPayment.getInvoiceIds().length == 1);
            assertEquals("payment not related to invoice", retPayment.getInvoiceIds()[0], new Integer(35));

            InvoiceWS retInvoice = api.getInvoiceWS(retPayment.getInvoiceIds()[0]);
            assertNotNull("New invoice not present", retInvoice);
            assertEquals("Balance of invoice should be total of order", BigDecimal.ZERO, retInvoice.getBalanceAsDecimal());
            assertEquals("Total of invoice should be total of order", new BigDecimal("15"), retInvoice.getTotalAsDecimal());
            assertEquals("New invoice not paid", retInvoice.getToProcess(), new Integer(0));
            assertNotNull("invoice not related to payment", retInvoice.getPayments());
            assertTrue("invoice not related to payment", retInvoice.getPayments().length == 1);
            assertEquals("invoice not related to payment", retInvoice.getPayments()[0].intValue(), retPayment.getId());

            /*
             * get latest
             */
            //verify the created payment
            System.out.println("Getting latest");
            retPayment = api.getLatestPayment(new Integer(2));
            assertNotNull("didn't get payment ", retPayment);
            assertEquals("latest id", ret.intValue(), retPayment.getId());
            assertEquals("created payment result", retPayment.getResultId(), payment.getResultId());
            assertEquals("created payment cheque ", retPayment.getCheque().getNumber(), payment.getCheque().getNumber());
            assertEquals("created payment user ", retPayment.getUserId(), payment.getUserId());

            try {
                System.out.println("Getting latest - invalid");
                retPayment = api.getLatestPayment(new Integer(13));
                fail("User 13 belongs to entity 301");
            } catch (Exception e) {
            }

            /*
             * get last
             */
            System.out.println("Getting last");
            Integer retPayments[] = api.getLastPayments(new Integer(2), new Integer(2));
            assertNotNull("didn't get payment ", retPayments);
            // fetch the payment


            retPayment = api.getPayment(retPayments[0]);

            assertEquals("created payment result", retPayment.getResultId(), payment.getResultId());
            assertEquals("created payment cheque ", retPayment.getCheque().getNumber(), payment.getCheque().getNumber());
            assertEquals("created payment user ", retPayment.getUserId(), payment.getUserId());
            assertTrue("No more than two records", retPayments.length <= 2);

            try {
                System.out.println("Getting last - invalid");
                retPayments = api.getLastPayments(new Integer(13),
                        new Integer(2));
                fail("User 13 belongs to entity 301");
            } catch (Exception e) {
            }


            /*
             * TODO test refunds. There are no refund WS methods.
             * Using applyPayment with is_refund = 1 DOES NOT work
             */


        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception caught:" + e);
        }
    }

    /**
     * Test for UserIdFilter, NameFilter, AddressFilter, PhoneFilter,
     * CreditCardFilter and IpAddressFilter
     */
    @Test
    public void testBlacklistFilters() {
        try {
            Integer userId = 1000; // starting user id

            // expected filter response messages
            String[] message = {
                    "User id is blacklisted.",
                    "Name is blacklisted.",
                    "Address is blacklisted.",
                    "Phone number is blacklisted.",
                    "Credit card number is blacklisted.",
                    "IP address is blacklisted." };

            JbillingAPI api = JbillingAPIFactory.getAPI();

            /*
             * Loop through users 1000-1005, which should fail on a respective
             * filter: UserIdFilter, NameFilter, AddressFilter, PhoneFilter,
             * CreditCardFilter or IpAddressFilter
             */
            for(int i = 0; i < 5; i++, userId++) {
                // create a new order and invoice it
                OrderWS order = new OrderWS();
                order.setUserId(userId);
                order.setBillingTypeId(Constants.ORDER_BILLING_PRE_PAID);
                order.setPeriod(2);
                order.setCurrencyId(new Integer(1));
                order.setActiveSince(new Date());

                // add a line
                OrderLineWS lines[] = new OrderLineWS[1];
                OrderLineWS line;
                line = new OrderLineWS();
                line.setTypeId(Constants.ORDER_LINE_TYPE_ITEM);
                line.setQuantity(new Integer(1));
                line.setItemId(new Integer(1));
                line.setUseItem(new Boolean(true));
                lines[0] = line;

                order.setOrderLines(lines);

                // create the order and invoice it
                System.out.println("Creating and invoicing order ...");
                Integer thisInvoiceId = api.createOrderAndInvoice(order);
                InvoiceWS newInvoice = api.getInvoiceWS(thisInvoiceId);
                Integer orderId = newInvoice.getOrders()[0]; // this is the order that was also created
                assertNotNull("The order was not created", orderId);

                // get invoice id
                InvoiceWS invoice = api.getLatestInvoice(userId);
                assertNotNull("Couldn't get last invoice", invoice);
                Integer invoiceId = invoice.getId();
                assertNotNull("Invoice id was null", invoiceId);

                // try paying the invoice
                System.out.println("Trying to pay invoice for blacklisted user ...");
                PaymentAuthorizationDTOEx authInfo = api.payInvoice(invoiceId);
                assertNotNull("Payment result empty", authInfo);

                // check that it was failed by the test blacklist filter
                assertFalse("Payment wasn't failed for user: " + userId, authInfo.getResult().booleanValue());
                assertEquals("Processor response", message[i], authInfo.getResponseMessage());

                // remove invoice and order
                api.deleteInvoice(invoiceId);
                api.deleteOrder(orderId);
            }

        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception caught:" + e);
        }
    }

    @Test
    public void testRemoveOnCCChange() throws Exception {
        JbillingAPI api = JbillingAPIFactory.getAPI();

        final Integer userId = 868; // this is a user with a good CC

        // put a pre-auth record on this user
        PaymentAuthorizationDTOEx auth = api.createOrderPreAuthorize(com.sapienter.jbilling.server.order.WSTest.createMockOrder(userId, 3, new BigDecimal("3.45")));
        Integer orderId = api.getLatestOrder(userId).getId();

        PaymentWS preAuthPayment = api.getPayment(auth.getPaymentId());
        assertThat(preAuthPayment, is(not(nullValue())));
        assertThat(preAuthPayment.getIsPreauth(), is(1));
        assertThat(preAuthPayment.getDeleted(), is(0)); // NOT deleted

        // update the user's credit card, this should remove the old card
        // and delete any associated pre-authorizations
        UserWS user = api.getUserWS(userId);

        CreditCardDTO creditCard = user.getCreditCard();
        creditCard.setName("test-user " + System.currentTimeMillis());
        creditCard.setExpiry(new DateMidnight().plusYears(4).withDayOfMonth(1).toDate());
        api.updateCreditCard(userId, creditCard);

        // validate that the pre-auth payment is no longer available
        preAuthPayment = api.getPayment(auth.getPaymentId());
        assertThat(preAuthPayment, is(not(nullValue())));
        assertThat(preAuthPayment.getIsPreauth(), is(1));
        assertThat(preAuthPayment.getDeleted(), is(1)); // is now deleted

        // clean-up
        pause(5000);
        api.deleteOrder(orderId);
    }

    /**
     * Test for BlacklistUserStatusTask. When a user's status moves to
     * suspended or higher, the user and all their information is
     * added to the blacklist.
     */
    @Test
    public void testBlacklistUserStatus() {
        try {
            final Integer USER_ID = 1006; // user id for testing

            // expected filter response messages
            String[] messages = new String[6];
            messages[0] = "User id is blacklisted.";
            messages[1] = "Name is blacklisted.";
            messages[2] = "Credit card number is blacklisted.";
            messages[3] = "Address is blacklisted.";
            messages[4] = "IP address is blacklisted.";
            messages[5] = "Phone number is blacklisted.";

            JbillingAPI api = JbillingAPIFactory.getAPI();

            // check that a user isn't blacklisted
            UserWS user = api.getUserWS(USER_ID);
            // CXF returns null
            if (user.getBlacklistMatches() != null) {
                assertTrue("User shouldn't be blacklisted yet",
                        user.getBlacklistMatches().length == 0);
            }

            // change their status to suspended
            user.setStatusId(UserDTOEx.STATUS_SUSPENDED);
            user.setPassword(null);
            api.updateUser(user);

            // check all their records are now blacklisted
            user = api.getUserWS(USER_ID);
            assertEquals("User records should be blacklisted.",
                    Arrays.toString(messages),
                    Arrays.toString(user.getBlacklistMatches()));

            // clean-up
            user.setStatusId(UserDTOEx.STATUS_ACTIVE);
            user.setPassword(null);
            api.updateUser(user);

        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception caught:" + e);
        }
    }

    /**
     * Tests the PaymentRouterCurrencyTask.
     */
    @Test
    public void testPaymentRouterCurrencyTask() {
        try {
            final Integer USER_USD = 10730;
            final Integer USER_AUD = 10731;

            JbillingAPI api = JbillingAPIFactory.getAPI();

            // create a new order
            OrderWS order = new OrderWS();
            order.setUserId(USER_USD);
            order.setBillingTypeId(Constants.ORDER_BILLING_PRE_PAID);
            order.setPeriod(2);
            order.setCurrencyId(new Integer(1));
            order.setActiveSince(new Date());

            // add a line
            OrderLineWS lines[] = new OrderLineWS[1];
            OrderLineWS line;
            line = new OrderLineWS();
            line.setTypeId(Constants.ORDER_LINE_TYPE_ITEM);
            line.setQuantity(new Integer(1));
            line.setItemId(new Integer(1));
            line.setUseItem(new Boolean(true));
            lines[0] = line;

            order.setOrderLines(lines);

            // create the order and invoice it
            System.out.println("Creating and invoicing order ...");
            Integer invoiceIdUSD = api.createOrderAndInvoice(order);
            Integer orderIdUSD = api.getLastOrders(USER_USD, 1)[0];

            // try paying the invoice in USD
            System.out.println("Making payment in USD...");
            PaymentAuthorizationDTOEx authInfo = api.payInvoice(invoiceIdUSD);

            assertTrue("USD Payment should be successful", authInfo.getResult().booleanValue());
            assertEquals("Should be processed by 'first_fake_processor'", authInfo.getProcessor(), "first_fake_processor");

            // create a new order in AUD and invoice it
            order.setUserId(USER_AUD);
            order.setCurrencyId(11);

            System.out.println("Creating and invoicing order ...");
            Integer invoiceIdAUD = api.createOrderAndInvoice(order);
            Integer orderIdAUD = api.getLastOrders(USER_AUD, 1)[0];

            // try paying the invoice in AUD
            System.out.println("Making payment in AUD...");
            authInfo = api.payInvoice(invoiceIdAUD);

            assertTrue("AUD Payment should be successful", authInfo.getResult().booleanValue());
            assertEquals("Should be processed by 'second_fake_processor'", authInfo.getProcessor(), "second_fake_processor");

            // remove invoices and orders
            System.out.println("Deleting invoices and orders.");
            api.deleteInvoice(invoiceIdUSD);
            api.deleteInvoice(invoiceIdAUD);
            api.deleteOrder(orderIdUSD);
            api.deleteOrder(orderIdAUD);

        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception caught:" + e);
        }
    }

    @Test
    public void testPayInvoice() {
        try {
            final Integer USER = 1072;

            JbillingAPI api = JbillingAPIFactory.getAPI();

            System.out.println("Getting an invoice paid, and validating the payment.");
            OrderWS order = com.sapienter.jbilling.server.order.WSTest.createMockOrder(USER, 3, new BigDecimal("3.45"));
            Integer invoiceId = api.createOrderAndInvoice(order);
            PaymentAuthorizationDTOEx auth = api.payInvoice(invoiceId);
            assertNotNull("auth can not be null", auth);
            PaymentWS payment  = api.getLatestPayment(USER);
            assertNotNull("payment can not be null", payment);
            assertNotNull("auth in payment can not be null", payment.getAuthorizationId());

            api.deleteInvoice(invoiceId);

            pause(5000); // db flush
            api.deleteOrder(api.getLatestOrder(USER).getId());

        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception caught:" + e);
        }
    }

    private void pause(long t) {
        System.out.println("pausing for " + t + " ms...");
        try {
            Thread.sleep(t);
        } catch (InterruptedException e) {
        }
    }

    @Test
    public void testProcessPayment(){
        try {
            JbillingAPI api = JbillingAPIFactory.getAPI();
            final Integer USER_ID = new Integer(1071);

            // first, create two unpaid invoices
            OrderWS order = com.sapienter.jbilling.server.order.WSTest.createMockOrder(USER_ID, 1, new BigDecimal("10.00"));
            Integer invoiceId1 = api.createOrderAndInvoice(order);
            Integer invoiceId2 = api.createOrderAndInvoice(order);

            // create the payment
            PaymentWS payment = new PaymentWS();
            payment.setAmount(new BigDecimal("5.00"));
            payment.setIsRefund(new Integer(0));
            payment.setMethodId(Constants.PAYMENT_METHOD_VISA);
            payment.setPaymentDate(Calendar.getInstance().getTime());
            payment.setCurrencyId(new Integer(1));
            payment.setUserId(USER_ID);


            /*
             * try a credit card number that fails
             */

            // note that creating a payment with a NEW credit card will save it and associate
            // it with the user who made the payment.

            CreditCardDTO cc = new CreditCardDTO();
            cc.setName("Frodo Baggins");
            cc.setNumber("4111111111111111");
            cc.setType(Constants.PAYMENT_METHOD_VISA);
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.YEAR, 5);
            cc.setExpiry(cal.getTime());
            payment.setCreditCard(cc);

            System.out.println("processing payment.");
            PaymentAuthorizationDTOEx authInfo = api.processPayment(payment, null);

            // check payment failed
            assertNotNull("Payment result not null", authInfo);
            assertFalse("Payment Authorization result should be FAILED", authInfo.getResult().booleanValue());

            // check payment has zero balance
            PaymentWS lastPayment = api.getLatestPayment(USER_ID);
            assertNotNull("payment can not be null", lastPayment);
            assertNotNull("auth in payment can not be null", lastPayment.getAuthorizationId());
            assertEquals("correct payment amount", new BigDecimal("5"), lastPayment.getAmountAsDecimal());
            assertEquals("correct payment balance", BigDecimal.ZERO, lastPayment.getBalanceAsDecimal());

            // check invoices still have balance
            InvoiceWS invoice1 = api.getInvoiceWS(invoiceId1);
            assertEquals("correct invoice balance", new BigDecimal("10.0"), invoice1.getBalanceAsDecimal());
            InvoiceWS invoice2 = api.getInvoiceWS(invoiceId1);
            assertEquals("correct invoice balance", new BigDecimal("10.0"), invoice2.getBalanceAsDecimal());

            // do it again, but using the credit card on file
            // which is also 41111111111111
            payment.setCreditCard(null);
            System.out.println("processing payment.");
            authInfo = api.processPayment(payment, null);
            // check payment has zero balance
            PaymentWS lastPayment2 = api.getLatestPayment(USER_ID);
            assertNotNull("payment can not be null", lastPayment2);
            assertNotNull("auth in payment can not be null", lastPayment2.getAuthorizationId());
            assertEquals("correct payment amount", new BigDecimal("5"), lastPayment2.getAmountAsDecimal());
            assertEquals("correct payment balance", BigDecimal.ZERO, lastPayment2.getBalanceAsDecimal());
            assertFalse("Payment is not the same as preiouvs", lastPayment2.getId() == lastPayment.getId());

            // check invoices still have balance
            invoice1 = api.getInvoiceWS(invoiceId1);
            assertEquals("correct invoice balance", new BigDecimal("10"), invoice1.getBalanceAsDecimal());
            invoice2 = api.getInvoiceWS(invoiceId1);
            assertEquals("correct invoice balance", new BigDecimal("10"), invoice2.getBalanceAsDecimal());


            /*
             * do a successful payment of $5
             */
            cc.setNumber("4111111111111152");
            payment.setCreditCard(cc);
            System.out.println("processing payment.");
            authInfo = api.processPayment(payment, null);

            // check payment successful
            assertNotNull("Payment result not null", authInfo);
            assertNotNull("Auth id not null", authInfo.getId());
            assertTrue("Payment Authorization result should be OK", authInfo.getResult().booleanValue());

            // check payment was made
            lastPayment = api.getLatestPayment(USER_ID);
            assertNotNull("payment can not be null", lastPayment);
            assertNotNull("auth in payment can not be null", lastPayment.getAuthorizationId());
            assertEquals("payment ids match", lastPayment.getId(), authInfo.getPaymentId().intValue());
            assertEquals("correct payment amount", new BigDecimal("5"), lastPayment.getAmountAsDecimal());
            assertEquals("correct payment balance", BigDecimal.ZERO, lastPayment.getBalanceAsDecimal());

            // check invoice 1 was partially paid (balance 5)
            invoice1 = api.getInvoiceWS(invoiceId1);
            assertEquals("correct invoice balance", new BigDecimal("5.0"), invoice1.getBalanceAsDecimal());

            // check invoice 2 wan't paid at all
            invoice2 = api.getInvoiceWS(invoiceId2);
            assertEquals("correct invoice balance", new BigDecimal("10.0"), invoice2.getBalanceAsDecimal());


            /*
             * another payment for $10, this time with the user's credit card
             */
            // update the credit card to the one that is good
            UserWS user = api.getUserWS(USER_ID);
            CreditCardDTO originalCreditCard = user.getCreditCard();

            CreditCardDTO userCard = user.getCreditCard();
            userCard.setNumber("4111111111111152");
            api.updateCreditCard(USER_ID, userCard);

            // process a payment without an attached credit card
            // should try and use the user's saved credit card
            payment.setCreditCard(null);
            payment.setAmount(new BigDecimal("10.00"));
            System.out.println("processing payment.");
            authInfo = api.processPayment(payment, null);

            // check payment successful
            // todo: fails here!!
            // for some reason this is processing with credit card "4111111111111111"
            assertNotNull("Payment result not null", authInfo);
            assertTrue("Payment Authorization result should be OK", authInfo.getResult().booleanValue());

            // check payment was made
            lastPayment = api.getLatestPayment(USER_ID);
            assertNotNull("payment can not be null", lastPayment);
            assertNotNull("auth in payment can not be null", lastPayment.getAuthorizationId());
            assertEquals("correct payment amount", new BigDecimal("10"), lastPayment.getAmountAsDecimal());
            assertEquals("correct payment balance", BigDecimal.ZERO, lastPayment.getBalanceAsDecimal());

            // check invoice 1 is fully paid (balance 0)
            invoice1 = api.getInvoiceWS(invoiceId1);
            assertEquals("correct invoice balance", BigDecimal.ZERO, invoice1.getBalanceAsDecimal());

            // check invoice 2 was partially paid (balance 5)
            invoice2 = api.getInvoiceWS(invoiceId2);
            assertEquals("correct invoice balance", new BigDecimal("5"), invoice2.getBalanceAsDecimal());


            /*
             *another payment for $10
             */
            payment.setCreditCard(cc);
            payment.setAmount(new BigDecimal("10.00"));
            System.out.println("processing payment.");
            authInfo = api.processPayment(payment, null);

            // check payment successful
            assertNotNull("Payment result not null", authInfo);
            assertTrue("Payment Authorization result should be OK", authInfo.getResult().booleanValue());

            // check payment was made
            lastPayment = api.getLatestPayment(USER_ID);
            assertNotNull("payment can not be null", lastPayment);
            assertNotNull("auth in payment can not be null", lastPayment.getAuthorizationId());
            assertEquals("correct  payment amount", new BigDecimal("10"), lastPayment.getAmountAsDecimal());
            assertEquals("correct  payment balance", new BigDecimal("5"), lastPayment.getBalanceAsDecimal());

            // check invoice 1 balance is unchanged
            invoice1 = api.getInvoiceWS(invoiceId1);
            assertEquals("correct invoice balance", BigDecimal.ZERO, invoice1.getBalanceAsDecimal());

            // check invoice 2 is fully paid (balance 0)
            invoice2 = api.getInvoiceWS(invoiceId2);
            assertEquals("correct invoice balance", BigDecimal.ZERO, invoice2.getBalanceAsDecimal());


            // clean up
            api.updateCreditCard(USER_ID, originalCreditCard);
            System.out.println("Deleting invoices and orders.");
            api.deleteInvoice(invoice1.getId());
            api.deleteInvoice(invoice2.getId());
            api.deleteOrder(invoice1.getOrders()[0]);
            api.deleteOrder(invoice2.getOrders()[0]);

        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception caught:" + e);
        }
    }

    @Test
    public void testAchFakePayments() throws Exception {

        JbillingAPI api = JbillingAPIFactory.getAPI();
        UserWS newUser = createUser();
        newUser.setCreditCard(null);

        System.out.println("Creating user with ACH record and no CC...");
        Integer userId = api.createUser(newUser);

        newUser = api.getUserWS(userId);
        AchDTO ach = newUser.getAch();
        // CreditCardDTO cc = newUser.getCreditCard();

        System.out.println("Testing ACH payment with even amount (should pass)");
        PaymentWS payment = new PaymentWS();
        payment.setAmount(new BigDecimal("15.00"));
        payment.setIsRefund(new Integer(0));
        payment.setMethodId(Constants.PAYMENT_METHOD_ACH);
        payment.setPaymentDate(Calendar.getInstance().getTime());
        payment.setResultId(Constants.RESULT_ENTERED);
        payment.setCurrencyId(new Integer(1));
        payment.setUserId(newUser.getUserId());
        payment.setPaymentNotes("Notes");
        payment.setPaymentPeriod(new Integer(1));
        payment.setAch(ach);

        PaymentAuthorizationDTOEx result = api.processPayment(payment, null);
        assertEquals("ACH payment with even amount should pass",
                Constants.RESULT_OK, api.getPayment(result.getPaymentId()).getResultId());

        System.out.println("Testing ACH payment with odd amount (should fail)");
        payment = new PaymentWS();
        payment.setAmount(new BigDecimal("15.01"));
        payment.setIsRefund(new Integer(0));
        payment.setMethodId(Constants.PAYMENT_METHOD_ACH);
        payment.setPaymentDate(Calendar.getInstance().getTime());
        payment.setResultId(Constants.RESULT_ENTERED);
        payment.setCurrencyId(new Integer(1));
        payment.setUserId(newUser.getUserId());
        payment.setPaymentNotes("Notes");
        payment.setPaymentPeriod(new Integer(1));
        payment.setAch(ach);

        result = api.processPayment(payment, null);
        assertEquals("ACH payment with odd amount should fail",
                Constants.RESULT_FAIL, api.getPayment(result.getPaymentId()).getResultId());
    }

    private UserWS createUser() {

        UserWS newUser = new UserWS();
        newUser.setUserName("testUserName-" + Calendar.getInstance().getTimeInMillis());
        newUser.setPassword("asdfasdf1");
        newUser.setLanguageId(new Integer(1));
        newUser.setMainRoleId(new Integer(5));
        newUser.setParentId(null);
        newUser.setStatusId(UserDTOEx.STATUS_ACTIVE);
        newUser.setCurrencyId(null);
        newUser.setBalanceType(Constants.BALANCE_NO_DYNAMIC);

        MetaFieldValueWS metaField1 = new MetaFieldValueWS();
        metaField1.setFieldName("partner.prompt.fee");
        metaField1.setValue("serial-from-ws");

        MetaFieldValueWS metaField2 = new MetaFieldValueWS();
        metaField2.setFieldName("ccf.payment_processor");
        metaField2.setValue("FAKE_2"); // the plug-in parameter of the processor

        newUser.setMetaFields(new MetaFieldValueWS[]{metaField1, metaField2});

        // add a contact
        ContactWS contact = new ContactWS();
        contact.setEmail(newUser.getUserName() + "@shire.com");
        contact.setFirstName("Frodo");
        contact.setLastName("Baggins");
        newUser.setContact(contact);

        // add a credit card
        CreditCardDTO cc = new CreditCardDTO();
        cc.setName("Frodo Baggins");
        cc.setNumber("4111111111111152");
        Calendar expiry = Calendar.getInstance();
        expiry.set(Calendar.YEAR, expiry.get(Calendar.YEAR) + 1);
        cc.setExpiry(expiry.getTime());

        newUser.setCreditCard(cc);

        AchDTO ach = new AchDTO();
        ach.setAbaRouting("123456789");
        ach.setAccountName("Frodo Baggins");
        ach.setAccountType(Integer.valueOf(1));
        ach.setBankAccount("123456789");
        ach.setBankName("Shire Financial Bank");

        newUser.setAch(ach);

        return newUser;
	}

    @Test
    public void testPayReviewInvoice() throws Exception{
        JbillingAPI api = JbillingAPIFactory.getAPI();

        //creating new user
        UserWS newUser = createUser();
        System.out.println("Creating new user with credit card..");
        Integer userId = api.createUser(newUser);
        System.out.println("User created : " + userId);
        assertNotNull("User created", userId);

        //setup order
        OrderWS order = new OrderWS();
        order.setUserId(userId);
        order.setBillingTypeId(Constants.ORDER_BILLING_PRE_PAID);
        order.setPeriod(2); // period monthly
        order.setCurrencyId(1);
        order.setActiveSince(new DateMidnight(2006, 9, 1).toDate());

        OrderLineWS line = new OrderLineWS();
        line.setTypeId(Constants.ORDER_LINE_TYPE_ITEM);
        line.setDescription("Order line");
        line.setItemId(1);
        line.setQuantity(1);
        line.setPrice(new BigDecimal("10.00"));
        line.setAmount(new BigDecimal("10.00"));
        order.setOrderLines(new OrderLineWS[] { line });

        System.out.println("Creating an order..");
        Integer orderId = api.createOrder(order);
        System.out.println("Order created : " + orderId);
        assertNotNull("Order created", orderId);

        //update billing config to generate review report
        updateBillingConfig(api, new DateMidnight(2006, 10, 27).toDate());

        //run billing
        System.out.println("Run billing on  "+new DateMidnight(2006, 10, 27).toDate());
        api.triggerBilling(new DateMidnight(2006, 10, 27).toDate());

        //pause for 1 minute for billing process to finish
        Thread.sleep(60*1000);

        //get the generated invoice
        Integer invoiceId = api.getBillingProcessGeneratedInvoices(api.getReviewBillingProcess().getId()).get(0);
        System.out.println("Invoice generated : " + invoiceId);
        assertNotNull("Invoice generated", invoiceId);

        InvoiceWS invoice = api.getInvoiceWS(invoiceId);

        //check if invoice is a review invoice
        System.out.println("Invoice is review : "+invoice.getIsReview());
        assertEquals("Invoice is a review invoice",new Integer(1), invoice.getIsReview());

        PaymentAuthorizationDTOEx auth=null;
        try{
        //pay for a review invoice
        auth = api.payInvoice(invoice.getId());
        System.out.println("Payment authorization : "+auth);
        } catch (Exception e){
            System.out.println("Exception caught:" + e);
        }

        assertNull("Payment auth must be null",auth);

        //clean up
        api.deleteInvoice(invoiceId);
        api.deleteOrder(orderId);
        api.setReviewApproval(Boolean.FALSE);
    }

    private void updateBillingConfig(JbillingAPI api, Date date) {
        BillingProcessConfigurationWS config = api.getBillingProcessConfiguration();

        config.setNextRunDate(date);
        config.setRetries(1);
        config.setDaysForRetry(5);
        config.setGenerateReport(1);     //review report true
        config.setAutoPayment(0);
        config.setAutoPaymentApplication(1);
        config.setDfFm(0);
        config.setDueDateUnitId(Constants.PERIOD_UNIT_MONTH);
        config.setDueDateValue(1);
        config.setInvoiceDateProcess(1);
        config.setMaximumPeriods(99);

        System.out.println("Updating billing run date to : " + date);
        api.createUpdateBillingProcessConfiguration(config);
	}
}
