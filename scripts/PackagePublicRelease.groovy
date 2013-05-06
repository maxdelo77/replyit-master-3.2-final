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

includeTargets << new File("${basedir}/scripts/PackageRelease.groovy")
includeTargets << new File("${basedir}/scripts/Jar.groovy")

imageDir = "${basedir}/image"
sourcePackageName = "${targetDir}/${releaseName}-src.zip"
packageName = "${targetDir}/${releaseName}.zip"

target(cleanPackages: "Remove old packages from the target directory.") {
    delete(dir: targetDir, includes: "${grailsAppName}-*.zip")
}

target(packageSource: "Packages the source code.") {
    zip(filesonly: false, update: false, destfile: sourcePackageName) {
        zipfileset(dir: basedir, prefix: releaseName) {
            exclude(name: "src/php/")

            exclude(name: "activemq-data/")
            exclude(name: "logs/")
            exclude(name: "tmp/")
            exclude(name: "image/")

            exclude(name: "resources/")
            exclude(name: "classes/")
            exclude(name: "target/")
            exclude(name: "out/")
            exclude(name: "**/*.jasper")

            exclude(name: ".ant-targets*.xml")
            exclude(name: "**/TEST*.xml")
            exclude(name: "**/*.log*")
            exclude(name: "**/*.swp")
            exclude(name: "*~")
            exclude(name: "**/*.iml")
            exclude(name: ".idea/")
            exclude(name: ".settings/")
            exclude(name: "**/jbilling.properties")

            exclude(name: "**/.git/")
            exclude(name: ".gitignore")
            exclude(name: ".gitattributes")
        }

        zipfileset(file: "${javaDir}/jbilling.properties.sample", fullpath: "${releaseName}/src/java/jbilling.properties")
    }
}

target(checkImage: "Checks that a previous release of jBilling exists to use as an image for the new release.") {
    if (!new File(imageDir).exists()) {
        println "\nBuild failed:"
        println "${imageDir} does not exist."
        println "Cannot build a release package without an ./image directory containing the jbilling release image."
        exit(1)
    }
}

target(updateImage: "Updates the jbilling image with the current release artifacts.") {
    checkImage()
    copyResources()
    compileDesigns()
    compileReports()
    jar()
    war()

    def jbillingHome = "${imageDir}/jbilling/"

    mkdir(dir: "${jbillingHome}/resources")

    // copy reports
    delete(dir: "${jbillingHome}/resources/reports", includes: "**/*")
    copy(todir: "${jbillingHome}/resources/reports") {
        fileset(dir: "${resourcesDir}/reports")
    }

    // copy invoice designs
    delete(dir: "${jbillingHome}/resources/designs", includes: "**/*")
    copy(todir: "${jbillingHome}/resources/designs") {
        fileset(dir: "${resourcesDir}/designs")
    }

    // copy logos
    delete(dir: "${jbillingHome}/resources/logos", includes: "**/*")
    copy(todir: "${jbillingHome}/resources/logos") {
        fileset(dir: "${resourcesDir}/logos")
    }

    // copy mediation descriptors and sample asterisk files
    copy(todir: "${jbillingHome}/resources/mediation", overwrite: true) {
        fileset(dir: "${resourcesDir}/mediation", includes: "asterisk.xml")
        fileset(dir: "${resourcesDir}/mediation", includes: "asterisk-sample*.csv")
        fileset(dir: "${resourcesDir}/mediation", includes: "jbilling_cdr.*")
        fileset(dir: "${resourcesDir}/mediation", includes: "mediation.dtd")
    }

    // copy client api artifacts
    delete(file: "${jbillingHome}/resources/api")
    mkdir(dir: "${jbillingHome}/resources/api")
    copy(file: "${targetDir}/${grailsAppName}.jar", todir: "${jbillingHome}/resources/api")
    copy(file: "${descriptorsDir}/spring/jbilling-remote-beans.xml", todir: "${jbillingHome}/resources/api")

    // copy plug-in spring xml configuration files
    delete(file: "${jbillingHome}/resources/spring")
    mkdir(dir: "${jbillingHome}/resources/spring")
    copy(todir: "${jbillingHome}/resources/spring") {
        fileset(dir: "${descriptorsDir}/spring", excludes: "jbilling-remote-beans.xml")
    }

    // copy configuration files
    // don't copy DataSource, the reference tomcat install uses HSQLDB
    copy(file: "${javaDir}/jbilling.properties", tofile: "${jbillingHome}/jbilling.properties", overwrite: true)
    copy(file: "${configDir}/Config.groovy", tofile: "${jbillingHome}/${grailsAppName}-Config.groovy", overwrite: true)

    // copy jbilling.war
    delete(file: "${imageDir}/webapps/${grailsAppName}.war")
    copy(file: "${targetDir}/${grailsAppName}.war", todir: "${imageDir}/webapps")
}


target(packageTomcat: "Builds and packages the binary jbilling tomcat release.") {
    // clear tomcat logs, temp and work directories
    delete(dir: "${imageDir}/logs")
    mkdir(dir: "${imageDir}/logs")

    delete(dir: "${imageDir}/temp")
    mkdir(dir: "${imageDir}/temp")

    delete(dir: "${imageDir}/work")
    mkdir(dir: "${imageDir}/work")

    // zip tomcat image
    zip(filesonly: false, update: false, destfile: packageName) {
        zipfileset(dir: imageDir, prefix: releaseName) {
            exclude(name: "webapps/jbilling/")
            exclude(name: "webapps/drools-guvnor/") // exclude exploded application directories

            exclude(name: "**/activemq-data/")
            exclude(name: "**/*.out")
            exclude(name: "**/*.log")               // exclude log files
        }
    }
}

target(packagePublicRelease: "Builds the public binary jbilling tomcat release, and the jbilling source release packages. ") {
    switch(args) {
        case "-update":
            println "Updating release image ..."
            updateImage()
            break

        default:
            println "Building release packages ..."
            cleanPackages()
            updateImage()
            packageSource()
            packageTomcat()
    }
}

setDefaultTarget(packagePublicRelease)
