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

package com.sapienter.jbilling.server.util.db;

import com.sapienter.jbilling.server.util.Constants;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;

/**
 * CountryDTO
 *
 * @author Brian Cowdery
 * @since 15/02/11
 */
@Entity
@Table(name = "country")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class CountryDTO extends AbstractDescription implements Serializable {

    private int id;
    private String code;

    public CountryDTO() {
    }

    @Id
    @Column(name = "id", unique = true, nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "code", unique = true, nullable = false, length = 2)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Transient
    @Override
    protected String getTable() {
        return Constants.TABLE_COUNTRY;
    }

    @Override
    public String toString() {
        return code;
    }
}
