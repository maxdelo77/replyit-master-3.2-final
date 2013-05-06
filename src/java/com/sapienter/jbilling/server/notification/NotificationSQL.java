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

package com.sapienter.jbilling.server.notification;

import com.sapienter.jbilling.server.util.Constants;

/**
 * @author Emil
 */
public interface NotificationSQL {

    static final String listTypes = 
        "select nmt.id, i.content " +
        "  from notification_message_type nmt, international_description i, " +
        "       jbilling_table bt " + 
        " where i.table_id = bt.id " +
        "   and bt.name = 'notification_message_type' " + 
        "   and i.foreign_id = nmt.id " + 
        "   and i.language_id = ? " +
        "   and i.psudo_column = 'description'";

    static final String allEmails = 
        "select c.email " +
        "  from base_user a, contact_map b, contact c, jbilling_table d, " +
        "       contact_type ct, user_role_map urm " +
        " where a.id = b.foreign_id " +
        "   and b.type_id = ct.id " +
        "   and a.id = urm.user_id " +
        "   and urm.role_id = " + Constants.TYPE_CUSTOMER +
        "   and ct.is_primary = 1 " +
        "   and b.table_id = d.id " +
        "   and b.contact_id = c.id " +
        "   and d.name = 'base_user' " +
        "   and c.email is not null " +
        "   and a.entity_id = ?";

}
