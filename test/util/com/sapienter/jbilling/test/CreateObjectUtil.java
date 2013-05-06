package com.sapienter.jbilling.server.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.sapienter.jbilling.server.entity.AchDTO;
import com.sapienter.jbilling.server.entity.CreditCardDTO;
import com.sapienter.jbilling.server.entity.PaymentInfoChequeDTO;
import com.sapienter.jbilling.server.item.ItemDTOEx;
import com.sapienter.jbilling.server.item.PlanItemBundleWS;
import com.sapienter.jbilling.server.item.PlanItemWS;
import com.sapienter.jbilling.server.item.PlanWS;
import com.sapienter.jbilling.server.order.OrderLineWS;
import com.sapienter.jbilling.server.order.OrderWS;
import com.sapienter.jbilling.server.payment.PaymentWS;
import com.sapienter.jbilling.server.pricing.PriceModelWS;
import com.sapienter.jbilling.server.pricing.db.PriceModelStrategy;
import com.sapienter.jbilling.server.process.BillingProcessConfigurationWS;
import com.sapienter.jbilling.server.user.ContactWS;
import com.sapienter.jbilling.server.user.MainSubscriptionWS;
import com.sapienter.jbilling.server.user.UserWS;
import com.sapienter.jbilling.server.util.api.JbillingAPI;

/**
 * User: Nikhil Date: 10/8/12 Description: A utility class written to help
 * developers write test cases quickly covering some common tasks such as a)
 * Creating An OrderWS b) Adding An Order Line to an OrderWS c) Creating A
 * PaymentWS d) Creating A Customer returning UserWS e) Creating A Customer
 * ConatctWS f) Pausing the thread for some seconds g) Updating the billing
 * configuration h) Creating PlanWS i) Get Contact Field Content
 */
public class CreateObjectUtil {

    /**
     * Creates an OrderWS object
     * 
     * @param userId
     * @param currencyId
     * @param billingType
     * @param orderPeriod
     * @param activeSince
     * @return
     */
    public static OrderWS createOrderObject(Integer userId, Integer currencyId,
            Integer billingType, Integer orderPeriod, Date activeSince) {
        /*
         * Create
         */
        OrderWS newOrder = new OrderWS();

        newOrder.setUserId(userId);
        newOrder.setCurrencyId(currencyId);
        newOrder.setBillingTypeId(billingType);
        newOrder.setPeriod(orderPeriod);

        // Defaults
        newOrder.setNotes("Domain: www.test.com");

        newOrder.setActiveSince(activeSince);
        // On some branches this field is present, please uncomment if required
        // newOrder.setCycleStarts(cal.getTime());

        return newOrder;
    }

    /**
     * To add a line to an order
     * 
     * @param order
     * @param lineQty
     * @param lineTypeId
     * @param lineItemId
     * @param linePrice
     * @param description
     * @return
     */
    public static OrderWS addLine(OrderWS order, Integer lineQty,
            Integer lineTypeId, Integer lineItemId, BigDecimal linePrice,
            String description) {

        // store the existing lines
        OrderLineWS[] existingLines = order.getOrderLines();
        List<OrderLineWS> finalLines = new ArrayList<OrderLineWS>();
        // iterate over the array and add to the ArrayList
        for (OrderLineWS oneItem : existingLines) {
            finalLines.add(oneItem);
        }
        // Now add some 1 line
        OrderLineWS line;
        line = new OrderLineWS();
        line.setTypeId(lineTypeId);
        line.setItemId(lineItemId);
        if (null != linePrice) {
            line.setPrice(linePrice);
        }
        line.setAmount(linePrice);
        line.setQuantity(lineQty);
        line.setDescription(description);
        finalLines.add(line);
        OrderLineWS[] simpleArray = new OrderLineWS[finalLines.size()];
        finalLines.toArray(simpleArray);
        order.setOrderLines(simpleArray);
        return order;
    }

