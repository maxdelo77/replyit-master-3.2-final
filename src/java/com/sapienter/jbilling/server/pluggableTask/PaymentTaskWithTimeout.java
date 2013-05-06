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

import com.sapienter.jbilling.server.pluggableTask.admin.ParameterDescription;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskDTO;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskException;

public abstract class PaymentTaskWithTimeout extends PaymentTaskBase {
    private int myTimeout;

    public static final ParameterDescription PARAMETER_TIMEOUT =
            new ParameterDescription("timeout_sec", false, ParameterDescription.Type.STR);

    //initializer for pluggable params
    {
        descriptions.add(PARAMETER_TIMEOUT);
    }

    @Override
    public void initializeParamters(PluggableTaskDTO task)
            throws PluggableTaskException {

        super.initializeParamters(task);

        String timeoutText = getOptionalParameter(PARAMETER_TIMEOUT.getName(), "10");
        try {
            myTimeout = Integer.parseInt(timeoutText);
        } catch (NumberFormatException e) {
            throw new PluggableTaskException(""
                    + "Integer expected for parameter: " + PARAMETER_TIMEOUT.getName()
                    + ", actual: " + timeoutText);
        }
    }

    protected int getTimeoutSeconds() {
        return myTimeout;
    }
}
