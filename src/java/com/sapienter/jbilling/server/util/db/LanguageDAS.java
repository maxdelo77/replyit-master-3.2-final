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

import org.hibernate.Query;


public class LanguageDAS extends AbstractDAS<LanguageDTO> {
    private static final String findByCodeSQL = 
        "SELECT a " + 
        "  FROM LanguageDTO a " + 
        " WHERE a.code = :code ";

    public LanguageDTO findByCode(String code) {
        Query query = getSession().createQuery(findByCodeSQL);
        query.setParameter("code", code);
        return (LanguageDTO) query.uniqueResult();
    }

}
