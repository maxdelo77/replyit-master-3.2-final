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

import java.util.Date;
import java.util.HashSet;

import com.sapienter.jbilling.server.notification.MessageSection;
import com.sapienter.jbilling.server.util.db.AbstractDAS;

/**
 * 
 * @author abimael
 * 
 */
public class NotificationMessageArchDAS extends
        AbstractDAS<NotificationMessageArchDTO> {
    private static int LINE_LENGTH = 500;

    public NotificationMessageArchDTO create(Integer id,
            MessageSection[] sections) {

        NotificationMessageArchLineDAS lineHome = new NotificationMessageArchLineDAS();
        NotificationMessageArchDTO nma = new NotificationMessageArchDTO();
        nma.setTypeId(id);
        nma.setCreateDatetime(new Date());

        for (int f = 0; f < sections.length; f++) {

            String content = sections[f].getContent();
            for (int index = 0; index < content.length(); index += LINE_LENGTH) {
                int end = (content.length() < index + LINE_LENGTH) ? content
                        .length() : index + LINE_LENGTH;
                NotificationMessageArchLineDTO line = lineHome.create(content
                        .substring(index, end), sections[f].getSection());
                line.setNotificationMessageArch(nma);
                nma.getNotificationMessageArchLines().add(line);
            }
        }

        return save(nma);
    }

}
