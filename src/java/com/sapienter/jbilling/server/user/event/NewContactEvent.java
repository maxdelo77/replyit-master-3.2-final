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

package com.sapienter.jbilling.server.user.event;

import com.sapienter.jbilling.server.system.event.Event;
import com.sapienter.jbilling.server.user.contact.db.ContactDTO;

/**
 *
 * @author emilc
 */
public class NewContactEvent implements Event {

    private final Integer entityId;
    private final ContactDTO contactDto;
    
    public ContactDTO getContactDto() {
        return contactDto;
    }

    public NewContactEvent(ContactDTO contactDto, Integer entityId) {
        this.contactDto = contactDto;
        this.entityId = entityId;
    }

    public String getName() {
        return "New Contact Event";
    }

    public Integer getEntityId() {
        return entityId;
    }
}
