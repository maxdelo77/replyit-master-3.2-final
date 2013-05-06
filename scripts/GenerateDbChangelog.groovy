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

includeTargets << grailsScript("Init")
includeTargets << new File("${basedir}/scripts/Liquibase.groovy")

target(generateChangelog: "Upgrades database to the latest version") {
    depends(parseArguments, initLiquibase)

    def db = getDatabaseParameters(argsMap)
    def version = getApplicationMinorVersion(argsMap)
    def changelog = argsMap.changelog ? argsMap.changelog : "descriptors/database/jbilling-changelog-${version}.xml"

    println "Generating changelog for version ${version}"
    println "${db.url} ${db.username}/${db.password ?: '[no password]'} (driver ${db.driver})"

    generateChangeLog(classpathref: "liquibase.classpath", driver: db.driver, url: db.url, username: db.username, password: db.password, outputfile: changelog)

    println "Generated changelog ${changelog}"
}

setDefaultTarget(generateChangelog)
