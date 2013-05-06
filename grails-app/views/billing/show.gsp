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

<%@ page import="com.sapienter.jbilling.common.CommonConstants" %>
<html>
<head>
	<meta name="layout" content="main"/>
</head>

<body>
    <div class="holder">
        <div class="form-box-1">
        	<em>
	            <g:message code="billing.details.label.process.period"/>&nbsp;
	            <strong>${formattedPeriod}</strong>
	            <g:message code="billing.details.label.process.period.separator"/>&nbsp;
	            <g:message code="billing.details.label.process.period.start"/>&nbsp;
	            <strong><g:formatDate date="${process.billingDate}" formatName="date.pretty.format"/>&nbsp;</strong>
	            <g:message code="billing.details.label.process.period.end"/>&nbsp;
	            <strong><g:formatDate date="${process.billingPeriodEndDate}" formatName="date.pretty.format"/></strong>
            </em>
        </div>

        <div class="form-box ${process?.isReview == 0 ? 'last' : ''}">
            <g:link action="showOrders" id="${process?.id}" class="submit apply"><span>Show Orders</span></g:link>
            <g:link action="showInvoices" id="${process?.id}" params="[isReview: process?.isReview]" class="submit show"><span>Show Invoices</span></g:link>
        </div>

        <g:if test="${process?.isReview == 1}">
            <div class="form-box last">
                <sec:access url="/billing/approve">
                    <a onclick="showConfirm('approve-'+${process?.id});" class="submit apply">
                        <span><g:message code="billing.details.approve"/></span>
                    </a>
                </sec:access>

                <sec:access url="/billing/disapprove">
                    <a onclick="showConfirm('disapprove-'+${process?.id});" class="submit cancel">
                        <span><g:message code="billing.details.disapprove"/></span>
                    </a>
                </sec:access>
            </div>
        </g:if>
    </div>

    <g:if test="${process?.isReview == 1}">
        <div class="holder">
            <p><g:message code="billing.details.is.review"/></p>
        </div>
    </g:if>

    <!-- main billing run -->
    <div class="table-info">
        <em><g:message code="billing.details.label.process.id"/> <strong> ${process?.id}</strong></em>
        <em><g:message code="billing.details.label.orders.processed"/> <strong>${process?.orderProcesses?.size()}</strong></em>
        <em><g:message code="billing.details.label.invoices.generated"/> <strong>${invoices.size()}</strong></em>
        <em><g:message code="billing.details.label.payments.generated"/> <strong>${generatedPayments.size()}</strong></em>

        <g:if test="${process?.isReview == 1}">
            <em>
                <g:message code="billing.details.label.review.status"/>
                <strong>
                    <g:if test="${ CommonConstants.REVIEW_STATUS_GENERATED.intValue() == configuration?.reviewStatus}">
                        <g:message code="billing.details.review.generated"/>
                    </g:if>
                    <g:if test="${ CommonConstants.REVIEW_STATUS_APPROVED.intValue() == configuration?.reviewStatus}">
                        <g:message code="billing.details.review.approved"/>
                    </g:if>
                    <g:if test="${ CommonConstants.REVIEW_STATUS_DISAPPROVED.intValue() == configuration?.reviewStatus}">
                        <g:message code="billing.details.review.disapproved"/>
                    </g:if>
                </strong>
            </em>
        </g:if>
    </div>

    <div class="table-area">
    <table>
        <thead>
            <tr>
                <td class="first"><g:message code="billing.details.label.start.date"/></td>
                <td><g:message code="billing.details.label.end.date"/></td>
                <td><g:message code="billing.details.label.process.result"/></td>
                <td><g:message code="billing.details.label.total.invoiced"/></td>
                <td><g:message code="billing.details.label.total.paid"/></td>
                <td class="last"><g:message code="billing.details.label.total.not.paid"/></td>
            </tr>
        </thead>
        <tbody>
            <!-- process summary -->
            <tr>
                <td class="col02"><g:formatDate date="${processRun?.started}" formatName="date.time.format"/></td>
                <td><g:formatDate date="${processRun?.finished}" formatName="date.time.format"/></td>
                <td>${processRun?.status?.getDescription(session['language_id'])}</td>
                <td>
                    <%
                        def invoiced = [:]
                        invoices.each { invoice ->
                            invoiced[invoice.currency] = invoiced.get(invoice.currency, BigDecimal.ZERO).add(invoice.total)
                        }
                    %>
                    <g:each var="total" in="${invoiced.entrySet()}">
                        <g:formatNumber number="${total.value}" type="currency" currencySymbol="${total.key.symbol}"/> <br/>
                    </g:each>
                </td>
                <td>
                    <%
                        def generatedPaymentMethods = [:]
                        generatedPayments.each { payment ->
                            def currencies = generatedPaymentMethods.get(payment.paymentMethod, [:])
                            currencies[payment.currency] = currencies.get(payment.currency, BigDecimal.ZERO).add(payment.amount)
                            generatedPaymentMethods[payment.paymentMethod] = currencies
                        }
                    %>

                    <g:each var="paymentMethod" in="${generatedPaymentMethods.entrySet()}">
                        <g:each var="paymentCurrency" in="${paymentMethod.value}">
                            <div>
                                <span class="small">
                                    <g:formatNumber number="${paymentCurrency.value}" type="currency" currencySymbol="${paymentCurrency.key.symbol}"/>
                                </span>
                                <span class="small">
                                    ${paymentMethod.key.getDescription(session['language_id'])} <br/>
                                </span>
                            </div>
                        </g:each>
                    </g:each>
                </td>
                <td>
                    <%
                        def paid = [:]
                        generatedPayments.each { payment ->
                            paid[payment.currency] = paid.get(payment.currency, BigDecimal.ZERO).add(payment.amount)
                        }

                        def unpaid = invoiced.clone()
                        paid.each { currency, amount ->
                            unpaid[currency] = unpaid.get(currency, BigDecimal.ZERO).subtract(amount)
                        }
                    %>
                    <g:each var="amount" in="${unpaid.entrySet()}">
                        <g:formatNumber number="${amount.value}" type="currency" currencySymbol="${amount.key.symbol}"/><br/>
                    </g:each>
                </td>
            </tr>

            <!-- grand totals -->
            <tr class="bg">
                <td class="col02"></td>
                <td></td>
                <td></td>
                <td>
                    <g:each var="amount" in="${invoiced.entrySet()}">
                        <strong>
                            <g:formatNumber number="${amount.value}" type="currency" currencySymbol="${amount.key.symbol}"/>
                        </strong>
                    </g:each>
                </td>
                <td>
                    <g:each var="amount" in="${paid.entrySet()}">
                        <strong>
                            <g:formatNumber number="${amount.value}" type="currency" currencySymbol="${amount.key.symbol}"/>
                        </strong>
                    </g:each>
                </td>
                <td>
                    <g:each var="amount" in="${unpaid.entrySet()}">
                        <strong>
                            <g:formatNumber number="${amount.value}" type="currency" currencySymbol="${amount.key.symbol}"/>
                        </strong>
                    </g:each>
                </td>
            </tr>
        </tbody>
    </table>
    </div>

    <!-- spacer -->
    <div><br/></div>

    <!-- payments made after the billing process by retries -->
    <div class="table-info">
        <em><g:message code="billing.details.payments.after.billing"/> <strong>${invoicePayments.size()}</strong></em>
    </div>
    <div class="table-area">
        <table>
        <thead>
            <tr>
                <td class="first"><g:message code="billing.details.payments.payment.date"/></td>
                <td><g:message code="billing.details.payments.number.of.payments"/></td>
                <td><g:message code="billing.details.payments.total.paid"/></td>
            </tr>
        </thead>
            <tbody>
            <%
                def invoicePaymentDates = new TreeMap()
                invoicePayments.each { payment ->
                    invoicePaymentDates[payment.paymentDate] = invoicePaymentDates.get(payment.paymentDate, []) << payment
                }
            %>

            <!-- all payments -->
            <g:each var="paymentDate" status="i" in="${invoicePaymentDates.entrySet()}">
                <tr class="${i % 2 == 0 ? 'even' : 'odd'}">
                    <td class="col02" valign="top"><g:formatDate date="${paymentDate.key}"/></td>
                    <td valign="top">${paymentDate.value.size()}</td>
                    <td valign="top">
                        <%
                            def invoicePaymentMethods = [:]
                            paymentDate.value.each { payment ->
                                def currencies = invoicePaymentMethods.get(payment.paymentMethod, [:])
                                currencies[payment.currency] = currencies.get(payment.currency, BigDecimal.ZERO).add(payment.amount)
                                invoicePaymentMethods[payment.paymentMethod] = currencies
                            }
                        %>

                        <g:each var="paymentMethod" in="${invoicePaymentMethods.entrySet()}">
                            <g:each var="paymentCurrency" in="${paymentMethod.value}">
                                <div>
                                    <span class="small">
                                        <g:formatNumber number="${paymentCurrency.value}" type="currency" currencySymbol="${paymentCurrency.key.symbol}"/>
                                    </span>
                                    <span class="small">
                                        ${paymentMethod.key.getDescription(session['language_id'])}
                                    </span>
                                </div>
                            </g:each>
                        </g:each>
                    </td>
                </tr>
            </g:each>

            <!-- grand totals -->
            <tr class="bg">
                <td class="col02"></td>
                <td><strong>${invoicePayments.size()}</strong></td>
                <td>
                    <%
                        def invoiceTotalPaid = [:]
                        invoicePayments.each { payment ->
                            invoiceTotalPaid[payment.currency] = invoiceTotalPaid.get(payment.currency, BigDecimal.ZERO).add(payment.amount)
                        }
                    %>

                    <g:each var="amount" in="${invoiceTotalPaid.entrySet()}">
                        <strong>
                            <g:formatNumber number="${amount.value}" type="currency" currencySymbol="${amount.key.symbol}"/>
                        </strong>
                    </g:each>
                </td>
            </tr>
            </tbody>
        </table>
    </div>

    <g:render template="/confirm"
              model="['message':'billing.details.approve.confirm',
                 'controller':'billing',
                 'action':'approve',
                 'id':process.id,
                ]"/>

    <g:render template="/confirm"
              model="['message':'billing.details.disapprove.confirm',
                 'controller':'billing',
                 'action':'disapprove',
                 'id':process.id,
                ]"/>
</body>
</html>