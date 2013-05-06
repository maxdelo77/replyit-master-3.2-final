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

import com.sapienter.jbilling.server.item.db.PlanItemDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityResult;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;

/**
 * Customer pricing mapping class. Provides a list of prices for each customer. New prices
 * are inserted when a customer subscribes to a configured plan.
 *
 * Due to bug HHH-3441 (http://opensource.atlassian.com/projects/hibernate/browse/HHH-3441) in Hibernate,
 * both the UserDTO and PlanItemDTO objects must be persisted BEFORE creating a new CustomerPriceDTO.
 *
 * @author Brian Cowdery
 * @since 26-08-2010
 */
@Entity
@Table(name = "customer_price")
@NamedQueries({
        @NamedQuery(name = "PlanItemDTO.find",
                    query = "select price "
                            + " from CustomerPriceDTO price "
                            + " where price.id.baseUser.id = :user_id"
                            + " and price.id.planItem.id = :plan_item_id"),

        @NamedQuery(name = "PlanItemDTO.findCustomerPriceByItem",
                    query = "select price.id.planItem "
                            + " from CustomerPriceDTO price "
                            + " where price.id.planItem.item.id = :item_id "
                            + " and price.id.baseUser.id = :user_id "
                            + " order by price.id.planItem.precedence, price.createDatetime desc"),

        @NamedQuery(name = "PlanItemDTO.findAllCustomerPrices",
                    query = "select price.id.planItem"
                            + " from CustomerPriceDTO price "
                            + " where price.id.baseUser.id = :user_id"),

        @NamedQuery(name = "PlanItemDTO.findAllCustomerSpecificPrices",
                    query = "select price.id.planItem"
                            + " from CustomerPriceDTO price "
                            + " where price.id.planItem.plan is null "
                            + " and price.id.baseUser.id = :user_id"),

        @NamedQuery(name = "CustomerPriceDTO.deletePrice",
                    query = "delete CustomerPriceDTO "
                            + " where id.planItem.id = :plan_item_id "
                            + " and id.baseUser.id = :user_id"),

        @NamedQuery(name = "CustomerPriceDTO.deletePricesByItems",
                    query = "delete CustomerPriceDTO "
                            + " where id.planItem.id in (:plan_item_ids)"),

        @NamedQuery(name = "CustomerPriceDTO.deletePriceByPlan",
                    query = "delete CustomerPriceDTO "
                            + " where id.baseUser.id = :user_id"
                            + " and id.planItem.id in ("                // postgresql has a strange syntax for delete
                            + "     select planItem.id "                // with join that is not supported by hibernate.
                            + "     from PlanItemDTO planItem "         // delete where id in (...) as a workaround
                            + "     where planItem.plan.id = :plan_id"
                            + ")")
})
// todo: cache config
public class CustomerPriceDTO implements Serializable {

    private CustomerPricePK id = new CustomerPricePK();
    private Date createDatetime = new Date();

    public CustomerPriceDTO() {
    }

    public CustomerPriceDTO(CustomerPricePK id) {
        this.id = id;
    }

    @Id
    public CustomerPricePK getId() {
        return id;
    }

    public void setId(CustomerPricePK id) {
        this.id = id;
    }

    @Transient
    public PlanItemDTO getPlanItem() {
        return id.getPlanItem();
    }

    public void setPlanItem(PlanItemDTO planItem) {
        id.setPlanItem(planItem);
    }

    @Transient
    public UserDTO getBaseUser() {
        return id.getBaseUser();
    }

    public void setBaseUser(UserDTO user) {
        id.setBaseUser(user);
    }

    @Transient
    public CustomerDTO getCustomer() {
        return (id.getBaseUser() != null ? id.getBaseUser().getCustomer() : null);
    }

    public void setCustomer(CustomerDTO customer) {
        if (customer != null)
            id.setBaseUser(customer.getBaseUser());
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_datetime", nullable = false)
    public Date getCreateDatetime() {
        return createDatetime;
    }

    public void setCreateDatetime(Date createDatetime) {
        this.createDatetime = createDatetime;
    }

    @Override
    public String toString() {
        return "CustomerPriceDTO{"
               + " userId=" + (getBaseUser() != null ? getBaseUser().getId() : null)
               + ", planItem=" + getPlanItem()
               + ", createDatetime=" + createDatetime
               + '}';
    }
}
