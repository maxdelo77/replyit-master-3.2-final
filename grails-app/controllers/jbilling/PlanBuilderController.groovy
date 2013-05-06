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

import com.sapienter.jbilling.client.pricing.util.PlanHelper
import com.sapienter.jbilling.common.CommonConstants
import com.sapienter.jbilling.common.SessionInternalError
import com.sapienter.jbilling.server.item.CurrencyBL
import com.sapienter.jbilling.server.item.ItemDTOEx
import com.sapienter.jbilling.server.item.PlanItemBundleWS
import com.sapienter.jbilling.server.item.PlanItemWS
import com.sapienter.jbilling.server.item.PlanWS
import com.sapienter.jbilling.server.item.db.ItemDTO;
import com.sapienter.jbilling.server.item.db.PlanDTO;
import com.sapienter.jbilling.server.item.db.PlanItemDTO;
import com.sapienter.jbilling.server.order.db.OrderPeriodDTO
import com.sapienter.jbilling.server.pricing.PriceModelBL
import com.sapienter.jbilling.server.pricing.PriceModelWS
import com.sapienter.jbilling.server.pricing.db.PriceModelStrategy
import com.sapienter.jbilling.server.user.db.CompanyDTO
import com.sapienter.jbilling.server.util.Constants
import com.sapienter.jbilling.server.util.Util
import grails.plugins.springsecurity.Secured
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

/**
 * Plan builder controller
 *
 * @author Brian Cowdery
 * @since 01-Feb-2011
 */
@Secured(["hasAnyRole('PLAN_60', 'PLAN_61')"])
class PlanBuilderController {

    def webServicesSession
    def viewUtils

    def breadcrumbService
    def productService

    def index = {
        redirect action: 'edit'
    }

    /**
     * Sorts a list of PlanItemWS objects by precedence and itemId.
     *
     * @param planItems plan items
     * @return sorted list of plan items
     */
    def sortPlanItems(planItems) {
        // precedence in ascending order, item id in descending
        return planItems.sort { a, b->
            (b.precedence <=> a.precedence) ?: (a.itemId <=> b.itemId)
        }
    }

    /**
     * Returns a sorted list of all plan item pricing dates.
     *
     * @param planItems plan items
     * @return sorted list of pricing dates
     */
    def collectPricingDates(planItems) {
        def dates = new TreeSet<Date>()

        planItems.each{ item->
            item.models.keySet().each{ date->
                dates << date
            }
        }

        return dates;
    }

