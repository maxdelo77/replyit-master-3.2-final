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

import grails.plugins.springsecurity.Secured
import jbilling.Breadcrumb

/**
 * Shows the user's home page after login.
 *
 * Mapped to "/", see UrlMappings.groovy
 *
 * @author Brian Cowdery
 * @since  22-11-2010
 */
class HomeController {

    def recentItemService
    def breadcrumbService

    @Secured(["isAuthenticated()"])
    def index = {        
        def breadcrumb = breadcrumbService.getLastBreadcrumb()

        if (breadcrumb) {
            // show last page viewed
            redirect(controller: breadcrumb.controller, action: breadcrumb.action, id: breadcrumb.objectId)
        } else {
            // show default page
            redirect(controller: 'customer')
        }
    }
}
