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

import com.sapienter.jbilling.common.SessionInternalError;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskException;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskManager;

/**
 * Implementation of this interface take an event, open it to extracts its data.
 * Then calls a specific pluggable task using this data as parameters
 * Usually, there is a one-to-one relationship between:
 *          event - processor - pluggable task
 * Yet, a processor can take care of many events, and deal with one pluggable task.
 * It can also deal with more than one pluggable task, but I don't see a reason 
 * for this.
 * @author ece
 */
public abstract class EventProcessor<TaskType> {
    public abstract void process(Event event);

    protected TaskType getPluggableTask(Integer entityId, Integer taskCategoryId) {
        try {
            PluggableTaskManager taskManager =
                new PluggableTaskManager(entityId,
                taskCategoryId);
            return  (TaskType) taskManager.getNextClass();
        } catch (PluggableTaskException e) {
            throw new SessionInternalError(e);
        }
    }

    public String toString() {
        return this.getClass().getName();
    }
}
