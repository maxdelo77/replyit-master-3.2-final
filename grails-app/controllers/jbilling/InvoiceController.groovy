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

import com.sapienter.jbilling.client.util.DownloadHelper
import com.sapienter.jbilling.client.util.SortableCriteria
import com.sapienter.jbilling.common.SessionInternalError
import com.sapienter.jbilling.server.invoice.InvoiceWS
import com.sapienter.jbilling.server.invoice.db.InvoiceDTO
import com.sapienter.jbilling.server.invoice.db.InvoiceStatusDAS
import com.sapienter.jbilling.server.item.CurrencyBL
import com.sapienter.jbilling.server.user.db.CompanyDTO
import com.sapienter.jbilling.server.util.csv.CsvExporter
import com.sapienter.jbilling.server.util.csv.Exporter
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import com.sapienter.jbilling.server.metafields.MetaFieldBL
import com.sapienter.jbilling.server.metafields.db.EntityType
import com.sapienter.jbilling.server.mediation.db.MediationMapDAS

import com.sapienter.jbilling.server.metafields.db.DataType
import com.sapienter.jbilling.server.metafields.db.MetaField
import com.sapienter.jbilling.server.metafields.db.value.IntegerMetaFieldValue
import com.sapienter.jbilling.server.metafields.db.value.StringMetaFieldValue

import org.hibernate.FetchMode
import org.hibernate.criterion.MatchMode
import org.hibernate.criterion.Restrictions
import org.hibernate.criterion.DetachedCriteria
import org.hibernate.criterion.Property
import org.hibernate.criterion.Projections
import com.sapienter.jbilling.server.invoice.db.InvoiceLineDTO
import com.sapienter.jbilling.server.invoice.InvoiceBL


/**
 * BillingController
 *
 * @author Vikas Bodani
 * @since
 */
@Secured(["MENU_91"])
class InvoiceController {

    static pagination = [max: 10, offset: 0, sort: 'id', order: 'desc']

    def webServicesSession
    def viewUtils
    def filterService
    def recentItemService
    def breadcrumbService
    def subAccountService

    def index = {
        list()
    }

    def list = {
        def filters = filterService.getFilters(FilterType.INVOICE, params)
        def invoiceIds = parameterIds

        def invoices = getInvoices(filters, params, invoiceIds)
        def selected = params.id ? InvoiceDTO.get(params.int('id')) : null

        breadcrumbService.addBreadcrumb(controllerName, 'list', null, params.int('id'))
		
		// if id is present and invoice not found, give an error message along with the list
        if (params.id?.isInteger() && selected == null) {
			flash.error = 'invoice.not.found'
            flash.args = [params.id]
        }
		
		def contactFieldTypes = params['contactFieldTypes']

        if (params.applyFilter || params.partial) {
            render template: 'invoices', model: [invoices: invoices, filters: filters, selected: selected, currencies: retrieveCurrencies(), contactFieldTypes: contactFieldTypes]
        } else {
            def lines = null
            if(selected){
                InvoiceBL invoiceBl = new InvoiceBL(selected);
                InvoiceDTO invoiceDto = invoiceBl.getInvoiceDTOWithHeaderLines();
                lines = invoiceDto.getInvoiceLines();
            }

            render view: 'list', model: [invoices: invoices, filters: filters, selected: selected, currencies: retrieveCurrencies(), lines: lines]
        }
    }

