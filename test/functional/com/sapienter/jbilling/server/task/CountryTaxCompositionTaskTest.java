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

/**
 *
 */
package com.sapienter.jbilling.server.task;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.Hashtable;

import junit.framework.TestCase;

import com.sapienter.jbilling.server.entity.InvoiceLineDTO;
import com.sapienter.jbilling.server.invoice.InvoiceWS;
import com.sapienter.jbilling.server.item.ItemDTOEx;
import com.sapienter.jbilling.server.order.OrderLineWS;
import com.sapienter.jbilling.server.order.OrderWS;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskWS;
import com.sapienter.jbilling.server.user.ContactWS;
import com.sapienter.jbilling.server.user.UserDTOEx;
import com.sapienter.jbilling.server.user.UserWS;
import com.sapienter.jbilling.server.util.Constants;
import com.sapienter.jbilling.server.util.api.JbillingAPI;
import com.sapienter.jbilling.server.util.api.JbillingAPIFactory;
import org.testng.annotations.*;

import static com.sapienter.jbilling.test.Asserts.*;
import static org.testng.AssertJUnit.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Test class to unit test the functionality of Tax applied to invoices that
 * belong to user from a configured country.
 *
 * @author Vikas Bodani
 * @since 29-Jul-2011
 *
 */
@Test(groups = { "integration", "task", "tax", "countrytax" })
public class CountryTaxCompositionTaskTest {

    private static final Integer COUNTRY_TAX_PLUGIN_TYPE_ID = 90;

    private static final String PLUGIN_PARAM_CHARGE_CARRYING_ITEM_ID = "charge_carrying_item_id";
    private static final String PLUGIN_PARAM_TAX_COUNTRY_CODE = "tax_country_code";

    private static final Integer FEE_ITEM_TYPE_ID = 22;
    private static final Integer LEMONADE_ITEM_ID = 2602;


    /*
       Enable/disable the CountryTaxCompositionTask plug-in.
    */

    private JbillingAPI api;

    private Integer taxPluginId;

    @BeforeClass
    private void getAPI() throws Exception {
        api = JbillingAPIFactory.getAPI();
    }

    private void enableTaxPlugin(Integer itemId) {
        PluggableTaskWS plugin = new PluggableTaskWS();
        plugin.setTypeId(COUNTRY_TAX_PLUGIN_TYPE_ID);
        plugin.setProcessingOrder(4);

        // plug-in adds the given tax item to the invoice
        // when the customers country code is Australia 'AU'
        Hashtable<String, String> parameters = new Hashtable<String, String>();
        parameters.put(PLUGIN_PARAM_CHARGE_CARRYING_ITEM_ID, itemId.toString());
        parameters.put(PLUGIN_PARAM_TAX_COUNTRY_CODE, "AU");
        plugin.setParameters(parameters);

        taxPluginId = api.createPlugin(plugin);
    }

    @AfterClass
    @AfterMethod
    private void disableTaxPlugin() {
        if (taxPluginId != null)
            api.deletePlugin(taxPluginId);
        taxPluginId = null;
    }


    // tests

