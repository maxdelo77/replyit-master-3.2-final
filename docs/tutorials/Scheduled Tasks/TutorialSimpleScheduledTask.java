package com.sapienter.jbilling.server.pluggableTask;

import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskException;
import com.sapienter.jbilling.server.process.task.AbstractBackwardSimpleScheduledTask;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SimpleTrigger;

import java.util.Calendar;
import java.util.concurrent.atomic.AtomicBoolean;

public class TutorialSimpleScheduledTask extends AbstractBackwardSimpleScheduledTask {
    private static final Logger LOG = Logger.getLogger(TutorialSimpleScheduledTask.class);
    private static final AtomicBoolean running = new AtomicBoolean(false);

    public String getTaskName() {
        return "Tutorial Simple Scheduled Task: " + getScheduleString();
    }

    public void execute(JobExecutionContext context) throws JobExecutionException {
        super.execute(context);//_init(context);

        if (running.compareAndSet(false, true)) {
            LOG.debug("[TutorialSimpleScheduledTask] - " + Calendar.getInstance().getTime());
            running.set(false);
        } else {
            LOG.warn("Failed to trigger tutorial simple process at " + context.getFireTime()
                    + ", another process is already running.");
        }
    }

    /**
     * Returns the scheduled trigger for the mediation process. If the plug-in is missing
     * the {@link com.sapienter.jbilling.server.process.task.AbstractSimpleScheduledTask}
     * parameters use the the default jbilling.properties process schedule instead.
     *
     * @return mediation trigger for scheduling
     * @throws com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskException
     *          thrown if properties or plug-in parameters could not be parsed
     */
    @Override
    public SimpleTrigger getTrigger() throws PluggableTaskException {
        SimpleTrigger trigger = super.getTrigger();

        // trigger start time and frequency using jbilling.properties unless plug-in
        // parameters have been explicitly set to define the mediation schedule
        if (useProperties()) {
            LOG.debug("Scheduling tutorial process from jbilling.properties ...");
            trigger = setTriggerFromProperties(trigger);
        } else {
            LOG.debug("Scheduling tutorial process using plug-in parameters ...");
        }

        return trigger;
    }
}
