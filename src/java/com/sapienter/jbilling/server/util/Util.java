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
 * Created on Aug 11, 2004
 */
package com.sapienter.jbilling.server.util;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.common.SessionInternalError;
import com.sapienter.jbilling.server.item.CurrencyBL;
import com.sapienter.jbilling.server.user.UserBL;
import com.sapienter.jbilling.server.util.db.InternationalDescriptionDAS;
import com.sapienter.jbilling.server.util.db.InternationalDescriptionDTO;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;


import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.sapienter.jbilling.common.SessionInternalError;
import com.sapienter.jbilling.server.item.CurrencyBL;
import com.sapienter.jbilling.server.user.UserBL;
import com.sapienter.jbilling.server.util.db.InternationalDescriptionDAS;
import com.sapienter.jbilling.server.util.db.InternationalDescriptionDTO;
import java.math.RoundingMode;


/**
 * @author Emil
 */
public class Util {


    public static final String[] hexLookupTable = {
        "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "0a", "0b", "0c", "0d", "0e", "0f",
        "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "1a", "1b", "1c", "1d", "1e", "1f",
        "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "2a", "2b", "2c", "2d", "2e", "2f",
        "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "3a", "3b", "3c", "3d", "3e", "3f",
        "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "4a", "4b", "4c", "4d", "4e", "4f",
        "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "5a", "5b", "5c", "5d", "5e", "5f",
        "60", "61", "62", "63", "64", "65", "66", "67", "68", "69", "6a", "6b", "6c", "6d", "6e", "6f",
        "70", "71", "72", "73", "74", "75", "76", "77", "78", "79", "7a", "7b", "7c", "7d", "7e", "7f",
        "80", "81", "82", "83", "84", "85", "86", "87", "88", "89", "8a", "8b", "8c", "8d", "8e", "8f",
        "90", "91", "92", "93", "94", "95", "96", "97", "98", "99", "9a", "9b", "9c", "9d", "9e", "9f",
        "a0", "a1", "a2", "a3", "a4", "a5", "a6", "a7", "a8", "a9", "aa", "ab", "ac", "ad", "ae", "af",
        "b0", "b1", "b2", "b3", "b4", "b5", "b6", "b7", "b8", "b9", "ba", "bb", "bc", "bd", "be", "bf",
        "c0", "c1", "c2", "c3", "c4", "c5", "c6", "c7", "c8", "c9", "ca", "cb", "cc", "cd", "ce", "cf",
        "d0", "d1", "d2", "d3", "d4", "d5", "d6", "d7", "d8", "d9", "da", "db", "dc", "dd", "de", "df",
        "e0", "e1", "e2", "e3", "e4", "e5", "e6", "e7", "e8", "e9", "ea", "eb", "ec", "ed", "ee", "ef",
        "f0", "f1", "f2", "f3", "f4", "f5", "f6", "f7", "f8", "f9", "fa", "fb", "fc", "fd", "fe", "ff"
      };

    public static String formatDate(Date date, Integer userId) throws SessionInternalError {
        Locale locale;
        try {
            UserBL user = new UserBL(userId);
            locale = user.getLocale();
        } catch (Exception e) {
            throw new SessionInternalError(e);
        }
        ResourceBundle bundle = ResourceBundle.getBundle("entityNotifications",
                locale);
        SimpleDateFormat df = new SimpleDateFormat(
                bundle.getString("format.date"));
        return df.format(date);
    }

    public static String formatDate(Date date, Locale locale) {
    	if (null == date) {
    		return "";
    	}
    	ResourceBundle bundle = ResourceBundle.getBundle("entityNotifications",
                locale);
        SimpleDateFormat df = new SimpleDateFormat(
                bundle.getString("format.date"));
        return df.format(date);
    }

    public static String formatMoneyWithoutSpace(BigDecimal number, Integer userId, Integer currencyId, boolean forEmail)
            throws SessionInternalError {

        try {
            String result = formatMoney(number, userId, currencyId, forEmail);
            return result.replace(" ","");
        } catch (Exception e) {
            throw new SessionInternalError(e);
        }
    }


    public static String formatMoney(BigDecimal number, Integer userId)
            throws SessionInternalError {

        try {
            // find first the right format for the number
            UserBL user = new UserBL(userId);
            Locale locale = user.getLocale();
            ResourceBundle bundle = ResourceBundle.getBundle(
                    "entityNotifications", locale);

            NumberFormat format = NumberFormat.getNumberInstance(locale);
            ((DecimalFormat) format).applyPattern(bundle
                    .getString("format.float"));

            return format.format(number.doubleValue());
        } catch (Exception e) {
            throw new SessionInternalError(e);
        }
    }
    
