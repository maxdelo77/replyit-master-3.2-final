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
package com.sapienter.jbilling.server.util;

import org.springframework.context.ApplicationContext;

/**
 * Static factory for accessing Spring beans from the local container.
 */
public class Context {

    // spring application context, injected by context aware clients
    private static ApplicationContext spring = null;

    // defined bean names
    public enum Name {
        // jbilling session beans
        ITEM_SESSION                    ("itemSession"),
        NOTIFICATION_SESSION            ("notificationSession"),
        CUSTOMER_SESSION                ("customerSession"),
        LIST_SESSION                    ("listSession"),
        USER_SESSION                    ("userSession"),
        INVOICE_SESSION                 ("invoiceSession"),
        ORDER_SESSION                   ("orderSession"),
        PLUGGABLE_TASK_SESSION          ("pluggableTaskSession"),
        PAYMENT_SESSION                 ("paymentSession"),
        MEDIATION_SESSION               ("mediationSession"),
        BILLING_PROCESS_SESSION         ("billingProcessSession"),
        PROVISIONING_PROCESS_SESSION    ("provisioningProcessSession"),
        WEB_SERVICES_SESSION            ("webServicesSession"),

        // jbilling data access service beans
        DESCRIPTION_DAS     ("internationalDescriptionDAS"),
        JBILLING_TABLE_DAS  ("jbillingTableDAS"),
        PLUGGABLE_TASK_DAS  ("pluggableTaskDAS"),

        // jbilling beans
        PROVISIONING                        ("provisioning"),
        INTERNAL_EVENTS_RULES_TASK_CONFIG   ("internalEventsRulesTaskConfig"),

        // persistence
        DATA_SOURCE         ("dataSource"),
        TRANSACTION_MANAGER ("transactionManager"),
        HIBERNATE_SESSION   ("sessionFactory"),
        JDBC_TEMPLATE       ("jdbcTemplate"),

        // security
        SPRING_SECURITY_SERVICE ("springSecurityService"),
        AUTHENTICATION_MANAGER  ("authenticationManager"),
        PASSWORD_ENCODER        ("passwordEncoder"),

        // cache
        CACHE                           ("cacheProviderFacade"),
        CACHE_MODEL_READONLY            ("cacheModelReadOnly"),
        CACHE_MODEL_RW                  ("cacheModelPTDTO"),
        CACHE_FLUSH_MODEL_RW            ("flushModelPTDTO"),

        CURRENCY_CACHE_MODEL            ("cacheModelCurrency"),
        CURRENCY_FLUSH_MODEL            ("flushModelCurrency"),

        // HSQLDB data loader cache
        MEMCACHE_DATASOURCE                 ("memcacheDataSource"),
        MEMCACHE_JDBC_TEMPLATE              ("memcacheJdbcTemplate"),
        MEMCACHE_TX_TEMPLATE                ("memcacheTransactionTemplate"),
        PRICING_FINDER                      ("pricingFinder"),
        NANPA_CALL_IDENTIFICATION_FINDER    ("callIdentificationFinder"),

        // jms
        JMS_TEMPLATE                            ("jmsTemplate"),
        PROCESSORS_DESTINATION                  ("processorsDestination"),
        PROVISIONING_COMMANDS_DESTINATION       ("provisioningCommandsDestination"),
        PROVISIONING_COMMANDS_REPLY_DESTINATION ("provisioningCommandsReplyDestination"),

        // misc
        CAI                 ("cai"),
        MMSC                ("mmsc"),
        VELOCITY            ("velocityEngine"),
        DATA_ENCRYPTER      ("dataEncrypter");
       
        private String name;
        Name(String name) { this.name = name; }
        public String getName() { return name; }
    }

    // static factory cannot be instantiated
    private Context() {
    }

    public static void setApplicationContext(ApplicationContext ctx) {
        spring = ctx;
    }

    public static ApplicationContext getApplicationContext() {
        return spring;
    }

    /**
     * Returns a Spring Bean of type T for the given Context.Name
     *
     * @param bean remote context name
     * @param <T> bean type
     * @return bean from remote context
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(Name bean) {
        return (T) getApplicationContext().getBean(bean.getName());
    }

    /**
     * Returns a Spring Bean of type T for the given name
     *
     * @param beanName bean name
     * @param <T> bean type
     * @return bean from remote context
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String beanName) {
        return (T)  getApplicationContext().getBean(beanName);
    }
}
