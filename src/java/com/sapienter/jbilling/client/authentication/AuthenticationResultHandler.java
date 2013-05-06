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

import com.sapienter.jbilling.server.user.IUserSessionBean;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;

/**
 * @author carevski
 * @since 10/24/12
 */
public class AuthenticationResultHandler {

    private IUserSessionBean userSession;

    public void setUserSession(IUserSessionBean userSession) {
        this.userSession = userSession;
    }

    public void loginSuccess(AuthenticationSuccessEvent event) {
        Authentication authentication = event.getAuthentication();
        update(authentication.getPrincipal(), true);
    }

    public void loginFailure(AbstractAuthenticationFailureEvent event) {
        Authentication authentication = event.getAuthentication();
        update(authentication.getPrincipal(), false);
    }

    private boolean update(Object details, boolean success){
        boolean doUpdate = false;
        String username = null;
        Integer entityId = null;

        if (details instanceof String) {
            String[] tokens = ((String) details).split(";");
            if (tokens.length < 2) {
                return false; //??
            }
            username = tokens[0];
            entityId = Integer.valueOf(tokens[1]);
            doUpdate = true;

        } else if (details instanceof CompanyUserDetails) {

            CompanyUserDetails companyDetails = (CompanyUserDetails) details;
            username = companyDetails.getPlainUsername();
            entityId = companyDetails.getCompanyId();
            doUpdate = true;

        }

        if (doUpdate) {
            if (success) {
                userSession.loginSuccess(username, entityId);
            } else {
                boolean locked = userSession.loginFailure(username, entityId);
            }
            return true;
        }

        return false;

    }

}
