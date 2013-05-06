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

includeTargets << grailsScript("_GrailsDocs")
includeTargets << new File("${basedir}/scripts/SetLicense.groovy")
includeTargets << new File("${basedir}/scripts/CheckDbConnection.groovy")

eventCreateWarStart = { warName, stagingDir ->
    println("Compiling documentation ...")
    docs()

    println "Copying generated documentation to staging dir"
    ant.copy(todir: "${stagingDir}/") {
        fileset(dir: "${basedir}/target/docs/", includes: "manual/**")
    }
}

eventCompileEnd = {
    setLicense()
    checkDbConnection()
}
