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

package com.sapienter.jbilling.server.item;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.sapienter.jbilling.common.SessionInternalError;
import junit.framework.TestCase;

import com.sapienter.jbilling.server.item.ItemDTOEx;
import com.sapienter.jbilling.server.order.OrderLineWS;
import com.sapienter.jbilling.server.order.OrderWS;
import com.sapienter.jbilling.server.util.Constants;
import com.sapienter.jbilling.server.util.InternationalDescriptionWS;
import com.sapienter.jbilling.server.util.api.JbillingAPI;
import com.sapienter.jbilling.server.util.api.JbillingAPIFactory;
import org.testng.annotations.Test;

import static com.sapienter.jbilling.test.Asserts.*;
import static org.testng.AssertJUnit.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * @author Emil
 */
@Test(groups = { "web-services", "item" })
public class WSTest {

    @Test
    public void test001Create() {
        System.out.println("#testCreate");

        try {
        	JbillingAPI api = JbillingAPIFactory.getAPI();
            /*
             * Create
             */
            ItemDTOEx newItem = new ItemDTOEx();
            newItem.setDescription("an item from ws");
            newItem.setPrice(new BigDecimal("29.5"));
            newItem.setNumber("WS-001");
            
            
            Integer types[] = new Integer[1];
            types[0] = new Integer(1);
            newItem.setTypes(types);
            
            System.out.println("Creating item ..." + newItem);
            Integer ret = api.createItem(newItem);
            assertNotNull("The item was not created", ret);
            System.out.println("Done!");

        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception caught:" + e);
        }
    }

    private ItemDTOEx createItemWithMultipleDescriptions(String number) {
        try {
            JbillingAPI api = JbillingAPIFactory.getAPI();
            /*
             * Create
             */
            ItemDTOEx newItem = new ItemDTOEx();

            List<InternationalDescriptionWS> descriptions = new java.util.ArrayList<InternationalDescriptionWS>();
            InternationalDescriptionWS enDesc = new InternationalDescriptionWS(1, "itemDescription-en");
            InternationalDescriptionWS frDesc = new InternationalDescriptionWS(2, "itemDescription-fr");
            descriptions.add(enDesc);
            descriptions.add(frDesc);

            newItem.setDescriptions(descriptions);
            newItem.setPrice(new BigDecimal("10.0"));
            newItem.setNumber(number);
            newItem.setHasDecimals(0);

            Integer types[] = new Integer[1];
            types[0] = new Integer(1);
            newItem.setTypes(types);

            System.out.println("Creating item ..." + newItem);
            Integer ret = api.createItem(newItem);
            assertNotNull("The item was not created", ret);
            System.out.println("Done!");
            newItem.setId(ret);

            return newItem;
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception caught:" + e);
            return null;
        }
    }

    private String getDescription(List<InternationalDescriptionWS> descriptions,int langId) {
        for (InternationalDescriptionWS description : descriptions) {
            if (description.getLanguageId() == langId) {
                return description.getContent();
            }
        }
        return "";
    }

    @Test
    public void test002CreateMultipleDescriptions() {
        System.out.println("#test002CreateMultipleDescriptions");
        try {
            JbillingAPI api = JbillingAPIFactory.getAPI();

            ItemDTOEx newItem = createItemWithMultipleDescriptions("WS-002");
            System.out.println("ItemId created: " + newItem.getId());
            System.out.println("Item created: " + newItem);

            newItem = api.getItem(newItem.getId(), 2, null);
            String enDescription = getDescription(newItem.getDescriptions(), 1);
            String frDescription = getDescription(newItem.getDescriptions(), 2);
            System.out.println("descriptions: " + enDescription + ", " + frDescription);
            assertEquals("itemDescription-en", enDescription);
            assertEquals("itemDescription-fr", frDescription);

            // delete the item
            api.deleteItem(newItem.getId());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception caught:" + e);
        }
    }

    @Test
    public void test003ModifyMultipleDescriptions() {
        System.out.println("#test003ModifyMultipleDescriptions");
        try {
            JbillingAPI api = JbillingAPIFactory.getAPI();
            ItemDTOEx newItem = createItemWithMultipleDescriptions("WS-003");
            // test remove one description (english)
            newItem.getDescriptions().get(0).setDeleted(true);
            api.updateItem(newItem);
            newItem = api.getItem(newItem.getId(), 2, null);
            int descriptionsCount = newItem.getDescriptions().size();
            assertEquals(1, descriptionsCount);

            // test modify content
            newItem.getDescriptions().get(0).setContent("newItemDescription-fr");
            api.updateItem(newItem);
            newItem = api.getItem(newItem.getId(), 2, null);
            String frDescription = getDescription(newItem.getDescriptions(), 2);
            assertEquals("newItemDescription-fr", frDescription);

            // delete the item
            api.deleteItem(newItem.getId());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception caught:" + e);
        }
    }
    
