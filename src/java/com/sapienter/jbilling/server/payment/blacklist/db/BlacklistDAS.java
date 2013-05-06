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
package com.sapienter.jbilling.server.payment.blacklist.db;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.server.util.db.AbstractDAS;


public class BlacklistDAS extends AbstractDAS<BlacklistDTO> {

    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(BlacklistDAS.class));
    
    public List<BlacklistDTO> findByEntity(Integer entityId) {
        // I need to access an association, so I can't use the parent helper class
        Criteria criteria = getSession().createCriteria(BlacklistDTO.class)
                .createAlias("company", "c")
                    .add(Restrictions.eq("c.id", entityId));

        return criteria.list();
    }

    public List<BlacklistDTO> findByEntityType(Integer entityId, Integer type) {
        Criteria criteria = getSession().createCriteria(BlacklistDTO.class)
                .createAlias("company", "c")
                    .add(Restrictions.eq("c.id", entityId))
                .add(Restrictions.eq("type", type));

        return criteria.list();    
    }

    public List<BlacklistDTO> findByEntitySource(Integer entityId, Integer source) {
        Criteria criteria = getSession().createCriteria(BlacklistDTO.class)
                .createAlias("company", "c")
                    .add(Restrictions.eq("c.id", entityId))
                .add(Restrictions.eq("source", source));

        return criteria.list();
    }

    public List<BlacklistDTO> findByUser(Integer userId) {
        Criteria criteria = getSession().createCriteria(BlacklistDTO.class)
                .createAlias("user", "u")
                    .add(Restrictions.eq("u.id", userId));

        return criteria.list();
    }

    public List<BlacklistDTO> findByUserType(Integer userId, Integer type) {
        Criteria criteria = getSession().createCriteria(BlacklistDTO.class)
                .createAlias("user", "u")
                    .add(Restrictions.eq("u.id", userId))
                .add(Restrictions.eq("type", type));

        return criteria.list();
    }

    // blacklist filter specific queries

    public List<BlacklistDTO> filterByName(Integer entityId, String firstName,
            String lastName) {
        Criteria criteria = getSession().createCriteria(BlacklistDTO.class)
                .createAlias("company", "c")
                    .add(Restrictions.eq("c.id", entityId))
                .add(Restrictions.eq("type", BlacklistDTO.TYPE_NAME))
                .createAlias("contact", "ct")
                    .add(equals("ct.firstName", firstName))
                    .add(equals("ct.lastName", lastName));

        return criteria.list();
    }

    public List<BlacklistDTO> filterByAddress(Integer entityId, String address1,
            String address2, String city, String stateProvince, 
            String postalCode, String countryCode) {
        Criteria criteria = getSession().createCriteria(BlacklistDTO.class)
                .createAlias("company", "c")
                    .add(Restrictions.eq("c.id", entityId))
                .add(Restrictions.eq("type", BlacklistDTO.TYPE_ADDRESS))
                .createAlias("contact", "ct")
                    .add(equals("ct.address1", address1))
                    .add(equals("ct.address2", address2))
                    .add(equals("ct.city", city))
                    .add(equals("ct.stateProvince", stateProvince))
                    .add(equals("ct.postalCode", postalCode))
                    .add(equals("ct.countryCode", countryCode));

        return criteria.list();
    }

    public List<BlacklistDTO> filterByPhone(Integer entityId, 
            Integer phoneCountryCode, Integer phoneAreaCode, String phoneNumber) {
        Criteria criteria = getSession().createCriteria(BlacklistDTO.class)
                .createAlias("company", "c")
                    .add(Restrictions.eq("c.id", entityId))
                .add(Restrictions.eq("type", BlacklistDTO.TYPE_PHONE_NUMBER))
                .createAlias("contact", "ct")
                    .add(equals("ct.phoneCountryCode", phoneCountryCode))
                    .add(equals("ct.phoneAreaCode", phoneAreaCode))
                    .add(equals("ct.phoneNumber", phoneNumber));

        return criteria.list();
    }

    public List<BlacklistDTO> filterByCcNumbers(Integer entityId, 
            Collection<String> rawNumbers) {
        Criteria criteria = getSession().createCriteria(BlacklistDTO.class)
                .createAlias("company", "c")
                    .add(Restrictions.eq("c.id", entityId))
                .add(Restrictions.eq("type", BlacklistDTO.TYPE_CC_NUMBER))
                .createAlias("creditCard", "cc")
                    .add(Restrictions.in("cc.rawNumber", rawNumbers));

        return criteria.list();
    }

    public List<BlacklistDTO> filterByIpAddress(Integer entityId, String ipAddress, Integer ccfId) {

        // don't try and filter if there's no IP address to lookup
        if (StringUtils.isBlank(ipAddress)) {
            return Collections.emptyList();
        }

        Criteria criteria = getSession().createCriteria(BlacklistDTO.class)
                .createAlias("company", "c")
                    .add(Restrictions.eq("c.id", entityId))
                .add(Restrictions.eq("type", BlacklistDTO.TYPE_IP_ADDRESS));
        
         Criteria secondCriteria = criteria.createCriteria("metaFieldValue", "fieldValue", CriteriaSpecification.LEFT_JOIN)
                    .add(Restrictions.eq("fieldValue.field.id", ccfId))
                    .add(Restrictions.sqlRestriction("{alias}.string_value = ?", ipAddress, Hibernate.STRING));

        return secondCriteria.list();
    }

    /**
     * Considers comparing nulls as equal. Useful for some filters,
     * such as address, where not all fields may have a value.
     */
    private Criterion equals(String propertyName, Object value) {
        if (value != null) {
            return Restrictions.eq(propertyName, value);
        }
        return Restrictions.isNull(propertyName);
    }

    public int deleteSource(Integer entityId, Integer source) {
        /*
        List<BlacklistDTO> deleteList = findByEntitySource(entityId, source);

        for (BlacklistDTO entry : deleteList) {
            delete(entry);
        }

        return deleteList.size();
        */

        // should be faster than above, but hql doesn't do cascading deletes :(
        String hql = "DELETE FROM CreditCardDTO WHERE id IN (" +
                "SELECT creditCard.id FROM BlacklistDTO " + 
                "WHERE company.id = :company AND source = :source)";
        Query query = getSession().createQuery(hql);
        query.setParameter("company", entityId);
        query.setParameter("source", source);
        query.executeUpdate();

        hql = "DELETE FROM MetaFieldValue WHERE id IN (" +
                "SELECT metaFieldValue.id FROM BlacklistDTO " +
                "WHERE company.id = :company AND source = :source)";
        query = getSession().createQuery(hql);
        query.setParameter("company", entityId);
        query.setParameter("source", source);
        query.executeUpdate();

        hql = "DELETE FROM ContactDTO WHERE id IN (" +
                "SELECT contact.id FROM BlacklistDTO " + 
                "WHERE company.id = :company AND source = :source)";
        query = getSession().createQuery(hql);
        query.setParameter("company", entityId);
        query.setParameter("source", source);
        query.executeUpdate();

        hql = "DELETE FROM BlacklistDTO " +
                "WHERE company.id = :company AND source = :source";
        query = getSession().createQuery(hql);
        query.setParameter("company", entityId);
        query.setParameter("source", source);
        int result = query.executeUpdate();

        return result;
    }
}
