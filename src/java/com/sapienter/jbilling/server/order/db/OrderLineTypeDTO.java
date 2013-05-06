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
package com.sapienter.jbilling.server.order.db;


import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.sapienter.jbilling.server.util.Constants;
import com.sapienter.jbilling.server.util.db.AbstractDescription;


@Entity
@Table(name="order_line_type")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class OrderLineTypeDTO extends AbstractDescription implements java.io.Serializable {

    private int id;
    private Integer editable;
    private Set<OrderLineDTO> orderLineDTOs = new HashSet<OrderLineDTO>(0);

    public OrderLineTypeDTO() {
    }

    public OrderLineTypeDTO(int id, Integer editable) {
        this.id = id;
        this.editable = editable;
    }

    public OrderLineTypeDTO(int id, Integer editable, Set<OrderLineDTO> orderLineDTOs) {
       this.id = id;
       this.editable = editable;
       this.orderLineDTOs = orderLineDTOs;
    }
   
    @Id 
    @Column(name="id", unique=true, nullable=false)
    public int getId() {
        return this.id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    @Column(name="editable", nullable=false)
    public Integer getEditable() {
        return this.editable;
    }
    
    public void setEditable(Integer editable) {
        this.editable = editable;
    }
    
    @OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="orderLineType")
    public Set<OrderLineDTO> getOrderLines() {
        return this.orderLineDTOs;
    }
    
    public void setOrderLines(Set<OrderLineDTO> orderLineDTOs) {
        this.orderLineDTOs = orderLineDTOs;
    }

    @Transient
    protected String getTable() {
        return Constants.TABLE_ORDER_LINE_TYPE;
    }

    @Transient
    public String getTitle(Integer languageId) {
        return getDescription(languageId, "description");
    }


}


