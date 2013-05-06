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

package com.sapienter.jbilling.server.payment.db;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.sapienter.jbilling.server.util.db.AbstractDAS;

/**
 * 
 * @author abimael
 *
 */
public class PaymentInfoChequeDAS extends AbstractDAS<PaymentInfoChequeDTO> {

    public PaymentInfoChequeDTO create() {
        
        return new PaymentInfoChequeDTO();
    }
    
    public PaymentInfoChequeDTO findByPayment(PaymentDTO payment) {
        Criteria criteria = getSession().createCriteria(PaymentInfoChequeDTO.class);
        criteria.add(Restrictions.eq("payment", payment));
        return (PaymentInfoChequeDTO) criteria.uniqueResult();
    }

}
