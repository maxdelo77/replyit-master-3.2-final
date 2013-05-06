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

<%@ page import="com.sapienter.jbilling.server.util.db.EnumerationDTO; com.sapienter.jbilling.common.Constants" contentType="text/html;charset=UTF-8" %>

<g:set var="isNew" value="${!payment || !payment?.id || payment?.id == 0}"/>

<html>
<head>
    <meta name="layout" content="main"/>

    <script type="text/javascript">
        function togglePaymentType(element) {
            $('.box-cards.payment-type').not(element).each(function () {
                // toggle slide
                closeSlide(this);
                $(this).find(':input').prop('disabled','true');

                // toggle "process now" for cheque payments
                if ($(element).attr('id') == 'cheque') {
                    $('#processNow').prop('checked','').prop('disabled','true');
                } else {
                    $('#processNow').prop('disabled','');
                }
            });

            $(element).find(':input').prop('disabled','');
        }

        function clearInvoiceSelection() {
            $(':input[type=radio][name=invoiceId]').prop('checked','');
            $('#payment\\.amountAsDecimal').val('');
        }

        function storeValues(index) {
            // update the value of amount
            var amount = $("#payment-amount-"+index).attr('value');
            $("#payment_amountAsDecimal").attr('value', amount).prop('disabled', true);
            // ensure the value is passed to server
            $("#payment_amountAsDecimal_hidden").attr('value', amount);
            $('#refund_cb').prop('checked', true);
            $("#invoicesContainer").slideUp(1000);
        }

        function clearPaymentSelection() {
            // clear the selected payment
            // reset the amount back to zero
            $("#payment_amountAsDecimal").attr('value', '').prop('disabled', '');
            $('#refund_cb').prop('checked', false).prop('disabled', '');
            $(".paymentRadio").each(function(){
                $(this).prop('checked',false);
            });
            $("#invoicesContainer").slideDown(1000);
        }

        <g:if test="${isNew}">
        $(document).ready(function() {
            // populate payment amount with selected invoice balance
            $('#invoices input[name=invoiceId]').change(function() {
                $('#payment\\.amountAsDecimal').val($('#invoice-' + $(this).val() + '-balance').val());
                var currid= $('#invoice-' + $(this).val() + '-curid').val();
                $('#payment\\.currencyId :selected').removeAttr('selected');
                $('#payment\\.currencyId option[value='+ currid +']').attr('selected','selected');
            });

            var validator = $('#payment-edit-form').validate();
            validator.init();
            validator.hideErrors();
        });
        </g:if>
        <g:if test="${isCheque}">
        $(document).ready(function() {
            // disable the cheque box of process now
            $('#processNow').attr({disabled:true});
        });
        </g:if>
    </script>
