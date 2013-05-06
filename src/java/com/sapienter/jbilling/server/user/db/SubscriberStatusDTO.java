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
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import com.sapienter.jbilling.server.util.Constants;
import com.sapienter.jbilling.server.util.db.AbstractGenericStatus;

@Entity
@DiscriminatorValue("subscriber_status")
public class SubscriberStatusDTO extends AbstractGenericStatus implements java.io.Serializable {

     private Set<UserDTO> baseUsers = new HashSet<UserDTO>(0);

    public SubscriberStatusDTO() {
    }

    
    public SubscriberStatusDTO(int statusValue) {
        this.statusValue = statusValue;
    }
    public SubscriberStatusDTO(int statusValue, Set<UserDTO> baseUsers) {
       this.statusValue = statusValue;
       this.baseUsers = baseUsers;
    }

    @Transient
    protected String getTable() {
        return Constants.TABLE_USER_SUBSCRIBER_STATUS;
    }
   
@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="subscriberStatus")
    public Set<UserDTO> getBaseUsers() {
        return this.baseUsers;
    }
    
    public void setBaseUsers(Set<UserDTO> baseUsers) {
        this.baseUsers = baseUsers;
    }




}


