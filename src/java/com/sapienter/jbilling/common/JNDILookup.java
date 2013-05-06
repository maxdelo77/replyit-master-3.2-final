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

package com.sapienter.jbilling.common;

import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

/**
 * EJB Home Factory, maintains a simple hashmap cache of EJBHomes
 * For a production implementations, exceptions such as NamingException
 * can be wrapped with a factory exception to futher simplify
 * the client.
 */
public class JNDILookup {

    private static final String DATABASE_JNDI = "java:/ApplicationDS";
    // this is then custom treated for serialization
    private static FormatLogger log = null;
    // this one is always checked for null
    private transient static JNDILookup aFactorySingleton = null;
    private transient Context ctx = null;

    /**
     * EJBHomeFactory private constructor.
     */
    private JNDILookup(boolean test) throws NamingException {
        log = new FormatLogger(Logger.getLogger(JNDILookup.class));
        if (test) {
            Hashtable env = new Hashtable();
            env.put(
                Context.INITIAL_CONTEXT_FACTORY,
                "org.jnp.interfaces.NamingContextFactory");
            env.put(
                Context.URL_PKG_PREFIXES,
                "org.jboss.naming:org.jnp.interfaces");
            env.put(Context.PROVIDER_URL, "localhost");
            ctx = new InitialContext(env);
            log.info("Context set with environment.");
        } else {
            ctx = new InitialContext();
            log.info("Default Context set");
        }
    }
    
    public static JNDILookup getFactory(boolean test)
        throws NamingException {

        if (JNDILookup.aFactorySingleton == null) {
            JNDILookup.aFactorySingleton = new JNDILookup(test);
            log.info("New EJBFactory created.");
        }

        return JNDILookup.aFactorySingleton;
    }

    /*
     * Returns the singleton instance of the EJBHomeFactory
     * The sychronized keyword is intentionally left out the
     * as I don't think the potential to intialize the singleton
     * twice at startup time (which is not a destructive event)
     * is worth creating a sychronization bottleneck on this
     * VERY frequently used class, for the lifetime of the
     * client application.
     */
    public static JNDILookup getFactory() throws NamingException {
        return getFactory(false);
    }

    public DataSource lookUpDataSource() {
        return (DataSource) com.sapienter.jbilling.server.util.Context.getBean(
                com.sapienter.jbilling.server.util.Context.Name.DATA_SOURCE);
    }

}
