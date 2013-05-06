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
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.sapienter.jbilling.server.util.db.JbillingTable;

import java.math.BigDecimal;

@Entity
@TableGenerator(
        name="preference_GEN",
        table="jbilling_seqs",
        pkColumnName = "name",
        valueColumnName = "next_id",
        pkColumnValue="preference",
        allocationSize = 10
)
@Table(name="preference")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class PreferenceDTO implements java.io.Serializable {

    private int id;
    private JbillingTable jbillingTable;
    private PreferenceTypeDTO preferenceType;
    private int foreignId;
    private String value;

    public PreferenceDTO() {
    }

    public PreferenceDTO(int id, JbillingTable jbillingTable, int foreignId) {
        this.id = id;
        this.jbillingTable = jbillingTable;
        this.foreignId = foreignId;
    }

    public PreferenceDTO(int id, JbillingTable jbillingTable, PreferenceTypeDTO preferenceType, int foreignId, String value) {
        this.id = id;
        this.jbillingTable = jbillingTable;
        this.preferenceType = preferenceType;
        this.foreignId = foreignId;
        this.value = value;
    }

    @Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator="preference_GEN")
    @Column(name="id", unique=true, nullable=false)
    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="table_id", nullable=false)
    public JbillingTable getJbillingTable() {
        return this.jbillingTable;
    }

    public void setJbillingTable(JbillingTable jbillingTable) {
        this.jbillingTable = jbillingTable;
    }

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="type_id")
    public PreferenceTypeDTO getPreferenceType() {
        return this.preferenceType;
    }

    public void setPreferenceType(PreferenceTypeDTO preferenceType) {
        this.preferenceType = preferenceType;
    }

    @Column(name="foreign_id", nullable=false)
    public int getForeignId() {
        return this.foreignId;
    }

    public void setForeignId(int foreignId) {
        this.foreignId = foreignId;
    }

    @Column(name="value", length=200)
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setValue(Integer value) {
        this.value = (value != null ? value.toString() : null);
    }

    public void setValue(BigDecimal value) {
        this.value = (value != null ? value.toString() : null);
    }

    @Transient
    public Integer getIntValue() {
        return value != null ? Integer.valueOf(value) : null;
    }

    @Transient
    public BigDecimal getFloatValue() {
        return value != null ? new BigDecimal(value) : null;
    }
}


