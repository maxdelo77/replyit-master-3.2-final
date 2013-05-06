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


import com.sapienter.jbilling.server.user.db.UserDAS;
import com.sapienter.jbilling.server.util.db.AbstractDAS;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import java.util.Date;
import java.util.List;

public class ProcessRunUserDAS extends AbstractDAS<ProcessRunUserDTO> {

    public ProcessRunUserDTO create(Integer processRunId, Integer userId, Integer status, Date created) {
        ProcessRunUserDTO dto = new ProcessRunUserDTO();
        dto.setStatus(status);
        dto.setCreated(created);
        dto.setUser(new UserDAS().find(userId));
        dto.setProcessRun(new ProcessRunDAS().find(processRunId));
        dto = save(dto);
        return dto;
    }

    public List<Integer> findSuccessfullUserIds(Integer processRunId) {
        return findUserIdsByStatus(processRunId, ProcessRunUserDTO.STATUS_SUCCEEDED);
    }

    public List<Integer> findFailedUserIds(Integer processRunId) {
        return findUserIdsByStatus(processRunId, ProcessRunUserDTO.STATUS_FAILED);
    }

    public Long findSuccessfullUsersCount(Integer processRunId) {
        return findUsersCountByStatus(processRunId, ProcessRunUserDTO.STATUS_SUCCEEDED);
    }

    public Long findFailedUsersCount(Integer processRunId) {
        return findUsersCountByStatus(processRunId, ProcessRunUserDTO.STATUS_FAILED);
    }

    private List<Integer> findUserIdsByStatus(Integer processRunId, Integer status) {
        Criteria criteria = getSession().createCriteria(ProcessRunUserDTO.class)
                .add(Restrictions.eq("status", status))
                .add(Restrictions.eq("processRun.id", processRunId))
                .setProjection(Projections.property("user.id"));

        return (List<Integer>) criteria.list();
    }

    public ProcessRunUserDTO getUser(Integer processRunId, Integer userId) {
        Criteria criteria = getSession().createCriteria(ProcessRunUserDTO.class)
                .add(Restrictions.eq("user.id", userId))
                .add(Restrictions.eq("processRun.id", processRunId));

        return (ProcessRunUserDTO) criteria.uniqueResult();
    }

    public void removeProcessRunUsersForProcessRun(Integer processRunId) {
        String hql = "DELETE FROM " + ProcessRunUserDTO.class.getSimpleName() +
                " WHERE processRun.id = :processRunId";
        Query query = getSession().createQuery(hql);
        query.setParameter("processRunId", processRunId);
        query.executeUpdate();
    }

    private Long findUsersCountByStatus(Integer processRunId, Integer status) {
        Criteria criteria = getSession().createCriteria(ProcessRunUserDTO.class)
                .add(Restrictions.eq("status", status))
                .add(Restrictions.eq("processRun.id", processRunId))
                .setProjection(Projections.count("user.id"));

        return (Long) criteria.uniqueResult();
    }

}
