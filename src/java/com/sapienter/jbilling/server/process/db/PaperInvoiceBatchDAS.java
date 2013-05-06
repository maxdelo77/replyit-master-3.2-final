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


package com.sapienter.jbilling.server.process.db;

import com.sapienter.jbilling.server.util.db.AbstractDAS;

/**
 *
 * @author emilc
 */
public class PaperInvoiceBatchDAS extends AbstractDAS<PaperInvoiceBatchDTO> {
    public PaperInvoiceBatchDTO create(Integer totalInvoices, Integer isSelfManaged) {
        PaperInvoiceBatchDTO dto = new PaperInvoiceBatchDTO(0, totalInvoices, isSelfManaged);
        return save(dto);
    }

}
