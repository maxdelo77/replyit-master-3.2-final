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
package com.sapienter.jbilling.server.user.db;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.sapienter.jbilling.server.order.db.OrderPeriodDAS;
import com.sapienter.jbilling.server.order.db.OrderPeriodDTO;

/**
 * 
 * @author Panche.Isajeski
 * 
 * 	Embedable class for customer main subscription parameters
 *
 */
@Embeddable
public class MainSubscriptionDTO implements Serializable {
	
	private OrderPeriodDTO subscriptionPeriod;
	private Integer nextInvoiceDayOfPeriod;

	public MainSubscriptionDTO() {
		super();
	}
	
	public MainSubscriptionDTO(OrderPeriodDTO subscriptionPeriod, Integer nextInvoiceDayOfPeriod) {
		super();
		this.subscriptionPeriod = subscriptionPeriod;
		this.nextInvoiceDayOfPeriod = nextInvoiceDayOfPeriod;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "main_subscript_order_period_id")
	public OrderPeriodDTO getSubscriptionPeriod() {
		return subscriptionPeriod;
	}

	public void setSubscriptionPeriod(OrderPeriodDTO subscriptionPeriod) {
		this.subscriptionPeriod = subscriptionPeriod;
	}
	
	public void setSubsriptionPeriodFromPeriodId(Integer periodId) {
		setSubscriptionPeriod(new OrderPeriodDAS().find(periodId));
	}

	@Column(name = "next_invoice_day_of_period")
	public Integer getNextInvoiceDayOfPeriod() {
		return nextInvoiceDayOfPeriod;
	}

	public void setNextInvoiceDayOfPeriod(Integer nextInvoiceDayOfMonth) {
		this.nextInvoiceDayOfPeriod = nextInvoiceDayOfMonth;
	}
	
	public static MainSubscriptionDTO createDefaultMainSubscription(Integer entityId) {
		return new MainSubscriptionDTO(new OrderPeriodDAS().findOrderPeriod(
				entityId, 1, 1), 1);
	}

	@Override
	public String toString() {
		return "MainSubscriptionDTO [subscriptionPeriod="
				+ subscriptionPeriod + ", nextInvoiceDayOfPeriod="
				+ nextInvoiceDayOfPeriod + "]";
	}

}
