/*
 JBILLING CONFIDENTIAL
 _____________________

 [2003] - [2012] Enterprise jBilling Software Ltd.
 All Rights Reserved.

 NOTICE:  All information contained herein is, and remains
 the property of Enterprise jBilling Software.
 The intellectual and technical concepts contained
 herein are proprietary to Enterprise jBilling Software
 and are protected by trade secret or copyright law.
 Dissemination of this information or reproduction of this material
 is strictly forbidden.
 */

package jbilling

import com.sapienter.jbilling.server.pricing.db.RateCardDTO
import com.sapienter.jbilling.server.pricing.db.RateCardWS
import com.sapienter.jbilling.server.user.db.CompanyDTO
import com.sapienter.jbilling.server.pricing.RateCardBL
import com.sapienter.jbilling.common.SessionInternalError
import com.sapienter.jbilling.client.ViewUtils
import grails.plugins.springsecurity.Secured
import org.springframework.jdbc.core.JdbcTemplate

import com.sapienter.jbilling.server.util.IWebServicesSessionBean;
import com.sapienter.jbilling.server.util.sql.JDBCUtils
import org.springframework.jdbc.datasource.DataSourceUtils
import javax.sql.DataSource
import com.sapienter.jbilling.server.pricing.db.RateCardDAS
import au.com.bytecode.opencsv.CSVWriter
import com.sapienter.jbilling.server.util.csv.CsvExporter
import com.sapienter.jbilling.client.util.DownloadHelper

@Secured(["MENU_99"])
class RateCardController {

    static pagination = [max: 10, offset: 0]

    IWebServicesSessionBean webServicesSession
    ViewUtils viewUtils
    DataSource dataSource
    def breadcrumbService


    def index = {
        list()
    }

    def list = {
        params.max = params?.max?.toInteger() ?: pagination.max
        params.offset = params?.offset?.toInteger() ?: pagination.offset

        def cards = RateCardDTO.createCriteria().list(
                max: params.max,
                offset: params.offset
        ) {
            eq('company', new CompanyDTO(session['company_id']))
            order('id', 'desc')
        }

        breadcrumbService.addBreadcrumb(controllerName, actionName, null, params.int('id'), null)
        
        def selected = params.id ? RateCardDTO.get(params.int("id")) : null

        if (params.applyFilter || params.partial) {
            render template: 'rateCards', model: [cards: cards, selected: selected]
        } else {
            render view: 'list', model: [cards: cards, selected: selected]
        }
    }

    def show = {
        def rateCard = RateCardDTO.get(params.int('id'))

        render template: 'show', model: [selected: rateCard]
    }

    def delete = {
        if (params.id) {
            try {
                webServicesSession.deleteRateCard(params.int('id'))
                flash.message = 'rate.card.deleted'
                flash.args = [params.id]
                log.debug("Deleted rate card ${params.id}.")
            } catch (SessionInternalError e) {
                viewUtils.resolveException(flash, session.locale, e)
            }
        }

        // re-render the list of rate cards
        params.applyFilter = true
        params.partial = false
        params.id = null
        list()
    }

    def edit = {
        def rateCard = params.id ? RateCardDTO.get(params.int('id')) : null

		breadcrumbService.addBreadcrumb(controllerName, actionName, params.id ? 'update' : 'create', params.int('id'))
        if (params.id && rateCard == null) {
            flash.error = 'rate.card.not.found'
            flash.args = [params.id as String]

            redirect controller: 'rateCard', action: 'list'
            return
        }

        render template: 'edit', model: [rateCard: rateCard]
    }

    def rates = {
        def RateCardDTO rateCard = params.id ? RateCardDTO.get(params.int('id')) : null

        if (params.id && rateCard == null) {
            flash.error = 'rate.card.not.found'
            flash.args = [params.id as String]

            redirect controller: 'rateCard', action: 'list'
            return
        }
		breadcrumbService.addBreadcrumb(controllerName, actionName, params.id ? 'rates' : 'rates', params.int('id'))

        // get column names for the table header
        def rateCardService = new RateCardBL(rateCard)
        def columns = rateCardService.getRateTableColumnNames()

        // scrolling result set for reading the table contents
        def resultSet = rateCardService.getRateTableRows()

        render view: 'rates', model: [rateCard: rateCard, columns: columns, resultSet: resultSet]
    }

    def csv = {
        def rateCard = params.id ? RateCardDTO.get(params.int('id')) : null

        if (params.id && rateCard == null) {
            flash.error = 'rate.card.not.found'
            flash.args = [params.id as String]

            redirect controller: 'rateCard', action: 'list'
            return
        }

        def rateCardService = new RateCardBL(rateCard)

        // outfile
        def file = File.createTempFile(rateCard.tableName, '.csv')
        CSVWriter writer = new CSVWriter(new FileWriter(file), ',' as char)

        // write csv header
        def columns = rateCardService.getRateTableColumnNames()
        writer.writeNext(columns.toArray(new String[columns.size()]))

        // read rows and write file
        def exporter = CsvExporter.createExporter(RateCardDTO.class)
        def resultSet = rateCardService.getRateTableRows()
        while (resultSet.next()) {
            writer.writeNext(exporter.convertToString(resultSet.get()))
        }

        writer.close()

        // send file
        DownloadHelper.setResponseHeader(response, "${rateCard.tableName}.csv")
        render text: file.text, contentType: "text/csv"
    }

    def save = {
        def rateCard = new RateCardWS();
        bindData(rateCard, params)

        // save uploaded file
        def rates = request.getFile("rates")
        def temp = null

        if (params.rates?.getContentType().toString().contains('text/csv') ||
			params.rates?.getOriginalFilename().toString().endsWith('.csv')
			|| (rateCard.id && rates.empty )) {
			if (!rates.empty && validateRateCardName(rateCard)) {
				def name = rateCard.tableName ?: 'rate'
				temp = File.createTempFile(name, '.csv')
				rates.transferTo(temp)
				log.debug("rate card csv saved to: " + temp?.getAbsolutePath());
			}
	        try {
	            // save or update
	            if (!rateCard.id) {
					rateCard.id = webServicesSession.createRateCard(rateCard, temp);
	
	                flash.message = 'rate.card.created'
	                flash.args = [rateCard.id as String]
	
	            } else {
					webServicesSession.updateRateCard(rateCard, temp)
	
	                flash.message = 'rate.card.updated'
	                flash.args = [rateCard.id as String]
	            }
	        } catch (SessionInternalError e) {
	            viewUtils.resolveException(flash, session.locale, e)
	            chain action: 'list', model: [ selected: rateCard ]
				return
	
	        } finally {
	            temp?.delete()
	        }
		
		} else {
			flash.error = "csv.error.found"
		}

        chain action: 'list', params: [ id: rateCard?.id ]
    }

    boolean validateRateCardName(rateCard) {
        boolean valid = false
        String name = rateCard.name

        if(name.length()>0 && name.length()<=50 && name.matches('^[a-zA-Z0-9_]+$')){
             valid = true
        }
        
        return valid;
    }    
}
