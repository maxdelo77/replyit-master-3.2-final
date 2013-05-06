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

<%@ page import="com.sapienter.jbilling.server.user.contact.db.ContactDTO" %>
<html>
<head>
    <meta name="layout" content="panels" />
</head>
<body>

<content tag="column1">
    <g:render template="plans" model="['plans': plans]"/>
</content>

<content tag="column2">
    <g:if test="${selected}">
        <!-- show selected payment details -->
        <g:render template="show" model="['plan': selected]"/>
    </g:if>
    <g:else>
        <!-- show empty block -->
        <div class="heading"><strong><em><g:message code="plan.not.selected.title"/></em></strong></div>
        <div class="box"><div class="sub-box"><em><g:message code="plan.not.selected.message"/></em></div></div>
        <div class="btn-box"></div>
    </g:else>
</content>

</body>
</html>