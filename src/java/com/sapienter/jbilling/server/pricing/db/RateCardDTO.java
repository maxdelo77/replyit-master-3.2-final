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

package com.sapienter.jbilling.server.pricing.db;

import com.sapienter.jbilling.server.user.db.CompanyDTO;
import com.sapienter.jbilling.server.util.api.validation.CreateValidationGroup;
import com.sapienter.jbilling.server.util.sql.JDBCUtils;
import com.sapienter.jbilling.server.util.sql.TableGenerator;
import org.apache.commons.lang.StringUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "rate_card")
@javax.persistence.TableGenerator(
        name = "rate_card_GEN",
        table = "jbilling_seqs",
        pkColumnName = "name",
        valueColumnName = "next_id",
        pkColumnValue = "rate_card",
        allocationSize = 10
)
// no cache
public class RateCardDTO implements Serializable {

    public static final String TABLE_PREFIX = "rate_";

    public static final List<TableGenerator.Column> TABLE_COLUMNS = Arrays.asList(
            new TableGenerator.Column("id", "int", false, true),
            new TableGenerator.Column("match", "varchar(20)", false, false),
            new TableGenerator.Column("comment", "varchar(255)", true, false),
            new TableGenerator.Column("rate", "numeric(22,10)", false, false)
    );

    private Integer id;
    private String name;
    private String tableName;
    private CompanyDTO company;

    public RateCardDTO() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "rate_card_GEN")
    @Column(name = "id", nullable = false, unique = true)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "name", nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "table_name", nullable = false, unique = true)
    public String getTableName() {
        if (StringUtils.isBlank(tableName) && StringUtils.isNotBlank(name)) {
            tableName = JDBCUtils.getDatabaseObjectName(name);
        }

        if (StringUtils.isNotBlank(tableName) && !tableName.startsWith(TABLE_PREFIX)) {
            tableName = TABLE_PREFIX + tableName;
        }

        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entity_id")
    public CompanyDTO getCompany() {
        return company;
    }

    public void setCompany(CompanyDTO company) {
        this.company = company;
    }

    @Override
    public String toString() {
        return "RateCardDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", tableName='" + tableName + '\'' +
                ", company=" + company +
                '}';
    }
}
