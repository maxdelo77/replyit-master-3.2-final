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

includeTargets << new File("${basedir}/scripts/CopyResources.groovy")
includeTargets << new File("${basedir}/scripts/CompileDesigns.groovy")
includeTargets << new File("${basedir}/scripts/CompileReports.groovy")
includeTargets << new File("${basedir}/scripts/PrepareTestDb.groovy")

target(prepareTest: "Prepares the testing environment, compiling all necessary resources and loading the test database.") {
    copyResources()
    compileDesigns()
    compileReports()
    prepareTestDb()

    println "Environment prepared for test run. Start grails with 'grails run-app' and run 'ant test'."
}

setDefaultTarget(prepareTest)

