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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.sapienter.jbilling.server.order.db.OrderPeriodDAS;
import com.sapienter.jbilling.server.order.db.OrderPeriodDTO;
import com.sapienter.jbilling.server.user.db.CompanyDAS;
import com.sapienter.jbilling.server.util.InternationalDescriptionWS;
import com.sapienter.jbilling.server.util.db.LanguageDTO;

/**
 * OrderPeriodWS
 *
 * @author Vikas Bodani
 * @since 29-03-2011
 */

public class OrderPeriodWS implements Serializable {

    private Integer id;
    private Integer entityId;
    
    private Integer periodUnitId;

    @NotNull(message = "validation.error.notnull")
    @Min(value = 1, message = "validation.error.min,1")
    @Digits(integer=3, fraction=0, message="validation.error.not.a.number")
    private Integer value;
    private Integer versionNum;

    @Size(min=1, message="validation.error.notnull")
    private List<InternationalDescriptionWS> descriptions = new ArrayList<InternationalDescriptionWS>();


    public OrderPeriodWS() {
    }

    public OrderPeriodWS(Integer id, Integer entityId, Integer periodUnitId, Integer value) {
       this.id = id;
       this.entityId = entityId;
       this.periodUnitId= periodUnitId;
       this.value = value;
    }

    public OrderPeriodWS(OrderPeriodDTO dto) {
    	this.id= dto.getId();
    	this.entityId= dto.getCompany().getId();
    	this.value=dto.getValue();
    	this.periodUnitId= dto.getPeriodUnit().getId();
    }
    
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getEntityId() {
		return entityId;
	}

	public void setEntityId(Integer entityId) {
		this.entityId = entityId;
	}

	public Integer getPeriodUnitId() {
		return periodUnitId;
	}

	public void setPeriodUnitId(Integer periodUnitId) {
		this.periodUnitId = periodUnitId;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}
	
	public List<InternationalDescriptionWS> getDescriptions() {
		return descriptions;
	}

	public void setDescriptions(List<InternationalDescriptionWS> descriptions) {
		this.descriptions = descriptions;
	}

	public InternationalDescriptionWS getDescription(Integer languageId) {
        for (InternationalDescriptionWS description : descriptions)
            if (description.getLanguageId().equals(languageId))
                return description;
        return null;
    }

	public String toString() {
		return "OrderPeriodWS [id=" + id + ", entityId=" + entityId
				+ ", periodUnitId=" + periodUnitId + ", value=" + value
				+ ", descriptions=" + descriptions + "]";
	}

	public Integer getVersionNum() {
        return versionNum;
    }

    public void setVersionNum(Integer versionNum) {
        this.versionNum = versionNum;
    }
    
}