    def editFlow = {

        /**
         * Initializes the plan builder, putting necessary data into the flow and conversation
         * contexts so that it can be referenced later.
         */
        initialize {
            action {
                if (!params.id && !SpringSecurityUtils.ifAllGranted("PLAN_60")) {
                    // not allowed to create
                    redirect controller: 'login', action: 'denied'
                    return
                }

                if (params.id && !SpringSecurityUtils.ifAllGranted("PLAN_61")) {
                    // not allowed to edit
                    redirect controller: 'login', action: 'denied'
                    return
                }

                def plan
                def product

                try {
                    plan = params.id ? webServicesSession.getPlanWS(params.int('id')) : new PlanWS()
                    product = plan?.itemId ? webServicesSession.getItem(plan.itemId, session['user_id'], null) : new ItemDTOEx()
                } catch (SessionInternalError e) {
                    log.error("Could not fetch WS object", e)
                    redirect controller: 'plan', action: 'list', params: params
                    return
                }

                def company = CompanyDTO.get(session['company_id'])
                def itemTypes = productService.getItemTypes()
                def internalPlansType = productService.getInternalPlansType()

				def currencies = new CurrencyBL().getCurrenciesWithoutRates(session['language_id'].toInteger(), session['company_id'].toInteger(),true)

                def orderPeriods = company.orderPeriods.collect { new OrderPeriodDTO(it.id) }
                def itemOrderPeriods = orderPeriods.clone()
                itemOrderPeriods << new OrderPeriodDTO(Constants.ORDER_PERIOD_ONCE) << new OrderPeriodDTO(Constants.ORDER_PERIOD_ALL_ORDERS)
                orderPeriods.sort { it.id }
                itemOrderPeriods.sort {it.id}

                // subscription product defaults for new plans
                if (!product.id || product.id == 0) {
                    product.hasDecimals = 0
                    product.types = [internalPlansType.id]
                    product.entityId = company.id

                    def priceModel = new PriceModelWS()
                    priceModel.type = PriceModelStrategy.METERED
                    priceModel.rate = BigDecimal.ZERO
                    priceModel.currencyId = (currencies.find { it.id == session['currency_id']} ?: company.currency).id

                    product.defaultPrices.put(CommonConstants.EPOCH_DATE, priceModel)
                }

                // subscription product uses a METERED price model
                // don't use the legacy compatibility pricing fields
                product.percentage = null
                product.price = null

                log.debug("plan subscription product ${product}")

                // defaults for new plans
                if (!plan.id || plan.id == 0) {
                    plan.periodId = orderPeriods.first().id
                }

                log.debug("plan ${plan}")

                // pricing timeline
                def pricingDates = collectPricingDates(plan.planItems)
                def startDate
                if (!product.id || product.id == 0) {
                    startDate = CommonConstants.EPOCH_DATE
                } else {
                    if (pricingDates) {
                        startDate = pricingDates.asList().last()
                    } else {
                        startDate = CommonConstants.EPOCH_DATE
                    }
                }

                // add breadcrumb
                def crumbName = params.id ? 'update' : 'create'
                def crumbDescription = params.id ? product.number : null
                breadcrumbService.addBreadcrumb(controllerName, actionName, crumbName, params.int('id'), crumbDescription)

                // model scope for this flow
                flow.company = company
                flow.itemTypes = itemTypes
                flow.currencies = currencies
                flow.orderPeriods = orderPeriods
                flow.itemOrderPeriods = itemOrderPeriods

                // conversation scope
                conversation.pricingDates = pricingDates
                conversation.startDate = startDate
                conversation.plan = plan
                conversation.product = product
                conversation.products = productService.getFilteredProducts(company, params, false)
            }
            on("success").to("build")
        }

        /**
         * Renders the plan details tab panel.
         */
        showDetails {
            action {
                params.template = 'details'
            }
            on("success").to("build")
        }

        /**
         * Renders the product list tab panel, filtering the product list by the given criteria.
         */
        showProducts {
            action {
                // filter using the first item type by default
                if (params.typeId == null && flow.itemTypes)
                    params.typeId = flow.itemTypes?.asList()?.first()?.id

                params.template = 'products'
                conversation.products = productService.getFilteredProducts(flow.company, params, false)
            }
            on("success").to("build")
        }

        /**
         * Renders the pricing timeline top panel, allowing navigation and creation of pricing dates.
         */
        showTimeline {
            action {
                params.template = 'timeline'
            }
            on("success").to("build")

        }

        addDate {
            action {
            
            	try {
            	
	            	Util.getParsedDateOrThrowError(message(code: 'date.format'), params.startDate, 'PlanWS,date,invalid.date.format') 
	            
	                def startDate = new Date().parse(message(code: 'date.format'), params.startDate)
	
	                log.debug("adding plan items pricing date ${params.startDate}")
	
	                // find the closet price model to the new date and copy it
	                // to create a new price for the given start date
	                for (PlanItemWS item : conversation.plan.planItems) {
	                    def itemPriceModel = PriceModelBL.getWsPriceForDate(item.getModels(), startDate)
	                    def priceModel = new PriceModelWS(itemPriceModel);
	                    priceModel.id = null
	
	                    item.addModel(startDate, priceModel);
	                }
	
	                // update pricing dates
	                conversation.pricingDates = collectPricingDates(conversation.plan.planItems)
	                conversation.startDate = startDate
	
	                log.debug("adding subscription product pricing date ${params.startDate}")
	
	                // copy the closest model to the new date
	                def defaultPriceModel = PriceModelBL.getWsPriceForDate(conversation.product.defaultPrices, startDate)
	                def priceModel = new PriceModelWS(defaultPriceModel)
	                priceModel.id = null
	
	                conversation.product.defaultPrices.put(startDate, priceModel)
	
	
	                params.template = 'review'
                
                } catch (SessionInternalError e) {
                	params.template = 'review'
                    viewUtils.resolveException(flash, session.locale, e)
                }
            }
            on("success").to("build")
        }

        editDate {
            action {
                def startDate = new Date().parse(message(code: 'date.format'), params.startDate)
                conversation.startDate = startDate

                log.debug("editing pricing date ${params.startDate}")

                params.template = 'review'
            }
            on("success").to("build")
        }

        removeDate {
            action {
                def startDate = new Date().parse(message(code: 'date.format'), params.startDate)
                log.debug("Removing pricing date from plan items ${startDate}")

                // remove plan items for the startDate
                for (PlanItemWS item : conversation.plan.planItems) {
                    item.removeModel(startDate);
                }

                log.debug("Remove subscription product pricing date ${startDate}")
                conversation.product.defaultPrices.remove(startDate)

                // refresh pricing dates
                conversation.pricingDates = collectPricingDates(conversation.plan.planItems)
                conversation.startDate = conversation.product.defaultPrices.lastKey();

                // (pai) this is not very good solution to delete the timeline on click
                // preffered way on application level should be on save changes
                /*
                def plan = conversation.plan
                def product = conversation.product

                if (plan?.id && product?.id) {

                    product.number = product?.number?.trim()
                    product.description = product?.description?.trim()

                    log.debug("Async saving changes to plan subscription item ${product.id}")
                    webServicesSession.updateItem(product)

                    log.debug("Async saving changes to plan ${plan.id}")
                    webServicesSession.updatePlan(plan)
                }*/

                params.template = 'review'

            }
            on("success").to("build")
        }

        /**
         * Add a new price for the given product id, and render the review panel.
         */
        addPrice {
            action {
                // product being added
                def productId = params.int('id')
                def product = conversation.products.find{ it.id == productId }

                //Line type product validation when creating a plan
                if(product.percentage){
                    params.template = 'review'
                    flow.errorMessages= [g.message(code:"validation.error.plan.invalid.product")]
                    return invalidProduct()
                }

                def productPrice = product.getPrice(conversation.startDate)
                // build a new plan item, using the default item price model
                // as the new objects starting values
                def priceModel = productPrice ? new PriceModelWS(productPrice) : new PriceModelWS()
                priceModel.id = null

                // empty bundle
                def bundle = new PlanItemBundleWS()

                // add price to the plan
                conversation.plan.planItems << new PlanItemWS(productId, priceModel, bundle)

                params.newLineIndex = conversation.plan.planItems.size() - 1
                params.template = 'review'
            }
            on("success").to("build")
            on("invalidProduct").to("build")
        }

        /**
         * Updates a price and renders the review panel.
         */
        updatePrice {
            action {
                def index = params.int('index')
                def planItem = conversation.plan.planItems[index]

                log.debug("updating price for date ${conversation.startDate}")

                if (!planItem.bundle) planItem.bundle = new PlanItemBundleWS()

                bindData(planItem, params, 'price')
                bindData(planItem.bundle, params, 'bundle')

				//TODO pass default values to binder methods when not-null values not acceptable
				if(!planItem?.bundle?.quantity) {
					planItem?.bundle?.quantity= "0"
				}

                def priceModel = PlanHelper.bindPriceModel(params)
                planItem.models.put(conversation.startDate, priceModel)

                log.debug("updated price: ${priceModel}")
                log.debug("price from timeline map ${planItem.models.get(conversation.startDate)}")

                try {
                    // validate attributes of updated price
                    PriceModelBL.validateWsAttributes(planItem.models.values())

                    // re-order plan items by precedence, unless a validation exception is thrown
                    conversation.plan.planItems = sortPlanItems(conversation.plan.planItems)

                    log.debug("Updated conversation plan ${conversation.plan}")
                    log.debug("Updated conversation item ${conversation.plan.planItems[index]}")

                } catch (SessionInternalError e) {
                    viewUtils.resolveException(flash, session.locale, e)
                    params.newLineIndex = index
                }

                params.template = 'review'
            }
            on("success").to("build")
        }

        /**
         * Removes a item price from the plan and renders the review panel.
         */
        removePrice {
            action {
                conversation.plan.planItems.remove(params.int('index'))
                params.template = 'review'
            }
            on("success").to("build")
        }

        /**
         * Updates a strategy of a model in a pricing chain.
         */
        updateStrategy {
            action {
                def index = params.int('index')
                def planItem = conversation.plan.planItems[index]

                bindData(planItem, params, 'price')
                bindData(planItem.bundle, params['bundle'])
                planItem.models.put(conversation.startDate, PlanHelper.bindPriceModel(params))

                params.newLineIndex = index
                params.template = 'review'
            }
            on("success").to("build")
        }

        /**
         * Adds an additional price model to the chain.
         */
        addChainModel {
            action {
                def index = params.int('index')
                def rootModel = PlanHelper.bindPriceModel(params)

                // add new price model to end of chain
                def model = rootModel
                while (model.next) {
                    model = model.next
                }
                model.next = new PriceModelWS();

                // add updated model to the plan item
                def planItem = conversation.plan.planItems[index]
                planItem.models.put(conversation.startDate, rootModel)

                params.newLineIndex = index
                params.template = 'review'
            }
            on("success").to("build")
        }

        /**
         * Removes a price model from the chain.
         */
        removeChainModel {
            action {
                def index = params.int('index')
                def modelIndex = params.int('modelIndex')
                def rootModel = PlanHelper.bindPriceModel(params)

                // remove price model from the chain
                def model = rootModel
                for (int i = 1; model != null; i++) {
                    if (i == modelIndex) {
                        model.next = model.next?.next
                        break
                    }
                    model = model.next
                }

                // add updated model to the plan item
                def planItem = conversation.plan.planItems[index]
                planItem.models.put(conversation.startDate, rootModel)

                params.newLineIndex = index
                params.template = 'review'
            }
            on("success").to("build")
        }

        /**
         * Adds a new attribute field to the plan price model, and renders the review panel.
         * The rendered review panel will have the edited line open for further modification.
         */
        addAttribute {
            action {
                def index = params.int('index')
                def rootModel = PlanHelper.bindPriceModel(params)

                def modelIndex = params.int('modelIndex')
                def attribute = message(code: 'plan.new.attribute.key', args: [ params.attributeIndex ])

                // find the model in the chain, and add a new attribute
                def model = rootModel
                for (int i = 0; model != null; i++) {
                    if (i == modelIndex) {
                        model.attributes.put(attribute, '')
                    }
                    model = model.next
                }

                // add updated model to the plan item
                def planItem = conversation.plan.planItems[index]
                planItem.models.put(conversation.startDate, rootModel)

                params.newLineIndex = index
                params.template = 'review'
            }
            on("success").to("build")
        }

        /**
         * Removes the given attribute name from a plan price model, and renders the review panel.
         * The rendered review panel will have the edited line open for further modification.
         */
        removeAttribute {
            action {
                def index = params.int('index')
                def rootModel = PlanHelper.bindPriceModel(params)

                def modelIndex = params.int('modelIndex')
                def attributeIndex = params.int('attributeIndex')

                // find the model in the chain, remove the attribute
                def model = rootModel
                for (int i = 0; model != null; i++) {
                    if (i == modelIndex) {
                        def name = params["model.${modelIndex}.attribute.${attributeIndex}.name"]
                        model.attributes.remove(name)
                    }
                    model = model.next
                }

                // add updated model to the plan item
                def planItem = conversation.plan.planItems[index]
                planItem.models.put(conversation.startDate, rootModel)

                params.newLineIndex = index
                params.template = 'review'

            }
            on("success").to("build")
        }

        /**
         * Updates the plan description and renders the review panel.
         */
        updatePlan {
            action {
                log.debug("updating plan details")

                bindData(conversation.plan, params, 'plan')
                bindData(conversation.product, params, 'product')

                // update default price for the current working start date
                def startDate = conversation.startDate
                def defaultPriceModel = PriceModelBL.getWsPriceForDate(conversation.product.defaultPrices, startDate)
                bindData(defaultPriceModel, params, 'price')

                log.debug("updating subscription product pricing for date ${startDate} = ${defaultPriceModel}")
                conversation.product.defaultPrices.put(startDate, defaultPriceModel)

                // sort prices by precedence
                conversation.plan.planItems = sortPlanItems(conversation.plan.planItems)

                params.template = 'review'
            }
            on("success").to("build")
        }

        /**
         * Shows the plan builder. This is the "waiting" state that branches out to the rest
         * of the flow. All AJAX actions and other states that build on the order should
         * return here when complete.
         *
         * If the parameter 'template' is set, then a partial view template will be rendered instead
         * of the complete 'build.gsp' page view (workaround for the lack of AJAX support in web-flow).
         */
        build {
            // list
            on("details").to("showDetails")
            on("products").to("showProducts")
            on("timeline").to("showTimeline")

            // pricing
            on("addDate").to("addDate")
            on("editDate").to("editDate")
            on("removeDate").to("removeDate")
            on("addPrice").to("addPrice")
            on("updatePrice").to("updatePrice")
            on("removePrice").to("removePrice")

            // pricing model
            on("updateStrategy").to("updateStrategy")
            on("addChainModel").to("addChainModel")
            on("removeChainModel").to("removeChainModel")
            on("addAttribute").to("addAttribute")
            on("removeAttribute").to("removeAttribute")

            // plan
            on("update").to("updatePlan")
            on("save").to("savePlan")
            on("cancel").to("finish")
        }

        /**
         * Saves the plan and exits the builder flow.
         */
        savePlan {
            action {
                try {
                
                    def plan = conversation.plan
                    def product = conversation.product

                    //to avoid spaces or tabs
                    product.number = product?.number?.trim()
                    product.description = product?.description?.trim()

                    if (!plan.id || plan.id == 0) {
                        if (SpringSecurityUtils.ifAllGranted("PLAN_60")) {

                            log.debug("creating plan subscription item ${product}")
                            
                            validateBundledQuantity(plan)
                            
                            product.id = plan.itemId = webServicesSession.createItem(product)
							
							log.debug("creating plan ${plan}")
                            plan.id = webServicesSession.createPlan(plan)

                            // set success message in session, contents of the flash scope doesn't survive
                            // the redirect to the order list when the web-flow finishes
                            session.message = 'plan.created'
                            session.args = [ plan.id ]

                        } else {
                            redirect controller: 'login', action: 'denied'
                            return
                        }

                    } else {
                        if (SpringSecurityUtils.ifAllGranted("PLAN_61")) {
                            log.debug("saving changes to plan subscription item ${product.id}")
                            
                            validateBundledQuantity(plan)
                            
                            webServicesSession.updateItem(product)
							
                            log.debug("saving changes to plan ${plan.id}")
                            webServicesSession.updatePlan(plan)

                            session.message = 'plan.updated'
                            session.args = [ plan.id ]


                        } else {
                            redirect controller: 'login', action: 'denied'
                            return
                        }
                    }

                } catch (SessionInternalError e) {
                    viewUtils.resolveException(flash, session.locale, e)
                    error()
                }
            }
            on("error").to("build")
            on("success").to("finish")
        }

        finish {
            redirect controller: 'plan', action: 'list', id: conversation.plan?.id
        }
    }
    