    def getInvoices(filters, params, ids) {
        params.max = params?.max?.toInteger() ?: pagination.max
        params.offset = params?.offset?.toInteger() ?: pagination.offset
        params.sort = params?.sort ?: pagination.sort
        params.order = params?.order ?: pagination.order

        // hide review invoices by default
        def reviewFilter = filters.find { it.field == 'isReview' }
        if (reviewFilter && reviewFilter.value == null) reviewFilter.integerValue = Integer.valueOf(0)

        // get list
        return InvoiceDTO.createCriteria().list(
                max: params.max,
                offset: params.offset
        ) {
            and {
                filters.each { filter ->
                    if (filter.value != null) {
                        //handle invoiceStatus
                        if (filter.field == 'invoiceStatus') {
                            def statuses = new InvoiceStatusDAS().findAll()
                            eq("invoiceStatus", statuses.find { it.primaryKey?.equals(filter.integerValue) })
                        } else if (filter.field == 'contact.fields') {
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

                createAlias('baseUser', 'baseUser')
                eq('baseUser.company', new CompanyDTO(session['company_id']))
                eq('deleted', 0)

                if (SpringSecurityUtils.ifNotGranted("INVOICE_74")) {
                    if (SpringSecurityUtils.ifAnyGranted("INVOICE_75")) {
                        // restrict query to sub-account user-ids
                        'in'('baseUser.id', subAccountService.subAccountUserIds)
                    } else {
                        // limit list to only this customer
                        eq('baseUser.id', session['user_id'])
                    }
                }

                if (ids) {
                    'in'('id', ids.toArray(new Integer[ids.size()]))
                }
            }

            // apply sorting
            SortableCriteria.sort(params, delegate)
        }
    }

    /**
     * Applies the set filters to the order list, and exports it as a CSV for download.
     */
    @Secured(["INVOICE_73"])
    def csv = {
        def filters = filterService.getFilters(FilterType.INVOICE, params)

        params.max = CsvExporter.MAX_RESULTS
        def invoices = getInvoices(filters, params, null)

        if (invoices.totalCount > CsvExporter.MAX_RESULTS) {
            flash.error = message(code: 'error.export.exceeds.maximum')
            flash.args = [CsvExporter.MAX_RESULTS]
            redirect action: 'list', id: params.id

        } else {
            DownloadHelper.setResponseHeader(response, "invoices.csv")
            Exporter<InvoiceDTO> exporter = CsvExporter.createExporter(InvoiceDTO.class);
            render text: exporter.export(invoices), contentType: "text/csv"
        }
    }

    /**
     * Convenience shortcut, this action shows all invoices for the given user id.
     */
    def user = {
        def filter = new Filter(type: FilterType.INVOICE, constraintType: FilterConstraint.EQ, field: 'baseUser.id', template: 'id', visible: true, integerValue: params.int('id'))
        filterService.setFilter(FilterType.INVOICE, filter)

        redirect action: 'list'
    }

    @Secured(["INVOICE_72"])
    def show = {
        def invoice = InvoiceDTO.get(params.int('id'))
        if (!invoice) {
            log.debug("Redirecting to list")
            redirect(action: 'list')
            return
        }
        recentItemService.addRecentItem(invoice.id, RecentItemType.INVOICE)
        breadcrumbService.addBreadcrumb(controllerName, 'list', null, invoice.id, invoice.number)

        InvoiceBL invoiceBl = new InvoiceBL((InvoiceDTO)invoice);
        InvoiceDTO invoiceDto = invoiceBl.getInvoiceDTOWithHeaderLines();
        List<InvoiceLineDTO> lines = invoiceDto.getInvoiceLines();

        render template: params.template ?: 'show', model: [selected: invoice, currencies: retrieveCurrencies(), lines : lines]
    }

    def snapshot = {
        def invoiceId = params.int('id')
        if (invoiceId) {
            InvoiceWS invoice = webServicesSession.getInvoiceWS(invoiceId)
            render template: 'snapshot', model: [ invoice: invoice, currencies: retrieveCurrencies(), availableMetaFields: retrieveAvailableMetaFields() ]
        }
    }

    @Secured(["INVOICE_70"])
    def delete = {
        int invoiceId = params.int('id')

        if (invoiceId) {
            try {
                webServicesSession.deleteInvoice(invoiceId)
                flash.message = 'invoice.delete.success'
                flash.args = [invoiceId]

            } catch (SessionInternalError e) {
                viewUtils.resolveException(flash, session.locale, e);
            } catch (Exception e) {
                log.error("Exception deleting invoice.", e)
                flash.error = 'error.invoice.delete'
                flash.args = [params.id]
                redirect action: 'list', params: [id: invoiceId]
                return
            }
        }

        redirect action: 'list'
    }

    @Secured(["INVOICE_71"])
    def email = {
        if (params.id) {
            try {
                def sent = webServicesSession.notifyInvoiceByEmail(params.int('id'))

                if (sent) {
                    flash.message = 'invoice.prompt.success.email.invoice'
                    flash.args = [params.id]
                } else {
                    flash.error = 'invoice.prompt.failure.email.invoice'
                    flash.args = [params.id]
                }

            } catch (Exception e) {
                log.error("Exception occurred sending invoice email", e)
                flash.error = 'invoice.prompt.failure.email.invoice'
                flash.args = [params.id]
            }
        }

        redirect action: 'list', params: [id: params.id]
    }

    def downloadPdf = {
        Integer invoiceId = params.int('id')

        try {
            byte[] pdfBytes = webServicesSession.getPaperInvoicePDF(invoiceId)
            def invoice = webServicesSession.getInvoiceWS(invoiceId)
            DownloadHelper.sendFile(response, "invoice-${invoice?.number}.pdf", "application/pdf", pdfBytes)

        } catch (Exception e) {
            log.error("Exception fetching PDF invoice data.", e)
            flash.error = 'invoice.prompt.failure.downloadPdf'
            redirect action: 'list', params: [id: invoiceId]
        }
    }

    @Secured(["PAYMENT_33"])
    def unlink = {
        try {
            webServicesSession.removePaymentLink(params.int('id'), params.int('paymentId'))
            flash.message = "payment.unlink.success"

        } catch (SessionInternalError e) {
            viewUtils.resolveException(flash, session.locale, e);

        } catch (Exception e) {
            log.error("Exception unlinking invoice.", e)
            flash.error = "error.invoice.unlink.payment"
        }

        redirect action: 'list', params: [id: params.id]
    }

    def byProcess = {
        if (!params.id) {
            flash.error = 'error.invoice.byprocess.missing.id'
            chain action: list
            return
        }

        // limit by billing process
        def processFilter = new Filter(type: FilterType.INVOICE, constraintType: FilterConstraint.EQ, field: 'billingProcess.id', template: 'id', visible: true, integerValue: params.int('id'))
        filterService.setFilter(FilterType.INVOICE, processFilter)

        // show review invoices if process generated a review
        def reviewFilter = new Filter(type: FilterType.INVOICE, constraintType: FilterConstraint.EQ, field: 'isReview', template: 'invoice/review', visible: true, integerValue: params.int('isReview'))
        filterService.setFilter(FilterType.INVOICE, reviewFilter, false)

        def filters = filterService.getFilters(FilterType.INVOICE, params)
        def invoices = getInvoices(filters, params, null)

        render view: 'list', model: [invoices: invoices, filters: filters, currencies: retrieveCurrencies()]

    }

    def byMediation = {

        def invoiceIds = parameterIds
        def filters = filterService.getFilters(FilterType.INVOICE, params)
        def invoices

        if (invoiceIds.isEmpty()) {
            invoices = new ArrayList<InvoiceDTO>()
        } else {
            invoices = getInvoices(filters, params, invoiceIds)
        }
        render view: 'list', model: [invoices: invoices, filters: filters, currencies: retrieveCurrencies()]
    }

    def retrieveCurrencies() {
		//in this controller we need only currencies objects with inUse=true without checking rates on date
        return new CurrencyBL().getCurrenciesWithoutRates(session['language_id'].toInteger(), session['company_id'].toInteger(),true)
    }

    def getAvailableMetaFields() {
        return MetaFieldBL.getAvailableFieldsList(session["company_id"], EntityType.INVOICE);
    }

    def getParameterIds() {

        // Grails bug when using lists with <g:remoteLink>
        // http://jira.grails.org/browse/GRAILS-8330
        // TODO (pai) remove workaround

        def parameterIds = new ArrayList<Integer>()
        def idParamList = params.list('ids')
        idParamList.each { idParam ->
            if (idParam?.isInteger()) {
                parameterIds.add(idParam.toInteger())
            }
        }
        if (parameterIds.isEmpty()) {
            String ids = params.ids
            if (ids) {
                ids = ids.replace('[', "").replace(']', "")
                String [] numbers = ids.split(", ")
                numbers.each { paramId ->
                    if (paramId?.isInteger()) {
                        parameterIds.add(paramId.toInteger());
                    }
                }
            }
        }

        return parameterIds;
    }
	
    def retrieveAvailableMetaFields() {
        return MetaFieldBL.getAvailableFieldsList(session["company_id"], EntityType.INVOICE);
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
