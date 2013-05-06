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

package com.sapienter.jbilling.server.util;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.common.SessionInternalError;
import org.apache.log4j.Logger;
import org.joda.time.*;
import org.joda.time.base.BaseSingleFieldPeriod;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: Panche.Isajeski
 * @since: 12/06/12
 */
public class CalendarUtils {

    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(CalendarUtils.class));

    public static Date findNearestTargetDateInPast(Date sourceDate, Date targetDate,
                                                   Integer nextInvoiceDaysOfPeriod,
                                                   Integer periodUnit, Integer periodValue) {

        DateTime sourceDatetime = new DateTime(sourceDate);
        DateTime targetDatetime = new DateTime(targetDate);

        Period datePeriod = getPeriodBetweenDates(sourceDatetime, targetDatetime, periodUnit, periodValue);

        LOG.debug("Past: Period between source date: %s and target date %s is %s ", sourceDatetime, targetDatetime, datePeriod);

        sourceDatetime = sourceDatetime.plus(datePeriod);

        // this would execute only once
        while (sourceDatetime.isAfter(targetDatetime)) {
            // get single period
            datePeriod = addUnitToPeriod(null, periodUnit, periodValue);
            sourceDatetime = sourceDatetime.minus(datePeriod);
        }

        // check if the source datetime is matching the nextInvoiceDaysOfPeriod for month unit
        if (periodUnit.compareTo(Constants.PERIOD_UNIT_MONTH) == 0) {
            if (sourceDatetime.getDayOfMonth() < nextInvoiceDaysOfPeriod
                    && sourceDatetime.dayOfMonth().getMaximumValue() >= nextInvoiceDaysOfPeriod) {
                sourceDatetime = sourceDatetime.withDayOfMonth(nextInvoiceDaysOfPeriod);
            }
        }

        return sourceDatetime.toDate();

    }

    public static Date findNearestTargetDateInFuture(Date sourceDate, Date targetDate,
                                                     Integer nextInvoiceDaysOfPeriod,
                                                     Integer periodUnit, Integer periodValue) {

        DateTime sourceDatetime = new DateTime(sourceDate);
        DateTime targetDatetime = new DateTime(targetDate);

        Period datePeriod = getPeriodBetweenDates(sourceDatetime, targetDatetime, periodUnit, periodValue);

        LOG.debug("Future: Period between source date: %s and target date %s is %s ", sourceDatetime, targetDatetime, datePeriod);

        sourceDatetime = sourceDatetime.plus(datePeriod);

        // this would execute only once
        while (sourceDatetime.isBefore(targetDatetime)) {
            // get single period
            datePeriod = addUnitToPeriod(null, periodUnit, periodValue);
            sourceDatetime = sourceDatetime.plus(datePeriod);
        }

        // check if the source datetime is matching the nextInvoiceDaysOfPeriod for month unit
        if (periodUnit.compareTo(Constants.PERIOD_UNIT_MONTH) == 0) {
            if (sourceDatetime.getDayOfMonth() < nextInvoiceDaysOfPeriod
                    && sourceDatetime.dayOfMonth().getMaximumValue() >= nextInvoiceDaysOfPeriod) {
                sourceDatetime = sourceDatetime.withDayOfMonth(nextInvoiceDaysOfPeriod);
            }
        }

        return sourceDatetime.toDate();
    }


    public static Period getPeriodBetweenDates(DateTime sourceDate, DateTime targetDate,
                                               Integer periodUnit, Integer periodValue) {

        BaseSingleFieldPeriod retValue;

        if (periodUnit == null) {
            throw new SessionInternalError("Can't get a period that is null");
        }
        if (periodUnit.compareTo(Constants.PERIOD_UNIT_DAY) == 0) {
            retValue = Days.daysBetween(sourceDate, targetDate).dividedBy(periodValue).multipliedBy(periodValue);
        } else if (periodUnit.compareTo(Constants.PERIOD_UNIT_MONTH) == 0) {
            retValue = Months.monthsBetween(sourceDate, targetDate).dividedBy(periodValue).multipliedBy(periodValue);
        } else if (periodUnit.compareTo(Constants.PERIOD_UNIT_WEEK) == 0) {
            retValue = Weeks.weeksBetween(sourceDate, targetDate).dividedBy(periodValue).multipliedBy(periodValue);
        } else if (periodUnit.compareTo(Constants.PERIOD_UNIT_YEAR) == 0) {
            retValue = Years.yearsBetween(sourceDate, targetDate).dividedBy(periodValue).multipliedBy(periodValue);
        } else { // error !
            throw new SessionInternalError("Period not supported:" + periodUnit);
        }

        return retValue.toPeriod();
    }

    public static Period addUnitToPeriod(Period sourcePeriod, Integer periodUnit, Integer periodValue) {

        ReadablePeriod retValue;

        if (periodUnit == null) {
            throw new SessionInternalError("Can't add to a period that is null");
        }
        if (periodUnit.compareTo(Constants.PERIOD_UNIT_DAY) == 0) {
            retValue = sourcePeriod == null ? Period.days(periodValue) :  sourcePeriod.plusDays(periodValue);
        } else if (periodUnit.compareTo(Constants.PERIOD_UNIT_MONTH) == 0) {
            retValue = sourcePeriod == null ? Period.months(periodValue) :  sourcePeriod.plusMonths(periodValue);
        } else if (periodUnit.compareTo(Constants.PERIOD_UNIT_WEEK) == 0) {
            retValue = sourcePeriod == null ? Period.weeks(periodValue) :  sourcePeriod.plusWeeks(periodValue);
        } else if (periodUnit.compareTo(Constants.PERIOD_UNIT_YEAR) == 0) {
            retValue = sourcePeriod == null ? Period.years(periodValue) :  sourcePeriod.plusYears(periodValue);
        } else { // error !
            throw new SessionInternalError("Period not supported:" + periodUnit);
        }

        return retValue.toPeriod();
    }


}
