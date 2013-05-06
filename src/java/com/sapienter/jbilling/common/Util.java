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

import org.apache.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Client miscelaneous utility functions
 */
public class Util {
    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(Util.class));
    /**
     * Creates a date object with the given parameters only if they belong to a
     * valid day, so February 30th would be returning null.
     * @param year
     * @param month
     * @param day
     * @return null if the parameters are invalid, otherwise the date object
     */
    static public Date getDate(Integer year, Integer month, Integer day) {
        Date retValue = null;

        try {
            GregorianCalendar cal = new GregorianCalendar();
            cal.setLenient(false);
            cal.clear();
            cal.set(year, month -1, day);

            retValue = cal.getTime();
        } catch (Exception e) {

        }
        return retValue;
    }

    /**
     * Converts a string in the format yyyy-mm-dd to a Date.
     * If the string can't be converted, it returns null
     * @param str
     * @return
     */
    static public Date parseDate(String str) {
        if (str == null || str.length() < 8 || str.length() > 10) {
            return null;
        }

        if (str.charAt(4) != '-' || str.lastIndexOf('-') < 6 ||
                str.lastIndexOf('-') > 7) {
            return null;
        }

        try {
            int year = getYear(str);
            int month = getMonth(str);
            int day = getDay(str);

            return getDate(new Integer(year), new Integer(month),
                    new Integer(day));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Recives date in sql format yyyy-mm-dd and extracts the day
     * @param day
     * @return
     */
    static public int getDay(String str) throws SessionInternalError {
        // from the last '-' to the end
        try {
            return Integer.valueOf(str.substring(str.lastIndexOf('-') + 1));
        } catch (NumberFormatException e) {
            throw new SessionInternalError("Cant get the day from " + str);
        }
    }

    static public int getMonth(String str) throws SessionInternalError {
        // from the first '-' to the second '-'
        try {
            return Integer.valueOf(str.substring(str.indexOf('-') + 1, str.lastIndexOf('-')));
        } catch (NumberFormatException e) {
            throw new SessionInternalError("Cant get the month from " + str);
        }

    }

    static public int getYear(String str) throws SessionInternalError {
        // from the begining to the first '-'
        try {
            return Integer.valueOf(str.substring(0, str.indexOf('-')));
        } catch (NumberFormatException e) {
            throw new SessionInternalError("Cant get the year from " + str);
        }
    }

    /**
     * Compares to dates, contemplating the posibility of null values.
     * If both are null, they are consider equal.
     * @param date1
     * @param date2
     * @return true if equal, otherwise false.
     */

    static public boolean equal(Date date1, Date date2) {
        boolean retValue;
        if (date1 == null && date2 == null) {
            retValue = true;
        } else if ((date1 == null && date2 != null) ||
                (date1 != null && date2 == null)) {
            retValue = false;
        } else {
            retValue = (date1.compareTo(date2) == 0);
        }

        return retValue;
    }

    static public Date truncateDate(Date arg) {
        if (arg == null) return null;
        GregorianCalendar cal = new GregorianCalendar();

        cal.setTime(arg);
        cal.set(GregorianCalendar.HOUR_OF_DAY, 0);
        cal.set(GregorianCalendar.MINUTE, 0);
        cal.set(GregorianCalendar.SECOND, 0);
        cal.set(GregorianCalendar.MILLISECOND, 0);

        return cal.getTime();
    }

    /**
     * Takes a date and returns it as String with the format 'yyyy-mm-dd'
     * @param date
     * @return
     */
    static public String parseDate(Date date) {
        GregorianCalendar cal = new GregorianCalendar();

        cal.setTime(date);
        return cal.get(GregorianCalendar.YEAR) + "-" +
                (cal.get(GregorianCalendar.MONTH) + 1) + "-" +
                cal.get(GregorianCalendar.DATE);
    }

    /**
     * Checks if the passed string can be converted into a date.
     * If the string can't be converted, it returns false
     * @param str : String to be parsed
     * @param df  : Date Format in which parsing would be tried
     * @return true: if str can be converted to date, false otherwise
     */
    static public boolean canParseDate(String str, SimpleDateFormat df) {
        try {
            LOG.debug("Trying to parse "+str+" to date in format "+df);
            df.parse(str);
        } catch (ParseException e) {
            LOG.debug("Cannot parse the given "+str+ " into a date for format "+df);
            // Eat the Exception & Leave
            return false;
        }
        return true;
    }

    /**
     * Returns the payment method for the given credit card. If this credit card
     * has been obscured (by the {@link com.sapienter.jbilling.server.payment.tasks.SaveCreditCardExternallyTask} plug-in)
     * then the payment type cannot detected and this method will return PAYMENT_METHOD_GATEWAY_KEY.
     *
     * @param creditCardNumber credit card number to parse
     * @return payment method
     */
    static public Integer getPaymentMethod(String creditCardNumber) {
        Integer type = null;

        if (creditCardNumber.length() > 0) {
            switch (creditCardNumber.charAt(0)) {
                case '4':
                    if (creditCardNumber.startsWith("4026") ||
                            creditCardNumber.startsWith("417500") ||
                            creditCardNumber.startsWith("4508") ||
                            creditCardNumber.startsWith("4844") ||
                            creditCardNumber.startsWith("4913") ||
                            creditCardNumber.startsWith("4917")) {
                        type = Constants.PAYMENT_METHOD_VISA_ELECTRON;
                    } else {
                        type = Constants.PAYMENT_METHOD_VISA;
                    }
                    break;
                case '5':
                    switch (creditCardNumber.charAt(1)) {
                        case '1':
                        case '2':
                        case '3':
                        case '4': //DINERS for US & Canada = MASTERCARD International
                        case '5': //DINERS for US & Canada = MASTERCARD International
                            type = Constants.PAYMENT_METHOD_MASTERCARD;
                            break;
                        case '0':
                            if (creditCardNumber.substring(2, 4).equals("18") ||
                                    creditCardNumber.substring(2, 4).equals("20") ||
                                    creditCardNumber.substring(2, 4).equals("38")) {
                                type = Constants.PAYMENT_METHOD_MAESTRO;
                            }
                            break;
                    }
                    break;
                case '3':
                    // both diners and american express start with a 3
                    if (creditCardNumber.charAt(1) == '7' || creditCardNumber.charAt(1) == '4') {
                        type = Constants.PAYMENT_METHOD_AMEX;
                    } else if (creditCardNumber.charAt(1) == '5') {
                        try {
                            int startNumber = Integer.valueOf(creditCardNumber.substring(0, 4));
                            if (startNumber >= 3528 && startNumber <= 3589) {
                                type = Constants.PAYMENT_METHOD_JCB;
                            }
                        } catch (Exception ex) {
                            //do nothing
                        }
                    } else if (creditCardNumber.charAt(1) == '0' || creditCardNumber.charAt(1) == '6') {
                        type = Constants.PAYMENT_METHOD_DINERS;
                    }
                    break;
                case '6':
                    if (creditCardNumber.startsWith("637") ||
                            creditCardNumber.startsWith("638") ||
                            creditCardNumber.startsWith("639")) {
                        type = Constants.PAYMENT_METHOD_INSTAL_PAYMENT;
                    } else if (creditCardNumber.startsWith("6304") || // also LASER card
                            creditCardNumber.startsWith("6759") ||
                            creditCardNumber.startsWith("6761") ||
                            creditCardNumber.startsWith("6762") ||
                            creditCardNumber.startsWith("6763")) {
                        type = Constants.PAYMENT_METHOD_MAESTRO;
                    } else if (creditCardNumber.startsWith("6706") ||
                            creditCardNumber.startsWith("6771") ||
                            creditCardNumber.startsWith("6709")) {
                        type = Constants.PAYMENT_METHOD_LASER;
                    } else if (creditCardNumber.startsWith("6011") ||
                            creditCardNumber.startsWith("65") ||
                            creditCardNumber.startsWith("644") ||
                            creditCardNumber.startsWith("645") ||
                            creditCardNumber.startsWith("646") ||
                            creditCardNumber.startsWith("647") ||
                            creditCardNumber.startsWith("648") ||
                            creditCardNumber.startsWith("649") ||
                            creditCardNumber.startsWith("622")) {
                        if (creditCardNumber.startsWith("622")) {
                            try {
                                int startNumber = Integer.valueOf(creditCardNumber.substring(0, 6));
                                if (startNumber >= 622126 && startNumber <=622925) {
                                    type = Constants.PAYMENT_METHOD_DISCOVERY;
                                }
                            } catch (Exception ex) {
                                //do nothing
                            }
                        } else {
                            type = Constants.PAYMENT_METHOD_DISCOVERY;
                        }
                    }
                    break;
            }
        }
        /*
            This isn't 100% accurate as obscured credit card numbers may not always mean that a gateway key
            is present. We should be checking CreditCardDTO to ensure that gatewayKey is not null when an
            obscured credit card number is encountered.
         */
        if (creditCardNumber.contains("*"))
            type = Constants.PAYMENT_METHOD_GATEWAY_KEY;

        return type;
    }

    static public String truncateString(String str, int length) {
        if (str == null) return null;
        String retValue;
        if (str.length() <= length) {
            retValue = str;
        } else {
            retValue = str.substring(0, length);
        }

        return retValue;
    }

    public static String getSysProp(String key) {
        try {
            return SystemProperties.getSystemProperties().get(key);
        } catch (Exception e) {
            LOG.error("Cannot read property '" + key + "' from " + SystemProperties.getPropertiesFile().getPath(), e);
            return null;
        }
    }

    /**
     * Gets a boolean system property. It returns true by default, and on any error.
     *
     * @param key boolean system property
     * @return boolean property value
     */
    public static boolean getSysPropBooleanTrue(String key) {
        try {
            return Boolean.parseBoolean(SystemProperties.getSystemProperties().get(key, "true"));
        } catch (Exception e) {
            LOG.error("Cannot read property '" + key + "' from " + SystemProperties.getPropertiesFile().getPath());
        }

        return true; // default if not found
    }

    /**
     * Credit Card Validate
     * Reference: http://www.ling.nwu.edu/~sburke/pub/luhn_lib.pl
     */
    public static boolean luhnCheck(String cardNumber) throws SessionInternalError {
        //just in case the card number is formated and may contain spaces
        cardNumber=getDigitsOnly(cardNumber);
        //mod 10 validation
        if (isLuhnNum(cardNumber)) {
            int no_digit = cardNumber.length();
            int oddoeven = no_digit & 1;

            int sum = 0;
            int digit = 0;
            int addend = 0;
            boolean timesTwo = false;
            for (int i = cardNumber.length() - 1; i >= 0; i--) {
                digit = Integer.parseInt(cardNumber.substring(i, i + 1));
                if (timesTwo) {
                    addend = digit * 2;
                    if (addend > 9) {
                        addend -= 9;
                    }
                } else {
                    addend = digit;
                }
                sum += addend;
                timesTwo = !timesTwo;
            }
            if (sum == 0) return false;
            if (sum % 10 == 0) return true;
        };
        return false;
    }

    private static String getDigitsOnly(String s) {
        StringBuffer digitsOnly = new StringBuffer();
        char c;
        for (int i = 0; i < s.length(); i++) {
            c = s.charAt(i);
            if (Character.isDigit(c)) {
                digitsOnly.append(c);
            }
        }
        return digitsOnly.toString();
    }

    private static boolean isLuhnNum(String argvalue) {
        if (argvalue.length() == 0) {
            return false;
        }
        for (int n = 0; n < argvalue.length(); n++) {
            char c = argvalue.charAt(n);
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

}
