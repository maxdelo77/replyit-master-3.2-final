import org.codehaus.groovy.grails.commons.ConfigurationHolder as CH

import groovy.sql.Sql

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

target(checkDbConnection: "Checks the connection to the database and prints the errors if there are any.") {
    depends(createConfig)

    String url = CH.config.dataSource.url
    String driver = CH.config.dataSource.driverClassName
    String userName = CH.config.dataSource.username
    String password = CH.config.dataSource.password

    try {
        println "Checking the connection to the DB..."
        def sql = Sql.newInstance(url, userName, password, driver)
        println "Connected to the DB successfully!!!"
    } catch (Exception e) {
        System.out.println("An error ocurred while trying to connect to the DB...");
        e.printStackTrace();
    }
}

//setDefaultTarget(checkDbConnection)
