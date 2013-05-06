/**
 * 
 */
package com.sapienter.jbilling.server.util;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.server.metafields.db.DataType;
import com.sapienter.jbilling.server.metafields.db.MetaField;
import com.sapienter.jbilling.server.metafields.db.MetaFieldDAS;
import com.sapienter.jbilling.server.util.db.EnumerationDAS;
import com.sapienter.jbilling.server.util.db.EnumerationDTO;
import com.sapienter.jbilling.server.util.db.EnumerationValueDAS;
import com.sapienter.jbilling.server.util.db.EnumerationValueDTO;


/**
 * @author Vikas Bodani
 * @since 10-Aug-2011
 *
 */
public class EnumerationBL {


    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(EnumerationBL.class));

    private EnumerationDAS enumerationDas;
    private EnumerationDTO enumeration;
    private EnumerationValueDAS enumerationValueDas;
    private EnumerationValueDTO enumerationValueDTO;
    
    public EnumerationBL() {
        _init();
    }

    public EnumerationBL(EnumerationDTO enumeration) {
        _init();
        this.enumeration = enumeration;
    }

    public EnumerationBL(Integer enumerationId) {
        _init();
        set(enumerationId);
    }

    private void _init() {
        this.enumerationDas = new EnumerationDAS();
        this.enumerationValueDas = new EnumerationValueDAS();
    }

    public void set(Integer enumerationId) {
        this.enumeration = enumerationDas.find(enumerationId);
    }

    public EnumerationDTO getEntity() {
        return enumeration;
    }

    /**
     * Saves a new Enumeration to and sets the BL entity to the newly created Enumeration. 
     *
     * @param Enumeration Enumeration to save
     * @return id of the new Enumeration
     */
    public Integer create(EnumerationDTO enumeration) {
        if (enumeration != null) {
            //for (EnumerationValueDTO value: enumeration.getValues()) {
                //value.setEnumeration(enumeration);
            //    this.createEnumerationValue(value);
            //}
            this.enumeration = enumerationDas.save(enumeration);
            return this.enumeration.getId();
        }

        LOG.error("Cannot save a null EnumerationDTO!");
        return null;
    }

    /**
     * Updates this Enumeration's values with those of the given Enumeration. 
     * @param dto EnumerationDTO 
     */
    public void update(EnumerationDTO dto) {
        enumeration.setName(dto.getName());
        setEnumerationValues(dto.getValues());
    }

    /**
     * Updates the meta_field_name table
     * @param oldEnumName
     * @param newEnumName
     */
    public void updateMetaFields(String oldEnumName, String newEnumName){
        List<Integer> metaFieldIdList = new MetaFieldDAS().getAllIdsByDataTypeAndName(DataType.ENUMERATION, oldEnumName);

        Iterator iterator = metaFieldIdList.iterator();

        while(iterator.hasNext()) {
            Integer metaFieldId = (Integer)iterator.next();
            MetaField metaField = new MetaFieldDAS().find(metaFieldId);
            metaField.setName(newEnumName);
            new MetaFieldDAS().save(metaField);
            LOG.debug("Metafield "+metaField.getId()+" updated.");
        }
    }

    public EnumerationValueDTO createEnumerationValue(EnumerationValueDTO valueDto) {
        if (valueDto != null) {
            return enumerationValueDas.save(valueDto);
        }

        LOG.error("Cannot save a null EnumerationValueDTO!");
        return null;
    }
    
    /**
     * Sets the granted valuess of this Enumeration to the given set.
     *
     */
    public void setEnumerationValues(List<EnumerationValueDTO> values) {
        if (enumeration != null) {
            enumeration.getValues().clear();
            enumeration.getValues().addAll(values);

            this.enumeration = enumerationDas.save(enumeration);
            enumerationDas.flush();

        } else {
            LOG.error("Cannot update, EnumerationDTO not found or not set!");
        }
    }

    /**
     * Deletes this Enumeration.
     */
    public void delete() {
        if (enumeration != null) {
            enumeration.getValues().clear();
            enumerationDas.delete(enumeration);
            enumerationDas.flush();

        } else {
            LOG.error("Cannot delete, EnumerationDTO not found or not set!");
        }
    }

    /**
     * This method is added specifically for duplicate name validation during update.
     * @param id
     * @param name
     * @return true/false depending on whether new name in update exists for another enumeration or not
     */
    public boolean exists(Integer id, String name, Integer entityId) {
    	return enumerationDas.exists(id, name, entityId);
    }
}
