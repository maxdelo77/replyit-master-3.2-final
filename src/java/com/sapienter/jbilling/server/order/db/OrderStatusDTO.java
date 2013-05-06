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
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import com.sapienter.jbilling.server.util.Constants;
import com.sapienter.jbilling.server.util.db.AbstractGenericStatus;


@Entity
@DiscriminatorValue("order_status")
public class OrderStatusDTO  extends AbstractGenericStatus implements java.io.Serializable {


     private Set<OrderDTO> orderDTOs = new HashSet<OrderDTO>(0);

    public OrderStatusDTO() {
    }

    
    public OrderStatusDTO(int statusValue) {
        this.statusValue = statusValue;
    }
    public OrderStatusDTO(int statusValue, Set<OrderDTO> orderDTOs) {
       this.statusValue = statusValue;
       this.orderDTOs = orderDTOs;
    }

    @Transient
    protected String getTable() {
        return Constants.TABLE_ORDER_STATUS;
    }

@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="orderStatus")
    public Set<OrderDTO> getPurchaseOrders() {
        return this.orderDTOs;
    }
    
    public void setPurchaseOrders(Set<OrderDTO> orderDTOs) {
        this.orderDTOs = orderDTOs;
    }
}


