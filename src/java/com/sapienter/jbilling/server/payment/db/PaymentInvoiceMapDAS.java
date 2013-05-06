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

import com.sapienter.jbilling.server.invoice.db.InvoiceDAS;
import com.sapienter.jbilling.server.invoice.db.InvoiceDTO;
import com.sapienter.jbilling.server.util.db.AbstractDAS;
import org.hibernate.Criteria;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

/**
 *
 * @author abimael
 *
 */
public class PaymentInvoiceMapDAS extends AbstractDAS<PaymentInvoiceMapDTO> {

    public PaymentInvoiceMapDTO create(InvoiceDTO invoice, PaymentDTO payment, BigDecimal realAmount) {
        PaymentInvoiceMapDTO map = new PaymentInvoiceMapDTO();
        map.setInvoiceEntity(invoice);
        map.setPayment(payment);
        map.setAmount(realAmount);
        map.setCreateDatetime(Calendar.getInstance().getTime());

        return save(map);
    }

    public void deleteAllWithInvoice(InvoiceDTO invoice) {
        InvoiceDTO inv = new InvoiceDAS().find(invoice.getId());
        Criteria criteria = getSession().createCriteria(PaymentInvoiceMapDTO.class);
        criteria.add(Restrictions.eq("invoiceEntity", inv));

        List<PaymentInvoiceMapDTO> results = criteria.list();

        if (results != null && !results.isEmpty()) {
            for (PaymentInvoiceMapDTO paym : results) {
                delete(paym);
            }
        }
    }

    public BigDecimal getLinkedInvoiceAmount(PaymentDTO payment, InvoiceDTO invoice) {

        Criteria criteria = getSession().createCriteria(PaymentInvoiceMapDTO.class);
        criteria.add(Restrictions.eq("payment", payment));
        criteria.add(Restrictions.eq("invoiceEntity", invoice));
        criteria.setProjection(Projections.sum("amount"));
        return criteria.uniqueResult() == null ? BigDecimal.ZERO : (BigDecimal) criteria.uniqueResult();

    }

    public PaymentInvoiceMapDTO getRow(PaymentDTO payment, InvoiceDTO invoice) {

        Criteria criteria = getSession().createCriteria(PaymentInvoiceMapDTO.class);
        criteria.add(Restrictions.eq("payment", payment));
        criteria.add(Restrictions.eq("invoiceEntity", invoice));

        return criteria.uniqueResult() == null ? null : (PaymentInvoiceMapDTO) criteria.uniqueResult();
    }

    public PaymentInvoiceMapDTO getRow(Integer id) {

        Criteria criteria = getSession().createCriteria(PaymentInvoiceMapDTO.class);
        criteria.add(Restrictions.eq("id", id));

        return criteria.uniqueResult() == null ? null : (PaymentInvoiceMapDTO) criteria.uniqueResult();
    }

}
