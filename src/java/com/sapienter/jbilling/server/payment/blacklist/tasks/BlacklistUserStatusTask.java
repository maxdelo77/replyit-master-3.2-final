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

package com.sapienter.jbilling.server.payment.blacklist.tasks;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.server.metafields.db.MetaField;
import com.sapienter.jbilling.server.metafields.db.MetaFieldDAS;
import com.sapienter.jbilling.server.metafields.db.MetaFieldValue;
import org.apache.log4j.Logger;

import com.sapienter.jbilling.server.payment.blacklist.BlacklistBL;
import com.sapienter.jbilling.server.payment.blacklist.db.BlacklistDTO;
import com.sapienter.jbilling.server.pluggableTask.PluggableTask;
import com.sapienter.jbilling.server.process.event.NewUserStatusEvent;
import com.sapienter.jbilling.server.system.event.Event;
import com.sapienter.jbilling.server.system.event.task.IInternalEventsTask;
import com.sapienter.jbilling.server.user.UserDTOEx;
import com.sapienter.jbilling.server.user.contact.db.ContactDAS;
import com.sapienter.jbilling.server.user.contact.db.ContactDTO;
import com.sapienter.jbilling.server.user.db.CreditCardDTO;
import com.sapienter.jbilling.server.user.db.UserDAS;
import com.sapienter.jbilling.server.user.db.UserDTO;

/**
 * Blacklists users and all their data when their status moves to 
 * suspended or higher. 
 */
public class BlacklistUserStatusTask extends PluggableTask 
        implements IInternalEventsTask {
    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(BlacklistUserStatusTask.class));

    private static final Class<Event> events[] = new Class[] { 
            NewUserStatusEvent.class };

    public Class<Event>[] getSubscribedEvents() {
        return events;
    }

    public void process(Event event) {
        NewUserStatusEvent myEvent = (NewUserStatusEvent) event;
        // only process suspended or higher events
        if (myEvent.getNewStatusId() < UserDTOEx.STATUS_SUSPENDED) {
            return;
        }

        // If user was already suspended or higher, then only blacklist user 
        // & their info if their user id isn't already blacklisted.
        if (myEvent.getOldStatusId() >= UserDTOEx.STATUS_SUSPENDED &&
                BlacklistBL.isUserIdBlacklisted(myEvent.getUserId())) {
            LOG.warn("User id is blacklisted for an already suspended or " +
                    "higher user, returning");
            return;
        }

        UserDTO user = new UserDAS().find(myEvent.getUserId());
        BlacklistBL blacklistBL = new BlacklistBL();

        LOG.debug("Adding blacklist records for user id: " + user.getId());

        // blacklist user id
        blacklistBL.create(user.getCompany(), BlacklistDTO.TYPE_USER_ID,
                BlacklistDTO.SOURCE_USER_STATUS_CHANGE, null, null, user, null);

                // blacklist ip address
        Integer ipAddressCcf =
                BlacklistBL.getIpAddressCcfId(user.getCompany().getId());

        if (ipAddressCcf == null) {
            // blacklist preference or payment filter plug-in
            // not configured properly
            LOG.warn("Null ipAddressCcf - skipping adding IpAddress contact info");
        } else if (user.getCustomer() != null && user.getCustomer().getMetaFields() != null) {
            Object ipAddress = null;
            MetaField metaField = new MetaFieldDAS().find(ipAddressCcf);
            if (metaField != null) {
                MetaFieldValue metaFieldValue = user.getCustomer().getMetaField(metaField.getName());
                if (metaFieldValue != null) {
                    ipAddress = metaFieldValue.getValue();
                }
            }
            // blacklist the ip address if it was found
            if (ipAddress != null) {
                MetaFieldValue newValue = metaField.createValue();
                newValue.setValue(ipAddress);
                blacklistBL.create(user.getCompany(), BlacklistDTO.TYPE_IP_ADDRESS,
                        BlacklistDTO.SOURCE_USER_STATUS_CHANGE, null, null, null, newValue);
            }
        }

        // user's contact
        ContactDTO contact = new ContactDAS().findPrimaryContact(myEvent.getUserId());

        if (contact == null) {
            LOG.warn("User " + myEvent.getUserId() + " does not have contact information to blacklist.");
            return;
        }

        // contact to be added to blacklist
        ContactDTO newContact = null;

        // blacklist name
        if (contact.getFirstName() != null || contact.getLastName() != null) {
            newContact = new ContactDTO();
            newContact.setCreateDate(new Date());
            newContact.setDeleted(0);
            newContact.setFirstName(contact.getFirstName());
            newContact.setLastName(contact.getLastName());
            blacklistBL.create(user.getCompany(), BlacklistDTO.TYPE_NAME,
                    BlacklistDTO.SOURCE_USER_STATUS_CHANGE, null, newContact, 
                    null, null);
        }

        // blacklist address
        if (contact.getAddress1() != null || contact.getAddress2() != null ||
                contact.getCity() != null || contact.getStateProvince() != null ||
                contact.getPostalCode() != null || contact.getCountryCode() != null) {
            newContact = new ContactDTO();
            newContact.setCreateDate(new Date());
            newContact.setDeleted(0);
            newContact.setAddress1(contact.getAddress1());
            newContact.setAddress2(contact.getAddress2());
            newContact.setCity(contact.getCity());
            newContact.setStateProvince(contact.getStateProvince());
            newContact.setPostalCode(contact.getPostalCode());
            newContact.setCountryCode(contact.getCountryCode());
            blacklistBL.create(user.getCompany(), BlacklistDTO.TYPE_ADDRESS,
                    BlacklistDTO.SOURCE_USER_STATUS_CHANGE, null, newContact, null, null);
        }

        // blacklist phone number
        if (contact.getPhoneCountryCode() != null || 
                contact.getPhoneAreaCode() != null || 
                contact.getPhoneNumber() != null) {
            newContact = new ContactDTO();
            newContact.setCreateDate(new Date());
            newContact.setDeleted(0);
            newContact.setPhoneCountryCode(contact.getPhoneCountryCode());
            newContact.setPhoneAreaCode(contact.getPhoneAreaCode());
            newContact.setPhoneNumber(contact.getPhoneNumber());
            blacklistBL.create(user.getCompany(), BlacklistDTO.TYPE_PHONE_NUMBER,
                    BlacklistDTO.SOURCE_USER_STATUS_CHANGE, null, newContact, null, null);
        }

        // blacklist cc numbers
        Collection<CreditCardDTO> creditCards = user.getCreditCards();
        for (CreditCardDTO cc : creditCards) {
            if (cc.getNumber() != null) {
                CreditCardDTO creditCard = new CreditCardDTO();
                creditCard.setNumber(cc.getNumber());
                creditCard.setDeleted(0);
                creditCard.setCcType(cc.getCcType()); // not null
                creditCard.setCcExpiry(cc.getCcExpiry()); // not null
                blacklistBL.create(user.getCompany(), BlacklistDTO.TYPE_CC_NUMBER,
                        BlacklistDTO.SOURCE_USER_STATUS_CHANGE, creditCard, 
                        null, null, null);
            }
        }
    }
}
