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

import java.text.ParseException;

import org.quartz.CronTrigger;
import org.quartz.Scheduler;

import com.sapienter.jbilling.server.pluggableTask.admin.ParameterDescription;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskException;

/**
 * Abstract task that contains all the plumbing necessary to construct a CronTrigger for
 * scheduling. This class will default to a daily task that will execute at 12:00 noon.
 *
 * Plug-in parameters:
 *
 *      cron_exp        Cron expression for the scheduled task
 *
 *
 * @link http://www.quartz-scheduler.org/docs/tutorials/crontrigger.html
 *
 * @author Brian Cowdery
 * @since 02-02-2010
 */
public abstract class AbstractCronTask extends ScheduledTask {

    protected static final ParameterDescription PARAM_CRON_EXPRESSION = 
    	new ParameterDescription("cron_exp", false, ParameterDescription.Type.STR);
    
	
	//initializer for pluggable params
    { 
    	descriptions.add(PARAM_CRON_EXPRESSION);
    }
    
    protected static final String DEFAULT_CRON_EXPRESSION = "0 0 12 * * ?"; // 12:00 noon every day

    public CronTrigger getTrigger() throws PluggableTaskException {
        CronTrigger trigger = new CronTrigger(getTaskName(), Scheduler.DEFAULT_GROUP);
        
        String expression = getCronExpression();
        try {
            trigger.setCronExpression(expression);
        } catch (ParseException e) {
            throw new PluggableTaskException("Invalid cron expression: " + expression);
        }

        trigger.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);

        return trigger;
    }
    
    /**
     * Returns the configured cron expression for this pluggable task. The cron
     * expression defines how often, and when this trigger will be executed.
     *
     * Defaults to "0 0 12 * * ?" (12:00 noon every day)
     *
     * @return cron expression string
     */
    public String getCronExpression() {
        return getParameter(PARAM_CRON_EXPRESSION.getName(), DEFAULT_CRON_EXPRESSION);
    }

    public String getScheduleString() {
        return getCronExpression();
    }

    protected String getParameter(String key, String defaultValue) {
        Object value = parameters.get(key);
        return value != null ? (String) value : defaultValue;
    }
}
