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

import org.hibernate.criterion.Restrictions;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.common.SessionInternalError;
import org.apache.log4j.Logger;
import org.springmodules.cache.provider.CacheProviderFacade;
import org.springmodules.cache.CachingModel;

public class JbillingTableDAS extends AbstractDAS<JbillingTable> {

    private CacheProviderFacade cache;
    private CachingModel cacheModel;

    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(JbillingTableDAS.class));

    protected JbillingTableDAS() {
        super();
    }

    // can not cache directly, CGLib can not proxy extenders of <> classes
    public JbillingTable findByName(String name) {
        JbillingTable table = (JbillingTable) cache.getFromCache("JbillingTable" + name, cacheModel);
        if (table == null) {
            LOG.debug("Looking for table + " + name);
            table = findByCriteriaSingle(Restrictions.eq("name", name));
            if (table == null) {
                throw new SessionInternalError("Can not find table " + name);
            } else {
                cache.putInCache("JbillingTable" + name, cacheModel, table);

            }
        }
        return table;
    }

    public void setCache(CacheProviderFacade cache) {
        this.cache = cache;
    }

    public void setCacheModel(CachingModel model) {
        cacheModel = model;
    }

    public static JbillingTableDAS getInstance() {
        return new JbillingTableDAS();
    }
}