    private OrderWS prepareOrder() {
    	
    	OrderWS newOrder = new OrderWS();
        newOrder.setUserId(new Integer(2)); 
        newOrder.setBillingTypeId(Constants.ORDER_BILLING_PRE_PAID);
        newOrder.setPeriod(new Integer(1)); // once
        newOrder.setCurrencyId(new Integer(1));
        newOrder.setActiveSince(new Date());
        
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

    @Test
    public void test006GetAllItems() throws Exception {
        System.out.println("#test006GetAllItems");

        JbillingAPI api = JbillingAPIFactory.getAPI();

        System.out.println("Getting all items");
        ItemDTOEx[] items =  api.getAllItems();
        Arrays.sort(items, new Comparator<ItemDTOEx>() {
            public int compare(ItemDTOEx i1, ItemDTOEx i2) {
                return i1.getId().compareTo(i2.getId());
            }
        });

            // 19 items in jbilling-test_data.xml for entity id 1
            // +1 item created in the above #testCreateItem
            //      = 20 items
        assertNotNull("The items were not retrieved", items);
        assertThat(items.length, is(greaterThan(1)));

        assertEquals("Description", "Lemonade - 1 per day monthly pass", items[0].getDescription());
        assertEquals("Price", new BigDecimal("10"), items[0].getPriceAsDecimal());
        assertEquals("Price List", new BigDecimal("10"), items[0].getDefaultPrice().getRateAsDecimal());
        assertEquals("ID", new Integer(1), items[0].getId());
        assertEquals("Number", "DP-1", items[0].getNumber());
        assertEquals("Type 1", new Integer(1), items[0].getTypes()[0]);

        assertEquals("Description", "Lemonade - all you can drink monthly", items[1].getDescription());
        assertEquals("Price", new BigDecimal("20"), items[1].getPriceAsDecimal());
        assertEquals("Price List", new BigDecimal("20"), items[1].getDefaultPrice().getRateAsDecimal());
        assertEquals("ID", new Integer(2), items[1].getId());
        assertEquals("Number", "DP-2", items[1].getNumber());
        assertEquals("Type 1", new Integer(1), items[1].getTypes()[0]);

        assertEquals("Description", "Coffee - one per day - Monthly", items[2].getDescription());
        assertEquals("Price", new BigDecimal("15"), items[2].getPriceAsDecimal());
        assertEquals("Price List", new BigDecimal("15"), items[2].getDefaultPrice().getRateAsDecimal());
        assertEquals("ID", new Integer(3), items[2].getId());
        assertEquals("Number", "DP-3", items[2].getNumber());
        assertEquals("Type 1", new Integer(1), items[2].getTypes()[0]);

        assertEquals("Description", "10% Elf discount.", items[3].getDescription());
        assertEquals("Percentage", new BigDecimal("-10.00"), items[3].getPercentageAsDecimal());
        assertEquals("ID", new Integer(14), items[3].getId());
        assertEquals("Number", "J-01", items[3].getNumber());
        assertEquals("Type 12", new Integer(12), items[3].getTypes()[0]);

        assertEquals("Description", "Cancel fee", items[4].getDescription());
        assertEquals("Price", new BigDecimal("5"), items[4].getPriceAsDecimal());
        assertEquals("ID", new Integer(24), items[4].getId());
        assertEquals("Number", "F-1", items[4].getNumber());
        assertEquals("Type 22", new Integer(22), items[4].getTypes()[0]);


        //    Skipping items created in language/description tests
        //        test001Create
        //        test002CreateMultipleDescriptions
        //        test003ModifyMultipleDescriptions


        System.out.println("Done!");
    }

    @Test
    public void test007UpdateItem() {
        System.out.println("#test007UpdateItem");
    
    	try {
    		JbillingAPI api = JbillingAPIFactory.getAPI();
	    	
    		System.out.println("Getting item");
	    	ItemDTOEx item = api.getItem(new Integer(1), new Integer(2), new PricingField[] {} );
	    	String description = item.getDescription();
	    	String number = item.getNumber();
	    	BigDecimal price = item.getPriceAsDecimal();
	    	BigDecimal perc = item.getPercentageAsDecimal();

	    	String promo = item.getPromoCode();
	
	    	System.out.println("Changing properties");
	    	item.setDescription("Another description");
	    	item.setNumber("NMR-01");
	    	item.setPrice(new BigDecimal("1.00"));
	    	
	    	System.out.println("Updating item");
	    	api.updateItem(item);
	    
	    	ItemDTOEx itemChanged = api.getItem(new Integer(1), new Integer(2), new PricingField[] {} );
	    	assertEquals(itemChanged.getDescription(), "Another description");
	    	assertEquals(itemChanged.getNumber(), "NMR-01");
	    	assertEquals(itemChanged.getPriceAsDecimal(), price);
	    	assertEquals(itemChanged.getPercentageAsDecimal(), perc);
	    	assertEquals(itemChanged.getPromoCode(), promo);
	    	System.out.println("Done!");
	    
	    	System.out.println("Restoring initial item state.");
	    	item.setDescription(description);
	    	item.setNumber(number);
	    	api.updateItem(item);
	    	System.out.println("Done!");

    	} catch (Exception e) {
    		e.printStackTrace();
    		fail("Exception caught:" + e);
    	}
	}

    @Test
    public void test008CurrencyConvert() {
        System.out.println("#test008CurrencyConvert");

    	try {
    		JbillingAPI api = JbillingAPIFactory.getAPI();

            // item 240 "DP-4" has price in AUD - fetch item using a USD customer
            ItemDTOEx item = api.getItem(new Integer(240), new Integer(2), new PricingField[] {} );

            // price automatically converted to user currency when item is fetched
            assertEquals("Price in USD", 1, item.getCurrencyId().intValue());
            assertEquals("Converted price AUD->USD", new BigDecimal("10.0"), item.getPriceAsDecimal());

            // verify that default item price is in AUD
            assertEquals("Default price in AUD", 11, item.getDefaultPrice().getCurrencyId().intValue());
            assertEquals("Default price in AUD", new BigDecimal("15.00"), item.getDefaultPrice().getRateAsDecimal());

    	} catch (Exception e) {
    		e.printStackTrace();
    		fail("Exception caught:" + e);
    	}
    }

    @Test
    public void test009GetAllItemCategories() throws Exception {
        System.out.println("#test009GetAllItemCategories");

        JbillingAPI api = JbillingAPIFactory.getAPI();

        ItemTypeWS[] types = api.getAllItemCategories();

        // includes hidden "plans" categories
        assertEquals("7 item types", 7, types.length);

        assertEquals(1, types[0].getId().intValue());
        assertEquals("Drink passes", types[0].getDescription());
    }

    @Test
    public void test010CreateItemCategory() throws Exception {
        System.out.println("#test010CreateItemCategory");

        try {
            String description = "Ice creams (WS test)";

            System.out.println("Getting API...");
            JbillingAPI api = JbillingAPIFactory.getAPI();

            ItemTypeWS itemType = new ItemTypeWS();
            itemType.setDescription(description);
            itemType.setOrderLineTypeId(1);

            System.out.println("Creating item category '" + description + "'...");
            Integer itemTypeId = api.createItemCategory(itemType);
            assertNotNull(itemTypeId);
            System.out.println("Done.");

            System.out.println("Getting all item categories...");
            ItemTypeWS[] types = api.getAllItemCategories();

            boolean addedFound = false;
            for (int i = 0; i < types.length; ++i) {
                if (description.equals(types[i].getDescription())) {
                    System.out.println("Test category was found. Creation was completed successfully.");
                    addedFound = true;
                    break;
                }
            }
            assertTrue("Ice cream not found.", addedFound);

            //Test the creation of a category with the same description as another one.
            System.out.println("Going to create a category with the same description.");

            try {
                itemTypeId = api.createItemCategory(itemType);
                fail("It should have thrown a SessionInternalError exception.");
            } catch (SessionInternalError sessionInternalError) {
                System.out.println("Exception caught. The category was not created because another one already existed with the same description.");
            }

            System.out.println("Test completed!");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception caught:" + e);
        }
    }

    @Test
    public void test011UpdateItemCategory() throws Exception {
        System.out.println("#test011UpdateItemCategory");

        try {
            Integer categoryId;
            String originalDescription;
            String description = "Drink passes (WS test)";

            System.out.println("Getting API...");
            JbillingAPI api = JbillingAPIFactory.getAPI();

            System.out.println("Getting all item categories...");
            ItemTypeWS[] types = api.getAllItemCategories();

            System.out.println("Changing description...");
            categoryId = types[0].getId();
            originalDescription = types[0].getDescription();
            types[0].setDescription(description);
            api.updateItemCategory(types[0]);

            System.out.println("Getting all item categories...");
            types = api.getAllItemCategories();
            System.out.println("Verifying description has changed...");
            for (int i = 0; i < types.length; ++i) {
                if (categoryId.equals(types[i].getId())) {
                    assertEquals(description, types[i].getDescription());

                    System.out.println("Restoring description...");
                    types[i].setDescription(originalDescription);
                    api.updateItemCategory(types[i]);
                    break;
                }
            }

            //Test the update of a category description to match one from another description.
            System.out.println("Getting all item categories...");
            types = api.getAllItemCategories();
            System.out.println("Storing an existent description");
            String usedDescription = types[0].getDescription();
            System.out.println("Changing the description of another category for this one.");
            types[1].setDescription(usedDescription);

            try {
                api.updateItemCategory(types[1]);
                fail("It should have thrown a SessionInternalError exception.");
            } catch (SessionInternalError sessionInternalError) {
                System.out.println("Exception caught. The category was not updated because another one already existed with the same description.");
            }

            System.out.println("Test completed!");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception caught:" + e);
        }
    }

    @Test
    public void test012GetItemsByCategory() throws Exception {
        System.out.println("#test012GetItemsByCategory");

        JbillingAPI api = JbillingAPIFactory.getAPI();

        final Integer DRINK_ITEM_CATEGORY_ID = 2;

        ItemDTOEx[] items = api.getItemByCategory(DRINK_ITEM_CATEGORY_ID);

        assertEquals("1 item in category 2", 1, items.length);
        assertEquals(4, items[0].getId().intValue());
        assertEquals("Poison Ivy juice (cold)", items[0].getDescription());
    }
}
