package com.sapienter.jbilling.server.task;

import com.sapienter.jbilling.server.order.OrderLineWS;
import com.sapienter.jbilling.server.order.OrderWS;
import com.sapienter.jbilling.server.payment.PaymentWS;
import com.sapienter.jbilling.server.user.ContactWS;
import com.sapienter.jbilling.server.user.UserWS;
import com.sapienter.jbilling.server.util.api.JbillingAPI;
import junit.framework.TestCase;
import com.sapienter.jbilling.server.user.db.UserDAS;
import com.sapienter.jbilling.server.payment.db.*;
import com.sapienter.jbilling.server.util.db.CurrencyDAS;
import com.sapienter.jbilling.server.util.api.JbillingAPI;
import com.sapienter.jbilling.server.util.api.JbillingAPIFactory;
import com.sapienter.jbilling.server.util.Constants;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.lang.Exception;
import java.lang.Integer;
import java.lang.String;
import java.lang.System;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;

import static com.sapienter.jbilling.test.Asserts.*;
import static org.testng.AssertJUnit.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


@Test(groups = { "integration", "task", "dynamic-balance" })
public class DynamicBalanceManagerTaskTest {

    private JbillingAPI api;
    private static int USER_ID;
    private static final int PAYMENT_METHOD_ID = 1;
    private static int  US_DOLLAR_ID = 1;
    private static int  AUS_DOLLAR_ID = 11;
    private static int PAYMENT_ID;

    @BeforeClass
    protected void setUp() throws Exception {
        api = JbillingAPIFactory.getAPI();
    }

    /**
     * Test Scenario: Make a user with currency AUS $ , then make a payment of US$ 100, the dynamic balance of the user should be greater than AUS $ 101 , assuming that US $ is STRONGER than AUS $
     */
    @Test
    public void testDetermineAmountWithPaymentSuccessfulEvent() {
        try {
            System.out.println("Testing determine amount with payment successful event");
            String username = "user-payment-succ-15-" + System.currentTimeMillis();
            String email = "user-payment-succ@jbilling.com";
            String balance = "1";
            // make a new user with ZERO balance and currency as AUS DOLLAR
            USER_ID = makeUser(AUS_DOLLAR_ID, username, email, balance);
            PaymentWS payment = new PaymentWS();
            payment.setUserId(USER_ID);
            //payment.setCheque(CHEQUE);
            payment.setMethodId(PAYMENT_METHOD_ID);
            Calendar calendar = Calendar.getInstance();
            payment.setPaymentDate(calendar.getTime());
            payment.setCurrencyId(US_DOLLAR_ID);
            payment.setAmount("100");
            payment.setIsRefund(0);
            // check that user's dynamic balance is 100
            UserWS user =  api.getUserWS(USER_ID);
            System.out.println("User's dynamic balance earlier was "+user.getDynamicBalanceAsDecimal());
            assertTrue("User's Balance would be ONE", (BigDecimal.ONE.compareTo(user.getDynamicBalanceAsDecimal())==0));
            api.createPayment(payment);
            // update the user
            api.updateUser(user);
            user = api.getUserWS(USER_ID);
            System.out.println("User's dynamic balance now is "+user.getDynamicBalanceAsDecimal());
            System.out.println("Payment amount "+payment.getAmountAsDecimal());
            System.out.println("balance "+new BigDecimal(balance));
            
            assertTrue("User's balance must be greater than the topped up value, assuming US $ is stronger than AUS $",(user.getDynamicBalanceAsDecimal().compareTo(payment.getAmountAsDecimal().add(new BigDecimal(balance)))>0));
        }catch(Exception e) {
            System.out.println("Exception "+e);
            e.printStackTrace();
        }
    }


