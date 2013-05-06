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

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.common.Util;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskException;
import com.sapienter.jbilling.server.process.IBillingProcessSessionBean;
import com.sapienter.jbilling.server.util.Context;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SimpleTrigger;

import java.util.Date;

/**
 * AgeingProcessTask
 *
 * @author Brian Cowdery
 * @since 29/04/11
 */
public class AgeingProcessTask extends AbstractBackwardSimpleScheduledTask {

    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(AgeingProcessTask.class));

    private static final String PROPERTY_RUN_AGEING = "process.run_ageing";

    public String getTaskName() {
        return "ageing process: , entity id " + getEntityId() + ", taskId " + getTaskId();
    }

    public void execute(JobExecutionContext context) throws JobExecutionException {
        super.execute(context); // _init(context);

        IBillingProcessSessionBean
                billing = (IBillingProcessSessionBean) Context.getBean(Context.Name.BILLING_PROCESS_SESSION);

        if (Util.getSysPropBooleanTrue(PROPERTY_RUN_AGEING)) {
            LOG.info("Starting ageing for entity " + getEntityId() + " at " + new Date());
            billing.reviewUsersStatus(getEntityId(), new Date());
            LOG.info("Ended ageing at " + new Date());
        }
    }

    /**
     * Returns the scheduled trigger for the ageing process. If the plug-in is missing
     * the {@link com.sapienter.jbilling.server.process.task.AbstractSimpleScheduledTask}
     * parameters use the the default jbilling.properties process schedule instead.
     *
     * @return billing trigger for scheduling
     * @throws com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskException thrown if properties or plug-in parameters could not be parsed
     */
    @Override
    public SimpleTrigger getTrigger() throws PluggableTaskException {
        SimpleTrigger trigger = super.getTrigger();

        // trigger start time and frequency using jbilling.properties unless plug-in
        // parameters have been explicitly set to define the ageing schedule
        if (useProperties()) {
            LOG.debug("Scheduling ageing process from jbilling.properties ...");
            trigger= setTriggerFromProperties(trigger);
        } else {
            LOG.debug("Scheduling ageing process using plug-in parameters ...");
        }

        return trigger;
    }
}
