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

package com.sapienter.jbilling.server.pricing;

import com.sapienter.jbilling.server.pricing.db.PriceModelDTO;
import org.joda.time.DateMidnight;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author Brian Cowdery
 * @since 06-08-2010
 */
public class PriceModelWS implements Serializable {

    public static final String ATTRIBUTE_WILDCARD = "*";

    private Integer id;
    private String type;
    private SortedMap<String, String> attributes = new TreeMap<String, String>();
    private String rate;
    private Integer currencyId;
    private PriceModelWS next;

    public PriceModelWS() {
    }

    public PriceModelWS(String type) {
        this.type = type;
    }

    public PriceModelWS(String type, BigDecimal rate, Integer currencyId) {
        this.type = type;
        this.rate = (rate != null ? rate.toString() : null);
        this.currencyId = currencyId;
    }

    public PriceModelWS(PriceModelDTO model) {
        this.id = model.getId();
        this.attributes = new TreeMap<String,String>(model.getAttributes());

        setRate(model.getRate());

        if (model.getType() != null ) this.type = model.getType().name();
        if (model.getCurrency() != null) this.currencyId = model.getCurrency().getId();
        if (model.getNext() != null) this.next = new PriceModelWS(model.getNext());
    }

    public PriceModelWS(PriceModelWS ws) {
        this.id = ws.getId();
        this.type = ws.getType();
        this.attributes = new TreeMap<String, String>(ws.getAttributes());
        this.rate = ws.getRate();
        this.currencyId = ws.getCurrencyId();

        if (ws.getNext() != null) this.next = new PriceModelWS(ws.getNext());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public SortedMap<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(SortedMap<String, String> attributes) {
        this.attributes = attributes;
    }

    public void addAttribute(String name, String value) {
        this.attributes.put(name, value);
    }

    public String getRate() {
        return rate;
    }

    public BigDecimal getRateAsDecimal() {
        return rate != null ? new BigDecimal(rate) : null;
    }

    public void setRateAsDecimal(BigDecimal rate) {
        setRate(rate);
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = (rate != null ? rate.toString() : null);
    }

    public Integer getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Integer currencyId) {
        this.currencyId = currencyId;
    }

    public PriceModelWS getNext() {
        return next;
    }

    public void setNext(PriceModelWS next) {
        this.next = next;
    }

    @Override
    public String toString() {
        return "PriceModelWS{"
                + "id=" + id
                + ", type='" + type + '\''
                + ", attributes=" + attributes
                + ", rate=" + rate
                + ", currencyId=" + currencyId
                + '}';
    }
}
