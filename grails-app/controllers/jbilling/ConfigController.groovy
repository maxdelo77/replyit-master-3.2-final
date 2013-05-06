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

import com.sapienter.jbilling.common.SessionInternalError
import com.sapienter.jbilling.common.Util
import com.sapienter.jbilling.server.item.CurrencyBL
import com.sapienter.jbilling.server.process.AgeingWS
import com.sapienter.jbilling.server.user.CompanyWS
import com.sapienter.jbilling.server.user.ContactWS
import com.sapienter.jbilling.server.util.Constants
import com.sapienter.jbilling.server.util.CurrencyWS
import com.sapienter.jbilling.server.util.PreferenceTypeWS
import com.sapienter.jbilling.server.util.PreferenceWS
import com.sapienter.jbilling.server.util.db.PreferenceTypeDTO
import grails.plugins.springsecurity.Secured
import com.sapienter.jbilling.common.CommonConstants

/**
 * ConfigurationController
 *
 * @author Brian Cowdery
 * @since 03-Jan-2011
 */
@Secured(["MENU_99"])
class ConfigController {

    def breadcrumbService
    def webServicesSession
    def viewUtils
    def userSession

    /*
        Show/edit all preferences
     */

    def index = {
        def preferenceTypes = PreferenceTypeDTO.list()

        // show preference if given id
        def preferenceId = params.int('id')
        def selected = preferenceId ? preferenceTypes.find { it.id == preferenceId } : null

        breadcrumbService.addBreadcrumb(controllerName, actionName, null, null)

        render view: 'index', model: [preferenceTypes: preferenceTypes, selected: selected]
    }

    def show = {
        def selected = PreferenceTypeDTO.get(params.int('id'))

        render template: 'show', model: [selected: selected]
    }

    def save = {
        def type = new PreferenceTypeWS()
        bindData(type, params, 'type')

        def preference = new PreferenceWS()
        bindData(preference, params, 'preference')
        preference.preferenceType = type

        try {
            webServicesSession.updatePreference(preference)

            flash.message = 'preference.updated'
            flash.args = [type.id as String]

        } catch (SessionInternalError e) {
            viewUtils.resolveException(flash, session.locale, e)
        }

        chain action: index, params: [id: type.id]
    }

    /*
       Ageing configuration
    */


    def aging = {
        log.debug "config.aging ${session['language_id']}"
        AgeingWS[] array = webServicesSession.getAgeingConfiguration(session['language_id'] as Integer)
        breadcrumbService.addBreadcrumb(controllerName, actionName, null, null)
        def gracePeriod = userSession.getEntityPreference(session['company_id'] as Integer, Constants.PREFERENCE_GRACE_PERIOD)
        render view: 'aging', model: [ageingSteps: array, gracePeriod: gracePeriod]
    }

    def saveAging = {

        def cnt = params.recCnt.toInteger()
        log.debug "Records Count: ${cnt}"

        AgeingWS[] array = new AgeingWS[cnt]
        for (int i = 0; i < cnt; i++) {
            log.debug "${params['obj[' + i + '].statusId']}"
            AgeingWS ws = new AgeingWS()
            bindData(ws, params["obj[" + i + "]"])
            array[i] = ws
        }

        for (AgeingWS dto : array) {
            log.debug "Printing: ${dto.toString()}"
        }
        try {
            webServicesSession.saveAgeingConfiguration(array, params.int('gracePeriod'), session['language_id'] as Integer)
            flash.message = 'config.ageing.updated'
        } catch (SessionInternalError e) {
            viewUtils.resolveException(flash, session.locale, e);
        } catch (Exception e) {
            log.error e.getMessage()
            flash.error = 'config.error.saving.ageing'
        }
        chain action: aging
    }

    /*
        Company configuration
     */

    def company = {
        CompanyWS company = webServicesSession.getCompany()
        breadcrumbService.addBreadcrumb(controllerName, actionName, null, null)

        render view: 'company', model: [company: company]
    }

