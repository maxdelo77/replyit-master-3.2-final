
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
  Plans list table.

  @author Brian Cowdery
  @since  01-Feb-2011
--%>

<div class="table-box">
    <div class="table-scroll">
        <table id="plans" cellspacing="0" cellpadding="0">
            <thead>
                <tr>
                    <th>
                        <g:remoteSort action="list" sort="id" update="column1">
                            <g:message code="plan.th.name"/>
                        </g:remoteSort>
                    </th>
                    <th class="medium">
                        <g:remoteSort action="list" sort="i.internalNumber" update="column1">
                            <g:message code="plan.th.item.number"/>
                        </g:remoteSort>
                    </th>
                    <th class="small">
                        <g:message code="plan.th.products"/>
                    </th>
                </tr>
            </thead>

            <tbody>
            <g:each var="plan" in="${plans}">
                <tr id="plan-${plan.id}" class="${selected?.id == plan.id ? 'active' : ''}">

                    <td>
                        <g:remoteLink class="cell double" action="show" id="${plan.id}" before="register(this);" onSuccess="render(data, next);">
                            <strong>${plan.item.description}</strong>
                            <em><g:message code="table.id.format" args="[plan.id as String]"/></em>
                        </g:remoteLink>
                    </td>
                    <td>
                        <g:remoteLink class="cell" action="show" id="${plan.id}" before="register(this);" onSuccess="render(data, next);">
                            <strong>${plan.item.internalNumber}</strong>
                        </g:remoteLink>
                    </td>
                    <td>
                        <g:remoteLink class="cell" action="show" id="${plan.id}" before="register(this);" onSuccess="render(data, next);">
                            <span>${plan.planItems?.size()}</span>
                        </g:remoteLink>
                    </td>

                </tr>
            </g:each>
            </tbody>
        </table>
    </div>
</div>

<g:if test="${plans?.totalCount > params.max}">
    <div class="pager-box">
        <div class="row left">
            <g:render template="/layouts/includes/pagerShowResults" model="[steps: [10, 20, 50], update: 'column1']"/>
        </div>
        <div class="row">
            <util:remotePaginate controller="plan" action="list" params="${sortableParams(params: [partial: true])}" total="${plans.totalCount}" update="column1"/>
        </div>
    </div>
</g:if>

<div class="btn-box">
    <sec:ifAllGranted roles="PLAN_60">
        <g:link controller="planBuilder" action="edit" class="submit add"><span><g:message code="button.create"/></span></g:link>
    </sec:ifAllGranted>
</div>