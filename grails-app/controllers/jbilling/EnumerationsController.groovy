package jbilling

import com.sapienter.jbilling.common.SessionInternalError
import com.sapienter.jbilling.server.metafields.db.DataType
import com.sapienter.jbilling.server.metafields.db.MetaFieldDAS
import com.sapienter.jbilling.server.user.db.CompanyDTO
import com.sapienter.jbilling.server.util.EnumerationBL
import com.sapienter.jbilling.server.util.db.EnumerationDAS
import com.sapienter.jbilling.server.util.db.EnumerationDTO
import com.sapienter.jbilling.server.util.db.EnumerationValueDTO

class EnumerationsController {

    static pagination = [max: 10, offset: 0]

    def webServicesSession
    def webServicesValidationAdvice
    def viewUtils
    def recentItemService
    def breadcrumbService

    def index = {
        list()
    }

    def list = {
        params.max = params?.max?.toInteger() ?: pagination.max
        params.offset = params?.offset?.toInteger() ?: pagination.offset

        def enums = EnumerationDTO.createCriteria().list(
                max: params.max,
                offset: params.offset
        ) {
            eq('entity', new CompanyDTO(session['company_id']))
            order('name', 'asc')
        }

        def selected = params.id ? EnumerationDTO.get(params.int("id")) : null
        breadcrumbService.addBreadcrumb(controllerName, actionName, null, params.int("id"))
        
        // if id is present and object not found, give an error message to the user along with the list
        if (params.id?.isInteger() && selected == null) {
			flash.error = 'enumeration.not.found'
            flash.args = [params.id]
        }
        
        if (params.applyFilter || params.partial) {
            render template: 'enumerations', model: [enumerations: enums, selected: selected]
        } else {
            render view: 'list', model: [enumerations: enums, selected: selected]
        }
    }

    /**
     * Shows details of the selected Enumeration.
     */
    def show = {
        def selected = EnumerationDTO.get(params.int('id'))
        breadcrumbService.addBreadcrumb(controllerName, 'list', null, params.int('id'))
        render template: 'show', model: [selected: selected]
    }

    def delete = {
        if (params.id) {
            def enumer = EnumerationDTO.get(params.int('id'))
            log.debug "found enumeration ${enumer}"
            if (new MetaFieldDAS().getFieldCountByDataTypeAndName(DataType.ENUMERATION, enumer.getName()) > 0) {
                log.debug('Can not delete enumeration ' + enumer.getName() + ', it is in use.')
                flash.error = 'Can not delete enumeration ' + enumer.getName() + ', it is in use.'
                return
            } else {
                enumer?.delete()
                log.debug("Deleted Enumeration ${params.id}.")
            }
        }

        flash.message = 'enumeration.deleted'
        flash.args = [params.id]

        // render the partial list
        chain action: list, params: [applyFilter: true]
    }

    def edit = {
        def enumeration = params.id ? EnumerationDTO.get(params.int('id')) : new EnumerationDTO()
        def crumbName = params.id ? 'update' : 'create'
        def crumbDescription = params.id ? enumeration?.getName() : null
        breadcrumbService.addBreadcrumb(controllerName, actionName, crumbName, params.int('id'), crumbDescription)

        render view: 'edit', model: [enumeration: enumeration]
    }

    def save = {
        def enumeration = new EnumerationDTO(params);
        if (enumeration?.name != null) {
            enumeration.setName(enumeration?.name.trim())
        }
        enumeration.setEntity(new CompanyDTO(session['company_id']));

        // validate atleast one enum-value
        if (!enumeration.values) {
            flash.error = 'enumeration.atleast.one'
            render view: 'edit', model: [enumeration: enumeration]
            return
        }

        // validate blank name
        if (!enumeration.name?.trim()) {
            flash.error = 'enumeration.name.empty'
            render view: 'edit', model: [enumeration: enumeration]
            return
        }

        EnumerationBL enumerationService = new EnumerationBL();

        // validate duplicate enum
        def var = EnumerationDTO.createCriteria().list() {
            if (enumeration.id > 0) {
                ne('id', enumeration.id)
            }
            eq('name', enumeration.name?.trim(), [ignoreCase: true])
            eq('entity.id', enumeration.entity.id)
        }
        if (var) {
            flash.error = 'enumeration.name.exists'
            render view: 'edit', model: [enumeration: enumeration]
            return
        }

        // validate enumeration values
        Set<String> values = new HashSet<String>()
        for (EnumerationValueDTO value : enumeration.values) {
            log.debug("value = ${value}")

            // empty value
            if (!value.value?.trim()) {
                flash.error = 'enumeration.value.missing'
                render view: 'edit', model: [enumeration: enumeration]
                return
            }

            // duplicate
            if (values.contains(value.value)) {
                flash.error = 'enumeration.value.duplicated'
                render view: 'edit', model: [enumeration: enumeration]
                return
            }

            value.enumeration = enumeration
            values.add(value.value)
        }

        // validate JSR-303 annotations
        try {
            webServicesValidationAdvice.validateObject(enumeration)

        } catch (SessionInternalError e) {
            viewUtils.resolveException(flash, session.locale, e)
            render view: 'edit', model: [enumeration: enumeration]
            return
        }

        // save or update
        if (!enumeration.id || enumeration.id == 0) {
            log.debug("saving new enumeration ${enumeration}")
            enumeration.id = enumerationService.create(enumeration)

            flash.message = 'enumeration.created'
            flash.args = [enumeration.id]

        } else {
            log.debug("updating enumeration ${enumeration.id}")

            String oldEnumName = new EnumerationDAS().find(enumeration.id).name;
            enumerationService.updateMetaFields(oldEnumName, params.name)
            enumerationService.set(enumeration.id)
            enumerationService.update(enumeration)

            flash.message = 'enumeration.updated'
            flash.args = [enumeration.id]
        }

        chain action: list, params: [id: enumeration.id]
    }
}
