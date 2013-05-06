<%@ page import="org.apache.commons.lang.StringUtils" %>
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
<g:hasErrors bean="${selected}">
    <div id="error-messages" class="msg-box error">
        <img src="${resource(dir: 'images', file: 'icon14.gif')}"
             alt="${message(code: 'error.icon.alt', default: 'Error')}"/>
        <strong><g:message code="flash.error.title"/></strong>
        <ul>
            <g:eachError var="err" bean="${selected}">
                <li><g:message error="${err}"/></li>
            </g:eachError>
        </ul>
    </div>
</g:hasErrors>

<div id="filterset-list" class="column">
    <div class="column-hold">
        <div class="table-box">
            <table id="users" cellspacing="0" cellpadding="0">
                <thead>
                <tr>
                    <th><g:message code="filters.save.th.name"/></th>
                    <th class="medium"><g:message code="filters.save.th.filters"/></th>
                </tr>
                </thead>
                <tbody>

                <g:each var="filterset" in="${filtersets}">
                    <tr id="filterset-${filterset.id}" class="${selected?.id == filterset.id ? 'active' : ''}">
                        <td>
                            <g:remoteLink class="cell double" controller="filter" action="edit" id="${filterset.id}" update="filterset-edit">
                                <strong>${StringUtils.abbreviate(filterset.name, 30).encodeAsHTML()}</strong>
                                <em><g:message code="table.id.format" args="[filterset.id]"/></em>
                            </g:remoteLink>
                        </td>
                        <td>
                            <g:set var="count" value="${filterset.filters.findAll { it.value }?.size()}"/>
                            <g:remoteLink class="cell" controller="filter" action="edit" id="${filterset.id}" update="filterset-edit">
                                ${count}
                            </g:remoteLink>
                        </td>
                    </tr>
                </g:each>

                </tbody>
            </table>
        </div>
        <div class="btn-box">
            <div class="row"></div>
        </div>
    </div>
</div>

<div id="filterset-edit" class="column">
    <g:render template="edit" model="[selected: selected, filters: filters]"/>
</div>