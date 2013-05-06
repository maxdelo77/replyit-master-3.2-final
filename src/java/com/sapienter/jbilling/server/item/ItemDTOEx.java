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

package com.sapienter.jbilling.server.item;

import com.sapienter.jbilling.server.item.validator.ItemTypes;
import com.sapienter.jbilling.server.metafields.MetaFieldValueWS;
import com.sapienter.jbilling.server.pricing.PriceModelWS;
import com.sapienter.jbilling.server.security.WSSecured;
import com.sapienter.jbilling.server.util.InternationalDescriptionWS;
import com.sapienter.jbilling.server.util.db.LanguageDTO;
import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.ListUtils;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class ItemDTOEx implements WSSecured, Serializable {

    // ItemDTO
    private Integer id;
    @NotNull(message = "validation.error.notnull")
   	@Size(min=1,max=50, message="validation.error.size,1,50")
    private String number;
    @Size (min=0,max=50, message="validation.error.size,1,50")
    private String glCode;
    @Digits(integer=3, fraction=4, message="validation.error.invalid.number.or.fraction")
    @Pattern(regexp = "^[0-9]{1,3}(\\.[0-9]{1,4})?$", message="validation.message.error.invalid.pattern" )
    private String percentage;
    private Integer[] excludedTypes = null;
    private Integer hasDecimals;
    private Integer deleted;
    
    private Integer entityId;
    private SortedMap<Date, PriceModelWS> defaultPrices = new TreeMap<Date, PriceModelWS>();
    private PriceModelWS defaultPrice;

    @Size(min=1,max=50, message="validation.error.size,1,50")
    private String description = null;
    @ItemTypes
    private Integer[] types = null;
    private String promoCode = null;
    private Integer currencyId = null;
    @Digits(integer=12, fraction=10, message="validation.error.not.a.number")
    private String price = null;
    private Integer orderLineTypeId = null;
    @NotEmpty(message = "validation.error.notnull")
    private List<InternationalDescriptionWS> descriptions = ListUtils.lazyList(new ArrayList<InternationalDescriptionWS>(), FactoryUtils.instantiateFactory(InternationalDescriptionWS.class));
    @Valid
    private MetaFieldValueWS[] metaFields;


    public ItemDTOEx() {
    }

    public ItemDTOEx(Integer id,String number, String glCode, Integer entity, String description,
                     Integer deleted, Integer currencyId, BigDecimal price, BigDecimal percentage,
                     Integer orderLineTypeId, Integer hasDecimals) {

        this(id, number, glCode, percentage, hasDecimals, deleted, entity);
        setDescription(description);
        setCurrencyId(currencyId);
        setPrice(price);
        setOrderLineTypeId(orderLineTypeId);
    }

    public ItemDTOEx(Integer id, String number, String glCode, BigDecimal percentage, Integer hasDecimals,
                     Integer deleted, Integer entityId) {
        this.id = id;
        this.number = number;
        this.glCode= glCode;
        this.percentage = percentage != null ? percentage.toString() : null;
        this.hasDecimals = hasDecimals;
        this.deleted = deleted;
        this.entityId = entityId;
    }

    public ItemDTOEx(ItemDTOEx otherValue) {
        this.id = otherValue.id;
        this.number = otherValue.number;
        this.glCode = otherValue.glCode;
        this.percentage = otherValue.percentage;
        this.hasDecimals = otherValue.hasDecimals;
        this.deleted = otherValue.deleted;
        this.entityId = otherValue.entityId;
        this.metaFields = otherValue.getMetaFields();
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNumber() {
        return this.number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getGlCode() {
        return glCode;
    }

    public void setGlCode(String glCode) {
        this.glCode = glCode;
    }

    public String getPercentage() {
        return this.percentage;
    }

    public BigDecimal getPercentageAsDecimal() {
        return percentage != null ? new BigDecimal(percentage) : null;
    }

    public void setPercentageAsDecimal(BigDecimal percentage) {
        setPercentage(percentage);
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }

    public void setPercentage(BigDecimal percentage) {
        this.percentage = (percentage != null ? percentage.toString() : null);
    }

    public Integer[] getExcludedTypes() {
        return excludedTypes;
    }

    public void setExcludedTypes(Integer[] excludedTypes) {
        this.excludedTypes = excludedTypes;
    }

    public Integer getHasDecimals() {
        return this.hasDecimals;
    }

    public void setHasDecimals(Integer hasDecimals) {
        this.hasDecimals = hasDecimals;
    }

    public Integer getDeleted() {
        return this.deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    public Integer getEntityId() {
        return this.entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    /**
     * Returns an english description.
     * 
     * @return String
     */
    public String getDescription() {
        for (InternationalDescriptionWS description : descriptions) {
            if (description.getLanguageId() == LanguageDTO.ENGLISH_LANGUAGE_ID) {
                return description.getContent();
            }
        }
        return "";
    }

    /**
     * Sets the a description in english.
     * 
     * @param newDescription
     *            The description to set
     */
    public void setDescription(String newDescription) {
        description = newDescription;

        for (InternationalDescriptionWS description : descriptions) {
            if (description.getLanguageId() == LanguageDTO.ENGLISH_LANGUAGE_ID) {
                description.setContent(newDescription);
                return;
            }
        }
        InternationalDescriptionWS newDescriptionWS = new InternationalDescriptionWS();
        newDescriptionWS.setContent(newDescription);
        newDescriptionWS.setPsudoColumn("description");
        newDescriptionWS.setLanguageId(LanguageDTO.ENGLISH_LANGUAGE_ID);
        descriptions.add(newDescriptionWS);
    }

    public Integer[] getTypes() {
        return types;
    }

    /*
     * Rules only work on collections of strings (operator contains)
     */
    public Collection<String> getStrTypes() {
        List<String> retValue = new ArrayList<String>();
        for (Integer i: types) {
            retValue.add(i.toString());
        }
        return retValue;
    }

    public void setTypes(Integer[] vector) {
        types = vector;
    }

    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String string) {
        promoCode = string;
    }

    public Integer getOrderLineTypeId() {
        return orderLineTypeId;
    }

    public void setOrderLineTypeId(Integer typeId) {
        orderLineTypeId = typeId;
    }

    public Integer getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Integer currencyId) {
        this.currencyId = currencyId;
    }

    public String getPrice() {
        return price;
    }

    public BigDecimal getPriceAsDecimal() {
        return price != null ? new BigDecimal(price) : null;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setPrice(BigDecimal price) {
        setPrice((price != null ? price.toString() : null));
    }

    public SortedMap<Date, PriceModelWS> getDefaultPrices() {
        return defaultPrices;
    }

    public void setDefaultPrices(SortedMap<Date, PriceModelWS> defaultPrices) {
        this.defaultPrices = defaultPrices;
    }

    public void addDefaultPrice(Date date, PriceModelWS model) {
        this.defaultPrices.put(date, model);
    }

    public PriceModelWS getDefaultPrice() {
        return defaultPrice;
    }

    public void setDefaultPrice(PriceModelWS defaultPrice) {
        this.defaultPrice = defaultPrice;
    }

    public MetaFieldValueWS[] getMetaFields() {
        return metaFields;
    }

    public void setMetaFields(MetaFieldValueWS[] metaFields) {
        this.metaFields = metaFields;
    }

    public Integer getOwningEntityId() {
        return getEntityId();
    }

    public List<InternationalDescriptionWS> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(List<InternationalDescriptionWS> descriptions) {
        this.descriptions = descriptions;
    }

    /**
     * Unsupported, web-service security enforced using {@link #getOwningEntityId()}
     * @return null
     */
    public Integer getOwningUserId() {
        return null;
    }

    public boolean isIdentical(Object other) {
        if (other instanceof ItemDTOEx) {
            ItemDTOEx that = (ItemDTOEx) other;
            boolean lEquals = true;
            if (this.number == null) {
                lEquals = lEquals && (that.number == null);
            } else {
                lEquals = lEquals && this.number.equals(that.number);
            }
            if (this.glCode == null) {
                lEquals = lEquals && (that.glCode == null);
            } else {
                lEquals = lEquals && this.glCode.equals(that.glCode);
            }
            if (this.percentage == null) {
                lEquals = lEquals && (that.percentage == null);
            } else {
                lEquals = lEquals && this.percentage.equals(that.percentage);
            }
            if (this.hasDecimals == null) {
                lEquals = lEquals && (that.hasDecimals == null);
            } else {
                lEquals = lEquals && this.hasDecimals.equals(that.hasDecimals);
            }
            if (this.deleted == null) {
                lEquals = lEquals && (that.deleted == null);
            } else {
                lEquals = lEquals && this.deleted.equals(that.deleted);
            }
            if (this.entityId == null) {
                lEquals = lEquals && (that.entityId == null);
            } else {
                lEquals = lEquals && this.entityId.equals(that.entityId);
            }

            return lEquals;
        } else {
            return false;
        }
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;

        if (!(other instanceof ItemDTOEx))
            return false;

        ItemDTOEx that = (ItemDTOEx) other;
        boolean lEquals = true;
        if( this.id == null ) {
            lEquals = lEquals && ( that.id == null );
        } else {
            lEquals = lEquals && this.id.equals( that.id );
        }

        lEquals = lEquals && isIdentical(that);
        return lEquals;
    }

    @Override
    public int hashCode(){
        int result = 17;
        result = 37*result + ((this.id != null) ? this.id.hashCode() : 0);
        result = 37*result + ((this.number != null) ? this.number.hashCode() : 0);
        result = 37*result + ((this.glCode != null) ? this.glCode.hashCode() : 0);
        result = 37*result + ((this.percentage != null) ? this.percentage.hashCode() : 0);
        result = 37*result + ((this.hasDecimals != null) ? this.hasDecimals.hashCode() : 0);
        result = 37*result + ((this.deleted != null) ? this.deleted.hashCode() : 0);
        result = 37*result + ((this.entityId != null) ? this.entityId.hashCode() : 0);

        return result;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ItemDTOEx [currencyId=");
        builder.append(currencyId);
        builder.append(", deleted=");
        builder.append(deleted);
        builder.append(", description=");
        builder.append(description);
        builder.append(", entityId=");
        builder.append(entityId);
        builder.append(", hasDecimals=");
        builder.append(hasDecimals);
        builder.append(", id=");
        builder.append(id);
        builder.append(", number=");
        builder.append(number);
        builder.append(", glCode=");
        builder.append(glCode);
        builder.append(", orderLineTypeId=");
        builder.append(orderLineTypeId);
        builder.append(", percentage=");
        builder.append(percentage);
        builder.append(", price=");
        builder.append(price);
        builder.append(", promoCode=");
        builder.append(promoCode);
        builder.append(", types=");
        builder.append(Arrays.toString(types));
        builder.append(", excludedTypes=");
        builder.append(Arrays.toString(excludedTypes));
        builder.append(']');
        return builder.toString();
    }


}
