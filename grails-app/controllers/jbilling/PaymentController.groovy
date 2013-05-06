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

import com.sapienter.jbilling.client.metafield.MetaFieldUtils
import com.sapienter.jbilling.client.util.DownloadHelper
import com.sapienter.jbilling.client.util.SortableCriteria
import com.sapienter.jbilling.common.Constants
import com.sapienter.jbilling.common.SessionInternalError
import com.sapienter.jbilling.common.Util
import com.sapienter.jbilling.server.entity.AchDTO
import com.sapienter.jbilling.server.entity.CreditCardDTO
import com.sapienter.jbilling.server.entity.PaymentInfoChequeDTO
import com.sapienter.jbilling.server.invoice.InvoiceWS
import com.sapienter.jbilling.server.item.CurrencyBL
import com.sapienter.jbilling.server.metafields.MetaFieldBL
import com.sapienter.jbilling.server.metafields.MetaFieldValueWS
import com.sapienter.jbilling.server.metafields.db.DataType
import com.sapienter.jbilling.server.metafields.db.MetaField
import com.sapienter.jbilling.server.metafields.db.value.IntegerMetaFieldValue
import com.sapienter.jbilling.server.metafields.db.value.StringMetaFieldValue
import com.sapienter.jbilling.server.metafields.db.EntityType
import com.sapienter.jbilling.server.payment.PaymentBL
import com.sapienter.jbilling.server.payment.PaymentWS
import com.sapienter.jbilling.server.payment.db.PaymentDAS
import com.sapienter.jbilling.server.payment.db.PaymentDTO
import com.sapienter.jbilling.server.user.CreditCardBL
import com.sapienter.jbilling.server.user.db.CompanyDTO
import com.sapienter.jbilling.server.util.csv.CsvExporter
import com.sapienter.jbilling.server.util.csv.Exporter
import grails.plugins.springsecurity.Secured
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import com.sapienter.jbilling.server.user.db.UserDTO
import com.sapienter.jbilling.server.user.db.UserDAS

import org.hibernate.FetchMode
import org.hibernate.criterion.MatchMode
import org.hibernate.criterion.Restrictions
import org.hibernate.criterion.DetachedCriteria
import org.hibernate.criterion.Property
import org.hibernate.criterion.Projections
import org.hibernate.Criteria


/**
 * PaymentController 
 *
 * @author Brian Cowdery
 * @since 04/01/11
 */
@Secured(["MENU_93"])
class PaymentController {

    static pagination = [ max: 10, offset: 0, sort: 'id', order: 'desc' ]

    def webServicesSession
    def webServicesValidationAdvice
    def viewUtils
    def filterService
    def recentItemService
    def breadcrumbService
    def subAccountService

    def index = {
        list()
    }

