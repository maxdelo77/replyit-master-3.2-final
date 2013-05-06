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

/**
 * FilterController
 *
 * @author Brian Cowdery
 * @since  03-12-2010
 */
@Secured(["isAuthenticated()"])
class FilterController {

    def filterService

    /**
     * Add a hidden filter to the filter pane.
     */
    def add = {
        def filters = filterService.showFilter(params.name)
        render template: "/layouts/includes/filters", model: [ filters: filters ]
    }

    /**
     * Remove a filter from the filter pane.
     */
    def remove = {
        def filters = filterService.removeFilter(params.name)
        render template: "/layouts/includes/filters", model: [ filters: filters ]
    }

    /**
     * Load a saved filter set and replace the current filters in the filter pane.
     */
    def load = {
        def filters = filterService.loadFilters(params.int("id"))
        render template: "/layouts/includes/filters", model: [ filters: filters ]
    }



    /**
     * Render the filter pane
     */
    def filters = {
        def filters = filterService.getCurrentFilters()
        render template: "/layouts/includes/filters", model: [ filters: filters ]
    }

    /**
     * Render a list of filter sets to be edited (from the save dialog)
     */
    def filtersets = {
        def filters = filterService.getCurrentFilters()
        def filtersets = FilterSet.findAllByUserId(session['user_id'])

        render template: "filtersets", model: [ filtersets: filtersets, filters: filters ]
    }

    def edit = {
        def filters = filterService.getCurrentFilters()
        def filterset = FilterSet.get(params.int('id'))

        render template: "edit", model: [ selected: filterset, filters: filters ]
    }

    def save = {
        def filterset = params.id ? FilterSet.get(params.int('id')) : new FilterSet(params)
        filterset.userId = session['user_id']

        def filters = filterService.getCurrentFilters()
        filterset.filters?.removeAll(filters);

        filters.each {
            filterset.addToFilters(new Filter(it))
        }

        filterset.save(flush: true)

        def filtersets = FilterSet.findAllByUserId(session['user_id'])
        render template: "filtersets", model: [ filtersets: filtersets, selected: filterset  ]
    }

    def delete = {
        FilterSet.get(params.int('id'))?.delete(flush: true)

        log.debug("deleted filter set ${params.id}")

        def filtersets = FilterSet.findAllByUserId(session['user_id'])
        render template: "filtersets", model: [ filtersets: filtersets ]
    }
}
