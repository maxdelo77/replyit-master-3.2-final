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

package com.sapienter.jbilling.server.process;

import java.io.Serializable;
import javax.validation.constraints.NotNull;
import com.sapienter.jbilling.server.security.WSSecured;


/**
 * AgeingWS
 * @author Vikas Bodani
 */
public class AgeingWS implements WSSecured, Serializable {
	
	@NotNull(message="validation.error.notnull")
	private Integer statusId = null;
    private String statusStr = null;
    @NotNull(message="validation.error.notnull")
    private String welcomeMessage = null;
    @NotNull(message="validation.error.notnull")
    private String failedLoginMessage = null;
    private Boolean inUse = null;
    private Integer days;
    private Integer entityId;
    
    //default constructor
    public AgeingWS(){}
    
	public AgeingWS(AgeingDTOEx dto) {
		super();
		this.statusId = dto.getStatusId();
		this.statusStr = dto.getStatusStr();
		this.welcomeMessage = dto.getWelcomeMessage();
		this.failedLoginMessage = dto.getFailedLoginMessage();
		this.inUse = dto.getInUse();
		this.days = dto.getDays();
		this.entityId= (null != dto.getCompany()) ? dto.getCompany().getId() : null;
	}
	
	public Integer getStatusId() {
		return statusId;
	}
	public void setStatusId(Integer statusId) {
		this.statusId = statusId;
	}
	public String getStatusStr() {
		return statusStr;
	}
	public void setStatusStr(String statusStr) {
		this.statusStr = statusStr;
	}
	public String getWelcomeMessage() {
		return welcomeMessage;
	}
	public void setWelcomeMessage(String welcomeMessage) {
		this.welcomeMessage = welcomeMessage;
	}
	public String getFailedLoginMessage() {
		return failedLoginMessage;
	}
	public void setFailedLoginMessage(String failedLoginMessage) {
		this.failedLoginMessage = failedLoginMessage;
	}
	public Boolean getInUse() {
		return inUse;
	}
	public void setInUse(Boolean inUse) {
		this.inUse = inUse;
	}
	public Integer getDays() {
		return days;
	}
	public void setDays(Integer days) {
		this.days = days;
	}
	
	public Integer getEntityId() {
		return entityId;
	}

	public void setEntityId(Integer entityId) {
		this.entityId = entityId;
	}

	public Integer getOwningEntityId() {
        return getEntityId();
    }
    /**
     * Unsupported, web-service security enforced using {@link #getOwningEntityId()}
     * @return null
     */
    public Integer getOwningUserId() {
        return null;
    }

	
	public String toString() {
		return "AgeingWS [statusId=" + statusId + ", statusStr=" + statusStr
				+ ", welcomeMessage=" + welcomeMessage
				+ ", failedLoginMessage=" + failedLoginMessage + ", inUse="
				+ inUse + ", days=" + days + "]";
	}

}
