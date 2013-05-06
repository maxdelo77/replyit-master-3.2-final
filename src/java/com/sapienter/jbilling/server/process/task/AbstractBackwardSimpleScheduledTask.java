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
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SimpleTrigger;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.common.Util;
import com.sapienter.jbilling.server.order.TimePeriod;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskException;
import com.sapienter.jbilling.server.util.Constants;

/**
 * A simple Scheduled process plug-in, executing the extending process class on a simple schedule.
 *
 * This plug-in accepts the standard {@link AbstractSimpleScheduledTask} plug-in parameters
 * for scheduling. If these parameters are omitted (all parameters are not defined or blank)
 * the plug-in will be scheduled using the jbilling.properties "process.time" and
 * "process.frequency" values, therefore named as 'AbstractBackward' because of this backward
 * compatility in scheduling the same process via the old jbilling.properties files.
 *
 * @see com.sapienter.jbilling.server.process.task.AbstractSimpleScheduledTask
 *
 * @author Vikas Bodani
 * @since 02-08-2010
 */

public abstract class AbstractBackwardSimpleScheduledTask extends
        AbstractSimpleScheduledTask {
    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(AbstractBackwardSimpleScheduledTask.class));
    private static final String PROPERTY_PROCESS_TIME = "process.time";
    private static final String PROPERTY_PROCESS_FREQ = "process.frequency";

    public void execute(JobExecutionContext context) throws JobExecutionException {
        _init(context);
    }

    @Override
    public String getScheduleString() {
        StringBuilder builder = new StringBuilder();

        try {
            builder.append("start: ");
            builder.append(useProperties()
                           ? Util.getSysProp(PROPERTY_PROCESS_TIME)
                           : getParameter(PARAM_START_TIME.getName(), DEFAULT_START_TIME).toString());
            builder.append(", ");

            builder.append("end: ");
            builder.append(getParameter(PARAM_END_TIME.getName(), DEFAULT_END_TIME));
            builder.append(", ");

            Integer repeat = getParameter(PARAM_REPEAT.getName(), DEFAULT_REPEAT);
            builder.append("repeat: ");
            builder.append((repeat == SimpleTrigger.REPEAT_INDEFINITELY ? "infinite" : repeat));
            builder.append(", ");

            builder.append("interval: ");
            builder.append(useProperties()
                           ? Util.getSysProp(PROPERTY_PROCESS_FREQ) + " mins"
                           : getParameter(PARAM_INTERVAL.getName(), DEFAULT_INTERVAL) + " hrs");

        } catch (PluggableTaskException e) {
            LOG.error("Exception occurred parsing plug-in parameters", e);
        }

        return builder.toString();
    }

    protected SimpleTrigger setTriggerFromProperties(SimpleTrigger trigger) throws PluggableTaskException {
    try {
            // set process.time as trigger start time if set
            String start = Util.getSysProp(PROPERTY_PROCESS_TIME);
            if (StringUtils.isNotBlank(start))
                trigger.setStartTime(DATE_FORMAT.parse(start));

            // set process.frequency as trigger repeat interval if set
            String repeat = Util.getSysProp(PROPERTY_PROCESS_FREQ);
            if (StringUtils.isNotBlank(repeat))
                trigger.setRepeatInterval(Long.parseLong(repeat) * 60 * 1000);

        } catch (ParseException e) {
            throw new PluggableTaskException("Exception parsing process.time for schedule", e);
        } catch (NumberFormatException e) {
            throw new PluggableTaskException("Exception parsing process.frequency for schedule", e);
        }
        return trigger;
    }

    /**
     * Returns true if the billing process should be scheduled using values from jbilling.properties
     * or if the schedule should be derived from plug-in parameters.
     *
     * @return true if properties should be used for scheduling, false if schedule from plug-ins
     */
    protected boolean useProperties() {
        return StringUtils.isBlank(parameters.get(PARAM_START_TIME.getName()))
            && StringUtils.isBlank(parameters.get(PARAM_END_TIME.getName()))
            && StringUtils.isBlank(parameters.get(PARAM_REPEAT.getName()))
            && StringUtils.isBlank(parameters.get(PARAM_INTERVAL.getName()));
    }
    
    
    public TimePeriod getTimePeriod() {

    	TimePeriod period = new TimePeriod();
    	Long schedulerIntervalInDays = Long.valueOf(0);

    	try {
    		if (useProperties()) {
    			String schedulerIntervalInMinutes = Util.getSysProp(PROPERTY_PROCESS_FREQ);
    			if (StringUtils.isNotBlank(schedulerIntervalInMinutes)) {
    				schedulerIntervalInDays = TimeUnit.MINUTES.toDays(Integer.valueOf(schedulerIntervalInMinutes));
    			}

    		} else {
    			Integer schedulerIntervalInHours = getParameter(PARAM_INTERVAL.getName(), DEFAULT_INTERVAL);
    			if (schedulerIntervalInHours != null) {
    				schedulerIntervalInDays = TimeUnit.HOURS.toDays(schedulerIntervalInHours);
    			}
    		}
    	}   catch (PluggableTaskException e) {
    		LOG.error("Exception occurred parsing plug-in parameters", e);
    	}

    	period.setUnitId(Constants.PERIOD_UNIT_DAY);
    	period.setValue(schedulerIntervalInDays.compareTo(Long.valueOf(0)) > 0 ? schedulerIntervalInDays
    			.intValue() : 1);

    	return period;
    }
}
