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
import com.sapienter.jbilling.server.user.permisson.db.RoleDTO
import com.sapienter.jbilling.server.user.db.CompanyDTO
import com.sapienter.jbilling.server.user.permisson.db.PermissionTypeDTO
import com.sapienter.jbilling.server.user.RoleBL
import com.sapienter.jbilling.server.user.permisson.db.PermissionDTO
import com.sapienter.jbilling.server.util.Constants
import grails.plugins.springsecurity.Secured
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

/**
 * RoleController 
 *
 * @author Brian Cowdery
 * @since 02/06/11
 */
@Secured(["MENU_99"])
class RoleController {

    def breadcrumbService
    def viewUtils

    def index = {
        redirect action: list, params: params
    }

    def getList(params) {

		def types = RoleDTO.createCriteria().list() {
			eq('company', new CompanyDTO(session['company_id']))
			order('roleTypeId', 'asc')
		}
    }

    def list = {
        def roles = getList(params)
        def selected = params.id ? RoleDTO.get(params.int('id')) : null

        breadcrumbService.addBreadcrumb(controllerName, 'list', null, selected?.id, selected?.getTitle(session['language_id']))

		// if id is present and object not found, give an error message to the user along with the list
        if (params.id?.isInteger() && selected == null) {
			flash.error = 'role.not.found'
            flash.args = [params.id]
        }

        if (params.applyFilter) {
            render template: 'roles', model: [ roles: roles, selected: selected ]
        } else {
            render view: 'list', model: [ roles: roles, selected: selected ]
        }
    }

    def show = {
        def role = RoleDTO.get(params.int('id'))

        breadcrumbService.addBreadcrumb(controllerName, 'list', null, role.id, role.getTitle(session['language_id']))

        render template: 'show', model: [ selected: role ]
    }

    def edit = {
        def role = chainModel?.role ?: params.id ? RoleDTO.get(params.int('id')) : new RoleDTO()
        
        if (role == null) {
        	redirect action: 'list', params:params
            return
        }
        
        def permissionTypes = PermissionTypeDTO.list(order: 'asc')

        def crumbName = params.id ? 'update' : 'create'
        def crumbDescription = params.id ? role.getTitle(session['language_id']) : null
        breadcrumbService.addBreadcrumb(controllerName, actionName, crumbName, params.int('id'), crumbDescription)

		def roleTitle = chainModel?.roleTitle;
		def roleDescription = chainModel?.roleDescription;
		def validationError = chainModel?.validationError ? true : false;

        [ role: role, permissionTypes: permissionTypes, roleTitle:roleTitle, roleDescription:roleDescription, validationError:validationError ]
    }
    
    def save = {
    	
    	def role = new RoleDTO();
    	role.company = CompanyDTO.get(session['company_id'])
	    bindData(role, params, 'role')
    	def roleTitle = params.role.title == null ?: params.role.title.trim();
    	def roleDescription = params.role.description == null ?: params.role.description.trim();
    	def languageId = session['language_id'];
    	
    	try {
	
            List<PermissionDTO> allPermissions = PermissionDTO.list()
            params.permission.each { id, granted ->
                if (granted) {
                    role.permissions.add(allPermissions.find { it.id == id as Integer })
                }
            }

			def isNonEmptyRoleTitle = params.role.title ? !params.role.title.trim().isEmpty() : false;
			if (isNonEmptyRoleTitle) {
	            def roleService = new RoleBL();
	
	            // save or update
	            if (!role.id || role.id == 0) {
	                log.debug("saving new role ${role}")
	                roleService.validateDuplicateRoleName(roleTitle, languageId)
	                role.id = roleService.create(role)
					roleService.updateRoleType(role.id)
	
	                flash.message = 'role.created'
	                flash.args = [role.id as String]
	
	            } else {
	                log.debug("updating role ${role.id}")
	
	                roleService.set(role.id)
	                
	                if (!roleService.getEntity()?.getDescription(languageId, Constants.PSUDO_COLUMN_TITLE)?.equalsIgnoreCase(roleTitle)) {
	                	roleService.validateDuplicateRoleName(roleTitle, languageId)
	                }
	                
	                roleService.update(role)
	
	                flash.message = 'role.updated'
	                flash.args = [role.id as String]
	            }
	
	            // set/update international descriptions
	            roleService.setTitle(languageId, roleTitle)
	            roleService.setDescription(languageId, roleDescription)
	            chain action: 'list', params: [id: role.id]
	        } else {
				
	            String [] errors = ["RoleDTO,title,role.error.title.empty"]
				throw new SessionInternalError("Description is missing ", errors);            
	        }
        
        } catch (SessionInternalError e) {
            viewUtils.resolveException(flash, session.locale, e)
            chain action: 'edit', model: [ role:role, roleTitle:roleTitle, roleDescription:roleDescription, validationError:true ]
        }
    }

    def delete = {
        if (params.id) {
        	def roleService = new RoleBL(params.int('id'))
            roleService.deleteDescription(session['language_id'])
			roleService.deleteTitle(session['language_id'])
            roleService.delete()
            log.debug("Deleted role ${params.id}.")
        }

        flash.message = 'role.deleted'
        flash.args = [ params.id ]

        // render the partial role list
        params.applyFilter = true
        params.id = null
        list()
    }
}
