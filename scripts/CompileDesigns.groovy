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

final tempDir = "${basedir}/tmp"
final resourcesDir = "${basedir}/resources"
final descriptorsDir = "${basedir}/descriptors"

target(compileDesigns: "Compiles jasper paper invoice designs.") {
    ant.taskdef(name: "jrc", classname: "net.sf.jasperreports.ant.JRAntCompileTask")

    delete(dir: "${resourcesDir}/designs")
    mkdir(dir: "${resourcesDir}/designs")

    mkdir(dir: tempDir)
    jrc(destdir: "${resourcesDir}/designs", tempdir: tempDir, keepjava: "true", xmlvalidation: "true") {
        src {
            fileset(dir: "${descriptorsDir}/designs", includes: "**/*.jrxml")
        }
    }
    delete(dir: tempDir)
}

setDefaultTarget(compileDesigns)
