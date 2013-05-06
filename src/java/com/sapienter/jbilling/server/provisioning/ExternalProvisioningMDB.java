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

package com.sapienter.jbilling.server.provisioning;

import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.log4j.Logger;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.common.SessionInternalError;
import com.sapienter.jbilling.server.util.Context;

/**
 * Receives messages from the provisioning commands rules task. Calls
 * the external provisioning logic through the provisioning session 
 * bean so it runs in a transaction. Configured in jbilling-jms.xml.
 */
public class ExternalProvisioningMDB implements MessageListener {

    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(
            ExternalProvisioningMDB.class));

    public void onMessage(Message message) {
        try {
            LOG.debug("Received a message");

            // use a session bean to make sure the processing is done in 
            // a transaction
            IProvisioningProcessSessionBean provisioning = 
                    (IProvisioningProcessSessionBean) Context.getBean(
                    Context.Name.PROVISIONING_PROCESS_SESSION);

            provisioning.externalProvisioning(message);
        } catch (Exception e) {
            throw new SessionInternalError(e);
        }
    }
}