    def getList(filters, params) {
        params.max = params?.max?.toInteger() ?: pagination.max
        params.offset = params?.offset?.toInteger() ?: pagination.offset
        params.sort = params?.sort ?: pagination.sort
        params.order = params?.order ?: pagination.order

        return PaymentDTO.createCriteria().list(
                max:    params.max,
                offset: params.offset
        ) {
            createAlias('baseUser', 'u')

            // create alias only if applying invoice filters to prevent duplicate results
            if (filters.find{ it.field.startsWith('i.') && it.value })
                createAlias('invoicesMap', 'i', Criteria.LEFT_JOIN)

            and {
                filters.each { filter ->
                    if (filter.value != null) {
                    	if (filter.field == 'contact.fields') {
                            String typeId = params['contactFieldTypes']
                            String ccfValue = filter.stringValue;
                            log.debug "Contact Field Type ID: ${typeId}, CCF Value: ${ccfValue}"

                            if (typeId && ccfValue) {
                                MetaField type = findMetaFieldType(typeId.toInteger());
                                if (type != null) {
                                    createAlias("metaFields", "fieldValue")
                                    createAlias("fieldValue.field", "type")
                                    setFetchMode("type", FetchMode.JOIN)
                                    eq("type.id", typeId.toInteger())

                                    switch (type.getDataType()) {
                                        case DataType.STRING:
                                        	def subCriteria = DetachedCriteria.forClass(StringMetaFieldValue.class, "stringValue")
                                        					.setProjection(Projections.property('id'))
										    				.add(Restrictions.like('stringValue.value', ccfValue + '%').ignoreCase())

                                        	addToCriteria(Property.forName("fieldValue.id").in(subCriteria))
                                            break;
                                        case DataType.INTEGER:
                                        	def subCriteria = DetachedCriteria.forClass(IntegerMetaFieldValue.class, "integerValue")
                                        					.setProjection(Projections.property('id'))
										    				.add(Restrictions.eq('integerValue.value', ccfValue.toInteger()))

                                        	addToCriteria(Property.forName("fieldValue.id").in(subCriteria))
                                            break;
                                        case DataType.ENUMERATION:
                                        case DataType.JSON_OBJECT:
                                            addToCriteria(Restrictions.ilike("fieldValue.value", ccfValue, MatchMode.ANYWHERE))
                                            break;
                                        default:
                                        // todo: now searching as string only, search for other types is impossible
//                                            def fieldValue = type.createValue();
//                                            bindData(fieldValue, ['value': ccfValue])
//                                            addToCriteria(Restrictions.eq("fieldValue.value", fieldValue.getValue()))

                                            addToCriteria(Restrictions.eq("fieldValue.value", ccfValue))
                                            break;
                                    }

                                }
                            }
                        } else {
                        	addToCriteria(filter.getRestrictions());
                        }
                    }
                }

                eq('u.company', new CompanyDTO(session['company_id']))
                eq('deleted', 0)

                if (SpringSecurityUtils.ifNotGranted("PAYMENT_36")) {
                    if (SpringSecurityUtils.ifAnyGranted("PAYMENT_37")) {
                        // restrict query to sub-account user-ids
                        'in'('u.id',subAccountService.getSubAccountUserIds())
                    } else {
                        // limit list to only this customer
                        eq('u.id', session['user_id'])
                    }
                }
            }

            // apply sorting
            SortableCriteria.sort(params, delegate)
        }
    }

    /**
     * Gets a list of payments and renders the the list page. If the "applyFilters" parameter is given,
     * the partial "_payments.gsp" template will be rendered instead of the complete payments list page.
     */
    def list = {
        def filters = filterService.getFilters(FilterType.PAYMENT, params)
        def payments = getList(filters, params)

        def selected = params.id ? PaymentDTO.get(params.int("id")) : null

        breadcrumbService.addBreadcrumb(controllerName, 'list', null, params.int('id'))

        def contactFieldTypes = params['contactFieldTypes']

        if (params.applyFilter || params.partial) {
            render template: 'payments', model: [ payments: payments, selected: selected, filters: filters, contactFieldTypes: contactFieldTypes ]
        } else {
            render view: 'list', model: [ payments: payments, selected: selected, filters: filters ]
        }
    }

    /**
     * Applies the set filters to the payment list, and exports it as a CSV for download.
     */
    @Secured(["PAYMENT_35"])
    def csv = {
        def filters = filterService.getFilters(FilterType.PAYMENT, params)

        params.max = CsvExporter.MAX_RESULTS
        def payments = getList(filters, params)

        if (payments.totalCount > CsvExporter.MAX_RESULTS) {
            flash.error = message(code: 'error.export.exceeds.maximum')
            flash.args = [ CsvExporter.MAX_RESULTS ]
            redirect action: 'list', id: params.id

        } else {
            DownloadHelper.setResponseHeader(response, "payments.csv")
            Exporter<PaymentDTO> exporter = CsvExporter.createExporter(PaymentDTO.class);
            render text: exporter.export(payments), contentType: "text/csv"
        }
    }

