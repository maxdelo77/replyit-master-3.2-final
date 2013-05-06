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


import com.sapienter.jbilling.common.Constants;
import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.server.invoice.db.InvoiceDeliveryMethodDAS;
import com.sapienter.jbilling.server.invoice.db.InvoiceDeliveryMethodDTO;
import com.sapienter.jbilling.server.metafields.MetaFieldBL;
import com.sapienter.jbilling.server.metafields.MetaFieldHelper;
import com.sapienter.jbilling.server.metafields.db.CustomizedEntity;
import com.sapienter.jbilling.server.metafields.db.EntityType;
import com.sapienter.jbilling.server.metafields.db.MetaFieldValue;
import com.sapienter.jbilling.server.user.UserBL;
import com.sapienter.jbilling.server.user.UserWS;
import com.sapienter.jbilling.server.user.partner.db.Partner;
import com.sapienter.jbilling.server.user.partner.db.PartnerDAS;
import org.apache.log4j.Logger;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;
import javax.persistence.Version;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@TableGenerator(
        name="customer_GEN",
        table="jbilling_seqs",
        pkColumnName = "name",
        valueColumnName = "next_id",
        pkColumnValue="customer",
        allocationSize = 100
)
// No cache, mutable and critical
@Table(name="customer")
public class CustomerDTO extends CustomizedEntity implements java.io.Serializable {

    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(CustomerDTO.class));

    private int id;
    private UserDTO baseUser;
    private InvoiceDeliveryMethodDTO invoiceDeliveryMethod;
    private Partner partner;
    private Integer referralFeePaid;
    private String notes;
    private Integer autoPaymentType;
    private Integer dueDateUnitId;
    private Integer dueDateValue;
    private Integer dfFm;
    private CustomerDTO parent;
    private Set<CustomerDTO> children = new HashSet<CustomerDTO>(0);
    private Integer isParent;
    private int excludeAging;
    private Integer invoiceChild;
    private boolean useParentPricing = false;
    private int balanceType = Constants.BALANCE_NO_DYNAMIC;
    private BigDecimal dynamicBalance;
    private BigDecimal autoRecharge;
    private BigDecimal creditLimit;
    private int versionNum;
    private Date lastInvoiceDate;
    
    private MainSubscriptionDTO mainSubscription;

    public CustomerDTO() {
    }

    public CustomerDTO(int id) {
        this.id = id;
    }

    public CustomerDTO(int id, InvoiceDeliveryMethodDTO invoiceDeliveryMethod, int excludeAging) {
        this.id = id;
        this.invoiceDeliveryMethod = invoiceDeliveryMethod;
        this.excludeAging = excludeAging;
    }

    public CustomerDTO(int id, UserDTO baseUser, InvoiceDeliveryMethodDTO invoiceDeliveryMethod, Partner partner,
            Integer referralFeePaid, String notes, Integer autoPaymentType, Integer dueDateUnitId,
            Integer dueDateValue, Integer dfFm, CustomerDTO parent, Integer isParent, int excludeAging, Integer invoiceChild, MainSubscriptionDTO mainSubscription) {
       this.id = id;
       this.baseUser = baseUser;
       this.invoiceDeliveryMethod = invoiceDeliveryMethod;
       this.partner = partner;
       this.referralFeePaid = referralFeePaid;
       this.notes = notes;
       this.autoPaymentType = autoPaymentType;
       this.dueDateUnitId = dueDateUnitId;
       this.dueDateValue = dueDateValue;
       this.dfFm = dfFm;
       this.parent = parent;
       this.isParent = isParent;
       this.excludeAging = excludeAging;
       this.invoiceChild = invoiceChild;
       this.mainSubscription = mainSubscription;
    }

    public CustomerDTO(Integer entityId, UserWS user) {
        setBaseUser(new UserDAS().find(user.getUserId()));

        if (user.getPartnerId() != null) {
            setPartner(new PartnerDAS().find(user.getPartnerId()));
        }

        if (user.getParentId() != null) {
            setParent(new CustomerDTO(user.getParentId()));
        }

        if (user.getIsParent() != null) {
            setIsParent(user.getIsParent().booleanValue() ? 1 : 0);
        }

        if (user.getInvoiceChild() != null) {
            setInvoiceChild(user.getInvoiceChild() ? 1 : 0);
        }

        if (user.getUseParentPricing() != null) {
            setUseParentPricing(user.useParentPricing());
        }

        if (user.getCreditCard() != null) {
            setAutoPaymentType(Constants.AUTO_PAYMENT_TYPE_CC);
        }

        if (user.getInvoiceDeliveryMethodId() != null) {
            InvoiceDeliveryMethodDTO deliveryMethod = new InvoiceDeliveryMethodDAS().find(user.getInvoiceDeliveryMethodId());
            setInvoiceDeliveryMethod(deliveryMethod);
        }

        setMainSubscription(UserBL.convertMainSubscriptionFromWS(user.getMainSubscription(), entityId));

        setBalanceType(user.getBalanceType() == null ? Constants.BALANCE_NO_DYNAMIC : user.getBalanceType());
        setCreditLimit(user.getCreditLimit() == null ? null : new BigDecimal(user.getCreditLimit()));
        setDynamicBalance(user.getDynamicBalance() == null ? null : new BigDecimal(user.getDynamicBalance()));
        setAutoRecharge(user.getAutoRecharge() == null ? null : new BigDecimal(user.getAutoRecharge()));

        setNotes(user.getNotes() == null ? "" : user.getNotes());
        setAutoPaymentType(user.getAutomaticPaymentType());

        setDueDateUnitId(user.getDueDateUnitId());
        setDueDateValue(user.getDueDateValue());

        setExcludeAging(user.getExcludeAgeing() != null && user.getExcludeAgeing() ? 1 : 0);

        MetaFieldBL.fillMetaFieldsFromWS(entityId, this, user.getMetaFields());

        LOG.debug("Customer created with auto-recharge: " + getAutoRecharge() + " incoming var, " + user.getAutoRecharge());
    }

    @Id @GeneratedValue(strategy = GenerationType.TABLE, generator = "customer_GEN")
    @Column(name = "id", unique = true, nullable = false)
    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    public UserDTO getBaseUser() {
        return this.baseUser;
    }

    public void setBaseUser(UserDTO baseUser) {
        this.baseUser = baseUser;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_delivery_method_id", nullable = false)
    public InvoiceDeliveryMethodDTO getInvoiceDeliveryMethod() {
        return this.invoiceDeliveryMethod;
    }

    public void setInvoiceDeliveryMethod(InvoiceDeliveryMethodDTO invoiceDeliveryMethod) {
        this.invoiceDeliveryMethod = invoiceDeliveryMethod;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_id")
    public Partner getPartner() {
        return this.partner;
    }

    public void setPartner(Partner partner) {
        this.partner = partner;
    }

    @Column(name = "referral_fee_paid")
    public Integer getReferralFeePaid() {
        return this.referralFeePaid;
    }

    public void setReferralFeePaid(Integer referralFeePaid) {
        this.referralFeePaid = referralFeePaid;
    }

    @Column(name = "notes", length = 1000)
    public String getNotes() {
        return this.notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Column(name = "auto_payment_type")
    public Integer getAutoPaymentType() {
        return this.autoPaymentType;
    }

    public void setAutoPaymentType(Integer autoPaymentType) {
        this.autoPaymentType = autoPaymentType;
    }

    @Column(name = "due_date_unit_id")
    public Integer getDueDateUnitId() {
        return this.dueDateUnitId;
    }

    public void setDueDateUnitId(Integer dueDateUnitId) {
        this.dueDateUnitId = dueDateUnitId;
    }

    @Column(name = "due_date_value")
    public Integer getDueDateValue() {
        return this.dueDateValue;
    }

    public void setDueDateValue(Integer dueDateValue) {
        this.dueDateValue = dueDateValue;
    }

    @Column(name = "df_fm")
    public Integer getDfFm() {
        return this.dfFm;
    }

    public void setDfFm(Integer dfFm) {
        this.dfFm = dfFm;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
    public Set<CustomerDTO> getChildren() {
        return children;
    }

    public void setChildren(Set<CustomerDTO> children) {
        this.children = children;
    }

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    public CustomerDTO getParent() {
        return this.parent;
    }

    public void setParent(CustomerDTO parent) {
        this.parent = parent;
    }

    @Column(name = "is_parent")
    public Integer getIsParent() {
        return this.isParent;
    }

    public void setIsParent(Integer isParent) {
        this.isParent = isParent;
    }

    @Column(name = "exclude_aging", nullable = false)
    public int getExcludeAging() {
        return this.excludeAging;
    }

    public void setExcludeAging(int excludeAging) {
        this.excludeAging = excludeAging;
    }

    @Column(name = "invoice_child")
    public Integer getInvoiceChild() {
        return this.invoiceChild;
    }

    public void setInvoiceChild(Integer invoiceChild) {
        this.invoiceChild = invoiceChild;
    }

    @Column(name = "use_parent_pricing", nullable = false)
    public boolean getUseParentPricing() {
        return useParentPricing;
    }

    public boolean useParentPricing() {
        return useParentPricing;
    }

    public void setUseParentPricing(boolean useParentPricing) {
        this.useParentPricing = useParentPricing;
    }

    @Column(name = "balance_type")
    public int getBalanceType() {
        return balanceType;
    }

    public void setBalanceType(int balanceType) {
        this.balanceType = balanceType;
    }

    @Column(name = "auto_recharge")
    public BigDecimal getAutoRecharge() {
        if (autoRecharge == null)
            return BigDecimal.ZERO;

        return autoRecharge;
    }

    public void setAutoRecharge(BigDecimal autoRecharge) {
        this.autoRecharge = autoRecharge;
    }

    @Column(name = "credit_limit")
    public BigDecimal getCreditLimit() {
        if (creditLimit == null) {
            return BigDecimal.ZERO;
        }
        return creditLimit;
    }

    public void setCreditLimit(BigDecimal creditLimit) {
        this.creditLimit = creditLimit;
    }

    @Transient
    public Set<CustomerPriceDTO> getPrices() {
        return getBaseUser().getPrices();
    }

    public void setPrices(Set<CustomerPriceDTO> prices) {
        this.getBaseUser().setPrices(prices);
    }

    @Column(name = "dynamic_balance")
    public BigDecimal getDynamicBalance() {
        if (dynamicBalance == null) {
            return BigDecimal.ZERO;
        }
        return dynamicBalance;
    }

    public void setDynamicBalance(BigDecimal dynamicBalance) {
        this.dynamicBalance = dynamicBalance;
    }
    
    @Embedded
    public MainSubscriptionDTO getMainSubscription() {
		return mainSubscription;
	}

	public void setMainSubscription(MainSubscriptionDTO mainSubscription) {
		this.mainSubscription = mainSubscription;
	}

	@Version
    @Column(name = "OPTLOCK")
    public Integer getVersionNum() {
        return versionNum;
    }

    public void setVersionNum(Integer versionNum) {
        this.versionNum = versionNum;
    }

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    @JoinTable(
            name = "customer_meta_field_map",
            joinColumns = @JoinColumn(name = "customer_id"),
            inverseJoinColumns = @JoinColumn(name = "meta_field_value_id")
    )
    @Sort(type = SortType.COMPARATOR, comparator = MetaFieldHelper.MetaFieldValuesOrderComparator.class)
    public List<MetaFieldValue> getMetaFields() {
        return getMetaFieldsList();
    }

    @Transient
    public EntityType getCustomizedEntityType() {
        return EntityType.CUSTOMER;
    }

    @Transient
    public Integer getTotalSubAccounts() {
        LOG.debug("sub acounts = " + getChildren().size());
        return (getChildren().size() == 0) ? null : new Integer(getChildren().size());
    }

    @Override
    public String toString() {
        return "CustomerDTO{" +
               "id=" + id +
               ", baseUser.userId=" + (baseUser != null ? baseUser.getUserId() : null) +
               ", baseUser.userName=" + (baseUser != null ? baseUser.getUserName() : null) +
               ", dynamicBalance = " + this.dynamicBalance +
               ", credit limit = " + this.creditLimit +
               '}';
    }
}


