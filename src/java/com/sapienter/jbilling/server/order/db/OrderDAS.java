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

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.ScrollableResults;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.sapienter.jbilling.common.Util;
import com.sapienter.jbilling.server.invoice.db.InvoiceDTO;
import com.sapienter.jbilling.server.util.Constants;
import com.sapienter.jbilling.server.util.db.AbstractDAS;

public class OrderDAS extends AbstractDAS<OrderDTO> {

    /**
     * Returns the newest active order for the given user id and period.
     *
     * @param userId user id
     * @param period period
     * @return newest active order for user and period.
     */
    @SuppressWarnings("unchecked")
    public OrderDTO findByUserAndPeriod(Integer userId, OrderPeriodDTO period) {
        Criteria criteria = getSession().createCriteria(OrderDTO.class)
                .createAlias("orderStatus", "s")
                .add(Restrictions.eq("s.id", Constants.ORDER_STATUS_ACTIVE))
                .add(Restrictions.eq("deleted", 0))
                .createAlias("baseUserByUserId", "u")
                .add(Restrictions.eq("u.id", userId))
                .add(Restrictions.eq("orderPeriod", period))
                .addOrder(Order.asc("id"))
                .setMaxResults(1);

        return findFirst(criteria);
    }

    /**
     * Returns an order by id and that is deleted or not depending on the isDeleted parameter.
     *
     * @param orderId   Id of the order to find.
     * @param isDeleted <b>true</b> if we want to find a deleted order and <b>false</b> if we want a not deleted order.
     * @return Order retrieved by id and deleted true/false.
     */
    public OrderDTO findByIdAndIsDeleted(Integer orderId, boolean isDeleted) {
        Criteria criteria = getSession().createCriteria(OrderDTO.class)
                .add(Restrictions.eq("id", orderId))
                .add(Restrictions.eq("deleted", isDeleted ? 1 : 0))
                .setMaxResults(1);

        return (OrderDTO) criteria.uniqueResult();
    }

    /**
     * Returns the oldest active order for the given user id that contains an item
     * with the given id and period different to once.
     *
     * @param userId user id
     * @param itemId item id
     * @return newest active order for user and period.
     */
    @SuppressWarnings("unchecked")
    public OrderDTO findRecurringOrder(Integer userId, Integer itemId) {
        Criteria criteria = getSession().createCriteria(OrderDTO.class)
                .createAlias("orderStatus", "s")
                .add(Restrictions.eq("s.id", Constants.ORDER_STATUS_ACTIVE))
                .add(Restrictions.eq("deleted", 0))
                .createAlias("baseUserByUserId", "u")
                .add(Restrictions.eq("u.id", userId))
                .createAlias("orderPeriod", "p")
                .add(Restrictions.ne("p.id", Constants.ORDER_PERIOD_ONCE))
                .createAlias("lines", "l")
                .createAlias("l.item", "i")
                .add(Restrictions.eq("i.id", itemId))
                .addOrder(Order.desc("id"))
                .setMaxResults(1);

        return findFirst(criteria);
    }

    public OrderProcessDTO findProcessByEndDate(Integer id, Date myDate) {
        return (OrderProcessDTO) getSession().createFilter(find(id).getOrderProcesses(),
                "where this.periodEnd = :endDate").setDate("endDate",
                        Util.truncateDate(myDate)).uniqueResult();

    }

    /**
     * Finds active recurring orders for a given user
     * @param userId
     * @return
     */
    public List<OrderDTO> findByUserSubscriptions(Integer userId) {
        // I need to access an association, so I can't use the parent helper class
        Criteria criteria = getSession().createCriteria(OrderDTO.class)
		        .add(Restrictions.eq("deleted", 0))
		        .createAlias("baseUserByUserId", "u")
		        	.add(Restrictions.eq("u.id", userId))
	        	.createAlias("orderPeriod", "p")
		        	.add(Restrictions.ne("p.id", Constants.ORDER_PERIOD_ONCE))
	        	.createAlias("orderStatus", "s");

        Criterion ORDER_ACTIVE = Restrictions.eq("s.id", Constants.ORDER_STATUS_ACTIVE);

        Criterion ORDER_FINISHED = Restrictions.eq("s.id", Constants.ORDER_STATUS_FINISHED);
        Criterion UNTIL_FUTURE = Restrictions.gt("activeUntil", new Date());
        LogicalExpression FINISH_IN_FUTURE= Restrictions.and(ORDER_FINISHED, UNTIL_FUTURE);

        LogicalExpression orderActiveOrEndsLater= Restrictions.or(ORDER_ACTIVE, FINISH_IN_FUTURE);

        // Criteria or condition
        criteria.add(orderActiveOrEndsLater);

        return criteria.list();
    }

