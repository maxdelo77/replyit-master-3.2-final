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
package com.sapienter.jbilling.server.process.task;

import java.math.BigDecimal;

import org.apache.log4j.Logger;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.server.pluggableTask.BasicCompositionTask;
import com.sapienter.jbilling.server.process.PeriodOfTime;
import com.sapienter.jbilling.server.util.Constants;

/**
 * Calculates the pro-rated amount taking the smallest unit: days
 * @author emilc
 *
 */
public class DailyProRateCompositionTask extends BasicCompositionTask {
    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(DailyProRateCompositionTask.class));
    
    public BigDecimal calculatePeriodAmount(BigDecimal fullPrice, PeriodOfTime period) {
        if (period == null || fullPrice == null) {
            LOG.warn("Called with null parameters");
            return null;
        }
        
        // this is an amount from a one-time order, not a real period of time
        if (period.getDaysInCycle() == 0) {
            return fullPrice;
        }
        
        // if this is not a fraction of a period, don't bother making any calculations
        if (period.getDaysInCycle() == period.getDaysInPeriod()) {
            return fullPrice;
        }
        
        BigDecimal oneDay = fullPrice.divide(new BigDecimal(period.getDaysInCycle()), 
                Constants.BIGDECIMAL_SCALE, Constants.BIGDECIMAL_ROUND);
        BigDecimal proRatedAmount = oneDay.multiply(new BigDecimal(period.getDaysInPeriod()));
        return proRatedAmount;
    }

}
