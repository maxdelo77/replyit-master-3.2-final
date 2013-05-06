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

includeTargets << grailsScript("Compile")

final targetDir = "${basedir}/target"

target(jar: "Packages all core jbilling classes in a .jar file.") {
    depends(compile)

    delete(dir: targetDir, includes: "${grailsAppName}.jar")

    exec(executable: "git", outputproperty: "version") {
        arg(line: "describe --tags")
    }

    tstamp()
    ant.jar(destfile: "${targetDir}/${grailsAppName}.jar", basedir: "${targetDir}/classes") {
        manifest {
            attribute(name: "Built-By", value: System.properties.'user.name')
            attribute(name: "Built-On", value: "${DSTAMP}-${TSTAMP}")
            attribute(name: "Specification-Title", value: grailsAppName)
            attribute(name: "Specification-Version", value: grailsAppVersion)
            attribute(name: "Specification-Vendor", value: "jBilling.com")

            attribute(name: "Package-Title", value: grailsAppName)
            attribute(name: "Package-Version", value: version)
            attribute(name: "Package-Vendor", value: "jBilling.com")
        }
    }
}

setDefaultTarget(jar)
