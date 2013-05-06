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
package com.sapienter.jbilling.server.pluggableTask.admin;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.sapienter.jbilling.server.util.Constants;
import com.sapienter.jbilling.server.util.db.AbstractDescription;

@Entity
@Table(name = "pluggable_task_type_category")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class PluggableTaskTypeCategoryDTO extends AbstractDescription implements Serializable {

    @Id
    public Integer Id;

    @Column(name = "interface_name")
    private String interfaceName;

    @Transient
    protected String getTable() {
        return Constants.TABLE_PLUGGABLE_TASK_TYPE_CATEGORY;
    }


    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }
    
    public String toString() {
        StringBuffer str = new StringBuffer("{");
        str.append("-" + this.getClass().getName() + "-");
        str.append("id=" + getId() + " " + "interfaceName=" + getInterfaceName() + " " + 
                " " + "description=" + getDescription());
        str.append('}');

        return(str.toString());

      }
}
