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

<%@ page import="org.joda.time.DateMidnight" %>

<%--
  Parameters for the Top Customers report.

  @author Juan Vidal
  @since  03-Feb-2012
--%>

<div class="form-columns">
    <g:applyLayout name="form/text">
        <content tag="label"><g:message code="start_year"/></content>
        <content tag="label.for">start_date</content>
        <g:textField class="{validate:{required:true, digits: true, minlength: 4, maxlength: 4}}" name="start_year"/>
    </g:applyLayout>

    <g:applyLayout name="form/text">
        <content tag="label"><g:message code="end_year"/></content>
        <content tag="label.for">end_date</content>
        <g:textField class="{validate:{required:true, digits: true, minlength: 4, maxlength: 4}}" name="end_year"/>
    </g:applyLayout>
</div>
