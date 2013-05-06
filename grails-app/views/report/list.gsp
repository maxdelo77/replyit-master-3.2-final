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
    <g:javascript src="form.js" />
    <script type="text/javascript">
        function validateDate(element) {
            var dateFormat= "<g:message code="date.format"/>";
            if(!isValidDate(element, dateFormat)) {
                $("#error-messages").css("display","block");
                $("#error-messages ul").css("display","block");
                $("#error-messages ul").html("<li><g:message code="invalid.date.format"/></li>");
                //element.focus();
                return false;
            } else {
                return true;
            }
        }
    </script>
</head>
<body>

<g:if test="${!selected}">
    <!-- show report types and reports -->
    <content tag="column1">
        <g:render template="types" model="[types: types]"/>
    </content>

    <content tag="column2">
        <g:render template="reports" model="[reports: reports, selectedTypeId: selectedTypeId ]"/>
    </content>
</g:if>
<g:else>
    <!-- show reports list and selected report -->
    <content tag="column1">
        <g:render template="reports" model="[reports: reports, selectedTypeId: selectedTypeId ]"/>
    </content>

    <content tag="column2">
        <g:render template="show" model="[selected: selected]"/>
    </content>
</g:else>

</body>
</html>