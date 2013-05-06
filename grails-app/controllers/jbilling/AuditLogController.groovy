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

import grails.plugins.springsecurity.Secured
import org.hibernate.FetchMode
import org.hibernate.criterion.Restrictions
import org.hibernate.criterion.Criterion
import org.hibernate.Criteria
import com.sapienter.jbilling.server.user.db.CompanyDTO
import com.sapienter.jbilling.server.util.audit.db.EventLogDTO
import jbilling.FilterType

@Secured(["isAuthenticated()"])
class AuditLogController {

    static pagination = [ max: 10, offset: 0, sort: 'createDatetime', order: 'desc' ]

    def webServicesSession
    def viewUtils
    def filterService
    def recentItemService
    def breadcrumbService

    def index = {
        redirect action: list, params: params
    }

    /**
     * Gets a list of logs and renders the the list page. If the "applyFilters" parameter is given,
     * the partial "_logs.gsp" template will be rendered instead of the complete logs list page.
     */
    def list = {
        def filters = filterService.getFilters(FilterType.LOGS, params)

        params.max = params?.max?.toInteger() ?: pagination.max
        params.offset = params?.offset?.toInteger() ?: pagination.offset
        params.sort = params?.sort ?: pagination.sort
        params.order = params?.order ?: pagination.order
		
		// Hide  common filter By ID. It's useless here
		filters.find{it.field=="id"}?.visible=false
		

        def logs = EventLogDTO.createCriteria().list(
                max:    params.max,
                offset: params.offset
        ) {
            createAlias('affectedUser', 'u', Criteria.LEFT_JOIN)
            createAlias('jbillingTable', 'table', Criteria.LEFT_JOIN)

            and {
                filters.each { filter ->
                    log.debug("Now processing filter " + filter);
                    if (filter.getValue() != null) {
                        // avoid adding a filter for no table selection
                        if (!(filter.getField().equals("table.name") && filter.getStringValue().trim().length() == 0)) {
                            //log.debug("Adding restriction " + filter.getRestrictions());
                            addToCriteria(filter.getRestrictions());
                        }
                    }
                }

                eq('company', new CompanyDTO(session['company_id']))
            }

            order("id", "desc")
        }

        def selected = params.id ? EventLogDTO.get(params.int("id")) : null

        breadcrumbService.addBreadcrumb(controllerName, 'list', null, params.int('id'))

        if (params.applyFilter || params.partial) {
            render template: 'logs', model: [ logs: logs, selected: selected, filters: filters ]
        } else {
            [ logs: logs, selected: selected, filters: filters ]
        }
    }

    /**
     * Show details of the selected log.
     */
    def show = {
        EventLogDTO log = EventLogDTO.get(params.int('id'))
        breadcrumbService.addBreadcrumb(controllerName, 'list', params.template ?: null, params.int('id'))

        render template: 'show', model: [ selected: log ]
    }

    /**
     * Convenience shortcut, this action shows all logs for the given user id.
     */
    def user = {
        def filters= filterService.getFilters(FilterType.LOGS, params)
        def filter = filters.find{ it.field == 'u.id' }
        filter.integerValue=params.int('id') 
        redirect (action: "list", params: params)
    }

}
