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

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;

import com.sapienter.jbilling.server.util.Constants;
import com.sapienter.jbilling.server.util.db.AbstractDAS;

public class OrderPeriodDAS extends AbstractDAS<OrderPeriodDTO> {
	
	public OrderPeriodDTO findOrderPeriod(Integer entityId, Integer value, Integer unitId) {
		
        final String hql = "select p from OrderPeriodDTO p where " +
        		"p.company.id=:entityId and p.periodUnit.id=:unitId and p.value=:value";

        Query query = getSession().createQuery(hql);
        query.setParameter("entityId", entityId);
        query.setParameter("unitId", unitId);
        query.setParameter("value", value);

        return (OrderPeriodDTO) query.uniqueResult();
		
	}

    /**
    * Returns any orderPeriod distinct to 'ONCE'
    *
    * @return a period
    */
   @SuppressWarnings("unchecked")
   public OrderPeriodDTO findRecurringPeriod() {
       Criteria criteria = getSession().createCriteria(OrderPeriodDTO.class)
               .add(Restrictions.ne("id", Constants.ORDER_PERIOD_ONCE))
               .setMaxResults(1);

       return findFirst(criteria);
   }
}
