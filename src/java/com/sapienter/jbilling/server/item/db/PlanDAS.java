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

import com.sapienter.jbilling.server.user.db.CustomerDTO;
import com.sapienter.jbilling.server.util.db.AbstractDAS;
import org.hibernate.Query;

import java.util.List;

/**
 * @author Brian Cowdery
 * @since 30-08-2010
 */
public class PlanDAS extends AbstractDAS<PlanDTO> {

    /**
     * Fetch a list of all customers that have subscribed to the given plan
     * by adding the "plan subscription" item to a recurring order.
     *
     * @param planId id of plan
     * @return list of customers subscribed to the plan, empty if none
     */
    @SuppressWarnings("unchecked")
    public List<CustomerDTO> findCustomersByPlan(Integer planId) {
        Query query = getSession().getNamedQuery("CustomerDTO.findCustomersByPlan");
        query.setParameter("plan_id", planId);

        return query.list();
    }

    /**
     * Returns true if the customer is subscribed to to the given plan id.
     *
     * @param userId user id of the customer
     * @param planId plan id
     * @return true if customer is subscribed to the plan, false if not.
     */
    public boolean isSubscribed(Integer userId, Integer planId) {
        Query query = getSession().getNamedQuery("PlanDTO.isSubscribed");
        query.setParameter("user_id", userId);
        query.setParameter("plan_id", planId);

        return !query.list().isEmpty();

    }

    /**
     * Fetch all plans for the given plan subscription item id.
     *
     * @param planItemId plan subscription item id
     * @return list of plans, empty if none found
     */
    @SuppressWarnings("unchecked")
    public List<PlanDTO> findByPlanSubscriptionItem(Integer planItemId) {
        Query query = getSession().getNamedQuery("PlanDTO.findByPlanItem");
        query.setParameter("plan_item_id", planItemId);

        return query.list();
    }

    /**
     * Fetch all plans that affect the pricing of the given item id, or include
     * the item in a bundle.
     *
     * @param affectedItemId affected item id
     * @return list of plans, empty if none found
     */
    @SuppressWarnings("unchecked")
    public List<PlanDTO> findByAffectedItem(Integer affectedItemId) {
        Query query = getSession().getNamedQuery("PlanDTO.findByAffectedItem");
        query.setParameter("affected_item_id", affectedItemId);

        return query.list();
    }

    /**
     * Fetch all plans for the given entity (company) id.
     *
     * @param entityId entity id
     * @return list of plans, empty if none found
     */
    @SuppressWarnings("unchecked")
    public List<PlanDTO> findAll(Integer entityId) {
        Query query = getSession().getNamedQuery("PlanDTO.findAllByEntity");
        query.setParameter("entity_id", entityId);

        return query.list();
    }
}
