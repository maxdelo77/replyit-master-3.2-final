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
package com.sapienter.jbilling.server.system.event;

import org.apache.log4j.Logger;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.common.SessionInternalError;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskDTO;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskException;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskManager;
import com.sapienter.jbilling.server.system.event.task.IInternalEventsTask;
import com.sapienter.jbilling.server.util.Constants;

public class InternalEventProcessor {
    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(InternalEventProcessor.class));

    public void process(Event event) {
    LOG.debug("In InternalEventProcessor::process()");
        try {
            PluggableTaskManager<IInternalEventsTask> taskManager
                    = new PluggableTaskManager<IInternalEventsTask>(event.getEntityId(),
                                                                    Constants.PLUGGABLE_TASK_INTERNAL_EVENT);

            for (PluggableTaskDTO task : taskManager.getAllTasks()) {
                IInternalEventsTask myClass = taskManager.getInstance(task.getType().getClassName(),
                                                                      task.getType().getCategory().getInterfaceName(),
                                                                      task);
                if (isProcessable(myClass, event)) {
                    LOG.debug("Processing " + event + " with " + myClass);
                    myClass.process(event);
                }
            }
        } catch (PluggableTaskException e) {
            throw new SessionInternalError("Exception processing internal event plug-in",
                                           InternalEventProcessor.class, e);
        }
    }

    /**
     * Returns true if the given IInternalEventsTask can process (is subscribed to)
     * the given event.
     *
     * @param task task to check
     * @param event event to process
     * @return true if event is processable, false if not.
     */
    public boolean isProcessable(IInternalEventsTask task, Event event) {
        if (task.getSubscribedEvents() != null) {
            for (Class subscribedEvent : task.getSubscribedEvents()) {
                if (CatchAllEvent.class.equals(subscribedEvent)) {
                    // subscribed to the CatchAllEvent, process any/all incoming events
                    return true;
                }
                if (event != null && event.getClass().equals(subscribedEvent)) {
                    // explicitly subscribed to the event
                    return true;
                }
            }
        }
        return false;
    }
}
