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

import javax.servlet.http.HttpSession
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.beans.factory.InitializingBean
import java.io.Serializable

/**
 * RecentItemService
 
 * @author Brian Cowdery
 * @since  07-12-2010
 */
class RecentItemService implements InitializingBean, Serializable {

    public static final String SESSION_RECENT_ITEMS = "recent_items"
    public static final Integer MAX_ITEMS = 5

    static scope = "session"

    def void afterPropertiesSet() {
        load()
    }

    def void load() {
        if (session['user_id'])
            session[SESSION_RECENT_ITEMS] = getRecentItems()
    }

    /**
     * Returns a list of recently viewed items for the currently logged in user.
     *
     * @return list of recently viewed items.
     */
    def Object getRecentItems() {
        return RecentItem.withCriteria {
            eq("userId", session["user_id"])
            order("id", "asc")
        }
    }

    /**
     * Add a new item to the recent items list for the currently logged in user and
     * update the session list.
     *
     * This method will not add a recent item if either the ID or recent item type is null.
     *
     * @param objectId object id
     * @param type recent item type
     */
    def void addRecentItem(Integer objectId, RecentItemType type) {
        if (objectId && type) {
            addRecentItem(new RecentItem(objectId: objectId, type: type))
        }
    }

    /**
     * Add a new item to the recent items list for the currently logged in user and
     * update the session list.
     *
     * @param item recent item
     */
    def void addRecentItem(RecentItem item) {
        def items = getRecentItems()
        def lastItem = items ? items.last() : null
		
        // add item only if it is different from the last item added
        try {
        	item.userId = session['user_id']
			if ( !(item == lastItem) ) {
			
                item.save()
                items << item

                if (items.size() > MAX_ITEMS) {
                    def remove = items.subList(0, items.size() - MAX_ITEMS)
                    remove.each{ it.delete(flush: true) }
                    remove.clear()
                }
                session[SESSION_RECENT_ITEMS] = items
            }

        } catch (Throwable t) {
            log.error("Exception caught adding recent item", t)
            session.error = 'recent.item.failed'
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
