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

import java.util.Collection;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.sapienter.jbilling.common.SessionInternalError;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskBL;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskDAS;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskDTO;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskParameterDTO;
import com.sapienter.jbilling.server.util.Context;

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
@Transactional( propagation = Propagation.REQUIRED )
public class PluggableTaskSessionBean implements IPluggableTaskSessionBean {

    //private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(PluggableTaskSessionBean.class));

    public PluggableTaskDTO getDTO(Integer typeId, 
            Integer entityId) throws SessionInternalError {
        try {
            PluggableTaskBL bl = new PluggableTaskBL();
            bl.set(entityId, typeId);
            return bl.getDTO();
        } catch (Exception e) {
            throw new SessionInternalError(e);
        }
    }

    public PluggableTaskDTO[] getAllDTOs(Integer entityId) 
            throws SessionInternalError {
            
        PluggableTaskDAS das = (PluggableTaskDAS) Context.getBean(Context.Name.PLUGGABLE_TASK_DAS);
        Collection<PluggableTaskDTO> tasks = das.findAllByEntity(entityId);

        for (PluggableTaskDTO task : tasks) {
            task.populateParamValues();
        }

        PluggableTaskDTO[] retValue = 
            new PluggableTaskDTO[tasks.size()];
        retValue = (PluggableTaskDTO[]) tasks.toArray(retValue);
        
        return retValue;
    }

    public void createParameter(Integer executorId, Integer taskId, PluggableTaskParameterDTO dto) {
            
        PluggableTaskBL bl = new PluggableTaskBL();
        bl.createParameter(taskId, dto);
    }

    @Transactional( propagation = Propagation.REQUIRES_NEW )
    public void update(Integer executorId, PluggableTaskDTO dto) {
            
        PluggableTaskBL bl = new PluggableTaskBL();
        bl.update(executorId, dto);
        
    }
    
    @Transactional( propagation = Propagation.REQUIRES_NEW )
    public PluggableTaskDTO[] updateAll(Integer executorId, PluggableTaskDTO dto[]) {

        PluggableTaskBL bl = new PluggableTaskBL();
        for (int f = 0; f < dto.length; f++) {
            bl.update(executorId, dto[f]);
            dto[f] = bl.getDTO(); // replace with the new version
        }
        
        return dto;
    }

    public void delete(Integer executorId, Integer id) {

        PluggableTaskBL bl = new PluggableTaskBL(id);
        bl.delete(executorId);
        
    }

    public void deleteParameter(Integer executorId, Integer id) {

        PluggableTaskBL bl = new PluggableTaskBL();
        bl.deleteParameter(executorId, id);
        
    }

    @Transactional( propagation = Propagation.REQUIRES_NEW )
    public void updateParameters(Integer executorId, PluggableTaskDTO dto) 
            throws SessionInternalError {

        PluggableTaskBL bl = new PluggableTaskBL();           
        bl.updateParameters(dto);
    }
}
