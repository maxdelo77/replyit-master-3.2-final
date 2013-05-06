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

import com.sapienter.jbilling.server.order.Usage;
import com.sapienter.jbilling.server.util.Context;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.util.Date;

/**
 * @author Brian Cowdery
 * @since 16-08-2010
 */
public class UsageDAS extends HibernateDaoSupport {

    public UsageDAS() {
        setSessionFactory((SessionFactory) Context.getBean(Context.Name.HIBERNATE_SESSION));
    }

    private static final String EXCLUDE_ORDER_ID_CLAUSE = " and ol.order_id != :excluded_order_id ";
    
    private static final String USAGE_BY_ITEM_ID_SQL =
            "select "
            + " sum(ol.amount) as amount, "
            + " sum(ol.quantity) as quantity "
            + "from "
            + " order_line ol"
            + " join purchase_order o on o.id = ol.order_id "
            + "where "
            + " o.deleted = 0 "
            + " and ol.deleted = 0 "          // order and line not deleted
            + " and o.status_id in (16, 17) " // active or finished
            + " and o.user_id = :user_id "
            + " and ol.item_id = :item_id "
            + " and o.active_since between :start_date and :end_date";

    public Usage findUsageByItem(Integer excludedOrderId, Integer itemId, Integer userId, Date startDate, Date endDate) {
        String sql = excludedOrderId != null
                     ? USAGE_BY_ITEM_ID_SQL + EXCLUDE_ORDER_ID_CLAUSE
                     : USAGE_BY_ITEM_ID_SQL;

        Query query = getSession().createSQLQuery(sql)
                .addScalar("amount")
                .addScalar("quantity")
                .setResultTransformer(Transformers.aliasToBean(Usage.class));

        query.setParameter("user_id", userId);
        query.setParameter("item_id", itemId);
        query.setParameter("start_date", startDate);
        query.setParameter("end_date", endDate);

        if (excludedOrderId != null)
            query.setParameter("excluded_order_id", excludedOrderId);

        Usage usage = (Usage) query.uniqueResult();
        usage.setUserId(userId);
        usage.setItemId(itemId);
        usage.setStartDate(startDate);
        usage.setEndDate(endDate);

        return usage;
    }

    private static final String SUBACCOUNT_USAGE_BY_ITEM_ID_SQL =
            "select "
            + " sum(ol.amount) as amount, "
            + " sum(ol.quantity) as quantity "
            + "from "
            + "  order_line ol "
            + "  join purchase_order o on o.id = ol.order_id "
            + "where "
            + " o.deleted = 0 "
            + " and ol.deleted = 0 "          // order and line not deleted
            + " and o.status_id in (16, 17) " // active or finished
            + " and ol.item_id = :item_id "
            + " and ( "
            + "  o.user_id = :user_id "
            + "  or o.user_id in ( "
            + "   select subaccount.user_id "
            + "   from customer parent "
            + "    left join customer subaccount on subaccount.parent_id = parent.id "
            + "   where parent.user_id = :user_id "
            + "  ) "
            + " ) "
            + " and ol.create_datetime between :start_date and :end_date";

    public Usage findSubAccountUsageByItem(Integer excludedOrderId, Integer itemId, Integer userId, Date startDate,
                                           Date endDate) {

        String sql = excludedOrderId != null
                     ? SUBACCOUNT_USAGE_BY_ITEM_ID_SQL + EXCLUDE_ORDER_ID_CLAUSE
                     : SUBACCOUNT_USAGE_BY_ITEM_ID_SQL;

        Query query = getSession().createSQLQuery(sql)
                .addScalar("amount")
                .addScalar("quantity")
                .setResultTransformer(Transformers.aliasToBean(Usage.class));

        query.setParameter("user_id", userId);
        query.setParameter("item_id", itemId);
        query.setParameter("start_date", startDate);
        query.setParameter("end_date", endDate);

        if (excludedOrderId != null)
            query.setParameter("excluded_order_id", excludedOrderId);

        Usage usage = (Usage) query.uniqueResult();
        usage.setUserId(userId);
        usage.setItemId(itemId);
        usage.setStartDate(startDate);
        usage.setEndDate(endDate);

        return usage;
    }

