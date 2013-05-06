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
  Parameters for the GL Summary report.

  @author Brian Cowdery
  @since  30-Mar-2011
--%>

<div class="form-columns">
    <g:applyLayout name="form/date">
        <content tag="label"><g:message code="end_date"/></content>
        <content tag="label.for">date</content>
        <g:textField class="field" name="date" value="${formatDate(date: new Date(), formatName: 'datepicker.format')}" onblur="validateDate(this)"/>
    </g:applyLayout>
</div>