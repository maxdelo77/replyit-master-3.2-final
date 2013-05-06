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

target(cleanDb: "Clean the test postgresql database, will drop/create the database if --hard.") {
    depends(parseArguments, initLiquibase)

    def db = getDatabaseParameters(argsMap)


    // execute the postgresql dropdb command to forcibly drop the database
    // when --drop or --hard
    if (argsMap.drop || argsMap.hard) {
        println "dropping database ${db.database}"
        exec(executable: "dropdb", failonerror: false) {
            arg(line: "-U ${db.username} -e ${db.database}")
        }
    }

    // execute postgresql createdb to create the database
    // when --create or --hard
    if (argsMap.create || argsMap.hard) {
        println "creating database ${db.database}"
        exec(executable: "createdb", failonerror: true) {
            arg(line: "-U ${db.username} -O ${db.username} -E UTF-8 -e ${db.database}")
        }
    }

    // default, just use liquibase to drop all existing objects within the database
    if (!argsMap.drop && !argsMap.create && !argsMap.hard) {
        println "dropping all objects in ${db.database}"
        dropAllDatabaseObjects(classpathref: "liquibase.classpath", driver: db.driver, url: db.url, username: db.username, password: db.password, defaultSchemaName: db.schema)
    }
}

target(prepareTestDb: "Import the test postgresql database.") {
    depends(parseArguments, initLiquibase)

    def db = getDatabaseParameters(argsMap)
    def version = getApplicationMinorVersion(argsMap)

    println "Loading database version ${version}"
    println "${db.url} ${db.username}/${db.password ?: '[no password]'} (schema: ${db.schema}) (driver ${db.driver})"


    // clean the db
    cleanDb()

    // changelog files to load
    def schema = "descriptors/database/jbilling-schema.xml"
    def init = "descriptors/database/jbilling-init_data.xml"
    def client = "client-data.xml"
    def test = "descriptors/database/jbilling-test_data.xml"
    def upgrade = "descriptors/database/jbilling-upgrade-${version}.xml"

    // load the jbilling database
    // by default this will load the testing data
    // if the -init argument is given then only the base jbilling data will be loaded
    // if the -client argument is given then the client reference data will be loaded
    if (argsMap.init) {
        println "updating with context = base. Loading init jBilling data"
        updateDatabase(classpathref: "liquibase.classpath", driver: db.driver, url: db.url, username: db.username, password: db.password, defaultSchemaName: db.schema, dropFirst: false, changeLogFile: schema, contexts: 'base')
        updateDatabase(classpathref: "liquibase.classpath", driver: db.driver, url: db.url, username: db.username, password: db.password, defaultSchemaName: db.schema, dropFirst: false, changeLogFile: init)
        updateDatabase(classpathref: "liquibase.classpath", driver: db.driver, url: db.url, username: db.username, password: db.password, defaultSchemaName: db.schema, dropFirst: false, changeLogFile: schema, contexts: 'FKs')
        updateDatabase(classpathref: "liquibase.classpath", driver: db.driver, url: db.url, username: db.username, password: db.password, defaultSchemaName: db.schema, dropFirst: false, changeLogFile: upgrade, contexts: 'base')
    } else if (argsMap.client) {
        println "updating with context = base. Loading client reference Db"
        updateDatabase(classpathref: "liquibase.classpath", driver: db.driver, url: db.url, username: db.username, password: db.password, defaultSchemaName: db.schema, dropFirst: false, changeLogFile: schema, contexts: 'base')
        updateDatabase(classpathref: "liquibase.classpath", driver: db.driver, url: db.url, username: db.username, password: db.password, defaultSchemaName: db.schema, dropFirst: false, changeLogFile: client)
        updateDatabase(classpathref: "liquibase.classpath", driver: db.driver, url: db.url, username: db.username, password: db.password, defaultSchemaName: db.schema, dropFirst: false, changeLogFile: schema, contexts: 'FKs')
        updateDatabase(classpathref: "liquibase.classpath", driver: db.driver, url: db.url, username: db.username, password: db.password, defaultSchemaName: db.schema, dropFirst: false, changeLogFile: upgrade, contexts: 'base')
        updateDatabase(classpathref: "liquibase.classpath", driver: db.driver, url: db.url, username: db.username, password: db.password, defaultSchemaName: db.schema, dropFirst: false, changeLogFile: upgrade, contexts: 'client')
    }

    if ((argsMap.test && !argsMap.init && !argsMap.client) || (!argsMap.init && !argsMap.client)) {
        println "updating with context = test. Loading test data"
        updateDatabase(classpathref: "liquibase.classpath", driver: db.driver, url: db.url, username: db.username, password: db.password, defaultSchemaName: db.schema, dropFirst: false, changeLogFile: schema, contexts: 'base')
        updateDatabase(classpathref: "liquibase.classpath", driver: db.driver, url: db.url, username: db.username, password: db.password, defaultSchemaName: db.schema, dropFirst: false, changeLogFile: test)
        updateDatabase(classpathref: "liquibase.classpath", driver: db.driver, url: db.url, username: db.username, password: db.password, defaultSchemaName: db.schema, dropFirst: false, changeLogFile: schema, contexts: 'FKs')
        updateDatabase(classpathref: "liquibase.classpath", driver: db.driver, url: db.url, username: db.username, password: db.password, defaultSchemaName: db.schema, dropFirst: false, changeLogFile: upgrade, contexts: 'base')
        updateDatabase(classpathref: "liquibase.classpath", driver: db.driver, url: db.url, username: db.username, password: db.password, defaultSchemaName: db.schema, dropFirst: false, changeLogFile: upgrade, contexts: 'test')
    }
}

setDefaultTarget(prepareTestDb)
