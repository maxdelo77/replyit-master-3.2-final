package com.sapienter.jbilling.server.pluggableTask;

import com.sapienter.jbilling.server.process.task.AbstractCronTask;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Calendar;

public class TutorialCronScheduledTask extends AbstractCronTask {
    private static final Logger LOG = Logger.getLogger(TutorialCronScheduledTask.class);

    public String getTaskName() {
        return "Tutorial Cron Scheduled Task: " + getScheduleString();
    }

    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        LOG.debug("[TutorialCronScheduledTask] - " + Calendar.getInstance().getTime());
    }

    public String getParameter(String key) throws JobExecutionException {
        String value = (String) parameters.get(key);
        if (value == null || value.trim().equals(""))
            throw new JobExecutionException("parameter '" + key + "' cannot be blank!");
        return value;
    }
}
