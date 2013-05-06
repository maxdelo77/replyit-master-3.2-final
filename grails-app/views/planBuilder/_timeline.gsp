%{--
  jBilling - The Enterprise Open Source Billing System
  Copyright (C) 2003-2011 Enterprise jBilling Software Ltd. and Emiliano Conde

  This file is part of jbilling.

  jbilling is free software: you can redistribute it and/or modify
  it under the terms of the GNU Affero General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  jbilling is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU Affero General Public License for more details.

  You should have received a copy of the GNU Affero General Public License
  along with jbilling.  If not, see <http://www.gnu.org/licenses/>.
  --}%

<%@ page import="com.sapienter.jbilling.common.CommonConstants; com.sapienter.jbilling.server.pricing.PriceModelWS" contentType="text/html;charset=UTF-8" %>

<div id="timeline">
    <div class="form-columns">
        <ul>
            <g:if test="${pricingDates}">
                <g:each var="date" status="i" in="${pricingDates}">
                    <li class="${startDate.equals(date) ? 'current' : ''}">
                        <g:set var="pricingDate" value="${formatDate(date: date)}"/>
                        <g:remoteLink action="edit" params="[_eventId: 'editDate', startDate: pricingDate]"
                                      update="column2" method="GET" onSuccess="timeline.refresh(); details.refresh();">
                            ${pricingDate}
                        </g:remoteLink>
                    </li>
                </g:each>
            </g:if>
            <g:else>
                <li class="current">
                    <g:set var="pricingDate" value="${formatDate(date: CommonConstants.EPOCH_DATE)}"/>
                    <g:remoteLink action="edit" params="[_eventId: 'editDate', startDate : pricingDate]"
                                  update="column2" method="GET" onSuccess="timeline.refresh(); details.refresh();">
                        ${pricingDate}
                    </g:remoteLink>
                </li>
            </g:else>

            <li class="new">
                <a onclick="$('#add-date-dialog').dialog('open');">
                    <g:message code="button.add.price.date"/>
                </a>
            </li>
        </ul>
    </div>

    <div id="add-date-dialog" title="Add Date">
        <g:formRemote name="add-date-form" url="[action: 'edit']" update="column2" method="GET">
            <g:hiddenField name="_eventId" value="addDate"/>
            <g:hiddenField name="execution" value="${flowExecutionKey}"/>

            <div class="column">
                <div class="columns-holder">
                    <fieldset>
                        <div class="form-columns">
                            <g:applyLayout name="form/date">
                                <content tag="label"><g:message code="plan.item.start.date"/></content>
                                <content tag="label.for">startDate</content>
                                <g:textField class="field" name="startDate" value="${formatDate(date: new Date(), formatName: 'datepicker.format')}"/>
                            </g:applyLayout>
                        </div>
                    </fieldset>
                </div>
            </div>
        </g:formRemote>
    </div>

    <script type="text/javascript">
        $('#add-date-dialog').dialog({
             autoOpen: false,
             height: 400,
             width: 520,
             modal: true,
             buttons: {
                 Cancel: function() {
                     $(this).dialog("close");
                 },
                 Save: function() {
                     $('#add-date-form').submit();
                     $(this).dialog("close");
                     timeline.refresh();
                 }
             }
         });
    </script>
</div>

