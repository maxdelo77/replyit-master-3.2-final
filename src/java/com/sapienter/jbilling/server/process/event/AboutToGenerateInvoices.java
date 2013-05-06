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

package com.sapienter.jbilling.server.process.event;

import com.sapienter.jbilling.server.system.event.Event;

public class AboutToGenerateInvoices implements Event {

	Integer entityId;
	Integer userId;
	
	public AboutToGenerateInvoices(Integer entityId, Integer userId) {
		this.entityId= entityId;
		this.userId= userId;
	}
	
	@Override
	public String getName() {
		return "ABOUT TO GENERATE INVOICE";
	}

	@Override
	public Integer getEntityId() {
		return entityId;
	}

	public Integer getUserId() {
		return userId;
	}
	
}
