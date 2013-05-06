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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.server.notification.NotificationBL;
import com.sapienter.jbilling.server.pluggableTask.admin.ParameterDescription;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskDTO;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskException;

/**
 * Sends an email when a payment processor is down.
 * @author Lucas Pickstone
 */
public class ProcessorEmailAlarmTask extends PluggableTask
            implements ProcessorAlarm {

    // pluggable task parameters names
    public static final ParameterDescription PARAMETER_FAILED_LIMIT = 
    	new ParameterDescription("failed_limit", true, ParameterDescription.Type.STR);
    public static final ParameterDescription PARAMETER_FAILED_TIME = 
    	new ParameterDescription("failed_time", true, ParameterDescription.Type.STR);
    public static final ParameterDescription PARAMETER_TIME_BETWEEN_ALARMS = 
    	new ParameterDescription("time_between_alarms", true, ParameterDescription.Type.STR);

    // optional parameter
    public static final ParameterDescription PARAMETER_EMAIL_ADDRESS = 
    	new ParameterDescription("email_address", false, ParameterDescription.Type.STR);

    //initializer for pluggable params
    { 
    	descriptions.add(PARAMETER_FAILED_LIMIT);
        descriptions.add(PARAMETER_FAILED_TIME);
        descriptions.add(PARAMETER_TIME_BETWEEN_ALARMS);
        descriptions.add(PARAMETER_EMAIL_ADDRESS);
    }
    
    
    private String processorName;
    private Integer entityId;
    private ProcessorEmailAlarm alarm;
    
    private int failedLimit;
    private int failedTime;
    private int timeBetweenAlarms;

    private FormatLogger log = new FormatLogger(Logger.getLogger(ProcessorEmailAlarmTask.class));
    
    @Override
    public void initializeParamters(PluggableTaskDTO task) throws PluggableTaskException {
        super.initializeParamters(task);
        failedLimit = Integer.parseInt((String) parameters.get(PARAMETER_FAILED_LIMIT.getName()));
        failedTime = Integer.parseInt((String) parameters.get(PARAMETER_FAILED_TIME.getName()));
        failedTime = Integer.parseInt((String) parameters.get(PARAMETER_TIME_BETWEEN_ALARMS.getName()));
    }

    // Initialisation
    public void init(String processorName, Integer entityId) {
        this.processorName = processorName;
        this.entityId = entityId;
        alarm = ProcessorEmailAlarm.getAlarm(processorName, entityId);
    }

    // Payment processed, but failed/declined.
    public void fail() {
        if (alarm.fail(failedLimit, failedTime, timeBetweenAlarms)) {
            String params[] = new String[4];
            params[0] = processorName;
            params[1] = entityId.toString();
            params[2] = "" + alarm.getFailedCounter();
            params[3] = (new Date()).toString();
            sendEmail("processorAlarm.fail", params);
        }
    }

    // Processor was unavailable.
    public void unavailable() {
        if (alarm.unavailable(timeBetweenAlarms)) {
            String params[] = new String[3];
            params[0] = processorName;
            params[1] = entityId.toString();
            params[2] = (new Date()).toString();
            sendEmail("processorAlarm.unavailable", params);
        }
    }

    // Payment processed and successful.
    public void successful() {
        alarm.successful();
    }

    // Sends email with given messageKey and params.
    private void sendEmail(String messageKey, String[] params) {
        log.debug("Sending alarm email.");

        String address = (String) parameters.get(PARAMETER_EMAIL_ADDRESS.getName());

        try {
            // if email address supplied as parameter, use it,
            if (address != null) {
                NotificationBL.sendSapienterEmail(address, entityId, 
                        messageKey, null, params);
            } 
            // otherwise use the entityId's default address.
            else {
                NotificationBL.sendSapienterEmail(entityId, messageKey, 
                        null, params);
            }
        } catch (Exception e) {
            log.error("Couldn't send email.", e);
        }
    }
    
    private int parseInt(Object object) throws PluggableTaskException {
        if (object instanceof Number){
            return ((Number)object).intValue();
        }
        if (object instanceof String){
            try {
                return Integer.parseInt((String)object);
            } catch (NumberFormatException e){
                //fall through
            }
        }
        throw new PluggableTaskException("Number expected: " + object);
    }
}
