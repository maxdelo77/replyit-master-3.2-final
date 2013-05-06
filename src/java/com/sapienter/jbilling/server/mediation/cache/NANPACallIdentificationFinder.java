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

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.server.util.Context;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * NANPACallIdentificationFinder to query call destination data based on the called international
 * code, area code and sub-area code.
 *
 * Rules:
 * <code>
 *  rule 'Resolve Call Destination'
 *  no-loop
 *  dialect 'java'
 *  when
 *      $result : MediationResult( step == MediationResult.STEP_4_RESOLVE_ITEM,
 *                                  description != null,
 *                                  lines.empty == false )
 *
 *      PricingField( name == "dst", $dst : strValue != null, resultId == $result.id )
 *
 *      # Only for international calls
 *      OrderLineDTO( itemId == 40 ) from $result.lines
 *
 *  then
 *      NANPACallIdentificationFinder finder = NANPACallIdentificationFinder.getInstance();
 *      String destination = finder.findCallDescription($dst);
 *
 *      $result.setDescription($result.getDescription() + " " + destination);
 *
 *      LOG.debug("Found call destination '" + destination + "', for: " + $dst);
 *  end
 * </code>
 *
 * See descriptors/spring/jbilling-caching.xml for configuration
 *
 * @author Brian Cowdery
 * @since 29-11-2010
 */
public class NANPACallIdentificationFinder extends AbstractFinder {
    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(NANPACallIdentificationFinder.class));

    private static final String BLANK = "";
    private static final String DEFAULT_DESCRIPTION = "Unknown";
    
    private static final Integer MAX_OCN_LENGTH = 3;
    private static final Integer MAX_RATE_CENTER_LENGTH = 5;

    public static NANPACallIdentificationFinder getInstance() {
        Object bean = Context.getBean(Context.Name.NANPA_CALL_IDENTIFICATION_FINDER);
        return (NANPACallIdentificationFinder) bean;
    }

    public NANPACallIdentificationFinder(JdbcTemplate template, ILoader loader) {
        super(template, loader);
    }

    public void init() {
        // noop
    }

    public String findCallDescription(String number) {
        LOG.debug("Identifying call '%s'", number);

        boolean nanp = isNANP(number);
        String internationalCode = getInternationalCode(number);
        String ocn = nanp ? getOCN(number) : findOCN(internationalCode, number);
        String rateCenter = nanp ? getRateCenter(number) : findRateCenter(internationalCode, ocn, number);
        
        LOG.debug("International code: '%s', OCN: '%s', RC: '%s'", internationalCode, ocn, rateCenter);

        return (String) jdbcTemplate.query(
                "select description "
                + " from " + loader.getTableName()
                + " where intl_code = ? "
                + " and ocn = ? "
                + " and rate_center = ?",
                new Object[] {
                        internationalCode,
                        ocn,
                        rateCenter
                },
                new ResultSetExtractor() {
                    public String extractData(ResultSet rs) throws SQLException, DataAccessException {
                        if (rs.next()) {
                            return rs.getString("description");
                        }
                        LOG.debug("No call identification data found.");
                        return DEFAULT_DESCRIPTION;
                    }
                });
        
    }

    /**
     * Returns the dialed international prefix code for the given phone number. Dialed
     * international codes can be either "011" or "1".
     *
     * @param number phone number.
     * @return returns 011, 1 or null if no international prefix code was dialed.
     */
    public String getInternationalCode(String number) {
        if (number.startsWith("011")) return "011";
        if (number.startsWith("1")) return "1";
        return null;            
    }

    /**
     * Returns true if the number dialed is a North American Number Plan (NANP)
     * long distance number and not a 011 international dialed number.
     *
     * @param number phone number
     * @return true if NANP long distance number.
     */
    public boolean isNANP(String number) {
        return number.matches("^(0111|1).*");
    }


    /**
     * Searches for the OCN of the given number in the rate center table. This search
     * attempts to find the best match by incrementally shortening the search OCN value
     * by one digit (e.g., search for 123, then 12, then 1). 
     *
     * @param internationalCode international code
     * @param number phone number to search
     * @return OCN
     */
    public String findOCN(String internationalCode, String number) {        
        if (internationalCode != null) number = number.replaceFirst("^" + internationalCode, BLANK);

        int length = MAX_OCN_LENGTH;
        number = getDigits(number, length);

        while (length >= 0) {
            LOG.debug("Searching for ocn: %s", number);

            SqlRowSet rs = jdbcTemplate.queryForRowSet(
                    "select distinct(ocn) "
                    + " from " + loader.getTableName()
                    + " where intl_code = ? "
                    + " and ocn = ?",
                    new Object[] {
                            internationalCode,
                            number
                    }
            );

            if (rs.next()) {
                return rs.getString("ocn");
            } else {
                length--;
                number = getDigits(number, length);
            }
        }

        return null;
    }

    /**
     * Searches for the Rate Center of the given number in the rate center table. This search
     * attempts to find the best match by incrementally shortening the search Rate Center value
     * by one digit (e.g., search for 123, then 12, then 1).
     *
     * @param internationalCode international code
     * @param ocn ocn
     * @param number phone number to search
     * @return rate center
     */
    public String findRateCenter(String internationalCode, String ocn, String number) {
        if (internationalCode != null) number = number.replaceFirst("^" + internationalCode, BLANK);
        if (ocn != null) number = number.replaceFirst("^" + ocn, BLANK);

        int length = MAX_RATE_CENTER_LENGTH;
        number = getDigits(number, length);

        while (length >= 0) {
            LOG.debug("Searching for rate center: %s", number);

            SqlRowSet rs = jdbcTemplate.queryForRowSet(
                    "select distinct(rate_center) "
                    + " from " + loader.getTableName()
                    + " where intl_code = ? "
                    + " and ocn = ? "
                    + " and rate_center = ?",
                    new Object[] {
                            internationalCode,
                            ocn,
                            number
                    }
            );

            if (rs.next()) {
                return rs.getString("rate_center");
            } else {
                length--;
                number = getDigits(number, length);
            }
        }

        return null;
    }

    /**
     * Returns the OCN number from the given 10-digit NANP called number.
     *
     * @param number phone number
     * @return OCN
     */
    public String getOCN(String number) {
        return number.length() >= 10 ? number.substring(number.length() - 10, number.length() - 7) : null;
    }

    /**
     * Returns the Rate Center number from the given 10-digit NANP called number.
     *
     * @param number phone number
     * @return rate center
     */
    public String getRateCenter(String number) {
        return number.length() >= 7 ? number.substring(number.length() - 7, number.length() - 4) : null;
    }       

    /**
     * Shortens a given number string down to the given length by removing the
     * trailing numbers. If the length is zero or less-than zero, the string "0"
     * will be returned as a possible wildcard value for lookup.
     *
     * @param length desired length
     * @param number to shorten
     * @return country code shortened to the desired length
     */
    public String getDigits(String number, int length) {
        if (length <= 0) return "0";
        return number.length() > length ? number.substring(0, length) : number;
    }
}
