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

package com.sapienter.jbilling.server.pluggableTask;

import com.sapienter.jbilling.common.SessionInternalError;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskDTO;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskParameterDTO;

/**
 *
 * This is the session facade for the invoices in general. It is a statless
 * bean that provides services not directly linked to a particular operation
 *
 * @author emilc
 * 
 * Even when using JPA, container transactions are required. This is because
 * transactional demarcation is taked from the application server.
 **/
public interface IPluggableTaskSessionBean {

    public PluggableTaskDTO getDTO(Integer typeId, Integer entityId) 
            throws SessionInternalError;

    public PluggableTaskDTO[] getAllDTOs(Integer entityId) 
            throws SessionInternalError;

    public void createParameter(Integer executorId, Integer taskId, 
            PluggableTaskParameterDTO dto);

    public void update(Integer executorId, PluggableTaskDTO dto);
    
    public PluggableTaskDTO[] updateAll(Integer executorId, 
            PluggableTaskDTO dto[]);

    public void delete(Integer executorId, Integer id);

    public void deleteParameter(Integer executorId, Integer id);

    public void updateParameters(Integer executorId, PluggableTaskDTO dto)
            throws SessionInternalError;
}