    def saveCompany = {
        try {
            CompanyWS company = new CompanyWS(session['company_id'].intValue())

            // Contact Type 1 is always Company Contact
            ContactWS contact = new ContactWS()
            bindData(company, params, ['id'])
            bindData(contact, params, ['id'])
            company.setContact(contact)

            webServicesSession.updateCompany(company)

            flash.message = 'config.company.save.success'

        } catch (SessionInternalError e) {
            viewUtils.resolveException(flash, session.locale, e);

        } catch (Exception e) {
            flash.error = 'config.company.save.error'
        }

        chain action: company
    }

    /*
        Invoice display configuration
     */

    def invoice = {
        def number = webServicesSession.getPreference(Constants.PREFERENCE_INVOICE_NUMBER)
        def prefix = webServicesSession.getPreference(Constants.PREFERENCE_INVOICE_PREFIX)

        render view: 'invoice', model: [number: number, prefix: prefix, logoPath: entityLogoPath]
    }

    def entityLogo = {
        def logo = new File(getEntityLogoPath())
        response.outputStream << logo.getBytes()
    }

    def saveInvoice = {
        def number = new PreferenceWS(preferenceType: new PreferenceTypeWS(id: Constants.PREFERENCE_INVOICE_NUMBER), value: params.number)
        def prefix = new PreferenceWS(preferenceType: new PreferenceTypeWS(id: Constants.PREFERENCE_INVOICE_PREFIX), value: params.prefix)

        try {
            webServicesSession.updatePreferences((PreferenceWS[]) [number, prefix])

        } catch (SessionInternalError e) {
            viewUtils.resolveException(flash, session.locale, e)
            render view: 'invoice', model: [number: number, prefix: prefix, logoPath: entityLogoPath]
            return
        }

        // save uploaded file
        def logo = request.getFile('logo');
        if (!logo.empty) {
            List validImageExtensions = grailsApplication.config.validImageExtensions as List
            if (!validImageExtensions.contains(logo.getContentType())) {
                flash.error = message(code: 'invoiceDetail.logo.format.error', args: [validImageExtensions])
            }else{
                logo.transferTo(new File(getEntityLogoPath()))
                flash.message = 'preferences.updated'
            }

        }
        chain action: invoice
    }

    def String getEntityLogoPath() {
        return Util.getSysProp("base_dir") + "${File.separator}logos${File.separator}entity-${session['company_id']}.jpg"
    }

    /*
       Currencies
    */

    def currency = {
        def startDate = params.startDate ? new Date().parse(message(code: 'date.format'), params.startDate) : getLastTimePointDate()
        return generateCurrenciesFormModel(com.sapienter.jbilling.common.Util.truncateDate(startDate))
    }

    def saveCurrencies = {
        def defaultCurrencyId = params.int('defaultCurrencyId')
        def startDate = params.startDate ? new Date().parse(message(code: 'date.format'), params.startDate) : getLastTimePointDate()

        // build a list of currencies
        def currencies = []
        params.currencies.each { k, v ->
            if (v instanceof Map) {
                def currency = new CurrencyWS()
                bindData(currency, removeBlankParams(v), ['_inUse'])
                currency.defaultCurrency = (currency.id == defaultCurrencyId)
                currency.fromDate = startDate

                currencies << currency
            }
        }

        // update all currencies
        try {
            webServicesSession.updateCurrencies((CurrencyWS[]) currencies)
            flash.message = 'currencies.updated'
        } catch (SessionInternalError e) {
            viewUtils.resolveException(flash, session.locale, e)
        }

        chain action: currency
    }

