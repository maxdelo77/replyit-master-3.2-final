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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.server.pluggableTask.PluggableTask;
import com.sapienter.jbilling.server.pluggableTask.TaskException;
import com.sapienter.jbilling.server.pluggableTask.admin.ParameterDescription;

/**
 * Test external provisioning pluggable task. See also 
 * TestExternalProvisioningMDB, ProvisioningTest, 
 * provisioning_commands.drl and jbilling-provisioning.xml. 
 */
public class TestExternalProvisioningTask extends PluggableTask 
        implements IExternalProvisioning {
	public static final ParameterDescription PARAM_ID = 
		new ParameterDescription("id", false, ParameterDescription.Type.STR);
    public static final String PARAM_ID_DEFAULT = "test";

    //initializer for pluggable params
    { 
    	descriptions.add(PARAM_ID);
    }



    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(
            TestExternalProvisioningTask.class));

    public Map<String, Object> sendRequest(String id, String command) 
            throws TaskException {

        // wait for command rules task transaction to complete
        //pause(1000);

        LOG.debug("id: " + id);
        LOG.debug("command: " + command);

        Map<String, Object> response = new HashMap<String, Object>();

        if(command.startsWith("DELETE:THIS:MSISDN,54321")) {
            response.put("result", "fail");
        } else if(command.startsWith("DELETE:THAT:MSISDN,98765")) {
            throw new TaskException("Test Exception");
        } else {
            response.put("result", "success");
        }

        return response;
    }

    public String getId() {
        String id = (String) parameters.get(PARAM_ID.getName());
        if (id != null) {
            return id;
        }
        return PARAM_ID_DEFAULT;
    }

    private void pause(long t) {
        LOG.debug("TestExternalProvisioningTask: pausing for " + t + " ms...");

        try {
            Thread.sleep(t);
        } catch (InterruptedException e) {
        }
    }
}
