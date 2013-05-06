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

package com.sapienter.jbilling.server.pricing;

import com.sapienter.jbilling.server.mediation.cache.BasicLoaderImpl;
import com.sapienter.jbilling.server.mediation.cache.ILoader;
import com.sapienter.jbilling.server.mediation.task.IMediationReader;
import com.sapienter.jbilling.server.mediation.task.StatelessJDBCReader;
import com.sapienter.jbilling.server.pricing.cache.RateCardFinder;
import com.sapienter.jbilling.server.pricing.db.RateCardDTO;
import com.sapienter.jbilling.server.util.Context;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * RateCardBeanFactory
 *
 * @author Brian Cowdery
 * @since 18-02-2012
 */
public class RateCardBeanFactory {

    private final RateCardDTO rateCard;

    public RateCardBeanFactory(RateCardDTO rateCard) {
        this.rateCard = rateCard;
    }

    public AbstractBeanDefinition getReaderBeanDefinition(Integer entityId) {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("table_name", rateCard.getTableName());
        parameters.put("key_column_name", "id");
        parameters.put("batch_size", String.valueOf(RateCardBL.BATCH_SIZE));

        BeanDefinitionBuilder beanDef = BeanDefinitionBuilder.rootBeanDefinition(StatelessJDBCReader.class);
        beanDef.setLazyInit(true);

        beanDef.addPropertyReference("jdbcTemplate", Context.Name.JDBC_TEMPLATE.getName());
        beanDef.addPropertyReference("dataSource", Context.Name.DATA_SOURCE.getName());
        beanDef.addPropertyValue("parameters", parameters);
        beanDef.addPropertyValue("entityId", entityId);

        return beanDef.getBeanDefinition();
    }
    public String getReaderBeanName() {
        return toBeanName(rateCard.getTableName()) + "Reader";
    }

    public IMediationReader getReaderInstance() {
        return Context.getBean(getReaderBeanName());
    }

    public AbstractBeanDefinition getLoaderBeanDefinition(String readerBeanName) {
        BeanDefinitionBuilder beanDef = BeanDefinitionBuilder.rootBeanDefinition(BasicLoaderImpl.class);
        beanDef.setLazyInit(true);
        beanDef.setInitMethodName("init");
        beanDef.setDestroyMethodName("destroy");

        beanDef.addPropertyReference("jdbcTemplate", Context.Name.MEMCACHE_JDBC_TEMPLATE.getName());
        beanDef.addPropertyReference("transactionTemplate", Context.Name.MEMCACHE_TX_TEMPLATE.getName());
        beanDef.addPropertyReference("reader", readerBeanName);
        beanDef.addPropertyValue("tableName", rateCard.getTableName());
        beanDef.addPropertyValue("indexName", rateCard.getTableName() + "_idx");
        beanDef.addPropertyValue("indexColumnNames", "match");

        return beanDef.getBeanDefinition();
    }

    public String getLoaderBeanName() {
        return toBeanName(rateCard.getTableName()) + "Loader";
    }

    public ILoader getLoaderInstance() {
        return Context.getBean(getLoaderBeanName());
    }

    public AbstractBeanDefinition getFinderBeanDefinition(String loaderBeanName) {
        BeanDefinitionBuilder beanDef = BeanDefinitionBuilder.rootBeanDefinition(RateCardFinder.class);
        beanDef.setLazyInit(true);
        beanDef.setInitMethodName("init");

        beanDef.addConstructorArgReference(Context.Name.MEMCACHE_JDBC_TEMPLATE.getName());
        beanDef.addConstructorArgReference(loaderBeanName);

        return beanDef.getBeanDefinition();
    }

    public String getFinderBeanName() {
        return toBeanName(rateCard.getTableName()) + "Finder";
    }

    public RateCardFinder getFinderInstance() {
        return Context.getBean(getFinderBeanName());
    }

    /**
     * Converts a rate card table name to a camelCase bean name to use registering
     * rating spring beans.
     *
     * @param tableName rate card table name
     */
    private static String toBeanName(String tableName) {
        StringBuilder builder = new StringBuilder();

        String[] tokens = tableName.split("_");
        for (int i = 0; i < tokens.length; i++) {
            if (i == 0) {
                builder.append(tokens[i]);
            } else {
                builder.append(StringUtils.capitalize(tokens[i]));
            }
        }

        return builder.toString();
    }
}
