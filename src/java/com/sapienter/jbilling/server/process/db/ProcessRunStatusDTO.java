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

import com.sapienter.jbilling.server.util.Constants;
import com.sapienter.jbilling.server.util.db.AbstractGenericStatus;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity
@DiscriminatorValue("process_run_status")
public class ProcessRunStatusDTO extends AbstractGenericStatus implements java.io.Serializable {


    public ProcessRunStatusDTO() {
    }


    public ProcessRunStatusDTO(int statusValue) {
        this.statusValue = statusValue;
    }

    @Transient
    protected String getTable() {
        return Constants.TABLE_PROCESS_RUN_STATUS;
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ProcessRunStatusDTO [getId()=");
        builder.append(getId());
        builder.append(", getDescription()=");
        builder.append(getDescription());
        builder.append(']');
        return builder.toString();
    }

}
