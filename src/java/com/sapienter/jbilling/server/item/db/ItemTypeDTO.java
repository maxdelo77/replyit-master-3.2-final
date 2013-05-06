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
package com.sapienter.jbilling.server.item.db;


import java.io.Serializable;
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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
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
        name = "item_type_GEN",
        table = "jbilling_seqs",
        pkColumnName = "name",
        valueColumnName = "next_id",
        pkColumnValue = "item_type",
        allocationSize = 100
)
@Table(name = "item_type")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ItemTypeDTO extends AbstractDescription implements Serializable {

    private int id;
    private CompanyDTO entity;
    private String description;
    private int orderLineTypeId;
    private boolean internal;
    private Set<ItemDTO> items = new HashSet<ItemDTO>(0);
    private Set<ItemDTO> excludedItems = new HashSet<ItemDTO>();
    private int versionNum;

    public ItemTypeDTO() {
    }

    public ItemTypeDTO(int id) {
        this.id = id;
    }

    public ItemTypeDTO(int id, CompanyDTO entity, int orderLineTypeId) {
        this.id = id;
        this.entity = entity;
        this.orderLineTypeId = orderLineTypeId;
    }

    public ItemTypeDTO(int id, CompanyDTO entity, String description, int orderLineTypeId, Set<ItemDTO> items) {
        this.id = id;
        this.entity = entity;
        this.description = description;
        this.orderLineTypeId = orderLineTypeId;
        this.items = items;
    }

    @Transient
    protected String getTable() {
        return Constants.TABLE_ITEM_TYPE;
    }

    @Id @GeneratedValue(strategy = GenerationType.TABLE, generator = "item_type_GEN")
    @Column(name = "id", unique = true, nullable = false)
    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entity_id", nullable = false)
    public CompanyDTO getEntity() {
        return this.entity;
    }

    public void setEntity(CompanyDTO entity) {
        this.entity = entity;
    }

    @Column(name = "description", length = 100)
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "order_line_type_id", nullable = false)
    public int getOrderLineTypeId() {
        return this.orderLineTypeId;
    }

    public void setOrderLineTypeId(int orderLineTypeId) {
        this.orderLineTypeId = orderLineTypeId;
    }

    @Column(name = "internal", nullable = false)
    public boolean isInternal() {
        return internal;
    }

    public void setInternal(boolean internal) {
        this.internal = internal;
    }

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinTable(name = "item_type_map",
               joinColumns = {@JoinColumn(name = "type_id", updatable = false)},
               inverseJoinColumns = {@JoinColumn(name = "item_id", updatable = false)}
    )
    public Set<ItemDTO> getItems() {
        return this.items;
    }

    public void setItems(Set<ItemDTO> items) {
        this.items = items;
    }

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinTable(name = "item_type_exclude_map",
               joinColumns = {@JoinColumn(name = "type_id", updatable = false)},
               inverseJoinColumns = {@JoinColumn(name = "item_id", updatable = false)}
    )
    public Set<ItemDTO> getExcludedItems() {
        return excludedItems;
    }

    public void setExcludedItems(Set<ItemDTO> excludedItems) {
        this.excludedItems = excludedItems;
    }

    @Version
    @Column(name = "OPTLOCK")
    public int getVersionNum() {
        return versionNum;
    }

    public void setVersionNum(int versionNum) {
        this.versionNum = versionNum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ItemTypeDTO that = (ItemTypeDTO) o;

        if (id != that.id) return false;
        if (orderLineTypeId != that.orderLineTypeId) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (entity != null ? !entity.equals(that.entity) : that.entity != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (entity != null ? entity.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + orderLineTypeId;
        return result;
    }

    @Override
    public String toString() {
        return "ItemTypeDTO{"
               + "id=" + id
               + ", orderLineTypeId=" + orderLineTypeId
               + ", description='" + description + '\''
               + '}';
    }
}


