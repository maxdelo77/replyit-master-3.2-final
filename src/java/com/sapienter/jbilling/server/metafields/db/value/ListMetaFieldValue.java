/*
 JBILLING CONFIDENTIAL
 _____________________

 [2003] - [2012] Enterprise jBilling Software Ltd.
 All Rights Reserved.

 NOTICE:  All information contained herein is, and remains
 the property of Enterprise jBilling Software.
 The intellectual and technical concepts contained
 herein are proprietary to Enterprise jBilling Software
 and are protected by trade secret or copyright law.
 Dissemination of this information or reproduction of this material
 is strictly forbidden.
 */

package com.sapienter.jbilling.server.metafields.db.value;


import com.sapienter.jbilling.server.metafields.db.MetaField;
import com.sapienter.jbilling.server.metafields.db.MetaFieldValue;
import org.hibernate.annotations.CollectionOfElements;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("list")
public class ListMetaFieldValue extends MetaFieldValue<List<String>> {

    private List<String> value = new ArrayList<String>();

    public ListMetaFieldValue () {
    }

    public ListMetaFieldValue (MetaField name) {
        super(name);
    }

    @CollectionOfElements(targetElement = String.class)
    @JoinTable(name = "list_meta_field_values",
               joinColumns = @JoinColumn(name = "meta_field_value_id")
    )
    @Column(name = "list_value")
    public List<String> getValue () {
        return value;
    }

    public void setValue (List<String> values) {
        this.value = values;
    }

    @Override
    @Transient
    public boolean isEmpty() {
        return value == null || value.isEmpty();
    }
}