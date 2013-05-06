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

import java.util.Calendar;
import java.util.Date;

import org.hibernate.Query;

import com.sapienter.jbilling.server.util.Constants;
import com.sapienter.jbilling.server.util.db.AbstractDAS;

public class ProcessRunDAS extends AbstractDAS<ProcessRunDTO> {

    //private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(ProcessRunDAS.class));

    public ProcessRunDTO create(BillingProcessDTO process, Date runDate, Integer invoicesGenerated, ProcessRunStatusDTO status) {
        ProcessRunDTO dto = new ProcessRunDTO(0, runDate, Calendar.getInstance().getTime());
        dto.setBillingProcess(process);
        dto.setInvoicesGenerated(invoicesGenerated);
        dto.setStatus(status);

        dto = save(dto);
        process.getProcessRuns().add(dto);
        return dto;
    }
    
    public ProcessRunDTO getLatestSuccessful(Integer entityId) {
        final String hql =
            "select a " +
            "  from ProcessRunDTO a " +
            " where a.billingProcess.entity.id = :entity " +
            "   and a.status.id = " + Constants.PROCESS_RUN_STATUS_SUCCESS +
            "   and a.billingProcess.isReview = 0 " +
            "order by a.id desc ";

        Query query = getSession().createQuery(hql);
        query.setParameter("entity", entityId);
        query.setMaxResults(1);
        return (ProcessRunDTO) query.uniqueResult();
    }

    public ProcessRunDTO getLatest(Integer entityId) {
        final String hql =
                "select processRun " +
                "   from ProcessRunDTO processRun " +
                " where processRun.billingProcess.entity.id = :entityId " +
                " order by processRun.started desc";
        Query query = getSession().createQuery(hql);
        query.setParameter("entityId", entityId);
        query.setMaxResults(1);
        return (ProcessRunDTO) query.uniqueResult();
    }
}