    def deleteCurrency = {
        log.debug 'delete currency called on ' + params.id
        try {
            boolean retVal = webServicesSession.deleteCurrency(params.int('id'));

            if (retVal) {
                flash.message = 'currency.deleted'
                flash.args = [params.code]
                log.debug("Deleted currency ${params.code}.")
            } else {
                flash.info = 'currency.delete.failure'
                flash.args = [params.code]
            }

        } catch (SessionInternalError e) {
            viewUtils.resolveException(flash, session.locale, e)
        } catch (Exception e) {
            log.error e.getMessage()
            flash.error = 'currency.delete.error'
            flash.args = [params.code]
        }

        chain action: currency
    }

    def addDatePoint = {
        def startDate = com.sapienter.jbilling.common.Util.truncateDate(new Date())
        def mdl = generateCurrenciesFormModel(startDate)
        mdl.timePoints.add(startDate)

        render template: 'currency/form', model: mdl
    }

    def editDatePoint = {
        def startDate = new Date().parse(message(code: 'date.format'), params.startDate)

        render template: 'currency/form', model: generateCurrenciesFormModel(startDate)
    }

    def removeDatePoint = {
        def startDate = new Date().parse(message(code: 'date.format'), params.startDate)
        new CurrencyBL().removeExchangeRatesForDate(session['company_id'], startDate)

        render template: 'currency/form', model: generateCurrenciesFormModel(getLastTimePointDate())
    }

    def generateCurrenciesFormModel = { date ->
        def currency = new CurrencyBL()
        def entityCurrency = currency.getEntityCurrency(session['company_id'])
        def currencies = currency.getCurrenciesToDate(session['language_id'], session['company_id'], date)
        def timePoints = currency.getUsedTimePoints(session['company_id'])

        return [entityCurrency: entityCurrency, currencies: currencies, startDate: date, timePoints: timePoints]
    }

    def getLastTimePointDate = {
        def timePoints = new CurrencyBL().getUsedTimePoints(session['company_id'])
        def lastDate = CommonConstants.EPOCH_DATE;
        if (timePoints.size() > 0) {
            lastDate = timePoints.get(timePoints.size() - 1)
        }
        return lastDate
    }

    def editCurrency = {
        // only shows edit template to create new currencies.
        // currencies can be edited from the main currency config form
        render template: 'currency/edit', model: [currency: null]
    }

    def saveCurrency = {
        def currency = new CurrencyWS()
        bindData(currency, removeBlankParams(params))
		

        try {
			def currencies = new CurrencyBL().getCurrencies(session['language_id'].toInteger(), session['company_id'].toInteger())
			
			if(currencies.find{ it.code.equalsIgnoreCase(currency.code) }){
				throw new SessionInternalError("The currency already exist with this code: " + currency.getCode(),
					["CurrencyWS,code,validation.error.currency.already.exists," + currency.getCode()] as String[]);
	
			}

            webServicesSession.createCurrency(currency)

            flash.message = 'currency.created'
            flash.args = [currency.code]
        } catch (SessionInternalError e) {
            viewUtils.resolveException(flash, session.locale, e)
            chain action: 'currency', model: [currency: currency]
        }

        chain action: 'currency'
    }

    // remove blank strings '' from binding parameters so that
    // we bind null for empty values
    def Map removeBlankParams(params) {
        def filtered = params.findAll { k, v ->
            if (!k.startsWith('_') && v instanceof String) {
                return v.trim().length()
            } else {
                return true
            }
        }
        return filtered
    }

    /*
       Email settings
    */