    /**
     * Test Scenario: Make a payment in different currency than user's default currency , then delete the payment, the result should be that user's dynamic balance should be updated according to the user's currency
     *  and not on the payment's currency value.
     */
    @Test
    public void testDetermineAmountWithPaymentDeletedEvent() {
        try {
            System.out.println("Testing determine amount with payment successful event");
            String username = "user-payment-del-2-" + System.currentTimeMillis();
            String email = "user-payment-succ@jbilling.com";
            String balance = "1";
            // make a new user with ZERO balance and currency as AUS DOLLAR
            USER_ID = makeUser(AUS_DOLLAR_ID, username, email, balance);
            PaymentWS payment = new PaymentWS();
            payment.setUserId(USER_ID);
            //payment.setCheque(CHEQUE);
            payment.setMethodId(PAYMENT_METHOD_ID);
            Calendar calendar = Calendar.getInstance();
            payment.setPaymentDate(calendar.getTime());
            payment.setCurrencyId(US_DOLLAR_ID);
            payment.setAmount("100");
            payment.setIsRefund(0);
            // check that user's dynamic balance is 100
            UserWS user =  api.getUserWS(USER_ID);
            System.out.println("User's dynamic balance earlier was "+user.getDynamicBalanceAsDecimal());
            assertTrue("User's Balance would be ONE", (BigDecimal.ONE.compareTo(user.getDynamicBalanceAsDecimal())==0));
            PAYMENT_ID = api.createPayment(payment);
            // update the user
            api.updateUser(user);
            user = api.getUserWS(USER_ID);
            System.out.println("User's dynamic balance now is "+user.getDynamicBalanceAsDecimal());
            System.out.println("Payment amount "+payment.getAmountAsDecimal());
            System.out.println("balance "+new BigDecimal(balance));
            assertTrue("User's balance must be greater than the topped up value, assuming US $ is stronger than AUS $",(user.getDynamicBalanceAsDecimal().compareTo(payment.getAmountAsDecimal().add(new BigDecimal(balance)))>0));
            
            // now delete the payment , user's dynamic balance should become equal to initial value
            api.deletePayment(PAYMENT_ID);
            // update the user
            api.updateUser(user);
            user = api.getUserWS(USER_ID);
            
            System.out.println("User's dynamic balance is now "+user.getDynamicBalanceAsDecimal());
            assertTrue("User's Dynamic Balance Must Be Back to initial balance ", (user.getDynamicBalanceAsDecimal().compareTo(new BigDecimal(balance))==0));
          
        }catch(Exception e) {
            System.out.println("Exception "+e);
            e.printStackTrace();
        }
    }

