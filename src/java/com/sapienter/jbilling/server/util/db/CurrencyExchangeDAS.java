
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

import org.hibernate.Query;

import java.util.Date;
import java.util.List;


public class CurrencyExchangeDAS extends AbstractDAS<CurrencyExchangeDTO> {

    private static final String findExchangeForDateSQL =
        "SELECT a " +
        "  FROM CurrencyExchangeDTO a " +
        " WHERE a.entityId = :entity " +
        "   AND a.currency.id = :currency " +
        "   AND a.validSince <= :date ORDER BY a.validSince DESC";

    private static final String findExchangeInRangeSQL =
        "SELECT a " +
        "  FROM CurrencyExchangeDTO a " +
        " WHERE a.entityId = :entity " +
        "   AND a.currency.id = :currency " +
        "   AND a.validSince >= :dateFrom " +
        "   AND a.validSince <= :dateTo ORDER BY a.validSince DESC";

    private static final String  findByEntitySQL =
        " SELECT a " +
        "   FROM CurrencyExchangeDTO a " +
        "  WHERE a.entityId = :entity";

    public CurrencyExchangeDTO findExchange(Integer entityId, Integer currencyId) {
        return getExchangeRateForDate(entityId, currencyId, new Date());
    }

    public List<CurrencyExchangeDTO> findByEntity(Integer entityId) {
        Query query = getSession().createQuery(findByEntitySQL);
        query.setParameter("entity", entityId);
        return query.list();
    }

    /**
     * Returns an exchange rate closest to a specified date
     */
    public CurrencyExchangeDTO getExchangeRateForDate(Integer entityId, Integer currencyId, Date forDate) {
        Query query = getSession().createQuery(findExchangeForDateSQL);
        query.setParameter("entity", entityId);
        query.setParameter("currency", currencyId);
        query.setParameter("date", forDate);
        final List<CurrencyExchangeDTO> results = query.list();
        if(results.isEmpty()) {
            return null;
        }
        return  results.get(0);
    }

    /**
     * Returns an exchange rate from specified date range closest to a 'to' parameter
     */
    public CurrencyExchangeDTO getExchangeRateForRange(Integer entityId, Integer currencyId, Date from, Date to) {
        Query query = getSession().createQuery(findExchangeInRangeSQL);
        query.setParameter("entity", entityId);
        query.setParameter("currency", currencyId);
        query.setParameter("dateFrom", from);
        query.setParameter("dateTo", to);
        final List<CurrencyExchangeDTO> results = query.list();
        if(results.isEmpty()) {
            return null;
        }
        return  results.get(0);
    }


}
