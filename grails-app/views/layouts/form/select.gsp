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
  Layout for labeled and styled select (drop-down box) elements.

  Usage:

    <g:applyLayout name="form/select">
        <content tag="label">Field Label</content>
        <content tag="label.for">element_id</content>
        <select name="name" id="element_id">
            <option value="1">Option 1</option>
            <option value="2">Option 2</option>
        </select>
    </g:applyLayout>


  @author Brian Cowdery
  @since  25-11-2010
--%>

<div class="row">
    <label for="<g:pageProperty name="page.label.for"/>"><g:pageProperty name="page.label"/></label>
    <g:layoutBody/>
</div>