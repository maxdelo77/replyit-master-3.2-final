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

<%@ page import="com.sapienter.jbilling.server.user.db.CompanyDTO" %>

<head>
    <meta name="layout" content="public" />

    <title><g:message code="login.page.title"/></title>

    <r:script disposition="head">
        $(document).ready(function() {
            $('#login input[name="j_username"]').focus();

            $(document).keypress(function(e) {
                    if(e.which == 13) {

                        $(this).blur();
                        $('#login form').submit();
                    }
                });

        });
    </r:script>
</head>
<body>

    <g:render template="/layouts/includes/messages"/>

    <div id="login" class="form-edit">
        <div class="heading">
            <strong><g:message code="login.prompt.title"/></strong>
        </div>
        <div class="form-hold">
            <form action='${postUrl}' method='POST' id='login-form' autocomplete='off'>
                <fieldset>

                    <div class="form-columns">
                        <g:applyLayout name="form/input">
                            <content tag="label"><g:message code="login.prompt.username"/></content>
                            <content tag="label.for">username</content>
                            <g:textField class="field" name="j_username" value="${params.userName}"/>
                        </g:applyLayout>

                        <g:applyLayout name="form/input">
                            <content tag="label"><g:message code="login.prompt.password"/></content>
                            <content tag="label.for">password</content>
                            <g:passwordField class="field" name="j_password"/>
                        </g:applyLayout>

                        <g:applyLayout name="form/select">
                            <g:set var="companies" value="${CompanyDTO.createCriteria().list(){eq('deleted', 0)}}"/>
                            <content tag="label"><g:message code="login.prompt.client.id"/></content>
                            <content tag="label.for">client_id</content>
                            <g:if test="${companies}" >
                                <g:select name="j_client_id"
                                          from="${companies}"
                                          optionKey="id"
                                          optionValue="description"
                                          value="${params.companyId}"/>
                            </g:if>
                            <g:else>
                                <g:select name="j_client_id"
                                          from="${companies}"
                                          optionKey="id"
                                          noSelection="['': message(code: 'default.no.selection')]"
                                          optionValue="description"
                                          value="${params.companyId}"/>
                            </g:else>
                        </g:applyLayout>

                        <g:applyLayout name="form/checkbox">
                            <content tag="label"><g:message code="login.prompt.remember.me"/></content>
                            <content tag="label.for">${rememberMeParameter}</content>
                            <g:checkBox class="cb checkbox" name="${rememberMeParameter}" checked="${hasCookie}"/>
                        </g:applyLayout>

                        <br/>
                    </div>

                    <div class="buttons">
                        <ul>
                            <li>
                                <a href="#" class="submit save" onclick="$('#login form').submit();">
                                    <span><g:message code="login.button.submit"/></span>
                                </a>
                            </li>
                        </ul>
                    </div>
                </fieldset>
            </form>
        </div>
    </div>

</body>
</html>