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

import org.hibernate.Query;

import com.sapienter.jbilling.server.user.db.CompanyDAS;
import com.sapienter.jbilling.server.user.db.UserStatusDAS;
import com.sapienter.jbilling.server.util.db.AbstractDAS;

public class AgeingEntityStepDAS extends AbstractDAS<AgeingEntityStepDTO> {

    private static final String findStepSQL = 
            "SELECT a " + 
            "  FROM AgeingEntityStepDTO a " + 
            " WHERE a.company.id = :entity " + 
            "   AND a.userStatus.id = :status ";

    public AgeingEntityStepDTO findStep(Integer entityId, Integer stepId) {
        Query query = getSession().createQuery(findStepSQL);
        query.setParameter("entity", entityId);
        query.setParameter("status", stepId);
        return (AgeingEntityStepDTO) query.uniqueResult();
    }

    public void create(Integer entityId, Integer statusId,
            String welcomeMessage, String failedLoginMessage,
            Integer languageId, int days) {

        AgeingEntityStepDTO ageing = new AgeingEntityStepDTO();
        ageing.setCompany(new CompanyDAS().find(entityId));
        ageing.setUserStatus(new UserStatusDAS().find(statusId));

        ageing.setWelcomeMessage(languageId, welcomeMessage);
        ageing.setFailedLoginMessage(languageId, failedLoginMessage);
        ageing.setDays(days);

        save(ageing);
    }
}
