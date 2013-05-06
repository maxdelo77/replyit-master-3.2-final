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

package com.sapienter.jbilling.server.process;

import junit.framework.TestCase;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * BusinessDaysTest
 *
 * @author Brian Cowdery
 * @since 29/04/11
 */
public class BusinessDaysTest extends TestCase {

    private static final Calendar calendar = GregorianCalendar.getInstance();

    // class under test
    BusinessDays businessDays = new BusinessDays();

    public BusinessDaysTest() {
    }

    public BusinessDaysTest(String name) {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        calendar.clear();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testAddBusinessDays() {
        // test add days over Saturday and Sunday
        calendar.set(2010, Calendar.APRIL, 29);                          // friday april 29th
        Date date = businessDays.addBusinessDays(calendar.getTime(), 3); // + 3 business days

        calendar.set(2010, Calendar.MAY, 4);
        assertEquals(date, calendar.getTime()); // should equal wednesday may 4th


        // test add days over Sunday
        calendar.set(2010, Calendar.MAY, 1);                        // sunday may 1st
        date = businessDays.addBusinessDays(calendar.getTime(), 3); // + 3 business days

        calendar.set(2010, Calendar.MAY, 5);
        assertEquals(date, calendar.getTime()); // should equal thursday may 5th
    }
}
