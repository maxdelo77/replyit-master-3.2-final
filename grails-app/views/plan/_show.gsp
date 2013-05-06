%{--
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
  --}%

<%@ page import="com.sapienter.jbilling.server.util.Util"%>

<%--
  Plan details template.

  @author Brian Cowdery
  @since  1-Feb-2010
--%>

<div class="column-hold">
    <div class="heading">
	    <strong>${plan.item.internalNumber}</strong>
	</div>

    <!-- plan details -->
	<div class="box">
        <div class="sub-box">
            <table class="dataTable" cellspacing="0" cellpadding="0">
                <tbody>
                    <tr>
                        <td><g:message code="plan.id"/></td>
                        <td class="value">${plan.id}</td>
                    </tr>
                    <tr>
                        <td><g:message code="plan.item.internal.number"/></td>
                        <td class="value">${plan.item.internalNumber}</td>
                    </tr>
                    <tr>
                        <td><g:message code="plan.item.description"/></td>
                        <td class="value">${plan.item.description}</td>
                    </tr>
                    <tr>
                        <td><g:message code="order.label.period"/></td>
                        <td class="value">${plan.period.getDescription(session['language_id'])}</td>
                    </tr>
    
                    <g:if test="${plan.item.defaultPrices}">
                        <g:set var="price" value="${plan.item.getPrice(new Date())}"/>
                        <tr>
                            <td>${price.currency.code}</td>
                            <td class="value">
                                <g:formatNumber number="${price.rate}" type="currency" currencySymbol="${price.currency.symbol}"/>
                            </td>
                        </tr>
                    </g:if>
                </tbody>
            </table>
    
            <p class="description">
                ${plan.description}
            </p>
        </div>
    </div>

    <!-- plan prices -->
    <g:if test="${plan.planItems}">
    <div class="heading">
        <strong><g:message code="builder.products.title"/></strong>
    </div>
    <div class="box">
        <div class="sub-box"><table class="dataTable" cellspacing="0" cellpadding="0" width="100%">
            <tbody>

            <g:each var="planItem" status="index" in="${plan.planItems.sort{ it.precedence }}">

                <tr>
                    <td><g:message code="product.internal.number"/></td>
                    <td class="value" colspan="3">
                        <sec:access url="/product/show">
                            <g:remoteLink controller="product" action="show" id="${planItem.item.id}" params="[template: 'show']" before="register(this);" onSuccess="render(data, next);">
                                ${planItem.item.internalNumber}
                            </g:remoteLink>
                        </sec:access>
                        <sec:noAccess url="/product/show">
                            ${planItem.item.internalNumber}
                        </sec:noAccess>
                    </td>
                </tr>
                <tr>
                    <td><g:message code="product.description"/></td>
                    <td class="value" colspan="3">
                        ${planItem.item.getDescription(session['language_id'])}
                    </td>
                </tr>

                <tr>
                    <td><g:message code="plan.item.precedence"/></td>
                    <td class="value">${planItem.precedence}</td>
                </tr>

                <g:if test="${planItem.bundle && planItem.bundle.quantity?.compareTo(BigDecimal.ZERO) > 0}">
                    <tr>
                        <td><g:message code="plan.item.bundled.quantity"/></td>
                        <td class="value"><g:formatNumber number="${planItem.bundle.quantity}"/></td>

                        <td><g:message code="plan.bundle.label.add.if.exists"/></td>
                        <td class="value"><g:message code="plan.bundle.add.if.exists.${planItem.bundle.addIfExists}"/></td>
                    </tr>

                    <tr>
                        <td><g:message code="plan.bundle.period"/></td>
                        <td class="value">${planItem.bundle.period.getDescription(session['language_id'])}</td>

                        <td><g:message code="plan.bundle.target.customer"/></td>
                        <td class="value"><g:message code="bundle.target.customer.${planItem.bundle.targetCustomer}"/></td>
                    </tr>

                </g:if>

                <!-- price model -->
                <tr><td colspan="4">&nbsp;</td></tr>
                <g:render template="/plan/priceModel" model="[model: planItem.getPrice(new Date())]"/>


                <!-- separator line -->
                <g:if test="${index < plan.planItems.size()-1}">
                    <tr><td colspan="4"><hr/></td></tr>
                </g:if>
            </g:each>

            </tbody>
        </table>
    </div>
    </div>
    </g:if>

    <div class="btn-box">
        <sec:ifAllGranted roles="PLAN_61">
            <g:link controller="planBuilder" action="edit" id="${plan.id}" class="submit edit"><span><g:message code="button.edit"/></span></g:link>
        </sec:ifAllGranted>

        <sec:ifAllGranted roles="PLAN_62">
            <a onclick="showConfirm('delete-${plan.id}');" class="submit delete"><span><g:message code="button.delete"/></span></a>
        </sec:ifAllGranted>
    </div>

    <g:render template="/confirm"
              model="['message': 'plan.delete.confirm',
                      'controller': 'plan',
                      'action': 'delete',
                      'id': plan.id,
                      'ajax': true,
                      'update': 'column1',
                      'onYes': 'closePanel($(\'#column2\'))'
                     ]"/>
</div>

