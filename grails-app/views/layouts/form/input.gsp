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
  Layout for labeled and styled form input elements.

  Usage:

    <g:applyLayout name="form/input">
        <content tag="label">Field Label</content>
        <content tag="label.for">element_id</content>
        <input type="text" class="field" name="name" id="element_id"/>
    </g:applyLayout>

	style: Use a content tag 'style' to apply additional css class to layoutBody.

  @author Brian Cowdery
  @since  25-11-2010
--%>

<div class="row">    
    <label for="<g:pageProperty name="page.label.for"/>"><g:pageProperty name="page.label"/></label>
    <div class="inp-bg <g:pageProperty name="page.style"/>">
        <g:layoutBody/>
    </div>
</div>