    private static final String USAGE_BY_ITEM_TYPE_SQL =
            "select "
            + " sum(ol.amount) as amount, "
            + " sum(ol.quantity) as quantity "
            + "from "
            + " order_line ol "
            + " join purchase_order o on o.id = ol.order_id "
            + " join item_type_map tm on tm.item_id = ol.item_id "
            + "where "
            + " o.deleted = 0 "
            + " and ol.deleted = 0 "          // order and line not deleted
            + " and o.status_id in (16, 17) " // active or finished
            + " and o.user_id = :user_id "
            + " and tm.type_id = :item_type_id"
            + " and ol.create_datetime between :start_date and :end_date";

    public Usage findUsageByItemType(Integer excludedOrderId, Integer itemTypeId, Integer userId, Date startDate,
                                     Date endDate) {

        String sql = excludedOrderId != null
                     ? USAGE_BY_ITEM_TYPE_SQL + EXCLUDE_ORDER_ID_CLAUSE
                     : USAGE_BY_ITEM_TYPE_SQL;

        Query query = getSession().createSQLQuery(sql)
                .addScalar("amount")
                .addScalar("quantity")
                .setResultTransformer(Transformers.aliasToBean(Usage.class));

        query.setParameter("user_id", userId);
        query.setParameter("item_type_id", itemTypeId);
        query.setParameter("start_date", startDate);
        query.setParameter("end_date", endDate);

        if (excludedOrderId != null)
            query.setParameter("excluded_order_id", excludedOrderId);

        Usage usage = (Usage) query.uniqueResult();
        usage.setUserId(userId);
        usage.setItemTypeId(itemTypeId);
        usage.setStartDate(startDate);
        usage.setEndDate(endDate);

        return usage;
    }

    private static final String SUBACCOUNT_USAGE_BY_ITEM_TYPE_SQL =
            "select "
            + " sum(ol.amount) as amount, "
            + " sum(ol.quantity) as quantity "
            + "from "
            + " order_line ol "
            + " join purchase_order o on o.id = ol.order_id "
            + " join item_type_map tm on tm.item_id = ol.item_id "
            + "where "
            + " o.deleted = 0 "
            + " and ol.deleted = 0 "          // order and line not deleted
            + " and o.status_id in (16, 17) " // active or finished
            + " and tm.type_id = :item_type_id "
            + " and ( "
            + "  o.user_id = :user_id "
            + "  or o.user_id in ( "
            + "   select subaccount.user_id "
            + "   from customer parent "
            + "    left join customer subaccount on subaccount.parent_id = parent.id "
            + "   where parent.user_id = :user_id "
            + "  ) "
            + " ) "
            + " and ol.create_datetime between :start_date and :end_date";

    public Usage findSubAccountUsageByItemType(Integer excludedOrderId, Integer itemTypeId, Integer userId,
                                               Date startDate, Date endDate) {

        String sql = excludedOrderId != null
                     ? SUBACCOUNT_USAGE_BY_ITEM_TYPE_SQL + EXCLUDE_ORDER_ID_CLAUSE
                     : SUBACCOUNT_USAGE_BY_ITEM_TYPE_SQL;


        Query query = getSession().createSQLQuery(sql)
                .addScalar("amount")
                .addScalar("quantity")
                .setResultTransformer(Transformers.aliasToBean(Usage.class));

        query.setParameter("user_id", userId);
        query.setParameter("item_type_id", itemTypeId);
        query.setParameter("start_date", startDate);
        query.setParameter("end_date", endDate);

        if (excludedOrderId != null)
            query.setParameter("excluded_order_id", excludedOrderId);

        Usage usage = (Usage) query.uniqueResult();
        usage.setUserId(userId);
        usage.setItemTypeId(itemTypeId);
        usage.setStartDate(startDate);
        usage.setEndDate(endDate);

        return usage;
    }
}
