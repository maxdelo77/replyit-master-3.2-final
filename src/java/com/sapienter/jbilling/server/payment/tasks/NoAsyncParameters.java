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
package com.sapienter.jbilling.server.payment.tasks;

import javax.jms.JMSException;
import javax.jms.MapMessage;

import com.sapienter.jbilling.server.pluggableTask.PluggableTask;
import com.sapienter.jbilling.server.pluggableTask.TaskException;

/**
 * A dummy task that really doesn't add parameters. Useful for those simple cases
 * where concurrent payment processing is not needed.
 *  
 * @author Emiliano Conde
 *
 */
public class NoAsyncParameters extends PluggableTask implements IAsyncPaymentParameters {

    public void addParameters(MapMessage message) throws TaskException {
        try {
            message.setStringProperty("processor", "all");
        } catch (JMSException e) {
            throw new TaskException(e);
        }
    }

}
