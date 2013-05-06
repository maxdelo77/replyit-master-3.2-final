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

package com.sapienter.jbilling.server.pluggableTask;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.log4j.Logger;

import com.sapienter.jbilling.common.FormatLogger;

/**
 * Stateful factory alarm class for ProcessorEmailAlarmTask.
 * Decides if emails should be sent.
 * @author Lucas Pickstone
 */
public class ProcessorEmailAlarm {
    private static HashMap alarms = new HashMap();

    private int failedCounter;
    private Queue times;          // holds queue of failure times
    private long lastEmailTime;   // last time email was sent

    private FormatLogger log = new FormatLogger(Logger.getLogger(ProcessorEmailAlarm.class));

    // Constructor
    public ProcessorEmailAlarm() {
        failedCounter = 0;
        times = new LinkedList();
        lastEmailTime = 0;
    }

    // Factory method to get existing alarms or create a new one
    // for each processor and entityid pair.
    public static ProcessorEmailAlarm getAlarm(String processorName, 
                                               Integer entityId) {
        ProcessorEmailAlarm alarm 
                = (ProcessorEmailAlarm) alarms.get(processorName + entityId);
        if (alarm == null) {
            alarm = new ProcessorEmailAlarm();
            alarms.put(processorName + entityId, alarm);
        }
        return alarm;
    }

    // Returns true if email should be sent
    public boolean fail(int failedLimit, int failedTime, 
                        int timeBetAlarms) {
        failedTime *= 1000; // convert seconds to milliseconds
        failedCounter++;
        long currentTime = (new Date()).getTime();

        // add time to queue
        times.offer(new Long(currentTime));
        // remove any old times no longer needed
        if (times.size() > failedLimit) {
             times.remove();
        }

        // If enough fails counted, check that they occurred within 
        // a period of time specified by failedTime.
        if (failedCounter >= failedLimit) {
            long earliestTime = ((Long) times.peek()).longValue();
            if (currentTime - earliestTime <= failedTime 
                    && canSendEmail(timeBetAlarms)) {
                lastEmailTime = currentTime;
                return true;
            }
        }
        return false;
    }

    // Returns true if email should be sent
    public boolean unavailable(int timeBetAlarms) {
        if (canSendEmail(timeBetAlarms)) {
            lastEmailTime = (new Date()).getTime();
            return true;
        } else {
            return false;
        }
    }

    public void successful() {
        if (failedCounter != 0) {
            failedCounter = 0;
            times = new LinkedList();
        }
    }

    public int getFailedCounter() {
        return failedCounter;
    }

    // Returns true if enought time has elapsed for next alarm.
    private boolean canSendEmail(int timeBetAlarms) {
        long currentTime = System.currentTimeMillis();
        return (currentTime - lastEmailTime > timeBetAlarms * 1000L);
    }
}
