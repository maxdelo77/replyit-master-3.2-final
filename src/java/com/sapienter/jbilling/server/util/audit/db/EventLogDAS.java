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
package com.sapienter.jbilling.server.util.audit.db;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.server.util.audit.EventLogger;
import com.sapienter.jbilling.server.util.db.AbstractDAS;

public class EventLogDAS extends AbstractDAS<EventLogDTO> {
    
    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(EventLogDAS.class));
    
    // QUERIES
    private static final String findLastTransition =
        "SELECT max(id) from EventLogDTO" +
        " WHERE eventLogModule.id = " + EventLogger.MODULE_WEBSERVICES +
        " AND eventLogMessage.id = " + EventLogger.USER_TRANSITIONS_LIST +
        " AND company.id = :entity";

    public Integer getLastTransitionEvent(Integer entityId) {
        Query query = getSession().createQuery(findLastTransition);
        query.setParameter("entity", entityId);
        Integer id = (Integer) query.uniqueResult();
        if (id == null) {
            LOG.warn("Can not find max value.");
            // it means that this is the very first time the web service
            // method is called with 'null,null'. Return all then.
            return 0;
        } 
        EventLogDTO latest = find(id);
        return latest.getOldNum();
    }

    public List<EventLogDTO> getEventsByAffectedUser(Integer userId) {
        Criteria criteria = getSession().createCriteria(EventLogDTO.class)
                .add(Restrictions.eq("affectedUser.id", userId))
                .addOrder(Order.desc("createDatetime"));

        return criteria.list();
    }
}
