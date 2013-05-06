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

target(tagDb: "Upgrades database to the latest version") {
    depends(parseArguments, initLiquibase)

    def db = getDatabaseParameters(argsMap)
    def tag = argsMap.tag

    if (!tag) throw new IllegalArgumentException("Argument -tag=[tag name] is required for tag / rollback operations!");

    println "Tagging database as '${tag}'"
    println "${db.url} ${db.username}/${db.password ?: '[no password]'} (schema: ${db.schema}) (driver ${db.driver})"


    // create tag
    tagDatabase(classpathref: "liquibase.classpath", driver: db.driver, url: db.url, username: db.username, password: db.password, defaultSchemaName: db.schema, tag: tag)
}

setDefaultTarget(tagDb)
