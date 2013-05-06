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

package com.sapienter.jbilling.server.pluggableTask.admin;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.server.util.db.AbstractDAS;

public class PluggableTaskTypeDAS extends AbstractDAS<PluggableTaskTypeDTO> {
	
	private static final FormatLogger LOG =  new FormatLogger(Logger.getLogger(PluggableTaskTypeDAS.class));
	
    private static final String findByCategorySQL =
	        "SELECT b " +
	        "  FROM PluggableTaskTypeDTO b " + 
	        " WHERE b.category.id = :category" +
	        " ORDER BY b.id";

	public List<PluggableTaskTypeDTO> findAllByCategory(Integer categoryId) {
		LOG.debug("finding types for category " + categoryId);
		Query query = getSession().createQuery(findByCategorySQL);
        query.setParameter("category", categoryId);
        query.setComment("PluggableTaskTypeDAS.findAllByCategory");
        List<PluggableTaskTypeDTO> ret = query.list();
		LOG.debug("found " + ret.size());

        return ret;
	}

}