    def email = {
        def selfDeliver = webServicesSession.getPreference(Constants.PREFERENCE_PAPER_SELF_DELIVERY)
        def customerNotes = webServicesSession.getPreference(Constants.PREFERENCE_TYPE_INCLUDE_CUSTOMER_NOTES)
        def daysForNotification1 = webServicesSession.getPreference(Constants.PREFERENCE_DAYS_ORDER_NOTIFICATION_S1)
        def daysForNotification2 = webServicesSession.getPreference(Constants.PREFERENCE_DAYS_ORDER_NOTIFICATION_S2)
        def daysForNotification3 = webServicesSession.getPreference(Constants.PREFERENCE_DAYS_ORDER_NOTIFICATION_S3)
        def useInvoiceReminders = webServicesSession.getPreference(Constants.PREFERENCE_USE_INVOICE_REMINDERS)
        def firstReminder = webServicesSession.getPreference(Constants.PREFERENCE_FIRST_REMINDER)
        def nextReminder = webServicesSession.getPreference(Constants.PREFERENCE_NEXT_REMINDER)

        [
                selfDeliver: selfDeliver,
                customerNotes: customerNotes,
                daysForNotification1: daysForNotification1,
                daysForNotification2: daysForNotification2,
                daysForNotification3: daysForNotification3,
                useInvoiceReminders: useInvoiceReminders,
                firstReminder: firstReminder,
                nextReminder: nextReminder
        ]
    }


    def saveEmail = {
        def selfDeliver = new PreferenceWS(preferenceType: new PreferenceTypeWS(id: Constants.PREFERENCE_PAPER_SELF_DELIVERY), value: params.selfDeliver ? '1' : '0')
        def customerNotes = new PreferenceWS(preferenceType: new PreferenceTypeWS(id: Constants.PREFERENCE_TYPE_INCLUDE_CUSTOMER_NOTES), value: params.customerNotes ? '1' : '0')
        def daysForNotification1 = new PreferenceWS(preferenceType: new PreferenceTypeWS(id: Constants.PREFERENCE_DAYS_ORDER_NOTIFICATION_S1), value: params.daysForNotification1, intValue: params.daysForNotification1)
        def daysForNotification2 = new PreferenceWS(preferenceType: new PreferenceTypeWS(id: Constants.PREFERENCE_DAYS_ORDER_NOTIFICATION_S2), value: params.daysForNotification2, intValue: params.daysForNotification2)
        def daysForNotification3 = new PreferenceWS(preferenceType: new PreferenceTypeWS(id: Constants.PREFERENCE_DAYS_ORDER_NOTIFICATION_S3), value: params.daysForNotification3,intValue: params.daysForNotification3)
        def useInvoiceReminders = new PreferenceWS(preferenceType: new PreferenceTypeWS(id: Constants.PREFERENCE_USE_INVOICE_REMINDERS), value: params.useInvoiceReminders ? '1' : '0')
        def firstReminder = new PreferenceWS(preferenceType: new PreferenceTypeWS(id: Constants.PREFERENCE_FIRST_REMINDER), value: params.firstReminder, intValue: params.firstReminder)
        def nextReminder = new PreferenceWS(preferenceType: new PreferenceTypeWS(id: Constants.PREFERENCE_NEXT_REMINDER), value: params.nextReminder, intValue: params.nextReminder)

        try {
            webServicesSession.updatePreferences((PreferenceWS[]) [selfDeliver, customerNotes, daysForNotification1, daysForNotification2, daysForNotification3, useInvoiceReminders, firstReminder, nextReminder])

        } catch (SessionInternalError e) {
            viewUtils.resolveException(flash, session.locale, e,getErrorLabel(params))
            render view: 'email', model: [
                    selfDeliver: selfDeliver,
                    customerNotes: customerNotes,
                    daysForNotification1: daysForNotification1,
                    daysForNotification2: daysForNotification2,
                    daysForNotification3: daysForNotification3,
                    useInvoiceReminders: useInvoiceReminders,
                    firstReminder: firstReminder,
                    nextReminder: nextReminder
            ]
            return
        }

        flash.message = 'preferences.updated'
        chain action: email
    }

    private List<String> getErrorLabel(Map <String,String> params){
        List <String> totalTextFields =["nextReminder","daysForNotification2","daysForNotification1","daysForNotification3","firstReminder"]
        List <String> emptyFields=[]
        for(String label:totalTextFields){
            if(params[label].toString().isEmpty() || params[label].equals("null") || params[label].toString().matches(/^\s*$/)){
                emptyFields.add(label)
            }
        }
        return emptyFields
    }
}
