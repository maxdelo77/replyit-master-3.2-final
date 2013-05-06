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

<g:hiddenField name="type.id" value="${type.id}"/>

<g:applyLayout name="form/input">
    <content tag="label"><g:message code="preference.label.value"/></content>
    <content tag="label.for">preference.strValue</content>
    <g:textField class="field" name="preference.value" value="${preference?.value ?: type.defaultValue}"/>
</g:applyLayout>
