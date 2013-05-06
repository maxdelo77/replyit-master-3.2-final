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

/*
 * Created on Apr 25, 2003
 *
 */
package com.sapienter.jbilling.server.util;

import java.util.GregorianCalendar;

import com.sapienter.jbilling.common.SessionInternalError;

/**
 * @author emilc
 *
 */
public class MapPeriodToCalendar {
    public static int map(Integer period) 
            throws SessionInternalError {
        int retValue;
        
        if (period == null) {
            throw new SessionInternalError("Can't map a period that is null");
        }
        if (period.compareTo(Constants.PERIOD_UNIT_DAY) == 0) {
            retValue = GregorianCalendar.DAY_OF_YEAR;
        } else if (period.compareTo(Constants.PERIOD_UNIT_MONTH) == 0) {
            retValue = GregorianCalendar.MONTH;
        } else if (period.compareTo(Constants.PERIOD_UNIT_WEEK) == 0) {
            retValue = GregorianCalendar.WEEK_OF_YEAR;
        } else if (period.compareTo(Constants.PERIOD_UNIT_YEAR) == 0) {
            retValue = GregorianCalendar.YEAR;
        } else { // error !
            throw new SessionInternalError("Period not supported:" + period);
        }
        
        return retValue;
    }
    
    public static int periodToDays(Integer period) {
        int retValue = 0;
        if (period == null) {
            throw new SessionInternalError("Can't convert a period that is null");
        }
        if (period.compareTo(Constants.PERIOD_UNIT_DAY) == 0) {
            retValue = 1;
        } else if (period.compareTo(Constants.PERIOD_UNIT_MONTH) == 0) {
            retValue = 31;
        } else if (period.compareTo(Constants.PERIOD_UNIT_WEEK) == 0) {
            retValue = 7;
        } else if (period.compareTo(Constants.PERIOD_UNIT_YEAR) == 0) {
            retValue = 365;
        } else { // error !
            throw new SessionInternalError("Period not supported:" + period);
        }
        
        return retValue;
    }
}
