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

import com.sapienter.jbilling.common.SessionInternalError;
import com.sapienter.jbilling.server.item.db.ItemDTO;
import com.sapienter.jbilling.server.item.db.ItemTypeDAS;
import com.sapienter.jbilling.server.item.db.ItemTypeDTO;
import com.sapienter.jbilling.server.util.db.CurrencyDTO;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/*
 *
 * This is the session facade for the Item. All interaction from the client
 * to the server is made through calls to the methods of this class. This
 * class uses helper classes (Business Logic -> BL) for the real logic.
 *
 * @author emilc
 *
 */

@Transactional( propagation = Propagation.REQUIRED )
public class ItemSessionBean implements IItemSessionBean {

    //private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(ItemSessionBean.class));

    // -------------------------------------------------------------------------
    // Methods
    // -------------------------------------------------------------------------

    public Integer create(ItemDTO dto, Integer languageId)
            throws SessionInternalError {
        try {
            ItemBL bl = new ItemBL();
            return bl.create(dto, languageId);
        } catch (Exception e) {
            throw new SessionInternalError(e);
        }
    }


    public void update(Integer executorId, ItemDTO dto, Integer languageId)
            throws SessionInternalError {
        try {
            ItemBL bl = new ItemBL(dto.getId());
            bl.update(executorId, dto, languageId);
        } catch (Exception e) {
            throw new SessionInternalError(e);
        }
    }

    public boolean validateDecimals( Integer hasDecimals, Integer itemId ) {
        if( itemId == null ) { return true; }
        ItemBL bl = new ItemBL(itemId);
        return bl.validateDecimals( hasDecimals );
    }

    public ItemDTO get(Integer id, Integer languageId, Integer userId,
            Integer currencyId, Integer entityId,
            List<PricingField> pricingFields) throws SessionInternalError {
        try {
            ItemBL itemBL = new ItemBL(id);
            itemBL.setPricingFields(pricingFields);
            return itemBL.getDTO(languageId, userId, entityId, currencyId);
        } catch (Exception e) {
            throw new SessionInternalError(e);
        }
    }

    public void delete(Integer executorId, Integer id)
            throws SessionInternalError {
        try {
            ItemBL bl = new ItemBL(id);
            bl.delete(executorId);
        } catch (Exception e) {
            throw new SessionInternalError(e);
        }

    }

    public Integer createType(ItemTypeDTO dto)
            throws SessionInternalError {
        try {
            ItemTypeBL bl = new ItemTypeBL();
            bl.create(dto);
            return bl.getEntity().getId();
        } catch (Exception e) {
            throw new SessionInternalError(e);
        }
    }

    public ItemTypeDTO getType(Integer id)
            throws SessionInternalError {
        try {
            ItemTypeDTO type = new ItemTypeDAS().find(id);
            ItemTypeDTO dto = new ItemTypeDTO();
            dto.setId(type.getId());
            dto.setEntity(type.getEntity());
            dto.setDescription(type.getDescription());
            dto.setOrderLineTypeId(type.getOrderLineTypeId());

            return dto;
        } catch (Exception e) {
            throw new SessionInternalError(e);
        }
    }

    public void updateType(Integer executorId, ItemTypeDTO dto)
            throws SessionInternalError {
        try {
            ItemTypeBL bl = new ItemTypeBL(dto.getId());
            bl.update(executorId, dto);
        } catch (Exception e) {
            throw new SessionInternalError(e);
        }

    }

    /*
     * For now, this will delete permanently
     *
     */
     public void deleteType(Integer executorId, Integer itemTypeId)
             throws SessionInternalError {
         try {

             ItemTypeBL bl = new ItemTypeBL(itemTypeId);
             bl.delete(executorId);

         } catch (Exception e) {
             throw new SessionInternalError(e);
         }
     }

    public CurrencyDTO[] getCurrencies(Integer languageId, Integer entityId)
            throws SessionInternalError {
        try {
            CurrencyBL bl = new CurrencyBL();
            return bl.getCurrencies(languageId, entityId);
        } catch (Exception e) {
            throw new SessionInternalError(e);
        }
    }

    public void setCurrencies(Integer entityId, CurrencyDTO[] currencies, Integer currencyId) throws SessionInternalError {
        try {
            CurrencyBL bl = new CurrencyBL();
            bl.setCurrencies(entityId, currencies);
            bl.setEntityCurrency(entityId, currencyId);
        } catch (Exception e) {
            throw new SessionInternalError(e);
        }

    }

    public Integer getEntityCurrency(Integer entityId) throws SessionInternalError {
        try {
            CurrencyBL bl = new CurrencyBL();
            return bl.getEntityCurrency(entityId);
        } catch (Exception e) {
            throw new SessionInternalError(e);
        }

    }

}
