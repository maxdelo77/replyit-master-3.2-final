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
package com.sapienter.jbilling.server.payment.blacklist.db;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Version;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.server.metafields.db.MetaFieldValue;
import org.apache.log4j.Logger;

import com.sapienter.jbilling.server.user.contact.db.ContactDTO;
import com.sapienter.jbilling.server.user.db.CompanyDTO;
import com.sapienter.jbilling.server.user.db.CreditCardDTO;
import com.sapienter.jbilling.server.user.db.UserDTO;

@Entity
@TableGenerator(
        name="blacklist_GEN",
        table="jbilling_seqs",
        pkColumnName = "name",
        valueColumnName = "next_id",
        pkColumnValue="blacklist",
        allocationSize = 100
        )
@Table(name = "blacklist")
//@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class BlacklistDTO implements Serializable {

    // constants
    
    // blacklist types
    public static final Integer TYPE_USER_ID = new Integer(1);
    public static final Integer TYPE_NAME = new Integer(2);
    public static final Integer TYPE_CC_NUMBER = new Integer(3);
    public static final Integer TYPE_ADDRESS = new Integer(4);
    public static final Integer TYPE_IP_ADDRESS = new Integer(5);
    public static final Integer TYPE_PHONE_NUMBER = new Integer(6);
    
    // blacklist sources
    public static final Integer SOURCE_CUSTOMER_SERVICE = new Integer(1);
    public static final Integer SOURCE_EXTERNAL_UPLOAD = new Integer(2);
    public static final Integer SOURCE_USER_STATUS_CHANGE = new Integer(3);
    public static final Integer SOURCE_BILLING_PROCESS = new Integer(4);

    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(BlacklistDTO.class));

    // mapped columns
    
    @Id @GeneratedValue(strategy=GenerationType.TABLE, generator="blacklist_GEN")
    private Integer id;

    @ManyToOne
    @JoinColumn(name="entity_id", nullable=false)
    private CompanyDTO company;

    @Column(name = "create_datetime", nullable=false, length=29)
    private Date createDate;

    @Column(name = "type", nullable=false)
    private Integer type;

    @Column(name = "source", nullable=false)
    private Integer source;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="credit_card_id")
    private CreditCardDTO creditCard;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="contact_id")
    private ContactDTO contact;

    @ManyToOne
    @JoinColumn(name="user_id")
    private UserDTO user;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="meta_field_value_id")
    private MetaFieldValue metaFieldValue;

    @Version
    @Column(name="OPTLOCK")
    private Integer versionNum;

    public BlacklistDTO() {
    }

    public BlacklistDTO(Integer id, CompanyDTO company, Date createDate, 
            Integer type, Integer source, CreditCardDTO creditCard,
            ContactDTO contact, UserDTO user, MetaFieldValue metaFieldValue) {
        this.id = id;
        this.company = company;
        this.createDate = createDate;
        this.type = type;
        this.source = source;
        this.creditCard = creditCard;
        this.contact = contact;
        this.user = user;
        this.metaFieldValue = metaFieldValue;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setCompany(CompanyDTO company) {
        this.company = company;
    }

    public CompanyDTO getCompany() {
        return company;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }

    public void setSource(Integer source) {
        this.source = source;
    }

    public Integer getSource() {
        return source;
    }

    public void setCreditCard(CreditCardDTO creditCard) {
        this.creditCard = creditCard;
    }

    public CreditCardDTO getCreditCard() {
        return creditCard;
    }

    public void setContact(ContactDTO contact) {
        this.contact = contact;
    }

    public ContactDTO getContact() {
        return contact;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public UserDTO getUser() {
        return user;
    }

    public MetaFieldValue getMetaFieldValue() {
        return metaFieldValue;
    }

    public void setMetaFieldValue(MetaFieldValue metaFieldValue) {
        this.metaFieldValue = metaFieldValue;
    }

    protected int getVersionNum() {
        return versionNum; 
    }
}
