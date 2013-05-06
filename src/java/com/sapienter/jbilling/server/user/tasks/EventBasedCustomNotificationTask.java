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

package com.sapienter.jbilling.server.user.tasks;

import com.sapienter.jbilling.server.notification.INotificationSessionBean;
import com.sapienter.jbilling.server.notification.MessageDTO;
import com.sapienter.jbilling.server.notification.NotificationBL;
import com.sapienter.jbilling.server.notification.NotificationNotFoundException;
import com.sapienter.jbilling.server.pluggableTask.PluggableTask;
import com.sapienter.jbilling.server.pluggableTask.admin.ParameterDescription;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskException;
import com.sapienter.jbilling.server.system.event.Event;
import com.sapienter.jbilling.server.system.event.task.IInternalEventsTask;
import com.sapienter.jbilling.server.user.UserBL;
import com.sapienter.jbilling.server.user.event.NewContactEvent;
import com.sapienter.jbilling.server.util.Context;
import org.apache.log4j.Logger;

/**
 * Event custom notification task.
 *
 * @author: Panche.Isajeski
 * @since: 12/07/12
 */
public class EventBasedCustomNotificationTask extends PluggableTask implements IInternalEventsTask {

    private static final Logger LOG = Logger.getLogger(EventBasedCustomNotificationTask.class);

    @SuppressWarnings("unchecked")
    private static final Class<Event>[] events = new Class[]{
            NewContactEvent.class
    };

    public static final ParameterDescription PARAMETER_NEW_CONTACT_CUSTOM_NOTIFICATION_ID =
            new ParameterDescription("new_contact_notification_id", true, ParameterDescription.Type.INT);


    //initializer for pluggable params
    // add as many event - notification parameters
    {
        descriptions.add(PARAMETER_NEW_CONTACT_CUSTOM_NOTIFICATION_ID);
    }

    @Override
    public void process(Event event) throws PluggableTaskException {

        INotificationSessionBean notificationSession = Context.getBean(Context.Name.NOTIFICATION_SESSION);
        if (event instanceof NewContactEvent) {
            fireNewContactEventNotification((NewContactEvent) event, notificationSession);
        }
    }

    @Override
    public Class<Event>[] getSubscribedEvents() {
        return events;
    }


    private boolean fireNewContactEventNotification(NewContactEvent newContactEvent,
                                                    INotificationSessionBean notificationSession) {

        Integer notificationMessageTypeId = Integer.parseInt((String) parameters
                .get(PARAMETER_NEW_CONTACT_CUSTOM_NOTIFICATION_ID.getName()));

        if (notificationMessageTypeId == null && newContactEvent.getContactDto() == null) {
            return false;
        }

        MessageDTO message = null;
        Integer userId = newContactEvent.getContactDto().getUserId();

        try {
            UserBL userBL = new UserBL(userId);
            message = new NotificationBL().getCustomNotificationMessage(
                    notificationMessageTypeId,
                    newContactEvent.getEntityId(),
                    userId,
                    userBL.getLanguage());

        } catch (NotificationNotFoundException e) {
            LOG.debug(String.format("Custom notification id: %s does not exist for the user id %s ",
                    notificationMessageTypeId, userId));
        }

        if (message == null) {
            return false;
        }

        LOG.debug(String.format("Notifying user: %s for a new contact event", userId));
        notificationSession.notify(userId, message);
        return true;
    }
}
