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

public class PreferenceDAS extends AbstractDAS<PreferenceDTO> {
    private static final String findByType_Row =
        "SELECT a " + 
        "  FROM PreferenceDTO a " + 
        " WHERE a.preferenceType.id = :typeId " +
        "   AND a.foreignId = :foreignId " +
        "   AND a.jbillingTable.name = :tableName ";

    public PreferenceDTO findByType_Row(Integer typeId,Integer foreignId,String tableName) {
        Query query = getSession().createQuery(findByType_Row);
        query.setParameter("typeId", typeId);
        query.setParameter("foreignId", foreignId);
        query.setParameter("tableName", tableName);
        query.setCacheable(true);
        return (PreferenceDTO) query.uniqueResult();
    }

}
