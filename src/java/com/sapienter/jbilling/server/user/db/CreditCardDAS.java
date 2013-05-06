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

import java.util.List;


import com.sapienter.jbilling.server.util.db.AbstractDAS;
import org.hibernate.Query;

public class CreditCardDAS extends AbstractDAS<CreditCardDTO> {

    private static final String findByLastDigits =
            " select b.id " +
            "   from UserDTO b join b.creditCards c " +
            "  where b.company.id = :entity " +
            "    and c.ccNumberPlain = :plain " +
            "    and b.deleted = 0 " +
            "    and c.deleted = 0";
    
    private static final String findByCreditCard =
            " select distinct bu.userName " +
            " 	from UserDTO bu, PaymentDTO p, CreditCardDTO cc " + 
            " where cc.rawNumber = :number " +
            " 	and cc.id = p.creditCard.id " +
            " 	and p.baseUser.id = bu.id";

    public List<Integer> findByLastDigits(Integer entityId, String plain) {
        Query query = getSession().createQuery(findByLastDigits);
        query.setParameter("entity", entityId);
        query.setParameter("plain", plain);
        query.setComment("CreditCardDAS.findByLastDigits " + entityId + " " + plain);
        return query.list();
    }

    public List<String> findByNumber(String number){
        Query query = getSession().createQuery(findByCreditCard);
        query.setParameter("number", number);
        query.setComment("CreditCardDAS.findByCreditCard " + number);
        return query.list();
    }
}
