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

package com.sapienter.jbilling.server.user.partner.db;

import com.sapienter.jbilling.server.util.db.AbstractDAS;
import org.hibernate.Query;

public class PartnerDAS extends AbstractDAS<Partner> {

	private static final String CURRENCY_USAGE_FOR_ENTITY_SQL =
	          "SELECT count(*) " +
	          "  FROM Partner a " +
	          " WHERE a.feeCurrency.id = :currency " +
	          "	  AND a.baseUser.company.id = :entity "+
	          "   AND a.baseUser.deleted = 0";
	
	public Long findPartnerCountByCurrencyAndEntity(Integer currencyId, Integer entityId){
      Query query = getSession().createQuery(CURRENCY_USAGE_FOR_ENTITY_SQL);
      query.setParameter("currency", currencyId);
      query.setParameter("entity", entityId);
      
      return (Long) query.uniqueResult();
  }

}
