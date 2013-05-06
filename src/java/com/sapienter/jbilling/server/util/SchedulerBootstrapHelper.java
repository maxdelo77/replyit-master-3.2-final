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

package com.sapienter.jbilling.server.util;

import com.sapienter.jbilling.client.process.JobScheduler;
import com.sapienter.jbilling.client.process.Trigger;
import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskException;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskManager;
import com.sapienter.jbilling.server.process.task.IScheduledTask;
import com.sapienter.jbilling.server.user.db.CompanyDAS;
import com.sapienter.jbilling.server.user.db.CompanyDTO;
import org.apache.log4j.Logger;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

/**
 * Spring bean that bootstraps jBilling services on application start.
 *
 * @author Brian Cowdery
 * @since 22-09-2010
 */
public class SchedulerBootstrapHelper {
    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(SchedulerBootstrapHelper.class));

    /**
     * Schedule all core jBilling batch processes.
     */
    private void scheduleBatchJobs() {
        // todo: refactor "Trigger" into separate scheduled Job classes.
        Trigger.Initialize();
    }

    /**
     * Schedule all configured {@link IScheduledTask} plug-ins for each entity.
     */
    private void schedulePluggableTasks() {
        JobScheduler scheduler = JobScheduler.getInstance();
        try {
            for (CompanyDTO entity : new CompanyDAS().findEntities()) {
                PluggableTaskManager<IScheduledTask> manager =
                        new PluggableTaskManager<IScheduledTask>
                                (entity.getId(), com.sapienter.jbilling.server.util.Constants.PLUGGABLE_TASK_SCHEDULED);

                LOG.debug("Processing %s scheduled tasks for entity %s", manager.getAllTasks().size(), entity.getId());
                for (IScheduledTask task = manager.getNextClass(); task != null; task = manager.getNextClass()) {
                    try {
                        if(task.getJobDetail() != null && task.getTrigger() != null){
                            scheduler.getScheduler().scheduleJob(task.getJobDetail(), task.getTrigger());
                            LOG.debug("Scheduled: [" + task.getTaskName() + "]");
                        }
                    } catch (PluggableTaskException e) {
                        LOG.warn("Failed to schedule pluggable task [%s]", task.getTaskName());
                    } catch (SchedulerException e) {
                        LOG.warn("Failed to schedule pluggable task [%s]", task.getTaskName());
                    }                    
                }
            }
        } catch (PluggableTaskException e) {
            LOG.error("Exception occurred scheduling pluggable tasks.", e);
        }
    }
    
	/**
	 * Reschedule a jBilling IScheduledTask after it has been saved.
	 * @param task
	 * @author Vikas Bodani
	 */
	public void rescheduleJob(IScheduledTask task) throws Exception {
		LOG.debug("Rescheduling instance of: %s", task.getClass().getName());
		if (null != task) {
			LOG.debug("Task Name: %s", task.getTaskName());
			try {
				Scheduler sd = JobScheduler.getInstance().getScheduler();
				boolean found = unScheduleExisting(task);
				// schedule new plugin if not found, no need to restart jbilling
				// then
				if (!found) {
					LOG.debug("This is a new scheduled task.");
				}
				LOG.debug("scheduling %s", task.getTaskName());
				sd.scheduleJob(task.getJobDetail(), task.getTrigger());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
    
	/**
	 * Reschedule a jBilling IScheduledTask after it has been saved.
	 * @param task
	 * @author Vikas Bodani
	 */
	public boolean unScheduleExisting(IScheduledTask task) throws Exception {
		LOG.debug("Unscheduling instance of: %s", task.getClass().getName());
		boolean found = false;
		if (null != task) {
			Scheduler sd = JobScheduler.getInstance().getScheduler();
			String[] triggerGrps;
			String[] triggers;
			try {
				triggerGrps = sd.getTriggerGroupNames();
				for (String stTriggerGrp : triggerGrps) {
					LOG.debug("Trigger Group Name: %s", stTriggerGrp);
					triggers = sd.getTriggerNames(stTriggerGrp);

					for (String stTrigger : triggers) {
						LOG.debug("Trigger Name : %s", stTrigger);

						if (stTrigger.equals(task.getTaskName())) {
							found = true;
							LOG.debug("unscheduling %s", stTrigger);
							sd.unscheduleJob(stTrigger, stTriggerGrp);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return found;
	}
    
}
