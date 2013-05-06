package com.sapienter.jbilling.server.user;

import java.io.Serializable;

import javax.validation.constraints.Min;

import com.sapienter.jbilling.server.util.Constants;
import org.hibernate.validator.constraints.ScriptAssert;

import com.sapienter.jbilling.server.order.db.OrderPeriodDAS;
import com.sapienter.jbilling.server.order.db.OrderPeriodDTO;
import com.sapienter.jbilling.server.util.MapPeriodToCalendar;

@ScriptAssert(lang="groovy", script="_this.isMainSubscriptionValid()", message="validation.error.main.subscription")
public class MainSubscriptionWS implements Serializable {
	
	private Integer periodId;
	
    @Min(value = 1, message = "validation.error.min,1")
	private Integer nextInvoiceDayOfPeriod;

	public MainSubscriptionWS() {
		super();
	}

	public MainSubscriptionWS(Integer periodId, Integer nextInvoiceDayOfPeriod) {
		super();
		this.periodId = periodId;
		this.nextInvoiceDayOfPeriod = nextInvoiceDayOfPeriod;
	}

	public Integer getPeriodId() {
		return periodId;
	}

	public void setPeriodId(Integer periodId) {
		this.periodId = periodId;
	}

	public Integer getNextInvoiceDayOfPeriod() {
		return nextInvoiceDayOfPeriod;
	}

	public void setNextInvoiceDayOfPeriod(Integer nextInvoiceDayOfPeriod) {
		this.nextInvoiceDayOfPeriod = nextInvoiceDayOfPeriod;
	}
	
	public boolean isMainSubscriptionValid() {
		
		if (periodId != null) {
			if (nextInvoiceDayOfPeriod == null) {
				return false;
			}
			OrderPeriodDTO orderPeriod = new OrderPeriodDAS().find(periodId);
			if (orderPeriod == null) {
				return false;
			}

            Integer totalDaysInPeriod = MapPeriodToCalendar.periodToDays(
                    orderPeriod.getPeriodUnit().getId()) * orderPeriod.getValue();

			return nextInvoiceDayOfPeriod <= totalDaysInPeriod;
		}
		return nextInvoiceDayOfPeriod == null;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((nextInvoiceDayOfPeriod == null) ? 0 : nextInvoiceDayOfPeriod
						.hashCode());
		result = prime * result
				+ ((periodId == null) ? 0 : periodId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MainSubscriptionWS other = (MainSubscriptionWS) obj;
		if (nextInvoiceDayOfPeriod == null) {
			if (other.nextInvoiceDayOfPeriod != null)
				return false;
		} else if (!nextInvoiceDayOfPeriod.equals(other.nextInvoiceDayOfPeriod))
			return false;
		if (periodId == null) {
			if (other.periodId != null)
				return false;
		} else if (!periodId.equals(other.periodId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MainSubscriptionWS [periodId=" + periodId
				+ ", nextInvoiceDayOfPeriod=" + nextInvoiceDayOfPeriod + "]";
	}
}
