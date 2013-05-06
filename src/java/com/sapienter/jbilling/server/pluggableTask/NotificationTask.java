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

package com.sapienter.jbilling.server.pluggableTask;

import com.sapienter.jbilling.server.notification.MessageDTO;
import com.sapienter.jbilling.server.user.db.UserDTO;

/*
 * Each task is resposaible of verifying if it should run or not,
 * for example, an custom email task for an entity can extend the
 * basic email task, and then perform a verification if that user
 * has subscribed or not to that particular type of message.
 * Eventually, a method like getPreferredDeliveryType could be
 * provided.   
 */
public interface NotificationTask {
    public void deliver(UserDTO user, MessageDTO sections)
            throws TaskException;
    
    /**
     * The needed sections for a task to do its job. Plain text email will do with 2, but 
     * HTML + text will need 3, for example. 
     * This will tell the GUI how many section to display, and makes notification_message_type.sections obsolete.
     * @return
     */
    public int getSections();
}
