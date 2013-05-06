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
@Table(name="order_billing_type")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class OrderBillingTypeDTO extends AbstractDescription implements java.io.Serializable {

    private int id;
    private Set<OrderDTO> orderDTOs = new HashSet<OrderDTO>(0);

    public OrderBillingTypeDTO() {
    }
    
    public OrderBillingTypeDTO(int id) {
        this.id = id;
    }

    public OrderBillingTypeDTO(int id, Set<OrderDTO> orderDTOs) {
       this.id = id;
       this.orderDTOs = orderDTOs;
    }

    @Transient
    public String getTable() {
        return Constants.TABLE_ORDER_BILLING_TYPE;
    }
    
    @Id 
    @Column(name="id", unique=true, nullable=false)
    public int getId() {
        return this.id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    @OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="orderBillingType")
    public Set<OrderDTO> getPurchaseOrders() {
        return this.orderDTOs;
    }
    
    public void setPurchaseOrders(Set<OrderDTO> orderDTOs) {
        this.orderDTOs = orderDTOs;
    }
    
    public String toString() {
        return " OrderBillingType=" + id;
    }
}