    @Test
    public void testCountryTaxAUPercentage() throws Exception {
        System.out.println("#testCountryTaxAUPercentage");

        // add a new tax item & enable the tax plug-in
        ItemDTOEx item = new ItemDTOEx();
        item.setCurrencyId(1);
        item.setPercentage(new BigDecimal(10));     // tax is %10
        item.setPrice(item.getPercentage());        //
        item.setDescription("AU Tax");
        item.setEntityId(1);
        item.setNumber("TAX-AU");
        item.setTypes(new Integer[] { FEE_ITEM_TYPE_ID });

        item.setId(api.createItem(item));
        assertNotNull("tax item created", item.getId());

        enableTaxPlugin(item.getId());


        // create a user for testing
        UserWS user = new UserWS();
        user.setUserName("country-tax-01-" + new Date().getTime());
        user.setPassword("password");
        user.setLanguageId(1);
        user.setCurrencyId(1);
        user.setMainRoleId(5);
        user.setStatusId(UserDTOEx.STATUS_ACTIVE);
        user.setBalanceType(Constants.BALANCE_NO_DYNAMIC);

        ContactWS contact = new ContactWS();
        contact.setEmail("test@test.com");
        contact.setFirstName("Country Tax Test");
        contact.setLastName("Percentage Rate");
        contact.setCountryCode("AU"); // country code set to Australia (AU)
        user.setContact(contact);

        user.setUserId(api.createUser(user)); // create user
        assertNotNull("customer created", user.getUserId());


        // purchase order with taxable items
        OrderWS order = new OrderWS();
        order.setUserId(user.getUserId());
        order.setBillingTypeId(Constants.ORDER_BILLING_POST_PAID);
        order.setPeriod(1);
        order.setCurrencyId(1);
        order.setActiveSince(new Date());

        OrderLineWS line = new OrderLineWS();
        line.setTypeId(Constants.ORDER_LINE_TYPE_ITEM);
        line.setItemId(LEMONADE_ITEM_ID);
        line.setUseItem(true);
        line.setQuantity(10);
        order.setOrderLines(new OrderLineWS[] { line });

        order.setId(api.createOrder(order)); // create order
        order = api.getOrder(order.getId());
        assertNotNull("order created", order.getId());


        // generate an invoice and verify the taxes
        Integer invoiceId = api.createInvoiceFromOrder(order.getId(), null);
        InvoiceWS invoice = api.getInvoiceWS(invoiceId);

        assertNotNull("invoice generated", invoice);
        assertEquals("two lines in invoice, lemonade and the tax line", 2, invoice.getInvoiceLines().length);

        boolean foundTaxItem = false;
        boolean foundLemonadeItem = false;

        for (InvoiceLineDTO invoiceLine : invoice.getInvoiceLines()) {

            // purchased lemonade
            if (invoiceLine.getItemId().equals(LEMONADE_ITEM_ID)) {
                assertEquals("lemonade item", "Lemonade ", invoiceLine.getDescription());
                assertEquals("lemonade $35", new BigDecimal("35"), invoiceLine.getAmountAsDecimal());
                foundLemonadeItem = true;
            }

            // tax, %10 of invoice total ($35 x 0.10 = $3.5)
            if (invoiceLine.getItemId().equals(item.getId())) {
                assertEquals("tax item", "AU Tax", invoiceLine.getDescription());
                assertEquals("tax $3.5", new BigDecimal("3.5"), invoiceLine.getAmountAsDecimal());
                foundTaxItem = true;
            }
        }

        assertTrue("found and validated tax", foundTaxItem);
        assertTrue("found and validated lemonade", foundLemonadeItem);


        // cleanup
        api.deleteItem(item.getId());
        api.deleteOrder(order.getId());
        api.deleteInvoice(invoice.getId());
        api.deleteUser(user.getUserId());
    }

    @Test
    public void testCountryTaxAUFlatFee() throws Exception {
        System.out.println("#testCountryTaxAUFlatFee");

        // add a new tax item & enable the tax plug-in
        ItemDTOEx item = new ItemDTOEx();
        item.setCurrencyId(1);
        item.setPercentage((BigDecimal) null);  // tax is not a percentage
        item.setPrice(new BigDecimal(10));      // $10 flat fee
        item.setDescription("AU Tax");
        item.setEntityId(1);
        item.setNumber("TAX-AU");
        item.setTypes(new Integer[] { FEE_ITEM_TYPE_ID });

        item.setId(api.createItem(item));
        assertNotNull("tax item created", item.getId());

        enableTaxPlugin(item.getId());


        // create a user for testing
        UserWS user = new UserWS();
        user.setUserName("country-tax-02-" + new Date().getTime());
        user.setPassword("password");
        user.setLanguageId(1);
        user.setCurrencyId(1);
        user.setMainRoleId(5);
        user.setStatusId(UserDTOEx.STATUS_ACTIVE);
        user.setBalanceType(Constants.BALANCE_NO_DYNAMIC);

        ContactWS contact = new ContactWS();
        contact.setEmail("test@test.com");
        contact.setFirstName("Country Tax Test");
        contact.setLastName("Flat Fee");
        contact.setCountryCode("AU"); // country code set to Australia (AU)
        user.setContact(contact);

        user.setUserId(api.createUser(user)); // create user
        assertNotNull("customer created", user.getUserId());


        // purchase order with taxable items
        OrderWS order = new OrderWS();
        order.setUserId(user.getUserId());
        order.setBillingTypeId(Constants.ORDER_BILLING_POST_PAID);
        order.setPeriod(1);
        order.setCurrencyId(1);
        order.setActiveSince(new Date());

        OrderLineWS line = new OrderLineWS();
        line.setTypeId(Constants.ORDER_LINE_TYPE_ITEM);
        line.setItemId(LEMONADE_ITEM_ID);
        line.setUseItem(true);
        line.setQuantity(10);
        order.setOrderLines(new OrderLineWS[] { line });

        order.setId(api.createOrder(order)); // create order
        order = api.getOrder(order.getId());
        assertNotNull("order created", order.getId());


        // generate an invoice and verify the taxes
        Integer invoiceId = api.createInvoiceFromOrder(order.getId(), null);
        InvoiceWS invoice = api.getInvoiceWS(invoiceId);

        assertNotNull("invoice generated", invoice);
        assertEquals("two lines in invoice, lemonade and the tax line", 2, invoice.getInvoiceLines().length);

        boolean foundTaxItem = false;
        boolean foundLemonadeItem = false;

        for (InvoiceLineDTO invoiceLine : invoice.getInvoiceLines()) {

            // purchased lemonade
            if (invoiceLine.getItemId().equals(LEMONADE_ITEM_ID)) {
                assertEquals("lemonade item", "Lemonade ", invoiceLine.getDescription());
                assertEquals("lemonade $35", new BigDecimal("35"), invoiceLine.getAmountAsDecimal());
                foundLemonadeItem = true;
            }

            // tax is a flat fee, not affected by the price of the invoice
            if (invoiceLine.getItemId().equals(item.getId())) {
                assertEquals("tax item", "AU Tax", invoiceLine.getDescription());
                assertEquals("tax $10", new BigDecimal("10"), invoiceLine.getAmountAsDecimal());
                foundTaxItem = true;
            }
        }

        assertTrue("found and validated tax", foundTaxItem);
        assertTrue("found and validated lemonade", foundLemonadeItem);


        // cleanup
        api.deleteItem(item.getId());
        api.deleteOrder(order.getId());
        api.deleteInvoice(invoice.getId());
        api.deleteUser(user.getUserId());
    }

