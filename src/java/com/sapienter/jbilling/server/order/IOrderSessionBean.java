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

package com.sapienter.jbilling.server.order;

import java.math.BigDecimal;
import java.util.Date;

import com.sapienter.jbilling.common.SessionInternalError;
import com.sapienter.jbilling.server.item.ItemDecimalsException;
import com.sapienter.jbilling.server.order.db.OrderDTO;
import com.sapienter.jbilling.server.order.db.OrderPeriodDTO;

/**
 *
 * This is the session facade for the orders in general. It is a statless
 * bean that provides services not directly linked to a particular operation
 *
 * @author emilc
 **/
public interface IOrderSessionBean {
    
    public void reviewNotifications(Date today) throws SessionInternalError;

    public OrderDTO getOrder(Integer orderId) throws SessionInternalError;

    public OrderDTO getOrderEx(Integer orderId, Integer languageId) 
            throws SessionInternalError;

    public OrderDTO setStatus(Integer orderId, Integer statusId, 
            Integer executorId, Integer languageId) throws SessionInternalError;

    /**
     * This is a version used by the http api, should be
     * the same as the web service but without the 
     * security check
    public Integer create(OrderWS order, Integer entityId,
            String rootUser, boolean process) throws SessionInternalError;
     */

    public void delete(Integer id, Integer executorId) 
            throws SessionInternalError;
 
    public OrderPeriodDTO[] getPeriods(Integer entityId, Integer languageId) 
            throws SessionInternalError;

    public OrderPeriodDTO getPeriod(Integer languageId, Integer id) 
            throws SessionInternalError;

    public void setPeriods(Integer languageId, OrderPeriodDTO[] periods)
            throws SessionInternalError;

    public void addPeriod(Integer entityId, Integer languageId) 
            throws SessionInternalError;

    public Boolean deletePeriod(Integer periodId) throws SessionInternalError;

    public OrderDTO addItem(Integer itemID, BigDecimal quantity, OrderDTO order,
            Integer languageId, Integer userId, Integer entityId) 
            throws SessionInternalError, ItemDecimalsException;
    
    public OrderDTO addItem(Integer itemID, Integer quantity, OrderDTO order,
            Integer languageId, Integer userId, Integer entityId) 
            throws SessionInternalError, ItemDecimalsException;

    public OrderDTO recalculate(OrderDTO modifiedOrder, Integer entityId) 
            throws ItemDecimalsException;

    public Integer createUpdate(Integer entityId, Integer executorId, 
            OrderDTO order, Integer languageId) throws SessionInternalError;

     public Long getCountWithDecimals(Integer itemId) 
             throws SessionInternalError;    
}
