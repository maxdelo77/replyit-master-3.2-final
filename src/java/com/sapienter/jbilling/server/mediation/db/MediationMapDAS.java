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

package com.sapienter.jbilling.server.mediation.db;

import com.sapienter.jbilling.server.util.db.AbstractDAS;
import org.hibernate.Query;

import java.util.List;

public class MediationMapDAS extends AbstractDAS<MediationOrderMap> {

    private static final String findOrdersByMediationHQL =
            "select orderId "
                    + " FROM MediationOrderMap "
                    + " WHERE mediationProcessId = :mediationProcessId ";

    private static final String findInvoicesByMediationSQL =
            "select op.invoice_id "
                    + " FROM mediation_order_map as mom INNER JOIN purchase_order as po on mom.order_id=po.id "
                    +"  INNER JOIN order_process as op on op.order_id = po.id "
                    + " WHERE mom.mediation_process_id = :mediationProcessId ";

    @SuppressWarnings("unchecked")
    public List<Integer> getOrdersByMediationProcess(Integer mediationProcessId) {
        Query query = getSession().createQuery(findOrdersByMediationHQL);
        query.setParameter("mediationProcessId" , mediationProcessId);
        return query.list();
    }

    @SuppressWarnings("unchecked")
    public List<Integer> getInvoicesByMediationProcess(Integer mediationProcessId) {
        Query query = getSession().createSQLQuery(findInvoicesByMediationSQL);
        query.setParameter("mediationProcessId" , mediationProcessId);
        return query.list();
    }
}
