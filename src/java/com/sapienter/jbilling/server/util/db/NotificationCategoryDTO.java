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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.sapienter.jbilling.server.notification.db.NotificationMessageTypeDTO;
import com.sapienter.jbilling.server.util.Constants;

@Entity
@TableGenerator(
    name = "notification_category_GEN",
            table = "jbilling_seqs",
            pkColumnName = "name",
            valueColumnName = "next_id",
            pkColumnValue = "notification_category",
            allocationSize = 10)
@Table(name="notification_category")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class NotificationCategoryDTO extends AbstractDescription implements java.io.Serializable {

    private int id;
    private Set<NotificationMessageTypeDTO> messageTypes = new HashSet<NotificationMessageTypeDTO>(0);

    public NotificationCategoryDTO() {
    }

    // for stubs
    public NotificationCategoryDTO(Integer id) {
        this.id = id;
    }
    
    public NotificationCategoryDTO(Integer id, Set<NotificationMessageTypeDTO> messageTypes) {
        this.id = id;
        this.messageTypes= messageTypes;
    }

    @Transient
    protected String getTable() {
        return Constants.TABLE_NOTIFICATION_CATEGORY;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "notification_category_GEN")
    @Column(name="id", unique=true, nullable=false)
    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "category")
	public Set<NotificationMessageTypeDTO> getMessageTypes() {
		return messageTypes;
	}

	public void setMessageTypes(Set<NotificationMessageTypeDTO> messageTypes) {
		this.messageTypes = messageTypes;
	}



}


