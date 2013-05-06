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

import com.sapienter.jbilling.client.ViewUtils;
import com.sapienter.jbilling.server.pluggableTask.PluggableTask 
import com.sapienter.jbilling.server.pluggableTask.admin.ParameterDescription;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskDAS;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskDTO;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskManager;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskTypeCategoryDAS 
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskTypeCategoryDTO;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskTypeDAS 
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskTypeDTO;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskWS;
import com.sapienter.jbilling.server.user.UserBL 
import com.sapienter.jbilling.server.util.WebServicesSessionSpringBean;
import com.sapienter.jbilling.common.SessionInternalError;

import org.springframework.context.support.ReloadableResourceBundleMessageSource 
import org.springframework.security.access.annotation.Secured 

@Secured(["MENU_99"])
class PluginController {
    
    // all automatically injected by Grails. Thanks.
    WebServicesSessionSpringBean webServicesSession;
    ReloadableResourceBundleMessageSource messageSource
    PluggableTaskDAS pluggableTaskDAS
    ViewUtils viewUtils
    RecentItemService recentItemService;
    BreadcrumbService breadcrumbService;
    
    def index = {
        listCategories();
    }
    
    /*
     * Lists all the categories. The same for every company
     */
    def listCategories = {
        breadcrumbService.addBreadcrumb("plugin", "listCategories", null, null);
        List categorylist= PluggableTaskTypeCategoryDTO.list();
        log.info "Categories found= " + categorylist?.size()
        render (view:"categories", model:[categories:categorylist])
    }
    
    /*
     * This action lists all the plug-ins that belong to a Company and to 
     * the selected Category
     */
    def plugins = {
        Integer languageId = session.language_id;
        Integer entityId = session.company_id;
        log.info "entityId=" + entityId
        log.info "selected " + params["id"]
        if (params["id"]) {
            Integer categoryId = Integer.valueOf(params["id"]);
            log.info "Category Id selected=" + categoryId
            
            breadcrumbService.addBreadcrumb("plugin", "plugins", null, categoryId);
            def lstByCateg= pluggableTaskDAS.findByEntityCategory(entityId, categoryId);
            
            log.info "number of plug-ins=" + lstByCateg.size();
            // add the category id to the session, so the 'create' button can know 
            // which category to create for
            session.selected_category_id = categoryId;
        
            // show the list of the plug-ins
            if (params.template == 'show') {
                render template: "plugins", model:[plugins:lstByCateg]
            } else {
                render (view:"categories", model:[categories:PluggableTaskTypeCategoryDTO.list(),plugins:lstByCateg])
            }
        } else {
            log.error "No Category selected?"
        }
    }
    
    def show = {
        Integer taskId = params.id.toInteger();
        breadcrumbService.addBreadcrumb("plugin", "show", null, taskId);
        PluggableTaskDTO dto = pluggableTaskDAS.find(taskId);
        if (params.template == 'show') {
            render template: "show", model:[plugin:dto]
        } else {
            // its being called by the breadcrumbs
            showListAndPlugin(taskId);
        }
    }
    
    def showForm = {
        // find out the category name
        PluggableTaskTypeCategoryDTO category =  new PluggableTaskTypeCategoryDAS().find(
               session.selected_category_id);
        
        List<PluggableTaskTypeDTO> typesList = new PluggableTaskTypeDAS().findAllByCategory(category.getId());

        if (typesList?.isEmpty()){
            flash.error = messageSource.getMessage("no.plugin.types",null, session.locale)
            redirect (action:listCategories)
            return
        }

        // show the form with the description
        render (view:"form", model:
                [description: category.getDescription(session.language_id), isEdit: false,
                 types:typesList, parametersDesc : getDescriptions(typesList.first()?.getId())])
    }
    
    /*
     * This is called when a new type is picked from the drop down list of plug-in types (classes)
     * and the parameters need to be re-rendered
     */
    def getTypeParametersDescriptions = {
        log.info "Getting parameters for plug-in type " + params.typeId;
        
        render template:"formParameters", model:[parametersDesc : getDescriptions(params.typeId as Integer) ]
    }
    
    private List<ParameterDescription> getDescriptions(Integer typeId) {
        PluggableTaskTypeDTO type = new PluggableTaskTypeDAS().find(typeId);
        // create a new class to extract the parameters descriptions
        PluggableTask thisTask = PluggableTaskManager.getInstance(type.getClassName(), 
            type.getCategory().getInterfaceName());
        return thisTask.getParameterDescriptions();
    }
    
