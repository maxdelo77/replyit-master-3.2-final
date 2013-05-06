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

import org.apache.log4j.Logger;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.server.pluggableTask.TaskException;

/**
 * Dummy CAI communication class for testing CAIProvisioningTask.
 */
public class TestCommunication implements IExternalCommunication {
    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(TestCommunication.class));

    public void connect(Map<String, String> parameters) throws TaskException {
        LOG.debug("Connect");
    }

    public String send(String command) throws TaskException {
        LOG.debug("Command: " + command);

        // return success (without a TRANSID) for login/logout commands
        if (command.startsWith("LOGIN:") || command.equals("LOGOUT;")) {
            return "RESP:0;";
        }

        // wait for command rules task transaction to complete
        //pause(2000);

        int transidIndexStart = command.indexOf(':', command.indexOf(':') + 1) 
                + 9;
        int transidIndexEnd = command.indexOf(":", transidIndexStart);
        String transid = command.substring(transidIndexStart, transidIndexEnd);

        // return success as well as the input fields
        String response = "RESP:TRANSID," + transid + ":0" + 
                command.substring(transidIndexEnd, command.length());
        LOG.debug("Response: " + response);
        return response;
    }

    public void close() throws TaskException {
        LOG.debug("Close");
    }

    private void pause(long t) {
        LOG.debug("TestCommunication: pausing for " + t + " ms...");

        try {
            Thread.sleep(t);
        } catch (InterruptedException e) {
        }
    }
}
