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
package com.sapienter.jbilling.client.process;

import org.apache.log4j.Logger;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

import com.sapienter.jbilling.common.FormatLogger;

/**
 * Singleton wrapper to provide easy access to the Quartz Scheduler. Used to schedule
 * all of jBilling's batch processes and {@link com.sapienter.jbilling.server.process.task.IScheduledTask}
 * plug-ins.
 *
 * @author Brian Cowdery
 * @since 02-02-2010
 */
public class JobScheduler {
    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(JobScheduler.class));

    private static JobScheduler instance = null;
    private SchedulerFactory factory = null;
    private Scheduler scheduler = null;

    private JobScheduler() {
        factory = new StdSchedulerFactory();
    }

    public static JobScheduler getInstance() {
        if (instance == null)
            instance = new JobScheduler();
        return instance;
    }

    public Scheduler getScheduler() {
        if (scheduler == null) {
            try {
                scheduler = factory.getScheduler();
            } catch (SchedulerException e) {
                LOG.error("Exception occurred retrieving the scheduler instance.", e);
            }
        }
        return scheduler;
    }

    public void start() {
        try {
            getScheduler().start();
        } catch (SchedulerException e) {
            LOG.error("Exception occurred starting the scheduler.", e);
        }
    }

    public void shutdown() {
        try {
            getScheduler().shutdown();
        } catch (SchedulerException e) {
            // swallow
        }
    }
}
