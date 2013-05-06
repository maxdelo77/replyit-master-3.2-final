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

import com.sapienter.jbilling.server.metafields.MetaFieldBL
import com.sapienter.jbilling.server.metafields.db.MetaFieldDAS
import com.sapienter.jbilling.server.user.db.CompanyDTO
import com.sapienter.jbilling.server.metafields.db.EntityType
import com.sapienter.jbilling.server.metafields.db.MetaField
import grails.plugins.springsecurity.Secured
import com.sapienter.jbilling.common.SessionInternalError
import com.sapienter.jbilling.server.util.db.EnumerationDTO
import com.sapienter.jbilling.server.metafields.db.DataType

/**
 * @author Alexander Aksenov
 * @since 20.10.11
 */
@Secured(['isAuthenticated()'])
class MetaFieldsController {

    def viewUtils
    def webServicesValidationAdvice
    def breadcrumbService


    def index = {
        redirect(action: 'listCategories')
    }

    def listCategories = {
        List categorylist = Arrays.asList(EntityType.values());
        breadcrumbService.addBreadcrumb(controllerName, actionName, null, null)
        [lst: categorylist]
    }

    def list = {
        
        EntityType entityType 
        MetaField selectedField = null;
        if (params.id && params.get('id').isInteger()) {
            selectedField= MetaField.findById(params.get('id'))
            entityType= selectedField?.entityType
        } else {
        	if (params.id) {
            	entityType= EntityType.valueOf(params.get('id').toString())
            }
        } 
		
		// if id is present and object not found, give an error message to the user along with the list
        if (params.id && params.get('id').isInteger() && selectedField == null) {
			flash.error = 'metaField.not.found'
            flash.args = [params.id]
        }
			
        def lstByCateg = MetaFieldBL.getAvailableFieldsList(session['company_id'], entityType);

        if (params.template)
            render template: 'list', model: [lstByCategory: lstByCateg, selected: new MetaField()]
        else {
            if (params.selectedId) {
                selectedField = MetaField.findById(params.int("selectedId"))
            }
            render(view: 'listCategories', model: [selectedCategory: entityType?.name(), lst: Arrays.asList(EntityType.values()), lstByCategory: lstByCateg, selected: selectedField])
        }
    }

    def show = {
        def metaField = MetaField.get(params.int('id'))

        breadcrumbService.addBreadcrumb(controllerName, 'list', null, metaField.id, metaField.name)

        render template: 'show', model: [selected: metaField]
    }

    def edit = {
        def metaField = params.id ? MetaField.get(params.int('id')) : new MetaField()

		if (metaField == null) {
			redirect action: 'list', params: params
			return
		}

        def crumbName = params.id ? 'update' : 'create'
        def crumbDescription = params.id ? metaField?.getName() : null
        breadcrumbService.addBreadcrumb(controllerName, actionName, crumbName, params.int('id'), crumbDescription)

        [metaField: metaField]
    }

    def save = {
        def metaField = new MetaField()
        bindData(metaField, params, 'metaField')
        metaField.setEntityType(EntityType.valueOf(params.entityType))
		metaField.setEntity(new CompanyDTO(session['company_id']))

        if (params.defaultValue) {
            def defaultValue = metaField.createValue()
            bindData(defaultValue, ['value': params.get("defaultValue")])
            metaField.setDefaultValue(defaultValue)
        }

		def existingMetaField = new MetaFieldDAS().getFieldByName(session['company_id'], metaField.entityType, metaField.name);
		
		if (existingMetaField != null && existingMetaField.id != metaField.id) {
			flash.error = 'metaField.name.exists'
			render view: 'edit', model: [ metaField: metaField ]
			return
		}

        // validate
        try {
            webServicesValidationAdvice.validateObject(metaField)

        } catch (SessionInternalError e) {
            viewUtils.resolveException(flash, session.locale, e)
            render view: 'edit', model: [ metaField : metaField ]
            return
        }


        // save or update
        MetaFieldBL metaFieldsService = new MetaFieldBL();

        if (!metaField.id || metaField.id == 0) {
            log.debug("saving new metaField ${metaField}")
            metaField.id = metaFieldsService.create(metaField).id

            flash.message = 'metaField.created'
            flash.args = [metaField.id]

        } else {
            log.debug("updating meta field ${metaField.id}")

            metaFieldsService.update(metaField)

            flash.message = 'metaField.updated'
            flash.args = [metaField.id]
        }

        redirect(action: "list", id: metaField.entityType.name(), params: ["selectedId": metaField.id])
    }

    def delete = {
        MetaField metaField = null

        if (params.id) {
            metaField = MetaField.findById(params.int("id"))
        }

        long useCnt= new MetaFieldDAS().getTotalFieldCount(metaField.id)  
        log.debug "Meta field values: $useCnt exist"
        if ( useCnt> 0){
            log.debug('Can not delete metafield '+metaField.getId()+', it is in use.')
            flash.error = 'Can not delete metafield '+metaField.getId()+', it is in use.'
        } else {
            new MetaFieldBL().delete(params.int('id'))
            flash.message = 'metaField.deleted'
        }

        flash.args = [params.id]
        redirect action: "list", id: metaField.entityType
    }
}
