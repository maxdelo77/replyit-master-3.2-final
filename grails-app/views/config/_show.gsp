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

<%@ page import="com.sapienter.jbilling.server.util.Constants" %>

<div class="column-hold">
    <div class="heading">
        <strong>
            <g:message code="preference.title"/>
            <em>${selected.id}</em>
        </strong>
    </div>

    <g:form name="save-preference-form" url="[controller: 'config', action: 'save']">

    <div class="box">
        <div class="sub-box">
          <p class="description">
              ${selected.getDescription(session['language_id'])}
          </p>
  
          <p>
              <em>${selected.getInstructions(session['language_id'])}</em>
          </p>
  
          <fieldset>
              <div class="form-columns">
  
                  <g:set var="hasPreference" value="${false}"/>
  
                  <g:each var="preference" status="index" in="${selected.preferences}">
                      <g:if test="${preference.jbillingTable.name == Constants.TABLE_ENTITY}">
                          <g:if test="${preference.foreignId == session['company_id']}">
                              <g:set var="hasPreference" value="${true}"/>
                              <g:render template="preference" model="[ preference: preference, type: selected]"/>
                          </g:if>
                      </g:if>
                  </g:each>
  
                  <g:if test="${!hasPreference}">
                      <g:render template="preference" model="[type: selected]"/>
                  </g:if>
  
              </div>
          </fieldset>
      </div>
    </div>

    </g:form>

    <div class="btn-box buttons">
        <ul>
            <li><a class="submit save" onclick="$('#save-preference-form').submit();"><span><g:message code="button.save"/></span></a></li>
            <li><a class="submit cancel" onclick="closePanel(this);"><span><g:message code="button.cancel"/></span></a></li>
        </ul>
    </div>

</div>
