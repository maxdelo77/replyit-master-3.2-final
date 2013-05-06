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

<%@ page import="com.sapienter.jbilling.server.pricing.PriceModelBL; org.apache.commons.lang.WordUtils; com.sapienter.jbilling.server.item.db.PlanItemBundleDTO; com.sapienter.jbilling.server.item.db.ItemDTO" %>

<%--
  Renders an OrderLineWS as an editable row for the order builder preview pane.

  @author Brian Cowdery
  @since 24-Jan-2011
--%>

<g:set var="product" value="${ItemDTO.get(line.itemId)}"/>
<g:set var="quantityNumberFormat" value="${product?.hasDecimals ? 'money.format' : 'default.number.format'}"/>
<g:set var="editable" value="${index == params.int('newLineIndex')}"/>

<g:formRemote name="line-${index}-update-form" url="[action: 'edit']" update="column2" method="GET">
    <g:hiddenField name="_eventId" value="updateLine"/>
    <g:hiddenField name="execution" value="${flowExecutionKey}"/>

    <li id="line-${index}" class="line ${editable ? 'active' : ''}">
        <span class="description">
            ${line.description}
        </span>
        <span class="sub-total">
            <g:set var="subTotal"
                   value="${formatNumber(number: line.getAmountAsDecimal(), type: 'currency', currencySymbol: user.currency.symbol, maxFractionDigits: 4)}"/>
            <g:message code="order.review.line.total" args="[subTotal]"/>
        </span>
        <span class="qty-price">
            <g:set var="quantity"
                   value="${formatNumber(number: line.getQuantityAsDecimal(), formatName: quantityNumberFormat)}"/>
            <g:if test="${product?.percentage}">
                <g:set var="percentage" value="%${formatNumber(number: product.percentage)}"/>
                <g:message code="order.review.quantity.by.price" args="[quantity, percentage]"/>
            </g:if>
            <g:else>
                <g:set var="price" value="${formatNumber(number: line.getPriceAsDecimal(), type: 'currency', currencySymbol: user.currency.symbol, maxFractionDigits: 4)}"/>
                <g:message code="order.review.quantity.by.price" args="[quantity, price]"/>
            </g:else>
        </span>
        <div style="clear: both;"></div>
    </li>

    <li id="line-${index}-editor" class="editor ${editable ? 'open' : ''}">
        <div class="box">
            <div class="form-columns">

                <g:applyLayout name="form/input">
                    <content tag="label"><g:message code="order.label.quantity"/></content>
                    <content tag="label.for">line-${index}.quantityAsDecimal</content>
                    <g:textField name="line-${index}.quantityAsDecimal" class="field quantity" value="${formatNumber(number: line.getQuantityAsDecimal() ?: BigDecimal.ONE, formatName: quantityNumberFormat)}"/>
                </g:applyLayout>

                <sec:ifAllGranted roles="ORDER_26">
                    <g:applyLayout name="form/input">
                        <g:if test="${product?.percentage}">
                            <content tag="label"><g:message code="order.label.line.price.percentage"/></content>
                        </g:if>
                        <g:else>
                            <content tag="label"><g:message code="order.label.line.price"/></content>
                        </g:else>
                        <content tag="label.for">line-${index}.priceAsDecimal</content>
                        <g:textField name="line-${index}.priceAsDecimal" class="field price" value="${formatNumber(number: line.getPriceAsDecimal() ?: BigDecimal.ZERO, formatName: 'money.format', maxFractionDigits: 4)}" disabled="${line.useItem}"/>
                    </g:applyLayout>
                </sec:ifAllGranted>

                <sec:ifAllGranted roles="ORDER_27">
                    <g:applyLayout name="form/input">
                        <content tag="label"><g:message code="order.label.line.descr"/></content>
                        <content tag="label.for">line-${index}.description</content>
                        <g:textField name="line-${index}.description" class="field description" value="${line.description}" disabled="${line.useItem}"/>
                    </g:applyLayout>
                </sec:ifAllGranted>

                <sec:ifAnyGranted roles="ORDER_26, ORDER_27">
                    <g:applyLayout name="form/checkbox">
                        <content tag="label">
                            <sec:ifNotGranted roles="ORDER_26">
                                <g:message code="order.label.line.use.item.description"/>
                            </sec:ifNotGranted>

                            <sec:ifNotGranted roles="ORDER_27">
                                <g:message code="order.label.line.use.item.price"/>
                            </sec:ifNotGranted>

                            <sec:ifAllGranted roles="ORDER_26, ORDER_27">
                                <g:message code="order.label.line.use.item"/>
                            </sec:ifAllGranted>
                        </content>
                        <content tag="label.for">line-${index}.useItem</content>
                        <g:checkBox name="line-${index}.useItem" line="${index}" class="cb check" value="${line.useItem}" />

                        <script type="text/javascript">
                            $('#line-${index}\\.useItem').change(function() {
                                var line = $(this).attr('line');

                                if ($(this).is(':checked')) {
                                    $('#line-' + line + '\\.priceAsDecimal').prop('disabled', 'true');
                                    $('#line-' + line + '\\.description').prop('disabled', 'true');
                                } else {
                                    $('#line-' + line + '\\.priceAsDecimal').prop('disabled', '');
                                    $('#line-' + line + '\\.description').prop('disabled', '');
                                }
                            }).change();
                        </script>
                    </g:applyLayout>
                </sec:ifAnyGranted>

                <g:hiddenField name="index" value="${index}"/>
            </div>
        </div>

        <div class="btn-box">
            <a class="submit save" onclick="$('#line-${index}-update-form').submit();"><span><g:message
                    code="button.update"/></span></a>
            <g:remoteLink class="submit cancel" action="edit" params="[_eventId: 'removeLine', index: index]"
                          update="column2" method="GET">
                <span><g:message code="button.remove"/></span>
            </g:remoteLink>
        </div>
    </li>

    <g:if test="${product.plans}">
        <g:each var="plan" in="${product.plans}">
            <g:each var="planItem" in="${plan.planItems}">
                <g:set var="planItemPriceModel" value="${PriceModelBL.getPriceForDate(planItem.models, pricingDate)}"/>

                <g:if test="${planItem.bundle?.quantity}">
                    <li class="bundled">
                        <span class="description">
                            ${planItem.item.description}
                        </span>
                        <span class="included-qty">
                            + <g:formatNumber number="${planItem.bundle?.quantity}"/>
                            <g:if test="${planItem.bundle?.period}">
                                ${WordUtils.capitalize(planItem.bundle?.period?.getDescription(session['language_id'])?.toLowerCase())}
                            </g:if>
                            <g:if test="${planItem.bundle?.targetCustomer != PlanItemBundleDTO.Customer.SELF}">
                                <g:message
                                        code="bundle.for.target.customer.${planItem.bundle?.targetCustomer}"/>
                            </g:if>
                        </span>

                        <div class="clear">&nbsp;</div>
                    </li>

                    <li class="bundled-price">
                        <table class="dataTable" cellspacing="0" cellpadding="0" width="100%">
                            <tbody>
                               <g:render template="/plan/priceModel" model="[model: planItemPriceModel]"/>
                            </tbody>
                        </table>
                    </li>
                </g:if>
            </g:each>
        </g:each>
    </g:if>

</g:formRemote>