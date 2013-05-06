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

%{--show all user's details--}%
<sec:ifAllGranted roles="MENU_90">
    <content tag="column1">
        <g:render template="customers" model="[users: users]"/>
    </content>

    <content tag="column2">
        <g:if test="${selected}">
            <!-- show selected user details -->
            <g:render template="show" model="[selected: selected, contact: contact]"/>
        </g:if>
    <g:else>
            <!-- show empty block -->
        <div class="heading"><strong><em><g:message code="customer.detail.not.selected.title"/></em></strong></div>
        <div class="box"><div class="sub-box"><em><g:message code="customer.detail.not.selected.message"/></em></div></div>
        <div class="btn-box"></div>
    </g:else>
</content>
</sec:ifAllGranted>

%{--just show details of the current user--}%
<sec:ifNotGranted roles="MENU_90">
    <content tag="column1">
        <g:if test="${selected}">
            <!-- show selected user details only -->
            <g:render template="show" model="[selected: selected, contact: contact]"/>
        </g:if>
        <g:else>
            <!-- show empty block -->
            <div class="heading"><strong><em><g:message code="customer.detail.not.selected.title"/></em></strong></div>
            <div class="box"><div class="sub-box"><em><g:message code="customer.detail.not.selected.message"/></em></div></div>
            <div class="btn-box"></div>
        </g:else>
    </content>
</sec:ifNotGranted>

</body>
</html>