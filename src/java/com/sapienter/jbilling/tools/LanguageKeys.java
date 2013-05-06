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

/*
 * Created on Sep 24, 2004
 *
 * Shows which keys are missing for a language in the ApplicationProperties
 */
package com.sapienter.jbilling.tools;

import java.io.FileInputStream;
import java.util.Iterator;
import java.util.Properties;

/**
 * @author Emil
 *
 */
public class LanguageKeys {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: LanguageKeys language_code");
        }
        
        String language = args[0];
        
        try {
            // open the default properties page
            Properties globalProperties = new Properties();
            FileInputStream propFile = new FileInputStream(
                    "ApplicationResources.properties");
            globalProperties.load(propFile);

            // and the one for the specifed language
            Properties languageProperties = new Properties();
            propFile = new FileInputStream(
                    "ApplicationResources_" + language + ".properties");
            languageProperties.load(propFile);
            
            // no go through all the keys
            for (Iterator it = globalProperties.keySet().iterator(); 
                    it.hasNext(); ) {
                String key = (String) it.next();
                if (!languageProperties.containsKey(key)) {
                    System.out.println(key + "=" + 
                            globalProperties.getProperty(key));
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }
}
