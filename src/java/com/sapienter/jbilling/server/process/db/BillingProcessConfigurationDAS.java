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

import java.util.Date;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.sapienter.jbilling.server.user.db.CompanyDTO;
import com.sapienter.jbilling.server.util.db.AbstractDAS;

/**
 * 
 * @author abimael
 *
 */
public class BillingProcessConfigurationDAS extends AbstractDAS<BillingProcessConfigurationDTO> {

    public BillingProcessConfigurationDTO create(CompanyDTO entity,
            Date nextRunDate, Integer generateReport) {
        BillingProcessConfigurationDTO nuevo = new BillingProcessConfigurationDTO();
        nuevo.setEntity(entity);
        nuevo.setNextRunDate(nextRunDate);
        nuevo.setGenerateReport(generateReport);
        
        return save(nuevo);
    }

    public BillingProcessConfigurationDTO findByEntity(CompanyDTO entity) {
        Criteria criteria = getSession().createCriteria(BillingProcessConfigurationDTO.class);
        criteria.add(Restrictions.eq("entity", entity));
        return (BillingProcessConfigurationDTO) criteria.uniqueResult();
    }

}
