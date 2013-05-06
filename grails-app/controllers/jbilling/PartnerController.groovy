/*
 jBilling - The Enterprise Open Source Billing System
 Copyright (C) 2003-2011 Enterprise jBilling Software Ltd. and Emiliano Conde

 This file is part of jbilling.

 jbilling is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 jbilling is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with jbilling.  If not, see <http://www.gnu.org/licenses/>.
 */

package jbilling

import grails.plugins.springsecurity.Secured

import org.springframework.security.authentication.encoding.PasswordEncoder

import com.sapienter.jbilling.server.item.CurrencyBL
import com.sapienter.jbilling.client.ViewUtils
import com.sapienter.jbilling.client.user.UserHelper
import com.sapienter.jbilling.client.util.SortableCriteria
import com.sapienter.jbilling.common.SessionInternalError
import com.sapienter.jbilling.server.user.UserWS
import com.sapienter.jbilling.server.user.contact.db.ContactDTO
import com.sapienter.jbilling.server.user.db.CompanyDTO
import com.sapienter.jbilling.server.user.partner.PartnerWS
import com.sapienter.jbilling.server.user.partner.db.Partner
import com.sapienter.jbilling.server.user.db.UserDTO
import com.sapienter.jbilling.server.user.db.UserStatusDTO
import com.sapienter.jbilling.server.util.Constants
import com.sapienter.jbilling.server.util.IWebServicesSessionBean
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import com.sapienter.jbilling.server.metafields.MetaFieldBL
import com.sapienter.jbilling.server.metafields.db.EntityType

@Secured(["MENU_100"])
class PartnerController {

    static pagination = [ max: 10, offset: 0, sort: 'id', order: 'desc' ]

    IWebServicesSessionBean webServicesSession
    ViewUtils viewUtils
    PasswordEncoder passwordEncoder

    def filterService
    def recentItemService
    def breadcrumbService
    def springSecurityService

    def index = {
        list()
    }

    def getList(filters, params) {
        params.max = params?.max?.toInteger() ?: pagination.max
        params.offset = params?.offset?.toInteger() ?: pagination.offset
        params.sort = params?.sort ?: pagination.sort
        params.order = params?.order ?: pagination.order

        def statuses = UserStatusDTO.findAll()
        return Partner.createCriteria().list(
            max:    params.max,
            offset: params.offset
        ) {
            and {
                createAlias("baseUser.contact", "contact")

                filters.each { filter ->
                    if (filter.value) {
                        if (filter.constraintType == FilterConstraint.STATUS) {
                            baseUser {
                                eq("userStatus", statuses.find{ it.id == filter.integerValue })
                            }
                        } else {
                            addToCriteria(filter.getRestrictions());
                        }
                    }
                }
                baseUser {
                    roles {
                        eq('roleTypeId', Constants.TYPE_PARTNER)
                    }
                    eq('deleted', 0)
                    eq('company', retrieveCompany())
                }
            }
            // apply sorting
            SortableCriteria.sort(params, delegate)
        }
    }

    def list = {
        def filters = filterService.getFilters(FilterType.PARTNER, params)
        def partners = getList(filters, params)

        def selected = params.id ? Partner.get(params.int("id")) : null
        def contact = selected ? ContactDTO.findByUserId(selected?.baseUser?.id) : null

        breadcrumbService.addBreadcrumb(controllerName, 'list', null, null)
        
        // if id is present and object not found, give an error message to the user along with the list
        if (params.id?.isInteger() && selected == null) {
			flash.error = 'partner.not.found'
            flash.args = [params.id]
        }
        
        if (params.applyFilter || params.partial) {
            render template: 'partners', model: [ partners: partners, selected: selected, contact: contact, filters:filters ]
            return 
        } 
        render view: 'list', model: [ partners: partners, selected: selected, contact: contact, filters:filters]
    }

    @Secured(["PARTNER_104"])
    def show = {
        def partner = Partner.get(params.int('id'))
        def contact = partner ? ContactDTO.findByUserId(partner?.baseUser.id) : null

        breadcrumbService.addBreadcrumb(controllerName, 'list', null, partner.id, UserHelper.getDisplayName(partner.baseUser, contact))

        render template: 'show', model: [ selected: partner, contact: contact ]
    }

    def payouts = {
        def partner = Partner.get(params.int('id'))
        [ partner : partner ]
    }

