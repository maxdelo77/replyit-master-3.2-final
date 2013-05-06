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

package com.sapienter.jbilling.server.provisioning.task;

import java.util.Map;

import com.sapienter.jbilling.server.pluggableTask.TaskException;

/**
 * Interface for communicating commands to external provisioning 
 * systems. Some possible implementations might be telnet, tcp/ip, 
 * X.25, or test dummy. 
 */
public interface IExternalCommunication {
    /**
     * Connects to external provisioning system. The 
     * ExternalProvisioning pluggable task can pass in its parameters.
     */
    public void connect(Map<String, String> parameters) throws TaskException;

    /**
     * Sends the command to the external provisioning system.
     */
    public String send(String command) throws TaskException;

    /**
     * Closes the connection to the external provisioning system.
     */
    public void close() throws TaskException;
}