    def save = {
        // Create a new object from the form
        PluggableTaskWS newTask = new PluggableTaskWS();
        bindData(newTask, params);
        for(String key: params.keySet()) { // manually bind the plug-in parameters
            def value = params.get(key)
            if (key.startsWith("plg-parm-") && value) {
                newTask.getParameters().put(key.substring(9), value);
            }
        }
        
        // save
        Locale locale = session.locale;
        try {
            log.info "now saving " + newTask + " by " + session.user_id;
            Integer pluginId;
            if (newTask.getId() == null || newTask.getId() == 0) {
                pluginId = webServicesSession.createPlugin(newTask);
            	pluggableTaskDAS.invalidateCache(); // or the list won't have the new plug-in
            
            	// the message
            	flash.message = messageSource.getMessage("plugins.create.new_plugin_saved", [pluginId].toArray(), locale);
            } else { 
                // it is an update
                webServicesSession.updatePlugin(newTask);
            	flash.message = messageSource.getMessage("plugins.create.plugin_updated", [newTask.getId()].toArray(), locale);
                pluginId = newTask.getId();
            }
            
            // forward to the list of plug-in types and the new plug-in selected
            showListAndPlugin(pluginId);
        } catch(SessionInternalError e) {
            // process the exception so the error messages from validation are
            // put in the flash
            viewUtils.resolveException(flash, locale, e);
            // mmm... this can fail if the this is a new plug-in, started after a recent item click?
            PluggableTaskTypeCategoryDTO category =  new PluggableTaskTypeCategoryDAS().find(
                   session.selected_category_id);
            
            // render the form again, with all the data
            render(view: "form", model:
                    [description: category.getDescription(session.language_id),
                            types: new PluggableTaskTypeDAS().findAllByCategory(category.getId()),
                            pluginws: newTask, isEdit: Boolean.valueOf(params.isEdit),
                            parametersDesc: getDescriptions(newTask.getTypeId())])
        }
    }
    
    private void showListAndPlugin(Integer pluginId) {
        
        PluggableTaskDTO dto = pluginId ? new PluggableTaskDAS().find(pluginId) : null;

        if (pluginId && dto == null) {
			pluginNotFoundErrorRedirect(pluginId)
        } else {
	        render (view: "showListAndPlugin", model:
	            [plugin: dto,
	             plugins: pluggableTaskDAS.findByEntityCategory(session.company_id, dto?.getType().getCategory().getId())]);
		}
    }
    
    def cancel = {
        flash.message = messageSource.getMessage("plugins.edit.canceled",null, session.locale);
        if ((params.plugin_id as Integer) > 0 ) {
            // go the the list with the plug selected
            showListAndPlugin(params.plugin_id as Integer);
        } else {
            // it was creating a new one
            redirect (action:listCategories)
        }
    }
    
    def edit = {
        PluggableTaskDTO dto =  pluggableTaskDAS.find(params.id as Integer);
        if (dto != null) {
            breadcrumbService.addBreadcrumb("plugin", "edit", null, dto.getId());
            recentItemService.addRecentItem(dto.getId(), RecentItemType.PLUGIN);
            PluggableTaskTypeCategoryDTO category =  dto.getType().getCategory();
            render (view: "form", model:
                    [description: category.getDescription(session.language_id),
                            types: new PluggableTaskTypeDAS().findAllByCategory(category.getId()),
                            pluginws: new PluggableTaskWS(dto), isEdit: true,
                            parametersDesc: getDescriptions(dto.getType().getId())])
        } else {
            pluginNotFoundErrorRedirect(params.id)
        }
    }
    
    private void pluginNotFoundErrorRedirect(pluginId) {
    	flash.error="plugins.plugin.not.found"
		flash.args = [ pluginId as String ]
        redirect action: 'list'
    }
    
    def delete = {
        Integer id = null
        try {
            id = params.id as Integer;
        	webServicesSession.deletePlugin(id);
        	pluggableTaskDAS.invalidateCache(); // or the list will still show the deleted plug-in
        	flash.message = messageSource.getMessage("plugins.delete.done",[id].toArray(), session.locale);
        } catch (SessionInternalError e) {
            viewUtils.resolveException(flash, session.locale, e);
            showListAndPlugin(id)
            return
        } catch (Exception e){
            viewUtils.resolveException(flash, session.locale, e)
        }
        redirect (action:listCategories)
    }
    
    // the next method is to support the 'Recent Items'
    def list = {
        if (params.id)
            showListAndPlugin(params.id as Integer);
        else 
            listCategories()
    }
}
