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

import grails.plugins.springsecurity.Secured

import com.sapienter.jbilling.common.SessionInternalError
import com.sapienter.jbilling.server.mediation.MediationConfigurationWS
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskDTO
import com.sapienter.jbilling.server.util.Constants
import com.sapienter.jbilling.server.mediation.db.MediationConfiguration
import com.sapienter.jbilling.server.mediation.task.IMediationReader
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskBL
import com.sapienter.jbilling.server.mediation.task.AbstractFileReader
import org.apache.commons.io.IOUtils
import com.sapienter.jbilling.server.mediation.task.IMediationProcess
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskDAS

/**
* MediationConfigController
*
* @author Vikas Bodani
* @since 15-Feb-2011
*/

@Secured(["MENU_99"])
class MediationConfigController {

    static pagination = [ max: 10, offset: 0 ]

    def mediationSession
	def webServicesSession
	def viewUtils
	def breadcrumbService
    PluggableTaskDAS pluggableTaskDAS

    def index = {
        redirect action: 'list'
    }

    def list = {

        def lastMediationProcessStatus
        def configurations= webServicesSession.getAllMediationConfigurations()

        def isMediationProcessRunning = webServicesSession.isMediationProcessRunning();
        if (isMediationProcessRunning) {
            flash.info = 'mediation.config.prompt.running'
        } else {
            lastMediationProcessStatus = webServicesSession.getMediationProcessStatus()
        }

        breadcrumbService.addBreadcrumb(controllerName, actionName, null, params.int('id'))
		
		if (params.applyFilter) {
			//flash.message=flash.message
			render template: 'configs', model:[types: configurations, readers: readers,
                    isMediationProcessRunning: isMediationProcessRunning, lastMediationProcessStatus: lastMediationProcessStatus]
		} else {
        	render view: 'list', model: [types: configurations, readers: readers,
                    isMediationProcessRunning: isMediationProcessRunning, lastMediationProcessStatus: lastMediationProcessStatus]
		}
    }
    
    def show = {
        
        def configId= params.int('id')
        
        log.debug "Show config id $params.id"
        
        def config= MediationConfiguration.get(configId)

        PluggableTaskBL<IMediationReader> readerTask = new PluggableTaskBL<IMediationReader>();
        readerTask.set(config.getPluggableTask());
        IMediationReader reader = readerTask.instantiateTask();

        def fileInjectionEnabled = (reader instanceof AbstractFileReader);

        if ( config.entityId != session['company_id']) {
            flash.error = 'configuration.does.not.exists.for.entity' 
            list()
        }
        
        breadcrumbService.addBreadcrumb(controllerName, actionName, null, configId)
        
        render template: 'show', model: [selected: config, fileInjectionEnabled: fileInjectionEnabled]
        
    }
	
    def edit = {
        
        def configId= params.int('id')
        
        def config = configId ? MediationConfiguration.get(configId) : null

        def crumbName = configId ? 'update' : 'create'
        def crumbDescription = params.id ? config?.name : null
        breadcrumbService.addBreadcrumb(controllerName, actionName, crumbName, configId, crumbDescription)

        render template: 'edit', model: [config: config, readers: readers, processors: processors]
    }

    def listEdit = {

        def configId= params.int('id')

        def config = configId ? MediationConfiguration.get(configId) : null
		def configurations= webServicesSession.getAllMediationConfigurations()

        def crumbName = configId ? 'update' : 'create'
        def crumbDescription = params.id ? config?.name : null
        breadcrumbService.addBreadcrumb(controllerName, 'listEdit', crumbName, configId, crumbDescription)

        render view: 'listEdit', model: [types: configurations, readers: readers, config: config]
    }