    /**
     * Finds all active orders for a given user
     * @param userId
     * @return
     */
    public Object findEarliestActiveOrder(Integer userId) {
        // I need to access an association, so I can't use the parent helper class
        Criteria criteria = getSession().createCriteria(OrderDTO.class)
                .createAlias("orderStatus", "s")
                    .add(Restrictions.eq("s.id", Constants.ORDER_STATUS_ACTIVE))
                .add(Restrictions.eq("deleted", 0))
                .createAlias("baseUserByUserId", "u")
                    .add(Restrictions.eq("u.id", userId))
                .addOrder(Order.asc("nextBillableDay"));

        return findFirst(criteria);
    }

    /**
     * Returns a scrollable result set of orders with a specific status belonging to a user.
     *
     * You MUST close the result set after iterating through the results to close the database
     * connection and discard the cursor!
     *
     * <code>
     *     ScrollableResults orders = new OrderDAS().findByUser_Status(123, 1);
     *     // do something
     *     orders.close();
     * </code>
     *
     * @param userId user ID
     * @param statusId order status to include
     * @return scrollable results for found orders.
     */
    public ScrollableResults findByUser_Status(Integer userId,Integer statusId) {
        // I need to access an association, so I can't use the parent helper class
        Criteria criteria = getSession().createCriteria(OrderDTO.class)
                .add(Restrictions.eq("deleted", 0))
                .createAlias("baseUserByUserId", "u")
                    .add(Restrictions.eq("u.id", userId))
                .createAlias("orderStatus", "s")
                    .add(Restrictions.eq("s.id", statusId));

        return criteria.scroll();
    }

    // used for the web services call to get the latest X orders
    public List<Integer> findIdsByUserLatestFirst(Integer userId, int maxResults) {
        Criteria criteria = getSession().createCriteria(OrderDTO.class)
                .add(Restrictions.eq("deleted", 0))
                .createAlias("baseUserByUserId", "u")
                    .add(Restrictions.eq("u.id", userId))
                .setProjection(Projections.id())
                .addOrder(Order.desc("id"))
                .setMaxResults(maxResults)
                .setComment("findIdsByUserLatestFirst " + userId + " " + maxResults);
        return criteria.list();
    }

    // used for the web services call to get the latest X orders that contain an item of a type id
    @SuppressWarnings("unchecked")
    public List<Integer> findIdsByUserAndItemTypeLatestFirst(Integer userId, Integer itemTypeId, int maxResults) {
        // I'm a HQL guy, not Criteria
        String hql =
            "select distinct(orderObj.id)" +
            " from OrderDTO orderObj" +
            " inner join orderObj.lines line" +
            " inner join line.item.itemTypes itemType" +
            " where itemType.id = :typeId" +
            "   and orderObj.baseUserByUserId.id = :userId" +
            "   and orderObj.deleted = 0" +
            " order by orderObj.id desc";
        List<Integer> data = getSession()
                                .createQuery(hql)
                                .setParameter("userId", userId)
                                .setParameter("typeId", itemTypeId)
                                .setMaxResults(maxResults)
                                .list();
        return data;
    }

    /**
     * @author othman
     * @return list of active orders
     */
    public List<OrderDTO> findToActivateOrders() {
        Date today = Util.truncateDate(new Date());
        Criteria criteria = getSession().createCriteria(OrderDTO.class);

        criteria.add(Restrictions.eq("deleted", 0));
        criteria.add(Restrictions.or(Expression.le("activeSince", today),
                Expression.isNull("activeSince")));
        criteria.add(Restrictions.or(Expression.gt("activeUntil", today),
                Expression.isNull("activeUntil")));

        return criteria.list();
    }

    /**
     * @author othman
     * @return list of inactive orders
     */
    public List<OrderDTO> findToDeActiveOrders() {
        Date today = Util.truncateDate(new Date());
        Criteria criteria = getSession().createCriteria(OrderDTO.class);

        criteria.add(Restrictions.eq("deleted", 0));
        criteria.add(Restrictions.or(Expression.gt("activeSince", today),
                Expression.le("activeUntil", today)));

        return criteria.list();
    }

    public BigDecimal findIsUserSubscribedTo(Integer userId, Integer itemId) {
        String hql =
                "select sum(l.quantity) " +
                "from OrderDTO o " +
                "inner join o.lines l " +
                "where l.item.id = :itemId and " +
                "o.baseUserByUserId.id = :userId and " +
                "o.orderPeriod.id != :periodVal and " +
                "o.orderStatus.id = :status and " +
                "o.deleted = 0 and " +
                "l.deleted = 0";

        BigDecimal result = (BigDecimal) getSession()
                .createQuery(hql)
                .setInteger("userId", userId)
                .setInteger("itemId", itemId)
                .setInteger("periodVal", Constants.ORDER_PERIOD_ONCE)
                .setInteger("status", Constants.ORDER_STATUS_ACTIVE)
                .uniqueResult();

        return (result == null ? BigDecimal.ZERO : result);
    }

