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
package com.sapienter.jbilling.server.pluggableTask.admin;

import java.util.List;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.server.util.db.AbstractDAS;
import org.apache.log4j.Logger;
import org.hibernate.Query;

public class PluggableTaskParameterDAS extends AbstractDAS<PluggableTaskParameterDTO> {

    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(PluggableTaskParameterDAS.class));

    // QUERIES
    private static final String findAllByTaskSQL =
            "SELECT b " +
                    "  FROM PluggableTaskParameterDTO b " +
                    " WHERE b.task = :task";

    // END OF QUERIES
    public List<PluggableTaskParameterDTO> findAllByTask(PluggableTaskDTO task) {
        Query query = getSession().createQuery(findAllByTaskSQL);
        query.setParameter("task", task);
        query.setCacheable(true);
        query.setComment("PluggableTaskParameterDAS.findAllByTask");
        return query.list();
    }

    /*
    public PluggableTaskParameterDTO find(Integer id) {

        PluggableTaskParameterDTO entity = em.find(PluggableTaskParameterDTO.class, id);
        return entity;
    }
    */
}
