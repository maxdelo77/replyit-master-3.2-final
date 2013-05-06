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

final resourcesDir = "${basedir}/resources"
final descriptorsDir = "${basedir}/descriptors"

target(cleanResources: "Removes the existing jbilling resources directory.") {
    delete(dir: "${resourcesDir}")
}

target(createStructure: "Creates the jbilling resources directory structure.") {
    ant.sequential {
        mkdir(dir: "${resourcesDir}")
        mkdir(dir: "${resourcesDir}/api")
        mkdir(dir: "${resourcesDir}/designs")
        mkdir(dir: "${resourcesDir}/invoices")
        mkdir(dir: "${resourcesDir}/logos")
        mkdir(dir: "${resourcesDir}/mediation")
        mkdir(dir: "${resourcesDir}/mediation/errors")
        mkdir(dir: "${resourcesDir}/reports")
	    mkdir(dir: "${resourcesDir}/notifications")
        mkdir(dir: "${resourcesDir}/spring")
    }
}

target(copyResources: "Creates the jbilling 'resources/' directories and copies necessary files.") {
    depends(cleanResources, createStructure)

    // copy default company logos
    copy(todir: "${resourcesDir}/logos") {
        fileset(dir: "${descriptorsDir}/logos")
    }

    // copy default mediation files
    copy(todir: "${resourcesDir}/mediation") {
        fileset(dir: "${descriptorsDir}/mediation", includes: "mediation.dtd")
        fileset(dir: "${descriptorsDir}/mediation", includes: "asterisk.xml")
    }

    // preserve empty directories when zipping
    touch(file: "${resourcesDir}/invoices/emptyfile.txt")
    touch(file: "${resourcesDir}/mediation/errors/emptyfile.txt")
}

setDefaultTarget(copyResources)
