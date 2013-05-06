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

includeTargets << grailsScript("War")

includeTargets << new File("${basedir}/scripts/CopyResources.groovy")
includeTargets << new File("${basedir}/scripts/CompileDesigns.groovy")
includeTargets << new File("${basedir}/scripts/CompileReports.groovy")
includeTargets << new File("${basedir}/scripts/Jar.groovy")

resourcesDir = "${basedir}/resources"
descriptorsDir = "${basedir}/descriptors"
configDir = "${basedir}/grails-app/conf"
sqlDir = "${basedir}/sql"
javaDir = "${basedir}/src/java"
targetDir = "${basedir}/target"

timestamp = String.format("%tF-%<tH%<tM", new Date())
releaseName = "${grailsAppName}-${grailsAppVersion}"
packageName = "${targetDir}/${releaseName}-${timestamp}.zip"

target(prepareRelease: "Builds the war and all necessary resources.") {
    copyResources()
    compileDesigns()
    compileReports()
    jar()
    war()
}

target(packageRelease: "Builds the war and packages all the necessary config files and resources in a release zip file.") {
    depends(prepareRelease)

    // ship the data.sql file if it exists, otherwise use jbilling_test.sql
    def testDb = new File("${basedir}/sql/jbilling_test.sql")
    def referenceDb = new File("${basedir}/data.sql")
    File sqlFile = referenceDb.exists() ? referenceDb : testDb

    // zip up resources into a release package
    delete(dir: targetDir, includes: "${grailsAppName}-*.zip")

    // zip into a timestamped archive for delivery to customers
    zip(filesonly: false, update: false, destfile: packageName) {
        zipfileset(dir: resourcesDir, prefix: "jbilling/resources")
        zipfileset(dir: targetDir, includes: "${grailsAppName}.jar", prefix: "jbilling/resources/api")
        zipfileset(dir: javaDir, includes: "jbilling.properties", fullpath: "jbilling/jbilling.properties")
        zipfileset(dir: configDir, includes: "Config.groovy", fullpath: "jbilling/${grailsAppName}-Config.groovy")
        zipfileset(dir: configDir, includes: "DataSource.groovy", fullpath: "jbilling/${grailsAppName}-DataSource.groovy")
        zipfileset(dir: targetDir, includes: "${grailsAppName}.war")
        zipfileset(file: sqlFile.absolutePath, includes: sqlFile.name)
        zipfileset(dir: sqlDir, includes: "upgrade.sql")
        zipfileset(file: "UPGRADE-NOTES")
    }

    println "Packaged release to ${packageName}"
}

setDefaultTarget(packageRelease)
