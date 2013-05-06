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

package com.sapienter.jbilling.server.user;

import com.sapienter.jbilling.server.security.WSSecured;

import java.io.Serializable;
import java.util.Date;

public class UserTransitionResponseWS implements WSSecured, Serializable {
    private Integer id;
    private Integer userId;
    private Date transitionDate;
    private Integer fromStatusId;
    private Integer toStatusId;
    
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getUserId() {
        return userId;
    }
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    public Date getTransitionDate() {
        return transitionDate;
    }
    public void setTransitionDate(Date transitionDate) {
        this.transitionDate = transitionDate;
    }
    public Integer getFromStatusId() {
        return fromStatusId;
    }
    public void setFromStatusId(Integer fromStatusId) {
        this.fromStatusId = fromStatusId;
    }
    public Integer getToStatusId() {
        return toStatusId;
    }
    public void setToStatusId(Integer toStatusId) {
        this.toStatusId = toStatusId;
    }

    /**
     * Unsupported, web-service security enforced using {@link #getOwningUserId()}
     * @return null
     */
    public Integer getOwningEntityId() {
        return null;
    }

    public Integer getOwningUserId() {
        return getUserId();
    }

    @Override
    public String toString() {
        return "id = " + getId() + " user_id = " + getUserId() +
                " from_status_id = " + getFromStatusId() + " to_status_id = " + getToStatusId() +
                " transition_date = " + getTransitionDate().toString();
    }

}
