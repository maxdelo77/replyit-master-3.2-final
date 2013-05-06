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

package com.sapienter.jbilling.client.util

/**
 * DownloadHelper 
 *
 * @author Brian Cowdery
 * @since 04/03/11
 */
class DownloadHelper {

    /**
     * Sets an appropriate response header for the downloaded file. These headers provide
     * a filename to the downloading client, and ensure that the download file is not cached.
     *
     * @param response response object
     * @param filename filename to set in the response header
     */
    static def setResponseHeader(response, String filename) {
        response.setHeader("Content-disposition", "attachment; filename=${filename}")
        response.setHeader("Expires", "0")
        response.setHeader("Cache-Control", "no-cache")
    }

    /**
     * Sends the given bytes as the content of the response object.
     *
     * @param response response object
     * @param filename filename to set in the response header
     * @param contentType MIME content type of the sent file
     * @param bytes bytes to send
     */
    static def sendFile(response, String filename, String contentType, byte[] bytes) {
        setResponseHeader(response, filename)

        response.setContentType(contentType)
        response.setContentLength(bytes.length)
        response.outputStream << bytes
    }
}
