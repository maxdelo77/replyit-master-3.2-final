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

package com.sapienter.jbilling.server.notification;

import com.sapienter.jbilling.server.user.db.CustomerDTO;
import com.sapienter.jbilling.server.user.db.UserDAS;
import com.sapienter.jbilling.server.user.partner.db.Partner;
import org.apache.log4j.Logger;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.common.SessionInternalError;
import com.sapienter.jbilling.common.Util;
import com.sapienter.jbilling.server.invoice.InvoiceBL;
import com.sapienter.jbilling.server.notification.db.NotificationMessageArchDAS;
import com.sapienter.jbilling.server.notification.db.NotificationMessageArchDTO;
import com.sapienter.jbilling.server.payment.PaymentBL;
import com.sapienter.jbilling.server.pluggableTask.NotificationTask;
import com.sapienter.jbilling.server.pluggableTask.TaskException;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskManager;
import com.sapienter.jbilling.server.user.UserBL;
import com.sapienter.jbilling.server.user.db.UserDTO;
import com.sapienter.jbilling.server.util.Constants;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional( propagation = Propagation.REQUIRED )
public class NotificationSessionBean implements INotificationSessionBean {

    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(NotificationSessionBean.class));

    
    /**
     * Sends an email with the invoice to a customer.
     * This is used to manually send an email invoice from the GUI
     * @param userId
     * @param invoiceId
     * @return
    */
    public Boolean emailInvoice(Integer invoiceId) 
            throws SessionInternalError {
        Boolean retValue;
        try {
            InvoiceBL invoice = new InvoiceBL(invoiceId);
            UserBL user = new UserBL(invoice.getEntity().getBaseUser());
            Integer entityId = user.getEntity().getEntity().getId();
            Integer languageId = user.getEntity().getLanguageIdField();
            NotificationBL notif = new NotificationBL();
            MessageDTO message = notif.getInvoiceEmailMessage(entityId, 
                    languageId, invoice.getEntity());
            retValue = notify(user.getEntity(), message);
        
        } catch (NotificationNotFoundException e) {
            retValue = new Boolean(false);
        } 
//        catch (FinderException e) {
//            log.error("Exception sending email invoice", e);
//            throw new SessionInternalError(e);
//        } 
        
        return retValue;
    }
    
    /**
     * Sends an email with the Payment information to the customer.
     * This is used to manually send an email Payment notification from the GUI (Show Payments)
     * @param userId
     * @param invoiceId
     * @return
    */
    public Boolean emailPayment(Integer paymentId) 
            throws SessionInternalError {
        Boolean retValue;
        try {
            PaymentBL payment = new PaymentBL(paymentId);
            UserBL user = new UserBL(payment.getEntity().getBaseUser());
            Integer entityId = user.getEntity().getEntity().getId();
            NotificationBL notif = new NotificationBL();
            MessageDTO message = notif.getPaymentMessage(entityId,
                    payment.getDTOEx(user.getEntity().getLanguageIdField()),
                    payment.getEntity().getPaymentResult().getId());
            retValue = notify(user.getEntity(), message);
        } catch (NotificationNotFoundException e) {
            retValue = new Boolean(false);
        } 
        
        return retValue;
    }

    public void notify(Integer userId, MessageDTO message) 
            throws SessionInternalError {

        try {
            UserBL user = new UserBL(userId);
            notify(user.getEntity(), message);            
        } catch (Exception e) {
            throw new SessionInternalError("Problems getting user entity" +
                    " for id " + userId + "." + e.getMessage());
        } 
    }
    
   /**
    * Sends a notification to a user. Returns true if no exceptions were
    * thrown, otherwise false. This return value could be considered
    * as if this message was sent or not for most notifications (emails).
    */
    public Boolean notify(UserDTO user, MessageDTO message) 
            throws SessionInternalError {
        
        Boolean retValue = new Boolean(true);
        try {
            // verify that the message is good
            if (message.validate() == false) {
                throw new SessionInternalError("Invalid message");
            }
            // parse this message contents with the parameters
            MessageSection sections[] = message.getContent();
            for (int f=0; f < sections.length; f++) {
                MessageSection section = sections[f];
                section.setContent(NotificationBL.parseParameters(
                        section.getContent(), message.getParameters()));
            }
            // now do the delivery with the pluggable tasks
            PluggableTaskManager taskManager =
                new PluggableTaskManager(
                    user.getEntity().getId(),
                    Constants.PLUGGABLE_TASK_NOTIFICATION);
            NotificationTask task =
                (NotificationTask) taskManager.getNextClass();

            NotificationMessageArchDAS messageHome =
                    new NotificationMessageArchDAS();

            Integer notifiedParentId = 0;
            
            while (task != null) {
                NotificationMessageArchDTO messageRecord =
                        messageHome.create(message.getTypeId(), sections);
                messageRecord.setBaseUser(user);
                try {
                    LOG.debug("Sending notification to user : " + user.getUserName());
                    task.deliver(user, message);

                    if(Integer.valueOf(1).equals(message.getNotifyAdmin())){
                        for (UserDTO admin : new UserDAS().findAdminUsers(user.getEntity().getId())) {
                            LOG.debug("Sending notification to admin : "+admin.getUserName());
                            task.deliver(admin,message);
                        }
                    }
                    if(Integer.valueOf(1).equals(message.getNotifyPartner())){
                        Partner partner = user.getPartner();
                        if(partner!=null) {
                            LOG.debug("Sending notification to partner : "+partner.getBaseUser().getUserName());
                            task.deliver(partner.getBaseUser(),message);
                        }
                    }
                    if(Integer.valueOf(1).equals(message.getNotifyParent())){
                        CustomerDTO customer = user.getCustomer();
                        if (customer != null) {
                            CustomerDTO parent = customer.getParent();
                            if(parent!=null) {
                                LOG.debug("Sending notification to parent : "+parent.getBaseUser().getUserName());
                                task.deliver(parent.getBaseUser(),message);
                                notifiedParentId = parent.getId();
                            }
                        }
                    }
                    if(Integer.valueOf(1).equals(message.getNotifyAllParents())){
                        CustomerDTO customer = user.getCustomer();
                        if (customer != null) {
                            CustomerDTO parent = customer.getParent();
                            do{
                                if(parent!=null){
                                    if(!Integer.valueOf(notifiedParentId).equals(parent.getId())){
                                        LOG.debug("Sending notification to parents : "+parent.getBaseUser().getUserName());
                                        task.deliver(parent.getBaseUser(),message);
                                    }
                                    parent = parent.getParent();
                                }
                            } while(parent!=null);
                        }
                    }
                } catch (TaskException e) {
                    messageRecord.setResultMessage(Util.truncateString(
                            e.getMessage(), 200));
                    LOG.error(e);
                    retValue = new Boolean(false);
                }
                task = (NotificationTask) taskManager.getNextClass();
            }
        } catch (Exception e) {
            LOG.error("Exception in notify", e);
            throw new SessionInternalError(e);
        }   
        
        return retValue;
    }

    public MessageDTO getDTO(Integer typeId, Integer languageId,
            Integer entityId) throws SessionInternalError {
        try {
            NotificationBL notif = new NotificationBL();
            MessageDTO retValue = null;
            int plugInSections = notif.getSections(entityId);
            notif.set(typeId, languageId, entityId);
            if (notif.getEntity() != null) {
                retValue = notif.getDTO();
            } else {
                retValue = new MessageDTO();
                retValue.setTypeId(typeId);
                retValue.setLanguageId(languageId);
                MessageSection sections[] =
                        new MessageSection[plugInSections];
                for (int f = 0; f < sections.length; f++) {
                    sections[f] = new MessageSection(new Integer(f + 1), "");
                }
                retValue.setContent(sections);
            }
            
            if (retValue.getContent().length < plugInSections) {
                // pad any missing sections, due to changes to a new plug-in with more sections
                for (int f = retValue.getContent().length ; f < plugInSections; f++) {
                    retValue.addSection(new MessageSection(new Integer(f + 1), ""));
                }
            } else if (retValue.getContent().length > plugInSections) {
                // remove excess sections 
                retValue.setContentSize(plugInSections);
            }


            return retValue;
        } catch (Exception e) {
            throw new SessionInternalError(e);
        }
    }

    public Integer createUpdate(MessageDTO dto, 
            Integer entityId) throws SessionInternalError {
        try {
            NotificationBL notif = new NotificationBL();
            
            return notif.createUpdate(entityId, dto);
        } catch (Exception e) {
            throw new SessionInternalError(e);
        }
    }

    public String getEmails(Integer entityId, String separator) 
            throws SessionInternalError {
        try {
            NotificationBL notif = new NotificationBL();
            
            return notif.getEmails(separator, entityId);
        } catch (Exception e) {
            throw new SessionInternalError(e);
        }
    }        
}
