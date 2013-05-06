/*
 jBilling - The Enterprise Open Source Billing System
 Copyright (C) 2003-2011 Enterprise jBilling Software Ltd. and Emiliano Conde

 This file is part of jbilling.

 jbilling is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 jbilling is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with jbilling.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.sapienter.jbilling.server.process.task;

import java.math.BigDecimal;

import org.apache.log4j.Logger;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.server.invoice.NewInvoiceDTO;
import com.sapienter.jbilling.server.pluggableTask.TaskException;
import com.sapienter.jbilling.server.pluggableTask.admin.ParameterDescription;
import com.sapienter.jbilling.server.user.UserBL;
import com.sapienter.jbilling.server.user.contact.db.ContactDAS;
import com.sapienter.jbilling.server.user.contact.db.ContactDTO;
import com.sapienter.jbilling.server.user.db.UserDTO;
import com.sapienter.jbilling.server.util.Constants;

/**
 * This plug-in calculates taxes for invoice.
 *
 * Plug-in parameters:
 * 
 *      tax_country_code .(required) 'country code' for which the above tax item id is applicable 
 * 
 * @author Vikas Bodani
 * @since 27-Jul-2011
 *
 */
public class CountryTaxCompositionTask extends AbstractChargeTask {

    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(CountryTaxCompositionTask.class));
    
    // Plug-in Parameters
    // Mandatory parameters
    protected static final ParameterDescription PARAM_TAX_COUNTRY_CODE = new ParameterDescription("tax_country_code", true, ParameterDescription.Type.STR);
    
    protected String strTaxCountryCode=null; 
    
    //initializer for pluggable params
    {
        descriptions.add(PARAM_TAX_COUNTRY_CODE);
    }
    
    protected BigDecimal calculateAndApplyTax(NewInvoiceDTO invoice, Integer userId) { 
        
        LOG.debug("calculateAndApplyTax");
        
        BigDecimal invoiceAmountSum= super.calculateAndApplyTax(invoice, userId);
        
        this.invoiceLineTypeId= Constants.INVOICE_LINE_TYPE_TAX;
        
        return invoiceAmountSum;
    }
    
    /**
     * Set the current set of plugin params
     */
    protected void setPluginParameters()  throws TaskException {
        LOG.debug("setPluginParameters()");
        super.setPluginParameters();
        try {
            String paramValue = getParameter(PARAM_TAX_COUNTRY_CODE.getName(), "");
            if (paramValue == null || "".equals(paramValue.trim())) {
                throw new TaskException("Tax Country Code is not defined!");
            }
            strTaxCountryCode= paramValue;
            LOG.debug("Param country code is set.");
        } catch (NumberFormatException e) {
            LOG.error("Incorrect plugin configuration", e);
            throw new TaskException(e);
        }
    }
    
    /**
     * Custom logic to determine if the tax should be applied to this user's invoice
     * @param userId The user_id of the Invoice
     * @return
     */
    protected boolean isTaxCalculationNeeded(NewInvoiceDTO invoice, Integer userId) {
        LOG.debug("isTaxCalculationNeeded()");
        
        //get parent user
        UserDTO user= UserBL.getUserEntity(userId);
        if ( null != user) {
            ContactDTO contactDto = new ContactDAS().findPrimaryContact(user.getUserId());
            if (contactDto != null) {
                LOG.debug("Contact Country Code is " + contactDto.getCountryCode());
                //determine
                return strTaxCountryCode.equals(contactDto.getCountryCode());
            } 
        }
        return false;
    }
}
