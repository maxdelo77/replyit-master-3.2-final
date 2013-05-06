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

package com.sapienter.jbilling.server.mediation.cache;

import org.springframework.jdbc.core.JdbcTemplate;

public abstract class AbstractFinder implements IFinder {

    protected final JdbcTemplate jdbcTemplate;
    protected final ILoader loader;

    public AbstractFinder(JdbcTemplate template, ILoader loader) {
        this.jdbcTemplate = template;
        this.loader = loader;
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
}
