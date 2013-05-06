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

import com.sapienter.jbilling.client.util.Constants
import com.sapienter.jbilling.client.util.DownloadHelper
import com.sapienter.jbilling.common.SessionInternalError
import com.sapienter.jbilling.server.customer.CustomerBL
import com.sapienter.jbilling.server.invoice.InvoiceBL
import com.sapienter.jbilling.server.invoice.db.InvoiceDAS
import com.sapienter.jbilling.server.item.CurrencyBL
import com.sapienter.jbilling.server.metafields.db.DataType
import com.sapienter.jbilling.server.metafields.db.MetaField
import com.sapienter.jbilling.server.metafields.db.value.IntegerMetaFieldValue
import com.sapienter.jbilling.server.metafields.db.value.StringMetaFieldValue
import com.sapienter.jbilling.server.order.OrderBL
import com.sapienter.jbilling.server.order.OrderWS
import com.sapienter.jbilling.server.order.db.OrderDAS
import com.sapienter.jbilling.server.order.db.OrderDTO
import com.sapienter.jbilling.server.order.db.OrderPeriodDAS
import com.sapienter.jbilling.server.order.db.OrderStatusDAS
import com.sapienter.jbilling.server.user.UserWS
import com.sapienter.jbilling.server.user.db.CustomerDTO
import com.sapienter.jbilling.server.user.db.UserDAS
import com.sapienter.jbilling.server.user.db.UserDTO
import com.sapienter.jbilling.server.util.csv.CsvExporter
import com.sapienter.jbilling.server.util.csv.Exporter
import grails.plugins.springsecurity.Secured
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import com.sapienter.jbilling.server.user.db.CompanyDTO

import org.hibernate.FetchMode
import org.hibernate.criterion.MatchMode
import org.hibernate.criterion.Restrictions
import org.hibernate.criterion.DetachedCriteria
import org.hibernate.criterion.Property
import org.hibernate.criterion.Projections
import org.hibernate.Criteria
import com.sapienter.jbilling.client.util.SortableCriteria
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import com.sapienter.jbilling.server.invoice.InvoiceWS
import com.sapienter.jbilling.server.metafields.MetaFieldBL
import com.sapienter.jbilling.server.metafields.db.EntityType
import com.sapienter.jbilling.client.metafield.MetaFieldUtils
import com.sapienter.jbilling.server.metafields.MetaFieldValueWS
import com.sapienter.jbilling.server.mediation.db.MediationMapDAS

/**
 *
 * @author vikas bodani
 * @since  20-Jan-2011
 *
 */

@Secured(["MENU_92"])
class OrderController {

    static pagination = [ max: 10, offset: 0, sort: 'id', order: 'desc' ]

    def webServicesSession
    def viewUtils
    def filterService
    def recentItemService
    def breadcrumbService
    def subAccountService

    def index = {
        list()
    }

