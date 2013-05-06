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

<%@ page import="com.sapienter.jbilling.server.user.contact.db.ContactDTO" contentType="text/html;charset=UTF-8" %>

<%--
  Shows a list of internal users.

  @author Brian Cowdery
  @since  04-Apr-2011
--%>

<div class="table-box">
    <table id="users" cellspacing="0" cellpadding="0">
        <thead>
            <tr>
                <th><g:message code="users.th.login"/></th>
                <th><g:message code="users.th.name"/></th>
                <th><g:message code="users.th.organization"/></th>
                <th class="small"><g:message code="users.th.role"/></th>
            </tr>
        </thead>

        <tbody>
            <g:each var="user" in="${users}">
                <g:set var="contact" value="${ContactDTO.findByUserId(user.id)}"/>

                <tr id="user-${user.id}" class="${selected?.id == user.id ? 'active' : ''}">
                    <td>
                        <g:remoteLink class="cell double" action="show" id="${user.id}" before="register(this);" onSuccess="render(data, next);">
                            <strong>${user.userName}</strong>
                            <em><g:message code="table.id.format" args="[user.id as String]"/></em>
                        </g:remoteLink>
                    </td>

                    <td>
                        <g:remoteLink class="cell" action="show" id="${user.id}" before="register(this);" onSuccess="render(data, next);">
                            ${contact.firstName} ${contact.lastName}
                        </g:remoteLink>
                    </td>

                    <td>
                        <g:remoteLink class="cell" action="show" id="${user.id}" before="register(this);" onSuccess="render(data, next);">
                            ${contact.organizationName}
                        </g:remoteLink>
                    </td>

                    <td class="small">
                        <g:remoteLink class="cell" action="show" id="${user.id}" before="register(this);" onSuccess="render(data, next);">
                            <g:if test="${user.roles}">
                                ${user.roles.asList().first()?.getTitle(session['language_id'])}
                            </g:if>
                            <g:else>
                                -
                            </g:else>
                        </g:remoteLink>
                    </td>
                </tr>

            </g:each>
        </tbody>
    </table>
</div>

<div class="pager-box">
    <div class="row">
        <div class="results">
            <g:render template="/layouts/includes/pagerShowResults" model="[steps: [10, 20, 50], update: 'column1']"/>
        </div>
    </div>

    <div class="row">
        <util:remotePaginate controller="user" action="list" params="${sortableParams(params: [partial: true])}" total="${users?.totalCount ?: 0}" update="column1"/>
    </div>
</div>

<div class="btn-box">
    <g:link action="edit" class="submit add">
        <span><g:message code="button.create"/></span>
    </g:link>
</div>