    /**
     * To create a payment object
     * 
     * @param userId
     * @param amount
     * @param currencyId
     * @param isRefund
     * @param paymentMethodId
     * @param paymentDate
     * @param paymentNotes
     * @param cheque
     * @param creditCard
     * @param ach
     * @return
     */
    public static PaymentWS createPaymentObject(Integer userId,
            BigDecimal amount, Integer currencyId, boolean isRefund,
            Integer paymentMethodId, Date paymentDate, String paymentNotes,
            PaymentInfoChequeDTO cheque, CreditCardDTO creditCard, AchDTO ach) {
        PaymentWS payment = new PaymentWS();
        payment.setUserId(userId);
        payment.setAmount(amount);
        payment.setIsRefund((isRefund ? new Integer(1) : new Integer(0)));
        payment.setCurrencyId(currencyId);
        payment.setMethodId(paymentMethodId);
        payment.setPaymentDate(paymentDate);
        payment.setPaymentNotes(paymentNotes);

        if (cheque != null) {
            payment.setCheque(cheque);
        } else if (creditCard != null) {
            payment.setCreditCard(creditCard);
        }

        else if (ach != null) {
            payment.setAch(ach);
        }

        return payment;
    }

    /**
     * To create a customer
     * 
     * @param currencyId
     * @param userName
     * @param password
     * @param languageId
     * @param mainRoleId
     * @param isParent
     * @param statusID
     * @param card
     * @param ach
     * @param contact
     * @param subscriptionWS
     * @return
     */
    public static UserWS createCustomer(Integer currencyId, String userName,
            String password, Integer languageId, Integer mainRoleId,
            boolean isParent, Integer statusID, CreditCardDTO card, AchDTO ach,
            ContactWS contact, MainSubscriptionWS subscriptionWS) {

        UserWS newUser = new UserWS();
        newUser.setUserName(userName);
        newUser.setLanguageId(languageId);
        newUser.setCurrencyId(currencyId);

        // Provide Defaults
        newUser.setPassword(password);
        newUser.setMainRoleId(mainRoleId);// customer
        newUser.setIsParent(isParent);// not parent
        newUser.setStatusId(statusID); // active user

        // add a contact
        if (contact != null) {
            newUser.setContact(contact);
        }

        if (card != null) {
            newUser.setCreditCard(card);
        }

        if (ach != null) {
            newUser.setAch(ach);
        }

        // not on some branches currently, so remove this and also the parameter
        // :(
        if (subscriptionWS != null) {
            newUser.setMainSubscription(subscriptionWS);
        }

        return newUser;
    }

    /**
     * To Create Customer Contact
     * 
     * @param email
     * @return
     */
    public static ContactWS createCustomerContact(String email) {
        ContactWS contact = new ContactWS();
        contact.setEmail(email);
        // rest of the fields are not mandatory
        return contact;
    }

    /**
     * To Pause the thread
     * 
     * @param t
     */
    public static void pause(long t) {

        try {
            Thread.sleep(t);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * To create a plan
     * 
     * @param entityId
     * @param cost
     * @param currencyID
     * @param planItemType
     * @param periodID
     * @param quantity
     * @param api
     * @return
     */
    public static PlanWS createPlan(Integer entityId, BigDecimal cost,
            Integer currencyID, Integer planItemType, Integer periodID,
            BigDecimal quantity, JbillingAPI api) {

        ItemDTOEx planItem = new ItemDTOEx();
        planItem.setNumber(String.valueOf(new Date().getTime()));
        planItem.setEntityId(entityId);
        planItem.setDescription("Test plan " + new Date().getTime());
        planItem.setDefaultPrice(new PriceModelWS(PriceModelStrategy.METERED
                .name(), cost, currencyID));
        planItem.setCurrencyId(currencyID);
        planItem.setTypes(new Integer[] { planItemType });
        planItem.setPrice(cost);

        Integer planItemId = api.createItem(planItem);

        PlanItemWS sms = new PlanItemWS();
        sms.setItemId(planItemId);

        // Present at some repo, please uncomment and use if required
        // sms.setModel(new PriceModelWS(PriceModelStrategy.FLAT.name(), new
        // BigDecimal("0"), currencyID));

        PlanItemBundleWS bundle = new PlanItemBundleWS();
        bundle.setQuantity(quantity);
        sms.setBundle(bundle);

        PlanWS plan = new PlanWS();
        plan.setDescription("Test plan description "
                + String.valueOf(new Date().getTime()));
        plan.setPeriodId(periodID);
        plan.setItemId(planItemId);
        plan.addPlanItem(sms);

        return plan;
    }
}