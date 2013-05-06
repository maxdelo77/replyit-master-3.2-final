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

package com.sapienter.jbilling.server.pricing.cache;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.sapienter.jbilling.common.FormatLogger;

import java.math.BigDecimal;

/**
 * MatchType specifies the logic used by {@link RateCardFinder} when determining if a pricing
 * field from mediation matches the 'match' column of the rating table when looking for a price.
 *
 * @author Brian Cowdery
 * @since 19-02-2012
 */
public enum MatchType {

    /**
     * Searches for an entry in the rating table where the entry exactly matches the search value
     */
    EXACT {
        public BigDecimal findPrice(JdbcTemplate jdbcTemplate, String query, String searchValue) {
            LOG.debug("Searching for exact match '" + searchValue + "'");
            SqlRowSet rs = jdbcTemplate.queryForRowSet(query, searchValue);

            if (rs.next())
                return rs.getBigDecimal("rate");

            return null;
        }
    },

    /**
     * Searches through the rating table looking for an entry using the search value as
     * a prefix. The BEST_MATCH continually shortens the prefix being used in the search
     * to find a match with the largest possible portion of the search string.
     */
    BEST_MATCH {
        public BigDecimal findPrice(JdbcTemplate jdbcTemplate, String query, String searchValue) {
            int length = 10;
            searchValue = getCharacters(searchValue, length);

            while (length >= 0) {
                LOG.debug("Searching for prefix '" + searchValue + "'");
                SqlRowSet rs = jdbcTemplate.queryForRowSet(query, searchValue);

                if (rs.next()) {
                    return rs.getBigDecimal("rate");
                } else {
                    length--;
                    searchValue = getCharacters(searchValue, length);
                }
            }

            return null;
        }

        public String getCharacters(String number, int length) {
            if (length <= 0) return "*";
            return number.length() > length ? number.substring(0, length) : number;
        }
    };


    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(MatchType.class));

    public abstract BigDecimal findPrice(JdbcTemplate jdbcTemplate, String query, String searchValue);
}
