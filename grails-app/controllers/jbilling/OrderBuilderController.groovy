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
import com.sapienter.jbilling.server.item.db.ItemDTO
import com.sapienter.jbilling.server.user.db.CompanyDTO
import com.sapienter.jbilling.server.order.OrderWS
import com.sapienter.jbilling.server.user.db.UserDTO

import com.sapienter.jbilling.server.order.db.OrderPeriodDTO
import com.sapienter.jbilling.server.order.db.OrderBillingTypeDTO
import com.sapienter.jbilling.server.util.Constants
import com.sapienter.jbilling.server.user.contact.db.ContactDTO
import com.sapienter.jbilling.server.order.OrderLineWS
import com.sapienter.jbilling.common.SessionInternalError
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

import com.sapienter.jbilling.server.order.db.OrderStatusDTO
import java.math.RoundingMode
import com.sapienter.jbilling.server.process.db.PeriodUnitDTO
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.apache.commons.lang.StringUtils
import com.sapienter.jbilling.server.item.CurrencyBL
import com.sapienter.jbilling.server.metafields.db.EntityType
import com.sapienter.jbilling.server.metafields.MetaFieldBL
import com.sapienter.jbilling.server.metafields.db.MetaField
import com.sapienter.jbilling.server.metafields.MetaFieldValueWS
import com.sapienter.jbilling.server.metafields.db.DataType
import com.sapienter.jbilling.client.metafield.MetaFieldUtils

/**
 * OrderController
 *
 * @author Brian Cowdery
 * @since 20-Jan-2011
 */
@Secured(["hasAnyRole('ORDER_20', 'ORDER_21')"])
class OrderBuilderController {

    def webServicesSession
    def viewUtils

    def breadcrumbService
    def productService

    def index = {
        redirect action: 'edit'
    }

    /**
     * Returns the plan contained in the given order lines.
     *
     * @param lines order lines
     * @param plans plan items
     * @return matching plan if order lines contains plan item, null if no plan found
     */
    def subscribedPlan(lines, plans) {
        for (OrderLineWS line : lines) {
            def planItem = plans.find{ it.id == line.getItemId() }
            //considering plan item which is not deleted from the order line
            if (planItem && line.getDeleted()==0) {
                return planItem.plans.asList()?.first()
            }
        }

        return null;
    }

