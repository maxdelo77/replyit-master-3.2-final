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
includeTargets << grailsScript("_GrailsArgParsing")

/**
 * Classpath containing all the Grails runtime dependencies, compiled classes
 * and plug-in classes for the liquibase ant task.
 *
 * Example:
 *     ant.taskdef(resource: "liquibasetasks.properties")
 *     ant.path(id: "liquibase.classpath", liquibaseClasspath)
 *
 *     updateDatabase(classpathref: "liquibase.classpath", args...)
 */
liquibaseClasspath = {
    commonClasspath.delegate = delegate
    commonClasspath.call()

    def dependencies = grailsSettings.runtimeDependencies
    if (dependencies) {
        for (File f in dependencies) {
            pathelement(location: f.absolutePath)
        }
    }

    pathelement(location: "${pluginClassesDir.absolutePath}")
    pathelement(location: "${classesDir.absolutePath}")
}

/**
 * Returns the application version as a numeric [major].[minor] version number.
 *
 * Can be explicitly set using the -dbVersion command line argument.
 *
 * Example:
 *      "enterprise-3.2.0" => 3.2
 */
getApplicationMinorVersion = { argsMap ->
    def version = argsMap.dbVersion ? argsMap.dbVersion : grailsAppVersion

    // strip all alphanumeric characters, then trim the string down to the first dotted pair
    def number = version.replaceAll(/[^0-9\.]/, '')
    return number.count('.') > 1 ? number.substring(0, number.lastIndexOf('.')) : number;
}

/**
 * Parses the command line arguments and builds a map of database parameters required
 * by all liquibase ant tasks. If no arguments are provided the defaults will be used.
 *
 *      -user   = Database username, defaults to 'jbilling'
 *      -pass   = Database password, defaults to ''
 *      -db     = Database name, defaults to 'jbilling_test'
 *      -url    = Database url, defaults to  'jdbc:postgresql://localhost:5432/jbilling_test'
 *      -driver = JDBC Driver class, defaults to 'org.postgresql.Driver'
 *
 * Example:
 *      grails liquibase -user=[username] -pass=[password] -db=[db name] -url=[jdbc url] -driver=[driver class]
 */
getDatabaseParameters = { argsMap ->
    def database = argsMap.db ? argsMap.db : "jbilling_test"

    def db = [
        username: argsMap.user ? argsMap.user : "jbilling",
        password: argsMap.pass ? argsMap.pass : "",
        database: database,
        url:      argsMap.url ? argsMap.url : "jdbc:postgresql://localhost:5432/${database}",
        driver:   argsMap.driver ? argsMap.driver : "org.postgresql.Driver",
        schema:   argsMap.schema ? argsMap.schema : "public"
    ]

    return db
}

target(initLiquibase: "Initialized the liquibase ant tasks") {
    // see http://www.liquibase.org/manual/ant
    ant.taskdef(resource: "liquibasetasks.properties")
    ant.path(id: "liquibase.classpath", liquibaseClasspath)
}

target(echoArgs: "Prints the parsed liquibase parameters to the screen.") {
    depends(parseArguments)

    println "This task does not have an executable target.\n"

    def db = getDatabaseParameters(argsMap)
    def version = getApplicationMinorVersion(argsMap)

    println "jBilling minor version = ${version}"
    println "DB config = ${db.url} ${db.username}/${db.password ?: '[no password]'} (schema: ${db.schema}) (driver ${db.driver})"
}

setDefaultTarget(echoArgs)
