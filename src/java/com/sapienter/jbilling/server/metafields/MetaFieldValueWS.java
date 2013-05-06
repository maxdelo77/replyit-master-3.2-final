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

import com.sapienter.jbilling.server.metafields.db.DataType;
import com.sapienter.jbilling.server.metafields.db.MetaFieldValue;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author Alexander Aksenov
 * @since 09.10.11
 */
public class MetaFieldValueWS implements Serializable {


    @NotNull(message="validation.error.notnull")
    @Size(min = 1, max = 100, message = "validation.error.size,1,100")
    private String fieldName;
    private boolean disabled;
    private boolean mandatory;
    private DataType dataType;
    private Object defaultValue;
    private Integer displayOrder;

    private Integer id;

    @Size(min = 0, max = 1000, message = "validation.error.size,0,1000")
    private String stringValue;
    private Date dateValue;
    private Boolean booleanValue;
    @Digits(integer = 12, fraction = 10, message="validation.error.not.a.number")
    private String decimalValue;
    private Integer integerValue;
    private String[] listValue;

    public MetaFieldValueWS() {
    }

    public MetaFieldValueWS(MetaFieldValue metaFieldValue) {
        if (metaFieldValue.getField() != null) {
            this.fieldName = metaFieldValue.getField().getName();
            this.disabled = metaFieldValue.getField().isDisabled();
            this.mandatory = metaFieldValue.getField().isMandatory();
            this.dataType = metaFieldValue.getField().getDataType();
            this.displayOrder = metaFieldValue.getField().getDisplayOrder();
            setDefaultValue(metaFieldValue.getField().getDefaultValue() != null ? metaFieldValue.getField().getDefaultValue().getValue() : null);
        }

        this.id = metaFieldValue.getId();
        this.setValue(metaFieldValue.getValue());
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @XmlTransient
    public Object getValue() {
        if (getStringValue() != null) {
            return getStringValue();
        } else if (getDateValue() != null) {
            return getDateValue();
        } else if (getBooleanValue() != null) {
            return getBooleanValue();
        } else if (getDecimalValue() != null) {
            return getDecimalValueAsDecimal();
        } else if (getIntegerValue() != null) {
            return getIntegerValue();
        } else if (getListValue() != null) {
            return getListValueAsList();
        }

        return null;
    }

    public void setValue(Object value) {
        setStringValue(null);
        setDateValue(null);
        setBooleanValue(null);
        setDecimalValue(null);
        setIntegerValue(null);

        if (value == null) return;

        if (value instanceof String) {
            setStringValue((String) value);
        } else if (value instanceof Date) {
            setDateValue((Date) value);
        } else if (value instanceof Boolean) {
            setBooleanValue((Boolean) value);
        } else if (value instanceof BigDecimal) {
            setBigDecimalValue((BigDecimal) value);
        } else if (value instanceof Integer) {
            setIntegerValue((Integer) value);
        } else if (value instanceof List) {
            // store List<String> as String[] for WS-compatible mode, perform manual convertion
            setListValue(((List<String>) value).toArray(new String[((List<String>) value).size()]));
        } else if (value instanceof String[]) {
            setListValue((String[]) value);
        }
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        if (defaultValue != null && defaultValue instanceof Collection) {
            // default value is the first in list
            if (((Collection) defaultValue).isEmpty()) {
                this.defaultValue = null;
            } else {
                this.defaultValue = ((Collection) defaultValue).iterator().next();
            }
        } else {
            this.defaultValue = defaultValue;
        }
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public Date getDateValue() {
        return dateValue;
    }

    public void setDateValue(Date dateValue) {
        this.dateValue = dateValue;
    }

    public Boolean getBooleanValue() {
        return booleanValue;
    }

    public void setBooleanValue(Boolean booleanValue) {
        this.booleanValue = booleanValue;
    }

    public String getDecimalValue() {
        return decimalValue;
    }

    public BigDecimal getDecimalValueAsDecimal() {
        return decimalValue != null ? new BigDecimal(decimalValue) : null;
    }


    public void setDecimalValue(String decimalValue) {
        this.decimalValue = decimalValue;
    }

    public void setBigDecimalValue(BigDecimal decimalValue) {
        this.decimalValue = decimalValue != null ? decimalValue.toPlainString() : null;
    }

    public Integer getIntegerValue() {
        return integerValue;
    }

    public void setIntegerValue(Integer integerValue) {
        this.integerValue = integerValue;
    }

    public String[] getListValue() {
        return listValue;
    }

    public void setListValue(String[] listValue) {
        this.listValue = listValue;
    }

    /**
     * Call this method instead of getValue() for metaField with type LIST, because
     * storing data inside MetaFieldValueWS as String[] for WS-complaint mode.
     *
     * @return value as java.util.List for LIST meta field type. null otherwise.
     */
    @XmlTransient
    public List getListValueAsList() {
        if (listValue != null) {
            return new LinkedList<String>(Arrays.asList(listValue));
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        return "MetaFieldValueWS{" +
                "id=" + id +
                ", fieldName='" + fieldName + '\'' +
                ", dataType=" + dataType +
                ", value=" + getValue() +
                '}';
    }
}
