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

<%@ page import="com.sapienter.jbilling.server.metafields.db.DataType; com.sapienter.jbilling.common.Constants" contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
</head>
<body>
<div class="form-edit">

    <g:set var="isNew" value="${!payment || !payment?.id || payment?.id == 0}"/>

    <div class="heading">
        <strong>
            <g:if test="${payment.isRefund > 0}">
                <g:message code="payment.confirm.refund.title"/>
            </g:if>
            <g:else>
                <g:message code="payment.confirm.payment.title"/>
            </g:else>
        </strong>
    </div>

    <div class="form-hold">
        <g:form name="payment-edit-form" action="save">
            <fieldset>

                <!-- invoices to pay -->
                <g:if test="${invoiceId && invoices}">
                    <table cellpadding="0" cellspacing="0" class="innerTable">
                        <thead class="innerHeader">
                        <tr>
                            <th><g:message code="invoice.label.number"/></th>
                            <th><g:message code="invoice.label.payment.attempts"/></th>
                            <th><g:message code="invoice.label.total"/></th>
                            <th><g:message code="invoice.label.balance"/></th>
                            <th><g:message code="invoice.label.duedate"/></th>
                            <th><!-- action --> &nbsp;</th>
                        </tr>
                        </thead>
                        <tbody>
                        <g:each var="invoice" in="${invoices}">
                            <g:if test="${invoiceId.toInteger() == invoice.id}">
                                <g:set var="currency" value="${currencies.find { it.id == invoice.currencyId }}"/>

                                <tr>
                                    <td class="innerContent">
                                        <g:message code= "payment.link.invoice" args="[invoice.number]"/>
                                        <g:hiddenField name="invoiceId" value="${invoice.id}"/>
                                    </td>
                                    <td class="innerContent">
                                        ${invoice.paymentAttempts}
                                    </td>
                                    <td class="innerContent">
                                        <g:formatNumber number="${invoice.getTotalAsDecimal()}" type="currency" currencySymbol="${currency?.symbol}"/>
                                    </td>
                                    <td class="innerContent">
                                        <g:formatNumber number="${invoice.getBalanceAsDecimal()}" type="currency" currencySymbol="${currency?.symbol}"/>
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

                    <!-- spacer -->
                    <div>
                        <br/>&nbsp;
                    </div>
                </g:if>

                <!-- payment details  -->
                <div class="form-columns">
                    <div class="column">
                        <g:applyLayout name="form/text">
                            <content tag="label"><g:message code="payment.id"/></content>

                            <g:if test="${!isNew}"><span>${payment.id}</span></g:if>
                            <g:else><span><em><g:message code="prompt.id.new"/></em></span></g:else>

                            <g:hiddenField name="payment.id" value="${payment?.id}"/>
                        </g:applyLayout>

                        <g:if test="${!isNew}">
                            <g:applyLayout name="form/text">
                                <content tag="label"><g:message code="payment.attempt"/></content>
                                <span>${payment.attempt}</span>
                                <g:hiddenField name="payment.attempt" value="${payment?.attempt}"/>
                            </g:applyLayout>
                        </g:if>

                        <g:set var="currency" value="${currencies.find { it.id == payment?.currencyId }}"/>
                        <g:applyLayout name="form/text">
                            <content tag="label"><g:message code="prompt.user.currency"/></content>
                            <span>${currency?.getDescription(session['language_id']) ?: payment.currencyId}</span>
                            <g:hiddenField name="payment.currencyId" value="${payment?.currencyId}"/>
                        </g:applyLayout>

                        <g:applyLayout name="form/text">
                            <content tag="label"><g:message code="payment.amount"/></content>
                            <span><g:formatNumber number="${payment.amount}" formatName="money.format"/></span>
                            <g:hiddenField class="field" name="payment.amountAsDecimal" value="${formatNumber(number: payment?.amount, formatName: 'money.format')}"/>
                        </g:applyLayout>

                        <g:applyLayout name="form/text">
                            <content tag="label"><g:message code="payment.date"/></content>
                            <g:set var="paymentDate" value="${payment?.paymentDate ?: new Date()}"/>
                            <span><g:formatDate date="${paymentDate}"/></span>
                            <g:hiddenField class="field" name="payment.paymentDate" value="${formatDate(date: paymentDate)}"/>
                        </g:applyLayout>

                        <!-- meta fields -->
                        <g:each var="metaField" in="${availableFields?.sort{ it.displayOrder }}">
                            <g:if test="${!metaField.disabled}">
                                <g:set var="paymentMetaField" value="${payment?.metaFields?.find{ it.fieldName == metaField.name }}"/>
                                <g:set var="fieldValue" value="${paymentMetaField?.getValue()}"/>

                                <g:if test="${metaField.getDataType() == DataType.DATE}">
                                    <g:applyLayout name="form/text">
                                        <content tag="label">${metaField.name}</content>
                                        <span><g:formatDate date="${fieldValue}"/></span>
                                        <g:hiddenField class="field" name="metaField_${metaField.id}.value" value="${formatDate(date: fieldValue)}"/>
                                    </g:applyLayout>
                                </g:if>
                                <g:elseif test="${metaField.getDataType() == DataType.LIST}">
                                    <g:applyLayout name="form/text">
                                        <content tag="label">${metaField.name}</content>
                                        <span>${fieldValue?.join(', ')}</span>
                                        <g:hiddenField class="field" name="metaField_${metaField.id}.value" value="${fieldValue}"/>
                                    </g:applyLayout>
                                </g:elseif>
                                <g:else>
                                    <g:applyLayout name="form/text">
                                        <content tag="label">${metaField.name}</content>
                                        <span>${fieldValue}</span>
                                        <g:hiddenField class="field" name="metaField_${metaField.id}.value" value="${fieldValue}"/>
                                    </g:applyLayout>
                                </g:else>
                            </g:if>

                        </g:each>

                    </div>

                    <div class="column">
                        <g:applyLayout name="form/text">
                            <content tag="label"><g:message code="payment.user.id"/></content>
                            <span><g:link controller="customer" action="list" id="${user.userId}">${user.userId}</g:link></span>
                            <g:hiddenField name="payment.userId" value="${user.userId}"/>
                        </g:applyLayout>

                        <g:applyLayout name="form/text">
                            <content tag="label"><g:message code="prompt.login.name"/></content>
                            <span>${user.userName}</span>
                        </g:applyLayout>

                        <g:if test="${user.contact?.firstName || user.contact?.lastName}">
                            <g:applyLayout name="form/text">
                                <content tag="label"><g:message code="prompt.customer.name"/></content>
                                <em>${user.contact.firstName} ${user.contact.lastName}</em>
                            </g:applyLayout>
                        </g:if>

                        <g:if test="${user.contact?.organizationName}">
                            <g:applyLayout name="form/text">
                                <content tag="label"><g:message code="prompt.organization.name"/></content>
                                <em>${user.contact.organizationName}</em>
                            </g:applyLayout>
                        </g:if>


                        <g:applyLayout name="form/text">
                            <content tag="label"><g:message code="payment.is.refund"/></content>
                            <g:formatBoolean boolean="${payment?.isRefund > 0}"/>
                            <g:hiddenField name="isRefund" value="${payment?.isRefund}"/>
                        </g:applyLayout>

                        <g:applyLayout name="form/text">
                            <content tag="label"><g:message code="payment.label.process.realtime"/></content>
                            <g:formatBoolean boolean="${processNow}"/>
                            <g:hiddenField name="processNow" value="${processNow}"/>
                        </g:applyLayout>

                        %{--show linked payment ID if present--}%
                        <g:if test="${refundPaymentId}">
                            <g:applyLayout name="form/text">
                                <content tag="label"><g:message code="prompt.linked.payment"/></content>
                                <em>${refundPaymentId} </em>
                                <g:hiddenField name="payment_id" value="${refundPaymentId}"/>
                            </g:applyLayout>
                        </g:if>

                    </div>
                </div>

                <!-- spacer -->
                <div>
                    <br/>&nbsp;
                </div>

                <!-- credit card -->
                <g:if test="${payment?.creditCard}">
                    <g:set var="creditCard" value="${payment?.creditCard}"/>
                    <%-- 
                    <g:hiddenField name="creditCard.id" value="${creditCard?.id}"/>
                    --%>
                    
                    <div id="creditCard" class="box-cards ${creditCard ? 'box-cards-open' : ''}">
                        <div class="box-cards-title">
                            <a class="btn-open"><span><g:message code="prompt.credit.card"/></span></a>
                        </div>
                        <div class="box-card-hold">
                            <div class="form-columns">
                                <div class="column">
                                    <g:applyLayout name="form/text">
                                        <content tag="label"><g:message code="prompt.name.on.card"/></content>
                                        <span>${creditCard?.name}</span>
                                        <%-- 
                                        <g:hiddenField name="creditCard.name" value="${creditCard?.name}" />
                                        --%>
                                    </g:applyLayout>


                                    <g:applyLayout name="form/text">
                                        <content tag="label"><g:message code="prompt.credit.card.number"/></content>

                                        %{-- obscure credit card by default, or if the preference is explicitly set --}%
                                        <g:preferenceIsNullOrEquals preferenceId="${Constants.PREFERENCE_HIDE_CC_NUMBERS}" value="1">
                                            <g:set var="creditCardNumber" value="${creditCard.number.replaceAll('^\\d{12}','************')}"/>
                                            ${creditCardNumber}
                                        </g:preferenceIsNullOrEquals>

                                        <g:preferenceEquals preferenceId="${Constants.PREFERENCE_HIDE_CC_NUMBERS}" value="0">
                                            ${creditCard.number}
                                        </g:preferenceEquals>
                                    </g:applyLayout>

                                    <g:applyLayout name="form/text">
                                        <content tag="label"><g:message code="prompt.expiry.date"/></content>
                                        <span>
                                            <g:formatDate date="${creditCard?.expiry}" format="MM"/>
                                            /
                                            <g:formatDate date="${creditCard?.expiry}" format="yyyy"/>
                                        </span>
                                        <g:hiddenField name="expiryMonth" maxlength="2" size="2" value="${formatDate(date: creditCard?.expiry, format:'MM')}" />
                                        <g:hiddenField name="expiryYear" maxlength="4" size="4" value="${formatDate(date: creditCard?.expiry, format:'yyyy')}"/>
                                    </g:applyLayout>
                                </div>
                            </div>
                        </div>
                    </div>
                </g:if>

                <!-- ach -->
                <g:if test="${payment?.ach}">
                    <g:set var="ach" value="${payment?.ach}"/>
                    <g:hiddenField name="ach.id" value="${ach?.id}"/>

                    <div id="ach" class="box-cards ${ach ? 'box-cards-open' : ''}">
                        <div class="box-cards-title">
                            <a class="btn-open" href="#"><span><g:message code="prompt.ach"/></span></a>
                        </div>
                        <div class="box-card-hold">
                            <div class="form-columns">
                                <div class="column">
                                    <g:applyLayout name="form/text">
                                        <content tag="label"><g:message code="prompt.aba.routing.num"/></content>
                                        <content tag="label.for">ach.abaRouting</content>
                                        <span>${ach?.abaRouting}</span>
                                        <%--
                                        <g:hiddenField name="ach.abaRouting" value="${ach?.abaRouting}" />
                                        --%>
                                    </g:applyLayout>

                                    <g:applyLayout name="form/text">
                                        <content tag="label"><g:message code="prompt.bank.acc.num"/></content>
                                        <content tag="label.for">ach.bankAccount</content>
                                        <span>${ach?.bankAccount}</span>
                                        <%--
                                        <g:hiddenField name="ach.bankAccount" value="${ach?.bankAccount}" />
                                        --%>
                                    </g:applyLayout>

                                    <g:applyLayout name="form/text">
                                        <content tag="label"><g:message code="prompt.bank.name"/></content>
                                        <content tag="label.for">ach.bankName</content>
                                        <span>${ach?.bankName}</span>
                                        <%--
                                        <g:hiddenField name="ach.bankName" value="${ach?.bankName}" />
                                        --%>
                                    </g:applyLayout>

                                    <g:applyLayout name="form/text">
                                        <content tag="label"><g:message code="prompt.name.customer.account"/></content>
                                        <content tag="label.for">ach.accountName</content>
                                        <span>${ach?.accountName}</span>
                                        <%--
                                        <g:hiddenField name="ach.accountName" value="${ach?.accountName}" />
                                        --%>
                                    </g:applyLayout>

                                    <g:applyLayout name="form/text">
                                        <content tag="label"><g:message code="prompt.account.type" /></content>

                                        <g:if test="${ach?.accountType == 1}">
                                            <span><g:message code="label.account.checking"/></span>
                                        </g:if>
                                        <g:elseif test="${ach?.accountType == 2}">
                                            <span><g:message code="label.account.savings"/></span>
                                        </g:elseif>

                                        <g:hiddenField name="ach.accountType" value="${ach?.accountType}"/>
                                    </g:applyLayout>
                                </div>
                            </div>
                        </div>
                    </div>
                </g:if>

                <!-- cheque -->
                <g:if if="cheque" test="${payment?.cheque}">
                    <g:set var="cheque" value="${payment?.cheque}"/>
                    <g:hiddenField name="cheque.id" value="${cheque?.id}"/>

                    <div id="cheque" class="box-cards ${cheque ? 'box-cards-open' : ''}">
                        <div class="box-cards-title">
                            <a class="btn-open"><span><g:message code="prompt.cheque"/></span></a>
                        </div>
                        <div class="box-card-hold">
                            <div class="form-columns">
                                <div class="column">
                                    <g:applyLayout name="form/text">
                                        <content tag="label"><g:message code="prompt.cheque.bank"/></content>
                                        <content tag="label.for">cheque.bank</content>
                                        <span>${cheque?.bank}</span>
                                        <g:hiddenField name="cheque.bank" value="${cheque?.bank}"/>
                                    </g:applyLayout>

                                    <g:applyLayout name="form/text">
                                        <content tag="label"><g:message code="prompt.cheque.number"/></content>
                                        <content tag="label.for">cheque.number</content>
                                        <span>${cheque?.number}</span>
                                        <g:hiddenField name="cheque.number" value="${cheque?.number}"/>
                                    </g:applyLayout>

                                    <g:applyLayout name="form/text">
                                        <content tag="label"><g:message code="prompt.cheque.date"/></content>
                                        <content tag="label.for">cheque.date</content>
                                        <span><g:formatDate date="${cheque?.date}" formatName="date.format"/></span>
                                        <g:hiddenField name="cheque.date" value="${formatDate(date: cheque?.date, formatName: 'datepicker.format')}"/>
                                    </g:applyLayout>
                                </div>
                            </div>
                        </div>
                    </div>
                </g:if>

                <!-- box text -->
                <div class="box-text">
                    <label for="payment.paymentNotes"><g:message code="payment.notes"/></label>
                    <g:textArea name="payment.paymentNotes" value="${payment?.paymentNotes}" rows="5" cols="60" readonly="true"/>
                </div>

                <div class="buttons">
                    <ul>
                        <li>
                            <a onclick="$('#payment-edit-form').submit()" class="submit payment">
                                <g:if test="${isNew}"><span><g:message code="button.make.payment"/></span></g:if>
                                <g:if test="${!isNew}"><span><g:message code="button.save"/></span></g:if>
                            </a>
                        </li>
                        <li>
                            <g:link action="list" class="submit cancel"><span><g:message code="button.cancel"/></span></g:link>
                        </li>
                    </ul>
                </div>

            </fieldset>
        </g:form>
    </div>

</div>
</body>
</html>