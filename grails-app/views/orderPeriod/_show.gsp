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
  Shows order period.

  @author Vikas Bodani
  @since  30-Sept-2011
--%>

<%@page import="com.sapienter.jbilling.server.process.db.PeriodUnitDTO" %>

<div class="column-hold">
    <div class="heading">
        <strong>
            ${selected.getDescription(session['language_id'].toInteger())}
        <%-- <em>${selected.id}</em> --%>
        </strong>
    </div>

    <div class="box">
        <div class="sub-box">
          <table class="dataTable" cellspacing="0" cellpadding="0">
            <tbody>
            <tr>
                <td><g:message code="orderPeriod.id"/></td>
                <td class="value">${selected.id}</td>
            </tr>
            <%-- 
            <tr>
                <td><g:message code="orderPeriod.description"/></td>
                <td class="value">${selected.getDescription(session['language_id'])}</td>
            </tr>
            --%>
            <tr>
                <td><g:message code="orderPeriod.unit"/></td>
                <td class="value">${selected?.periodUnit?.getDescription(session['language_id'])}</td>
            </tr>
            <tr>
                <td><g:message code="orderPeriod.value"/></td>
                <td class="value">${selected.value}</td>
            </tr>
            </tbody>
        </table>
      </div>
    </div>

    <div class="btn-box">
        <div class="row">
            <g:remoteLink class="submit add" id="${selected.id}" action="edit" update="column2">
                <span><g:message code="button.edit"/></span>
            </g:remoteLink>
            <a onclick="showConfirm('delete-${selected.id}');" class="submit delete"><span><g:message code="button.delete"/></span></a>
        </div>
    </div>

    <g:render template="/confirm"
              model="['message': 'config.period.delete.confirm',
                      'controller': 'orderPeriod',
                      'action': 'delete',
                      'id': selected.id,
                      'ajax': true,
                      'update': 'column1',
                      'onYes': 'closePanel(\'#column2\')'
                     ]"/>
</div>