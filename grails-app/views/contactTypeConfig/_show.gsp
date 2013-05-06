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
  Shows a contact type.

  @author Brian Cowdery
  @since  27-Jan-2011
--%>

<div class="column-hold">
    <div class="heading">
        <strong>
            ${selected.getDescription(session['language_id'])}
            <em>${selected.id}</em>
        </strong>
    </div>

    <div class="box">
        <div class="sub-box">
          <fieldset>
            <div class="form-columns">
                <g:applyLayout name="form/text">
                    <content tag="label"><g:message code="contact.type.label.primary"/></content>
                    <g:formatBoolean boolean="${selected?.isPrimary > 0}"/>
                </g:applyLayout>

                <g:each var="language" in="${languages}">
                    <g:applyLayout name="form/text">
                        <content tag="label">${language.description}</content>
                        ${selected?.getDescription(language.id)}
                    </g:applyLayout>
                </g:each>
            </div>
        </fieldset>
      </div>
    </div>

    <div class="btn-box buttons">
        <div class="row"></div>
    </div>
</div>