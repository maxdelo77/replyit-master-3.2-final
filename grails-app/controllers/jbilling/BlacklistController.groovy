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

import com.sapienter.jbilling.server.payment.blacklist.db.BlacklistDTO
import com.sapienter.jbilling.server.payment.IPaymentSessionBean
import com.sapienter.jbilling.server.util.Context
import com.sapienter.jbilling.server.user.UserBL
import grails.plugins.springsecurity.Secured
import com.sapienter.jbilling.server.payment.blacklist.CsvProcessor
import org.hibernate.criterion.CriteriaSpecification

@Secured(["CUSTOMER_14"])
class BlacklistController {

    def index = {
        redirect action: list, params: params
    }

    def getFilteredList(params) {
        def blacklist = BlacklistDTO.createCriteria().list() {
            createAlias("company", "company", CriteriaSpecification.INNER_JOIN)
            createAlias("user", "user", CriteriaSpecification.LEFT_JOIN)
            createAlias("creditCard", "creditCard", CriteriaSpecification.LEFT_JOIN)
            if (params.filterBy && params.filterBy != message(code: 'blacklist.filter.by.default')) {
                or {
                    eq('user.id', params.int('filterBy'))
                    ilike('user.userName', "%${params.filterBy}%")
                    ilike('creditCard.ccNumberPlain', "%${params.filterBy}%")
                }
            }

            eq('company.id', session['company_id'])
            or {
                isNull('user')
                eq('user.deleted', 0)
            }
            order('id', 'asc')
        }

    }

    def list = {
        def blacklist = getFilteredList(params)
        def selected = params.id ? BlacklistDTO.get(params.int('id')) : null

        render view: 'list', model: [ blacklist: blacklist, selected: selected ]
    }

    def filter = {
        def blacklist = getFilteredList(params)

        render template: 'entryList', model: [blacklist: blacklist]
    }

    def show = {
        def entry = BlacklistDTO.get(params.int('id'))

        render template: 'show', model: [selected: entry]
    }

    def save = {
        def replace = params.csvUpload == 'modify'
        def file = request.getFile('csv');
        if (!params.csv.getContentType().toString().contains('text/csv')) {
            flash.error = "csv.error.found"
            redirect action: 'list'
        } else if (!file.empty) {
            def csvFile = File.createTempFile("blacklist", ".csv")
            file.transferTo(csvFile)

            IPaymentSessionBean paymentSession = Context.getBean(Context.Name.PAYMENT_SESSION)
            def added
            try {
                added = paymentSession.processCsvBlacklist(csvFile.getAbsolutePath(), replace, (Integer) session['company_id'])
                flash.message = replace ? 'blacklist.updated' : 'blacklist.added'
                flash.args = [added]
                redirect view: 'list'
            } catch (CsvProcessor.ParseException e) {
                log.debug "Invalid format for the Blacklsit CSV file"
                flash.error = "Invalid format for the Blacklist CSV file"
                redirect action: 'list'
            }
        }

        chain view: list
    }

    def user = {
        if (params.id) {
            def bl = new UserBL(params.int('id'))
            bl.setUserBlacklisted((Integer) session['user_id'], true)

            flash.message = 'user.blacklisted'
            flash.args = [params.id as String]
        }

        redirect controller: 'customerInspector', action: 'inspect', id: params.id
    }

}
