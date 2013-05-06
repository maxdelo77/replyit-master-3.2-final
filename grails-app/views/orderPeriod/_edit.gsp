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

<%@ page contentType="text/html;charset=UTF-8" %>

<%--
  Shows edit form for a contact type.

  @author Vikas Bodani
  @since  30-Sept-2011
--%>

<div class="column-hold">
    
    <g:set var="isNew" value="${!period || !period?.id || period?.id == 0}"/>
    
    <div class="heading">
        <strong>
            <g:if test="${isNew}">
                <g:message code="period.add.title"/>
            </g:if>
            <g:else>
                <g:message code="period.edit.title"/>
            </g:else>
        </strong>
    </div>

    <g:form id="save-period-form" name="order-period-form" url="[action: 'save']" >
    <input type="hidden" name="isNew" value="${isNew}">
    <div class="box">
        <div class="sub-box">
          <fieldset>
            <div class="form-columns">
                <g:hiddenField name="id" value="${period?.id}"/>

                <g:applyLayout name="form/input">
                    <content tag="label"><g:message code="orderPeriod.description"/></content>
                    <content tag="label.for">description</content>
                    <g:textField class="field" name="description" value="${periodWS?periodWS.getDescription(session['language_id'])?.content:period?.getDescription(session['language_id'])}"/>
                </g:applyLayout>

                <g:applyLayout name="form/select">
                    <content tag="label"><g:message code="orderPeriod.unit"/></content>
                    <content tag="label.for">periodUnit</content>
                    <g:select from="${periodUnits}"
                              optionKey="id"
                              optionValue="${{it.getDescription(session['language_id'])}}"
                              name="periodUnitId"
                              value="${periodWS?periodWS.periodUnitId:period?.periodUnit?.id}"/>
                </g:applyLayout>

                <g:applyLayout name="form/input">
                    <content tag="label"><g:message code="orderPeriod.value"/></content>
                    <content tag="label.for">periodValue</content>
                    <g:textField class="field" name="value" value="${periodWS?periodWS.value:period?.value}"/>
                </g:applyLayout>
                
            </div>
        </fieldset>
      </div>
    </div>

    </g:form>

    <div class="btn-box buttons">
        <ul>
            <li><a class="submit save" onclick="$('#save-period-form').submit();"><span><g:message code="button.save"/></span></a></li>
            <li><a class="submit cancel" onclick="closePanel(this);"><span><g:message code="button.cancel"/></span></a></li>
        </ul>
    </div>
</div>