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

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Abstract class for status classes. The get/setId() methods maps to
 * the status_value, instead of the primary key. Allows use of status
 * constants as the id.
 */
@Entity
@Table(name = "generic_status")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
    name="dtype",
    discriminatorType = DiscriminatorType.STRING
)
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public abstract class AbstractGenericStatus extends AbstractDescription {

    protected int id;
    protected Integer statusValue;

    @Id 
    @Column(name="id", unique=true, nullable=false)
    public Integer getPrimaryKey() {
        return id;
    }
    
    public void setPrimaryKey(Integer id) {
        this.id = id;
    }

    @Column(name="status_value", unique=true, nullable=false)
    public int getId() {
        return statusValue;
    }
    
    public void setId(int statusValue) {
        this.statusValue = statusValue;
    }
}
