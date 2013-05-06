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


includeTargets << grailsScript("_GrailsInit")

target(setLicense: "Set the license key in jbilling.properties with whatever is in license.txt") {
    println "Setting license in jbilling.properties from license.txt"
    ant.loadfile(property:"licenseKey", srcFile:"license.txt", quiet:"true")

    if (licenseKey) {
        ant.replace(file: "${basedir}/src/java/jbilling.properties", propertyFile: "license.txt") {
            replacefilter(token:"licensee name", property:"licensee")
        }
        ant.replace(file: "${basedir}/src/java/jbilling.properties", propertyFile: "license.txt") {
            replacefilter(token:"place license key here", property:"licenseKey")
        }
    }
}
