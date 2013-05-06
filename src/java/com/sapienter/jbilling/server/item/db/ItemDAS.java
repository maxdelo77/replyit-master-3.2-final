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

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.Query;

public class ItemDAS extends AbstractDAS<ItemDTO> {

    /**
     * Returns a list of all items for the given item type (category) id.
     * If no results are found an empty list will be returned.
     *
     * @param itemTypeId item type id
     * @return list of items, empty if none found
     */
    @SuppressWarnings("unchecked")
    public List<ItemDTO> findAllByItemType(Integer itemTypeId) {
        Criteria criteria = getSession().createCriteria(getPersistentClass())
                .createAlias("itemTypes", "type")
                .add(Restrictions.eq("type.id", itemTypeId));

        return criteria.list();
    }

    /**
     * Returns a list of all items with item type (category) who's
     * description matches the given prefix.
     *
     * @param prefix prefix to check
     * @return list of items, empty if none found
     */
    @SuppressWarnings("unchecked")
    public List<ItemDTO> findItemsByCategoryPrefix(String prefix) {
        Criteria criteria = getSession().createCriteria(getPersistentClass())
                .createAlias("itemTypes", "type")
                .add(Restrictions.like("type.description", prefix + "%"));

        return criteria.list();
    }    

    public List<ItemDTO> findItemsByInternalNumber(String internalNumber) {
        Criteria criteria = getSession().createCriteria(getPersistentClass())
                .add(Restrictions.eq("internalNumber", internalNumber));

        return criteria.list();
    }

    public ItemDTO findItemByInternalNumber(String internalNumber) {
        Criteria criteria = getSession().createCriteria(getPersistentClass())
                .add(Restrictions.eq("internalNumber", internalNumber))
                .add(Restrictions.eq("deleted", 0));

        return (ItemDTO)criteria.uniqueResult();
    }

    private static final String CURRENCY_USAGE_FOR_ENTITY_SQL =
            "select count(*) from " +
            " item i, " +
            " item_price_timeline ipt, " +
            " price_model pm " +
            " where " +
            "     ipt.item_id = i.id " +
            " and ipt.price_model_id = pm.id " +
            " and pm.currency_id = :currencyId " +
            " and i.entity_id = :entityId " +
            " and i.deleted = 0 ";

    public Long findProductCountByCurrencyAndEntity(Integer currencyId, Integer entityId ) {
        Query sqlQuery = getSession().createSQLQuery(CURRENCY_USAGE_FOR_ENTITY_SQL);
        sqlQuery.setParameter("currencyId", currencyId);
        sqlQuery.setParameter("entityId", entityId);
        Number count = (Number) sqlQuery.uniqueResult();
        return Long.valueOf(null == count ? 0L : count.longValue());
    }
}