</head>
<body>
<div class="form-edit">

    <div class="heading">
        <strong>
            <g:if test="${!isNew}">
                <g:if test="${payment.isRefund > 0}">
                    <g:message code="payment.edit.refund.title"/>
                </g:if>
                <g:else>
                    <g:message code="payment.edit.payment.title"/>
                </g:else>
            </g:if>
            <g:else>
                <g:message code="payment.new.payment.title"/>
            </g:else>
        </strong>
    </div>

    <div class="form-hold">
        <g:form name="payment-edit-form" action="confirm">
            <fieldset>

                <!-- invoices to pay -->
            <div id="invoicesContainer">
                <g:if test="${invoices}">
                    <div id="invoices" class="box-cards box-cards-open">
                        <div class="box-cards-title">
                            <a class="btn-open"><span><g:message code="payment.payable.invoices.title"/></span></a>
                        </div>
                        <div class="box-card-hold">

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
                                <g:set var="selectedInvoiceCurrencyId" value=""/>
                                <g:set var="currencyInvoice" value=""/>
                                <g:each var="invoice" in="${invoices}">
                                    <g:set var="currency" value="${currencies.find { it.id == invoice.currencyId }}"/>
                                    <g:set var="currencyInvoice" value="${currencies.find { it.id == invoice.currencyId }}"/>
                                    <g:if test="${invoice.id == invoiceId}">
                                        <g:set var="selectedInvoiceCurrencyId" value="${invoice.currencyId}"/>
                                    </g:if>
                                    <tr>
                                        <td class="innerContent">
                                            <g:applyLayout name="form/radio">
                                                <g:radio id="invoice-${invoice.id}" name="invoiceId" value="${invoice.id}" checked="${invoice.id == invoiceId}" />
                                                <label for="invoice-${invoice.id}" class="rb">
                                                    <g:message code= "payment.link.invoice" args="[invoice.number]"/>
                                                </label>
                                            </g:applyLayout>
                                        </td>
                                        <td class="innerContent">
                                            ${invoice.paymentAttempts}
                                            <g:hiddenField name="invoice-${invoice.id}-curid" value="${currency?.id}"/>
                                        </td>
                                        <td class="innerContent">
                                            <g:formatNumber number="${invoice.getTotalAsDecimal()}" type="currency" currencySymbol="${currency?.symbol}"/>
                                            <g:hiddenField name="invoice-${invoice.id}-amount" value="${formatNumber(number: invoice.total, formatName: 'money.format')}"/>
                                        </td>
                                        <td class="innerContent">
                                            <g:formatNumber number="${invoice.getBalanceAsDecimal()}" type="currency" currencySymbol="${currency?.symbol}"/>
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
                                </g:each>
                                </tbody>
                            </table>

                            <div class="btn-row">
                                <a onclick="clearInvoiceSelection();" class="submit delete"><span><g:message code="button.clear"/></span></a>
                            </div>

                        </div>
                    </div>
                </g:if>
            </div>

            %{--Payments made --}%
            <div id="paymentContainer">
            <g:if test="${refundablePayments}">
                    <div id="invoices" class="box-cards box-cards-open">
                        <div class="box-cards-title">
                            <a class="btn-open"><span><g:message code="payment.paid.title"/></span></a>
                        </div>
                        <div class="box-card-hold">

                            <table cellpadding="0" cellspacing="0" class="innerTable">
                                <thead class="innerHeader">
                                <tr>
                                    <th><g:message code="payment.id"/></th>
                                    <th><g:message code="payment.date"/></th>
                                    <th><g:message code="payment.amount"/></th>
                                    <th><g:message code="payment.method"/></th>
                                    <th><g:message code="payment.notes"/></th>
                                    <th><!-- action --> &nbsp;</th>
                                </tr>
                                </thead>
                                <tbody>
                                <g:set var="selectedPaymentCurrencyId" value=""/>
                                <g:each var="payment" in="${refundablePayments}" status="counter">
                                    <g:set var="currency" value="${currencies.find { it.id == payment?.getCurrency()?.getId()}}"/>
                                    <g:if test="${payment?.id == refundPaymentId}">
                                        <g:set var="selectedPaymentCurrencyId" value="${payment?.getCurrency()?.getId()}"/>
                                    </g:if>
                                    <tr>
                                        <td class="innerContent">
                                            <g:applyLayout name="form/radio">
                                                <g:radio id="payment-${payment.id}" class="paymentRadio" name="payment.paymentId" value="${payment.id}" onclick="storeValues(${counter});" />
                                                <label for="payment-${payment.id}" class="rb">
                                                    ${payment.id}
                                                </label>
                                            </g:applyLayout>
                                        </td>
                                        <td class="innerContent">
                                           <g:formatDate date="${payment.getCreateDatetime()}"/>
                                            <g:hiddenField name="payment-${payment.id}-curid" value="${currency.id}"/>
                                        </td>
                                        <td class="innerContent">
                                            <g:formatNumber number="${payment.getAmount()}" type="currency" currencySymbol="${currency.symbol}"/>
                                            <g:hiddenField id="payment-amount-${counter}" name="payment-amount-${counter}" value="${formatNumber(number: payment.getAmount(), formatName: 'money.format')}"/>
                                        </td>
                                        <td class="innerContent">
                                            %{--<g:formatNumber number="${invoice.getBalanceAsDecimal()}" type="currency" currencySymbol="${currency.symbol}"/>--}%
                                            ${payment?.getPaymentMethod()?.getDescription()}
                                            %{--<g:hiddenField name="invoice-${invoice.id}-balance" value="${formatNumber(number: invoice.balance, formatName: 'money.format')}"/>--}%
                                        </td>
                                        <td class="innerContent">
                                           <div style="width:200px;margin:auto;white-space: pre;white-space: pre-wrap;white-space: pre-line;white-space: -pre-wrap;white-space: -o-pre-wrap;white-space: -moz-pre-wrap;white-space: -hp-pre-wrap;word-wrap: break-word;    ">
                                           ${payment.getPaymentNotes()}
                                           %{--<g:formatDate date="${invoice.dueDate}"/>--}%</div>

                                        </td>
                                        %{--<td class="innerContent">--}%
                                            %{--<g:link controller="invoice" action="list" id="${invoice.id}">--}%
                                                %{--<g:message code= "payment.link.view.invoice" args="[invoice.number]"/>--}%
                                            %{--</g:link>--}%
                                        %{--</td>--}%
                                    </tr>
                                </g:each>
                                </tbody>
                            </table>

                            <div class="btn-row">
                                <a onclick="clearPaymentSelection();" class="submit delete"><span><g:message code="button.clear"/></span></a>
                            </div>

                        </div>
                    </div>
            </g:if>
            </div>

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

                        <g:if test="${!isNew}">
                            <g:set var="currency" value="${currencies.find { it.id == payment?.currencyId }}"/>

                            <g:applyLayout name="form/text">
                                <content tag="label"><g:message code="prompt.user.currency"/></content>
                                <span>${currency?.getDescription(session['language_id']) ?: payment.currencyId}</span>
                                <g:hiddenField name="payment.currencyId" value="${payment?.currencyId}"/>
                            </g:applyLayout>
                        </g:if>
                        <g:else>
                            <g:applyLayout name="form/select">
                                <content tag="label"><g:message code="prompt.user.currency"/></content>
                                <content tag="label.for">payment.currencyId</content>
                                <g:select name="payment.currencyId"
                                          from="${currencyInvoice ? currencyInvoice : currencies.find { it.id == user?.currencyId }}"
                                          value="${selectedInvoiceCurrencyId}" 
                                          optionKey="id"
                                          optionValue="${{it.getDescription(session['language_id'])}}"/>
                            </g:applyLayout>
                        </g:else>

                        <g:applyLayout name="form/input">
                            <content tag="label"><g:message code="payment.amount"/></content>
                            <content tag="label.for">payment.amountAsDecimal</content>
                            <g:set var="paymentAmount" value="${payment?.amount ?: invoices?.find{ it.id == invoiceId }?.balance }"/>
                            <g:textField class="field" id="payment_amountAsDecimal" name="payment.amountAsDecimal" value="${formatNumber(number: paymentAmount, formatName: 'money.format')}"/>
                            <g:hiddenField name="payment.amountAsDecimal" id="payment_amountAsDecimal_hidden" value=""/>
                        </g:applyLayout>

                        <g:applyLayout name="form/date">
                            <content tag="label"><g:message code="payment.date"/></content>
                            <content tag="label.for">payment.paymentDate</content>
                            <g:set var="paymentDate" value="${payment?.paymentDate ?: new Date()}"/>
                            <g:textField class="field" name="payment.paymentDate" value="${formatDate(date: paymentDate, formatName: 'datepicker.format')}"/>
                        </g:applyLayout>

                        <g:if test="${isNew}">
                            <g:applyLayout name="form/checkbox">
                                <content tag="label"><g:message code="payment.is.refund.payment"/></content>
                                <content tag="label.for">isRefund</content>
                                <g:checkBox id="refund_cb" class="cb checkbox" name="isRefund" checked="${payment?.isRefund > 0}"/>
                                %{--<g:hiddenField id="refund_cb_hidden" name="isRefund" value=""/>--}%
                            </g:applyLayout>
                        </g:if>
                        <g:else>
                            <g:applyLayout name="form/text">
                                <content tag="label"><g:message code="payment.is.refund.payment"/></content>
                                <span><g:formatBoolean boolean="${payment?.isRefund > 0}"/></span>
                                <g:hiddenField name="payment.isRefund" value="${payment?.isRefund?.intValue()}"/>
                            </g:applyLayout>
                        </g:else>

                        <g:if test="${isNew}">
                            <g:applyLayout name="form/checkbox">
                                <content tag="label"><g:message code="payment.process.realtime"/></content>
                                <content tag="label.for">processNow</content>
                                <g:checkBox class="cb checkbox" name="processNow" value="${processNow}"/>
                            </g:applyLayout>
                        </g:if>

                        <!-- meta fields -->
                        <g:render template="/metaFields/editMetaFields" model="[ availableFields: availableFields, fieldValues: payment?.metaFields ]"/>
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

                    </div>
                </div>

                <!-- spacer -->
                <div>
                    <br/>&nbsp;
                </div>

                <!-- credit card -->
                %{
                    def creditCardAllowed = paymentMethods.find {
                        it.id == Constants.PAYMENT_METHOD_VISA ||
                        it.id == Constants.PAYMENT_METHOD_VISA_ELECTRON ||
                        it.id == Constants.PAYMENT_METHOD_MASTERCARD ||
                        it.id == Constants.PAYMENT_METHOD_AMEX ||
                        it.id == Constants.PAYMENT_METHOD_DISCOVERY ||
                        it.id == Constants.PAYMENT_METHOD_DINERS ||
                        it.id == Constants.PAYMENT_METHOD_INSTAL_PAYMENT ||
                        it.id == Constants.PAYMENT_METHOD_JCB ||
                        it.id == Constants.PAYMENT_METHOD_LASER ||
                        it.id == Constants.PAYMENT_METHOD_MAESTRO ||
                        it.id == Constants.PAYMENT_METHOD_GATEWAY_KEY
                    }
                }%

                <g:if test="${(creditCardAllowed && isNew) || (creditCardAllowed && payment?.creditCard)}">
                    <g:set var="creditCard" value="${payment?.creditCard}"/>

                    <div id="creditCard" class="box-cards ${creditCard ? 'box-cards-open' : ''} payment-type" onOpen="togglePaymentType('#creditCard');">
                        <div class="box-cards-title">
                            <a class="btn-open"><span><g:message code="prompt.credit.card"/></span></a>
                        </div>
                        <div class="box-card-hold">
                            <div class="form-columns">
                                <div class="column">
                                    <g:hiddenField name="creditCard.id" value="${creditCard?.id}"/>

                                    <g:applyLayout name="form/input">
                                        <content tag="label"><g:message code="prompt.name.on.card"/></content>
                                        <content tag="label.for">creditCard.name</content>
                                        <g:textField class="field" name="creditCard.name" value="${creditCard?.name}" />
                                    </g:applyLayout>

                                    <g:applyLayout name="form/input">
                                        <content tag="label"><g:message code="prompt.credit.card.number"/></content>
                                        <content tag="label.for">creditCard.number</content>

                                        <g:if test="${creditCard?.number}">
                                            %{-- obscure credit card by default, or if the preference is explicitly set --}%
                                            <g:preferenceIsNullOrEquals preferenceId="${Constants.PREFERENCE_HIDE_CC_NUMBERS}" value="1">
                                                <g:set var="creditCardNumber" value="${creditCard.number.replaceAll('^\\d{12}','************')}"/>

                                                <g:if test="${creditCardNumber.size() < 16}">
                                                    <g:set var="creditCardNumber" value="************${creditCardNumber}"/>
                                                </g:if>

                                                <g:textField class="field" name="creditCard.number" value="${creditCardNumber}" />
                                            </g:preferenceIsNullOrEquals>

                                            <g:preferenceEquals preferenceId="${Constants.PREFERENCE_HIDE_CC_NUMBERS}" value="0">
                                                <g:textField class="field" name="creditCard.number" value="${creditCard?.number}" />
                                            </g:preferenceEquals>
                                        </g:if>
                                        <g:else>
                                            <g:textField class="field" name="creditCard.number" value="${creditCard?.number}" />
                                        </g:else>
                                        
                                    </g:applyLayout>

                                    <g:applyLayout name="form/text">
                                        <content tag="label"><g:message code="prompt.expiry.date"/></content>
                                        <content tag="label.for">expiryMonth</content>
                                        <span>
                                            <g:textField class="text" name="expiryMonth" maxlength="2" size="2" value="${formatDate(date: creditCard?.expiry, format:'MM')}" />
                                            -
                                            <g:textField class="text" name="expiryYear" maxlength="4" size="4" value="${formatDate(date: creditCard?.expiry, format:'yyyy')}"/>
                                            mm/yyyy
                                        </span>
                                    </g:applyLayout>
                                </div>

                            </div>
                        </div>
                    </div>

                    <g:if test="${isNew && payment?.creditCard}">
                        <script type="text/javascript">
                            /*
                                Clear the default credit card ID if any of the input fields are
                                changed when creating a new payment.
                             */
                            $(function() {
                                $('#creditCard :input').change(function() {
                                    $('#creditCard\\.id').val('');
                                    $('#creditCard :input').unbind('change');
                                });
                            });
                        </script>
                    </g:if>

                </g:if>

                <!-- ach -->
                %{
                    def achAllowed = paymentMethods.find { it.id == Constants.PAYMENT_METHOD_ACH }
                }%

                <g:if test="${(achAllowed && isNew) || (achAllowed && payment?.ach)}">
                    <g:set var="ach" value="${payment?.ach}"/>

                    <div id="ach" class="box-cards ${ach ? 'box-cards-open' : ''} payment-type" onOpen="togglePaymentType('#ach');">
                        <div class="box-cards-title">
                            <a class="btn-open" href="#"><span><g:message code="prompt.ach"/></span></a>
                        </div>
                        <div class="box-card-hold">
                            <div class="form-columns">
                                <div class="column">
                                    <g:hiddenField name="ach.id" value="${ach?.id}"/>

                                    <g:applyLayout name="form/input">
                                        <content tag="label"><g:message code="prompt.aba.routing.num"/></content>
                                        <content tag="label.for">ach.abaRouting</content>
                                        <g:textField class="field" name="ach.abaRouting" value="${ach?.abaRouting}" />
                                    </g:applyLayout>

                                    <g:applyLayout name="form/input">
                                        <content tag="label"><g:message code="prompt.bank.acc.num"/></content>
                                        <content tag="label.for">ach.bankAccount</content>
                                        <g:textField class="field" name="ach.bankAccount" value="${ach?.bankAccount}" />
                                    </g:applyLayout>

                                    <g:applyLayout name="form/input">
                                        <content tag="label"><g:message code="prompt.bank.name"/></content>
                                        <content tag="label.for">ach.bankName</content>
                                        <g:textField class="field" name="ach.bankName" value="${ach?.bankName}" />
                                    </g:applyLayout>

                                    <g:applyLayout name="form/input">
                                        <content tag="label"><g:message code="prompt.name.customer.account"/></content>
                                        <content tag="label.for">ach.accountName</content>
                                        <g:textField class="field" name="ach.accountName" value="${ach?.accountName}" />
                                    </g:applyLayout>

                                    <g:applyLayout name="form/radio">
                                        <content tag="label"><g:message code="prompt.account.type" /></content>

                                        <g:radio class="rb" id="ach.accountType.checking" name="ach.accountType" value="1" checked="${ach?.accountType == 1}"/>
                                        <label class="rb" for="ach.accountType.checking"><g:message code="label.account.checking"/></label>

                                        <g:radio class="rb" id="ach.accountType.savings" name="ach.accountType" value="2" checked="${ach?.accountType == 2}"/>
                                        <label class="rb" for="ach.accountType.savings"><g:message code="label.account.savings"/></label>
                                    </g:applyLayout>
                                </div>
                            </div>
                        </div>
                    </div>

                    <g:if test="${isNew && payment?.ach}">
                        <script type="text/javascript">
                            /*
                                Clear the default ach ID if any of the input fields are
                                changed when creating a new payment.
                             */
                            $(function() {
                                $('#ach :input').change(function() {
                                    $('#ach\\.id').val('');
                                    $('#ach :input').unbind('change');
                                });
                            });
                        </script>
                    </g:if>

                </g:if>

                <!-- cheque -->
                %{
                    def chequeAllowed = paymentMethods.find { it.id == Constants.PAYMENT_METHOD_CHEQUE }
                }%

                <g:if if="cheque" test="${(chequeAllowed && isNew) || payment?.cheque}">
                    <g:set var="cheque" value="${payment?.cheque}"/>

                    <div id="cheque" class="box-cards ${cheque ? 'box-cards-open' : ''} payment-type" onOpen="togglePaymentType('#cheque');">
                        <div class="box-cards-title">
                            <a class="btn-open"><span><g:message code="prompt.cheque"/></span></a>
                        </div>
                        <div class="box-card-hold">
                            <div class="form-columns">
                                <div class="column">
                                    <g:hiddenField name="cheque.id" value="${cheque?.id}"/>

                                    <g:applyLayout name="form/input">
                                        <content tag="label"><g:message code="prompt.cheque.bank"/></content>
                                        <content tag="label.for">cheque.bank</content>
                                        <g:textField class="field" name="cheque.bank" value="${cheque?.bank}"/>
                                    </g:applyLayout>

                                    <g:applyLayout name="form/input">
                                        <content tag="label"><g:message code="prompt.cheque.number"/></content>
                                        <content tag="label.for">cheque.number</content>
                                        <g:textField class="field" name="cheque.number" value="${cheque?.number}"/>
                                    </g:applyLayout>

                                    <g:applyLayout name="form/date">
                                        <content tag="label"><g:message code="prompt.cheque.date"/></content>
                                        <content tag="label.for">cheque.date</content>
                                        <g:textField class="field" name="cheque.date" value="${formatDate(date: cheque?.date, formatName:'datepicker.format')}"/>
                                    </g:applyLayout>
                                </div>
                            </div>
                        </div>
                    </div>
                </g:if>

                <!-- box text -->
                <div class="box-text">
                    <label for="payment.paymentNotes"><g:message code="payment.notes"/></label>
                    <g:textArea name="payment.paymentNotes" value="${payment?.paymentNotes}" rows="5" cols="60"/>
                </div>

                <div class="buttons">
                    <ul>
                        <li>
                            <a onclick="$('#payment-edit-form').submit()" class="submit payment">
                                <span><g:message code="button.review.payment"/></span>
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
