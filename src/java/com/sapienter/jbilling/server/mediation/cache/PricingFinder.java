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

package com.sapienter.jbilling.server.mediation.cache;

import java.math.BigDecimal;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StopWatch;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.server.item.PricingField;
import com.sapienter.jbilling.server.item.tasks.PricingResult;
import com.sapienter.jbilling.server.util.Context;

public class PricingFinder extends AbstractFinder {

    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(PricingFinder.class));

    private static final String SPACE = " ";
    private static final String COMMA = ", ";

    public static final PricingFinder getInstance() {
        Object bean = Context.getBean(Context.Name.PRICING_FINDER);
        LOG.debug("Method: getInstance() found: %s", bean);
        return (PricingFinder) bean;
    }

    PricingFinder(JdbcTemplate template, ILoader loader) {
        super(template, loader);
    }

    /**
     * 
     */
    private void init() {
        StopWatch watch = new StopWatch();
        watch.start();
        LOG.debug("Finder Initialized successfully.");
        watch.stop();
        LOG.debug("Watch: %s", watch.toString());
    }

    public BigDecimal getPriceForDestination(String digits) {
        BigDecimal retVal = null;
        try {
            String query = "Select TOP 1 price from " + loader.getTableName()
                    + " Where '" + digits
                    + "' like CONCAT(dgts, '%') order by dgts desc;";
            LOG.debug("Method: getPriceForDestination - Select query:\n"
                    + query);
            retVal = (BigDecimal) this.jdbcTemplate.queryForObject(query,
                    BigDecimal.class);
            LOG.debug("Method: getPriceForDestination - Best Match: " + retVal);
        } catch (Exception e) {
            LOG.error(
                    "ERROR occurred in PricingFinder.getPriceForDestination %s output %s ",
                    digits, retVal);
        }
        return retVal;
    }

    public BigDecimal getPriceForItemAndNumber(PricingField pricingField) {
        return getPricingResultForItemNumber(pricingField).getPrice();
    }

    public PricingResult getPricingResultForItemNumber(PricingField pricingField) {
        PricingResult result = null;
        String strSql = null;
        if ("dst".equalsIgnoreCase(pricingField.getName())) {
            String query = "Select TOP 1 price from " + loader.getTableName()
                    + " Where " + pricingField.getStrValue()
                    + " like CONCAT(dgts, '%') order by dgts desc;";
        }

        // this.jdbcTemplate.query("", new PricingResultMapper());

        return result;
    }

}
