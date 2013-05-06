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

package com.sapienter.jbilling.server.user;

import com.sapienter.jbilling.server.user.contact.db.ContactTypeDTO;
import com.sapienter.jbilling.server.util.InternationalDescriptionWS;
import com.sapienter.jbilling.server.util.db.LanguageDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * ContactTypeWS
 *
 * @author Brian Cowdery
 * @since 27/01/11
 */
public class ContactTypeWS {

    private Integer id;
    private Integer companyId;
    private Integer isPrimary;
    private List<InternationalDescriptionWS> descriptions = new ArrayList<InternationalDescriptionWS>();

    public ContactTypeWS() {
    }

    public ContactTypeWS(ContactTypeDTO contactType, List<LanguageDTO> languages) {
        this.id = contactType.getId();
        this.isPrimary = contactType.getIsPrimary();

        if (contactType.getEntity() != null)
            this.companyId = contactType.getEntity().getId();

        for (LanguageDTO language : languages) {
            descriptions.add(new InternationalDescriptionWS(contactType.getDescriptionDTO(language.getId())));
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public Integer getPrimary() {
        return isPrimary;
    }

    public void setPrimary(Integer primary) {
        isPrimary = primary;
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

    @Override
    public String toString() {
        return "ContactTypeWS{"
               + "descriptions=" + descriptions
               + ", id=" + id
               + ", companyId=" + companyId
               + ", isPrimary=" + isPrimary
               + '}';
    }
}
