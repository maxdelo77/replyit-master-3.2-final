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

package com.sapienter.jbilling.server.order;

import com.sapienter.jbilling.server.util.Constants;

/**
 * @author Emil
 */
public interface OrderSQL {

    // This one is used for root and clerks
    static final String listInternal = 
        "select po.id, po.id, bu.user_name, c.organization_name , po.create_datetime " + 
        "  from purchase_order po, base_user bu, contact c " +
        "  where po.deleted = 0  " +
        "  and bu.entity_id = ? " +
        "  and po.user_id = bu.id " +
        "  and c.user_id = bu.id ";

    // PARTNER: will show only customers that belong to this partner
    static final String listPartner = 
        "select po.id, po.id, bu.user_name, c.organization_name, po.create_datetime " +
        "  from purchase_order po, base_user bu, customer cu, partner pa, contact c " +
        " where po.deleted = 0 " +
        "   and bu.entity_id = ? " +
        "   and po.user_id = bu.id" +
        "   and cu.partner_id = pa.id " +
        "   and pa.user_id = ? " +
        "   and cu.user_id = bu.id " +
        "   and c.user_id = bu.id ";

    static final String listCustomer = 
        "select po.id, po.id, bu.user_name, c.organization_name, po.create_datetime " +
        "  from purchase_order po, base_user bu, contact c " +
        " where po.deleted = 0 " +
        "   and po.user_id = ? " +
        "   and po.user_id = bu.id " +
        "   and c.user_id = bu.id ";

    static final String listByProcess = 
        "select po.id, po.id, bu.user_name, po.create_datetime " +
        "  from purchase_order po, base_user bu, billing_process bp, order_process op "+
        " where bp.id = ? " +
        "   and po.user_id = bu.id " +
        "  and op.billing_process_id = bp.id " + 
        "  and op.order_id = po.id " +
        "  order by 1 desc";
    
    static final String getAboutToExpire =
        "select purchase_order.id, purchase_order.active_until, " +
        "       purchase_order.notification_step " +
        " from purchase_order, base_user " +
        "where active_until >= ? " +
        "  and active_until <= ? " +
        "  and notify = 1 " +
        "  and purchase_order.status_id = (select id from generic_status " +
        "    where dtype = 'order_status' AND status_value = 1 )" +
        "  and user_id = base_user.id " +
        "  and entity_id = ? " +
        "  and (notification_step is null or notification_step < ?)";
    
    static final String getLatest =
    	"select id from purchase_order where " +
    	"create_datetime = (select max(create_datetime) " +
    	"from purchase_order where user_id = ? and deleted = 0)";
    
    static final String getLatestByItemType =
        "select max(purchase_order.id) " +
        "  from purchase_order "+
        "  inner join order_line on order_line.order_id = purchase_order.id " +
        "  inner join item on item.id = order_line.item_id " +
        "  inner join item_type_map on item_type_map.item_id = item.id " +
        " where purchase_order.user_id = ?" +
        "   and item_type_map.type_id = ? " +
        "   and purchase_order.deleted = 0";
    
    static final String getByUserAndPeriod =
        "select id " +
        "  from purchase_order " +
        " where user_id = ? " +
        "   and period_id = ? " +
        "   and deleted = 0";

}