    public static String formatMoney(BigDecimal number, Integer userId, Integer currencyId, boolean forEmail)
            throws SessionInternalError {

        try {
            // find first the right format for the number
            UserBL user = new UserBL(userId);
            Locale locale = user.getLocale();
            ResourceBundle bundle = ResourceBundle.getBundle("entityNotifications", locale);

            NumberFormat format = NumberFormat.getNumberInstance(locale);
            ((DecimalFormat) format).applyPattern(bundle.getString("format.float"));

            // now the symbol of the currency
            CurrencyBL currency = new CurrencyBL(currencyId);
            String symbol = currency.getEntity().getSymbol();
            if (symbol.length() >= 4 && symbol.charAt(0) == '&' &&
                    symbol.charAt(1) == '#') {
                if (!forEmail) {
                    // this is an html symbol
                    // remove the first two digits
                    symbol = symbol.substring(2);
                    // remove the last digit (;)
                    symbol = symbol.substring(0, symbol.length() - 1);
                    // convert to a single char
                    Character ch = (char) Integer.valueOf(symbol).intValue();
                    symbol = ch.toString();
                } else {
                    symbol = currency.getEntity().getCode();
                }
            }
            return symbol + " " + format.format(number.doubleValue());
        } catch (Exception e) {
            throw new SessionInternalError(e);
        }
    }

    public static String formatMoney(Float number, Integer userId,
            Integer currencyId, boolean forEmail)
            throws SessionInternalError {
        Locale locale;
        try {
            // find first the right format for the number
            UserBL user = new UserBL(userId);
            locale = user.getLocale();
            ResourceBundle bundle = ResourceBundle.getBundle("entityNotifications",
                    locale);
            NumberFormat format = NumberFormat.getNumberInstance(locale);
            ((DecimalFormat) format).applyPattern(bundle.getString(
                    "format.float"));

            // now the symbol of the currency
            CurrencyBL currency = new CurrencyBL(currencyId);
            String symbol = currency.getEntity().getSymbol();
            if (symbol.length() >= 4 && symbol.charAt(0) == '&' &&
                    symbol.charAt(1) == '#') {
                if (!forEmail) {
                    // this is an html symbol
                    // remove the first two digits
                    symbol = symbol.substring(2);
                    // remove the last digit (;)
                    symbol = symbol.substring(0, symbol.length() - 1);
                    // convert to a single char
                    Character ch = new Character((char) Integer.valueOf(symbol).intValue());
                    symbol = ch.toString();
                } else {
                    symbol = currency.getEntity().getCode();
                }
            }
            return symbol + " " + format.format(number.doubleValue());
        } catch (Exception e) {
            throw new SessionInternalError(e);
        }
    }

    public static String getPeriodUnitStr(Integer id, Integer language) {
        FormatLogger log = new FormatLogger(Logger.getLogger(Util.class));
        InternationalDescriptionDTO inter = ((InternationalDescriptionDAS)
                Context.getBean(Context.Name.DESCRIPTION_DAS)).findIt(Constants.TABLE_PERIOD_UNIT, id,
                "description", language);

       if (inter == null) {
           log.debug("Description not set for period unit " + id + " language" +
                   " " + language);
           return null;
       }

        return inter.getContent();
    }

    public static double round(double val, int places) {
        long factor = (long) Math.pow(10, places);

        //         Shift the decimal the correct number of places
        //         to the right.
        val = val * factor;

        //         Round to the nearest integer.
        long tmp = Math.round(val);

        //         Shift the decimal the correct number of places
        //         back to the left.
        return (double) tmp / factor;
    }

    public static float round(float val, int places) {
        return (float) round((double) val, places);
    }

    public static String decimal2string(BigDecimal arg, Locale loc) {
        if (arg  == null) return null;
        return NumberFormat.getInstance(loc).format(arg);
    }

    public static String float2string(Float arg, Locale loc) {
        if (arg == null) {
            return null;
        }
        NumberFormat nf = NumberFormat.getInstance(loc);
        return nf.format(arg);
    }

    public static String float2string(double arg, Locale loc) {
        return float2string(new Float(arg), loc);
    }

    public static Float string2float (String arg, Locale loc)
        throws ParseException {
        if (arg == null) {
            return null;
        }
        NumberFormat nf = NumberFormat.getInstance(loc);
        return nf.parse(arg).floatValue();
    }

    public static String binaryToString(byte[] string) {
        int readBytes = string.length;
        StringBuffer hexData = new StringBuffer();
        for (int i=0; i < readBytes; i++) {
           hexData.append(hexLookupTable[0xff & string[i]]);
        }
        return hexData.toString();
    }

    public static byte[] stringToBinary(String string) {
        byte retValue[] = new byte[string.length()/2];
        for (int i=0; i < retValue.length; i++) {
            String digit = string.substring(i * 2, (i *2) + 2);
            int hex = Integer.parseInt(digit, 16);
            byte by = (byte) hex;
            retValue[i] = by;
        }
        return retValue;
    }