    /**
     * Downloads the payment details for the given paymentID
     */
    @Secured(["hasAnyRole('PAYMENT_34', 'PAYMENT_36')"])
    def downloadPayment = {
        // get paymentId and userId from params
        Integer paymentId = params.int('paymentId')
        Integer userId = params.int('userId')
        try {
            // limit user to to only this customer's payment
            if ( SpringSecurityUtils.ifAllGranted("ROLE_CUSTOMER")  
                && SpringSecurityUtils.ifNotGranted("PAYMENT_36")) {
                log.debug 'Is Customer, and PAYMENT_36 Role not granted.'
                if ( !userId.equals(session['user_id'])) {
                    log.error "Unauthorized access: User ${session['user_id']} trying to view User ${userId} 's Payment"
                    throw new SessionInternalError("Unauthorized.");
                }
            }
            
            UserDTO user = UserDTO.get(userId)
            PaymentDTO paymentDto= PaymentDTO.get(paymentId)
            
            // retrieve the payment kept at specified location
            String baseDir= com.sapienter.jbilling.common.Util.getSysProp("base_dir")
            String separator= System.getProperty("file.separator")
            String fileName= "Payment-${paymentDto.getPublicNumber()}.pdf"
            String pdfLocation = "${baseDir}notifications${separator}${user.getUserName()}${separator}${fileName}"
            
            log.debug "Pdf Location: $pdfLocation"
            byte[] pdfBytes = new File(pdfLocation).getBytes()
            DownloadHelper.sendFile(response, fileName, "application/pdf", pdfBytes)
        } catch (FileNotFoundException fnfe) {
            log.error("File Not Found Exception "+fnfe)
            flash.error = "payment.prompt.failure.downloadPdf.fileNotFound"
            redirect(action: 'list', params: [id: paymentId])
        } catch (Exception e) {
            log.error("Some Exception occured "+e)
            flash.error = "payment.prompt.failure.downloadPdf"
            redirect(action: 'list', params: [id: paymentId])
        }

    }

    /**
     * Show details of the selected payment.
     */
    @Secured(["PAYMENT_34"])
    def show = {
        PaymentDTO payment = PaymentDTO.get(params.int('id'))
        if (!payment) {
            log.debug "redirecting to list"
            redirect(action: 'list')
            return
        }
        recentItemService.addRecentItem(params.int('id'), RecentItemType.PAYMENT)
        breadcrumbService.addBreadcrumb(controllerName, 'list', params.template ?: null, params.int('id'))

        render template: 'show', model: [ selected: payment ]
    }

    /**
     * Convenience shortcut, this action shows all payments for the given user id.
     */
    def user = {
        def filter =  new Filter(type: FilterType.PAYMENT, constraintType: FilterConstraint.EQ, field: 'u.id', template: 'id', visible: true, integerValue: params.id)
        filterService.setFilter(FilterType.PAYMENT, filter)

        redirect (action: "list")
    }

    /**
     * Delete the given payment id
     */
    @Secured(["PAYMENT_32"])
    def delete = {
        if (params.id) {
            try {
                webServicesSession.deletePayment(params.int('id'))
                log.debug("Deleted payment ${params.id}.")
                flash.message = 'payment.deleted'
                flash.args = [params.id]
            } catch (SessionInternalError e) {
                viewUtils.resolveException(flash, session.local, e)
                //redirect action: 'list', params: [ id: params.id ]
                params.applyFilter = false
                params.partial = true
                list()
                return
            }
        }

        // render the partial payments list
        params.applyFilter = true
        list()
    }

    /**
     * Shows the payment link screen for the given payment ID showing a list of un-paid invoices
     * that the payment can be applied to.
     */
    @Secured(["PAYMENT_33"])
    def link = {
        def payment = webServicesSession.getPayment(params.int('id'))
        def user = webServicesSession.getUserWS(payment?.userId ?: params.int('userId'))
        def invoices = getUnpaidInvoices(user.userId)

        render view: 'link', model: [ payment: payment, user: user, invoices: invoices, currencies: retrieveCurrencies(), invoiceId: params.invoiceId, availableFields: retrieveAvailableMetaFields() ]
    }

    /**
     * Applies a given payment ID to the given invoice ID.
     */
    @Secured(["PAYMENT_33"])
    def applyPayment = {
        def payment = webServicesSession.getPayment(params.int('id'))

        if (payment && params.invoiceId) {
            try {
                log.debug("appling payment ${payment} to invoice ${params.invoiceId}")
                webServicesSession.createPaymentLink(params.int('invoiceId'), payment.id)

                flash.message = 'payment.link.success'
                flash.args = [ payment.id, params.invoiceId ]

            } catch (SessionInternalError e) {
                viewUtils.resolveException(flash, session.local, e)
                link()
                return
            }

        } else if (!payment) {
            flash.warn = 'payment.not.exists'
            flash.args = [payment.id, params.invoiceId]
        } else {
            flash.warn = 'invoice.not.selected'
            flash.args = [payment.id, params.invoiceId]
        }


        // show the list page
        def filters = filterService.getFilters(FilterType.PAYMENT, params)
        def payments = getList(filters, params)
        def selected = params.id ? PaymentDTO.get(params.int("id")) : null

        render view: 'list', model: [ payments: payments, selected: selected, filters: filters ]
    }

