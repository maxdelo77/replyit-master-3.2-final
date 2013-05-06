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

<html>
<head>
    <meta name="layout" content="configuration" />
</head>
<body>
    <!-- selected configuration menu item -->
    <content tag="menu.item">contactType</content>

    <content tag="column1">
        <g:render template="contactTypes" model="[types: types]" />
    </content>

    <content tag="column2">
        <g:if test="${contactType}">
            <g:render template="edit" model="[contactType: contactType, languages: languages]"/>
        </g:if>
    </content>
</body>
</html>
