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

/*
 * Created on Oct 7, 2004
 *
 */
package com.sapienter.jbilling.server.order;

import java.util.Calendar;

/**
 * @author Emil
 *
 */
public class TimePeriod {
    private Integer unitId = null;
    private Integer value = null;
    private Boolean df_fm = null;
    private Long own_invoice = null;

    public Integer getUnitId() {
        return unitId;
    }

    public void setUnitId(Integer unitId) {
        this.unitId = unitId;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public Boolean getDf_fm() {
        return df_fm;
    }

    public void setDf_fm(Boolean df_fm) {
        this.df_fm = df_fm;
    }

    public void setDf_fm(Integer df_fm) {
        if (df_fm == null) {
            this.df_fm = null;
        } else {
            this.df_fm = new Boolean(df_fm.intValue() == 1);
        }
    }

    public Long getOwn_invoice() {
        return own_invoice;
    }

    public void setOwn_invoice(Integer own_invoice) {
        if (own_invoice != null && own_invoice.intValue() == 1) {
            // give a unique number to it
            Calendar cal = Calendar.getInstance();
            this.own_invoice = new Long(cal.getTimeInMillis());
        } else {
            this.own_invoice = new Long(0);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TimePeriod that = (TimePeriod) o;

        if (df_fm != null ? !df_fm.equals(that.df_fm) : that.df_fm != null) return false;
        if (own_invoice != null ? !own_invoice.equals(that.own_invoice) : that.own_invoice != null) return false;
        if (!unitId.equals(that.unitId)) return false;
        if (!value.equals(that.value)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = unitId.hashCode();
        result = 31 * result + value.hashCode();
        result = 31 * result + (df_fm != null ? df_fm.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TimePeriod{" +
               "unitId=" + unitId +
               ", value=" + value +
               '}';
    }
}
