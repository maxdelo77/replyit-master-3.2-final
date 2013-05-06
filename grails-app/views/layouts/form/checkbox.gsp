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
  Layout for labeled and styled checkbox input elements.

  Usage:

    <g:applyLayout name="form/checkbox">
        <content tag="label">Checkbox Label</content>
        <content tag="label.for">element_name</content>
        
        <g:checkBox name="element_name" class="cb check" ... />
    </g:applyLayout>


  @author Brian Cowdery
  @since  25-11-2010
--%>

<div class="row">
    <label>${pageProperty(name: 'page.group.label') ?: '&nbsp;'}</label>
    <g:layoutBody/>
    <label for="<g:pageProperty name="page.label.for"/>" class="lb"><g:pageProperty name="page.label"/></label>
</div>
