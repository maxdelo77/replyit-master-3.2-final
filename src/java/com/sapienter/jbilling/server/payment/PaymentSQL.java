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

package com.sapienter.jbilling.server.payment;

import com.sapienter.jbilling.common.Constants;

public interface PaymentSQL {
        
    // Root-Clerk gets all the entity's payments
    static final String rootClerkList = 
        "select p.id, p.id, u.user_name, co.organization_name, c.symbol, " +
        "       p.amount, p.create_datetime, i.content, i2.content " +
        "  from payment p, base_user u, international_description i, " + 
        "       international_description i2, payment_method pm, " +
        "       jbilling_table bt, jbilling_table bt2, currency c, contact co, " + 
        "       payment_result pr " +
        " where p.user_id = u.id " +
        "   and p.is_refund = ?" +
        "   and p.is_preauth = 0" +
        "   and p.method_id = pm.id " +
        "   and p.currency_id = c.id " +
        "   and u.entity_id = ? " +
        "   and i.table_id = bt.id " +
        "   and bt.name = 'payment_method' " +
        "   and i.foreign_id = pm.id " +
        "   and i.language_id = ? " +
        "   and i.psudo_column = 'description' " +
        "   and co.user_id = u.id " +
        "   and p.id not in (select payment_id from partner_payout where payment_id is not null) " +
        "   and p.deleted = 0 " +
        "   and i2.table_id = bt2.id" +
        "   and i2.language_id = i.language_id " +
        "   and i2.psudo_column = 'description' " +
        "   and bt2.name= 'payment_result'" +
        "   and pr.id = p.result_id" +
        "   and pr.id = i2.foreign_id";

    // The partner get's only its users
    static final String partnerList = 
        "select p.id, p.id, u.user_name, co.organization_name, c.symbol, " +
        "       p.amount, p.create_datetime, i.content " +
        "  from payment p, base_user u, international_description i, " +
        "       payment_method pm, jbilling_table bt, partner pa, " +
        "       customer cu, currency c, contact co " +
        " where p.user_id = u.id " +
        "   and p.is_refund = ?" +
        "   and p.is_preauth = 0" +
        "   and p.method_id = pm.id " +
        "   and p.currency_id = c.id " +
        "   and u.entity_id = ? " +
        "   and cu.partner_id = pa.id " +
        "   and pa.user_id = ? " +
        "   and cu.user_id = u.id " +        
        "   and i.table_id = bt.id " +
        "   and bt.name = 'payment_method' " +
        "   and i.foreign_id = pm.id " +
        "   and i.language_id = ? " +
        "   and i.psudo_column = 'description' " +
        "   and p.id not in (select payment_id from partner_payout where payment_id is not null) " +
        "   and co.user_id = u.id " +
        "   and p.deleted = 0 ";        

    // A customer only sees its own
    static final String customerList = 
        "select p.id, p.id, u.user_name, co.organization_name, c.symbol, " +
        "       p.amount, p.create_datetime, i.content, i2.content " +
        "  from payment p, base_user u, international_description i, " +
        "       international_description i2, payment_method pm, " +
        "       jbilling_table bt, jbilling_table bt2, currency c, contact co, " +  
        "       payment_result pr " +
        " where p.user_id = u.id " +
        "   and p.is_refund = ?" +
        "   and p.is_preauth = 0" +
        "   and p.method_id = pm.id " +
        "   and p.currency_id = c.id " +
        "   and u.id = ? " +
        "   and i.table_id = bt.id " +
        "   and bt.name = 'payment_method' " +
        "   and i.foreign_id = pm.id " +
        "   and i.language_id = ? " +
        "   and i.psudo_column = 'description' " +
        "   and co.user_id = u.id " +
        "   and p.deleted = 0 " +
        "   and (p.result_id = " + Constants.RESULT_OK  + 
        "         or p.result_id=" + Constants.RESULT_ENTERED + ")" +
        "   and i2.table_id = bt2.id" +
        "   and i2.language_id = i.language_id " +
        "   and i2.psudo_column = 'description' " +
        "   and bt2.name= 'payment_result'" +
        "   and pr.id = p.result_id" +
        "   and pr.id = i2.foreign_id";

    // The refundable payments are those only of a customer (like customerList)
    // but that have been not refunded previously
    static final String refundableList = 
        "select p.id, p.id, u.user_name, c.symbol, p.amount, " +
        "       p.create_datetime, i.content, i2.content " +
        "  from payment p, base_user u, international_description i, " +
        "       payment_method pm, jbilling_table bt, currency c," +
        "       international_description i2, jbilling_table bt2, " +
        "       payment_result pr " +
        " where p.user_id = u.id " +
        "   and p.is_refund = ?" +
        "   and p.is_preauth = 0" +
        "   and p.method_id = pm.id " +
        "   and p.currency_id = c.id " +
        "   and u.id = ? " +
        "   and i.table_id = bt.id " +
        "   and bt.name = 'payment_method' " +
        "   and i.foreign_id = pm.id " +
        "   and i.language_id = ? " +
        "   and i.psudo_column = 'description' " +
        "   and i2.table_id = bt2.id" +
        "   and i2.language_id = i.language_id " +
        "   and i2.psudo_column = 'description' " +
        "   and bt2.name= 'payment_result'" +
        "   and pr.id = p.result_id" +
        "   and pr.id = i2.foreign_id" +
        "   and p.deleted = 0 " +    
        "   and p.id not in ( " +
        "        select payment_id " +
        "          from payment " +
        "         where is_refund = 1 " +
        "           and payment_id is not null " +
        "   )" +
        " order by 1 desc";    
    
    static final String getLatest = 
		"select id from payment where create_datetime = (select max(create_datetime) from payment) " +
		        " and deleted = 0 " +
		        " and user_id = ?";
    
}
