/*
 jBilling - The Enterprise Open Source Billing System
 Copyright (C) 2003-2011 Enterprise jBilling Software Ltd. and Emiliano Conde

 This file is part of jbilling.

 jbilling is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 jbilling is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with jbilling.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.sapienter.jbilling.client.authentication;

import com.sapienter.jbilling.client.authentication.model.EncryptedLicense;
import com.sapienter.jbilling.client.authentication.model.User;
import com.sapienter.jbilling.common.JBCrypto;
import com.sapienter.jbilling.common.Util;
import com.sapienter.jbilling.server.user.IUserSessionBean;
import com.sapienter.jbilling.server.user.UserBL;
import com.sapienter.jbilling.server.user.db.UserDTO;
import com.sapienter.jbilling.server.user.permisson.db.PermissionDTO;
import com.sapienter.jbilling.server.user.permisson.db.RoleDTO;
import com.sapienter.jbilling.server.util.Constants;
import com.sapienter.jbilling.server.util.PreferenceBL;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * AuthenticationUserService
 *
 * @author Brian Cowdery
 * @since 20/07/11
 */
public class AuthenticationUserService implements UserService {


    private IUserSessionBean userSession;

    public AuthenticationUserService() {
    }

    public void setUserSession(IUserSessionBean userSession) {
        this.userSession = userSession;
    }

    public User getUser(String username, Integer entityId) {
        UserBL bl = new UserBL(username, entityId);
        UserDTO dto = bl.getEntity();

        if (dto != null) {
            User user = new User();
            user.setId(dto.getId());
            user.setUsername(dto.getUserName());
            user.setPassword(dto.getPassword()); // hashed password
            user.setEnabled(dto.isEnabled());
            user.setAccountExpired(dto.isAccountExpired());
            user.setCredentialsExpired(dto.isPasswordExpired());
            //the account is considered locked out if the lockout password is set
            if(isLockoutEnforced(dto)){
                user.setAccountLocked(bl.isLockoutPasswordSet());
            }else{
                user.setAccountLocked(dto.isAccountLocked());
            }
            user.setLocale(bl.getLocale());
            user.setMainRoleId(bl.getMainRole());
            user.setCompanyId(dto.getCompany().getId());
            user.setCurrencyId(dto.getCurrency().getId());
            user.setLanguageId(dto.getLanguage().getId());

            return user;
        }

        return null;
    }

    public boolean isLockoutEnforced(UserDTO user) {
        String result = userSession.getEntityPreference(user.getEntity().getId(), Constants.PREFERENCE_FAILED_LOGINS_LOCKOUT);
        if (null != result && !result.trim().isEmpty()) {
            int allowedRetries = Integer.parseInt(result);
            return allowedRetries > 0;
        }
        return false;
    }

    public Collection<GrantedAuthority> getAuthorities(String username, Integer entityId) {
        UserBL bl = new UserBL(username, entityId);
        UserDTO user = bl.getEntity();

        if (user != null) {
            Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();

            for (PermissionDTO permission : bl.getPermissions()) {
                permission.initializeAuthority();
                authorities.add(permission);
            }

            for (RoleDTO role : user.getRoles()) {
                role.initializeAuthority();
                authorities.add(role);
            }

            return authorities;
        }

        return Collections.emptyList();
    }

    public EncryptedLicense getLicense(Integer entityId) {
        String licenseKey = Util.getSysProp("license_key");
        String licenseeName = Util.getSysProp("licensee");

        return new EncryptedLicense(licenseKey, licenseeName);
    }
}
