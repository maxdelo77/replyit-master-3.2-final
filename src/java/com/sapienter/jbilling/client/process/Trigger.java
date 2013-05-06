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

/*
 * Created on Jul 14, 2004
 *
 */
package com.sapienter.jbilling.client.process;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.common.SessionInternalError;
import com.sapienter.jbilling.common.Util;
import com.sapienter.jbilling.server.invoice.IInvoiceSessionBean;
import com.sapienter.jbilling.server.order.IOrderSessionBean;
import com.sapienter.jbilling.server.process.IBillingProcessSessionBean;
import com.sapienter.jbilling.server.provisioning.IProvisioningProcessSessionBean;
import com.sapienter.jbilling.server.user.IUserSessionBean;
import com.sapienter.jbilling.server.util.Context;

/**
 * @author Emil
 *
 */
public class Trigger implements Job {

    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(Trigger.class));

    /**
     * Initialize tool Trigger. Load properties from jbilling.properties and set up Quartz job/trigger
     * 
     *  process.time=YYYYMMDD-HHmm (a full date followed by HH is the hours in 24hs format and mm the minutes).
     *  process.frequency=X (where X is an integer >= 0 and < 1440
     *  
     *  The fist property indicates at what time of the day the trigger has to happen for the very first time. After this first 
     *  run you will need X minutes (specified by 'process.frequency') to run the trigger again. Since only the billing process 
     *  can run more than once per day, we need some logic in the ejbTimout method to verify if the call is the first one of the 
     *  day (where it runs all the services) or not (where it runs only the billing process).
     *  
     *  The first property is optional. If it is not present, or its value is null, then the next trigger will happen at 
     *  startup + minutes indicated in 'process.frequency'.
     *
     */
    public static void Initialize() {
        // Load properties from jbilling.properties
        String time = null;
        String frequency = null;

        try {
            time = Util.getSysProp("process.time");
            frequency = Util.getSysProp("process.frequency");
        } catch (Exception e) {
        // just eat it
        }

        // both null or empty, log one message and return
        if ((time == null || time.length() == 0) && (frequency == null || frequency.length() == 0)) {
            LOG.info("No schedule information found.");
            return;
        }

        Date startTime = null;
        int interval = 0;

        // if process.time absents and frequency is zero, fire every 0:00AM
        GregorianCalendar cal = new GregorianCalendar();
        if ((time == null || time.length() == 0) && "0".equals(frequency)) {
            cal.add(Calendar.DATE, 1);
            startTime = Util.truncateDate(cal.getTime());
            interval = 24 * 60;
        } else if (time == null || time.length() == 0) { // process.time is not present, start at startup + frequency
            try {
                interval = Integer.parseInt(frequency);
            } catch (NumberFormatException e) {
                LOG.debug(e);
                LOG.info("Error:" + e.getMessage() + " Schedule does not start.");

                // Leave
                return;
            }
            cal.add(Calendar.MINUTE, interval);
            startTime = Util.truncateDate(cal.getTime());
        } else { // Its normal one
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd-HHmm");
            try {
                interval = Integer.parseInt(frequency);
                if (interval == 0) {
                    LOG.info("The frequency can not be zero when time is specified.");
                    return;
                }
                startTime = df.parse(time);
            } catch (ParseException e) {
                LOG.debug(e);
                LOG.info("Error:" + e.getMessage() + " Schedule does not start.");
                // Leave
                return;
            } catch (NumberFormatException e) {
                LOG.debug(e);
                LOG.info("Error:" + e.getMessage() + " Schedule does not start.");
                //Leave
                return;
            }
        }

        // set up trigger
        try {
            JobDetail jbillingJob = new JobDetail("jbilling", Scheduler.DEFAULT_GROUP, Trigger.class);

            SimpleTrigger trigger = new SimpleTrigger("jbillingTrigger",
                    Scheduler.DEFAULT_GROUP,
                    startTime,
                    null,
                    SimpleTrigger.REPEAT_INDEFINITELY,
                    interval * 60 * 1000);

            trigger.setMisfireInstruction(SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT);

            JobScheduler.getInstance().getScheduler().scheduleJob(jbillingJob, trigger);
            
        } catch (SchedulerException e) {
            LOG.error("An exception occurred scheduling the jBilling batch processes.",e);
        }
    }

    public static void main(String[] args) {

    }

    public void execute(JobExecutionContext ctx) throws JobExecutionException {
        IBillingProcessSessionBean remoteBillingProcess = null;

        try {
            // get a session for the remote interfaces
            IUserSessionBean remoteUser = (IUserSessionBean) Context.getBean(
                    Context.Name.USER_SESSION);
            IOrderSessionBean remoteOrder = (IOrderSessionBean) Context.getBean(
                    Context.Name.ORDER_SESSION);
            IInvoiceSessionBean remoteInvoice = (IInvoiceSessionBean) 
                    Context.getBean(Context.Name.INVOICE_SESSION);
            IProvisioningProcessSessionBean remoteProvisioningProcess = 
                    (IProvisioningProcessSessionBean) Context.getBean(
                    Context.Name.PROVISIONING_PROCESS_SESSION);
            
            // determine the date for this run
            Date today = Calendar.getInstance().getTime();
            today = Util.truncateDate(today);

            // Determine if this call first time of today
            boolean firstOfToday = true;
            Date lastFire = ctx.getPreviousFireTime();

            if (lastFire == null) { // Should be first time call
                firstOfToday = true;
            } else {
                lastFire = Util.truncateDate(lastFire);
                firstOfToday = lastFire.before(today);
            }


            // now the ageing process
            if (firstOfToday) {
                if (Util.getSysPropBooleanTrue("process.run_partner")) {
                    // now the partner payout process
                    LOG.info("Starting partner process at %s", Calendar.getInstance().getTime());
                    remoteUser.processPayouts(today);
                    LOG.info("Ended partner process at %s", Calendar.getInstance().getTime());
                }

                if (Util.getSysPropBooleanTrue("process.run_order_expire")) {
                    // finally the orders about to expire notification
                    LOG.info("Starting order notification at %s", Calendar.getInstance().getTime());
                    remoteOrder.reviewNotifications(today);
                    LOG.info("Ended order notification at " + Calendar.getInstance().getTime());
                }
                if (Util.getSysPropBooleanTrue("process.run_invoice_reminder")) {
                    // the invoice reminders
                    LOG.info("Starting invoice reminders at %s", Calendar.getInstance().getTime());
                    remoteInvoice.sendReminders(today);
                    LOG.info("Ended invoice reminders at %s", Calendar.getInstance().getTime());
                }
                if (Util.getSysPropBooleanTrue("process.run_cc_expire")) {
                    // send credit card expiration emails
                    LOG.info("Starting credit card expiration at %s", Calendar.getInstance().getTime());
                    remoteUser.notifyCreditCardExpiration(today);
                    LOG.info("Ended credit card expiration at %s", Calendar.getInstance().getTime());
                }
             // run the provisioning process
                if (Util.getSysPropBooleanTrue("process.run_provisioning")) {
                    LOG.info("Running trigger for %s", today);
                    LOG.info("Starting provisioning process at %s", 
                            Calendar.getInstance().getTime());
                    remoteProvisioningProcess.trigger();
                    LOG.info("Ended provisioning process at %s", 
                            Calendar.getInstance().getTime());
                    
                }
            }

        } catch (SessionInternalError e) {
            LOG.debug(e);
        }

    }
}
