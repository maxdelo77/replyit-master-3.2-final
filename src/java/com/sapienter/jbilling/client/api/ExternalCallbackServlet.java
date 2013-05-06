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
 * Created on Jan 22, 2005
 *
 */
package com.sapienter.jbilling.client.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.server.payment.IPaymentSessionBean;
import com.sapienter.jbilling.server.util.Context;

/**
 * @author Emil
 *
 */
public class ExternalCallbackServlet extends HttpServlet {
    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(ExternalCallbackServlet.class));
    public void doPost(HttpServletRequest request, 
            HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            LOG.debug("callback received");
            
            if (request.getParameter("caller") == null ||
                    !request.getParameter("caller").equals("paypal")) {
                LOG.debug("caller not supported");
                return;
            }
            
            if (!verifyTransactionType(request.getParameter("txn_type"))) {
                LOG.debug("transaction is type %s ignoring", request.getParameter("txn_type"));
                return;
            }
            
            // go over the parameters, making my string for the validation
            // call to paypal
            String validationStr = "cmd=_notify-validate";
            Enumeration parameters = request.getParameterNames();
            while (parameters.hasMoreElements()) {
                String parameter = (String) parameters.nextElement();
                String value = request.getParameter(parameter);
                LOG.debug("parameter : %s value : %s", parameter, value);
                validationStr = validationStr + "&" + parameter + "=" + 
                    URLEncoder.encode(value);
            }
            
            LOG.debug("About to call paypal for validation.  Request %s", validationStr);
            URL u = new URL("https://www.paypal.com/cgi-bin/webscr");
            URLConnection uc = u.openConnection();
            uc.setDoOutput(true);
            uc.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            PrintWriter pw = new PrintWriter(uc.getOutputStream());
            pw.println(validationStr);
            pw.close();
    
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(uc.getInputStream()));
            String res = in.readLine();
            in.close();
    
            //check notification validation
            LOG.debug("Validation result is %s", res);
            if(res.equals("VERIFIED")) {
            //if(res.equals("INVALID")) { // only for testing
                LOG.debug("ok");
                String invoiceNumber = request.getParameter("invoice");
                String paymentStatus = request.getParameter("payment_status");
                String paymentAmount = request.getParameter("mc_gross");
                String paymentCurrency = request.getParameter("mc_currency");
                String receiverEmail = request.getParameter("receiver_email");
                String userEmail = request.getParameter("payer_email");
                String userIdStr = request.getParameter("custom");
                
                if (paymentStatus == null || !paymentStatus.equalsIgnoreCase(
                        "completed")) {
                    LOG.debug("payment status is %s Rejecting", paymentStatus);
                } else { 
                    try {
                        IPaymentSessionBean paymentSession = 
                                (IPaymentSessionBean) Context.getBean(
                                Context.Name.PAYMENT_SESSION);
                        Integer invoiceId = getInt(invoiceNumber);
                        BigDecimal amount = new BigDecimal(paymentAmount);
                        Integer userId = getInt(userIdStr);
                        Boolean result = paymentSession.processPaypalPayment(invoiceId, receiverEmail, amount,
                                                                             paymentCurrency, userId, userEmail);
                        
                        LOG.debug("Finished callback with result %s", result);
                    } catch (Exception e) {
                        LOG.error("Exception processing a paypal callback ", e);
                    }
                   
                }
            }
            else if(res.equals("INVALID")) {
                LOG.debug("invalid");
            }
            else {
                LOG.debug("error");
            }
            LOG.debug("done callback");
        } catch (Exception e) {
            LOG.error("Error processing external callback", e);
        }
    }
    
    private Integer getInt(String str) {
        Integer retValue = null;
        if (str != null && str.length() > 0) {
            try {
                retValue = Integer.parseInt(str);
            } catch (NumberFormatException e) {
                LOG.debug("Invalid int field. %s - ", str, e.getMessage());
            }
        }
        return retValue;
    }
    
    private boolean verifyTransactionType(String type) {
        if (type == null || type.length() == 0) {
            return true;
        } else {
            if (type.equals("subscr_signup") ||
                    type.equals("subscr_cancel") ||
                    type.equals("subscr_failed") ||
                    type.equals("subscr_eot") ||
                    type.equals("subscr_modify")) {
                return false;
            }
            return true;
        }
    }
}
