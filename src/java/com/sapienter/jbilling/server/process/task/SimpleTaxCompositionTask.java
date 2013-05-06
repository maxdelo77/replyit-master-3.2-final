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

package com.sapienter.jbilling.server.process.task;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.server.invoice.NewInvoiceDTO;
import com.sapienter.jbilling.server.invoice.db.InvoiceLineDTO;
import com.sapienter.jbilling.server.item.ItemBL;
import com.sapienter.jbilling.server.item.db.ItemDAS;
import com.sapienter.jbilling.server.item.db.ItemDTO;
import com.sapienter.jbilling.server.item.db.ItemTypeDTO;
import com.sapienter.jbilling.server.metafields.db.MetaFieldValue;
import com.sapienter.jbilling.server.pluggableTask.TaskException;
import com.sapienter.jbilling.server.pluggableTask.admin.ParameterDescription;
import com.sapienter.jbilling.server.process.PeriodOfTime;
import com.sapienter.jbilling.server.user.UserBL;
import com.sapienter.jbilling.server.user.db.CustomerDTO;
import com.sapienter.jbilling.server.user.db.UserDTO;
import com.sapienter.jbilling.server.util.Constants;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.Set;

/**
 * This plug-in calculates taxes for invoice.
 *
 * Plug-in parameters:
 *
 *      tax_item_id                 (required) The item that will be added to an invoice with the taxes
 *
 *      customer_exempt_field_id     (optional) The id of CCF that if its value is 'true' or 'yes' for a customer,
 *                                  then the customer is considered exempt. Exempt customers do not get the tax
 *                                  added to their invoices.
 *      item_exempt_category_id     (optional) The id of an item category that, if the item belongs to, it is
 *                                  exempt from taxes
 *
 * @author Alexander Aksenov, Vikas Bodani
 * @since 30.04.11
 */
public class SimpleTaxCompositionTask extends AbstractChargeTask {

    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(SimpleTaxCompositionTask.class));

    protected Integer exemptItemCategoryID = null;
    protected Integer exemptCustomerAttributeID = null;
    
    // plug-in parameters
    
    // optional, may be empty
    public static final ParameterDescription PARAM_CUSTOM_CONTACT_FIELD_ID =
        new ParameterDescription("customer_exempt_field_id", false, ParameterDescription.Type.STR);
    public static final ParameterDescription PARAM_ITEM_EXEMPT_CATEGORY_ID = 
		new ParameterDescription("item_exempt_category_id", false, ParameterDescription.Type.STR);

    //initializer for pluggable params
    {
        descriptions.add(PARAM_CUSTOM_CONTACT_FIELD_ID);
        descriptions.add(PARAM_ITEM_EXEMPT_CATEGORY_ID);
    }

    /**
     * Set the current set of plugin params
     */
    protected void setPluginParameters()  throws TaskException {
        LOG.debug("setPluginParameters()");
        super.setPluginParameters();
        try {
            String paramValue = getParameter(PARAM_ITEM_EXEMPT_CATEGORY_ID.getName(), "");
            if (paramValue != null && !"".equals(paramValue.trim())) {
                exemptItemCategoryID = new Integer(paramValue);
            }
            paramValue = getParameter(PARAM_CUSTOM_CONTACT_FIELD_ID.getName(), "");
            if (paramValue != null && !"".equals(paramValue.trim())) {
                exemptCustomerAttributeID = new Integer(paramValue);
            }
        } catch (NumberFormatException e) {
            LOG.error("Incorrect plugin configuration", e);
            throw new TaskException(e);
        }
    }

    public boolean isTaxCalculationNeeded(NewInvoiceDTO invoice, Integer userId) {
    	LOG.debug("isTaxCalculationNeeded for user " + userId + " having exemptProperty " + exemptCustomerAttributeID );
    	//default true
    	boolean retVal= true;
    	if ( null != exemptCustomerAttributeID ) { 
	    	UserDTO user= UserBL.getUserEntity(userId);
	        CustomerDTO customer = user.getCustomer();
	        if (null != customer) {
	        	LOG.debug ("User and Customer resolved. ");
		        MetaFieldValue customField = customer.getMetaField(exemptCustomerAttributeID);
		        if ( null != customField) {
		        	LOG.debug("Exempt field value " + customField.getValue());
			        String value = (String) customField.getValue();
			        if ("yes".equalsIgnoreCase(value) || "true".equalsIgnoreCase(value)) {
			            retVal= false;
			        }
		        }
	        }
	        
    	}
        return retVal;
    }

    /**
    * Used to set primarily the line type id for tax item or any other customization
    */
    protected BigDecimal calculateAndApplyTax(NewInvoiceDTO invoice, Integer userId) { 
        
        LOG.debug("calculateAndApplyTax");
        
        BigDecimal invoiceAmountSum= super.calculateAndApplyTax(invoice, userId);
        
        LOG.debug("Exempt Category " + exemptItemCategoryID);
        if (exemptItemCategoryID != null) {
            // find exemp items and subtract price
            for (int i = 0; i < invoice.getResultLines().size(); i++) {
                InvoiceLineDTO invoiceLine = (InvoiceLineDTO) invoice.getResultLines().get(i);
                ItemDTO item = invoiceLine.getItem();

                if (item != null) {
                    Set<ItemTypeDTO> itemTypes = new ItemDAS().find(item.getId()).getItemTypes();
                    for (ItemTypeDTO itemType : itemTypes) {
                        if (itemType.getId() == exemptItemCategoryID) {
                            LOG.debug("Item " + item.getDescription() + " is Exempt. Category " + itemType.getId());
                            invoiceAmountSum = invoiceAmountSum.subtract(invoiceLine.getAmount());
                            break;
                        }
                    }
                }
            }
        }
        
        this.invoiceLineTypeId= Constants.INVOICE_LINE_TYPE_TAX;
        
        return invoiceAmountSum;
    }
    
    @Override
    public BigDecimal calculatePeriodAmount(BigDecimal fullPrice, PeriodOfTime period) {
        throw new UnsupportedOperationException("Can't call this method");
    }
}