    @Test
    public void testDynamicBalanceAfterQuantityChange() {

        System.out.println("Testing dynamic balance after quantity change on one-time order");

        try {

            String username = "user-order-quant-change-1-" + System.currentTimeMillis();
            String email = "user-order-quanti-change@jbilling.com";
            String balance = "0";

            Integer userId = makeUser(US_DOLLAR_ID, username, email, balance);
            OrderWS order = buildOrder(userId);
            BigDecimal secondProductPrice = api.getItem(2, userId, null).getPriceAsDecimal();

            Integer orderId = api.createOrder(order);

            UserWS user = api.getUserWS(userId);
            order = api.getOrder(orderId);

            System.out.println("user dynamic balance: " + user.getDynamicBalanceAsDecimal());
            System.out.println("order total amount: " + order.getTotalAsDecimal());

            //Calculate the total amount of the order. This makes the test resiliant to changes of the second product price
            BigDecimal calculatedOrderTotal = BigDecimal.valueOf(10).add(secondProductPrice);
            System.out.println("The order total is " + calculatedOrderTotal.toPlainString());
            assertEquals("The order total", calculatedOrderTotal, order.getTotalAsDecimal());
            assertEquals("Both should be 30", user.getDynamicBalanceAsDecimal(), order.getTotalAsDecimal().negate());

            //change the quantity of the first product in the order
            //and check the order total and the dynamic balance
            OrderLineWS orderLineWS = findLine(order, 1);
            orderLineWS.setQuantity(2);
            api.updateOrder(order);

            user = api.getUserWS(userId);
            order = api.getOrder(orderId);

            calculatedOrderTotal = BigDecimal.valueOf(2).multiply(BigDecimal.valueOf(10)).add(secondProductPrice);
            System.out.println("The order total is " + calculatedOrderTotal.toPlainString());
            assertEquals("The order total", calculatedOrderTotal, order.getTotalAsDecimal());
            assertEquals("Both should be 40", user.getDynamicBalanceAsDecimal(), order.getTotalAsDecimal().negate());

            //change the quantities of both products simultaneously
            //and check the order total amount and the balance of the user
            orderLineWS = findLine(order, 1);
            orderLineWS.setQuantity(5);

            orderLineWS = findLine(order, 2);
            orderLineWS.setQuantity(5);

            api.updateOrder(order);

            user = api.getUserWS(userId);
            order = api.getOrder(orderId);

            BigDecimal firstLineTotalAmount = BigDecimal.valueOf(5).multiply(BigDecimal.valueOf(10));
            BigDecimal secondLineTotalAmount = BigDecimal.valueOf(5).multiply(secondProductPrice);
            //5*10 + 5*20 = 50+100 = 150
            calculatedOrderTotal = firstLineTotalAmount.add(secondLineTotalAmount);
            System.out.println("The order total is " + calculatedOrderTotal.toPlainString());
            assertEquals("The order total", calculatedOrderTotal, order.getTotalAsDecimal());
            assertEquals("Both should be 150", user.getDynamicBalanceAsDecimal(), order.getTotalAsDecimal().negate());

            //change both the quantity and the price of first product
            //and quantity of the second product and check if the total order amount
            //and user's dynamic balance will correctly follow

            orderLineWS = findLine(order, 1);
            orderLineWS.setQuantity(10);
            orderLineWS.setPrice(BigDecimal.valueOf(3));

            orderLineWS = findLine(order, 2);
            orderLineWS.setQuantity(10);

            api.updateOrder(order);

            user = api.getUserWS(userId);
            order = api.getOrder(orderId);

            firstLineTotalAmount = BigDecimal.valueOf(10).multiply(BigDecimal.valueOf(3));
            secondLineTotalAmount = BigDecimal.valueOf(10).multiply(secondProductPrice);
            //10*3 + 10*20 = 20+200 = 220
            calculatedOrderTotal = firstLineTotalAmount.add(secondLineTotalAmount);
            System.out.println("The order total is " + calculatedOrderTotal.toPlainString());
            assertEquals("The order total", calculatedOrderTotal, order.getTotalAsDecimal());
            assertEquals("Both should be 220", user.getDynamicBalanceAsDecimal(), order.getTotalAsDecimal().negate());

            api.deleteOrder(orderId);

            user = api.getUserWS(userId);

            System.out.println("User's dynamic balance after order delete is: " + user.getDynamicBalanceAsDecimal().toPlainString());
            assertEquals("The Dynamic Balance should be 0", BigDecimal.ZERO, user.getDynamicBalanceAsDecimal());

            api.deleteUser(userId);

        } catch (Exception e) {
            System.out.println("Exception " + e);
            e.printStackTrace();
        }
    }