    /**
     * Un-links the given payment ID from the given invoice ID and re-renders
     * the "show payment" view panel.
     */
    @Secured(["PAYMENT_33"])
    def unlink = {
        try {
            webServicesSession.removePaymentLink(params.int('invoiceId'), params.int('id'))
            flash.message = "payment.unlink.success"

        } catch (SessionInternalError e) {
            viewUtils.resolveException(flash, session.locale, e);

        } catch (Exception e) {
            log.error("Exception unlinking invoice.", e)
            flash.error = "error.invoice.unlink.payment"
        }

        redirect action: 'list', params: [id: params.id]
    }

    /**
     * Redirects to the user list and sets a flash message.
     */
    @Secured(["PAYMENT_30"])
    def create = {
        flash.info = 'payment.select.customer'
        redirect controller: 'customer', action: 'list'
    }

    /**
     * Gets the payment to be edited and shows the "edit.gsp" view. This edit action cannot be used
     * to create a new payment, as creation requires a wizard style flow where the user is selected first.
     */
    @Secured(["hasAnyRole('PAYMENT_30', 'PAYMENT_31')"])
    def edit = {
        def payment
        def user

        try {
            payment = params.id ? webServicesSession.getPayment(params.int('id')) : new PaymentWS()

            if (payment?.deleted==1) {
            	paymentNotFoundErrorRedirect(params.id)
            	return
            }

            user = webServicesSession.getUserWS(payment?.userId ?: params.int('userId'))

        } catch (SessionInternalError e) {
            log.error("Could not fetch WS object", e)
			paymentNotFoundErrorRedirect(params.id)
            return
        }


        def invoices = getUnpaidInvoices(user.userId)
        def paymentMethods = CompanyDTO.get(session['company_id']).getPaymentMethods()

        // set default payment instrument for new payments
        if (!params.id) {
            def instrument = webServicesSession.getUserPaymentInstrument(user.userId)
            payment.setCreditCard(instrument?.creditCard)
            payment.setAch(instrument?.ach)
        }

        breadcrumbService.addBreadcrumb(controllerName, actionName, null, params.int('id'))

        //send all the payments of the current user as well which are not normal payments with a balance greater than zero
        List<PaymentDTO> refundablePayments = new PaymentDAS().getRefundablePayments(user.getUserId())
        log.debug "invoices are ${invoices}"
        log.debug "payments are ${refundablePayments}"

        [ payment: payment, user: user, invoices: invoices, currencies: retrieveCurrencies(), paymentMethods: paymentMethods, invoiceId: params.int('invoiceId'), refundablePayments: refundablePayments, refundPaymentId: params.int('payment?.paymentId'), availableFields: retrieveAvailableMetaFields() ]
    }

    private void paymentNotFoundErrorRedirect(paymentId) {
    	flash.error = 'payment.not.found'
		flash.args = [ paymentId as String ]
		redirect controller: 'payment', action: 'list'
    }

    def getUnpaidInvoices(Integer userId) {
        def invoiceIds = webServicesSession.getUnpaidInvoices(userId);

        List<InvoiceWS> invoices = new ArrayList<InvoiceWS>(invoiceIds.size());
        for (Integer id : invoiceIds)
            invoices.add(webServicesSession.getInvoiceWS(id))
        return invoices;
    }

