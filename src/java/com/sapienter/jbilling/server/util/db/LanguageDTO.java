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
package com.sapienter.jbilling.server.util.db;


import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.sapienter.jbilling.server.notification.db.NotificationMessageDTO;
import com.sapienter.jbilling.server.user.db.CompanyDTO;
import com.sapienter.jbilling.server.user.db.UserDTO;

@Entity
@Table(name="language")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class LanguageDTO  implements java.io.Serializable {
     public static final int ENGLISH_LANGUAGE_ID = 1;

     private int id;
     private String code;
     private String description;
     private Set<NotificationMessageDTO> notificationMessages = new HashSet<NotificationMessageDTO>(0);
     private Set<CompanyDTO> entities = new HashSet<CompanyDTO>(0);
     private Set<UserDTO> baseUsers = new HashSet<UserDTO>(0);

    public LanguageDTO() {
    }

    public LanguageDTO(int id) {
        this.id = id;
    }
    
    public LanguageDTO(int id, String code, String description) {
        this.id = id;
        this.code = code;
        this.description = description;
    }
    public LanguageDTO(int id, String code, String description, Set<NotificationMessageDTO> notificationMessages, Set<CompanyDTO> entities, Set<UserDTO> baseUsers) {
       this.id = id;
       this.code = code;
       this.description = description;
       this.notificationMessages = notificationMessages;
       this.entities = entities;
       this.baseUsers = baseUsers;
    }
   
     @Id 
    
    @Column(name="id", unique=true, nullable=false)
    public int getId() {
        return this.id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    @Column(name="code", nullable=false, length=2)
    public String getCode() {
        return this.code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    @Column(name="description", nullable=false, length=50)
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="language")
    public Set<NotificationMessageDTO> getNotificationMessages() {
        return this.notificationMessages;
    }
    
    public void setNotificationMessages(Set<NotificationMessageDTO> notificationMessages) {
        this.notificationMessages = notificationMessages;
    }
@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="language")
    public Set<CompanyDTO> getEntities() {
        return this.entities;
    }
    
    public void setEntities(Set<CompanyDTO> entities) {
        this.entities = entities;
    }
@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="language")
    public Set<UserDTO> getBaseUsers() {
        return this.baseUsers;
    }
    
    public void setBaseUsers(Set<UserDTO> baseUsers) {
        this.baseUsers = baseUsers;
    }
}


