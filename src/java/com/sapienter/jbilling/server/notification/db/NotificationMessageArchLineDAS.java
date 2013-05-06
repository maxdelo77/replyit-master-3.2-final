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
package com.sapienter.jbilling.server.notification.db;

import org.apache.log4j.Logger;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.server.util.db.AbstractDAS;

/**
 * 
 * @author abimael
 * 
 */
public class NotificationMessageArchLineDAS extends
        AbstractDAS<NotificationMessageArchLineDTO> {

    public static final int CONTENT_MAX_LENGTH = 500;

    public NotificationMessageArchLineDTO create(String content, Integer section) {
        NotificationMessageArchLineDTO nmal = new NotificationMessageArchLineDTO();

        if (content.length() > CONTENT_MAX_LENGTH) {
            content = content.substring(0, CONTENT_MAX_LENGTH);
            new FormatLogger(Logger.getLogger(NotificationMessageArchLineDAS.class)).warn(
                    "Trying to insert line too long. Truncating to "
                            + CONTENT_MAX_LENGTH);
        }

        nmal.setSection(section);
        nmal.setContent(content);

        return save(nmal);
    }

}
