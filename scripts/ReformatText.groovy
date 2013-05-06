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

target(reformatText: "Cleans up text files and sets the appropriate eol character.") {
    // replace tab characters with spaces
    // replace UNIX eol characters with DOS characters
    fixcrlf(srcdir: "${basedir}/src", includes: "**/*.java",
            tab: "remove", tablength: "4", javafiles: "yes", eol: "crlf")
}

setDefaultTarget(reformatText)
