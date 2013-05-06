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

import com.sapienter.jbilling.server.user.contact.db.ContactDTO
import com.sapienter.jbilling.server.user.db.UserDTO
import com.sapienter.jbilling.server.util.db.CurrencyDTO
import com.sapienter.jbilling.server.util.db.LanguageDTO
import com.sapienter.jbilling.server.user.db.CompanyDTO
import com.sapienter.jbilling.server.util.Constants
import com.sapienter.jbilling.server.user.db.UserStatusDAS
import com.sapienter.jbilling.server.user.UserDTOEx
import com.sapienter.jbilling.server.user.db.SubscriberStatusDAS
import com.sapienter.jbilling.server.user.permisson.db.RoleDTO
import com.sapienter.jbilling.server.user.permisson.db.RoleDAS
import com.sapienter.jbilling.server.user.RoleBL
import com.sapienter.jbilling.server.user.contact.db.ContactTypeDTO
import com.sapienter.jbilling.server.user.contact.db.ContactMapDTO
import com.sapienter.jbilling.server.util.db.JbillingTable
import com.sapienter.jbilling.client.EntityDefaults
import javax.validation.constraints.NotNull
import com.sapienter.jbilling.common.SessionInternalError
import com.sapienter.jbilling.server.util.api.validation.EntitySignupValidationGroup
import com.sapienter.jbilling.server.user.ContactWS
import com.sapienter.jbilling.server.user.UserWS
import javax.validation.groups.Default
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

/**
 * SignupController 
 *
 * @author Brian Cowdery
 * @since 10/03/11
 */
class SignupController {

    def webServicesValidationAdvice
    def messageSource
    def passwordEncoder
    def viewUtils
    def springSecurityService
    def securityContextLogoutHandler

    def index = {
    }

    def save = {
        // validate required fields
        try {
            def contact = new ContactWS()
            bindData(contact, params, 'contact')

            def user = new UserWS()
            bindData(user, params, 'user')

            user.contact = contact

            webServicesValidationAdvice.validateObject(user, Default.class, EntitySignupValidationGroup.class)

        } catch (SessionInternalError e) {
            viewUtils.resolveException(flash, session?.locale ?: new Locale("en"), e)
            render view: 'index'
            return
        }

        if (params['user.password'] != params.verifiedPassword) {
            flash.error = 'passwords.dont.match'
            render view: 'index'
            return
        }

        if(CompanyDTO.findByDescription(params['contact.organizationName'])) {
            // show a error message and return
            flash.error = 'company.already.exists'
            flash.args = [params.contact.organizationName]
            render view: 'index'
            return
        }

        /*
            Create the new entity, root user and basic contact information
         */

        // create company
        def language = LanguageDTO.get(params.languageId)
        def currency = CurrencyDTO.get(params.currencyId)
        def company = createCompany(language, currency)
        def companyContact = createCompanyContact(company)

        // create root user and contact information
        def user = createUser(language, currency, company)
        def primaryContactType = createPrimaryContactType(language, company)
        def userContact = createUserContact(user, primaryContactType)

        // set all entity defaults
        new EntityDefaults(company, user, language, messageSource).init()

        if (springSecurityService.isLoggedIn()) {
            // if logged in, delete the remember me cookie and log the user out
            // the user should always be shown the login page after signup
            response.deleteCookie(SpringSecurityUtils.securityConfig.rememberMe.cookieName)
            redirect uri: SpringSecurityUtils.securityConfig.logout.filterProcessesUrl

        } else {
            flash.message = 'signup.successful'
            flash.args = [ companyContact.organizationName, user.userName ]
            redirect controller: 'login', action: 'auth', params: [ userName: user.userName, companyId: company.id ]
        }
    }

    /**
     * Create a new company for the given language and currency.
     *
     * @param language
     * @param currency
     * @return created company
     */
    def createCompany(language, currency) {
        def company = new CompanyDTO(
                description: params['contact.organizationName'],
                createDatetime: new Date(),
                language: language,
                currency: currency,
                deleted: 0
        ).save()

        return company
    }

