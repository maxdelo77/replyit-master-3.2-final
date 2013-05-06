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
  Payment data-table template for the customer inspector.

  @author Brian Cowdery
  @since  12-Jan-2011
--%>

<table class="dataTable" cellspacing="0" cellpadding="0">
    <tbody>
    <tr>
        <td><g:message code="payment.date"/></td>
        <td class="value"><g:formatDate date="${payment?.paymentDate ?: payment?.createDatetime}" formatName="date.pretty.format"/></td>

        <td><g:message code="payment.id"/></td>
        <td class="value"><g:link controller="payment" action="list" id="${payment?.id}">${payment?.id}</g:link></td>
    </tr>
    <tr>
        <td><g:message code="payment.amount"/></td>
        <td class="value"><g:formatNumber number="${payment?.amount}" type="currency" currencySymbol="${payment?.currency?.symbol}"/> &nbsp;</td>

        <td><g:message code="payment.balance"/></td>
        <td class="value"><g:formatNumber number="${payment?.balance}" type="currency" currencySymbol="${payment?.currency?.symbol}"/> &nbsp;</td>
    </tr>
    <tr>
        <td><g:message code="payment.result"/></td>
        <td class="value">${payment?.paymentResult.getDescription(session['language_id'])}</td>

        <td><g:message code="payment.attempt"/></td>
        <td class="value">${payment?.attempt ?: 0}</td>
    </tr>
    </tbody>
</table>