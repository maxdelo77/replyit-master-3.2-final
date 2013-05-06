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

import com.sapienter.jbilling.server.order.db.OrderDTO;
import org.hibernate.criterion.Restrictions;
import org.hibernate.Criteria;

public class CurrencyDAS extends AbstractDAS<CurrencyDTO> {

	public boolean findAssociationExistsForCurrency(Integer currencyId, Class associationClass, String currencyFieldName) {
		
		Criteria criteria =getSession().createCriteria(associationClass)
                            .add(Restrictions.eq(currencyFieldName + ".id", currencyId));
        
        return findFirst(criteria) != null;
	}
}