    def getFilteredOrders(filters, params, ids) {
        params.max = params?.max?.toInteger() ?: pagination.max
        params.offset = params?.offset?.toInteger() ?: pagination.offset
        params.sort = params?.sort ?: pagination.sort
        params.order = params?.order ?: pagination.order

        return OrderDTO.createCriteria().list(
                max:    params.max,
                offset: params.offset
        ) {
            createAlias('baseUserByUserId', 'u', Criteria.LEFT_JOIN)
            and {
                filters.each { filter ->
                    if (filter.value) {
                        //handle orderStatus & orderPeriod separately
                        if (filter.constraintType == FilterConstraint.STATUS) {
                            if (filter.field == 'orderStatus') {
                                def statuses = new OrderStatusDAS().findAll()
                                eq("orderStatus", statuses.find{ it.id == filter.integerValue })
                            } else if (filter.field == 'orderPeriod') {
                                def periods = new OrderPeriodDAS().findAll()
                                eq("orderPeriod", periods.find{ it.id == filter.integerValue })
                            }
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
                
                eq('u.company', new CompanyDTO(session['company_id']))
                eq('deleted', 0)

                if (SpringSecurityUtils.ifNotGranted("ORDER_28")) {
                    if (SpringSecurityUtils.ifAnyGranted("ORDER_29")) {
                        // restrict query to sub-account user-ids
                        'in'('u.id', subAccountService.subAccountUserIds)
                    } else {
                        // limit list to only this customer
                        eq('u.id', session['user_id'])
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

    def list = {
        def filters = filterService.getFilters(FilterType.ORDER, params)

        def orderIds = parameterIds
        def orders = getFilteredOrders(filters, params, orderIds)

        def selected = params.id ? webServicesSession.getOrder(params.int("id")) : null
        def user = selected ? webServicesSession.getUserWS(selected.userId) : null

        breadcrumbService.addBreadcrumb(controllerName, 'list', null, selected?.id)

        if (params.applyFilter || params.partial) {
            render template: 'orders', model: [ orders: orders, order: selected, user: user, currencies: retrieveCurrencies(), filters: filters, ids: params.ids ]
        } else {
            render view: 'list', model: [ orders: orders, order: selected, user: user, currencies: retrieveCurrencies(), filters: filters, ids: params.ids ]
        }
    }

    @Secured(["ORDER_24"])
    def show = {
        OrderWS order = webServicesSession.getOrder(params.int('id'))
        UserWS user = webServicesSession.getUserWS(order.getUserId())

        breadcrumbService.addBreadcrumb(controllerName, 'list', null, order.id)
        recentItemService.addRecentItem(order.id, RecentItemType.ORDER)

        render template:'show', model: [order: order, user: user, currencies: retrieveCurrencies()]
    }

    /**
     * Applies the set filters to the order list, and exports it as a CSV for download.
     */
    @Secured(["ORDER_25"])
    def csv = {
        def filters = filterService.getFilters(FilterType.ORDER, params)

        params.max = CsvExporter.MAX_RESULTS

        def orderIds = parameterIds
        def orders = getFilteredOrders(filters, params, orderIds)

        if (orders.totalCount > CsvExporter.MAX_RESULTS) {
            flash.error = message(code: 'error.export.exceeds.maximum')
            flash.args = [ CsvExporter.MAX_RESULTS ]
            redirect action: 'list', id: params.id

        } else {
            DownloadHelper.setResponseHeader(response, "orders.csv")
            Exporter<OrderDTO> exporter = CsvExporter.createExporter(OrderDTO.class);
            render text: exporter.export(orders), contentType: "text/csv"
        }
    }

    /**
     * Convenience shortcut, this action shows all invoices for the given user id.
     */
    def user = {
        def filter = new Filter(type: FilterType.ORDER, constraintType: FilterConstraint.EQ, field: 'baseUserByUserId.id', template: 'id', visible: true, integerValue: params.int('id'))
        filterService.setFilter(FilterType.ORDER, filter)
        redirect action: 'list'
    }

    @Secured(["ORDER_23"])
    def generateInvoice = {
        log.debug "generateInvoice for order ${params.id}"

        def orderId = params.id?.toInteger()

        Integer invoiceID= null;
        try {
            invoiceID = webServicesSession.createInvoiceFromOrder(orderId, null)

        } catch (SessionInternalError e) {
            flash.error= 'order.error.generating.invoice'
            redirect action: 'list', params: [ id: params.id ]
            return
        }

        if ( null != invoiceID) {
            flash.message ='order.geninvoice.success'
            flash.args = [orderId]
            redirect controller: 'invoice', action: 'list', params: [id: invoiceID]

        } else {
            flash.error ='order.error.geninvoice.inactive'
            redirect action: 'list', params: [ id: params.id ]
        }
    }

    @Secured(["ORDER_23"])
    def applyToInvoice = {
        def invoices = getApplicableInvoices(params.int('userId'))

        if (!invoices || invoices.size() == 0) {
            flash.error = 'order.error.invoices.not.found'
            flash.args = [params.userId]
            redirect (action: 'list', params: [ id: params.id ])
        }

        session.applyToInvoiceOrderId = params.int('id')
        [ invoices:invoices, currencies: retrieveCurrencies(), orderId: params.id ]
    }

    @Secured(["ORDER_23"])
    def apply = {
        def order =  new OrderDAS().find(params.int('id'))
        if (!order.getStatusId().equals(Constants.ORDER_STATUS_ACTIVE)) {
            flash.error = 'order.error.status.not.active'
        }

        // invoice with meta fields
        def invoiceTemplate = new InvoiceWS()
        bindData(invoiceTemplate, params, 'invoice')

        def invoiceMetaFields = retrieveInvoiceMetaFields();
        def fieldsArray = MetaFieldUtils.bindMetaFields(invoiceMetaFields, params);
        invoiceTemplate.metaFields = fieldsArray.toArray(new MetaFieldValueWS[fieldsArray.size()])

        // apply invoice to order.
        try {
            def invoice = webServicesSession.applyOrderToInvoice(order.getId(), invoiceTemplate)
            if (!invoice) {
                flash.error = 'order.error.apply.invoice'
                render view: 'applyToInvoice', model: [ invoice: invoice, invoices: getApplicableInvoices(params.int('userId')), currencies:retrieveCurrencies(), availableMetaFields: invoiceMetaFields, fieldsArray: fieldsArray ]
                return
            }

            flash.message = 'order.succcessfully.applied.to.invoice'
            flash.args = [params.id, invoice]

        } catch (SessionInternalError e) {
            viewUtils.resolveException(flash, session.locale, e);

            def invoice = webServicesSession.getInvoiceWS(params.int('invoice.id'))
            def invoices = getApplicableInvoices(params.int('userId'))
            render view: 'applyToInvoice', model: [ invoice: invoice, invoices: invoices, currencies:retrieveCurrencies(), availableMetaFields: invoiceMetaFields, fieldsArray: fieldsArray ]
            return
        }

        redirect action: 'list', params: [ id: params.id ]
    }

    def getApplicableInvoices(Integer userId) {

        CustomerDTO payingUser
        Integer _userId
        UserDTO user= new UserDAS().find(userId)
        if (user.getCustomer()?.getParent()) {
            payingUser= new CustomerBL(user.getCustomer().getId()).getInvoicableParent()
            _userId=payingUser.getBaseUser().getId()
        } else {
            _userId= user.getId()
        }
        InvoiceDAS das= new InvoiceDAS()
        List invoices =  new ArrayList()
        for (Iterator it= das.findAllApplicableInvoicesByUser(_userId ).iterator(); it.hasNext();) {
            invoices.add InvoiceBL.getWS(das.find (it.next()))
        }

        log.debug "Found ${invoices.size()} for user ${_userId}"

        invoices as List
    }


    def retrieveInvoiceMetaFields() {
        return MetaFieldBL.getAvailableFieldsList(session["company_id"], EntityType.INVOICE);
    }

    def retrieveCurrencies() {
		//in this controller we need only currencies objects with inUse=true without checking rates on date
        return new CurrencyBL().getCurrenciesWithoutRates(session['language_id'].toInteger(), session['company_id'].toInteger(),true)
    }

    def byProcess = {
        OrderBL bl= new OrderBL();
        List<Integer> orderIds= bl.getOrdersByProcess(params?.processId?.toInteger())

        log.debug "Expecting ${orderIds.size()} orders."
        def orders
        def filters=filterService.getFilters(FilterType.ORDER, params)

        if (orderIds.isEmpty()) {
            orders = new ArrayList<OrderDTO>()
        } else {
            orders = getFilteredOrders(filters, params, orderIds)
        }
        log.debug("Found ${orders.size()} orders.")
        render view: 'list', model: [orders:orders, filters:filters, ids: orderIds]
    }

    def byMediation = {

        def orderIds = parameterIds

        log.debug "Expecting ${orderIds.size()} orders."
        def orders
        def filters=filterService.getFilters(FilterType.ORDER, params)

        if (orderIds.isEmpty()) {
            orders = new ArrayList<OrderDTO>()
        } else {
            orders = getFilteredOrders(filters, params, orderIds)
        }
        log.debug("Found ${orders.size()} orders.")

        render view: 'list', model: [orders:orders, filters:filters, ids: orderIds]
    }

    @Secured(["ORDER_22"])
    def deleteOrder = {
        try {
            webServicesSession.deleteOrder(params.int('id'))
            flash.message = 'order.delete.success'
            flash.args = [params.id, params.id]
        } catch (SessionInternalError e){
            flash.error ='order.error.delete'
            viewUtils.resolveException(flash, session.locale, e);
        } catch (Exception e) {
            log.error e
            flash.error= e.getMessage()
        }
        redirect action: 'list'
    }
    
    def retrieveAvailableMetaFields() {
        return MetaFieldBL.getAvailableFieldsList(session["company_id"], EntityType.ORDER);
    }

    def findMetaFieldType(Integer metaFieldId) {
        for (MetaField field : retrieveAvailableMetaFields()) {
            if (field.id == metaFieldId) {
                return field;
            }
        }
        return null;
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

}
