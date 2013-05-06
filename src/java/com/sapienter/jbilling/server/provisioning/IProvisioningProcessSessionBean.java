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

import com.sapienter.jbilling.common.SessionInternalError;

/**
 * @author othman
 * 
 *         This is the session facade for the provisioning process and its
 *         related services.
 */
public interface IProvisioningProcessSessionBean {
    public void trigger() throws SessionInternalError;

    public void updateProvisioningStatus(Integer in_order_id,
            Integer in_order_line_id, String result);

    public void updateProvisioningStatus(Integer orderLineId, 
            Integer provisioningStatus);

    /**
     * Runs the external provisioning code in a transation.
     */
    public void externalProvisioning(Message message);
}
