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

package com.sapienter.jbilling.server.user.contact.db;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.sapienter.jbilling.server.util.db.AbstractDAS;

public class ContactTypeDAS extends AbstractDAS<ContactTypeDTO> {

    public ContactTypeDTO findPrimary(Integer entityId) {
        Criteria criteria = getSession().createCriteria(ContactTypeDTO.class)
            .createAlias("entity", "e")
                .add(Restrictions.eq("e.id", entityId))
            .add(Restrictions.eq("isPrimary", 1));
        
        return (ContactTypeDTO) criteria.uniqueResult();
    }
}
