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

<%@ page import="com.sapienter.jbilling.common.Constants" contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>

    <r:script disposition="head">

        function clearInvoiceSelection() {
            $(':input[type=radio][name=invoiceId]').prop('checked','');
            $("#invoice-details").replaceWith('<div id="invoice-details" class="box-card-hold"></div>')
        }

        function onInvoiceChange(invId) {
            $.ajax({
                url: "${createLink(controller: 'invoice', action: 'snapshot')}/" + invId,
                global: false,
                success: function(data) { $("#invoice-details").replaceWith(data) }
            });
            $('#invoice-details').visibility='visible';
        }

        $(document).ready(function() {
            //radio select or change
            $(':input[type=radio][name=invoiceId]').change(function() {
                //alert('Selected Invoice ID ' + $(this).val());
                $.ajax({
                    url: "${createLink(controller: 'invoice', action: 'snapshot')}/" + $(this).val(),
                    global: false,
                    success: function(data) { $("#invoice-details").replaceWith(data) }
                });
                $('#invoice-details').visibility='visible';
            });
        });
    </r:script>
</head>
<body>
<div class="form-edit">

    <div class="heading">
        <strong>
            <g:message code="order.label.apply.to.invoice" args="[params.id]"/>
        </strong>
    </div>

    <div class="form-hold">
        <g:form name="order-invoice-form" action="confirm">
            <fieldset>
            <!-- invoices to pay -->
                <g:if test="${invoices}">
                    <div class="box-card-hold">
                        <table cellpadding="0" cellspacing="0" class="innerTable">
                            <thead class="innerHeader">
                            <tr>
                                <th><g:message code="invoice.label.number"/></th>
                                <%-- <th><g:message code="invoice.label.payment.attempts"/></th>  --%>
                                <th><g:message code="invoice.label.total"/></th>
                                <th><g:message code="invoice.label.balance"/></th>
                                <th><g:message code="invoice.label.duedate"/></th>
                                <th><!-- action --> &nbsp;</th>
                            </tr>
                            </thead>
                            <tbody>
                            <g:each var="invoice" in="${invoices}">
                                <g:if test="${!invoice.orders.find{ it == orderId as Integer}}">
                                    <g:set var="currency" value="${currencies.find { it.id == invoice.currencyId }}"/>

                                    <tr>
                                        <td class="innerContent">
                                            <g:applyLayout name="form/radio">
                                                <g:radio id="invoice-${invoice.id}" name="invoiceId" value="${invoice.id}" checked="${invoice.id == invoiceId || invoice.id == params.int('invoice.id')}"/>
                                                <label for="invoice-${invoice.id}" class="rb">
                                                    <g:message code= "payment.link.invoice" args="[invoice.number]"/>
                                                </label>
                                            </g:applyLayout>
                                        </td><%--
                                    <td class="innerContent">
                                        ${invoice.paymentAttempts}
                                    </td> --%>
                                        <td class="innerContent">
                                            <g:formatNumber number="${invoice.getTotalAsDecimal()}" type="currency" currencyCode="${currency.code}"/>
                                            <g:hiddenField name="invoice-${invoice.id}-amount" value="${formatNumber(number: invoice.total, formatName: 'money.format')}"/>
                                        </td>
                                        <td class="innerContent">
                                            <g:formatNumber number="${invoice.getBalanceAsDecimal()}" type="currency" currencyCode="${currency.code}"/>
                                            <g:hiddenField name="invoice-${invoice.id}-balance" value="${formatNumber(number: invoice.balance, formatName: 'money.format')}"/>
                                        </td>
                                        <td class="innerContent">
                                            <g:formatDate date="${invoice.dueDate}"/>
                                        </td>
                                        <td class="innerContent">
                                            <g:link controller="invoice" action="list" id="${invoice.id}">
                                                <g:message code= "payment.link.view.invoice" args="[invoice.number]"/>
                                            </g:link>
                                        </td>
                                    </tr>
                                </g:if>
                            </g:each>
                            </tbody>
                        </table>

                        <div class="btn-row">
                            <a onclick="clearInvoiceSelection();" class="submit delete"><span><g:message code="button.clear"/></span></a>
                        </div>

                    </div>
                </g:if>
            </fieldset>
        </g:form>

        <g:form name="apply-form" controller="order" action="apply">
            <!-- space for invoice details, populated with an ajax call -->
            <div id="invoice-details" style="${!invoice ? 'visibility: hidden' : ''}" class="box-card-hold">
                <g:if test="${invoice}">
                    <g:render template="/invoice/snapshot" model="[invoice: invoice, currencies: currencies, availableMetaFields: availableMetaFields, fieldsArray: fieldsArray ]"/>
                </g:if>
            </div>
        </g:form>

    </div>
</div>

</body>
</html>
