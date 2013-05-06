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
  Quick edit form for the selected customer's notes.

  @author Brian Cowdery
  @since  26-Nov-2010
--%>

<div class="heading">
    <strong><g:message code="customer.detail.edit.note.title"/></strong>
</div>

<g:form id="notes-form" name="notes-form" url="[action: 'saveNotes']">
    <g:hiddenField name="id" value="${selected.id}"/>

    <div class="box">
      <div class="sub-box">
        <div class="box-text">
            <label class="lb"><g:message code="customer.detail.note.title"/></label>
            <g:textArea name="notes" value="${selected.customer.notes}" rows="5" cols="60"/>
        </div>
      </div>
    </div>

    <div class="btn-box buttons">
        <ul>
            <li><a class="submit save" onclick="$('#notes-form').submit();"><span><g:message code="button.save"/></span></a></li>
            <li><a class="submit cancel" onclick="closePanel(this);"><span><g:message code="button.cancel"/></span></a></li>
        </ul>
    </div>
</g:form>