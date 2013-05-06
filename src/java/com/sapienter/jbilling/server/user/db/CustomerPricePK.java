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

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

/**
 * CustomerPriceDTO composite primary key
 *
 * @author Brian Cowdery
 * @since 26-08-2010
 */
@Embeddable
public class CustomerPricePK implements Serializable {

    private PlanItemDTO planItem;
    private UserDTO baseUser;

    public CustomerPricePK() {
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "plan_item_id", nullable = false)
    public PlanItemDTO getPlanItem() {
        return planItem;
    }

    public void setPlanItem(PlanItemDTO planItem) {
        this.planItem = planItem;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    public UserDTO getBaseUser() {
        return baseUser;
    }

    public void setBaseUser(UserDTO baseUser) {
        this.baseUser = baseUser;

    }
}
