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

package com.sapienter.jbilling.server.item.event;

import com.sapienter.jbilling.server.item.db.PlanDTO;
import com.sapienter.jbilling.server.system.event.Event;

public abstract class AbstractPlanEvent implements Event {

    private PlanDTO plan;
    private Integer entityId;

    public AbstractPlanEvent(PlanDTO plan) {
        this.plan = plan;
        entityId = plan.getItem().getEntityId();
    }

    public PlanDTO getPlan() {
        return plan;
    }

    public Integer getEntityId() {
        return entityId;
    }

    @Override
    public String toString() {
        return "Event: " + getName() + " planId: " + plan.getId() + 
                " entityId: " + entityId;
    }
}
