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

import com.sapienter.jbilling.server.util.db.AbstractDAS;
import org.hibernate.Query;

/**
 * @author Brian Cowdery
 * @since 30-08-2010
 */
public class PlanItemDAS extends AbstractDAS<PlanItemDTO> {

    private static final String CURRENCY_USAGE_FOR_ENTITY_SQL=
            "select count(*) from " +
            " plan_item pi, " +
            " item i, " +
            " plan_item_price_timeline pipt, " +
            " price_model pm " +
            " where " +
            "     pipt.plan_item_id = pi.id " +
            " and pipt.price_model_id = pm.id " +
            " and pi.item_id = i.id " +
            " and pm.currency_id = :currencyId " +
            " and i.entity_id = :entityId " +
            " and i.deleted = 0";

    public Long findPlanItemCountByCurrencyAndEntity(Integer currencyId, Integer entityId ) {
    	Query sqlQuery = getSession().createSQLQuery(CURRENCY_USAGE_FOR_ENTITY_SQL)
    		  .setParameter("currencyId", currencyId)
    		  .setParameter("entityId", entityId);
        Number count = (Number) sqlQuery.uniqueResult();
        return Long.valueOf(null == count ? 0L : count.longValue());
    }
	
}
