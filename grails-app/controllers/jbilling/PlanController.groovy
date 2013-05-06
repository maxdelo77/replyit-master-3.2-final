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

import com.sapienter.jbilling.server.item.db.PlanDTO
import com.sapienter.jbilling.server.user.db.CompanyDTO
import grails.plugins.springsecurity.Secured
import com.sapienter.jbilling.server.item.CurrencyBL
import com.sapienter.jbilling.server.item.PlanItemWS
import com.sapienter.jbilling.common.SessionInternalError
import com.sapienter.jbilling.client.pricing.util.PlanHelper
import com.sapienter.jbilling.server.item.db.ItemDTO
import com.sapienter.jbilling.server.pricing.PriceModelWS
import com.sapienter.jbilling.server.user.CustomerPriceBL
import com.sapienter.jbilling.client.util.SortableCriteria

/**
 * PlanController
 *
 * @author Brian Cowdery
 * @since 01-Feb-2011
 */
@Secured(["MENU_98"])
class PlanController {

    static pagination = [ max: 10, offset: 0, sort: 'id', order: 'desc' ]

    def webServicesSession
    def viewUtils
    def filterService
    def breadcrumbService

    def index = {
        list()
    }

	def getPlans(params) {

        return PlanDTO.createCriteria().list(
                max:    params.max,
                offset: params.offset
        ) {
        	createAlias('item', 'i')
            eq('i.entity', new CompanyDTO(session['company_id']))
            // apply sorting
            SortableCriteria.buildSortNoAlias(params, delegate)
        }
    }
    
    /**
     * Get a list of plans and render the list page. If the "applyFilters" parameter is given, the
     * partial "_plans.gsp" template will be rendered instead of the complete list.
     */
    def list = {
        params.max = params?.max?.toInteger() ?: pagination.max
        params.offset = params?.offset?.toInteger() ?: pagination.offset
        params.sort = params?.sort ?: pagination.sort
        params.order = params?.order ?: pagination.order

        def plans = getPlans(params)

        def selected = params.id ? PlanDTO.get(params.int("id")) : null
        
        // if id is present and plan not found, give an error message along with the list
        if (params.id?.isInteger() && selected == null) {
			flash.error = 'plan.not.found'
            flash.args = [params.id]
        }
        
        breadcrumbService.addBreadcrumb(controllerName, 'list', null, selected ? params.int('id') : null, selected?.item?.internalNumber)
        
        if (params.applyFilter || params.partial) {
            render template: 'plans', model: [ plans: plans, selected: selected ]
        } else {
            render view: 'list', model: [ plans: plans, selected: selected ]
        }
    }

    /**
     * Shows details of the selected plan.
     */
    @Secured(["PLAN_63"])
    def show = {
        PlanDTO plan = PlanDTO.get(params.int('id'))
        breadcrumbService.addBreadcrumb(controllerName, 'list', null, params.int('id'), plan.item.internalNumber)

        render template: 'show', model: [ plan: plan ]
    }

    /**
     * Deletes the given plan id and all the plan item prices.
     */
    @Secured(["PLAN_62"])
    def delete = {
        if (params.id) {
            def plan

            try {
                plan = webServicesSession.getPlanWS(params.int('id'))
            } catch (SessionInternalError e) {
                log.error("Could not fetch WS object", e)

                flash.error = 'plan.not.found'
                flash.args = [ params.id ]

                redirect action: 'list'
                return
            }

            webServicesSession.deletePlan(plan.id)
            webServicesSession.deleteItem(plan.itemId)

            log.debug("Deleted plan ${params.id} and subscription product ${plan.itemId}.")

            flash.message = 'plan.deleted'
            flash.args = [ params.id ]
        }

        // render the partial plan list
        params.applyFilter = true
        params.id = null
        list()
    }
}
