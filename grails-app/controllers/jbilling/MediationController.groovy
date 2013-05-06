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

import com.sapienter.jbilling.client.util.SortableCriteria
import com.sapienter.jbilling.server.invoice.db.InvoiceDTO
import com.sapienter.jbilling.server.mediation.db.MediationProcess
import com.sapienter.jbilling.server.order.db.OrderDTO
import grails.plugins.springsecurity.Secured
import com.sapienter.jbilling.server.mediation.db.MediationMapDAS
import com.sapienter.jbilling.server.mediation.db.MediationRecordStatusDAS
import com.sapienter.jbilling.server.user.db.CompanyDTO
import com.sapienter.jbilling.server.mediation.db.MediationRecordDTO
import com.sapienter.jbilling.server.mediation.db.MediationOrderMap
import com.sapienter.jbilling.server.util.csv.CsvExporter
import com.sapienter.jbilling.client.util.DownloadHelper
import com.sapienter.jbilling.server.util.csv.Exporter
import com.sapienter.jbilling.server.mediation.MediationErrorRecordWS

/**
 * MediationController
 *
 * @author Vikas Bodani
 * @since 17/02/2011
 */
@Secured(["MENU_95"])
class MediationController {

    static pagination = [max: 10, offset: 0, sort: 'id', order: 'desc']

    def webServicesSession
    def recentItemService
    def breadcrumbService
    def filterService
    def mediationSession

    def index = {
        list()
    }

    def list = {
        def filters = filterService.getFilters(FilterType.MEDIATIONPROCESS, params)
        List<MediationProcess> processes = []
        def processValues
        (processes, processValues) = getFilteredProcesses(filters, params)

        breadcrumbService.addBreadcrumb(controllerName, actionName, null, null)

        if (params.applyFilter || params.partial) {
            render template: 'processes', model: [processes: processes, filters: filters, processValues: processValues]
        } else {
            render view: "list", model: [processes: processes, filters: filters, processValues: processValues]
        }
    }

    def getFilteredProcesses (filters, params) {
		params.max = (params?.max?.toInteger()) ?: pagination.max
		params.offset = (params?.offset?.toInteger()) ?: pagination.offset
        params.sort = params?.sort ?: pagination.sort
        params.order = params?.order ?: pagination.order

        List<MediationProcess> processes = []
        def processValues = new HashMap<Integer, Integer>()

        processes = MediationProcess.createCriteria().list(
                max: params.max,
                offset: params.offset
        ) {
            and {
                filters.each { filter ->
                    if (filter.value != null) {
                        addToCriteria(filter.getRestrictions());
                    }
                }
            }

            join 'configuration'

            configuration {
                eq("entityId", session['company_id'])
            }

            // apply sorting
            SortableCriteria.sort(params, delegate)
        }

        def processIds = processes.collect {it.id}

        MediationRecordDTO.createCriteria().list() {
                projections {
                    groupProperty('process.id')
                    countDistinct('id')
                }
                if (processIds) {
                    'in'('process.id', processIds)
                }

        }.each { row -> //map the collection to a map
            processValues[row[0]] = row[1]
        }

        return [processes, processValues]
    }

    def show = {
        def process = MediationProcess.get(params.int('id'))
        def invoicesCreated = new MediationMapDAS().getInvoicesByMediationProcess(process?.id)
        def ordersCreated = new MediationMapDAS().getOrdersByMediationProcess(process?.id)

        def recordStatuses = webServicesSession.getNumberOfMediationRecordsByStatusesByMediationProcess(process?.id)
        def processRecordSize = MediationRecordDTO.createCriteria().get {
            projections {
                countDistinct('id')
            }

            eq('process', process)
        }

        recentItemService.addRecentItem(process.id, RecentItemType.MEDIATIONPROCESS)
        breadcrumbService.addBreadcrumb(controllerName, actionName, null, process.id)

        if (params.template) {
            render template: params.template, model: [selected: process,
                    invoicesCreated: invoicesCreated, recordStatuses: recordStatuses,
                    ordersCreated: ordersCreated, processRecordSize: processRecordSize]

        } else {
            def filters = filterService.getFilters(FilterType.MEDIATIONPROCESS, params)
            def processes
            def processValues
            (processes, processValues) = getFilteredProcesses(filters, params)

            render view: 'list', model: [selected: process, processes: processes, filters: filters,
                    invoicesCreated: invoicesCreated, recordStatuses: recordStatuses, processValues: processValues,
                    ordersCreated: ordersCreated, processRecordSize: processRecordSize]
        }
    }

