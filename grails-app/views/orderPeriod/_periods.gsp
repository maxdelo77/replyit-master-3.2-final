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
  
<%@page import="com.sapienter.jbilling.server.process.db.PeriodUnitDTO" %>

<%@ page contentType="text/html;charset=UTF-8" %>

<%--
  Shows a list of order periods.

  @author Vikas Bodani
  @since  30-Sept-2011
--%>

<div class="table-box">
    <table id="periods" cellspacing="0" cellpadding="0">
        <thead>
            <tr>
                <th class="medium"><g:message code="orderPeriod.description"/></th>
                <th class="medium"><g:message code="orderPeriod.unit"/></th>
                <th class="large"><g:message code="orderPeriod.value"/></th>
            </tr>
        </thead>

        <tbody>
            <g:each var="period" in="${periods}">

                <tr id="period-${period.id}" class="${selected?.id == period.id ? 'active' : ''}">
                    <!-- ID -->
                    <td>
                        <g:remoteLink class="cell double" action="show" id="${period.id}" before="register(this);" onSuccess="render(data, next);">
                            <strong>${period?.getDescription(session['language_id'])}</strong>
                            <em><g:message code="table.id.format" args="[period.id]"/></em>
                        </g:remoteLink>
                    </td>
                    
                    <!-- Unit -->
                    <td>
                        <g:remoteLink class="cell double" action="show" id="${period.id}" before="register(this);" onSuccess="render(data, next);">
                            ${period?.periodUnit?.getDescription(session['language_id'])}
                        </g:remoteLink>
                    </td>
                    
                    <!-- Value -->
                    <td>
                        <g:remoteLink class="cell double" action="show" id="${period.id}" before="register(this);" onSuccess="render(data, next);">
                            ${period.value}
                        </g:remoteLink>
                    </td>
                    
                </tr>

            </g:each>
        </tbody>
    </table>
</div>

<div class="btn-box">
    <g:remoteLink class="submit add" action="edit" before="register(this);" onSuccess="render(data, next);">
        <span><g:message code="button.create"/></span>
    </g:remoteLink>
</div>