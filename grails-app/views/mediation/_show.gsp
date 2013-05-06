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

<%@ page import="org.joda.time.Period; com.sapienter.jbilling.server.util.Constants;" %>

<%--
    @author Vikas Bodani, Pance Isajeski
    @since 18 Feb 2011
 --%>

<div class="column-hold">
    <div class="heading">
        <strong><g:message code="mediation.process.title"/> <em>${selected.id}</em>
        </strong>
    </div>
 
    <div class="box">
        <div class="sub-box">
            <!-- mediation process info -->
            <table cellspacing="0" cellpadding="0" class="dataTable">
                <tbody>
                    <tr>
                        <td><g:message code="mediation.label.id"/></td>
                        <td class="value">${selected.id}</td>
                    </tr>
                    <tr>
                        <td><g:message code="mediation.label.config"/></td>
                        <td class="value">${selected.configuration.name}</td>
                    </tr>
                    <tr>
                        <td><g:message code="mediation.label.start.time"/></td>
                        <td class="value"><g:formatDate date="${selected.startDatetime}" formatName="date.timeSecsAMPM.format"/></td>
                    </tr>
                    <tr>
                        <td><g:message code="mediation.label.end.time"/></td>
                        <td class="value"><g:formatDate date="${selected.endDatetime}" formatName="date.timeSecsAMPM.format"/></td>
                    </tr>
                    <tr>
                        <td><g:message code="mediation.label.total.runtime"/></td>
                        <td class="value">
                            <g:if test="${selected.startDatetime && selected.endDatetime}">
                                <g:set var="runtime" value="${new Period(selected.startDatetime?.time, selected.endDatetime?.time)}"/>
                                <g:message code="mediation.runtime.format" args="[runtime.getHours(), runtime.getMinutes(), runtime.getSeconds()]"/>
                            </g:if>
                            <g:else>
                                -
                            </g:else>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>

    <!-- records info -->
    <div class="heading">
        <strong><g:message code="mediation.process.records"/></strong>
    </div>

    <div class="box">
        <div class="sub-box">
            <table cellpadding="0" cellspacing="0" class="dataTable">
                <tbody>
                    <tr>
                        <td><g:message code="mediation.label.orders.affected"/></td>
                        <td class="value">${ordersCreated.size()}</td>
                        <g:if test="${ordersCreated.size()}">
                            <td class="value">
                                <sec:access url="/order/list">
                                    <g:link controller="order" action="byMediation" id="${selected.id}"
                                            params="${params + ['ids': ordersCreated]}">
                                        <g:message code="mediation.show.all.orders"/>
                                    </g:link>
                                </sec:access>
                            </td>
                        </g:if>
                    </tr>
                    <tr>
                        <td><g:message code="mediation.label.invoices.created"/></td>
                        <td class="value">${invoicesCreated.size()}</td>
                        <g:if test="${invoicesCreated.size()}">
                            <td class="value">
                                <sec:access url="/invoice/list">
                                    <g:link controller="invoice" action="byMediation" id="${selected.id}"
                                            params="${params + ['ids': invoicesCreated]}">
                                        <g:message code="mediation.show.all.invoices"/>
                                    </g:link>
                                </sec:access>
                            </td>
                        </g:if>
                    </tr>
                </tbody>
            </table>

            <hr/>

            <!-- mediation process stats -->
            <table cellspacing="0" cellpadding="0" class="dataTable">
                <tbody>
                    %{
                        def doneBillable = 0;
                        def doneNotBillable = 0;
                        def errorDetected = 0;
                        def errorDeclared = 0;

                        recordStatuses?.each {
                            if (it.statusId == Constants.MEDIATION_RECORD_STATUS_DONE_AND_BILLABLE) doneBillable+=it.count;
                            if (it.statusId == Constants.MEDIATION_RECORD_STATUS_DONE_AND_NOT_BILLABLE) doneNotBillable+=it.count;
                            if (it.statusId == Constants.MEDIATION_RECORD_STATUS_ERROR_DETECTED) errorDetected+=it.count;
                            if (it.statusId == Constants.MEDIATION_RECORD_STATUS_ERROR_DECLARED) errorDeclared+=it.count;
                        }
                    }%

                    <tr>
                        <td><g:message code="mediation.label.done.billable"/></td>
                        <td class="value">${doneBillable}</td>
                        <g:if test="${doneBillable != 0}">
                            <td class="value">
                                <sec:access url="/invoice/list">
                                    <g:link controller="mediation" action="showMediationRecords" id="${selected.id}"
                                            params="${params + ['status': Constants.MEDIATION_RECORD_STATUS_DONE_AND_BILLABLE]}">
                                        <g:message code="mediation.show.all.records"/>
                                    </g:link>
                                </sec:access>
                            </td>
                        </g:if>
                    </tr>
                    <tr>
                        <td><g:message code="mediation.label.done.not.billable"/></td>
                        <td class="value">${doneNotBillable}</td>
                        <g:if test="${doneNotBillable != 0}">
                            <td class="value">
                                <sec:access url="/invoice/list">
                                    <g:link controller="mediation" action="showMediationRecords" id="${selected.id}"
                                            params="${params + ['status': Constants.MEDIATION_RECORD_STATUS_DONE_AND_NOT_BILLABLE]}">
                                        <g:message code="mediation.show.all.records"/>
                                    </g:link>
                                </sec:access>
                            </td>
                        </g:if>
                    </tr>
                    <tr>
                        <td><g:message code="mediation.label.errors.detected"/></td>
                        <td class="value">${errorDetected}</td>
                        <g:if test="${errorDetected != 0}">
                            <td class="value">
                                <sec:access url="/invoice/list">
                                    <g:link controller="mediation" action="showMediationErrors" id="${selected.id}"
                                            params="${params + ['status': Constants.MEDIATION_RECORD_STATUS_ERROR_DETECTED]}">
                                        <g:message code="mediation.show.all.records"/>
                                    </g:link>
                                </sec:access>
                            </td>
                        </g:if>
                    </tr>
                    <tr>
                        <td><g:message code="mediation.label.errors.declared"/></td>
                        <td class="value">${errorDeclared}</td>
                        <g:if test="${errorDeclared != 0}">
                            <td class="value">
                                <sec:access url="/invoice/list">
                                    <g:link controller="mediation" action="showMediationErrors" id="${selected.id}"
                                            params="${params + ['status': Constants.MEDIATION_RECORD_STATUS_ERROR_DECLARED]}">
                                        <g:message code="mediation.show.all.records"/>
                                    </g:link>
                                </sec:access>
                            </td>
                        </g:if>
                    </tr>
                    <tr class="column-hold">
                        <td class="col01"><g:message code="mediation.label.records"/></td>
                        <td class="value">${processRecordSize}</td>
                    </tr>

                </tbody>
            </table>
        </div>
    </div>
    <div class="btn-box"></div>
</div>
