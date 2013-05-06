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

import org.springframework.web.context.request.RequestContextHolder
import javax.servlet.http.HttpSession
import com.sapienter.jbilling.server.item.db.ItemDTO
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import com.sapienter.jbilling.server.user.db.CompanyDTO
import com.sapienter.jbilling.server.item.db.ItemTypeDTO
import com.sapienter.jbilling.server.item.ItemTypeBL
import java.io.Serializable

class ProductService implements Serializable {

    static transactional = true

    def messageSource

    /**
     * Returns a list of products filtered by simple criteria. The given filterBy parameter will
     * be used match either the ID, internalNumber or description of the product. The typeId parameter
     * can be used to restrict results to a single product type.
     *
     * @param company company
     * @param params parameter map containing filter criteria
     * @return filtered list of products
     */
    def getFilteredProducts(CompanyDTO company, GrailsParameterMap params, boolean includePlans) {

        // default filterBy message used in the UI
        def defaultFilter = messageSource.resolveCode('products.filter.by.default', session.locale).format((Object[]) [])

        // filter on item type, item id and internal number
        def products = ItemDTO.createCriteria().list() {
            and {
                if (params.filterBy && params.filterBy != defaultFilter) {
                    or {
                        eq('id', params.int('filterBy'))
                        ilike('internalNumber', "%${params.filterBy}%")
                    }
                }

                if (params.typeId) {
                    itemTypes {
                        eq('id', params.int('typeId'))
                    }
                }

                if(!includePlans){
                    isEmpty('plans')
                }
                eq('deleted', 0)
                eq('entity', company)
            }
            order('id', 'desc')
        }

        // if no results found, try filtering by description
        if (!products && params.filterBy) {
            products = ItemDTO.createCriteria().list() {
                and {

                    if(!includePlans){
                        isEmpty('plans')
                    }
                    eq('deleted', 0)
                    eq('entity', company)
                }
                order('id', 'desc')
            }.findAll {
                it.getDescription(session['language_id']).toLowerCase().contains(params.filterBy.toLowerCase())
            }
        }

        return products
    }

    /**
     * Returns a list of plan subscription items filtered by simple criteria. The given filterBy parameter
     * will be used match either the ID, internalNumber or description of the product.
     *
     * @param company company
     * @param params parameter map containing filter criteria
     * @return filtered list of products
     */
    def getFilteredPlans(CompanyDTO company, GrailsParameterMap params) {

        // default filterBy message used in the UI
        def defaultFilter = messageSource.resolveCode('products.filter.by.default', session.locale).format((Object[]) [])

        // filter on item type, item id and internal number
        def plans = ItemDTO.createCriteria().list() {
            and {
                if (params.filterBy && params.filterBy != defaultFilter) {
                    or {
                        eq('id', params.int('filterBy'))
                        ilike('internalNumber', "%${params.filterBy}%")
                    }
                }

                isNotEmpty('plans')
                eq('deleted', 0)
                eq('entity', company)
            }
            order('id', 'desc')
        }

        // if no results found, try filtering by description
        if (!plans && params.filterBy) {
            plans = ItemDTO.createCriteria().list() {
                and {
                    isNotEmpty('plans')
                    eq('deleted', 0)
                    eq('entity', company)
                }
                order('id', 'desc')
            }.findAll {
                it.getDescription(session['language_id']).toLowerCase().contains(params.filterBy.toLowerCase())
            }
        }

        return plans
    }

    /**
     * Returns a list of visible item types.
     *
     * @return list of item types
     */
    def getItemTypes() {
        log.debug("getting item types")

        return ItemTypeDTO.createCriteria().list() {
            and {
                eq('internal', false)
                eq('entity', new CompanyDTO(session['company_id']))
            }
            order('id', 'desc')
        }
    }

    /**
     * Returns the internal "plans" category used for storing plan subscription items.
     *
     * @return internal plans category
     */
    def getInternalPlansType() {
        new ItemTypeBL().getInternalPlansType(session['company_id'])
    }

    /**
     * Returns the HTTP session
     *
     * @return http session
     */
    def HttpSession getSession() {
        return RequestContextHolder.currentRequestAttributes().getSession()
    }
}
