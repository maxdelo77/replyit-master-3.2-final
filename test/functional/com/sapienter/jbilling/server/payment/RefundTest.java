/*
 JBILLING CONFIDENTIAL
 _____________________

 [2003] - [2012] Enterprise jBilling Software Ltd.
 All Rights Reserved.

 NOTICE:  All information contained herein is, and remains
 the property of Enterprise jBilling Software.
 The intellectual and technical concepts contained
 herein are proprietary to Enterprise jBilling Software
 and are protected by trade secret or copyright law.
 Dissemination of this information or reproduction of this material
 is strictly forbidden.
 */

package com.sapienter.jbilling.server.payment;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;

import com.sapienter.jbilling.server.metafields.MetaFieldValueWS;
import junit.framework.TestCase;

import com.sapienter.jbilling.common.SessionInternalError;
import com.sapienter.jbilling.server.entity.CreditCardDTO;
import com.sapienter.jbilling.server.entity.PaymentInfoChequeDTO;
import com.sapienter.jbilling.server.invoice.InvoiceWS;
import com.sapienter.jbilling.server.order.OrderLineWS;
import com.sapienter.jbilling.server.order.OrderWS;
import com.sapienter.jbilling.server.payment.PaymentWS;
import com.sapienter.jbilling.server.user.ContactWS;
import com.sapienter.jbilling.server.user.UserBL;
import com.sapienter.jbilling.server.user.UserDTOEx;
import com.sapienter.jbilling.server.user.UserWS;
import com.sapienter.jbilling.server.util.Constants;
import com.sapienter.jbilling.server.util.api.JbillingAPI;
import com.sapienter.jbilling.server.util.api.JbillingAPIException;
import com.sapienter.jbilling.server.util.api.JbillingAPIFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.sapienter.jbilling.test.Asserts.*;
import static org.testng.AssertJUnit.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * jUnit Test cases for jBilling's refund funcationality
 * @author Vikas Bodani
 * @since 04/01/12
 */
@Test(groups = { "integration", "payment" })
public class RefundTest {

    private JbillingAPI api;

    @BeforeClass
    protected void setUp() throws Exception {
        api = JbillingAPIFactory.getAPI();
    }

