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

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.server.provisioning.task.mmsc.AddCustomerRequest;
import com.sapienter.jbilling.server.provisioning.task.mmsc.DeleteCustomerRequest;
import com.sapienter.jbilling.server.provisioning.task.mmsc.GetCustomerRequest;
import com.sapienter.jbilling.server.provisioning.task.mmsc.GetCustomerResponse;
import com.sapienter.jbilling.server.provisioning.task.mmsc.IMMSCHandlerFacade;
import com.sapienter.jbilling.server.provisioning.task.mmsc.MMSCException_Exception;
import com.sapienter.jbilling.server.provisioning.task.mmsc.MmscFacadeHandlerResponse;
import com.sapienter.jbilling.server.provisioning.task.mmsc.ModifyCustomerRequest;


/**
 * Dummy MMSC communication class for testing MMSCProvisioningTask.
 */
public class TestMMSCCommunication implements IMMSCHandlerFacade {
    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(TestMMSCCommunication.class));


    public MmscFacadeHandlerResponse addCustomer(AddCustomerRequest request)
            throws MMSCException_Exception {
        LOG.debug("Calling Dummy method addCustomer");
        return getResponse(request.getTransactionId());
    }

    public MmscFacadeHandlerResponse modifyCustomer(
            ModifyCustomerRequest request) throws MMSCException_Exception {
        LOG.debug("Calling Dummy method modifyCustomer");
        return getResponse(request.getTransactionId());
    }

    public MmscFacadeHandlerResponse deleteCustomer(
            DeleteCustomerRequest request) throws MMSCException_Exception {
        LOG.debug("Calling Dummy method deleteCustomer");
        return getResponse(request.getTransactionId());
    }

    public GetCustomerResponse getCustomerInfo(GetCustomerRequest request)
            throws MMSCException_Exception {
        return null; // not implemented
    }

    private MmscFacadeHandlerResponse getResponse(String transactionId) {
        MmscFacadeHandlerResponse response = new MmscFacadeHandlerResponse();
        // wait for command rules task transaction to complete
        //pause(2000);

        response.setTransactionId(transactionId);
        response.setStatusCode(MMSCProvisioningTask.STATUS_CODE_OK);
        response.setStatusMessage("Operation Performed Successfully");

        return response;
    }

    private void pause(long t) {
        LOG.debug("TestMMSCCommunication: pausing for " + t + " ms...");

        try {
            Thread.sleep(t);
        } catch (InterruptedException e) {
        }
    }

}
