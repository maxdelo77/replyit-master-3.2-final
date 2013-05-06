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

import com.sapienter.jbilling.server.item.db.ItemDTO;
import com.sapienter.jbilling.server.util.db.AbstractDAS;

import java.math.BigDecimal;

/**
 * 
 * @author abimael
 * 
 */
public class InvoiceLineDAS extends AbstractDAS<InvoiceLineDTO> {

    public InvoiceLineDTO create(String description, BigDecimal amount,
            BigDecimal quantity, BigDecimal price, Integer typeId, ItemDTO itemId,
            Integer sourceUserId, Integer isPercentage) {

        InvoiceLineDTO newEntity = new InvoiceLineDTO();
        newEntity.setDescription(description);
        newEntity.setAmount(amount);
        newEntity.setQuantity(quantity);
        newEntity.setPrice(price);
        newEntity.setInvoiceLineType(new InvoiceLineTypeDAS().find(typeId));
        newEntity.setItem(itemId);
        newEntity.setSourceUserId(sourceUserId);
        newEntity.setIsPercentage(isPercentage);
        newEntity.setDeleted(0);
        return save(newEntity);
    }

}
