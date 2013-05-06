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

import com.sapienter.jbilling.server.util.db.AbstractDAS;
import org.hibernate.Criteria;
import org.hibernate.LockMode;
import org.hibernate.criterion.Restrictions;

import java.math.BigDecimal;

/**
 * 
 * @author abimael
 *
 */
public class ProcessRunTotalPmDAS extends AbstractDAS<ProcessRunTotalPmDTO> {

    public ProcessRunTotalPmDTO create(BigDecimal total) {
        ProcessRunTotalPmDTO newEntity = new ProcessRunTotalPmDTO();
        newEntity.setTotal(total);
        return save(newEntity);
    }
    
     /**
     * Returns the locked row, since payment processing updates this in parallel
      *
     * @param methodId payment method id
     * @param total run total
     * @return locked process run total 
     */
    public ProcessRunTotalPmDTO getByMethod(Integer methodId, ProcessRunTotalDTO total) {
        Criteria criteria = getSession().createCriteria(ProcessRunTotalPmDTO.class)
                .createAlias("processRunTotal", "r")
                    .add(Restrictions.eq("r.id", total.getId()))
                .createAlias("paymentMethod", "c")
                    .add(Restrictions.eq("c.id", methodId))
                .setComment("ProcessRunTotalPmDAS.getByMethod");

        return (ProcessRunTotalPmDTO) criteria.uniqueResult();
    }
   

}
