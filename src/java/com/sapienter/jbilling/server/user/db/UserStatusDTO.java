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
package com.sapienter.jbilling.server.user.db;


import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.persistence.DiscriminatorValue;

import com.sapienter.jbilling.server.process.db.AgeingEntityStepDTO;
import com.sapienter.jbilling.server.util.Constants;
import com.sapienter.jbilling.server.util.db.AbstractGenericStatus;

@Entity
@DiscriminatorValue("user_status")
public class UserStatusDTO extends AbstractGenericStatus implements java.io.Serializable {

     private int canLogin;
     private Set<AgeingEntityStepDTO> ageingEntitySteps = new HashSet<AgeingEntityStepDTO>(0);
     private Set<UserDTO> baseUsers = new HashSet<UserDTO>(0);

    public UserStatusDTO() {
    }

    public UserStatusDTO(Integer statusvalue) {
        this.statusValue = statusValue;
    }
    
    public UserStatusDTO(int statusValue, int canLogin) {
        this.statusValue = statusValue;
        this.canLogin = canLogin;
    }
    
    public UserStatusDTO(int statusValue, int canLogin, Set<AgeingEntityStepDTO> ageingEntitySteps, Set<UserDTO> baseUsers) {
       this.statusValue = statusValue;
       this.canLogin = canLogin;
       this.ageingEntitySteps = ageingEntitySteps;
       this.baseUsers = baseUsers;
    }
    
    @Transient
    protected String getTable() {
        return Constants.TABLE_USER_STATUS;
    }
    
    @Column(name="can_login", nullable=false)
    public int getCanLogin() {
        return this.canLogin;
    }
    
    public void setCanLogin(int canLogin) {
        this.canLogin = canLogin;
    }
@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="userStatus")
    public Set<AgeingEntityStepDTO> getAgeingEntitySteps() {
        return this.ageingEntitySteps;
    }
    
    public void setAgeingEntitySteps(Set<AgeingEntityStepDTO> ageingEntitySteps) {
        this.ageingEntitySteps = ageingEntitySteps;
    }
@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="userStatus")
    public Set<UserDTO> getBaseUsers() {
        return this.baseUsers;
    }
    
    public void setBaseUsers(Set<UserDTO> baseUsers) {
        this.baseUsers = baseUsers;
    }




}


