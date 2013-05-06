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
import com.sapienter.jbilling.server.util.db.CurrencyDAS;
import org.hibernate.Criteria;
import org.hibernate.LockMode;
import org.hibernate.criterion.Restrictions;

import java.math.BigDecimal;

public class ProcessRunTotalDAS extends AbstractDAS<ProcessRunTotalDTO> {

    public ProcessRunTotalDTO create(ProcessRunDTO run, BigDecimal invoiced, BigDecimal notPaid, BigDecimal paid, Integer currencyId) {
        ProcessRunTotalDTO dto = new ProcessRunTotalDTO();
        dto.setTotalInvoiced(invoiced);
        dto.setTotalNotPaid(notPaid);
        dto.setTotalPaid(paid);
        dto.setCurrency(new CurrencyDAS().find(currencyId));
        dto.setProcessRun(run);
        dto = save(dto);
        run.getProcessRunTotals().add(dto);
        return dto;
    }

    /**
     * Returns the locked row, since payment processing updates this in parallel
     * @param run process run
     * @param currencyId currency id
     * @return locked process run total
     */
    public ProcessRunTotalDTO getByCurrency(ProcessRunDTO run, Integer currencyId) {
        Criteria criteria = getSession().createCriteria(ProcessRunTotalDTO.class)
                .createAlias("processRun", "r")
                    .add(Restrictions.eq("r.id", run.getId()))
                .createAlias("currency", "c")
                    .add(Restrictions.eq("c.id", currencyId))
                .setComment("ProcessRunTotalDAS.getByCurrency");

        return (ProcessRunTotalDTO) criteria.uniqueResult();
    }

}
