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

/*
 * Created on Apr 14, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sapienter.jbilling.server.pluggableTask;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.server.notification.NotificationBL;
import com.sapienter.jbilling.server.payment.PaymentDTOEx;
import com.sapienter.jbilling.server.pluggableTask.admin.ParameterDescription;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskException;
import com.sapienter.jbilling.server.user.UserBL;
import com.sapienter.jbilling.server.util.Constants;

/**
 * @author Emil
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PaymentEmailAuthorizeNetTask extends PaymentAuthorizeNetTask {
	
	
	// pluggable task parameters names
    public static final ParameterDescription PARAMETER_EMAIL_ADDRESS = 
        new ParameterDescription("email_address", true, ParameterDescription.Type.STR);
    
    public static final List<ParameterDescription> descriptions = new ArrayList<ParameterDescription>() {
        { 
        	descriptions.add(PARAMETER_EMAIL_ADDRESS); 
        }
    };

	
    public boolean process(PaymentDTOEx paymentInfo) 
            throws PluggableTaskException {
        FormatLogger log = new FormatLogger(Logger.getLogger(PaymentEmailAuthorizeNetTask.class));
        boolean retValue = super.process(paymentInfo);
        String address = (String) parameters.get(PARAMETER_EMAIL_ADDRESS.getName());
        try {
            UserBL user = new UserBL(paymentInfo.getUserId());
            String message;
            if (new Integer(paymentInfo.getPaymentResult().getId()).equals(Constants.RESULT_OK)) {
                message = "payment.success";
            } else {
                message = "payment.fail";
            }
            String params[] = new String[6];
            params[0] = paymentInfo.getUserId().toString();
            params[1] = user.getEntity().getUserName();
            params[2] = paymentInfo.getId() + "";
            params[3] = paymentInfo.getAmount().toString();
            if (paymentInfo.getAuthorization() != null) {
                params[4] = paymentInfo.getAuthorization().getTransactionId();
                params[5] = paymentInfo.getAuthorization().getApprovalCode();
            } else {
                params[4] = "Not available";
                params[5] = "Not available";
            }
            log.debug("Bkp 6 " + params[0] + " " + params[1] + " " + params[2] + " " + params[3] + " " + params[4] + " " + params[5] + " ");
            NotificationBL.sendSapienterEmail(address, 
                    user.getEntity().getEntity().getId(), message, null, 
                    params);
        } catch (Exception e) {
            
            log.warn("Cant send receit email");
        }
        
        return retValue;
    }
}