    /**
     * Shows a summary of the created/edited payment to be confirmed before saving.
     */
    @Secured(["hasAnyRole('PAYMENT_30', 'PAYMENT_31')"])
    def confirm = {
        def payment = new PaymentWS()
        bindPayment(payment, params)

        session['user_payment']= payment

        // make sure the user still exists before
        def users
        try {
            user = webServicesSession.getUserWS(payment?.userId ?: params.int('userId'))
        } catch (SessionInternalError e) {
            log.error("Could not fetch WS object", e)

            flash.error = 'customer.not.found'
            flash.args = [ params.id ]

            redirect controller: 'payment', action: 'list'
            return
        }

        def invoices = getUnpaidInvoices(user.userId)
        def paymentMethods = CompanyDTO.get(session['company_id']).getPaymentMethods()
		List<PaymentDTO> refundablePayments = new PaymentDAS().getRefundablePayments(user.getUserId())

        // validate before showing the confirmation page
        try {
            webServicesValidationAdvice.validateObject(payment)

            if(payment.amountAsDecimal == BigDecimal.ZERO) {
                String [] errors = ["PaymentWS,amount,validation.error.payment.amount.cannot.be.zero"]
                    throw new SessionInternalError("Payment Amount Cannot Be Zero",
                        errors);
            }

            if(payment.isRefund) {
                if(!PaymentBL.validateRefund(payment)){
                    String [] errors = ["PaymentWS,paymentId,validation.error.apply.without.payment.or.different.linked.payment.amount"]
                    throw new SessionInternalError("Either refund payment was not linked to any payment or the refund amount is in-correct",
                            errors);
                }
            }
        } catch (SessionInternalError e) {
            viewUtils.resolveException(flash, session.local, e)
            boolean isCheque = false
            if (payment.methodId == Constants.PAYMENT_METHOD_CHEQUE) {
                 isCheque = true
            }
            render view: 'edit', model: [ payment: payment, user: user, invoices: invoices, refundablePayments: refundablePayments, currencies: retrieveCurrencies(), paymentMethods: paymentMethods, invoiceId: params.int('invoiceId'), availableFields: retrieveAvailableMetaFields(), isCheque: isCheque ]
            return
        }

        // validation passed, render the confirmation page
        def processNow = params.processNow ? true : false
        [ payment: payment, user: user, invoices: invoices, currencies: retrieveCurrencies(), processNow: processNow, invoiceId: params.invoiceId, availableFields: retrieveAvailableMetaFields() ]
    }

    /**
     * Validate and save payment.
     */
    @Secured(["hasAnyRole('PAYMENT_30', 'PAYMENT_31')"])
    def save = {

        /* Reuse the same payment that was bound earlier during confirm */
        def payment = session['user_payment'];
        //new PaymentWS()
        //bindPayment(payment, params)

        def invoiceId = params.int('invoiceId')

        // save or update
        try {
            if (!payment.id || payment.id == 0) {
                if (SpringSecurityUtils.ifAllGranted("PAYMENT_30")) {
                    def processNow = params.boolean('processNow') && payment.methodId != Constants.PAYMENT_METHOD_CHEQUE

                    log.debug("creating payment ${payment} for invoice ${invoiceId}")

                    if (processNow) {
                        log.debug("processing payment in real time")

                        def authorization = webServicesSession.processPayment(payment, invoiceId)
                        payment.id = authorization.paymentId

                        if (authorization.result) {
                            flash.message = getSuccessMessageKey(payment, processNow)
                            flash.args = [ payment.id ]

                        } else {
							def autorizationMessage = authorization.responseMessage;
							if (autorizationMessage == null) {
								autorizationMessage = "Payment processor unavailable"
							}
                            flash.error = 'payment.failed'
                            flash.args = [ payment.id, autorizationMessage ]
                        }

                    } else {
                        log.debug("entering payment")
                        payment.id = webServicesSession.applyPayment(payment, invoiceId)

                        if (payment.id) {
                            flash.info = getSuccessMessageKey(payment, processNow)
                            flash.args = [ payment.id ]

                        } else {
                            flash.info = 'payment.entered.failed'
                            flash.args = [ payment.id ]
                        }
                    }

                } else {
                    render view: '/login/denied'
                    return
                }

            } else {
                if (SpringSecurityUtils.ifAllGranted("PAYMENT_31")) {
                    log.debug("saving changes to payment ${payment.id}")
                    webServicesSession.updatePayment(payment)

                    if (invoiceId) {
                        log.debug("appling payment ${payment} to invoice ${invoiceId}")
                        webServicesSession.createPaymentLink(invoiceId, payment.id)
                    }

                    flash.message = 'payment.updated'
                    flash.args = [ payment.id ]

                } else {
                    render view: '/login/denied'
                }
            }

        } catch (SessionInternalError e) {
            viewUtils.resolveException(flash, session.local, e)

            def user = webServicesSession.getUserWS(payment.userId)
            def invoices = getUnpaidInvoices(user.userId)
            def paymentMethods = CompanyDTO.get(session['company_id']).getPaymentMethods()
            List<PaymentDTO> refundablePayments = new PaymentDAS().getRefundablePayments(user.getUserId())

            render view: 'edit', model: [ payment: payment, user: user, invoices: invoices, currencies: retrieveCurrencies(), paymentMethods: paymentMethods, invoiceId: params.int('invoiceId'), availableFields: retrieveAvailableMetaFields(), refundablePayments: refundablePayments, refundPaymentId: params.int('payment?.paymentId')]
            return
        } finally {
            session.removeAttribute("user_payment")
        }

        chain action: 'list', params: [ id: payment.id ]
    }

