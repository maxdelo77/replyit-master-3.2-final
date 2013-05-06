
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

<g:set var="currency" value="${currencies.find{ it.id == invoice?.currencyId}}"/>

<div id="invoice-details">
    <!--  Invoice Details snapshot -->
    <div class="form-columns">
        <div class="column">
            <div class="row">
                <label><g:message code="invoice.label.id"/>:</label><span>${invoice.id}</span>

                <g:hiddenField name="id" value="${session['applyToInvoiceOrderId']}"/>
                <g:hiddenField name="userId" value="${invoice.userId}"/>
                <g:hiddenField name="invoice.id" value="${invoice.id}"/>
            </div>
            <div class="row"><label><g:message code="invoice.label.number"/>:</label><span>${invoice.number}</span></div>
            <div class="row"><label><g:message code="invoice.label.status"/>:</label><span>${invoice.statusDescr}</span></div>
            <div class="row"><label><g:message code="invoice.label.date"/>:</label>
                <span>
                    <g:formatDate date="${invoice?.createDateTime}" formatName="date.pretty.format"/>
                </span>
            </div>
            <div class="row"><label><g:message code="invoice.label.duedate"/>:</label>
                <span>
                    <g:formatDate date="${invoice?.dueDate}" formatName="date.pretty.format"/>
                </span>
            </div>

            <!-- meta fields -->
            <g:render template="/metaFields/editMetaFields" model="[ availableFields: availableMetaFields, fieldValues: invoice?.metaFields, fieldsArray: fieldsArray ]"/>
        </div>
    
        <div class="column">
            <div class="row"><label><g:message code="invoice.label.gen.date"/>:</label>
                <span>
                    <g:formatDate date="${invoice?.createTimeStamp}" formatName="date.pretty.format"/>
                </span>
            </div>
            <div class="row"><label><g:message code="invoice.label.amount"/>:</label>
                <span>
                    <g:formatNumber number="${new BigDecimal(invoice.total?: 0)}" type="currency" currencySymbol="${currency?.symbol}"/>
            </div>
            <div class="row"><label><g:message code="invoice.label.balance"/>:</label>
                <span>
                    <g:formatNumber number="${new BigDecimal(invoice.balance ?: 0)}" type="currency" currencySymbol="${currency?.symbol}"/>
                </span>
            </div>
            <div class="row"><label><g:message code="invoice.label.carried.bal"/>:</label>
                <span>
                    <g:formatNumber number="${new BigDecimal(invoice.carriedBalance ?: 0)}" type="currency" currencySymbol="${currency?.symbol}"/>
                </span>
            </div>
            
            <div class="row"><label><g:message code="invoice.label.payment.attempts"/>:</label><span>${invoice.paymentAttempts}</span></div>
            <div class="row"><label><g:message code="invoice.label.orders"/>:</label><span>
                <g:each var="order" in="${invoice.orders}">${order.toString()}&nbsp;</g:each></span>
            </div>
        </div>
    </div>
    
    <div class="btn-row">
        <a class="submit okay" onclick="$('#apply-form').submit()">
            <span><g:message code="order.button.apply"/></span></a>
        <a href="${createLink (controller: 'order', action: 'list', params: [id: session['applyToInvoiceOrderId']])}" class="submit cancel">
            <span><g:message code="button.cancel"/></span>
        </a>
    </div>
</div>