    def editFlow = {
        /**
         * Initializes the order builder, putting necessary data into the flow and conversation
         * contexts so that it can be referenced later.
         */
        initialize {
            action {
                if (!params.id && !SpringSecurityUtils.ifAllGranted("ORDER_20")) {
                    // not allowed to create
                    redirect controller: 'login', action: 'denied'
                    return
                }

                if (params.id && !SpringSecurityUtils.ifAllGranted("ORDER_21")) {
                    // not allowed to edit
                    redirect controller: 'login', action: 'denied'
                    return
                }

                def order = params.id ? webServicesSession.getOrder(params.int('id')) : new OrderWS()

                if (!order) {
                    log.error("Could not fetch WS object")
                    orderNotFoundErrorRedirect(params.id)
                    return
                }
                
                if (order?.deleted==1) {
                	log.error("order is deleted, redirect to list")
                    orderNotFoundErrorRedirect(params.id)
                    return
                }

                def user = UserDTO.get(order?.userId ?: params.int('userId'))
                def contact = ContactDTO.findByUserId(order?.userId ?: user.id)

                def company = CompanyDTO.get(session['company_id'])
                def currencies =  new CurrencyBL().getCurrenciesWithoutRates(session['language_id'].toInteger(), session['company_id'].toInteger(),true)


                // set sensible defaults for new orders
                if (!order.id || order.id == 0) {
                    order.userId        = user.id
                    order.currencyId    = (currencies.find { it.id == user.currency.id} ?: company.currency).id
                    order.statusId      = Constants.ORDER_STATUS_ACTIVE
                    order.period        = Constants.ORDER_PERIOD_ONCE
                    order.billingTypeId = Constants.ORDER_BILLING_POST_PAID
                    order.activeSince   = new Date()
                    order.orderLines    = []
                }

                // add breadcrumb for order editing
                if (params.id) {
                    breadcrumbService.addBreadcrumb(controllerName, actionName, null, params.int('id'))
                }

                // available order periods, statuses and order types
                def itemTypes = productService.getItemTypes()
                def orderStatuses = OrderStatusDTO.list().findAll { it.id != Constants.ORDER_STATUS_SUSPENDED_AGEING }
                def orderPeriods = company.orderPeriods.collect { new OrderPeriodDTO(it.id) } << new OrderPeriodDTO(Constants.ORDER_PERIOD_ONCE)
                orderPeriods.sort { it.id }
                def periodUnits = PeriodUnitDTO.list()

                def orderBillingTypes = [
                        new OrderBillingTypeDTO(Constants.ORDER_BILLING_PRE_PAID),
                        new OrderBillingTypeDTO(Constants.ORDER_BILLING_POST_PAID)
                ]

                // model scope for this flow
                flow.company = company
                flow.itemTypes = itemTypes
                flow.orderStatuses = orderStatuses
                flow.orderPeriods = orderPeriods
                flow.periodUnits = periodUnits
                flow.orderBillingTypes = orderBillingTypes
                flow.user = user
                flow.contact = contact

                // conversation scope
                conversation.order = order
                conversation.products = productService.getFilteredProducts(company, params, false)
                conversation.plans = productService.getFilteredPlans(company, params)
                conversation.availableFields = getAvailableMetaFields()
                conversation.deletedLines = []
                conversation.pricingDate = order.activeSince ?: order.createDate ?: new Date()
            }
            on("success").to("build")
        }

        /**
         * Renders the order details tab panel.
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
                if (params['product.typeId'] == null && flow.itemTypes)
                    params['product.typeId'] = flow.itemTypes?.asList()?.first()?.id

                params.template = 'products'
                conversation.products = productService.getFilteredProducts(flow.company, params, false)
            }
            on("success").to("build")
        }

        /**
         * Renders the plans list tab panel, filtering the plans list by the given criteria.
         */
        showPlans {
            action {
                params.template = 'plans'
                conversation.plans = productService.getFilteredPlans(flow.company, params)
            }
            on("success").to("build")
        }


        /**
         * Adds a product to the order as a new order line and renders the review panel.
         */
        addOrderLine {
            action {

                // build line
                def line = new OrderLineWS()
                line.typeId = Constants.ORDER_LINE_TYPE_ITEM
                line.quantity = BigDecimal.ONE
                line.itemId = params.int('id')
                line.useItem = true

                // add line to order
                def order = conversation.order
                def lines = order.orderLines as List
                lines.add(line)
                order.orderLines = lines.toArray()

                // rate order
                if (order.orderLines) {
                    try {
                        order = webServicesSession.rateOrder(order)
                    } catch (SessionInternalError e) {
                        viewUtils.resolveException(flow, session.locale, e)
                    }
                }
                conversation.order = order

                params.newLineIndex = lines.size() - 1
                params.template = 'review'
            }
            on("success").to("build")
        }

        /**
         * Adds a plan to the order as a new order line and renders the review panel.
         *
         * The builder does not allow multiple plans to be added to the same order. If the order
         * already contains a plan subscription item, an error message will be displayed.
         */
        addOrderPlanLine {
            action {
                def order = conversation.order
                def plans = conversation.plans

                if (!subscribedPlan(order.orderLines as List, plans)) {
                    // update the order to reflect the subscribed plan
                    def itemId = params.int('id')
                    def plan = plans.find{ it.id == itemId }?.plans?.asList()?.first()

                    // force order period to the plan's period
                    order.period = plan.period.id

                } else {
                    flow.errorMessages = [ message(code: 'validation.error.multiple.plans') ]
                    params.template = 'review'
                    error()
                }
            }
            on("success").to("addOrderLine")
            on("error").to("build")
        }

        /**
         * Updates an order line  and renders the review panel.
         */
        updateOrderLine {
            action {
				flash.errorMessages = null
				flash.error = null
                def order = conversation.order

                // get existing line
                def index = params.int('index')
                def line = order.orderLines[index]

                // update line
                bindData(line, params["line-${index}"])

                // must have a quantity
                if (!line.quantity) {
                    line.quantity = BigDecimal.ONE
                }

                // if product does not support decimals, drop scale of the given quantity
                boolean isPlan = false;
                def product = conversation.products?.find{ it.id == line.itemId }
                if (!product) {
                    product = conversation.plans?.find{ it.id == line.itemId }
                    isPlan = true;
                }
                if(isPlan && line.quantityAsDecimal.remainder(BigDecimal.ONE) > 0){
                    try {
                        String [] errors = ["OrderLineWS,planQuantity,bean.OrderLineWS.validation.error.partial.quantity"]
                        throw new SessionInternalError("Partial quantity is not allowed for a plan.",
                                errors);
                    } catch (SessionInternalError e) {
                        viewUtils.resolveException(flash, session.local, e)
                    }
                } else if (product?.hasDecimals == 0) {
                    line.quantity = line.getQuantityAsDecimal().setScale(0, RoundingMode.HALF_UP)
                }

                // existing line that's stored in the database will be deleted when quantity == 0
                if (line.quantityAsDecimal == BigDecimal.ZERO) {
                    line.useItem = false
                }

                // add line to order
                order.orderLines[index] = line

                // rate order
                if (order.orderLines) {
                    try {
                        order = webServicesSession.rateOrder(order)
                    } catch (SessionInternalError e) {
                        viewUtils.resolveException(flow, session.locale, e)
                    }
                }

                // In case of a single order line having quantity set to zero, total of the order should be zero
                if(order.orderLines.size()==1 && order.orderLines[0].quantity=='0'){
                    order.total = BigDecimal.ZERO
                }

                // sort order lines
                order.orderLines = order.orderLines.sort { it.itemId }
                conversation.order = order

                params.template = 'review'
            }
            on("success").to("build")
        }

        /**
         * Removes a line from the order and renders the review panel.
         */
        removeOrderLine {
            action {
                def order = conversation.order

                def index = params.int('index')
                def lines = order.orderLines as List

                // remove or mark as deleted if already saved to the DB
                def line = lines.get(index)
                if (line.id != 0) {
                    log.debug("marking line ${line.id} to be deleted.")
                    line.deleted = 1
                    conversation.deletedLines << line

                } else {
                    log.debug("removing transient line from order.")
                    lines.remove(index)
                }

                order.orderLines = lines.toArray()

                // rate order
                if (order.orderLines) {
                    try {
                        order = webServicesSession.rateOrder(order)
                    } catch (SessionInternalError e) {
                        viewUtils.resolveException(flow, session.locale, e)
                    }
                } else {
                    order.setTotal(new BigDecimal(0))
                }

                conversation.order = order

                params.template = 'review'
            }
            on("success").to("build")
        }

        /**
         * Updates order attributes (period, billing type, active dates etc.) and
         * renders the order review panel.
         */
        updateOrder {
            action {
                def order = conversation.order
                bindData(order, params)

                bindMetaFields(order, params);

                order.notify = params.notify ? 1 : 0
                order.ownInvoice = params.ownInvoice ? 1 : 0
                order.notesInInvoice = params.notesInInvoice ? 1 : 0

                // update the order to reflect the subscribed plan
                def subscribedPlan = subscribedPlan(order.orderLines as List, conversation.plans);
                if (subscribedPlan) {
                    order.period = subscribedPlan.period.id
                }

                // one time orders are ALWAYS post-paid
                if (order.period == Constants.ORDER_PERIOD_ONCE)
                    order.billingTypeId = Constants.ORDER_BILLING_POST_PAID

                // rate order
                if (order.orderLines) {
                    try {
                        order = webServicesSession.rateOrder(order)
                    } catch (SessionInternalError e) {
                        viewUtils.resolveException(flow, session.locale, e)
                    }
                }

                // sort order lines
                order.orderLines = order.orderLines.sort { it.itemId }
                conversation.order = order

                params.template = 'review'
            }
            on("success").to("build")
        }

        /**
         * Shows the order builder. This is the "waiting" state that branches out to the rest
         * of the flow. All AJAX actions and other states that build on the order should
         * return here when complete.
         *
         * If the parameter 'template' is set, then a partial view template will be rendered instead
         * of the complete 'build.gsp' page view (workaround for the lack of AJAX support in web-flow).
         */
        build {
            on("details").to("showDetails")
            on("products").to("showProducts")
            on("plans").to("showPlans")
            on("addLine").to("addOrderLine")
            on("addPlan").to("addOrderPlanLine")
            on("updateLine").to("updateOrderLine")
            on("removeLine").to("removeOrderLine")
            on("update").to("updateOrder")

            on("save").to("saveOrder")
            // on("save").to("checkItem")  // check to see if an item exists, and show an information page before saving
            // on("save").to("beforeSave") // show an information page before saving

            on("cancel").to("finish")
        }

        /**
         * Example action that shows a static page before saving if the order contains
         * a Lemonade item.
         *
         * Uncomment the "save" to "checkItem" transition in the builder() state to use.
         */
        checkItem {
            action {
                def order = conversation.order
                if (order.orderLines.find{ it.itemId == 2602}) {
                    // order contains lemonade, show beforeSave.gsp
                    hasItem();
                } else {
                    // order does not contain lemonade, goto save
                    save();
                }
            }
            on("hasItem").to("beforeSave")
            on("save").to("saveOrder")
        }

        /**
         * Example action that shows a static page before the order is saved.
         *
         * Uncomment the "save" to "beforeSave" transition in the builder() state to use.
         */
        beforeSave {
            on("save").to("saveOrder")
            on("cancel").to("build")
        }

        /**
         * Saves the order and exits the builder flow.
         */
        saveOrder {
            action {
                try {
                    def order = conversation.order

                    if (!order.id || order.id == 0) {
                        if (SpringSecurityUtils.ifAllGranted("ORDER_20")) {

                            log.debug("creating order ${order}")
                            order.id = webServicesSession.createOrder(order)

                            // set success message in session, contents of the flash scope doesn't survive
                            // the redirect to the order list when the web-flow finishes
                            session.message = 'order.created'
                            session.args = [ order.id, order.userId ]

                        } else {
                            redirect controller: 'login', action: 'denied'
                        }

                    } else {
                        if (SpringSecurityUtils.ifAllGranted("ORDER_21")) {

                            // add deleted lines to our order so that updateOrder() can save them
                            def deletedLines = conversation.deletedLines
                            def lines = order.orderLines as List

                            log.debug "appending ${deletedLines.size()} line(s) for deletion."
                            lines.addAll(deletedLines)
                            order.orderLines = lines.toArray()

                            // save changes
                            log.debug("saving changes to order ${order.id}")
                            webServicesSession.updateOrder(order)

                            session.message = 'order.updated'
                            session.args = [ order.id, order.userId ]

                        } else {
                            redirect controller: 'login', action: 'denied'
                        }
                    }

                } catch (SessionInternalError e) {
                    viewUtils.resolveException(flow, session.locale, e)
                    error()
                }
            }
            on("error").to("build")
            on("success").to("finish")
        }

        finish {
            redirect controller: 'order', action: 'list', id: conversation.order?.id
        }
    }

	private void orderNotFoundErrorRedirect(orderId) {
		session.error = 'order.not.found'
        session.args = [ orderId as String ]
		redirect controller: 'order', action: 'list'		
	}

    def getAvailableMetaFields() {
        return MetaFieldBL.getAvailableFieldsList(session['company_id'], EntityType.ORDER);
    }

    def bindMetaFields(orderWS, params) {
        def fieldsArray = MetaFieldUtils.bindMetaFields(availableMetaFields, params);
        orderWS.metaFields = fieldsArray.toArray(new MetaFieldValueWS[fieldsArray.size()])
    }
}
