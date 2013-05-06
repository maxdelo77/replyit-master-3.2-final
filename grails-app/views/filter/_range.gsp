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
  _range

  @author Brian Cowdery
  @since  11-Apr-2011
--%>

<div id="${filter.name}">
    <span class="title <g:if test='${filter.value}'>active</g:if>"><g:message code="filters.${filter.field}.title"/></span>
    <g:remoteLink class="delete" controller="filter" action="remove" params="[name: filter.name]" update="filters"/>
    
    <div class="slide">
        <fieldset>
            <div class="input-row">
                <div class="input-bg">
                    <g:textField name="filters.${filter.name}.decimalValue" value="${filter.decimalValue}" class="{validate:{ number: true }}"/>
                </div>
                <label for="filters.${filter.name}.decimalValue"><g:message code="filters.${filter.field}.low.label"/></label>
            </div>

            <div class="input-row">
                <div class="input-bg">
                    <g:textField name="filters.${filter.name}.decimalHighValue" value="${filter.decimalHighValue}" class="{validate:{ number: true }}"/>
                </div>
                <label for="filters.${filter.name}.decimalHighValue"><g:message code="filters.${filter.field}.high.label"/></label>
            </div>
        </fieldset>

    </div>
</div>