    public Integer[] findUserItemsByCategory(Integer userId,
            Integer categoryId) {

        Integer[] result = null;

        final String hql =
                "select distinct(i.id) " +
                "from OrderDTO o " +
                "inner join o.lines l " +
                "inner join l.item i " +
                "inner join i.itemTypes t " +
                "where t.id = :catId and " +
                "o.baseUserByUserId.id = :userId and " +
                "o.orderPeriod.id != :periodVal and " +
                "o.deleted = 0 and " +
                "l.deleted = 0";
        List qRes = getSession()
                .createQuery(hql)
                .setInteger("userId", userId)
                .setInteger("catId", categoryId)
                .setInteger("periodVal", Constants.ORDER_PERIOD_ONCE)
                .list();
        if (qRes != null && qRes.size() > 0) {
            result = (Integer[])qRes.toArray(new Integer[0]);
        }
        return result;
    }

    private static final String FIND_ONETIMERS_BY_DATE_HQL =
            "select o " +
                    "  from OrderDTO o " +
                    " where o.baseUserByUserId.id = :userId " +
                    "   and o.orderPeriod.id = :periodId " +
                    "   and cast(activeSince as date) = :activeSince " +
                    "   and deleted = 0";

    @SuppressWarnings("unchecked")
    public List<OrderDTO> findOneTimersByDate(Integer userId, Date activeSince) {
        Query query = getSession().createQuery(FIND_ONETIMERS_BY_DATE_HQL)
                .setInteger("userId", userId)
                .setInteger("periodId", Constants.ORDER_PERIOD_ONCE)
                .setDate("activeSince", activeSince);

        return query.list();
    }

    /**
     * Find orders by user ID and order notes.
     *
     * @param userId user id
     * @param notes order notes to match
     * @return list of found orders, empty if none
     */
    @SuppressWarnings("unchecked")
    public List<OrderDTO> findByNotes(Integer userId, String notes) {
        Criteria criteria = getSession().createCriteria(getPersistentClass())
                .add(Restrictions.eq("notes", notes))
                .add(Restrictions.eq("deleted", 0))
                .add(Restrictions.eq("baseUserByUserId.id", userId));

        return criteria.list();
    }

    /**
     * Find orders by user ID and where notes are like the given string. This method
     * can accept wildcard characters '%' for matching.
     *
     * @param userId user id
     * @param like string to match against order notes
     * @return list of found orders, empty if none
     */
    @SuppressWarnings("unchecked")
    public List<OrderDTO> findByNotesLike(Integer userId, String like) {
        Criteria criteria = getSession().createCriteria(getPersistentClass())
                .add(Restrictions.like("notes", like))
                .add(Restrictions.eq("deleted", 0))
                .add(Restrictions.eq("baseUserByUserId.id", userId));

        return criteria.list();
    }

    /**
     * Returns the latest active order for the given user id
     * that was created as an Invoice Overdue Penalty.
     *
     * @param userId user id
     * @param invoiceDto invoice
     * @return penalty order for invoice invoice
     */
    @SuppressWarnings("unchecked")
    public List<OrderDTO>  findPenaltyOrderForInvoice(InvoiceDTO invoice) {
        Criteria criteria = getSession().createCriteria(OrderDTO.class)
                .createAlias("orderStatus", "s")
                .add(Restrictions.eq("s.id", Constants.ORDER_STATUS_ACTIVE))
                .add(Restrictions.eq("deleted", 0))
                .createAlias("baseUserByUserId", "u")
                .add(Restrictions.eq("u.id", invoice.getUserId()))
                .createAlias("orderPeriod", "p")
                .add(Restrictions.ne("p.id", Constants.ORDER_PERIOD_ONCE))
                //.add(Restrictions.eq("activeSince", invoice.getDueDate()))
                .createAlias("lines", "l")
                .add(Restrictions.eq("l.orderLineType.id", Constants.ORDER_LINE_TYPE_PENALTY))
                .add(Restrictions.ilike("l.description", "Overdue Penalty for Invoice Number ", MatchMode.ANYWHERE))
                .addOrder(Order.desc("id"));
        //.setMaxResults(1);

        return criteria.list();
    }
    
    private static final String CURRENCY_USAGE_FOR_ENTITY_SQL =
    		" select count(*) " +
    		" from OrderDTO dto " +
    		" where dto.orderStatus.id = :status " +
    		" and dto.currency.id = :currencyId " +
    		" and dto.baseUserByUserId.company.id = :entityId ";
    
    public Long findOrderCountByCurrencyAndEntity(Integer currencyId, Integer entityId ) {
    	Query query = getSession().createQuery(CURRENCY_USAGE_FOR_ENTITY_SQL)
    		  .setParameter("status", Constants.ORDER_STATUS_ACTIVE)
    		  .setParameter("currencyId", currencyId)
    		  .setParameter("entityId", entityId);
    	return (Long) query.uniqueResult();
    }
}
