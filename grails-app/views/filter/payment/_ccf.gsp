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

<%@ page import="com.sapienter.jbilling.server.metafields.db.EntityType; com.sapienter.jbilling.server.metafields.MetaFieldBL" %>
<%@ page import="com.sapienter.jbilling.server.user.db.CompanyDTO" %>
<%@ page import="com.sapienter.jbilling.server.util.Constants" %>

<%--
  _status

  @author Amol Gadre
  @since  18-10-2012
--%>

<div id="${filter.name}">
    <span class="title <g:if test='${filter.value}'>active</g:if>"><g:message code="filters.${filter.field}.title"/></span>
    <g:remoteLink class="delete" controller="filter" action="remove" params="[name: filter.name]" update="filters"/>
    
    <div class="slide">
        <fieldset>
            <div class="input-row">
                <div class="select-bg" style="float:left;">
                    <g:set var="company" value="${CompanyDTO.get(session['company_id'])}"/>
                    <g:select style="float:left;"  
                            name="contactFieldTypes" 
                            from="${MetaFieldBL.getAvailableFieldsList (session['company_id'], EntityType.PAYMENT)}"
                            optionKey="id" optionValue="name"
                            noSelection="['': message(code: 'filters.contactFieldTypes.empty')]" />
                </div>
                <div class="input-bg">
                    <g:textField name="filters.${filter.name}.stringValue" value="${filter.stringValue}" class="{validate:{ maxlength: 50 }}"/>
                </div>
                <label for="filters.${filter.name}.stringValue"><g:message code="filters.value.label"/></label>
            </div>
        </fieldset>
    </div>
</div>

