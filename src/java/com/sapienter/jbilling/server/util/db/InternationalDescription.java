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
package com.sapienter.jbilling.server.util.db;


import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name="international_description")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class InternationalDescription  implements java.io.Serializable {


     private InternationalDescriptionId id;
     private String content;
     // OPTLOCK: it makes no sense in this entity. The optimistic locking id done at the
     // parent level (the entity that delegates the column to this one).
     // OR, the content is simple readonly

    public InternationalDescription() {
    }

    public InternationalDescription(InternationalDescriptionId id, String content) {
       this.id = id;
       this.content = content;
    }
   
     @EmbeddedId
    
    @AttributeOverrides( {
        @AttributeOverride(name="tableId", column=@Column(name="table_id", nullable=false) ), 
        @AttributeOverride(name="foreignId", column=@Column(name="foreign_id", nullable=false) ), 
        @AttributeOverride(name="psudoColumn", column=@Column(name="psudo_column", nullable=false, length=20) ), 
        @AttributeOverride(name="languageId", column=@Column(name="language_id", nullable=false) ) } )
    public InternationalDescriptionId getId() {
        return this.id;
    }
    
    public void setId(InternationalDescriptionId id) {
        this.id = id;
    }
    
    @Column(name="content", nullable=false, length=5000)
    public String getContent() {
        return this.content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
}


