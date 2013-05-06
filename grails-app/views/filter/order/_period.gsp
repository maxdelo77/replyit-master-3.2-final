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

<%@ page import="com.sapienter.jbilling.server.order.db.OrderPeriodDTO" %>
<%@ page import="com.sapienter.jbilling.server.user.db.CompanyDTO" %>
<%@ page import="com.sapienter.jbilling.server.util.Constants" %>

<%--
  _status

  @author Vikas Bodani
  @since  31-1-2011
--%>

<div id="${filter.name}">
    <span class="title <g:if test='${filter.value}'>active</g:if>"><g:message code="filters.${filter.field}.title"/></span>
    <g:remoteLink class="delete" controller="filter" action="remove" params="[name: filter.name]" update="filters"/>
    
    <div class="slide">
        <fieldset>
            <div class="input-row">
                <div class="select-bg">
                    <g:set var="company" value="${CompanyDTO.get(session['company_id'])}"/>
                    <g:select name="filters.${filter.name}.integerValue"
                            value="${filter.integerValue}"
                            from="${(company.orderPeriods << new OrderPeriodDTO(Constants.ORDER_PERIOD_ONCE)).sort{it.id}}" 
                            optionKey="id" optionValue="description"
                            noSelection="['': message(code: 'filters.orderPeriod.empty')]" />

                </div>
                <label for="filters.${filter.name}.stringValue"><g:message code="filters.orderPeriod.label"/></label>
            </div>
        </fieldset>
    </div>
</div>

