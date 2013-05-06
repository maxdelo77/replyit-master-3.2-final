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
 * Interface for External Provisioning pluggable tasks.
 */
public interface IExternalProvisioning {
    /**
     * Receives a UUID and command string. Returns results in a map.
     */
    public Map<String, Object> sendRequest(String id, String command) 
            throws TaskException;

    /**
     * Returns the id of the task (used for command mapping).
     */
    public String getId();
}
