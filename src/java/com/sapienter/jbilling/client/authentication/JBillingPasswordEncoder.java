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

package com.sapienter.jbilling.client.authentication;

import com.sapienter.jbilling.common.Constants;
import com.sapienter.jbilling.common.JBCrypto;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.encoding.PasswordEncoder;

/**
 * Implementation of the Spring Security {@link PasswordEncoder} using jBilling's own
 * cryptology algorithm.
 *
 * @author Brian Cowdery
 * @since 07-10-2010
 */
public class JBillingPasswordEncoder implements PasswordEncoder {

    public JBillingPasswordEncoder() {
    }

    /**
     * Encodes a password using jBillings own cryptology algorithm. This implementation does
     * not support the use of a salt source. Given salt values will be ignored and will not
     * change the outcome of the encoded password.
     *
     * @param password password to encode
     * @param saltSource company user details
     * @return encoded password
     * @throws DataAccessException
     */
    public String encodePassword(String password, Object saltSource) throws DataAccessException {
    	Integer mainRoleId = null;
    	if (saltSource instanceof CompanyUserDetails) {
    		CompanyUserDetails companyUserDetails = (CompanyUserDetails) saltSource;
    		mainRoleId = companyUserDetails.getMainRoleId();
    	}
        JBCrypto cipher = JBCrypto.getPasswordCrypto(mainRoleId);
        return cipher.encrypt(password);
    }

    /**
     * Returns true if the 2 given encoded passwords match.
     *
     * @param encPass encoded password from stored user
     * @param rawPass plain-text password from authentication form
     * @param saltSource company user details
     * @return true if passwords match, false if not
     * @throws DataAccessException
     */
    public boolean isPasswordValid(String encPass, String rawPass, Object saltSource) throws DataAccessException {        
        return encPass.equals(encodePassword(rawPass, saltSource));
    }
}
