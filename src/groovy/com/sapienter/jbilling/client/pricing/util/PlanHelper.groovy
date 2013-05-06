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

package com.sapienter.jbilling.client.pricing.util

import com.sapienter.jbilling.server.pricing.PriceModelWS
import com.sapienter.jbilling.server.pricing.db.AttributeDefinition
import com.sapienter.jbilling.server.pricing.db.PriceModelStrategy
import org.codehaus.groovy.grails.web.metaclass.BindDynamicMethod
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

/**
 * PlanHelper 
 *
 * @author Brian Cowdery
 * @since 23/02/11
 */
class PlanHelper {

    static def PriceModelWS bindPriceModel(GrailsParameterMap params) {
        // sort price model parameters by index
        def sorted = new TreeMap<Integer, GrailsParameterMap>()
        params.model.each{ k, v ->
            if (v instanceof Map)
                sorted.put(k, v)
        }

        // build price model chain
        def root = null
        def model = null

        sorted.each{ i, modelParams ->
            if (model == null) {
                model = root = new PriceModelWS()
            } else {
                model = model.next = new PriceModelWS()
            }

            // bind model (can't use bindData() since this isn't a controller)
            def args =  [ model, modelParams, [exclude:[]] ]
            new BindDynamicMethod().invoke(model, 'bind', (Object[]) args)

            // bind model attributes
            modelParams.attribute.each{ j, attrParams ->
                if (attrParams instanceof Map)
                    if (attrParams.name)
                        model.attributes.put(attrParams.name, attrParams.value)
            }

            // clear type specific attributes after a change in strategy
            if (!modelParams.oldType.isEmpty() && modelParams.type != modelParams.oldType) {
                PriceModelStrategy oldType = PriceModelStrategy.valueOf(modelParams.oldType)
                for (AttributeDefinition attribute : oldType.strategy.attributeDefinitions) {
                    model.attributes.remove(attribute.name)
                }
            } else {
                if (params?.startDate != params?.originalStartDate) {
                    model.id = null
                }
            }

        }

        return root;
    }

}
