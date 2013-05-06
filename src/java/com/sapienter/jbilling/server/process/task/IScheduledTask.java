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
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.Trigger;

/**
 * Scheduled tasks that will be added to the Quartz Scheduler instance at application start up. All
 * Scheduled tasks must return a valid JobDetail and Trigger item for scheduling, but you may use
 * covariant return types in implementing classes to use a specific type of Trigger.
 *
 * This task is implemented in 2 separate abstract base classes to make adding new IScheduledTask
 * plug-ins easier. These are the {@link AbstractCronTask} and the {@link AbstractSimpleScheduledTask}
 * which provide (respectively) Quartz CronTrigger and StandardTrigger configuration. 
 *
 * @link http://www.quartz-scheduler.org/docs/
 *
 * @author Brian Cowdery
 * @since 02-02-2010
 */
public interface IScheduledTask extends Job {
    public String getTaskName();
    public JobDetail getJobDetail() throws PluggableTaskException;
    public Trigger getTrigger() throws PluggableTaskException;
}
