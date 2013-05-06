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

import java.io.InputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.SocketException;
import java.util.Map;

import org.apache.commons.net.telnet.TelnetClient;
import org.apache.log4j.Logger;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.server.pluggableTask.TaskException;

/**
 * Telnet implementation of CAI communication.
 */
public class TelnetCommunication implements IExternalCommunication {
    public static final String PARAM_TELNET_SERVER = "telnet_server";
    public static final String PARAM_TELNET_PORT = "telnet_port";
    public static final int PARAM_TELNET_PORT_DEFAULT = 23;
    public static final String PARAM_TELNET_USERNAME = "telnet_username";
    public static final String PARAM_TELNET_PASSWORD = "telnet_password";

    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(
            TelnetCommunication.class));

    private static final String prompt = "Enter command: ";

    private TelnetClient telnet = null;
    private InputStream in = null;
    private PrintStream out = null;

    /**
     * Connects to the CAI system.
     */
    public void connect(Map<String, String> parameters) throws TaskException {
        // try to get server parameters
        String telnetServer = getParameter(PARAM_TELNET_SERVER, parameters);

        int telnetPort;
        String value = parameters.get(PARAM_TELNET_PORT);
        if (value != null) {
            telnetPort = Integer.parseInt(value);
        } else {
            telnetPort = PARAM_TELNET_PORT_DEFAULT;
        }

        String telnetUsername = getParameter(PARAM_TELNET_USERNAME, parameters);
        String telnetPassword = getParameter(PARAM_TELNET_PASSWORD, parameters);

        // Connect to the specified server
        try {
            telnet = new TelnetClient();

            // connect
            LOG.debug("Connecting to server: " + telnetServer + " on port: " +
                    telnetPort);
            telnet.connect(telnetServer, telnetPort);

            // Get input and output stream references
            in = telnet.getInputStream();
            out = new PrintStream(telnet.getOutputStream());

            // Login the user
            LOG.debug("Logging in");
            readUntil("login: ");
            write(telnetUsername);
            readUntil("Password: ");
            write(telnetPassword);

            // Advance to a prompt
            LOG.debug("Reading up to prompt.");
            readUntil(prompt);
            LOG.debug("Prompt found. Ready.");
        } catch (IOException ioe) {
            throw new TaskException(ioe);
        }
    }

    /**
     * Sends the command string to the CAI system.
     */
    public String send(String command) throws TaskException {
        try {
            write(command);
            readUntil("\n"); // read past echo 
            String result = readUntil(prompt);
            // drop trailing '\n'
            return result.substring(0, result.length() - 1);
        } catch (IOException e) {
            throw new TaskException(e);
        }
    }

    /**
     * Closes the connection.
     */
    public void close() throws TaskException {
        try {
            telnet.disconnect();
        } catch (IOException ioe) {
            throw new TaskException(ioe);
        }
    }

    /**
     * Reads input stream until the given pattern is reached. The 
     * pattern is discarded and what was read up until the pattern is
     * returned.
     */
    private String readUntil(String pattern) throws IOException {
        char lastChar = pattern.charAt(pattern.length() - 1);
        StringBuilder sb = new StringBuilder();
        int c;

        while((c = in.read()) != -1) {
            char ch = (char) c;
            //System.out.print(ch);
            sb.append(ch);
            if(ch == lastChar) {
                String str = sb.toString();
                if(str.endsWith(pattern)) {
                    return str.substring(0, str.length() - 
                            pattern.length());
                }
            }
        }

        return null;
    }

    /**
     * Writes the value to the output stream.
     */
    private void write(String value) {
        out.println(value);
        out.flush();
        //System.out.println(value);
    }

    /**
     * Helper method to get values from parameter map. 
     */
    private String getParameter(String parameter, 
            Map<String, String>  parameters) throws TaskException { 
        String value = parameters.get(parameter);
        if (value == null) {
            throw new TaskException("No '" + parameter + "' plug-in " +
                    "parameter found.");
        }
        return value;
    }
}
