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


import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.joda.time.DateMidnight;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;

public class PeriodOfTime implements Serializable {
    
    private final DateMidnight start;
    private final DateMidnight end;
    private final int position;
    private final int daysInCycle;

    public PeriodOfTime(Date start, Date end, int dayInCycle, int position) {
        this.start = start != null ? new DateMidnight(start.getTime()) : null;
        this.end = end != null ? new DateMidnight(end.getTime()) : null;
        this.position = position;
        this.daysInCycle = dayInCycle;
    }

    public Date getEnd() {
        return end != null ? end.toDate() : null;
    }

    public DateMidnight getDateMidnightEnd() {
        return end;
    }

    public int getPosition() {
        return position;
    }

    public Date getStart() {
        return start != null ? start.toDate() : null;
    }

    public DateMidnight getDateMidnightStart() {
        return start;
    }

    public int getDaysInCycle() {
        return daysInCycle;
    }

    /**
     * Find the number of days between the period start date to the period end date. This means
     * that the start date is counted as a days within the period, but not the end date. For example, January 01 to
     * January 10th includes 9 days total.
     *
     * This method takes into account daylight savings time to ensure that days are counted
     * correctly across DST boundaries.
     *
     * @return number of days between start and end dates
     */
    public int getDaysInPeriod() {
        if (start == null || end == null || end.isBefore(start)) {
            return 0;
        }
        return Days.daysBetween(start, end).getDays();
    }

    @Override
    public String toString() {
        return "period starts: " + start + " ends " + end + " position "
                + position + " days in cycle " + getDaysInCycle();
    }
}
