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

package com.sapienter.jbilling.server.util.audit;

public class EventLoggerSQL {
    
    public static String searchLog = "SELECT old_num from event_log" +
                " WHERE module_id = " + EventLogger.MODULE_WEBSERVICES +
                " AND message_id = " + EventLogger.USER_TRANSITIONS_LIST +
                " AND entity_id = ? ORDER BY id DESC";

}
