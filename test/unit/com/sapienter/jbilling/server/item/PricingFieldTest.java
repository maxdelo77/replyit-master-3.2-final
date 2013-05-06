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

/**
 *
 * @author Brian Cowdery
 * @since 10-11-2009
 */
package com.sapienter.jbilling.server.item;

import junit.framework.TestCase;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class PricingFieldTest extends TestCase {

    private Date DATE_VALUE;
    private String DATE_VALUE_STRING;

    public PricingFieldTest() {
        super();
    }

    public PricingFieldTest(String name) {
        super(name);

        Calendar calendar = GregorianCalendar.getInstance();
        calendar.clear();
        calendar.set(2009, 11, 16);

        DATE_VALUE = calendar.getTime();
        DATE_VALUE_STRING = String.valueOf(DATE_VALUE.getTime());        
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testGetValue() {
        PricingField string = new PricingField("str field", "Some String");
        assertEquals("Some String", string.getValue());

        PricingField date = new PricingField("date field", DATE_VALUE);
        assertEquals(DATE_VALUE, date.getValue());

        PricingField integer = new PricingField("int field", 2009);
        assertEquals(2009, ((Integer) integer.getValue()).intValue());

        PricingField decimal = new PricingField("decimal field", new BigDecimal("20.63"));
        assertEquals(new BigDecimal("20.63").toString(), ((BigDecimal) decimal.getValue()).toString());
    }

    public void testGetStrValue() {
        PricingField string = new PricingField("str field", "Some String");
        assertEquals("Some String", string.getStrValue());

        PricingField date = new PricingField("date field", DATE_VALUE);
        assertEquals(DATE_VALUE_STRING, date.getStrValue());

        PricingField integer = new PricingField("int field", 2009);
        assertEquals("2009", integer.getStrValue());

        PricingField decimal = new PricingField("decimal field", new BigDecimal("20.63"));
        assertEquals("20.63", decimal.getStrValue());
    }

    public void testGetDateValue() {
        PricingField date = new PricingField("date field", DATE_VALUE);

        assertEquals(PricingField.Type.DATE, date.getType());
        assertEquals(DATE_VALUE, date.getDateValue());
    }

    public void testGetCalendarValue() {
        PricingField date = new PricingField("date field", DATE_VALUE);

        assertEquals(PricingField.Type.DATE, date.getType());

        Calendar calendar = date.getCalendarValue();
        assertEquals(2009, calendar.get(Calendar.YEAR));
        assertEquals(11, calendar.get(Calendar.MONTH));
        assertEquals(16, calendar.get(Calendar.DAY_OF_MONTH));  
    }

    public void testGetIntegerValue() {
        PricingField integer = new PricingField("int field", 2009);

        assertEquals(PricingField.Type.INTEGER, integer.getType());
        assertEquals(2009, integer.getIntValue().intValue());
    }

    public void testGetDecimalValue() {
        PricingField decimal = new PricingField("decimal field", new BigDecimal("20.63"));

        assertEquals(PricingField.Type.DECIMAL, decimal.getType());
        assertEquals(new BigDecimal("20.63"), decimal.getDecimalValue());
    }

    public void testGetBooleanValue() {
        PricingField bool = new PricingField("boolean field", true);

        assertEquals(PricingField.Type.BOOLEAN, bool.getType());
        assertEquals(true, bool.getBooleanValue().booleanValue());

        bool.setBooleanValue(false);
        assertEquals(false, bool.getBooleanValue().booleanValue());               
    }

    public void testEncode() {
        PricingField string = new PricingField("str field", "Some String");
        assertEquals("str field:1:string:Some String", PricingField.encode(string));

        PricingField date = new PricingField("date field", DATE_VALUE);
        assertEquals("date field:1:date:" + DATE_VALUE_STRING, PricingField.encode(date));

        PricingField integer = new PricingField("int field", 2009);
        assertEquals("int field:1:integer:2009", PricingField.encode(integer));

        PricingField decimal = new PricingField("decimal field", new BigDecimal("20.63"));
        assertEquals("decimal field:1:float:20.63", PricingField.encode(decimal));

        PricingField bool = new PricingField("boolean field", true);
        assertEquals("boolean field:1:boolean:true", PricingField.encode(bool));
    }

    public void testDecode() {
        PricingField string = new PricingField("str field:1:string:Some String");
        assertEquals(PricingField.Type.STRING, string.getType());
        assertEquals("Some String", string.getStrValue());      

        PricingField date = new PricingField("date field:1:date:" + DATE_VALUE_STRING);
        assertEquals(DATE_VALUE.getTime(), date.getDateValue().getTime());

        PricingField integer = new PricingField("int field:1:integer:2009");
        assertEquals(PricingField.Type.INTEGER, integer.getType());
        assertEquals(2009, integer.getIntValue().intValue());

        PricingField decimal = new PricingField("decimal field:1:float:20.63");
        assertEquals(PricingField.Type.DECIMAL, decimal.getType());
        assertEquals(new BigDecimal("20.63"), decimal.getDecimalValue());

        PricingField bool = new PricingField("boolean field:1:boolean:true");
        assertEquals(PricingField.Type.BOOLEAN, bool.getType());
        assertEquals(true, bool.getBooleanValue().booleanValue());
    }
}
 