    private void validateBundledQuantity(PlanWS plan) throws SessionInternalError {
    	
    	boolean bundledQuantityMaxError = false;
    	String planItemDescription = "";
    	
    	for (PlanItemWS planItem : plan.getPlanItems()) {
    	
    		if (planItem != null && 
    			planItem.getBundle() != null && 
    			planItem.getBundle().getQuantity() != null) {
    			
    			planItemDescription = ItemDTO.get(planItem.itemId)?.description ?: "";
    				
    			if ((planItem.getBundle().getQuantity().contains(Constants.DECIMAL_POINT) && 
    				 planItem.getBundle().getQuantity().length() > 23) 
    					|| 
    				(!planItem.getBundle().getQuantity().contains(Constants.DECIMAL_POINT) && 
    				 planItem.getBundle().getQuantity().length() > 12)) {
	    			
    				bundledQuantityMaxError = true;
	    			break;
    			}
    			
    			try {
	    			if (new BigDecimal(planItem.getBundle().getQuantity()) >= new BigDecimal(1000000000000)) {
		    			bundledQuantityMaxError = true;
		    			break;
	    			}
    			} catch (Exception e) {
    				String []errors = new String[1];
		    		errors[0] = "PlanWS.planItem,bundledQuantity,validation.error.plan.planItem.bundledQuantity.not.numeric," + planItemDescription;
		    		throw new SessionInternalError("Bundled Quantity should be numeric.", errors);
    			}
    		}
    	}
    	
    	if (bundledQuantityMaxError) {
    		String []errors = new String[1];
    		errors[0] = "PlanWS.planItem,bundledQuantity,validation.error.plan.planItem.bundledQuantity.max.exceeded," + planItemDescription;
    		throw new SessionInternalError("Max Bundled Quantity can be upto 10^12.", errors);
    	}
    	
    }
}