    /**
     * 1. Simplest test scenario - A refund affects linked payments balance.
     */
    @Test
    public void testRefundPayment() {

        System.out.println("testRefundPayment().");
        try {
            //create user
            UserWS user= createUser(false, null, null);
            assertTrue(user.getUserId() > 0);
            System.out.println("User created successfully " + user.getUserId());

            //make payment
            Integer paymentId= createPayment(api, "100.00", false, user.getUserId(), null);
            System.out.println("Created payment " + paymentId);
            assertNotNull("Didn't get the payment id", paymentId);

            //check payment balance = payment amount
            PaymentWS payment= api.getPayment(paymentId);
            assertNotNull(payment);
            assertEquals(payment.getAmountAsDecimal(), payment.getBalanceAsDecimal());

            assertTrue(payment.getInvoiceIds().length == 0 );

            //create refund for above payment, refund amount = payment amount
            Integer refundId= createPayment(api, "100.00", true, user.getUserId(), paymentId);
            System.out.println("Created refund " + refundId);
            assertNotNull("Didn't get the payment id", refundId);

            //check payment balance = 0
            payment= api.getPayment(paymentId);
            assertNotNull(payment);
            assertEquals(BigDecimal.ZERO, payment.getBalanceAsDecimal());

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

//    /**
//     * 2. A refund should bring the User's balance to its Original value before payment
//     */
//   public void testRefundUserBalanceUnchanged() {
//       System.out.println("testRefundUserBalanceUnchanged()");
//        try {
//            //create user
//            UserWS user= createUser(false, null, null);
//            assertTrue(user.getUserId() > 0);
//            System.out.println("User created successfully " + user.getUserId());
//
//            user= api.getUserWS(user.getUserId());
//            UserBL userBl= new UserBL(user.getUserId());
//            assertNotNull("This should not be null",userBl);
//            assertEquals(userBl.getBalance(user.getUserId()), BigDecimal.ZERO);
//
//            //make payment
//            Integer paymentId= createPayment(api, "100.00", false, user.getUserId(), null);
//            System.out.println("Created payment " + paymentId);
//            assertNotNull("Didn't get the payment id", paymentId);
//
//            //check payment balance = payment amount
//            PaymentWS payment= api.getPayment(paymentId);
//            assertNotNull(payment);
//            assertEquals(payment.getAmountAsDecimal(), payment.getBalanceAsDecimal());
//
//            //check user's balance
//            user= api.getUserWS(user.getUserId());
//            BigDecimal userBalance= userBl.getBalance(user.getUserId());
//            assertNotNull(userBalance);
//            assertTrue("User Balance should have been negetive", BigDecimal.ZERO.compareTo(userBalance) > 0);
//
//            assertTrue(payment.getInvoiceIds().length == 0 );
//
//            //create refund for above payment, refund amount = payment amount
//            Integer refundId= createPayment(api, "100.00", true, user.getUserId(), paymentId);
//            System.out.println("Created refund " + refundId);
//            assertNotNull("Didn't get the payment id", refundId);
//
//            //check user's balance = 0
//            user = api.getUserWS(user.getUserId());
//            assertNotNull(user);
//            assertEquals(BigDecimal.ZERO, userBl.getBalance(user.getUserId()));
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            fail(e.getMessage());
//        }
//   }

    /**
     * 3. A refund must link to a Payment ID (negetive)
     * because a refund is only issued against a surplus
     */
    @Test
    public void testRefundFailWhenNoPaymentLinked() {
        System.out.println("testRefundFailWhenNoPaymentLinked()");
        try {
            //create user
            UserWS user= createUser(false, null, null);
            assertTrue(user.getUserId() > 0);
            System.out.println("User created successfully " + user.getUserId());

            //create refund with no payment set
            try {
                Integer refundId= createPayment(api, "100.00", true, user.getUserId(), null);
                System.out.println("Returned refund payment id." + refundId);
                assertNull("Refund payment got created without linked payment id.", refundId);
            } catch (SessionInternalError e) {
                //check for validation error
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    /**
     * 4. Test payment balance unchanged when linked payment has zero balance and linked invoices,
     * but invoice balance increased from previous balance
     */
    @Test
    public void testRefundPaymentWithInvoiceLinked() {
       System.out.println("testRefundPaymentWithInvoiceLinked()");
        try {
            //CREATE USER
            UserWS user= createUser(false, null, null);
            assertTrue(user.getUserId() > 0);
            System.out.println("User created successfully " + user.getUserId());

            //CREATE ORDER & INVOICE
            Integer invoiceId= createOrderAndInvoice(api, user.getUserId());
            assertNotNull(invoiceId);

            //check invoice balance greater then zero
            InvoiceWS invoice= api.getLatestInvoice(user.getUserId());
            assertNotNull(invoice);
            assertTrue(invoice.getBalanceAsDecimal().compareTo(BigDecimal.ZERO) > 0 );

            //MAKE PAYMENT
            Integer paymentId= createPayment(api, "100.00", false, user.getUserId(), null);
            System.out.println("Created payment " + paymentId);
            assertNotNull("Didn't get the payment id", paymentId);

            //check invoice balance is zero
            invoice= api.getInvoiceWS(invoice.getId());
            assertNotNull(invoice);
            assertEquals(invoice.getBalanceAsDecimal(), BigDecimal.ZERO );

            //check payment balance = zero since invoice paid
            PaymentWS payment= api.getPayment(paymentId);
            assertNotNull(payment);
            assertEquals(BigDecimal.ZERO, payment.getBalanceAsDecimal());

            //payment has linked invoices
            assertTrue(payment.getInvoiceIds().length > 0 );

            //CREATE REFUND for above payment, refund amount = payment amount
            Integer refundId= null;
            try {
                createPayment(api, "100.00", true, user.getUserId(), paymentId);
                fail("Cannot refund a linked payment.");
            } catch (Exception e) {
                System.out.println("Is SessionInternalError: " + (e instanceof SessionInternalError));
            }
            
            for(Integer invIds : payment.getInvoiceIds()){
              api.removePaymentLink(invIds,paymentId);
            }
            
            System.out.println("Succesfully unlnked payment from Invoice");
            refundId= createPayment(api, "100.00", true, user.getUserId(), paymentId);
            System.out.println("Created refund " + refundId);
            assertNotNull("Didn't get the payment id", refundId);

            //check payment balance = 0
            payment= api.getPayment(paymentId);
            assertNotNull(payment);
            assertEquals(BigDecimal.ZERO, payment.getBalanceAsDecimal());

            //check invoice balance is greater than zero
            invoice= api.getInvoiceWS(invoice.getId());
            assertNotNull(invoice);
            assertTrue(invoice.getBalanceAsDecimal().compareTo(BigDecimal.ZERO) > 0);

            //invoice balance is equal to its total
            assertEquals(invoice.getBalanceAsDecimal(), invoice.getTotalAsDecimal());

            System.out.println("Invoice balance is " + invoice.getBalance());

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    /**
     * Refund a payment that is linked to one invoice, paying it in full, but
     * having some balance left. Result: payment balance is Refund amount less amount used to pay invoice originally.
     * Invoice balance is equal to its total (used to be zero).
     */
    @Test
    public void testRefundWithPaymentBalance() {
        System.out.println("testRefundWithPaymentBalance()");

        try {
            //CREATE USER
            UserWS user= createUser(false, null, null);
            assertTrue(user.getUserId() > 0);
            System.out.println("User created successfully " + user.getUserId());

            //CREATE ORDER & INVOICE
            Integer invoiceId= createOrderAndInvoice(api, user.getUserId());
            assertNotNull(invoiceId);

            //check invoice balance greater then zero
            InvoiceWS invoice= api.getLatestInvoice(user.getUserId());
            assertNotNull(invoice);
            assertTrue(invoice.getBalanceAsDecimal().compareTo(BigDecimal.ZERO) > 0 );

            //MAKE PAYMENT
            Integer paymentId= createPayment(api, "200.00", false, user.getUserId(), null);
            System.out.println("Created payment " + paymentId);
            assertNotNull("Didn't get the payment id", paymentId);

            //check invoice balance is zero
            invoice= api.getInvoiceWS(invoice.getId());
            assertNotNull(invoice);
            assertEquals(invoice.getBalanceAsDecimal(), BigDecimal.ZERO );

            //check payment balance > zero since balance left after invoice paid
            PaymentWS payment= api.getPayment(paymentId);
            assertNotNull(payment);
            assertTrue(payment.getBalanceAsDecimal().compareTo(BigDecimal.ZERO) > 0 );
            assertEquals(new BigDecimal("100.00"), payment.getBalanceAsDecimal());

            //payment has linked invoices
            assertTrue(payment.getInvoiceIds().length > 0 );

            //CREATE REFUND for above payment, refund amount = payment amount
            Integer refundId= createPayment(api, "100.00", true, user.getUserId(), paymentId);
            System.out.println("Created refund " + refundId);
            assertNotNull("Didn't get the payment id", refundId);

            //check payment balance = 0
            payment= api.getPayment(paymentId);
            assertNotNull(payment);
            assertEquals(BigDecimal.ZERO, payment.getBalanceAsDecimal());

            //check invoice balance is greater than zero
            invoice= api.getInvoiceWS(invoice.getId());
            assertNotNull(invoice);
            assertEquals(invoice.getBalanceAsDecimal(), BigDecimal.ZERO );

            System.out.println("Invoice balance is " + invoice.getBalance());

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    /**
     * Refund a payment that is linked to many invoices, paying some partially,
     * some in full (uses the whole balance of the payment). Result: payment
     * balance remains zero. Invoice balance for each invoice = balance + amount
     * paid by the payment.
     */
    @Test
    public void testRefundPaymentLinkedManyInvoices() {
        System.out.println("testRefundPaymentLinkedManyInvoices()");

        try {
            //CREATE USER
            UserWS user= createUser(false, null, null);
            assertTrue(user.getUserId() > 0);
            System.out.println("User created successfully " + user.getUserId());

            //CREATE ORDER & INVOICE 1
            Integer invoiceId= createOrderAndInvoice(api, user.getUserId());
            assertNotNull(invoiceId);

            //2
            invoiceId= createOrderAndInvoice(api, user.getUserId());
            assertNotNull(invoiceId);

            //3
            invoiceId= createOrderAndInvoice(api, user.getUserId());
            assertNotNull(invoiceId);

            //check invoice balance greater then zero
            InvoiceWS invoice= api.getLatestInvoice(user.getUserId());
            assertNotNull(invoice);
            assertTrue(invoice.getBalanceAsDecimal().compareTo(BigDecimal.ZERO) > 0 );

            //MAKE PAYMENT
            Integer paymentId= createPayment(api, "300.00", false, user.getUserId(), null);
            System.out.println("Created payment " + paymentId);
            assertNotNull("Didn't get the payment id", paymentId);

            //check invoice balance is zero
            invoice= api.getInvoiceWS(invoice.getId());
            assertNotNull(invoice);
            assertEquals(invoice.getBalanceAsDecimal(), BigDecimal.ZERO );

            //check payment balance = zero since invoice paid
            PaymentWS payment= api.getPayment(paymentId);
            assertNotNull(payment);
            assertEquals(BigDecimal.ZERO, payment.getBalanceAsDecimal());

            //payment has linked invoices
            assertTrue(payment.getInvoiceIds().length == 3 );

            //CREATE REFUND for above payment, refund amount = payment amount
            Integer refundId= null;
            try{
                createPayment(api, "300.00", true, user.getUserId(), paymentId);
                fail("Refund should not have succeded.");
            } catch(Exception e) {
                System.out.println("Exception thrown: " + e.getClass().getSimpleName());
            }

            System.out.println("Invoice balance is.. " + invoice.getBalance());

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    /*
     * Deleting a Payment that has been refunded must fail.
     */

    /**
     * Cannot delete payment that has been refunded (negetive)
     */
    @Test
    public void testDeletePaymentThatHasRefund() {
        System.out.println("testDeletePaymentThatHasRefund()");
        try {
            //create user
            UserWS user= createUser(false, null, null);
            assertTrue(user.getUserId() > 0);
            System.out.println("User created successfully " + user.getUserId());

            //make payment
            Integer paymentId= createPayment(api, "100.00", false, user.getUserId(), null);
            System.out.println("Created payment " + paymentId);
            assertNotNull("Didn't get the payment id", paymentId);

            //check payment balance = payment amount
            PaymentWS payment= api.getPayment(paymentId);
            assertNotNull(payment);
            assertEquals(payment.getAmountAsDecimal(), payment.getBalanceAsDecimal());

            assertTrue(payment.getInvoiceIds().length == 0 );

            //create refund for above payment, refund amount = payment amount
            Integer refundId= createPayment(api, "100.00", true, user.getUserId(), paymentId);
            System.out.println("Created refund " + refundId);
            assertNotNull("Didn't get the payment id", refundId);

            //check payment balance = 0
            payment= api.getPayment(paymentId);
            assertNotNull(payment);
            assertEquals(BigDecimal.ZERO, payment.getBalanceAsDecimal());

            try {
                api.deletePayment(paymentId);
                fail();
            } catch (Exception e) {
                //expected
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testNegativeAmountRefundPayment(){
        System.out.println("testNegativeAmountRefundPayment().");
        Integer refundId = null;
        try {
            //create user
            UserWS user= createUser(false, null, null);
            assertTrue(user.getUserId() > 0);
            System.out.println("User created successfully " + user.getUserId());

            //make payment
            Integer paymentId= createPayment(api, "100.00", false, user.getUserId(), null);
            System.out.println("Created payment " + paymentId);
            assertNotNull("Didn't get the payment id", paymentId);

            //make refund payment with negative amount
            refundId= createPayment(api, "-100.00", true, user.getUserId(), paymentId);
            System.out.println("Created refund " + refundId);
            assertNotNull("Didn't get the refund payment id", refundId);

            //clean up
            api.deleteUser(user.getUserId());
        } catch (Exception e) {
            System.out.println("Exception caught : "+e);
        }

        assertNull("Can't make refund payment for negative amount", refundId);
    }

    //Helper method to create user
    private static UserWS createUser(boolean goodCC, Integer parentId, Integer currencyId) throws JbillingAPIException, IOException {
        System.out.println("createUser called");
        JbillingAPI api = JbillingAPIFactory.getAPI();

        /*
        * Create - This passes the password validation routine.
        */
        UserWS newUser = new UserWS();
        newUser.setUserId(0); // it is validated
        newUser.setUserName("refund-test-" + Calendar.getInstance().getTimeInMillis());
        newUser.setPassword("asdfasdf1");
        newUser.setLanguageId(new Integer(1));
        newUser.setMainRoleId(new Integer(5));
        newUser.setParentId(parentId); // this parent exists
        newUser.setStatusId(UserDTOEx.STATUS_ACTIVE);
        newUser.setCurrencyId(currencyId);
        newUser.setBalanceType(Constants.BALANCE_NO_DYNAMIC);
        newUser.setInvoiceChild(new Boolean(false));

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
        cc.setNumber(goodCC ? "4111111111111152" : "4111111111111111");

        // valid credit card must have a future expiry date to be valid for payment processing
        Calendar expiry = Calendar.getInstance();
        expiry.set(Calendar.YEAR, expiry.get(Calendar.YEAR) + 1);
        cc.setExpiry(expiry.getTime());

        newUser.setCreditCard(cc);

        System.out.println("Creating user ...");
        newUser.setUserId(api.createUser(newUser));

        return newUser;
    }

    //Helper method to create payment
    private static Integer createPayment(JbillingAPI api, String amount, boolean isRefund, Integer userId, Integer linkedPaymentId) {
        PaymentWS payment = new PaymentWS();
        payment.setAmount(new BigDecimal(amount));
        payment.setIsRefund(isRefund? new Integer(1): new Integer(0));
        payment.setMethodId(Constants.PAYMENT_METHOD_CHEQUE);
        payment.setPaymentDate(Calendar.getInstance().getTime());
        payment.setResultId(Constants.RESULT_ENTERED);
        payment.setCurrencyId(new Integer(1));
        payment.setUserId(userId);
        payment.setPaymentNotes("Notes");
        payment.setPaymentPeriod(new Integer(1));
        payment.setPaymentId(linkedPaymentId);

        PaymentInfoChequeDTO cheque = new PaymentInfoChequeDTO();
        cheque.setBank("ws bank");
        cheque.setDate(Calendar.getInstance().getTime());
        cheque.setNumber("2232-2323-2323");
        payment.setCheque(cheque);

        System.out.println("Creating " + (isRefund? " refund." : " payment.") );
        Integer ret = api.createPayment(payment);
        return ret;
    }

    //Helper method to create order and invoice
    private static Integer createOrderAndInvoice(JbillingAPI api, Integer userId) {

        Integer invoiceId= null;

        try {
            OrderWS newOrder = new OrderWS();
            newOrder.setUserId(userId);
            newOrder.setBillingTypeId(Constants.ORDER_BILLING_PRE_PAID);
            newOrder.setPeriod(Constants.ORDER_PERIOD_ONCE);
            newOrder.setCurrencyId(new Integer(1));
            // notes can only be 200 long... but longer should not fail
            newOrder.setNotes("Lorem ipsum text.");

            Calendar cal = Calendar.getInstance();
            cal.clear();
            cal.set(2008, 1, 1);
            newOrder.setActiveSince(cal.getTime());

            // now add some lines
            OrderLineWS lines[] = new OrderLineWS[1];
            OrderLineWS line;

            line = new OrderLineWS();
            line.setPrice(new BigDecimal("100.00"));
            line.setTypeId(Constants.ORDER_LINE_TYPE_ITEM);
            line.setQuantity(new Integer(1));
            line.setAmount(new BigDecimal("100.00"));
            line.setDescription("Fist line");
            line.setItemId(new Integer(1));
            lines[0] = line;
            newOrder.setOrderLines(lines);
            System.out.println("Creating order ... " + newOrder);
            invoiceId = api.createOrderAndInvoice(newOrder);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
        return invoiceId;
    }
    
    public void testPartialRefund() throws Exception {
        try {
            System.out.println("*** testPartialRefund ***");
            JbillingAPI api = JbillingAPIFactory.getAPI();

            //create user
            UserWS user= createUser(false, null, null);
            assertTrue(user.getUserId() > 0);
            System.out.println("User created successfully " + user.getUserId());

            user= api.getUserWS(user.getUserId());
            
            // Create a payment
            PaymentWS payment = new PaymentWS();
            payment.setAmount(new BigDecimal("30.00"));
            payment.setIsRefund(new Integer(0));
            payment.setMethodId(Constants.PAYMENT_METHOD_VISA);
            payment.setPaymentDate(Calendar.getInstance().getTime());
            payment.setCurrencyId(new Integer(5)); // GBP
            payment.setUserId(user.getUserId()); // Existing user Frank Thompson

            // Add the token for this payment
            CreditCardDTO cc = new CreditCardDTO();
            cc.setName("Joe Bloggs");
            cc.setType(Constants.PAYMENT_METHOD_VISA);
            cc.setNumber("4111111111111152"); // dummy data, to pass validation
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.YEAR, 9999); // dummy data, to pass validation
            cc.setExpiry(cal.getTime());

            payment.setCreditCard(cc);

            // Process
            System.out.println("Processing token payment...");
            PaymentAuthorizationDTOEx authInfo = api.processPayment(payment, null);
            assertNotNull("Payment result not null", authInfo);

            Integer paymentId = authInfo.getPaymentId();

            //printAuthorizationResults(authInfo);

            assertTrue("Payment Authorization result should be successful",
                    authInfo.getResult().booleanValue());

            //remove paymentInvoices links.
            payment = api.getPayment(authInfo.getPaymentId());
            System.out.println("Balance after payment " + payment.getBalanceAsDecimal());
//            for(Integer invoiceId : payment.getInvoiceIds()){
//                api.removePaymentLink(invoiceId,paymentId);
//            }

            // Create a refund
            PaymentWS payment2 = new PaymentWS();
            payment2.setAmount(new BigDecimal("200.00")); //Invalid amount
            payment2.setIsRefund(new Integer(1));
            payment2.setMethodId(Constants.PAYMENT_METHOD_VISA);
            payment2.setPaymentDate(Calendar.getInstance().getTime());
            payment2.setCurrencyId(new Integer(5)); // GBP
            payment2.setUserId(user.getUserId()); // Existing user Frank Thompson
            payment2.setPaymentId(paymentId);

            // Add the token for this payment
            CreditCardDTO cc2 = new CreditCardDTO();
            cc2.setName("Joe Bloggs");
            cc2.setType(Constants.PAYMENT_METHOD_VISA);
            cc2.setNumber("4111111111111152"); // dummy data, to pass validation
            cal = Calendar.getInstance();
            cal.add(Calendar.YEAR, 9999); // dummy data, to pass validation
            cc2.setExpiry(cal.getTime());

            payment2.setCreditCard(cc2);

            // Process invalid refund
            System.out.println("Processing token payment...");
            PaymentAuthorizationDTOEx authInfo2;
            try {
                authInfo2 = api.processPayment(payment2, null);
                fail("An exception should be thrown");
            } catch (SessionInternalError e) {
                assertEquals("An exception should be thrown, the amount of the refund is greater thant the payment",
                        "Either refund payment was not linked to any payment or the refund amount is in-correct", e.getMessage());
            }
            //now set a valid amount.
            payment2.setAmount(new BigDecimal("20.00"));
            authInfo2 = api.processPayment(payment2, null);
            assertNotNull("Payment result not null", authInfo2);

            //printAuthorizationResults(authInfo2);

            assertTrue("Payment Authorization result should be successful",
                    authInfo2.getResult().booleanValue());

            PaymentWS originalPayment= api.getPayment(paymentId);

            assertEqualsBigDecimal("The original payments balance should have reduced to 10.00",
                    BigDecimal.TEN, originalPayment.getBalanceAsDecimal());
            
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception caught:" + e);
        }
    }

    public static void assertEqualsBigDecimal(BigDecimal expected, BigDecimal actual) {
        assertEqualsBigDecimal(null, expected, actual);
    }

    public static void assertEqualsBigDecimal(String message, BigDecimal expected, BigDecimal actual) {
        assertEquals(message,
                (Object) (expected == null ? null : expected.setScale(2, RoundingMode.HALF_UP)),
                (Object) (actual == null ? null : actual.setScale(2, RoundingMode.HALF_UP)));
    }

}
