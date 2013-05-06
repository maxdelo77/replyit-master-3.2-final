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

package com.sapienter.jbilling.server.process.task;


import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskException;
import org.quartz.Trigger;

/**
 * This abstract class is used to create tasks to be executed in a chain.
 * No parameter is required, all the configuration must be done in the first task of the chain,
 * and it shouldn't extend from this class.
 *
 * @author Oscar Bidabehere
 * @since 03-09-2012
 */
public abstract class AbstractChainableScheduleTask extends ScheduledTask {
    @Override
    public String getScheduleString () {
        return null;
    }

    public Trigger getTrigger () throws PluggableTaskException {
        return null;
    }
}
