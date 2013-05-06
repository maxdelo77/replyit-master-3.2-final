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
  _created

  @author Brian Cowdery
  @since  30-11-2010
--%>



<div id="${filter.name}">
    <span class="title <g:if test='${filter.value}'>active</g:if>"><g:message code="filters.${filter.field}.title"/></span>
    <g:remoteLink class="delete" controller="filter" action="remove" params="[name: filter.name]" update="filters"/>

    <div class="slide">
        <fieldset>
            <div class="input-row">
                <div class="input-bg">
                    <a href="#" onclick="$('#filters\\.${filter.name}\\.startDateValue').datepicker('show')"></a>
                    <g:textField class="date-text" name="filters.${filter.name}.startDateValue" value="${formatDate(date: filter.startDateValue, formatName: 'datepicker.format')}"/>
                </div>
                <label for="filters.${filter.name}.startDateValue"><g:message code="filters.date.from.label"/></label>
            </div>

            <div class="input-row">
                <div class="input-bg">
                    <a href="#" onclick="$('#filters\\.${filter.name}\\.endDateValue').datepicker('show')"></a>
                    <g:textField class="date-text" name="filters.${filter.name}.endDateValue" value="${formatDate(date:filter.endDateValue, formatName: 'datepicker.format')}"/>
                </div>
                <label for="filters.${filter.name}.endDateValue"><g:message code="filters.date.to.label"/></label>
            </div>
        </fieldset>

        <script type="text/javascript">
            $(function() {
                $("#filters\\.${filter.name}\\.startDateValue").datepicker({dateFormat: "${message(code: 'datepicker.jquery.ui.format')}" });
                $("#filters\\.${filter.name}\\.endDateValue").datepicker({dateFormat: "${message(code: 'datepicker.jquery.ui.format')}" });
            });
        </script>
    </div>
</div>