    /**
     * Notify about this payment.
     */
    def emailNotify = {

        def pymId= params.id.toInteger()
        try {
            def result= webServicesSession.notifyPaymentByEmail(pymId)
            if (result) {
                flash.info = 'payment.notification.sent'
                flash.args = [ pymId ]
            } else {
                flash.error = 'payment.notification.sent.fail'
                flash.args = [ pymId ]
            }
        } catch (SessionInternalError e) {
            viewUtils.resolveException(flash, session.local, e)
        }
        chain action: 'list', params: [ id: pymId]
    }
    
    def bindPayment(payment, params) {
        if(params.isRefund == 'on' || params.isRefund == '1') {
            params.payment.isRefund = 1
        } else {
            params.payment.isRefund = 0
        }
        bindData(payment, params, 'payment')

        bindMetaFields(payment, params)

        // bind credit card object if parameters present
        if (params.creditCard.any { key, value -> value }) {
            def creditCard = new CreditCardDTO()
            bindData(creditCard, params, 'creditCard')
            bindExpiryDate(creditCard, params)
            payment.setCreditCard(creditCard)

            //get the un-obscured credit card number
            if (creditCard.id) {
                def existingCard =  new CreditCardBL(creditCard.id).getEntity();
                if (existingCard) creditCard.number = existingCard.getNumber()
            }

            payment.setMethodId(Util.getPaymentMethod(creditCard.number))
        }

        // bind ach object if parameters present
        if (params.ach.any { key, value -> value }) {
            def ach = new AchDTO()
            bindData(ach, params, 'ach')
            payment.setAch(ach)

            payment.setMethodId(Constants.PAYMENT_METHOD_ACH)
        }

        // bind cheque object if parameters present
        if (params.cheque.any { key, value -> value }) {
            def cheque = new PaymentInfoChequeDTO()
            bindData(cheque, params, 'cheque')
            payment.setCheque(cheque)

            payment.setMethodId(Constants.PAYMENT_METHOD_CHEQUE)

            // no processing for cheques
            params.processNow = false
        }

        return payment
    }

    def bindExpiryDate(creditCard, params) {
        Integer expiryMonth = params.int('expiryMonth')
        Integer expiryYear = params.int('expiryYear')

        if (expiryMonth && expiryYear)  {
            Calendar calendar = Calendar.getInstance()
            calendar.clear()
            calendar.set(Calendar.MONTH, expiryMonth - 1) // calendar API months start at 0
            calendar.set(Calendar.YEAR, expiryYear)
            calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))

            creditCard.expiry = calendar.getTime()
        }
    }

    def getSuccessMessageKey(payment, processRealTime) {
        if (payment.getCreditCard()) {
            return processRealTime ? 'payment.credit.card.processed' : 'payment.credit.card.entered'
        }

        if (payment.getAch()) {
            return processRealTime ? 'payment.ach.processed' : 'payment.ach.entered'
        }

        if (payment.getCheque()) {
            return processRealTime ? 'payment.cheque.processed' : 'payment.cheque.entered'
        }

        return 'payment.successful'
    }

    def retrieveCurrencies() {
        return new CurrencyBL().getCurrenciesWithoutRates(session['language_id'].toInteger(), session['company_id'].toInteger(),true)
    }

    def retrieveAvailableMetaFields() {
        return MetaFieldBL.getAvailableFieldsList(session['company_id'], EntityType.PAYMENT);
    }

    def bindMetaFields(paymentWS, params) {
        def fieldsArray = MetaFieldUtils.bindMetaFields(retrieveAvailableMetaFields(), params);
        paymentWS.metaFields = fieldsArray.toArray(new MetaFieldValueWS[fieldsArray.size()])
    }

    def findMetaFieldType(Integer metaFieldId) {
        for (MetaField field : retrieveAvailableMetaFields()) {
            if (field.id == metaFieldId) {
                return field;
            }
        }
        return null;
    }
}
