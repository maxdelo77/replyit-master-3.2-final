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

<%@ page import="com.sapienter.jbilling.server.user.contact.db.ContactDTO; org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils" contentType="text/html;charset=UTF-8" %>

<%--
  Shows an internal user.

  @author Brian Cowdery
  @since  04-Apr-2011
--%>

<div class="column-hold">
    <div class="heading">
        <strong>
            <g:if test="${contact?.firstName || contact?.lastName}">
                ${contact.firstName} ${contact.lastName}
            </g:if>
            <g:else>
                ${selected.userName}
            </g:else>
            <em><g:if test="${contact}">${contact.organizationName}</g:if></em>
        </strong>
    </div>

    <!-- user details -->
    <div class="box">
        <div class="sub-box">
            <table class="dataTable" cellspacing="0" cellpadding="0">
                <tbody>
                <tr>
                    <td><g:message code="customer.detail.user.user.id"/></td>
                    <td class="value">${selected.id}</td>
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
                <tr>
                    <td><g:message code="customer.detail.user.status"/></td>
                    <td class="value">${selected.userStatus.description}</td>
                </tr>
                <tr>
                    <td><g:message code="user.language"/></td>
                    <td class="value">${selected.language.getDescription()}</td>
                </tr>

                <tr>
                    <td><g:message code="customer.detail.user.created.date"/></td>
                    <td class="value"><g:formatDate date="${selected.createDatetime}" formatName="date.pretty.format"/></td>
                </tr>
                <tr>
                    <td><g:message code="user.last.login"/></td>
                    <td class="value"><g:formatDate date="${selected.lastLogin}" formatName="date.pretty.format"/></td>
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
                        <td><g:message code="customer.detail.user.email"/></td>
                        <td class="value"><a href="mailto:${contact?.email}">${contact?.email}</a></td>
                    </tr>
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

    <!-- linked partners -->
    <g:if test="${selected.partnersForRelatedClerk}">
    <div class="heading">
        <strong>Linked Partners</strong>
    </div>
    <div class="box">
        <div class="sub-box">
            <table class="dataTable" cellspacing="0" cellpadding="0" width="100%">
                <tbody>
                    <g:each var="partner" status="i" in="${selected.partnersForRelatedClerk}">
                        <g:set var="partnerContact" value="${ContactDTO.findByUserId(partner?.baseUser.id)}"/>
                    <tr>
                        <td><g:message code="partner.detail.name"/></td>
                        <td class="value">
                            <g:remoteLink controller="partner" action="show" id="${partner.id}" before="register(this);" onSuccess="render(data, next);">
                                <g:if test="${partnerContact?.firstName || partnerContact?.lastName}">
                                    ${partnerContact.firstName} ${partnerContact.lastName}
                                </g:if>
                                <g:else>
                                    ${partner.user.userName}
                                </g:else>
                            </g:remoteLink>
                        </td>
                        <td><g:message code="customer.detail.user.username"/></td>
                        <td class="value">
                            ${partner.user.userName}
                        </td>
                    </tr>
                    <tr>
                        <td><g:message code="partner.detail.number.of.customers"/></td>
                        <td class="value">
                            <g:link controller="customer" action="partner" id="${partner.id}">
                                ${partner.customers?.size() ?: 0}
                            </g:link>
                        </td>
                        <td><g:message code="partner.detail.next.payout"/></td>
                        <td class="value">
                            <g:formatDate date="${partner.nextPayoutDate}"/>
                        </td>
                    </tr>
                        <tr>
                            <td colspan="4">
                                <g:if test="${i < selected.partnersForRelatedClerk.size()-1}"><hr/></g:if>
                            </td>
                        </tr>
                    </g:each>
                </tbody>
            </table>
        </div>
    </div>
    </g:if>

    <div class="btn-box">
        <div class="row">
            <g:link action="permissions" id="${selected.id}" class="submit edit"><span><g:message code="button.edit.permissions"/></span></g:link>
        </div>
        <div class="row">
            <g:if test="${selected && selected.deleted != 1}">
                <g:link action="edit" id="${selected.id}" class="submit edit"><span><g:message code="button.edit"/></span></g:link>
            </g:if>
            <g:if test="${currentUser?.id != selected?.id}">
                <a onclick="showConfirm('delete-${selected.id}');" class="submit delete"><span><g:message code="button.delete"/></span></a>
            </g:if>
        </div>
    </div>

    <g:render template="/confirm"
              model="['message': 'user.delete.confirm',
                      'controller': 'user',
                      'action': 'delete',
                      'id': selected.id,
                      'ajax': true,
                      'update': 'column1',
                      'onYes': 'closePanel(\'#column2\')'
                     ]"/>

</div>
