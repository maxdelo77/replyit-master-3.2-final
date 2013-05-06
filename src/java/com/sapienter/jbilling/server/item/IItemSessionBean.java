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

package com.sapienter.jbilling.server.item;

import java.util.List;

import com.sapienter.jbilling.common.SessionInternalError;
import com.sapienter.jbilling.server.item.db.ItemDTO;
import com.sapienter.jbilling.server.item.db.ItemTypeDTO;
import com.sapienter.jbilling.server.util.db.CurrencyDTO;

/*
 *
 * This is the session facade for the Item. All interaction from the client
 * to the server is made through calls to the methods of this class. This 
 * class uses helper classes (Business Logic -> BL) for the real logic.
 *
 * @author emilc
 * 
 */

public interface IItemSessionBean {

    public Integer create(ItemDTO dto, Integer languageId) 
            throws SessionInternalError;    

    public void update(Integer executorId, ItemDTO dto, Integer languageId) 
            throws SessionInternalError;
    
    public boolean validateDecimals( Integer hasDecimals, Integer itemId );

    public ItemDTO get(Integer id, Integer languageId, Integer userId,
            Integer currencyId, Integer entityId, List<PricingField> 
            pricingFields) throws SessionInternalError;


    public void delete(Integer executorId, Integer id) 
            throws SessionInternalError;

    public Integer createType(ItemTypeDTO dto) throws SessionInternalError;
    
    public ItemTypeDTO getType(Integer id) throws SessionInternalError;

    public void updateType(Integer executorId, ItemTypeDTO dto) 
            throws SessionInternalError;

    /*
     * For now, this will delete permanently
     *
     */
     public void deleteType(Integer executorId, Integer itemTypeId) 
             throws SessionInternalError;

    public CurrencyDTO[] getCurrencies(Integer languageId, Integer entityId) 
            throws SessionInternalError;
    
    public void setCurrencies(Integer entityId, CurrencyDTO[] currencies,
            Integer currencyId) throws SessionInternalError;

    public Integer getEntityCurrency(Integer entityId) 
            throws SessionInternalError;
}
