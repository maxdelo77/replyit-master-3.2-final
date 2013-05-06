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

package com.sapienter.jbilling.server.util;

import com.sapienter.jbilling.server.util.db.InternationalDescriptionDTO;

import java.io.Serializable;

/**
 * InternationalDescriptionWS
 *
 * @author Brian Cowdery
 * @since 27/01/11
 */
public class InternationalDescriptionWS implements Serializable{

    private String psudoColumn;
    private Integer languageId;
    private String content;
    private boolean deleted;

    public InternationalDescriptionWS() {
    }

    public InternationalDescriptionWS(Integer languageId, String content) {
        this.psudoColumn = "description";
        this.languageId = languageId;
        this.content = content;
    }

    public InternationalDescriptionWS(String psudoColumn, Integer languageId, String content) {
        this.psudoColumn = psudoColumn;
        this.languageId = languageId;
        this.content = content;
    }

    public InternationalDescriptionWS(InternationalDescriptionDTO description) {
        if (description.getId() != null) {
            this.psudoColumn = description.getId().getPsudoColumn();
            this.languageId = description.getId().getLanguageId();
        }
        this.content = description.getContent();
    }

    /**
     * Alias for {@link #getPsudoColumn()}
     * @return psudo-column label
     */
    public String getLabel() {
        return getPsudoColumn();
    }

    /**
     * Alias for {@link #setPsudoColumn(String)}
     * @param label psudo-column label string
     */
    public void setLabel(String label) {
        setPsudoColumn(label);
    }

    public String getPsudoColumn() {
        return psudoColumn;
    }

    public void setPsudoColumn(String psudoColumn) {
        this.psudoColumn = psudoColumn;
    }

    public Integer getLanguageId() {
        return languageId;
    }

    public void setLanguageId(Integer languageId) {
        this.languageId = languageId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public String toString() {
        return "InternationalDescriptionWS{"
               + ", psudoColumn='" + psudoColumn + '\''
               + ", languageId=" + languageId
               + ", content='" + content + '\''
               + '}';
    }
}
