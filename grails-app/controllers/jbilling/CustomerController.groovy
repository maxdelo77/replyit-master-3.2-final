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

import com.sapienter.jbilling.client.ViewUtils
import com.sapienter.jbilling.client.user.UserHelper
import com.sapienter.jbilling.client.util.DownloadHelper
import com.sapienter.jbilling.client.util.SortableCriteria
import com.sapienter.jbilling.common.SessionInternalError
import com.sapienter.jbilling.server.item.CurrencyBL
import com.sapienter.jbilling.server.metafields.MetaFieldBL
import com.sapienter.jbilling.server.metafields.db.DataType
import com.sapienter.jbilling.server.metafields.db.EntityType
import com.sapienter.jbilling.server.metafields.db.MetaField
import com.sapienter.jbilling.server.metafields.db.value.StringMetaFieldValue
import com.sapienter.jbilling.server.process.db.PeriodUnitDTO
import com.sapienter.jbilling.server.user.UserWS
import com.sapienter.jbilling.server.user.contact.db.ContactDAS
import com.sapienter.jbilling.server.user.contact.db.ContactDTO
import com.sapienter.jbilling.server.user.db.CompanyDTO
import com.sapienter.jbilling.server.user.db.UserDTO
import com.sapienter.jbilling.server.user.db.UserStatusDAS
import com.sapienter.jbilling.server.util.IWebServicesSessionBean
import com.sapienter.jbilling.server.util.csv.CsvExporter
import com.sapienter.jbilling.server.util.csv.Exporter
import grails.plugins.springsecurity.Secured
import org.apache.commons.lang.StringUtils
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.hibernate.FetchMode
import org.hibernate.criterion.MatchMode
import org.hibernate.criterion.Restrictions
import org.hibernate.criterion.DetachedCriteria
import org.hibernate.criterion.Property
import org.hibernate.criterion.Projections
import org.springframework.security.authentication.encoding.PasswordEncoder
import org.hibernate.criterion.Criterion
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import com.sapienter.jbilling.server.user.ContactWS
import com.sapienter.jbilling.server.user.contact.db.ContactDAS

import com.sapienter.jbilling.server.process.db.PeriodUnitDTO
import org.apache.commons.lang.StringUtils
import com.sapienter.jbilling.server.order.db.OrderPeriodDTO
import org.hibernate.criterion.LogicalExpression

@Secured(["MENU_90"])
class CustomerController {

    static pagination = [max: 10, offset: 0, sort: 'id', order: 'desc']

    IWebServicesSessionBean webServicesSession
    ViewUtils viewUtils
    PasswordEncoder passwordEncoder

    def filterService
    def recentItemService
    def breadcrumbService
    def springSecurityService
    def subAccountService

    @Secured(["hasAnyRole('MENU_90', 'CUSTOMER_15')"])
    def index = {
        list()
    }

