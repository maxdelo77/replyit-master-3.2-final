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

import com.sapienter.jbilling.server.invoice.db.InvoiceDTO;
import com.sapienter.jbilling.server.system.event.Event;

/**
 *
 * @author emilc
 */
public class NewInvoiceEvent implements Event {

    private final InvoiceDTO invoice;
    private final Integer entityId;
    private final Integer userId;

    public NewInvoiceEvent(InvoiceDTO invoice) {
        entityId = invoice.getBaseUser().getEntity().getId();
        userId = invoice.getBaseUser().getId();
        this.invoice = invoice;
    }

    public String getName() {
        return "New invoice event";
    }

    public Integer getEntityId() {
        return entityId;
    }

    /**
     * Warning, the invoice returned is in the hibernate session.
     * Any changes will be reflected in the database.
     * @return
     */
    public InvoiceDTO getInvoice() {
        return invoice;
    }

    public Integer getUserId() {
        return userId;
    }

}
