<%@ page import="com.sapienter.jbilling.server.pricing.PriceModelBL" %>

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

<%--
  Shows a list of item pricing strategies and attributes.

  @author Brian Cowdery
  @since 28-Feb-2011
--%>
<g:if test="${product}">
    <div class="heading">
        <strong><g:message code="customer.inspect.default.price.title"/></strong>
    </div>
    <div class="box">
        <div class="sub-box">
            <table class="dataTable" cellspacing="0" cellpadding="0" width="100%">
                <tbody>
                    <tr>
                        <td><g:message code="product.internal.number"/></td>
                        <td class="value" colspan="3">
                            <g:link controller="product" action="list" id="${product.id}">
                                ${product.internalNumber}
                            </g:link>
                        </td>
                    </tr>

                    <tr>
                        <td><g:message code="product.description"/></td>
                        <td class="value" colspan="3">
                            ${product.getDescription(session['language_id'])}
                        </td>
                    </tr>

                    <!-- price model -->
                    <tr><td colspan="4">&nbsp;</td></tr>
                    <g:render template="/plan/priceModel" model="[model: product.getPrice(new Date())]"/>
                </tbody>
            </table>
        </div>
    </div>
</g:if>

<div class="heading">
    <g:if test="${product}">
        <strong>
            <g:message code="customer.inspect.customer.prices.title"/>
        </strong>
    </g:if>
    <g:else>
        <strong><g:message code="customer.inspect.prices.all.title"/></strong>
    </g:else>
</div>

<div class="box">
    <div class="sub-box">
        <g:if test="${prices}">
    
            <table class="dataTable" cellspacing="0" cellpadding="0" width="100%">
                <tbody>
    
                <g:each var="price" status="index" in="${prices?.sort{ it.precedence }}">
                    <tr>
                        <td><g:message code="product.internal.number"/></td>
                        <td class="value" colspan="2">
                            <g:link controller="product" action="list" id="${price.item.id}">
                                ${price.item.internalNumber}
                            </g:link>
                        </td>
                        <td class="right">
                            <g:if test="${!price.plan}">
                                <!-- delete customer-specific price -->
                                <g:remoteLink action="deleteCustomerPrice" id="${price.id}" params="[userId: user?.id ?: userId, itemId: price.item.id]" update="prices-column">
                                    <img src="${resource(dir:'images', file:'cross.png')}" alt="remove"/>
                                </g:remoteLink>
                            </g:if>
                        </td>
                    </tr>
    
                    <tr>
                        <td><g:message code="product.description"/></td>
                        <td class="value" colspan="3">
                            ${price.item.getDescription(session['language_id'])}
                        </td>
                    </tr>
    
                    <tr>
                        <td><g:message code="plan.item.precedence"/></td>
                        <td class="value" colspan="3">
                            ${price.precedence}
                        </td>
                    </tr>
    
                    <!-- price model -->
                    <tr><td colspan="4">&nbsp;</td></tr>
                    <g:render template="/plan/priceModel" model="[model: PriceModelBL.getPriceForDate(price.models, new Date())]"/>
    
                    <!-- separator line -->
                    <g:if test="${index < prices.size()-1}">
                        <tr><td colspan="4"><hr/></td></tr>
                    </g:if>
                </g:each>
    
                </tbody>
            </table>
    
        </g:if>
        <g:else>
            <em><g:message code="customer.inspect.no.prices"/></em>
        </g:else>
    </div>
</div>

<div class="btn-box">
    <g:if test="${product}">
        <g:link class="submit add" action="editCustomerPrice" params="[userId: user?.id ?: userId, itemId: product.id]">
            <span><g:message code="button.add.customer.price"/></span>
        </g:link>

        <g:remoteLink action="allProductPrices" update="prices-column" params="[userId: user?.id ?: userId]" class="submit show">
            <span><g:message code="button.show.all"/></span>
        </g:remoteLink>
    </g:if>
</div>
