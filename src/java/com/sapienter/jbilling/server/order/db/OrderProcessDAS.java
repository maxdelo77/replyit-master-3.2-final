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

import java.util.Date;
import java.util.List;

import org.hibernate.Query;

import com.sapienter.jbilling.server.util.Constants;
import com.sapienter.jbilling.server.util.db.AbstractDAS;

public class OrderProcessDAS extends AbstractDAS<OrderProcessDTO> {
    
    //used to check of the order has any invoices (non deleted not cancelled)
    public List<Integer> findActiveInvoicesForOrder(Integer orderId) {

        String hql = "select pr.invoice.id" +
                     "  from OrderProcessDTO pr " +
                     "  where pr.purchaseOrder.id = :orderId" +
                     "    and pr.invoice.deleted = 0" + 
                     "    and pr.isReview = 0";

        List<Integer> data = getSession()
                        .createQuery(hql)
                        .setParameter("orderId", orderId)
                        .setComment("OrderProcessDAS.findActiveInvoicesForOrder " + orderId)
                        .list();
        return data;
    }
    
    public Date getNextInvoiceDateForUser(Integer userId) {
    	
    	String hql = "select max(pr.periodStart) from OrderProcessDTO pr " +
    			"where pr.isReview = 0 " +
    			"and pr.invoice.deleted = 0 " +
    			"and pr.purchaseOrder.deleted = 0 " +
    			"and pr.purchaseOrder.baseUserByUserId.id = :userId";
    	
    	Query query = getSession().createQuery(hql);
    	query.setInteger("userId", userId);
    	
    	return (Date) query.uniqueResult();
    }
    
}
