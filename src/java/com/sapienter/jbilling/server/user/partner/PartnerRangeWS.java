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

package com.sapienter.jbilling.server.user.partner;

import com.sapienter.jbilling.server.user.partner.db.PartnerRange;

import java.io.Serializable;

/**
 * PartnerRangeWS
 *
 * @author Brian Cowdery
 * @since 25-10-2010
 */
public class PartnerRangeWS implements Serializable {

    private Integer id;
    private Integer partnerId;
    private Double percentageRate;
    private Double referralFee;
    private Integer rangeFrom;
    private Integer rangeTo;

    public PartnerRangeWS() {
    }

    public PartnerRangeWS(PartnerRange dto) {
        this.id = dto.getId();
        this.partnerId = dto.getPartner() != null ? dto.getPartner().getId() : null;
        this.percentageRate = dto.getPercentageRate();
        this.referralFee = dto.getReferralFee();
        this.rangeFrom = dto.getRangeFrom();
        this.rangeTo = dto.getRangeTo();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(Integer partnerId) {
        this.partnerId = partnerId;
    }

    public Double getPercentageRate() {
        return percentageRate;
    }

    public void setPercentageRate(Double percentageRate) {
        this.percentageRate = percentageRate;
    }

    public Double getReferralFee() {
        return referralFee;
    }

    public void setReferralFee(Double referralFee) {
        this.referralFee = referralFee;
    }

    public Integer getRangeFrom() {
        return rangeFrom;
    }

    public void setRangeFrom(Integer rangeFrom) {
        this.rangeFrom = rangeFrom;
    }

    public Integer getRangeTo() {
        return rangeTo;
    }

    public void setRangeTo(Integer rangeTo) {
        this.rangeTo = rangeTo;
    }

    @Override
    public String toString() {
        return "PartnerRangeWS{"
               + "id=" + id
               + ", partnerId=" + partnerId
               + ", percentageRate=" + percentageRate
               + ", referralFee=" + referralFee
               + ", rangeFrom=" + rangeFrom
               + ", rangeTo=" + rangeTo
               + '}';
    }
}