    def getList(filters, statuses, params) {
        params.max = params?.max?.toInteger() ?: pagination.max
        params.offset = params?.offset?.toInteger() ?: pagination.offset
        params.sort = params?.sort ?: pagination.sort
        params.order = params?.order ?: pagination.order

        return UserDTO.createCriteria().list(
                max:    params.max,
                offset: params.offset
        ) {
            createAlias("contact", "contact")
            createAlias("customer", "customer")
            and {
                filters.each { filter ->
                    if (filter.value) {
                        // handle user status separately from the other constraints
                        // we need to find the UserStatusDTO to compare to
                        if (filter.constraintType == FilterConstraint.STATUS) {
                            eq("userStatus", statuses.find{ it.id == filter.integerValue })

                        } else if (filter.field == 'contact.fields') {
                            String typeId = params['contactFieldTypes']
                            String ccfValue= filter.stringValue
                            log.debug "Contact Field Type ID: ${typeId}, CCF Value: ${ccfValue}"
                            if (typeId && ccfValue) {
                                MetaField type = findMetaFieldType(typeId.toInteger());
                                if (type != null) {
                                    createAlias("customer.metaFields", "fieldValue")
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
                        } else if (filter.field == 'contact.name') {
                            Criterion USER_NAME = Restrictions.ilike("userName",  filter.stringValue, MatchMode.ANYWHERE);
                            Criterion ORGANIZATION_NAME = Restrictions.ilike("contact.organizationName", filter.stringValue, MatchMode.ANYWHERE);
                            Criterion FIRST_NAME = Restrictions.ilike("contact.firstName", filter.stringValue, MatchMode.ANYWHERE);
                            Criterion LAST_NAME = Restrictions.ilike("contact.lastName", filter.stringValue, MatchMode.ANYWHERE);

                            LogicalExpression userNameOrOrganizationName= Restrictions.or(USER_NAME, ORGANIZATION_NAME);
                            LogicalExpression firstNameOrLastName= Restrictions.or(FIRST_NAME, LAST_NAME);
                            LogicalExpression name= Restrictions.or(userNameOrOrganizationName, firstNameOrLastName);

                            addToCriteria(name)
                        } else {
                            addToCriteria(filter.getRestrictions());
                        }
                    }
                }
                //check that the user is a customer
                isNotNull('customer')
                eq('company', retrieveCompany())
                eq('deleted', 0)

                if (SpringSecurityUtils.ifNotGranted("CUSTOMER_17")) {
                    if (SpringSecurityUtils.ifAnyGranted("CUSTOMER_18")) {
                        // restrict query to sub-account user-ids
                        'in'('id', subAccountService.getSubAccountUserIds())
                    } else {
                        // limit list to only this customer
                        eq('id', session['user_id'])
                    }
                }
            }

            // apply sorting
            SortableCriteria.sort(params, delegate)
        }
    }

    /**
     * Get a list of users and render the list page. If the "applyFilters" parameter is given, the
     * partial "_users.gsp" template will be rendered instead of the complete user list.
     */
    @Secured(["hasAnyRole('MENU_90', 'CUSTOMER_15')"])
    def list = {
        def filters = filterService.getFilters(FilterType.CUSTOMER, params)
        def statuses = new UserStatusDAS().findAll()
        def users = []

        // if logged in as a customer, you can only view yourself
        if (SpringSecurityUtils.ifNotGranted("MENU_90")) {
            users << UserDTO.get(springSecurityService.principal.id)
        } else {
            users = getList(filters, statuses, params)
        }

        def selected = params.id ? UserDTO.get(params.int("id")) : null
        def contact = null
        def revenue = null
        def latestOrder = null
        def latestPayment = null
        def latestInvoice = null

        if (selected) {
            contact = new ContactDAS().findPrimaryContact(selected.userId)
            revenue = webServicesSession.getTotalRevenueByUser(selected.userId)
            latestOrder = webServicesSession.getLatestOrder(selected.userId)
            latestPayment = webServicesSession.getLatestPayment(selected.userId)
            latestInvoice = webServicesSession.getLatestInvoice(selected.userId)
        }

        def crumbDescription = selected ? UserHelper.getDisplayName(selected, contact) : null
        breadcrumbService.addBreadcrumb(controllerName, 'list', null, selected?.id, crumbDescription)
        
        def contactFieldTypes = params['contactFieldTypes']

        if (params.applyFilter || params.partial) {
            render template: 'customers', model: [  selected: selected, contact: contact, revenue: revenue, latestOrder: latestOrder, latestPayment: latestPayment, latestInvoice: latestInvoice,
                                                    users: users, statuses: statuses, filters: filters, contactFieldTypes: contactFieldTypes ]
        } else {
            render view: 'list', model: [ selected: selected, contact: contact, revenue: revenue, latestOrder: latestOrder, latestPayment: latestPayment, latestInvoice: latestInvoice,
                                          users: users, statuses: statuses, filters: filters ]
        }
    }

    /**
     * Applies the set filters to the user list, and exports it as a CSV for download.
     */
    @Secured(["CUSTOMER_16"])
    def csv = {
        def filters = filterService.getFilters(FilterType.CUSTOMER, params)
        def statuses = new UserStatusDAS().findAll()

        params.max = CsvExporter.MAX_RESULTS
        def users = getList(filters, statuses, params)

        if (users.totalCount > CsvExporter.MAX_RESULTS) {
            flash.error = message(code: 'error.export.exceeds.maximum')
            flash.args = [ CsvExporter.MAX_RESULTS ]
            redirect action: 'list'

        } else {
            DownloadHelper.setResponseHeader(response, "users.csv")
            Exporter<UserDTO> exporter = CsvExporter.createExporter(UserDTO.class);
            render text: exporter.export(users), contentType: "text/csv"
        }
    }

    /**
     * Show details of the selected user. By default, this action renders the "_show.gsp" template.
     * When rendering for an AJAX request the template defined by the "template" parameter will be rendered.
     */
    @Secured(["CUSTOMER_15"])
    def show = {
        def user = UserDTO.get(params.int('id'))
        if (!user) {
            log.debug "redirecting to list"
            redirect(action: 'list')
            return
        }
        def contact = new ContactDAS().findPrimaryContact(user.userId)

        def revenue = webServicesSession.getTotalRevenueByUser(user.userId)
        def latestOrder = webServicesSession.getLatestOrder(user.userId)
        def latestPayment = webServicesSession.getLatestPayment(user.userId)
        def latestInvoice = webServicesSession.getLatestInvoice(user.userId)

        recentItemService.addRecentItem(user.userId, RecentItemType.CUSTOMER)
        breadcrumbService.addBreadcrumb(controllerName, 'list', params.template ?: null, user.userId, UserHelper.getDisplayName(user, contact))

        render template: params.template ?: 'show', model: [ selected: user, contact: contact, revenue: revenue, latestOrder: latestOrder, latestPayment: latestPayment, latestInvoice: latestInvoice ]
    }

    /**
     * Fetches a list of sub-accounts for the given user id and renders the user list "_table.gsp" template.
     */
    def subaccounts = {
        params.max = params?.max?.toInteger() ?: pagination.max
        params.offset = params?.offset?.toInteger() ?: pagination.offset

        def children = UserDTO.createCriteria().list(
                max:    params.max,
                offset: params.offset
        ) {
            and{
                customer {
                    parent {
                        eq('baseUser.id', params.int('id'))
                        order("id","desc")
                    }
                }
                eq('deleted', 0)
            }
        }

        def parent = UserDTO.get(params.int('id'))
        render template: 'customers', model: [ users: children, parent: parent ]
    }

    /**
     * Shows all customers of the given partner id
     */
    def partner = {
        def filter = new Filter(type: FilterType.CUSTOMER, constraintType: FilterConstraint.EQ, field: 'customer.partner.id', template: 'id', visible: true, integerValue: params.id)
        filterService.setFilter(FilterType.CUSTOMER, filter)

        redirect action: 'list'
    }

    /**
     * Updates the notes for the given user id.
     */
    @Secured(["CUSTOMER_11"])
    def saveNotes = {
        if (params.id) {
            try {
                webServicesSession.saveCustomerNotes(params.int('id'), params.notes)

                log.debug("Updating notes for user ${params.id}.")

                flash.message = 'customer.notes'
                flash.args = [params.id as String]
            } catch (SessionInternalError e) {
                log.error("Could not save the customer's notes", e)

                viewUtils.resolveException(flash, session.locale, e)
            }
        }

        // render user list with selected id
        redirect action: "list", id: params.id
    }

    /**
     * Delete the given user id.
     */
    @Secured(["CUSTOMER_12"])
    def delete = {
        if (params.id) {
            webServicesSession.deleteUser(params.int('id'))

            flash.message = 'customer.deleted'
            flash.args = [ params.id ]
            log.debug("Deleted user ${params.id}.")

            // remove the id from the list in session.
            subAccountService.removeSubAccountUserId(params.int('id'))
        }

        // render the partial user list
        params.partial = true
        list()
    }

    /**
     * Get the user to be edited and show the "edit.gsp" view. If no ID is given this view
     * will allow creation of a new user.
     */
    @Secured(["hasAnyRole('CUSTOMER_10', 'CUSTOMER_11')"])
    def edit = {
        def user
        def contacts
        def parent

        try {
            user = params.id ? webServicesSession.getUserWS(params.int('id')) : null
            
            if (params.id?.isInteger() && user?.deleted==1) {
				log.error("Customer not found or deleted, redirect to list.")
				customerNotFoundErrorRedirect(params.id)
            	return
            }
            
            contacts = user ? webServicesSession.getUserContactsWS(user.userId) : null
            parent = params.parentId ? webServicesSession.getUserWS(params.int('parentId')) : null

        } catch (SessionInternalError e) {
            log.error("Could not fetch WS object", e)
			customerNotFoundErrorRedirect(params.id)
            return
        }

        def crumbName = params.id ? 'update' : 'create'
        def crumbDescription = params.id ? UserHelper.getDisplayName(user, user.contact) : null
        breadcrumbService.addBreadcrumb(controllerName, actionName, crumbName, params.int('id'), crumbDescription)

        def periodUnits = PeriodUnitDTO.list()
		def orderPeriods = OrderPeriodDTO.createCriteria().list() { eq('company', retrieveCompany()) }
        [ user: user, contacts: contacts, parent: parent, company: retrieveCompany(), currencies: retrieveCurrencies(), periodUnits: periodUnits, orderPeriods: orderPeriods, availableFields: retrieveAvailableMetaFields() ]
    }

	private void customerNotFoundErrorRedirect(customerId) {
		flash.error = 'customer.not.found'
		flash.args = [ customerId as String ]
		redirect controller: 'customer', action: 'list'
	}

    /**
     * Validate and save a user.
     */
    @Secured(["hasAnyRole('CUSTOMER_10', 'CUSTOMER_11')"])
    def save = {
        def user = new UserWS()

        UserHelper.bindUser(user, params)

        UserHelper.bindMetaFields(user, retrieveAvailableMetaFields(), params)

        def contacts = []
        UserHelper.bindContacts(user, contacts, retrieveCompany(), params)

        def oldUser = (user.userId && user.userId != 0) ? webServicesSession.getUserWS(user.userId) : null
        UserHelper.bindPassword(user, oldUser, params, flash)
		
		def periodUnits = PeriodUnitDTO.list()
		def orderPeriods = OrderPeriodDTO.createCriteria().list(){eq('company', new CompanyDTO(session['company_id']))}

        if (flash.error) {
            render view: 'edit', model: [ user: user, contacts: contacts, company: retrieveCompany(), availableFields: retrieveAvailableMetaFields(), periodUnits: periodUnits, orderPeriods: orderPeriods, currencies: retrieveCurrencies() ]
            return
        }

        try {
            // save or update
            if (!oldUser) {
                if (SpringSecurityUtils.ifAllGranted("CUSTOMER_10")) {
                    if (user.userName.trim()) {
                        user.userId = webServicesSession.createUser(user)

                        flash.message = 'customer.created'
                        flash.args = [user.userId as String]
                                                
                        // add the id to the list in session.
                        subAccountService.addSubAccountUserId(user)
                        
                    } else {
                        user.userName = ''
                        flash.error = message(code: 'customer.error.name.blank')

                        render view: "edit", model: [user: user, contacts: contacts, parent: null, company: retrieveCompany(), currencies: retrieveCurrencies(), periodUnits: periodUnits, orderPeriods: orderPeriods, availableFields: retrieveAvailableMetaFields()]
                        return
                    }
                } else {
                    render view: '/login/denied'
                    return
                }

            } else {
                if (SpringSecurityUtils.ifAllGranted("CUSTOMER_11")) {

                    webServicesSession.updateUser(user)

                    // ACH updates are not handled through updateUser. Make a separate API call
                    // to update the customers ACH data if it's present
                    if (user.ach) {
                        webServicesSession.updateAch(user.userId, user.ach)
                    }

                    // payment data deletions
                    if (params.deleteAch) {
                        log.debug("deleting ACH for user ${user.userId}")
                        webServicesSession.deleteAch(user.userId)
                    }

                    if (params.deleteCreditCard) {
                        log.debug("deleting Credit Card for user ${user.userId}")
                        webServicesSession.deleteCreditCard(user.userId)
                    }

                    flash.message = 'customer.updated'
                    flash.args = [user.userId as String]

                } else {
                    render view: '/login/denied'
                    return
                }
            }

            // save contacts
            if (user.userId) {
                contacts.each {
                    webServicesSession.updateUserContact(user.userId, it.type, it);
                }
            }

        } catch (SessionInternalError e) {
            flash.clear()
            viewUtils.resolveException(flash, session.locale, e)
            render view: 'edit', model: [ user: user, contacts: contacts, company: retrieveCompany(), currencies: retrieveCurrencies(), availableFields: retrieveAvailableMetaFields() ,periodUnits: periodUnits, orderPeriods: orderPeriods]
            return
        }

        chain action: 'list', params: [id: user.userId]
    }

    def retrieveCurrencies() {
        def currencies = new CurrencyBL().getCurrencies(session['language_id'].toInteger(), session['company_id'].toInteger())
        return currencies.findAll { it.inUse }
    }

    def retrieveCompany() {
        CompanyDTO.get(session['company_id'])
    }

    def retrieveAvailableMetaFields() {
        return MetaFieldBL.getAvailableFieldsList(session["company_id"], EntityType.CUSTOMER);
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
