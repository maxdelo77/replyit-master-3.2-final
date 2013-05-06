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

package com.sapienter.jbilling.server.item.tasks;

import org.apache.log4j.Logger;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.server.item.event.PlanDeletedEvent;
import com.sapienter.jbilling.server.item.event.PlanUpdatedEvent;
import com.sapienter.jbilling.server.item.event.NewPlanEvent;
import com.sapienter.jbilling.server.item.tasks.planChanges.FilePlanChangesCommunication;
import com.sapienter.jbilling.server.item.tasks.planChanges.IPlanChangesCommunication;
import com.sapienter.jbilling.server.item.tasks.planChanges.SugarCrmPlanChangesCommunication;
import com.sapienter.jbilling.server.pluggableTask.PluggableTask;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskException;
import com.sapienter.jbilling.server.system.event.Event;
import com.sapienter.jbilling.server.system.event.task.IInternalEventsTask;

/**
 * Responds to changes to plans. Used to communicate the changes to 
 * external systems, such as SugarCRM.
 */
public class PlanChangesExternalTask extends PluggableTask 
        implements IInternalEventsTask {

    private static final FormatLogger LOG = 
            new FormatLogger(Logger.getLogger(PlanChangesExternalTask.class));

    private static final Class<Event> events[] = new Class[] { 
            NewPlanEvent.class, PlanUpdatedEvent.class, 
            PlanDeletedEvent.class };

    public Class<Event>[] getSubscribedEvents() {
        return events;
    }

    //initializer for pluggable params
    {
        SugarCrmPlanChangesCommunication.addParameters(descriptions);
    }

    public void process(Event event) throws PluggableTaskException {

        // IPlanChangesCommunication task = new FilePlanChangesCommunication();
        IPlanChangesCommunication task = new SugarCrmPlanChangesCommunication(
                parameters);

        if (event instanceof NewPlanEvent) {
            task.process((NewPlanEvent) event);
        } else if (event instanceof PlanUpdatedEvent) {
            task.process((PlanUpdatedEvent) event);
        } else if (event instanceof PlanDeletedEvent) {
            task.process((PlanDeletedEvent) event);
        } else {
            throw new PluggableTaskException("Unknown event: " + 
                    event.getClass());
        }
    }
}
