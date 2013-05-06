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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * This is a Singleton call that provides the system properties from
 * the jbilling.properties file
 */
public class SystemProperties {
    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(SystemProperties.class));

    private static final String JBILLING_HOME = "JBILLING_HOME";
    private static final String PROPERTIES_FILE = "jbilling.properties";
    private static final String RESOURCES_DIR = "resources";
    private static final String BASE_DIR_PROPERTY = "base_dir";

    private static SystemProperties INSTANCE;

    private String resourcesDir = null;
    private Properties prop = null;

    /*
        private singleton constructor
     */
    private SystemProperties() throws IOException {
        File properties = getPropertiesFile();
        FileInputStream stream = new FileInputStream(properties);

        prop = new Properties();
        prop.load(stream);

        stream.close();

        LOG.debug("System properties loaded from: %s", properties.getPath());
        System.out.println("System properties loaded from: " + properties.getPath());

        resourcesDir = getJBillingResourcesDir();

        LOG.debug("Resolved jbilling resources directory to: %s", resourcesDir);
        System.out.println("Resolved jbilling resources directory to: " + resourcesDir);
    }

    /**
     * Returns a singleton instance of SystemProperties
     *
     * @return instance
     * @throws IOException if properties could not be loaded
     */
    public static SystemProperties getSystemProperties()  throws IOException{
        if (INSTANCE == null)
            INSTANCE = new SystemProperties();
        return INSTANCE;
    }

    /**
     * Returns the jBilling home path where resources and configuration files
     * can be found.
     *
     * The environment variable JBILLING_HOME and system property JBILLING_HOME are examined
     * for this value, with precedence given to system properties set via command line arguments.
     *
     * If no jBilling home path is set, properties will be loaded from the classpath.
     *
     * @return jbilling home path
     */
    public static String getJBillingHome() {
        String jbillingHome = System.getProperty(JBILLING_HOME);

        if (jbillingHome == null) {
            jbillingHome = System.getenv(JBILLING_HOME);
        }

        return jbillingHome;
    }

    /**
     * Returns the path to the jBilling resources directory.
     *
     * The resources directory is always assumed to be located in JBILLING_HOME. If JBILLING_HOME is not
     * set, this method will return a relative path as the default location for the resources directory.
     *
     * @return path to the resources directory
     */
    public String getJBillingResourcesDir() {
        // try JBILLING_HOME
        String jbillingHome = getJBillingHome();
        if (jbillingHome != null) {
            return jbillingHome + File.separator + RESOURCES_DIR + File.separator;
        }

        try {
            // try root dir
            File resources = new File("." + File.separator + RESOURCES_DIR);
            if (resources.exists()) {
                return resources.getCanonicalPath() + File.separator;
            }

            // try one level down (tomcat root)
            resources = new File(".." + File.separator + RESOURCES_DIR);
            if (resources.exists()) {
                return resources.getCanonicalPath() + File.separator;
            }
        } catch (IOException e) {
            LOG.warn("IOException when attempting to resolve canonical path to jbilling resources/", e);
        }

        return "";
    }

    /**
     * Returns the path to the jbilling.properties file.
     *
     * @return properties file
     */
    public static File getPropertiesFile() {
        String jbillingHome = getJBillingHome();
        if (jbillingHome != null) {
            // properties file from filesystem
            return new File(jbillingHome + File.separator + PROPERTIES_FILE);

        } else {
            // properties file from classpath
            URL url = SystemProperties.class.getResource("/" + PROPERTIES_FILE);
            return new File(url.getFile());
        }
    }

    public String get(String key) throws Exception {
        // "base_dir" should always resolve to the JBILLING_HOME resources dir
        // this value is no longer part of jbilling.properties
        if (BASE_DIR_PROPERTY.equals(key)) {
            return resourcesDir;
        }

        // get value from jbilling.properties
        String value = prop.getProperty(key);

        if (value == null)
            throw new Exception("Missing system property: " + key);

        return value;
    }
    
    public String get(String key, String defaultValue) {
        return prop.getProperty(key, defaultValue);
    }
}
