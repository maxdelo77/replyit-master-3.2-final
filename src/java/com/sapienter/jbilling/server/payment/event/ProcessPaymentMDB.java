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
package com.sapienter.jbilling.server.payment.event;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.server.invoice.db.InvoiceDAS;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.log4j.Logger;

import com.sapienter.jbilling.server.process.IBillingProcessSessionBean;
import com.sapienter.jbilling.server.util.Context;

/*
 * The configuration needs to be done specifically for each installation/scenario
 * using the file jbilling-jms.xml
 */
public class ProcessPaymentMDB implements MessageListener {
    
    private final FormatLogger LOG = new FormatLogger(Logger.getLogger(ProcessPaymentMDB.class));

    public void onMessage(Message message) {
        try {
            LOG.debug("Processing message. Processor " + message.getStringProperty("processor") + 
                    " entity " + message.getIntProperty("entityId") + " by " + this.hashCode());
            MapMessage myMessage = (MapMessage) message;
            
            // use a session bean to make sure the processing is done in one transaction
            IBillingProcessSessionBean process = (IBillingProcessSessionBean) 
                    Context.getBean(Context.Name.BILLING_PROCESS_SESSION);

            String type = message.getStringProperty("type"); 
            if (type.equals("payment")) {
                LOG.debug("Now processing asynch payment:" +
                        " processId: " + myMessage.getInt("processId") +
                        " runId:" + myMessage.getInt("runId") +
                        " invoiceId:" + myMessage.getInt("invoiceId"));
                Integer invoiceId = (myMessage.getInt("invoiceId") == -1) ? null : myMessage.getInt("invoiceId");
                if (invoiceId != null) {
                    // lock it
                    new InvoiceDAS().findForUpdate(invoiceId);
                }
                process.processPayment(
                        (myMessage.getInt("processId") == -1) ? null : myMessage.getInt("processId"),
                        (myMessage.getInt("runId") == -1) ? null : myMessage.getInt("runId"),
                        invoiceId);
                LOG.debug("Done");
            } else if (type.equals("ender")) {
                process.endPayments(myMessage.getInt("runId"));
            } else {
                LOG.error("Can not process message of type " + type);
            }
        } catch (Exception e) {
            LOG.error("Generating payment", e);
        }
    }

}
