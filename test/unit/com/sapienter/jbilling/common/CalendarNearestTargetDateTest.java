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

package com.sapienter.jbilling.common;

import com.sapienter.jbilling.server.util.CalendarUtils;
import junit.framework.TestCase;
import org.joda.time.DateMidnight;
import org.joda.time.Period;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: Panche.Isajeski
 * @since: 12/06/12
 */
public class CalendarNearestTargetDateTest extends TestCase {

    public void testEndOfMonthTargetDateInPast() {

        Calendar calendar = GregorianCalendar.getInstance();
        calendar.clear();

        calendar.set(2006, Calendar.NOVEMBER, 30);

        Date sourceDate = calendar.getTime();

        calendar.set(2012, Calendar.NOVEMBER, 05);

        Date targetDate = calendar.getTime();

        Date nearestDate = CalendarUtils.findNearestTargetDateInPast(sourceDate, targetDate, 31, 1, 1);

        calendar.set(2012, Calendar.OCTOBER, 31);
        assertEquals("nearest target in past for end of month not matching", calendar.getTime(), nearestDate);

    }

    public void testFindNearestTargetDateInPast() {

        Calendar calendar = GregorianCalendar.getInstance();
        calendar.clear();

        calendar.set(2006, Calendar.AUGUST, 31);

        Date sourceDate = calendar.getTime();

        calendar.set(2012, Calendar.MAY, 10);

        Date targetDate = calendar.getTime();

        Date nearestDate = CalendarUtils.findNearestTargetDateInPast(sourceDate, targetDate, 31, 1, 1);

        calendar.set(2012, Calendar.APRIL, 30);
        assertEquals("nearest target in past not matching", calendar.getTime(), nearestDate);

    }

    public void testFebruaryLeapYearInPast() {

        Calendar calendar = GregorianCalendar.getInstance();
        calendar.clear();

        calendar.set(2006, Calendar.AUGUST, 31);

        Date sourceDate = calendar.getTime();

        calendar.set(2012, Calendar.MARCH, 10);

        Date targetDate = calendar.getTime();

        Date nearestDate = CalendarUtils.findNearestTargetDateInPast(sourceDate, targetDate, 30, 1, 1);

        calendar.set(2012, Calendar.FEBRUARY, 29);
        assertEquals("leap year february date in past not matching", calendar.getTime(), nearestDate);

    }

    public void testFebruaryNonLeapYearInPast() {

        Calendar calendar = GregorianCalendar.getInstance();
        calendar.clear();

        calendar.set(2006, Calendar.AUGUST, 31);

        Date sourceDate = calendar.getTime();

        calendar.set(2011, Calendar.MARCH, 10);

        Date targetDate = calendar.getTime();

        Date nearestDate = CalendarUtils.findNearestTargetDateInPast(sourceDate, targetDate, 30, 1, 1);

        calendar.set(2011, Calendar.FEBRUARY, 28);
        assertEquals("non leap year february date in past not matching", calendar.getTime(), nearestDate);

    }

    public void testFindNearestTargetDateInPastNegative() {

        Calendar calendar = GregorianCalendar.getInstance();
        calendar.clear();

        calendar.set(2011, Calendar.OCTOBER, 15);

        Date sourceDate = calendar.getTime();

        calendar.set(2011, Calendar.JULY, 10);

        Date targetDate = calendar.getTime();

        Date nearestDate = CalendarUtils.findNearestTargetDateInPast(sourceDate, targetDate, 15, 1, 1);

        calendar.set(2011, Calendar.JUNE, 15);
        assertEquals("nearest negative target date in past not matching", calendar.getTime(), nearestDate);

    }

    public void testFindNearestTargetDateInFuture() {


        Calendar calendar = GregorianCalendar.getInstance();
        calendar.clear();

        calendar.set(2006, Calendar.OCTOBER, 31);

        Date sourceDate = calendar.getTime();

        calendar.set(2012, Calendar.APRIL, 15);

        Date targetDate = calendar.getTime();

        Date nearestDate = CalendarUtils.findNearestTargetDateInFuture(sourceDate, targetDate, 31, 1, 3);

        calendar.set(2012, Calendar.APRIL, 30);
        assertEquals("nearest target in future not matching", calendar.getTime(), nearestDate);

    }

    public void testEndOfMonthTargetDateInFuture() {

        Calendar calendar = GregorianCalendar.getInstance();
        calendar.clear();

        calendar.set(2011, Calendar.NOVEMBER, 30);

        Date sourceDate = calendar.getTime();

        calendar.set(2012, Calendar.MAY, 05);

        Date targetDate = calendar.getTime();

        Date nearestDate = CalendarUtils.findNearestTargetDateInFuture(sourceDate, targetDate, 31, 1, 1);

        calendar.set(2012, Calendar.MAY, 31);
        assertEquals("nearest target in future for end of month not matching", calendar.getTime(), nearestDate);

    }

    public void testFebruaryLeapYearInFuture() {

        Calendar calendar = GregorianCalendar.getInstance();
        calendar.clear();

        calendar.set(2006, Calendar.SEPTEMBER, 30);

        Date sourceDate = calendar.getTime();

        calendar.set(2012, Calendar.FEBRUARY, 10);

        Date targetDate = calendar.getTime();

        Date nearestDate = CalendarUtils.findNearestTargetDateInFuture(sourceDate, targetDate, 31, 1, 1);

        calendar.set(2012, Calendar.FEBRUARY, 29);
        assertEquals("leap year february date in future not matching", calendar.getTime(), nearestDate);

    }

    public void testFebruaryNonLeapYearInFuture() {

        Calendar calendar = GregorianCalendar.getInstance();
        calendar.clear();

        calendar.set(2006, Calendar.AUGUST, 31);

        Date sourceDate = calendar.getTime();

        calendar.set(2011, Calendar.FEBRUARY, 10);

        Date targetDate = calendar.getTime();

        Date nearestDate = CalendarUtils.findNearestTargetDateInFuture(sourceDate, targetDate, 31, 1, 1);

        calendar.set(2011, Calendar.FEBRUARY, 28);
        assertEquals("non leap year february date in future not matching", calendar.getTime(), nearestDate);

    }
}
