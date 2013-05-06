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

  @author Brian Cowdery
  @since  27-Jan-2011
--%>

<div class="column-hold">
    <div class="heading">
        <strong><g:message code="contact.type.label.new.contact"/></strong>
    </div>

    <g:form id="save-type-form" name="notes-form" url="[action: 'save']">

    <div class="box">
        <div class="sub-box">
          <fieldset>
            <div class="form-columns">
                <g:hiddenField name="id" value="${contactType?.id}"/>

                <g:applyLayout name="form/text">
                    <content tag="label"><g:message code="contact.type.label.primary"/></content>
                    <g:formatBoolean boolean="${contactType?.isPrimary > 0}"/>
                    <g:hiddenField name="isPrimary" value="${contactType?.isPrimary ?: 0}"/>
                </g:applyLayout>

                <g:each var="language" in="${languages}">
                    <g:applyLayout name="form/input">
                        <content tag="label">${language.description}</content>
                        <content tag="label.for">language.${language.id}</content>
                        <g:textField class="field" name="language.${language.id}" value="${contactType?.getDescription(language.id)?.content}"/>
                    </g:applyLayout>
                </g:each>
            </div>
          </fieldset>
        </div>
    </div>

    </g:form>

    <div class="btn-box buttons">
        <ul>
            <li><a class="submit save" onclick="$('#save-type-form').submit();"><span><g:message code="button.save"/></span></a></li>
            <li><a class="submit cancel" onclick="closePanel(this);"><span><g:message code="button.cancel"/></span></a></li>
        </ul>
    </div>
</div>