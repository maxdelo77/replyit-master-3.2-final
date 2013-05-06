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

package com.sapienter.jbilling.server.system.event.task;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;


import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.common.SessionInternalError;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskException;
import com.sapienter.jbilling.server.rule.RulesBaseTask;
import com.sapienter.jbilling.server.system.event.Event;
import com.sapienter.jbilling.server.util.Context;

/**
 * InternalEventsRulesTask is a rules-based plug-in that can respond 
 * to interal events. The events it subscribes to is configured in 
 * Spring using the file jbilling-internal-events-rules-tasks.xml. 
 * It inserts into the rules memory context the received event object,
 * plus the publically accessible objects the event contains.
 */
@Deprecated
public class InternalEventsRulesTask extends RulesBaseTask
        implements IInternalEventsTask {

    private static final Class<Event>[] DEFAULT_SUBSCRIBED_EVENTS = 
            new Class[] { };

    @Override
    protected FormatLogger getLog() {
        return new FormatLogger(Logger.getLogger(InternalEventsRulesTask.class));
    }

    /**
     * Returns the subscribed events from the Spring configuration.
     */
    public Class<Event>[] getSubscribedEvents() {
        // get the configuration
        Map<String, List<String>> config = (Map<String, List<String>>) 
                Context.getBean(Context.Name.INTERNAL_EVENTS_RULES_TASK_CONFIG);
        List<String> classNames = config.get(getTaskId().toString());

        // not configured yet?
        if (classNames == null) {
            LOG.info("No configuration found for InternalEventsRulesTask " +
                    "with task id: " + getTaskId());
            return DEFAULT_SUBSCRIBED_EVENTS;
        }

        Class<Event>[] events = new Class[classNames.size()];
        int i = 0;
        for (String className : classNames) {
            try {
                events[i] = (Class<Event>) Class.forName(className);
                i++;
            } catch (Exception e) {
                throw new SessionInternalError("Exception getting event " +
                        "Class object: " + className + " configured for task: " + 
                        getTaskId() + " ", InternalEventsRulesTask.class, e);
            }
        }

        return events;
    }

    /**
     * Processes the event by placing the event object and the objects
     * it contains into the rules memory context. Executes the rules. 
     */
    public void process(Event event) throws PluggableTaskException {
        // add event
        rulesMemoryContext.add(event);

        // Extract fields from concrete event type using reflection
        // and add to rules memory context.
        try {
            Class<?> eventClass = event.getClass();
            Method methods[] = eventClass.getMethods();
            for (Method method : methods) {
                // If method starts with 'get', returns an Object and
                // takes no parameters, execute it and save result for 
                // rules memory context.
                if (method.getName().startsWith("get") &&
                        !method.getReturnType().isPrimitive() &&
                        method.getParameterTypes().length == 0) {
                    rulesMemoryContext.add(method.invoke(event));
                }
            }
            executeRules();
        } catch (Exception e) {
            throw new PluggableTaskException("Error extracting event fields.", e);
        }
   }
}