    @Secured(["hasAnyRole('PARTNER_101', 'PARTNER_102')"])
    def edit = {
        def user
        def partner
        def contacts

        try {
            
            partner= params.id ? webServicesSession.getPartner(params.int('id')) : new PartnerWS()
            log.debug partner?.nextPayoutDate
            user= (params.id &&  partner) ? webServicesSession.getUserWS(partner?.userId) : new UserWS()
            contacts = params.id ? webServicesSession.getUserContactsWS(user.userId) : null
            
            breadcrumbService.addBreadcrumb(controllerName, 'edit', null, partner.id, 
            	UserHelper.getDisplayName(user, contacts && contacts.length > 0 ? contacts[0] : null))
            
        } catch (SessionInternalError e) {
            log.error("Could not fetch WS object", e)
            redirect action: 'list', params:params
            return
        }

        [ partner: partner, user: user, contacts: contacts, company: retrieveCompany(), currencies: retrieveCurrencies(), clerks: retrieveClerks(), availableFields: retrieveAvailableMetaFields() ]
    }

    /**
     * Validate and Save the Partner User
     */
    @Secured(["hasAnyRole('PARTNER_101', 'PARTNER_102')"])
    def save = {
        def partner = new PartnerWS()
        def user = new UserWS()

        bindData(partner, params)
        UserHelper.bindUser(user, params)

        def availableMetaFields = retrieveAvailableMetaFields()
        UserHelper.bindMetaFields(user, availableMetaFields, params)

        log.debug("bound fields: ${user.getMetaFields()}")

        def contacts = []
        UserHelper.bindContacts(user, contacts, retrieveCompany(), params)

        def oldUser = (user.userId && user.userId != 0) ? webServicesSession.getUserWS(user.userId) : null
        UserHelper.bindPassword(user, oldUser, params, flash)

        if (flash.error) {
            render view: 'edit', model: [ partner: partner, user: user, contacts: contacts, company: retrieveCompany(), currencies: retrieveCurrencies(), clerks:retrieveClerks(), availableFields: availableMetaFields ]
            return
        }

        try {
            // save or update
            if (!oldUser) {
                if (SpringSecurityUtils.ifAllGranted("PARTNER_101")) {
                    log.debug("creating partner ${user}")

                    partner.id = webServicesSession.createPartner(user, partner)

                    flash.message = 'partner.created'
                    flash.args = [partner.id]

                } else {
                    render view: '/login/denied'
                    return
                }

            } else {
                if (SpringSecurityUtils.ifAllGranted("PARTNER_102")) {
                    log.debug("saving changes to partner ${user.userId} & ${user.customerId}")

                    partner.setUserId(user.getUserId())
                    webServicesSession.updatePartner(user, partner)

                    flash.message = 'partner.updated'
                    flash.args = [partner.id]

                } else {
                    render view: '/login/denied'
                    return
                }
            }

            // save secondary contacts
            if (user.userId) {
                contacts.each {
                    webServicesSession.updateUserContact(user.userId, it.type, it);
                }
            }
            
        } catch (SessionInternalError e) {
            viewUtils.resolveException(flash, session.locale, e)
            render view: 'edit', model: [ partner: partner, user: user, contacts: contacts, company: retrieveCompany(), currencies: retrieveCurrencies(), clerks: retrieveClerks(), availableFields: availableMetaFields ]
            return
        }

        chain action: 'list', params: [ id: partner.id ]
    }

    @Secured(["PARTNER_103"])
    def delete = {
        if (params.id) {
            webServicesSession.deletePartner(params.int('id'))
            log.debug("Deleted partner ${params.id}.")
        }

        flash.message = 'partner.deleted'
        flash.args = [params.id]

        // render the partial user list
        params.applyFilter = true
        params.id = null
        list()
    }

    def retrieveClerks() {
        return UserDTO.createCriteria().list() {
            and {
                or {
                    isEmpty('roles')
                    roles {
                        ne('roleTypeId', Constants.TYPE_CUSTOMER)
                        ne('roleTypeId', Constants.TYPE_PARTNER)
                    }
                }
                eq('company', retrieveCompany())
                eq('deleted', 0)
            }
            order('id', 'desc')
        }
    }
    
    def retrieveCurrencies() {
        return new CurrencyBL().getCurrenciesWithoutRates(session['language_id'].toInteger(), session['company_id'].toInteger(),true)
    }
    
    def retrieveCompany() {
        CompanyDTO.get(session['company_id'])
    }

    def retrieveAvailableMetaFields() {
        return MetaFieldBL.getAvailableFieldsList(session["company_id"], EntityType.CUSTOMER);
    }
}
