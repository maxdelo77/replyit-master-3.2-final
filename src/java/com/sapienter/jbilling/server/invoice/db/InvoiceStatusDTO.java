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
package com.sapienter.jbilling.server.invoice.db;

import com.sapienter.jbilling.server.util.db.AbstractGenericStatus;
import com.sapienter.jbilling.server.util.Constants;

import javax.persistence.Entity;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Set;
import java.util.HashSet;

@Entity
@DiscriminatorValue("invoice_status")
public class InvoiceStatusDTO extends AbstractGenericStatus implements Serializable {

    private Set<InvoiceDTO> invoiceDTOs = new HashSet<InvoiceDTO>(0);

    public InvoiceStatusDTO() { }

    public InvoiceStatusDTO(int statusValue) {
        this.statusValue = statusValue;
    }

    public InvoiceStatusDTO(int statusValue, Set<InvoiceDTO> invoiceDTOs) {
        this.statusValue = statusValue;
        this.invoiceDTOs = invoiceDTOs;
    }

    @Transient
    protected String getTable() {
        return Constants.TABLE_INVOICE_STATUS;
    }
}