    @Test
    public void testDynamicBalanceAfterPriceChange() {

        System.out.println("Testing dynamic balance after price change on one-time order");

        try {
            String username = "user-order-price-change-1-" + System.currentTimeMillis();
            String email = "user-order-price-change@jbilling.com";
            String balance = "0";

            Integer userId = makeUser(US_DOLLAR_ID, username, email, balance);
            OrderWS order = buildOrder(userId);
            BigDecimal secondProductPrice = api.getItem(2, userId, null).getPriceAsDecimal();

            Integer orderId = api.createOrder(order);

            UserWS user = api.getUserWS(userId);
            order = api.getOrder(orderId);

            System.out.println("user dynamic balance: " + user.getDynamicBalanceAsDecimal());
            System.out.println("order total amount: " + order.getTotalAsDecimal());

            //Calculate the total amount of the order. This makes the test resiliant to changes of the second product price
            BigDecimal calculatedOrderTotal = BigDecimal.valueOf(10).add(secondProductPrice);
            System.out.println("The order total is " + calculatedOrderTotal.toPlainString());
            assertEquals("The order total", calculatedOrderTotal, order.getTotalAsDecimal());
            assertEquals("Both should be 30", user.getDynamicBalanceAsDecimal(), order.getTotalAsDecimal().negate());

            //change the quantity of the first product in the order
            //and check the order total and the dynamic balance
            OrderLineWS orderLineWS = findLine(order, 1);
            orderLineWS.setQuantity(2);
            api.updateOrder(order);

            user = api.getUserWS(userId);
            order = api.getOrder(orderId);

            calculatedOrderTotal = BigDecimal.valueOf(2).multiply(BigDecimal.valueOf(10)).add(secondProductPrice);
            System.out.println("The order total is " + calculatedOrderTotal.toPlainString());
            assertEquals("The order total", calculatedOrderTotal, order.getTotalAsDecimal());
            assertEquals("Both should be 40", user.getDynamicBalanceAsDecimal(), order.getTotalAsDecimal().negate());

            //change only the price of the first product. DO NOT change quantity
            orderLineWS = findLine(order, 1);
            orderLineWS.setPrice(new BigDecimal(7));
            api.updateOrder(order);

            user = api.getUserWS(userId);
            order = api.getOrder(orderId);

            calculatedOrderTotal = BigDecimal.valueOf(2).multiply(BigDecimal.valueOf(7)).add(secondProductPrice);
            System.out.println("The order total is " + calculatedOrderTotal.toPlainString());
            assertEquals("The order total", calculatedOrderTotal, order.getTotalAsDecimal());
            assertEquals("Both should be 34", user.getDynamicBalanceAsDecimal(), order.getTotalAsDecimal().negate());

            api.deleteOrder(orderId);

            user = api.getUserWS(userId);

            System.out.println("User's dynamic balance after order delete is: " + user.getDynamicBalanceAsDecimal().toPlainString());
            assertEquals("The Dynamic Balance should be 0", BigDecimal.ZERO, user.getDynamicBalanceAsDecimal());

            api.deleteUser(userId);

        } catch (Exception e) {
            System.out.println("Exception " + e);
            e.printStackTrace();
        }
    }


    /**
     * Creates a user by calling the api. The user will have a pre-paid dynamic balance
     * account with the given
     *
     * @param currencyId
     * @param username
     * @param email
     * @param balance
     * @return
     */
    private int makeUser(int currencyId,String username,String email, String balance) {
        System.out.println("Making User");
        UserWS user = new UserWS();
        user.setCurrencyId(currencyId);
        user.setUserName(username);
        user.setPassword("123qwe");
        user.setContact(new ContactWS());
        user.getContact().setEmail(email);
        System.out.println("Creating user");
        user.setMainRoleId(5);// customer role
        user.setRole("Customer");
        user.setLanguageId(new Integer(1));
        user.setBalanceType(Constants.BALANCE_PRE_PAID);
        user.setDynamicBalance(new BigDecimal(balance));
        return api.createUser(user);
    }

    private OrderWS buildOrder(Integer userId){
        OrderWS newOrder = new OrderWS();
        newOrder.setUserId(userId);
        newOrder.setBillingTypeId(Constants.ORDER_BILLING_POST_PAID);
        newOrder.setPeriod(Constants.ORDER_PERIOD_ONCE);
        newOrder.setCurrencyId(US_DOLLAR_ID);
        newOrder.setNotes("Order to test dynamic balance changes when chaging line quantities and price");

        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(2006, 9, 3);
        newOrder.setActiveSince(cal.getTime());

        // now add some lines
        OrderLineWS lines[] = new OrderLineWS[2];
        OrderLineWS line;

        line = new OrderLineWS();
        line.setPrice(new BigDecimal("10.00"));
        line.setTypeId(Constants.ORDER_LINE_TYPE_ITEM);
        line.setQuantity(new Integer(1));
        line.setAmount(new BigDecimal("10.00"));
        line.setDescription("Fist line");
        line.setItemId(new Integer(1));
        lines[0] = line;

        // this is an item line
        line = new OrderLineWS();
        line.setTypeId(Constants.ORDER_LINE_TYPE_ITEM);
        line.setQuantity(new Integer(1));
        line.setItemId(new Integer(2));
        // take the description from the item
        line.setUseItem(new Boolean(true));
        lines[1] = line;

        newOrder.setOrderLines(lines);

        return newOrder;
    }

    private OrderLineWS findLine(OrderWS order, Integer orderLineId){
        for(OrderLineWS orderLine : order.getOrderLines()){
            if(0 == orderLine.getItemId().compareTo(orderLineId)){
                return orderLine;
            }
        }
        return null;
    }
}