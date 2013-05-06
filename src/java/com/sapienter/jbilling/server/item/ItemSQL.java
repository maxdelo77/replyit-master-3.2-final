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

package com.sapienter.jbilling.server.item;

/**
 * @author Emil
 */
public interface ItemSQL {
    
    // the general list of items, shows always the description of
    // the entity. This then prevents items not showing up because
    // the logged user has a differenct language
    static final String list = 
        "select a.id, a.id, a.internal_number, b.content " +
        "  from item a, international_description b, jbilling_table c," +
        "       entity e " +
        " where a.entity_id = e.id " +
        "   and e.id = ? " +
        "   and a.deleted = 0 " +
        "   and b.table_id = c.id " +
        "   and c.name = 'item' " +
        "   and b.foreign_id = a.id " +
        "   and b.language_id = e.language_id " +
        "   and b.psudo_column = 'description' " +
        " order by a.internal_number";

    static final String listType = 
        "select a.id, a.id, a.description " +
        "  from item_type a " +
        " where a.entity_id = ? ";

    static final String listUserPrice = 
        "select d.id, a.id, a.internal_number, b.content, d.price " +
        "  from item a, international_description b, jbilling_table c, " + 
        "       item_user_price d " +
        " where a.entity_id = ? " +
        "   and d.user_id = ? " +
        "   and a.id = d.item_id " +
        "   and a.deleted = 0 " +
        "   and b.table_id = c.id " +
        "   and c.name = 'item' " +
        "   and b.foreign_id = a.id " +
        "   and b.language_id = ? " +
        "   and b.psudo_column = 'description' " +
        " order by 1";

    static final String listPromotion = 
        "select b.id, b.code, b.since, b.until, b.once, c.content" +
        "  from item a, promotion b, international_description c, jbilling_table d  " +
        " where a.entity_id = ? " +
        "   and a.deleted = 0 " +
        "   and c.table_id = d.id " +
        "   and d.name = 'item' " +
        "   and c.foreign_id = a.id " +
        "   and c.language_id = ? " +
        "   and c.psudo_column = 'description' " +
        "   and a.id = b.item_id " +
        " order by 1";

}