    public static ResourceBundle getEntityNotificationsBundle(Integer userId) throws SessionInternalError {
        ResourceBundle bundle;
        try {
            UserBL userBL = new UserBL(userId);
            bundle = ResourceBundle.getBundle("entityNotifications", userBL.getLocale());
        } catch (Exception e) {
            throw new SessionInternalError("Error getting user info or resource bundle",
                    Util.class, e);
        }
        return bundle;
    }

    /**
     * Basic CSV line splitting that takes quotes into account.
     * Doesn't do any error checking, e.g., mis-matched quotes.
     */
    public static String[] csvSplitLine(String line, char fieldSeparator) {
        LinkedList<String> fields = new LinkedList<String>();
        boolean inQuote = false; // whether inside a quotation
        String field = "";
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == '\"') {
                if (inQuote && i + 1 != line.length() && line.charAt(i + 1) == '\"') {
                    field += '\"';
                    i++; // skip over quote escape
                } else {
                    inQuote = !inQuote;
                }
            } else if (!inQuote && line.charAt(i) == fieldSeparator) {
                fields.add(field);
                field = "";
            } else {
                field += line.charAt(i);
            }
        }
        fields.add(field); // after last ','

        return fields.toArray(new String[0]);
    }

    /**
     * Joining by separator after each 'not last' value
     *
     * @param lst list for joining
     * @param separator separator string
     * @return joined string
     */
    public static String join(List<String> lst, String separator) {
        if (lst == null) return "";
        StringBuilder str = new StringBuilder();
        Iterator<String> iter = lst.iterator();
        while (iter.hasNext()) {
            str.append(iter.next());
            if (iter.hasNext()) {
                str.append(separator);
            }
        }
        return str.toString();
    }

    /**
     * Basic CSV line concatination with characters escaping
     *
     * @param values values for concatination
     * @param fieldSeparator character for fields separation
     * @return concatinated string
     */
    public static String concatCsvLine(List<String> values, String fieldSeparator) {

        if (values == null || values.isEmpty()) return null;
        StringBuilder builder = new StringBuilder(escapeStringForCsvFormat(values.get(0), fieldSeparator));
        for (int i = 1; i < values.size(); i++) {
            //add separator for 'not last' element
            builder.append(fieldSeparator);
            builder.append(escapeStringForCsvFormat(values.get(i), fieldSeparator));
        }
        return builder.toString();
    }

    public static void closeQuietly(PreparedStatement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException ignored) {
            }
        }
    }

    public static void closeQuietly(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException ignored) {
            }
        }
    }

    public static void closeQuietly(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ignored) {
            }
        }
    }

    private static String escapeStringForCsvFormat(String str, String fieldSeparator) {
        if (str == null) return "";
        //is escaping fieldSeparators and line separators by quotes needed
        boolean inQuotes = str.indexOf(fieldSeparator) != -1 || str.indexOf('\n') != -1;
        StringBuilder builder = new StringBuilder();
        if (inQuotes) builder.append('\"');
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            //escaping quote by duplicating it
            if (ch == '\"') {
                builder.append('\"');
            }
            builder.append(ch);
        }
        if (inQuotes) builder.append('\"');
        return builder.toString();
    }

    /**
     * Escapes special characters in a given String for using with XML.
     * </p>
     * These characters are escaped:
     * <uL>
     *     <li>&</li>
     *     <li><</li>
     *     <li>></li>
     *     <li>"</li>
     *     <li>'</li>
     * </uL>
     * @param str String to format.
     * @return A String fully formatted.
     */
    public static String escapeStringForXmlFormat(String str) {
        if (str == null) return "";

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            String newValue = "";

            // Escape &, <, >, ", '
            if (ch == '&') {
                newValue = "&amp;";
            } else if (ch == '<') {
                newValue = "&lt;";
            } else if (ch == '>') {
                newValue = "&gt;";
            } else if (ch == '\"') {
                newValue = "&quot;";
            } else if (ch == '\'') {
                newValue = "&apos;";
            }

            builder.append(StringUtils.isEmpty(newValue) ? ch : newValue);
        }

        return builder.toString();
    }

    /**
     * Accepts the possible date alongwith its pattern which should be checked against and the error String to throw if
     * date is un-parsable. The errorString can use standard way of populating erorrs from message.properties.
     * @param pattern
     * @param canBeDate
     * @param errorString
     * @return
     * @throws SessionInternalError
     */
    public static DateTime getParsedDateOrThrowError(String pattern, String canBeDate, String errorString) throws SessionInternalError {

        DateTimeFormatter fmt = DateTimeFormat.forPattern(pattern);
        DateTime startDate;

        try {
            startDate =  fmt.parseDateTime(canBeDate);
            if (Integer.toString(startDate.getYear()).length()> 4) {
                // we are considering a length of over 4 in the year as an error, so 999 and 1000,2012, etc are OK but 20121 is not OK
                throw new SessionInternalError();
            }
        }
        catch (Exception e) {
            String [] errors = new String[] {errorString};
            throw new SessionInternalError("Unparsable Date", errors);
        }

        return startDate;
    }

}