    @Test
    public void testCountryTaxNonAUCustomer() throws Exception {
        System.out.println("#testCountryTaxNonAUCustomer");

        // add a new tax item & enable the tax plug-in
        ItemDTOEx item = new ItemDTOEx();
        item.setCurrencyId(1);
        item.setPercentage(new BigDecimal(10));     // tax is %10
        item.setPrice(item.getPercentage());        //
        item.setDescription("AU Tax");
        item.setEntityId(1);
        item.setNumber("TAX-AU");
        item.setTypes(new Integer[] { FEE_ITEM_TYPE_ID });

        item.setId(api.createItem(item));
        assertNotNull("tax item created", item.getId());

        enableTaxPlugin(item.getId());


        // create a user for testing
        UserWS user = new UserWS();
        user.setUserName("country-tax-03-" + new Date().getTime());
        user.setPassword("password");
        user.setLanguageId(1);
        user.setCurrencyId(1);
        user.setMainRoleId(5);
        user.setStatusId(UserDTOEx.STATUS_ACTIVE);
        user.setBalanceType(Constants.BALANCE_NO_DYNAMIC);

        ContactWS contact = new ContactWS();
        contact.setEmail("test@test.com");
        contact.setFirstName("Country Tax Test");
        contact.setLastName("Non-Australian");
        contact.setCountryCode("CA"); // country NOT set to AU,
        user.setContact(contact);     // non-Australian customers shouldn't be taxed

        user.setUserId(api.createUser(user)); // create user
        assertNotNull("customer created", user.getUserId());


        // purchase order with taxable items
        OrderWS order = new OrderWS();
        order.setUserId(user.getUserId());
        order.setBillingTypeId(Constants.ORDER_BILLING_POST_PAID);
        order.setPeriod(1);
        order.setCurrencyId(1);
        order.setActiveSince(new Date());

        OrderLineWS line = new OrderLineWS();
        line.setTypeId(Constants.ORDER_LINE_TYPE_ITEM);
        line.setItemId(LEMONADE_ITEM_ID);
        line.setUseItem(true);
        line.setQuantity(10);
        order.setOrderLines(new OrderLineWS[] { line });

        order.setId(api.createOrder(order)); // create order
        order = api.getOrder(order.getId());
        assertNotNull("order created", order.getId());


        // generate an invoice and verify the taxes
        Integer invoiceId = api.createInvoiceFromOrder(order.getId(), null);
        InvoiceWS invoice = api.getInvoiceWS(invoiceId);

        assertNotNull("invoice generated", invoice);
        assertEquals("one lines in invoice, just lemonade, no tax", 1, invoice.getInvoiceLines().length);

        InvoiceLineDTO invoiceLine = invoice.getInvoiceLines()[0];
        assertEquals("lemonade item", LEMONADE_ITEM_ID, invoiceLine.getItemId());
        assertEquals("lemonade item", "Lemonade ", invoiceLine.getDescription());
        assertEquals("lemonade $35", new BigDecimal("35"), invoiceLine.getAmountAsDecimal());


        // cleanup
        api.deleteItem(item.getId());
        api.deleteOrder(order.getId());
        api.deleteInvoice(invoice.getId());
        api.deleteUser(user.getUserId());
    }
}
