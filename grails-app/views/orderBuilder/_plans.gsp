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

<%@ page import="com.sapienter.jbilling.server.user.db.CompanyDTO; com.sapienter.jbilling.server.item.db.ItemTypeDTO" %>

<%--
  Shows the plans list and provides some basic filtering capabilities.

  @author Brian Cowdery
  @since 02-Feb-2011
--%>

<div id="product-box">

    <!-- filter -->
    <div class="form-columns">
        <g:formRemote name="plans-filter-form" url="[action: 'edit']" update="ui-tabs-3" method="GET">
            <g:hiddenField name="_eventId" value="plans"/>
            <g:hiddenField name="execution" value="${flowExecutionKey}"/>

            <g:applyLayout name="form/input">
                <content tag="label"><g:message code="filters.title"/></content>
                <content tag="label.for">filterBy</content>
                <g:textField name="filterBy" class="field default" placeholder="${message(code: 'products.filter.by.default')}" value="${params.filterBy}"/>
            </g:applyLayout>
        </g:formRemote>

        <script type="text/javascript">
            $('#plans-filter-form :input[name=filterBy]').blur(function() { $('#plans-filter-form').submit(); });
            placeholder();
        </script>
    </div>

    <!-- product list -->
    <div class="table-box tab-table">
        <div class="table-scroll">
            <table id="plans" cellspacing="0" cellpadding="0">
                <tbody>

                <g:each var="plan" in="${plans}">
                    <tr>
                        <td>
                            <g:remoteLink class="cell double" action="edit" id="${plan.id}" params="[_eventId: 'addPlan']" update="column2" method="GET">
                                <strong>${plan.getDescription(session['language_id'])}</strong>
                                <em><g:message code="table.id.format" args="[plan.id as String]"/></em>
                            </g:remoteLink>
                        </td>
                        <td class="small">
                            <g:remoteLink class="cell double" action="edit" id="${plan.id}" params="[_eventId: 'addPlan']" update="column2" method="GET">
                                <span>${plan.internalNumber}</span>
                            </g:remoteLink>
                        </td>
                        <td class="medium">
                            <g:remoteLink class="cell double" action="edit" id="${plan.id}" params="[_eventId: 'addPlan']" update="column2" method="GET">
                                <g:if test="${plan.percentage}">
                                    %<g:formatNumber number="${plan.percentage}" formatName="money.format"/>
                                </g:if>
                                <g:else>
                                    <g:set var="price" value="${plan.getPrice(order.activeSince ?: order.createDate ?: new Date())}"/>
                                    <g:formatNumber number="${price?.rate}" type="currency" currencySymbol="${price?.currency?.symbol}"/>
                                </g:else>
                            </g:remoteLink>
                        </td>
                    </tr>
                </g:each>

                </tbody>
            </table>
        </div>
    </div>

</div>