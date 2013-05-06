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

<%@ page import="org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils; com.sapienter.jbilling.server.invoice.db.InvoiceStatusDAS; com.sapienter.jbilling.server.customer.CustomerBL; com.sapienter.jbilling.common.Constants; com.sapienter.jbilling.server.user.UserBL;" %>

<%--
  Shows details of a selected user.

  @author Brian Cowdery
  @since  23-Nov-2010
--%>

<g:set var="customer" value="${selected.customer}"/>

<div class="column-hold">
    <!-- user notes -->
    <div class="heading">
        <strong>
            <g:if test="${contact?.firstName || contact?.lastName}">
                ${contact.firstName} ${contact.lastName}
            </g:if>
            <g:else>
                ${selected.userName}
            </g:else>
            <em><g:if test="${contact}">${contact.organizationName}</g:if></em>
            <g:if test="${selected.deleted}">
                <span style="color: #ff0000;">(${selected.userStatus.description})</span>
            </g:if>
        </strong>
    </div>
    <div class="box edit">
        <div class="sub-box">
            <g:remoteLink action="show" id="${selected.id}" params="[template: 'notes']" before="register(this);" onSuccess="render(data, next);" class="edit"/>
            <g:if test="${customer?.notes}">
                <p>${customer.notes}</p>
            </g:if>
            <g:else>
                <p><em><g:message code="customer.detail.note.empty.message"/></em></p>
            </g:else>
        </div>
    </div>

    <!-- user details -->
    <div class="heading">
        <strong><g:message code="customer.detail.user.title"/></strong>
    </div>
    <div class="box">
        <div class="sub-box">
            <table class="dataTable" cellspacing="0" cellpadding="0">
                    <tbody>
                        <tr>
                            <td><g:message code="customer.detail.user.user.id"/></td>
                            <td class="value">
                                <sec:access url="/customerInspector/inspect">
                                    <g:link controller="customerInspector" action="inspect" id="${selected.id}" title="${message(code: 'customer.inspect.link')}">
                                        ${selected.id}
                                        <img src="${resource(dir: 'images', file: 'magnifier.png')}" alt="inspect customer"/>
                                    </g:link>
                                </sec:access>
                                <sec:noAccess url="/customerInspector/inspect">
                                    ${selected.id}
                                </sec:noAccess>
                            </td>
                        </tr>
                        <tr>
                            <td><g:message code="customer.detail.user.username"/></td>
                            <td class="value">
        
                                <g:if test="${!SpringSecurityUtils.isSwitched() && selected.id != session['user_id']}">
                                    <sec:ifAllGranted roles="USER_SWITCHING_111">
                                        <form id="switch-user-form" action="${request.contextPath}/j_spring_security_switch_user" method="POST">
                                            <g:hiddenField name="j_username" value="${selected.userName};${session['company_id']}"/>
                                        </form>
                                        <a onclick="$('#switch-user-form').submit()" title="${message(code: 'switch.user.link')}">
                                            ${selected.userName} <img src="${resource(dir: 'images', file: 'user_go.png')}" alt="switch user"/>
                                        </a>
                                    </sec:ifAllGranted>
        
                                    <sec:ifAllGranted roles="USER_SWITCHING_110">
                                        <sec:ifNotGranted roles="USER_SWITCHING_111">
                                            <!-- todo: validate if this customer is a direct sub-account before showing switch-user link -->
                                            <form id="switch-user-form" action="${request.contextPath}/j_spring_security_switch_user" method="POST">
                                                <g:hiddenField name="j_username" value="${selected.userName};${session['company_id']}"/>
                                            </form>
                                            <a onclick="$('#switch-user-form').submit()" title="${message(code: 'switch.user.link')}">
                                                ${selected.userName} <img src="${resource(dir: 'images', file: 'user_go.png')}" alt="switch user"/>
                                            </a>
                                        </sec:ifNotGranted>
                                    </sec:ifAllGranted>
        
                                    <sec:ifNotGranted roles="USER_SWITCHING_110, USER_SWITCHING_111">
                                        ${selected.userName}
                                    </sec:ifNotGranted>
                                </g:if>
                                <g:else>
                                    ${selected.userName}
                                </g:else>
        
                            </td>
                        </tr>
                        <g:if test="${customer.partner}">
                            <tr>
                                <td>Related Partner</td>
                                <td class="value">
                                    <g:remoteLink controller="partner" action="show" id="${customer.partner.id}" before="register(this);" onSuccess="render(data, next);">
                                        ${customer.partner.user.userName}
                                    </g:remoteLink>
                                </td>
                            </tr>
                        </g:if>
                        <tr>
                            <td><g:message code="customer.detail.user.status"/></td>
                            <td class="value">${selected.userStatus.description}</td>
                        </tr>
                        <tr>
                            <td><g:message code="customer.detail.user.created.date"/></td>
                            <td class="value"><g:formatDate date="${selected.createDatetime}" formatName="date.pretty.format"/></td>
                        </tr>
                        <tr>
                            <td><g:message code="customer.detail.user.email"/></td>
                            <td class="value"><a href="mailto:${contact?.email}">${contact?.email}</a></td>
                        </tr>
        
                        <g:if test="${customer?.metaFields}">
                            <!-- empty spacer row -->
                            <tr>
                                <td colspan="2"><br/></td>
                            </tr>
                            <g:render template="/metaFields/metaFields" model="[metaFields: customer?.metaFields]"/>
                        </g:if>
        
                        <g:if test="${customer?.parent}">
                            <!-- empty spacer row --> 
                            <tr>
                                <td colspan="2"><br/></td>
                            </tr>
                            <tr>
                                <td><g:message code="prompt.parent.id"/></td>
                                <td class="value">
                                    <g:remoteLink action="show" id="${customer.parent.baseUser.id}" before="register(this);" onSuccess="render(data, next);">
                                        ${customer.parent.baseUser.id} - ${customer.parent.baseUser.userName}
                                    </g:remoteLink>
                                </td>
                            </tr>
                            <tr>
                                <td><g:message code="customer.invoice.if.child.label"/></td>
                                <td class="value">
                                    <g:if test="${customer.invoiceChild > 0}">
                                        <g:message code="customer.invoice.if.child.true"/>
                                    </g:if>
                                    <g:else>
                                        <g:set var="parent" value="${new CustomerBL(customer.id).getInvoicableParent()}"/>
                                        <g:remoteLink action="show" id="${parent.baseUser.id}" before="register(this);" onSuccess="render(data, next);">
                                            <g:message code="customer.invoice.if.child.false" args="[ parent.baseUser.id ]"/>
                                        </g:remoteLink>
                                    </g:else>
                                </td>
                            </tr>
                            <tr>
                                <td><g:message code="prompt.use.parent.pricing"/></td>
                                <td class="value">
                                    <g:formatBoolean boolean="${customer.useParentPricing}"/>
                                </td>
                            </tr>
                        </g:if>
        
                        <g:if test="${customer?.children}">
                            <!-- empty spacer row --> 
                            <tr>
                                <td colspan="2"><br/></td>
                            </tr>
                            
                            <!-- direct sub-accounts -->
                            <g:each var="account" in="${customer.children.findAll{ it.baseUser.deleted == 0 }}">
                                <tr>
                                    <td><g:message code="customer.subaccount.title" args="[ account.baseUser.id ]"/></td>
                                    <td class="value">
                                        <g:remoteLink action="show" id="${account.baseUser.id}" before="register(this);" onSuccess="render(data, next);">
                                            ${account.baseUser.userName}
                                        </g:remoteLink>
                                    </td>
                                </tr>
                            </g:each>
                        </g:if>
                    </tbody>
                </table>
            </div>
    </div>

    <!-- user payment details -->
    <div class="heading">
        <strong><g:message code="customer.detail.payment.title"/></strong>
    </div>
    <div class="box">

        <div class="sub-box"><!-- show most recent order, invoice and payment -->
            <table class="dataTable" cellspacing="0" cellpadding="0">
                <tbody>
                <tr>
                    <td><g:message code="customer.detail.payment.order.date"/></td>
    
                    <td class="value">
                        <sec:access url="/order/show">
                            <g:remoteLink controller="order" action="show" id="${latestOrder?.id}" before="register(this);" onSuccess="render(data, next);">
                                <g:formatDate date="${latestOrder?.createDate}" formatName="date.pretty.format"/>
                            </g:remoteLink>
                        </sec:access>
                        <sec:noAccess url="/order/show">
                            <g:formatDate date="${latestOrder?.createDate}" formatName="date.pretty.format"/>
                        </sec:noAccess>
                    </td>
                    <td class="value">
                        <sec:access url="/order/list">
                            <g:link controller="order" action="user" id="${selected.id}">
                                <g:message code="customer.show.all.orders"/>
                            </g:link>
                        </sec:access>
                    </td>
                </tr>
                    <tr>
                        <td><g:message code="customer.detail.payment.invoiced.date"/></td>
                        <td class="value">
                            <sec:access url="/invoice/show">
                                <g:remoteLink controller="invoice" action="show" id="${latestInvoice?.id}" before="register(this);" onSuccess="render(data, next);">
                                    <g:formatDate date="${latestInvoice?.createDateTime}" formatName="date.pretty.format"/>
                                </g:remoteLink>
                            </sec:access>
                            <sec:noAccess url="/invoice/show">
                                <g:formatDate date="${latestInvoice?.createDateTime}" formatName="date.pretty.format"/>
                            </sec:noAccess>
                        </td>
                        <td class="value">
                            <sec:access url="/invoice/list">
                                <g:link controller="invoice" action="user" id="${selected.id}">
                                    <g:message code="customer.show.all.invoices"/>
                                </g:link>
                            </sec:access>
                        </td>
                    </tr>
                    <tr>
                        <td><g:message code="customer.detail.payment.paid.date"/></td>
                        <td class="value">
                            <sec:access url="/payment/show">
                                <g:remoteLink controller="payment" action="show" id="${latestPayment?.id}" before="register(this);" onSuccess="render(data, next);">
                                    <g:formatDate date="${latestPayment?.paymentDate ?: latestPayment?.createDatetime}" formatName="date.pretty.format"/>
                                </g:remoteLink>
                            </sec:access>
                            <sec:noAccess url="/payment/show">
                                <g:formatDate date="${latestPayment?.paymentDate ?: latestPayment?.createDatetime}" formatName="date.pretty.format"/>
                            </sec:noAccess>
                        </td>
                        <td class="value">
                            <sec:access url="/payment/list">
                                <g:link controller="payment" action="user" id="${selected.id}">
                                    <g:message code="customer.show.all.payments"/>
                                </g:link>
                            </sec:access>
                        </td>
                    </tr>
                    <tr>
                        <td><g:message code="customer.detail.user.next.invoice.date"/></td>
                        <td class="value">
                            <g:set var="nextInvoiceDate" value="${new UserBL(selected.id).getNextInvoiceDate()}"/>
    
                            <g:if test="${nextInvoiceDate}">
                                <span><g:formatDate date="${nextInvoiceDate}" formatName="date.pretty.format"/></span>
                            </g:if>
                            <g:else>
                                <g:message code="prompt.no.active.orders"/>
                            </g:else>
                        </td>
                    </tr>
                    <tr>
                        <td><g:message code="customer.detail.payment.due.date"/></td>
                        <td class="value"><g:formatDate date="${latestInvoice?.dueDate}" formatName="date.pretty.format"/></td>
                    </tr>
                    <tr>
                        <td><g:message code="customer.detail.payment.invoiced.amount"/></td>
                        <td class="value"><g:formatNumber number="${latestInvoice?.totalAsDecimal}" type="currency" currencySymbol="${selected.currency.symbol}"/></td>
                    </tr>
                    <tr>
                        <td><g:message code="invoice.label.status"/></td>
                        <td class="value">
                            <g:if test="${latestInvoice}">
                                <g:set var="invoiceStatus" value="${new InvoiceStatusDAS().find(latestInvoice?.statusId)}"/>
                                <g:if test="${latestInvoice?.statusId == Constants.INVOICE_STATUS_UNPAID}">
                                    <g:link controller="payment" action="edit" params="[userId: selected.id, invoiceId: latestInvoice.id]" title="${message(code: 'invoice.pay.link')}">
                                        ${invoiceStatus.getDescription(session['language_id'])}
                                    </g:link>
                                </g:if>
                                <g:else>
                                    ${invoiceStatus?.getDescription(session['language_id'])}
                                </g:else>
                            </g:if>
                        </td>
                    </tr>
                    <tr>
                        <td><g:message code="customer.detail.payment.amount.owed"/></td>
                        <td class="value"><g:formatNumber number="${UserBL.getBalance(selected.id)}" type="currency"  currencySymbol="${selected.currency.symbol}"/></td>
                    </tr>
                    <tr>
                        <td><g:message code="customer.detail.payment.lifetime.revenue"/></td>
                        <td class="value"><g:formatNumber number="${revenue}" type="currency"  currencySymbol="${selected.currency.symbol}"/></td>
                    </tr>
                </tbody>
            </table>
    
            <hr/>
    
            <g:set var="card" value="${selected.creditCards ? selected.creditCards.asList().first() : null}"/>
            <table class="dataTable" cellspacing="0" cellpadding="0">
                <tbody>
                    <tr>
                        <td><g:message code="customer.detail.payment.credit.card"/></td>
                        <td class="value">
                            <g:if test="${card?.number}">
                                %{-- obscure credit card by default, or if the preference is explicitly set --}%
                                <g:preferenceIsNullOrEquals preferenceId="${Constants.PREFERENCE_HIDE_CC_NUMBERS}" value="1">
                                    <g:set var="creditCardNumber" value="${card.number.replaceAll('^\\d{12}','************')}"/>
                                    ${creditCardNumber}
                                </g:preferenceIsNullOrEquals>
    
                                <g:preferenceEquals preferenceId="${Constants.PREFERENCE_HIDE_CC_NUMBERS}" value="0">
                                ${card?.number}
                                </g:preferenceEquals>
                             </g:if>
                        </td>
                    </tr>
    
                    <tr>
                        <td><g:message code="customer.detail.payment.credit.card.expiry"/></td>
                        <td class="value"><g:formatDate date="${card?.ccExpiry}" formatName="credit.card.date.format"/></td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>

    <!-- contact details -->    
    <div class="heading">
        <strong><g:message code="customer.detail.contact.title"/></strong>
    </div>
    <g:if test="${contact}">
    <div class="box">

        <div class="sub-box">
            <table class="dataTable" cellspacing="0" cellpadding="0">
                <tbody>
                    <tr>
                        <td><g:message code="customer.detail.contact.telephone"/></td>
                        <td class="value">
                            
                            <g:phoneNumber countryCode="${contact?.phoneCountryCode}" 
                                areaCode="${contact?.phoneAreaCode}" number="${contact?.phoneNumber}"/>
                            
                        </td>
                    </tr>
                    <tr>
                        <td><g:message code="customer.detail.contact.address"/></td>
                        <td class="value">${contact.address1} ${contact.address2}</td>
                    </tr>
                    <tr>
                        <td><g:message code="customer.detail.contact.city"/></td>
                        <td class="value">${contact.city}</td>
                    </tr>
                    <tr>
                        <td><g:message code="customer.detail.contact.state"/></td>
                        <td class="value">${contact.stateProvince}</td>
                    </tr>
                    <tr>
                        <td><g:message code="customer.detail.contact.country"/></td>
                        <td class="value">${contact.countryCode}</td>
                    </tr>
                    <tr>
                        <td><g:message code="customer.detail.contact.zip"/></td>
                        <td class="value">${contact.postalCode}</td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>
    </g:if>

    <div class="btn-box">
        <g:if test="${!selected.deleted}">
            <div class="row">
                <sec:ifAllGranted roles="ORDER_20">
                    <g:link controller="orderBuilder" action="edit" params="[userId: selected.id]" class="submit order"><span><g:message code="button.create.order"/></span></g:link>
                </sec:ifAllGranted>

                <sec:ifAllGranted roles="PAYMENT_30">
                    <g:link controller="payment" action="edit" params="[userId: selected.id]" class="submit payment"><span><g:message code="button.make.payment"/></span></g:link>
                </sec:ifAllGranted>
            </div>
            <div class="row">
                <sec:ifAllGranted roles="CUSTOMER_11">
                    <g:link action="edit" id="${selected.id}" class="submit edit"><span><g:message code="button.edit"/></span></g:link>
                </sec:ifAllGranted>

                <sec:ifAllGranted roles="CUSTOMER_12">
                    <a onclick="showConfirm('delete-${selected.id}');" class="submit delete"><span><g:message code="button.delete"/></span></a>
                </sec:ifAllGranted>

                <sec:ifAllGranted roles="CUSTOMER_10">
                    <g:if test="${customer?.isParent > 0}">
                        <g:link action="edit" params="[parentId: selected.id]" class="submit add"><span><g:message code="customer.add.subaccount.button"/></span></g:link>
                    </g:if>
                </sec:ifAllGranted>
            </div>
        </g:if>
    </div>

    <g:render template="/confirm"
              model="['message': 'customer.delete.confirm',
                      'controller': 'customer',
                      'action': 'delete',
                      'id': selected.id,
                      'ajax': true,
                      'update': 'column1',
                      'onYes': 'closePanel(\'#column2\')'
                     ]"/>

</div>
