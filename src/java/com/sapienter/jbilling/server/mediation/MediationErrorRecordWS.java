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

package com.sapienter.jbilling.server.mediation;

import com.sapienter.jbilling.server.item.PricingField;
import com.sapienter.jbilling.server.mediation.db.MediationRecordDTO;
import com.sapienter.jbilling.server.util.csv.Exportable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Wrapper for mediation error records
 *
 * @author: Panche.Isajeski
 * @since: 23/05/12
 */
public class MediationErrorRecordWS implements Serializable, Exportable {

    private Integer recordId;
    private String key;
    private Integer mediationProcessId;
    private Integer recordStatusId;

    private List<PricingField> fields = new ArrayList<PricingField>();
    private List<String> errors = new ArrayList<String>();

    public MediationErrorRecordWS() {
    }

    public MediationErrorRecordWS(MediationRecordDTO recordDTO, List<PricingField> pricingFields, List<String> errors) {
        this.key = recordDTO.getKey();
        this.recordId = recordDTO.getId();
        this.mediationProcessId = recordDTO.getProcess() != null ? recordDTO.getProcess().getId() : null;
        this.recordStatusId = recordDTO.getRecordStatus() != null ? recordDTO.getRecordStatus().getId() : null;
        this.fields = pricingFields;
        this.errors = errors;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getRecordId() {
        return recordId;
    }

    public void setRecordId(Integer recordId) {
        this.recordId = recordId;
    }

    public Integer getMediationProcessId() {
        return mediationProcessId;
    }

    public void setMediationProcessId(Integer mediationProcessId) {
        this.mediationProcessId = mediationProcessId;
    }

    public Integer getRecordStatusId() {
        return recordStatusId;
    }

    public void setRecordStatusId(Integer recordStatusId) {
        this.recordStatusId = recordStatusId;
    }

    public List<PricingField> getFields() {
        return fields;
    }

    public void setFields(List<PricingField> fields) {
        this.fields = fields;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public String[] getFieldNames() {

        return new String[] {
                "fields",
                "errors",
                "recordId",
                "recordStatus",
                "mediationProcessId",
                "recordKey"
        };
    }

    public Object[][] getFieldValues() {
        List<Object[]> values = new ArrayList<Object[]>();

        int pricingFieldSize = fields.size();
        Object[] objects = new Object[pricingFieldSize + 5];

        for (int i=0; i<pricingFieldSize; i++) {
            objects[i] = PricingField.encode(fields.get(i));
        }

        StringBuilder errorBuilder = new StringBuilder();
        for (Iterator<String> it = errors.iterator(); it.hasNext();) {
            errorBuilder.append(it.next());
            if (it.hasNext()) errorBuilder.append(" ");
        }

        objects[pricingFieldSize] =  errorBuilder.toString();
        objects[pricingFieldSize + 1] = recordId;
        objects[pricingFieldSize + 2] = recordStatusId;
        objects[pricingFieldSize + 3] = mediationProcessId;
        objects[pricingFieldSize + 4] = key;

        values.add(objects);

        return values.toArray(new Object[values.size()][]);
    }

    @Override
    public String toString() {
        return "MediationErrorRecordWS{" +
                "key='" + key + '\'' +
                ", mediationProcessId=" + mediationProcessId +
                ", recordStatusId=" + recordStatusId +
                ", errors=" + errors +
                '}';
    }

}
