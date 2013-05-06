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

import grails.plugins.springsecurity.Secured;
import java.util.Iterator;
import java.math.BigDecimal;

import com.sapienter.jbilling.server.process.db.BillingProcessDTO;
import com.sapienter.jbilling.server.process.db.BillingProcessDAS;
import com.sapienter.jbilling.server.user.db.CompanyDTO;
import com.sapienter.jbilling.server.payment.db.PaymentMethodDTO;
import com.sapienter.jbilling.common.SessionInternalError;
import com.sapienter.jbilling.server.invoice.db.InvoiceDAS
import com.sapienter.jbilling.client.util.SortableCriteria
import com.sapienter.jbilling.server.util.Constants
import com.sapienter.jbilling.server.util.Util
import com.sapienter.jbilling.server.util.db.CurrencyDTO
import com.sapienter.jbilling.server.process.db.BillingProcessConfigurationDTO
import com.sapienter.jbilling.server.payment.db.PaymentDTO
import com.sapienter.jbilling.server.payment.db.PaymentDAS;

/**
* BillingController
*
* @author Vikas Bodani
* @since 07/01/11
*/
@Secured(["MENU_94"])
class BillingController {

	static pagination = [ max: 10, offset: 0, sort: 'id', order: 'desc' ]

	def webServicesSession
	def recentItemService
	def breadcrumbService
	def filterService

	def index = {
		list()
	}
	
	/*
	 * Renders/display list of Billing Processes Ordered by Process Id descending
	 * so that the lastest process shows first.
	 */
	def list = {
		def filters = filterService.getFilters(FilterType.BILLINGPROCESS, params)
		def processes = getProcesses(filters)

        breadcrumbService.addBreadcrumb(controllerName, actionName, null, null)

        if (params.applyFilter || params.partial) {
            render template: 'list', model: [ processes: processes, filters:filters ]
        } else {
            render view: "index", model: [ processes: processes, filters:filters ]
        }
	}

	/*
	 * Filter the process results based on the parameter filter values
	 */
	def getProcesses(filters) {
		params.max = (params?.max?.toInteger()) ?: pagination.max
		params.offset = (params?.offset?.toInteger()) ?: pagination.offset
        params.sort = params?.sort ?: pagination.sort
        params.order = params?.order ?: pagination.order
		
		return BillingProcessDTO.createCriteria().list(
			max:    params.max,
			offset: params.offset
			) {
				and {
					filters.each { filter ->
						if (filter.value) {
							addToCriteria(filter.getRestrictions());
						}
					}
					eq('entity', new CompanyDTO(session['company_id']))
				}

                // apply sorting
                SortableCriteria.sort(params, delegate)
			}
	}

	/*
	 * To display the run details of a given Process Id
	 */
	def show = {
        Integer processId = params.int('id')

        if ( !BillingProcessDTO.exists( processId ) ) {
            flash.error = 'billing.process.review.doesnotexist'
            flash.args = [processId]
            redirect action:'list'
        }

        // get billing process record
        def process = BillingProcessDTO.get(processId)
        def configuration = BillingProcessConfigurationDTO.findByEntity(new CompanyDTO(session['company_id']))

        // main billing process run (not a retry!)
        def processRuns = process?.processRuns?.asList()?.sort{ it.started }
        def processRun =  processRuns?.size() > 0 ? processRuns.first() : null 

        // all payments made to generated invoices between process start & end
		def generatedPayments = []
		if (processRun) {
        	generatedPayments = new PaymentDAS().findBillingProcessGeneratedPayments(processId, processRun.started, processRun.finished)
		}
        // all payments made to generated invoice after the process end
        def invoicePayments = []
		if (processRun) {
			invoicePayments = new PaymentDAS().findBillingProcessPayments(processId, processRun.finished)
		}

        // all invoices for the billing process. Avoiding using the associations
        def invoices = new InvoiceDAS().findByProcess(process)

		recentItemService.addRecentItem(processId, RecentItemType.BILLINGPROCESS)
		breadcrumbService.addBreadcrumb(controllerName, actionName, null, processId)

        [ process: process, processRun: processRun, generatedPayments: generatedPayments, invoicePayments: invoicePayments, configuration: configuration, invoices: invoices,
          formattedPeriod: getFormattedPeriod(process.periodUnit.id, process.periodValue, session['language_id']) ]
	}
	
	private String getFormattedPeriod(Integer periodUnitId, Integer periodValue, Integer languageId) {
		String periodUnitStr = Util.getPeriodUnitStr(periodUnitId, languageId)
		return periodValue + Constants.SINGLE_SPACE + periodUnitStr;
	}

	def showInvoices = {
		redirect controller: 'invoice', action: 'byProcess', id: params.id, params: [ isReview : params.isReview ]
	}
	
	def showOrders = {
        redirect controller: 'order', action: 'byProcess', params: [processId: params.id]
	}

    @Secured(["BILLING_80"])
	def approve = {
		try {
			webServicesSession.setReviewApproval(Boolean.TRUE)
		} catch (Exception e) {
			throw new SessionInternalError(e)
		}
		flash.message = 'billing.review.approve.success'
		redirect action: 'list'
	}

    @Secured(["BILLING_80"])
	def disapprove = {
		try {
			webServicesSession.setReviewApproval(Boolean.FALSE)
		} catch (Exception e) {
			throw new SessionInternalError(e)
		}
		flash.message = 'billing.review.disapprove.success'
		redirect action: 'list'
	}
}
