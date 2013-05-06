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
import com.sapienter.jbilling.server.process.BillingProcessConfigurationWS;
import com.sapienter.jbilling.common.SessionInternalError;

/**
* BillingController
*
* @author Vikas Bodani
* @since 11/01/11
*/
@Secured(["MENU_99"])
class BillingconfigurationController {

	def webServicesSession
	def viewUtils
	def recentItemService
	def breadcrumbService
    
    def index = {
		
		breadcrumbService.addBreadcrumb(controllerName, actionName, null, null)
		def configuration= webServicesSession.getBillingProcessConfiguration()
		boolean isBillingRunning= webServicesSession.isBillingProcessRunning()
		if (isBillingRunning)
		{
			flash.info = 'prompt.billing.running'
		}
		[configuration:configuration, isBillingRunning: isBillingRunning]
	}
	
	def saveConfig = {
		
		log.info "${params}"
		def configuration= new BillingProcessConfigurationWS() 
		bindData(configuration, params)

		//set all checkbox values as int
		configuration.setGenerateReport params.generateReport ? 1 : 0
		configuration.setInvoiceDateProcess params.invoiceDateProcess ? 1 : 0 
		configuration.setOnlyRecurring params.onlyRecurring ? 1 : 0
		configuration.setAutoPayment params.autoPayment ? 1 : 0
		configuration.setAutoPaymentApplication params.autoPaymentApplication ? 1 : 0
		//configuration.setNextRunDate (new SimpleDateFormat("dd-MMM-yyyy").parse(params.nextRunDate) )
		configuration.setEntityId webServicesSession.getCallerCompanyId()
		
		log.info "Generate Report ${params.generateReport}"
		
		try {
			webServicesSession.createUpdateBillingProcessConfiguration(configuration)
			flash.message = 'billing.configuration.save.success'
		} catch (SessionInternalError e){
			viewUtils.resolveException(flash, session.locale, e);
		} catch (Exception e) {
			log.info e.getMessage()
			flash.error = 'billing.configuration.save.fail'
		}
		
		chain action: index
	}
	
	def runBilling = {
		try {
			if (!webServicesSession.isBillingProcessRunning()) {
				webServicesSession.triggerBillingAsync(new Date())
				flash.message = 'prompt.billing.trigger'
			} else {
				flash.error = 'prompt.billing.already.running'
			}
		} catch (Exception e) {
			log.error e.getMessage()
			viewUtils.resolveException(flash, session.locale, e);
		}

		chain action: index
	}
	
}
