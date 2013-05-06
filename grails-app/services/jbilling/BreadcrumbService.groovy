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

package jbilling

import org.springframework.beans.factory.InitializingBean
import org.springframework.web.context.request.RequestContextHolder
import javax.servlet.http.HttpSession
import java.io.Serializable

/**
 * BreadcrumbService
 *
 * @author Brian Cowdery
 * @since  14-12-2010
 */
class BreadcrumbService implements InitializingBean, Serializable {

    public static final String SESSION_BREADCRUMBS = "breadcrumbs"
    public static final Integer MAX_ITEMS = 7

    static scope = "session"

    def void afterPropertiesSet() {
        load()
    }

    def void load() {
        if (session['user_id'])
            session[SESSION_BREADCRUMBS] = getBreadcrumbs()
    }

    /**
     * Returns a list of recorded breadcrumbs for the currently logged in user.
     *
     * @return list of recorded breadcrumbs.
     */
    def Object getBreadcrumbs() {
        return Breadcrumb.withCriteria {
            eq("userId", session["user_id"])
            order("id", "asc")
        }
    }

    /**
     * Returns the last recorded breadcrumb for the currently logged in user.
     *
     * @return last recorded breadcrumb.
     */
    def Object getLastBreadcrumb() {
        return Breadcrumb.findByUserId(session['user_id'], [sort:'id', order:'desc'])
    }

    /**
     * Add a new breadcrumb to the breadcrumb list for the currently logged in user and
     * update the session list.
     *
     * The resulting breadcrumb link is generated using the 'g:link' grails tag. The same
     * parameter requirements for g:link apply here as well. A breadcrumb MUST have a controller,
     * but action and ID are optional. the name parameter is used to control the translated breadcrumb
     * message and is optional.
     *
     * @param controller breadcrumb controller (required)
     * @param action breadcrumb action, may be null
     * @param name breadcrumb message key name, may be null
     * @param objectId breadcrumb entity id, may be null.
     */
    def void addBreadcrumb(String controller, String action, String name, Integer objectId) {
        addBreadcrumb(new Breadcrumb(controller: controller, action: action, name: name, objectId: objectId))
    }

    def void addBreadcrumb(String controller, String action, String name, Integer objectId, String description) {
        addBreadcrumb(new Breadcrumb(controller: controller, action: action, name: name, objectId: objectId, description: description))
    }

    /**
     * Add a new breadcrumb to the recent breadcrumb list for the currently logged in user and
     * update the session list.
     *
     * @param crumb breadcrumb to add
     */
    def void addBreadcrumb(Breadcrumb crumb) {
        def crumbs = getBreadcrumbs()
        def lastItem = !crumbs.isEmpty() ? crumbs.getAt(-1) : null

        // add breadcrumb only if it is different from the last crumb added
        try {
            if (!lastItem || !lastItem.equals(crumb)) {
                crumb.userId = session['user_id']
                crumb.save()

                crumbs << crumb

                if (crumbs.size() > MAX_ITEMS) {
                    def remove = crumbs.subList(0, crumbs.size() - MAX_ITEMS)
                    remove.each{ it.delete(flush: true) }
                    remove.clear()
                }

                session[SESSION_BREADCRUMBS] = crumbs
            }

        } catch (Throwable t) {
            log.error("Exception caught adding breadcrumb", t)
            session.error = 'breadcrumb.failed'
        }
    }

    /**
     * Returns the HTTP session
     *
     * @return http session
     */
    def HttpSession getSession() {
        return RequestContextHolder.currentRequestAttributes().getSession()
    }

}