    /**
     * Create new root user for the given company, currency, and language.
     *
     * @param language
     * @param currency
     * @param company
     * @return created root user
     */
    def createUser(language, currency, company) {
        def user = new UserDTO()
        bindData(user, params, 'user')
        user.password = passwordEncoder.encodePassword(params['user.password'], null)
        user.deleted = 0
        user.userStatus = new UserStatusDAS().find(UserDTOEx.STATUS_ACTIVE)
        user.subscriberStatus = new SubscriberStatusDAS().find(UserDTOEx.SUBSCRIBER_ACTIVE)
        user.language = language
        user.currency = currency
        user.company = company
        user.createDatetime = new Date()

		createDefaultRoles(language, company)

		// get root role		
        def rootRole = new RoleDAS().findByRoleTypeIdAndCompanyId(
			Constants.TYPE_ROOT, company.id)

        user.roles.add(rootRole);
        user.save()

        return user
    }
	
	/**
	 * 	Creates default roles taken from another company
	 * 
	 * @param language
	 * @param currency
	 * @param company
	 * @return
	 */
	def createDefaultRoles(language, company) {

		def defaultRoleList = [ Constants.TYPE_ROOT, Constants.TYPE_CLERK, Constants.TYPE_CUSTOMER, Constants.TYPE_PARTNER ];

		def roleService = new RoleBL();

		defaultRoleList.each() {

			def role = new RoleDAS().findByRoleTypeIdAndCompanyId(
					it, null)

			// check the initial role ( companyId = null )
			if (!role) {
				// if not initial role set use the latest company role settings available
				def defaultCompanyId = CompanyDTO.createCriteria().get {
					projections {
						min("id")
					}
				}
				role = new RoleDAS().findByRoleTypeIdAndCompanyId(
						it, defaultCompanyId)
			}
			
			if (!role) {
				return;
			}

			def newRole = new RoleDTO();
			newRole.permissions.addAll(role.permissions);
			newRole.company = company;
			newRole.roleTypeId = it

			roleService.create(newRole)
            roleService.setDescription(language.id, role.getDescription(language.id)?:role.getDescription())
            roleService.setTitle(language.id, role.getTitle(language.id)?:role.getTitle(1))

		}
	}
	

    /**
     * Create the companies primary contact type.
     *
     * @param language
     * @param company
     * @return created primary contact type
     */
    def createPrimaryContactType(language, company) {
        def primaryContactType = new ContactTypeDTO(
                entity: company,
                isPrimary: 1,
        ).save()

        primaryContactType.setDescription("Primary", language.id)

        return primaryContactType
    }

    /**
     * Create a new primary contact for the given user.
     *
     * @param user
     * @param primaryContactType primary contact type
     * @return created user contact
     */
    def createUserContact(user, primaryContactType) {
        def userContact = new ContactDTO()
        bindData(userContact, params, 'contact')
        userContact.deleted = 0
        userContact.createDate = new Date()
        userContact.userId = user.id
        userContact.save()

        // map contact to the user table
        // map contact to the primary contact type
        new ContactMapDTO(
                jbillingTable: JbillingTable.findByName(Constants.TABLE_BASE_USER),
                contactType: primaryContactType,
                contact: userContact,
                foreignId: user.id
        ).save()

        return userContact
    }

    /**
     * Create a new contact for the company.
     *
     * @param company
     * @return created company contact
     */
    def createCompanyContact(company) {
        def entityContact = new ContactDTO()
        bindData(entityContact, params, 'contact')
        entityContact.deleted = 0
        entityContact.createDate = new Date()
        entityContact.save()

        // map contact to the entity table
        // map contact to the base entity contact type
        new ContactMapDTO(
                jbillingTable: JbillingTable.findByName(Constants.TABLE_ENTITY),
                contactType: ContactTypeDTO.get(Constants.ENTITY_CONTACT_TYPE),
                contact: entityContact,
                foreignId: company.id
        ).save()

        return entityContact
    }
}
