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

enum SearchType {
    CUSTOMERS, ORDERS, INVOICES, PAYMENTS, BILLINGPROCESS, MEDIATIONPROCESS
}

class SearchCommand {
    Integer id
    String type
}

/**
 * SearchController
 *
 * @author Brian Cowdery
 * @since 15-Dec-2010
 */
class SearchController {

    def filterService
    def recentItemService
    def breadcrumbService

    def index = { SearchCommand cmd ->

        // add a filter to limit the list by the ID searched
        def filter = new Filter(type: FilterType.ALL, constraintType: FilterConstraint.EQ, field: 'id', template: 'id', visible: true, integerValue: cmd.id)

        // redirect to the controller of the type being searched
        def type = cmd?.type ? Enum.valueOf(SearchType.class, cmd?.type) : ""
        switch (type) {
            case SearchType.CUSTOMERS:
                filterService.setFilter(FilterType.CUSTOMER, filter)
                redirect(controller: 'customer', action: 'list', id: cmd.id)
                break

            case SearchType.ORDERS:
                filterService.setFilter(FilterType.ORDER, filter)
                redirect(controller: 'order', action: 'list', id: cmd.id)
                break

            case SearchType.INVOICES:
                filterService.setFilter(FilterType.INVOICE, filter)
                redirect(controller: 'invoice', action: 'list', id: cmd.id)
                break

            case SearchType.PAYMENTS:
                filterService.setFilter(FilterType.PAYMENT, filter)
                redirect(controller: 'payment', action: 'list', id: cmd.id)
                break
				
			case SearchType.BILLINGPROCESS:
				filterService.setFilter(FilterType.BILLINGPROCESS, filter)
				redirect(controller: 'billing', action: 'index', id: cmd.id)
				break
				
			case SearchType.MEDIATIONPROCESS:
				filterService.setFilter(FilterType.MEDIATIONPROCESS, filter)
				redirect(controller: 'mediation', action: 'index', id: cmd.id)
				break
            default:
                redirect(controller: 'home', action: 'index')
        }
    }
}
