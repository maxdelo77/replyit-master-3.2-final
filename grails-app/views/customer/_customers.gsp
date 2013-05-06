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

<%@ page import="com.sapienter.jbilling.server.user.UserBL; com.sapienter.jbilling.server.user.contact.db.ContactDTO" %>

<%--
  Customer table template. The customer table is used multiple times for rendering the
  main list and for rendering a separate list of sub-accounts. 

  @author Brian Cowdery
  @since  24-Nov-2010
--%>

<div class="table-box">
    <table id="users" cellspacing="0" cellpadding="0">
        <thead>
            <tr>
                <th>
                    <g:remoteSort action="list" sort="userName" update="column1">
                        <g:message code="customer.table.th.name"/>
                    </g:remoteSort>
                </th>
                <th class="small">
                    <g:remoteSort action="list" sort="id" update="column1">
                        <g:message code="customer.table.th.user.id"/>
                    </g:remoteSort>
                </th>
                <th class="tiny2">
                    <g:remoteSort action="list" sort="userStatus.id" update="column1">
                        <g:message code="customer.table.th.status"/>
                    </g:remoteSort>
                </th>
                <th class="small">
                    <g:message code="customer.table.th.balance"/>
                </th>
                <th class="tiny3">
                    <g:message code="customer.table.th.hierarchy"/>
                </th>
            </tr>
        </thead>

        <tbody>
        <g:each in="${users}" var="user">
            <g:set var="customerVar" value="${user.customer}"/>
            <g:set var="contactVar" value="${ContactDTO.findByUserId(user.id)}"/>

            <tr id="user-${user.id}" class="${selected?.id == user.id ? 'active' : ''}">
                <td>
                    <g:remoteLink class="cell double" action="show" id="${user.id}" before="register(this);" onSuccess="render(data, next);">
                        <strong>
                            <g:if test="${contactVar?.firstName || contactVar?.lastName}">
                                ${contactVar.firstName} ${contactVar.lastName}
                            </g:if>
                            <g:else>
                                ${user.userName}
                            </g:else>
                        </strong>
                        <em>${contactVar?.organizationName}</em>
                    </g:remoteLink>
                </td>
                <td>
                    <g:remoteLink class="cell" action="show" id="${user.id}" before="register(this);" onSuccess="render(data, next);">
                        <span>${user.id}</span>
                    </g:remoteLink>
                </td>
                <td class="center">
                    <g:remoteLink class="cell" action="show" id="${user.id}" before="register(this);" onSuccess="render(data, next);">
                        <span>
                            <g:if test="${user.userStatus.id > 1 && user.userStatus.id < 5}">
                                <img src="${resource(dir:'images', file:'icon15.gif')}" alt="overdue" />
                            </g:if>
                            <g:elseif test="${user.userStatus.id >= 5}">
                                <img src="${resource(dir:'images', file:'icon16.gif')}" alt="suspended" />
                            </g:elseif>
                        </span>
                    </g:remoteLink>
                </td>
                <td>
                    <g:remoteLink class="cell" action="show" id="${user.id}" before="register(this);" onSuccess="render(data, next);">
                        <span><g:formatNumber number="${UserBL.getBalance(user.id)}" type="currency"  currencySymbol="${user.currency.symbol}"/></span>
                    </g:remoteLink>
                </td>
                <td class="center">
                    <g:if test="${customerVar}">
                        <g:if test="${customerVar.isParent == 1 && customerVar.parent}">
                            <%-- is a parent, but also a child of another account --%>
                            <g:remoteLink action="subaccounts" id="${user.id}" before="register(this);" onSuccess="render(data, next);">
                                <img src="${resource(dir:'images', file:'icon17.gif')}" alt="parent and child" />
                                <g:set var="children" value="${customerVar.children.findAll{ it.baseUser.deleted == 0 }}"/>
                                <span>${children.size()}</span>
                            </g:remoteLink>
                        </g:if>
                        <g:elseif test="${customerVar.isParent == 1 && !customerVar.parent}">
                            <%-- is a top level parent --%>
                            <g:remoteLink action="subaccounts" id="${user.id}" before="register(this);" onSuccess="render(data, next);">
                                <img src="${resource(dir:'images', file:'icon18.gif')}" alt="parent" />
                                <g:set var="children" value="${customerVar.children.findAll{ it.baseUser.deleted == 0 }}"/>
                                <span>${children.size()}</span>
                            </g:remoteLink>
                        </g:elseif>
                        <g:elseif test="${customerVar.isParent == 0 && customerVar.parent}">
                            <%-- is a child account, but not a parent --%>
                            <img src="${resource(dir:'images', file:'icon19.gif')}" alt="child" />
                        </g:elseif>
                    </g:if>
                </td>
            </tr>

        </g:each>
        </tbody>
    </table>
</div>

<div class="pager-box">
    %{-- remote pager does not support "onSuccess" for panel rendering, take a guess at the update column --}%
    <g:set var="updateColumn" value="${actionName == 'subaccounts' ? 'column2' : 'column1'}"/>

    <div class="row">
        <div class="results">
            <g:render template="/layouts/includes/pagerShowResults" model="[steps: [10, 20, 50], update: updateColumn, contactFieldTypes: contactFieldTypes]" />
        </div>
        <div class="download">
            <sec:access url="/customer/csv">
                <g:link action="csv" params="${sortableParams(params: [partial: true, contactFieldTypes: contactFieldTypes])}" >
                    <g:message code="download.csv.link"/>
                </g:link>
            </sec:access>
        </div>
    </div>

    <div class="row">
        <util:remotePaginate controller="customer" action="list" params="${sortableParams(params: [partial: true, contactFieldTypes: contactFieldTypes])}" total="${users?.totalCount ?: 0}" update="${updateColumn}"/>
    </div>
</div>

<div class="btn-box">
    <sec:ifAllGranted roles="CUSTOMER_10">
        <g:if test="${parent?.customer?.isParent > 0}">
            <sec:ifAnyGranted roles="CUSTOMER_17, CUSTOMER_18">
                <g:link action="edit" params="[parentId: parent.id]" class="submit add"><span><g:message code="customer.add.subaccount.button"/></span></g:link>
            </sec:ifAnyGranted>
        </g:if>
        <g:else>
            <sec:ifAllGranted roles="CUSTOMER_17">
                <g:link action='edit' class="submit add"><span><g:message code="button.create"/></span></g:link>
            </sec:ifAllGranted>
        </g:else>
    </sec:ifAllGranted>
</div>