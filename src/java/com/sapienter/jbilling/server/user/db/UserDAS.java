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

import java.util.List;

import com.sapienter.jbilling.server.util.db.CurrencyDTO;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.sapienter.jbilling.common.CommonConstants;
import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.server.user.UserDTOEx;
import com.sapienter.jbilling.server.util.db.AbstractDAS;

public class UserDAS extends AbstractDAS<UserDTO> {
    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(UserDAS.class));

     private static final String findInStatusSQL = 
         "SELECT a " + 
         "  FROM UserDTO a " + 
         " WHERE a.userStatus.id = :status " +
         "   AND a.company.id = :entity " +
         "   AND a.deleted = 0" ;

     private static final String findNotInStatusSQL =
         "SELECT a " +
         "  FROM UserDTO a " + 
         " WHERE a.userStatus.id <> :status " +
         "   AND a.company.id = :entity " +
         "   AND a.deleted = 0";

     private static final String findAgeingSQL = 
         "SELECT a " + 
         "  FROM UserDTO a " + 
         " WHERE a.userStatus.id > " + UserDTOEx.STATUS_ACTIVE +
         "   AND a.userStatus.id <> " + UserDTOEx.STATUS_DELETED +
         "   AND a.customer.excludeAging = 0 " +
         "   AND a.company.id = :entity " +
         "   AND a.deleted = 0";
     
     private static final String CURRENCY_USAGE_FOR_ENTITY_SQL =
             "SELECT count(*) " +
             "  FROM UserDTO a " +
             " WHERE a.currency.id = :currency " +
             "	  AND a.company.id = :entity "+
             "   AND a.deleted = 0";
     
     public Long findUserCountByCurrencyAndEntity(Integer currencyId, Integer entityId){
         Query query = getSession().createQuery(CURRENCY_USAGE_FOR_ENTITY_SQL);
         query.setParameter("currency", currencyId);
         query.setParameter("entity", entityId);
         
         return (Long) query.uniqueResult();
     }

    private static final String findCurrencySQL =
          "SELECT count(*) " +
          "  FROM UserDTO a " +
          " WHERE a.currency.id = :currency "+
          "   AND a.deleted = 0";

    public UserDTO findRoot(String username) {
        if (username == null || username.length() == 0) {
            LOG.error("can not find an empty root: " + username);
            return null;
        }
        // I need to access an association, so I can't use the parent helper class
        Criteria criteria = getSession().createCriteria(UserDTO.class)
            .add(Restrictions.eq("userName", username))
            .add(Restrictions.eq("deleted", 0))
            .createAlias("roles", "r")
                .add(Restrictions.eq("r.roleTypeId", CommonConstants.TYPE_ROOT));
        
        criteria.setCacheable(true); // it will be called over an over again
        
        return (UserDTO) criteria.uniqueResult();
    }

    public UserDTO findWebServicesRoot(String username) {
        if (username == null || username.length() == 0) {
            LOG.error("can not find an empty root: " + username);
            return null;
        }
        // I need to access an association, so I can't use the parent helper class
        Criteria criteria = getSession().createCriteria(UserDTO.class)
            .add(Restrictions.eq("userName", username))
            .add(Restrictions.eq("deleted", 0))
            .createAlias("roles", "r")
                .add(Restrictions.eq("r.roleTypeId", CommonConstants.TYPE_ROOT))
            .createAlias("permissions", "p")
                .add(Restrictions.eq("p.permission.id", 120));
        
        criteria.setCacheable(true); // it will be called over an over again
        
        return (UserDTO) criteria.uniqueResult();
    }

    public UserDTO findByUserName(String username, Integer entityId) {
        // I need to access an association, so I can't use the parent helper class
        Criteria criteria = getSession().createCriteria(UserDTO.class)
                .add(Restrictions.eq("userName", username))
                .add(Restrictions.eq("deleted", 0))
                .createAlias("company", "e")
                    .add(Restrictions.eq("e.id", entityId))
                    .add(Restrictions.eq("e.deleted", 0));
        
        return (UserDTO) criteria.uniqueResult();
    }

    public List<UserDTO> findByEmail(String email, Integer entityId) {
        Criteria criteria = getSession().createCriteria(UserDTO.class)
                .add(Restrictions.eq("deleted", 0))
                .createAlias("company", "e")
                .add(Restrictions.eq("e.id", entityId))
                .createAlias("contact", "c")
                .add(Restrictions.eq("c.email", email).ignoreCase());

        return criteria.list();
    }

    public List<UserDTO> findInStatus(Integer entityId, Integer statusId) {
        Query query = getSession().createQuery(findInStatusSQL);
        query.setParameter("entity", entityId);
        query.setParameter("status", statusId);
        return query.list();
    }
    
    public List<UserDTO> findNotInStatus(Integer entityId, Integer statusId) {
        Query query = getSession().createQuery(findNotInStatusSQL);
        query.setParameter("entity", entityId);
        query.setParameter("status", statusId);
        return query.list();
    }

    public List<UserDTO> findAgeing(Integer entityId) {
        Query query = getSession().createQuery(findAgeingSQL);
        query.setParameter("entity", entityId);
        return query.list();
    }

    public boolean exists(Integer userId, Integer entityId) {
        Criteria criteria = getSession().createCriteria(getPersistentClass())
                .add(Restrictions.idEq(userId))
                .createAlias("company", "company")
                .add(Restrictions.eq("company.id", entityId))
                .setProjection(Projections.rowCount());

        return (criteria.uniqueResult() != null && ((Long) criteria.uniqueResult()) > 0);
    }

    public Long findUserCountByCurrency(Integer currencyId){
        Query query = getSession().createQuery(findCurrencySQL);
        query.setParameter("currency", currencyId);
        return (Long) query.uniqueResult();
    }

    public List<UserDTO> findAdminUsers(Integer entityId) {
        Criteria criteria = getSession().createCriteria(UserDTO.class)
                .add(Restrictions.eq("company.id", entityId))
                .add(Restrictions.eq("deleted", 0))
                .createAlias("roles", "r")
                .add(Restrictions.eq("r.roleTypeId", CommonConstants.TYPE_ROOT));

        return criteria.list();
    }
}