    def showMediationRecords = {

        def mediationProcessId = params.int('id')
        def status = params.int('status');
        def currency = CompanyDTO.get(session['company_id']).currency

        def records = mediationSession.getMediationRecordsByMediationProcessAndStatus(mediationProcessId, status)
        def recordLines = new ArrayList<>();

        def record
        if (records) {
            record = records?.get(0)
            records.eachWithIndex{ item, pos ->
                recordLines.addAll(item.getLines())
            }
        } else {
            flash.info = message(code: 'event.mediation.records.not.available')
            flash.args = [params.id, params.status]
        }

        render view: 'events', model: [records: records, recordLines: recordLines, record: record, currency: currency]

    }

    def showMediationErrors = {

        def status = params.int('status');
        def mediationProcessId = params.int('id')
        def mediationErrorRecords = mediationSession.getMediationErrorRecordsByMediationProcess(
                session['company_id'], mediationProcessId, status);

        def record
        if (mediationErrorRecords) {
            record = mediationErrorRecords?.get(0)
        } else {
            flash.info = message(code: 'event.mediation.records.not.available')
            flash.args = [params.id, params.status]
        }

        render view: 'errors', model: [records: mediationErrorRecords, record: record]
    }

    def mediationRecordsCsv = {

        def status = params.int('status');
        def mediationProcessId = params.int('id')
        def records = mediationSession.getMediationRecordsByMediationProcessAndStatus(mediationProcessId, status)
        params.max = CsvExporter.MAX_RESULTS

        if (records.size() > CsvExporter.MAX_RESULTS) {
            flash.error = message(code: 'error.export.exceeds.maximum')
            flash.args = [CsvExporter.MAX_RESULTS]
            redirect action: 'list', id: params.id

        } else {
            DownloadHelper.setResponseHeader(response, "mediation_records.csv")
            Exporter<MediationRecordDTO> exporter = CsvExporter.createExporter(MediationRecordDTO.class);
            render text: exporter.export(records), contentType: "text/csv"
        }
    }

    def mediationErrorsCsv = {

        def status = params.int('status');
        def mediationProcessId = params.int('id')
        def mediationErrorRecords = mediationSession.getMediationErrorRecordsByMediationProcess(
                session['company_id'], mediationProcessId, status);

        params.max = CsvExporter.MAX_RESULTS

        if (mediationErrorRecords.size() > CsvExporter.MAX_RESULTS) {
            flash.error = message(code: 'error.export.exceeds.maximum')
            flash.args = [CsvExporter.MAX_RESULTS]
            redirect action: 'list', id: params.id

        } else {
            DownloadHelper.setResponseHeader(response, "mediation_errors.csv")
            Exporter<MediationErrorRecordWS> exporter = CsvExporter.createExporter(MediationErrorRecordWS.class);
            render text: exporter.export(mediationErrorRecords), contentType: "text/csv"
        }

    }

    def invoice = {

        def invoiceId = params.int('id')
        def invoice = InvoiceDTO.get(invoiceId)
        def recordLines = mediationSession.getMediationRecordLinesForInvoice(invoiceId)
        def record = recordLines?.get(0)?.record

        render view: 'events', model: [invoice: invoice, recordLines: recordLines, record: record]
    }

    def order = {

        def orderId = params.int('id')
        def order, recordLines, record

        try {
            order = OrderDTO.get(orderId)
            recordLines = mediationSession.getMediationRecordLinesForOrder(orderId)
            record = recordLines?.get(0)?.record
        } catch (Exception e) {
            flash.info = message(code: 'error.mediation.events.none')
            flash.args = [params.id]
        }
        render view: 'events', model: [ order: order, recordLines: recordLines, record: record]
    }

}