	def save = {

        if(!params.orderValue?.toString()?.isNumber()){
            flash.error= 'mediation.config.invalid.order'
            redirect action: 'list'
        }else{
            boolean nameFlag = true
            def ws= new MediationConfigurationWS()
            bindData(ws, params)
            if ( params.int('id') > 0 ) {
                log.debug "config exists.."

                try {
                    webServicesSession.updateAllMediationConfigurations([ws])

                } catch (SessionInternalError e) {
                    viewUtils.resolveException(flash, session.locale, e)
                    nameFlag=false
                }
                if (nameFlag==true){
                    flash.message = 'mediation.config.update.success'
                }
            } else {
                boolean newConfigSaved = true
                log.debug "New config.."
                ws.setCreateDatetime new Date()
                ws.setEntityId webServicesSession.getCallerCompanyId()
                try {
                    webServicesSession.createMediationConfiguration(ws)
                } catch (SessionInternalError e) {
                    viewUtils.resolveException(flash, session.locale, e)
                    newConfigSaved = false;
                }

                if (newConfigSaved == true) {
                    flash.message = 'mediation.config.create.success'
                }
            }
            chain action: 'list', params: [ id: ws?.id ]
        }
	}

	def delete = {

        try {
            webServicesSession.deleteMediationConfiguration(params.int('id'))
            flash.message = 'mediation.config.delete.success'
        } catch (SessionInternalError e){
            viewUtils.resolveException(flash, session.locale, e);
        } catch (Exception e) {
            log.error e.getMessage()
            flash.error = 'mediation.config.delete.failure'
        }

        // render list
        params.applyFilter = true
		list()

	}


    def run = {
        try {
            if (!webServicesSession.isMediationProcessRunning()) {
                webServicesSession.triggerMediation()
                flash.message = 'mediation.config.prompt.trigger'
            } else {
                flash.error = 'mediation.config.prompt.running'
            }
        } catch (Exception e) {
            log.error e.getMessage()
            viewUtils.resolveException(flash, session.locale, e);
        }

        params.applyFilter = null

        redirect action: 'list'
    }

    def showInject = {

        def configId= params.int('id')
        def config = configId ? MediationConfiguration.get(configId) : null
        def fileInjectionEnabled = params.boolean('fileInjectionEnabled')

        render template: 'inject', model: [ config: config, fileInjectionEnabled: fileInjectionEnabled]
    }

    def doInject = {

        def configId= params.int('id')
        def entityId= params.int('entityId')

        def fileInjectionEnabled = params.boolean('fileInjectionEnabled')
        try {
            if (fileInjectionEnabled) {
                def eventFile = request.getFile("events")

                if (!eventFile?.empty) {
                    def temp = File.createTempFile(eventFile.name, '.tmp')
                    eventFile.transferTo(temp)
                    log.debug("Injected event file saved to: " + temp?.getAbsolutePath());
                    mediationSession.triggerMediationByConfigurationWithFileInjection(
                            configId, entityId, temp)
                } else {
                    flash.error = 'mediation.config.inject.file.failure'
                }

            } else {

                def recordsString = params.recordsString
                if (recordsString) {
                    List<String> encodedEventRecords = IOUtils.readLines(new StringReader(recordsString))
                    mediationSession.triggerMediationByConfigurationWithRecordsInjection(
                            configId, entityId, encodedEventRecords)
                } else {
                    flash.error = 'mediation.config.inject.record.failure'
                }

            }
        } catch (SessionInternalError e){
            viewUtils.resolveException(flash, session.locale, e);
        } catch (Exception e) {
            log.error e.getMessage()
            flash.error = 'mediation.config.inject.failure'
        }

        params.applyFilter = null

        redirect action: 'list'
    }
    def getReaders() {

        def readers= new ArrayList<PluggableTaskDTO>()
        if (session.company_id) {
            readers= pluggableTaskDAS.findByEntityCategory(session.company_id as Integer, Constants.PLUGGABLE_TASK_MEDIATION_READER);
        }
        return readers
    }

    def getProcessors() {
        def processors = new ArrayList<IMediationProcess>()

        Integer languageId = session.language_id;
        Integer entityId = session.company_id;
        processors = pluggableTaskDAS.findByEntityCategory(entityId, Constants.PLUGGABLE_TASK_MEDIATION_PROCESS);

        return processors
    }
}
