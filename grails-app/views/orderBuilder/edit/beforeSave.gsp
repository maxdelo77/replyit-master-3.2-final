
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
  An example information page that is rendered before the order is saved.

  To enable this page, change the builder() web-flow state "save" transition to
  go to either the checkItem() or beforeSave() states.

  @author Brian Cowdery
  @since 17-Feb-2011
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>

</head>
<body>
<div class="form-edit">

    <div class="heading">
        <strong>
            <g:message code="order.review.id" args="[order.id ?: '']"/>
        </strong>
    </div>

    <div class="form-hold">
        <div class="form-columns">
            <p>
                Your page content goes here.<br/>
            </p>
        </div>


        <div class="buttons">
            <ul>
                <li>
                    <g:link class="submit save" action="edit" params="[_eventId: 'save']">
                        <span><g:message code="button.save"/></span>
                    </g:link>
                </li>

                <li>
                    <g:link class="submit cancel" action="edit" params="[_eventId: 'cancel']">
                        <span><g:message code="button.cancel"/></span>
                    </g:link>
                </li>
            </ul>
        </div>
    </div>


</div>
</body>
</html>