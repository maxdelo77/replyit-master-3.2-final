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

package com.sapienter.jbilling.server.metafields;

import com.sapienter.jbilling.server.metafields.db.*;

import java.util.Comparator;
import java.util.Map;

/**
 * Helper class for working with custom fields. It is needed because some classes
 * cann't extends CustomizedEntity directly. Instead they can implement MetaContent interface
 * and use this helper to do work.
 *
 * @author Alexander Aksenov
 * @since 11.10.11
 */
public class MetaFieldHelper {

    /**
     * Returns the meta field by name if it's been defined for this object.
     *
     * @param customizedEntity entity for searching fields
     * @param name             meta field name
     * @return field if found, null if not set.
     */
    public static MetaFieldValue getMetaField(MetaContent customizedEntity, String name) {
        for (MetaFieldValue value : customizedEntity.getMetaFields()) {
            if (value.getField() != null && value.getField().getName().equals(name)) {
                return value;
            }
        }
        return null;
    }

    /**
     * Adds a meta field to this object. If there is already a field associated with
     * this object then the existing value should be updated.
     *
     * @param customizedEntity entity for searching fields
     * @param field            field to update.
     */
    public static void setMetaField(MetaContent customizedEntity, MetaFieldValue field) {
        MetaFieldValue oldValue = customizedEntity.getMetaField(field.getField().getName());
        if (oldValue != null) {
            customizedEntity.getMetaFields().remove(oldValue);
        }
        customizedEntity.getMetaFields().add(field);
    }

    /**
     * Sets the value of a meta field that is already associated with this object. If
     * the field does not already exist, or if the value class is of an incorrect type
     * then an IllegalArgumentException will be thrown.
     *
     * @param customizedEntity entity for search/set fields
     * @param name             field name
     * @param value            field value
     * @throws IllegalArgumentException thrown if field name does not exist, or if value is of an incorrect type.
     */
    public static void setMetaField(Integer entityId, MetaContent customizedEntity, String name, Object value) throws IllegalArgumentException {
        MetaFieldValue fieldValue = customizedEntity.getMetaField(name);
        if (fieldValue != null) { // common case during editing
            try {
                fieldValue.setValue(value);
            } catch (Exception ex) {
                throw new IllegalArgumentException("Incorrect type for meta field with name " + name, ex);
            }
        } else {
            EntityType type = customizedEntity.getCustomizedEntityType();
            if (type == null) {
                throw new IllegalArgumentException("Meta Fields could not be specified for current entity");
            }
            MetaField fieldName = new MetaFieldDAS().getFieldByName(entityId, type, name);
            if (fieldName == null) {
                throw new IllegalArgumentException("Meta Field with name " + name + " was not defined for current entity");
            }
            MetaFieldValue field = fieldName.createValue();
            try {
                field.setValue(value);
            } catch (Exception ex) {
                throw new IllegalArgumentException("Incorrect type for meta field with name " + name, ex);
            }
            customizedEntity.setMetaField(field);
        }
    }

    /**
     * Usefull method for updating meta fields with validation before entity saving
     * @param entity    target entity
     * @param dto       dto with new data
     */
    public static void updateMetaFieldsWithValidation(Integer entityId, MetaContent entity, MetaContent dto) {
        Map<String, MetaField> availableMetaFields = MetaFieldBL.getAvailableFields(entityId, entity.getCustomizedEntityType());
        for (String fieldName : availableMetaFields.keySet()) {
            MetaFieldValue newValue = dto.getMetaField(fieldName);
            if (newValue == null) { // try to search by id, may be temp fix
                MetaField metaFieldName = availableMetaFields.get(fieldName);
                newValue = dto.getMetaField(metaFieldName.getId());
            }
//          TODO: (VCA) - we want the null values for the validation
//            if ( null != newValue && null != newValue.getValue() ) {
            	entity.setMetaField(entityId, fieldName, newValue != null ? newValue.getValue() : null);
//            } //else {
              //no point creating null/empty-value records in db
              //}
        }
        for (MetaFieldValue value : entity.getMetaFields()) {
            MetaFieldBL.validateMetaField(value.getField(), value);
        }
    }

    public static MetaFieldValue getMetaField(MetaContent customizedEntity, Integer metaFieldNameId) {
        for (MetaFieldValue value : customizedEntity.getMetaFields()) {
            if (value.getField() != null && value.getField().getId().equals(metaFieldNameId)) {
                return value;
            }
        }
        return null;
    }


    /**
     * Comparator for sorting meta field values after retrieving from DB
     */
    public final static class MetaFieldValuesOrderComparator implements Comparator<MetaFieldValue> {
        public int compare(MetaFieldValue o1, MetaFieldValue o2) {
            if (o1.getField().getDisplayOrder() == null && o2.getField().getDisplayOrder() == null) {
                return 0;
            }
            if (o1.getField().getDisplayOrder() != null) {
                return o1.getField().getDisplayOrder().compareTo(o2.getField().getDisplayOrder());
            } else {
                return -1 * o2.getField().getDisplayOrder().compareTo(o1.getField().getDisplayOrder());
            }
        }
    }

}
