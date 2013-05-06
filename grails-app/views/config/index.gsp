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
    <content tag="menu.item">all</content>

    <content tag="column1">
       <g:render template="all" />
    </content>

    <content tag="column2">
        <g:if test="${selected}">
            <!-- show selected preference type -->
            <g:render template="show" model="[selected: selected]"/>
        </g:if>
    </content>
</body>
</html>