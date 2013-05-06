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


import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;

public abstract class JBCrypto {

    protected static final Charset UTF8 = Charset.forName("UTF-8");
    public static final String PROP_DIGEST_ALL_PASSWORDS = "password_encrypt_all";
    public static final int MIN_UNDIGESTED_ROLE = CommonConstants.TYPE_PARTNER;
    protected boolean useHexForBinary = true;

    public abstract String encrypt(String text);

    public abstract String decrypt(String crypted);

    public void setUseHexForBinary(boolean flag) {
        useHexForBinary = flag;
    }

    public static JBCrypto getPasswordCrypto(Integer role) {
        boolean digestAll = Boolean.parseBoolean(
                Util.getSysProp(PROP_DIGEST_ALL_PASSWORDS));

        return (digestAll || role == null || role < MIN_UNDIGESTED_ROLE) ? ONE_WAY : DUMMY;
    }

    public String digest(String input) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(
                    "MD5 digest is expected to be available :" + e);
        }

        byte[] hash = md5.digest(input.getBytes(UTF8));
        return useHexForBinary ? com.sapienter.jbilling.server.util.Util.binaryToString(hash) : 
                new String(Base64.encodeBase64(hash));
    }

    public static JBCrypto DUMMY = new JBCrypto() {

        public String encrypt(String text) {
            return text;
        }

        public String decrypt(String crypted) {
            return crypted;
        }
    };
    
    private static JBCrypto ONE_WAY = new JBCrypto() {

        public String encrypt(String text) {
            return digest(text);
        }

        public String decrypt(String crypted) {
            throw new UnsupportedOperationException("I am one way digets only");
        }
    };
}
