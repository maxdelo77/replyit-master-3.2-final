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
package com.sapienter.jbilling.server.user.contact.db;


import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.sapienter.jbilling.server.user.db.CompanyDTO;
import com.sapienter.jbilling.server.util.Constants;
import com.sapienter.jbilling.server.util.db.AbstractDescription;

@Entity
@TableGenerator(
        name = "contact_type_GEN",
        table = "jbilling_seqs",
        pkColumnName = "name",
        valueColumnName = "next_id",
        pkColumnValue = "contact_type",
        allocationSize = 10
)
@Table(name = "contact_type")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ContactTypeDTO extends AbstractDescription implements java.io.Serializable {

    private int id;
    private CompanyDTO entity;
    private Integer isPrimary;
    private Set<ContactMapDTO> contactMaps = new HashSet<ContactMapDTO>(0);
    private Integer versionNum;

    public ContactTypeDTO() {
    }

    public ContactTypeDTO(int id) {
        this.id = id;
    }

    public ContactTypeDTO(int id, CompanyDTO entity, Integer isPrimary, Set<ContactMapDTO> contactMaps) {
        this.id = id;
        this.entity = entity;
        this.isPrimary = isPrimary;
        this.contactMaps = contactMaps;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "contact_type_GEN")
    @Column(name = "id", unique = true, nullable = false)
    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entity_id")
    public CompanyDTO getEntity() {
        return this.entity;
    }

    public void setEntity(CompanyDTO entity) {
        this.entity = entity;
    }

    @Column(name = "is_primary")
    public Integer getIsPrimary() {
        return this.isPrimary;
    }

    public void setIsPrimary(Integer isPrimary) {
        this.isPrimary = isPrimary;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "contactType")
    public Set<ContactMapDTO> getContactMaps() {
        return this.contactMaps;
    }

    public void setContactMaps(Set<ContactMapDTO> contactMaps) {
        this.contactMaps = contactMaps;
    }

    @Version
    @Column(name = "OPTLOCK")
    public Integer getVersionNum() {
        return versionNum;
    }

    public void setVersionNum(Integer versionNum) {
        this.versionNum = versionNum;
    }

    @Transient
    protected String getTable() {
        return Constants.TABLE_CONTACT_TYPE;
    }

    @Override
    public String toString() {
        return "ContactTypeDTO{"
               + "id=" + id
               + ", entityId=" + (entity != null ? entity.getId() : null)
               + ", isPrimary=" + isPrimary
               + ", description=" + getDescription()
               + '}';
    }
}


