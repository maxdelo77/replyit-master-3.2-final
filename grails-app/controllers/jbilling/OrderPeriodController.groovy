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

import java.util.List;

import grails.plugins.springsecurity.Secured
import com.sapienter.jbilling.server.user.db.CompanyDTO
import com.sapienter.jbilling.server.util.db.LanguageDTO
import com.sapienter.jbilling.server.util.db.InternationalDescription
import com.sapienter.jbilling.server.util.InternationalDescriptionWS
import com.sapienter.jbilling.common.SessionInternalError
import com.sapienter.jbilling.server.order.db.OrderPeriodDTO
import com.sapienter.jbilling.server.order.OrderPeriodWS

import com.sapienter.jbilling.server.payment.tasks.PaymentSageTask.Params;
import com.sapienter.jbilling.server.process.db.PeriodUnitDTO

/**
 * OrderPeriodController 
 *
 * @author Vikas Bodani
 * @since 09-Mar-2011
 */


@Secured(["isAuthenticated()", "MENU_99"])
class OrderPeriodController {

	static pagination = [ max: 10, offset: 0 ]
	def breadcrumbService
	def webServicesSession
	def viewUtils
	
    def index = {
        redirect action: list, params: params
    }

    def list = {
        breadcrumbService.addBreadcrumb(controllerName, actionName, null, null)
		getList(params)
	}
	
	def getList(params) {

		def period = OrderPeriodDTO.get(params.int('id'))
		
		if (params.id?.isInteger() && !period) {
        	flash.error = 'orderPeriod.not.found'
			flash.args = [ params.id as String ]
        }

		params.max = params?.max?.toInteger() ?: pagination.max
        params.offset = params?.offset?.toInteger() ?: pagination.offset
		
		def periods= getPeriodsForEntity()
		
		if (params.applyFilter) {
            render template: 'periods', model: [ periods: periods, selected: period ]
		} else {
			if(chainModel){
				def cp=chainModel
				render view: 'list', model:[selected: period, periods: periods]+chainModel
			}
			else
				render view: 'list', model:[periods: periods, selected: period]
		}
	}
	
    def show = {
        def period = OrderPeriodDTO.get(params.int('id'))

        breadcrumbService.addBreadcrumb(controllerName, 'list', null, period.id, period.getDescription(session['language_id']))

        render template: 'show', model: [ selected: period ]
    }
    
	def getPeriodsForEntity () {
		return OrderPeriodDTO.createCriteria().list(
			max:    params.max,
			offset: params.offset
		) {
			eq('company', new CompanyDTO(session['company_id']))
			order("id", "desc")
		}
	}
	
    def edit = {
        def period = params.id ? OrderPeriodDTO.get(params.int('id')) : null

        def crumbName = params.id ? 'update' : 'create'
        def crumbDescription = params.id ? period?.getDescription(session['language_id']) : null
        
        breadcrumbService.addBreadcrumb(controllerName, 'listEdit', crumbName, params.int('id'), crumbDescription)

        def periodUnits = PeriodUnitDTO.list()
        
        render template: 'edit', model: [ period: period, periodUnits: periodUnits ]
    }
    
    def listEdit = {
        
        def period = params.id ? OrderPeriodDTO.get(params.int('id')) : null
        
        if (params.id?.isInteger() && !period) {
			redirect action: 'list', params: params
			return
        }
        
        params.max = params?.max?.toInteger() ?: pagination.max
        params.offset = params?.offset?.toInteger() ?: pagination.offset
		def periods = getPeriodsForEntity()
		
        def crumbName = params.id ? 'update' : 'create'
        def crumbDescription = params.id ? period?.getDescription(session['language_id']) : null
        breadcrumbService.addBreadcrumb(controllerName, 'listEdit', crumbName, params.int('id'), crumbDescription)
        
        def periodUnits = PeriodUnitDTO.list()
        
        render view: 'listEdit', model: [periods: periods, period: period, periodUnits: periodUnits]
    }
    
	def save = {
        
        OrderPeriodWS ws= new OrderPeriodWS()
        bindData(ws, params)
        
		log.debug ws
		if(params.description){
			InternationalDescriptionWS descr=
				new InternationalDescriptionWS(session['language_id'] as Integer, params.description)
	        log.debug descr
			ws.descriptions.add descr
		}
		ws.setEntityId(session['company_id'].toInteger())
        log.debug ws
        
		try {
			boolean retVal= webServicesSession.updateOrCreateOrderPeriod(ws);        \
			if (params.isNew=="true")
            {
                flash.message= 'config.periods.created'
            }
            else{
                flash.message= 'config.periods.updated'
            }
		} catch (SessionInternalError e){
			viewUtils.resolveException(flash, session.locale, e);
			chain action: 'list', model:[periodWS:ws, periodUnits: PeriodUnitDTO.list()]
			return
		} catch (Exception e) {
			log.error e.getMessage()
			flash.error = 'config.periods.saving.error'
		}
		redirect (action: 'list')
		
	}
	
	def delete = {
		log.debug 'delete called on ' + params.id
        if (params.id) {
            def period= OrderPeriodDTO.get(params.int('id'))
            if (period) {
                try {
                    boolean retVal= webServicesSession.deleteOrderPeriod(params.id?.toInteger());
                    if (retVal) { 
                        flash.message= 'config.periods.delete.success'
                        flash.args = [ params.id ]
                    } else {
                        flash.info = 'config.periods.delete.failure'
                    }
                } catch (SessionInternalError e){
                    viewUtils.resolveException(flash, session.locale, e);
                } catch (Exception e) {
                    log.error e.getMessage()
                    flash.error = 'config.periods.delete.error'
                }
            }
        }

        // render the period list
        params.applyFilter = true
        params.id = null
        getList(params)
	}

}
