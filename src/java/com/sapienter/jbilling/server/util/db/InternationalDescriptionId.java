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


import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class InternationalDescriptionId  implements java.io.Serializable {


     private int tableId;
     private int foreignId;
     private String psudoColumn;
     private int languageId;

    public InternationalDescriptionId() {
    }

    public InternationalDescriptionId(int tableId, int foreignId, String psudoColumn, int languageId) {
       this.tableId = tableId;
       this.foreignId = foreignId;
       this.psudoColumn = psudoColumn;
       this.languageId = languageId;
    }
   

    @Column(name="table_id", nullable=false)
    public int getTableId() {
        return this.tableId;
    }
    
    public void setTableId(int tableId) {
        this.tableId = tableId;
    }

    @Column(name="foreign_id", nullable=false)
    public int getForeignId() {
        return this.foreignId;
    }
    
    public void setForeignId(int foreignId) {
        this.foreignId = foreignId;
    }

    @Column(name="psudo_column", nullable=false, length=20)
    public String getPsudoColumn() {
        return this.psudoColumn;
    }
    
    public void setPsudoColumn(String psudoColumn) {
        this.psudoColumn = psudoColumn;
    }

    @Column(name="language_id", nullable=false)
    public int getLanguageId() {
        return this.languageId;
    }
    
    public void setLanguageId(int languageId) {
        this.languageId = languageId;
    }


   public boolean equals(Object other) {
         if ( (this == other ) ) return true;
         if ( (other == null ) ) return false;
         if ( !(other instanceof InternationalDescriptionId) ) return false;
         InternationalDescriptionId castOther = ( InternationalDescriptionId ) other; 
         
		return (this.getTableId() == castOther.getTableId())
				&& (this.getForeignId() == castOther.getForeignId())
				&& ((this.getPsudoColumn() == castOther.getPsudoColumn()) || (this
						.getPsudoColumn() != null
						&& castOther.getPsudoColumn() != null && this
						.getPsudoColumn().equals(castOther.getPsudoColumn())))
				&& (this.getLanguageId() == castOther.getLanguageId());
	}
   
   public int hashCode() {
         int result = 17;
         
         result = 37 * result + this.getTableId();
         result = 37 * result + this.getForeignId();
         result = 37 * result + ( getPsudoColumn() == null ? 0 : this.getPsudoColumn().hashCode() );
         result = 37 * result + this.getLanguageId();
         return result;
   }   

   public String toString() {
       return "foreignId " + foreignId + " languageId " + languageId + " psudoColumn " + psudoColumn +
               " tableId " + tableId;
   }
}


