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

package com.sapienter.jbilling.server.invoice;

import java.sql.SQLException;
import java.util.Date;

import com.sapienter.jbilling.common.SessionInternalError;
import com.sapienter.jbilling.server.invoice.db.InvoiceDTO;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskException;
import java.util.Set;

/**
 *
 * This is the session facade for the invoices in general. It is a statless
 * bean that provides services not directly linked to a particular operation
 *
 * @author emilc
 **/
public interface IInvoiceSessionBean {

    public InvoiceDTO getInvoice(Integer invoiceId) throws SessionInternalError;

    public void create(Integer entityId, Integer userId,
            NewInvoiceDTO newInvoice) throws SessionInternalError;

    public String getFileName(Integer invoiceId) throws SessionInternalError;

    /**
     * The transaction requirements of this are not big. The 'atom' is 
     * just a single invoice. If the next one fails, it's ok that the
     * previous ones got updated. In fact, they should, since the email
     * has been sent.
     */
    public void sendReminders(Date today) throws SessionInternalError;

    public InvoiceDTO getInvoiceEx(Integer invoiceId, Integer languageId);

    public byte[] getPDFInvoice(Integer invoiceId) throws SessionInternalError;

    public void delete(Integer invoiceId, Integer executorId)
            throws SessionInternalError;

    /**
     * The real path is known only to the web server
     * It should have the token _FILE_NAME_ to be replaced by the generated file
     */
    public String generatePDFFile(java.util.Map map, String realPath) 
            throws SessionInternalError;

    // only for unit tests
    public Set<InvoiceDTO> getAllInvoices(Integer userId);
}    
