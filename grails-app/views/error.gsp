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
<sec:ifLoggedIn>
        <meta name="layout" content="main" />
    </sec:ifLoggedIn>

    <sec:ifNotLoggedIn>
        <meta name="layout" content="public" />
    </sec:ifNotLoggedIn>

    <title><g:message code="exception.page.title"/></title>
</head>

<body>

<g:if test="${exception}">
    <div class="msg-box error wide">
        <img src="${resource(dir:'images', file:'icon14.gif')}" alt="${message(code:'error.icon.alt',default:'Error')}"/>
        <strong><g:message code="flash.exception.message.title"/></strong>
        <p>
            <g:message code="flash.exception.message"/>
        </p>
    </div>


    <div class="form-edit">
        <div class="heading">
            <strong><g:message code="exception.code.title" args="[request.'javax.servlet.error.status_code']"/></strong>
        </div>

        <div class="form-hold">
            <div class="form-columns">

                <!-- error details -->
                <table cellpadding="0" cellspacing="0" class="dataTable">
                    <tr>
                        <td><g:message code="exception.uri"/></td>
                        <td class="value">${request['javax.servlet.error.request_uri']}</td>
                    </tr>

                    <tr>
                        <td><g:message code="exception.message"/></td>
                        <td class="value">${exception.message?.encodeAsHTML()}</td>
                    </tr>

                    <tr>
                        <td><g:message code="exception.cause"/></td>
                        <td class="value">${exception.cause?.message?.encodeAsHTML()}</td>
                    </tr>

                    <tr>
                        <td><g:message code="exception.source"/></td>
                        <td class="value">
                            <g:message code="exception.message" args="[exception.className, exception.lineNumber]"/>
                        </td>
                    </tr>
                </table>

                <g:if test="${exception.codeSnippet}">
                    <div class="code">
                        <g:each var="cs" in="${exception.codeSnippet}">
                            ${cs?.encodeAsHTML()}<br />
                        </g:each>
                    </div>
                </g:if>
            </div>

            <!-- stack trace -->
            <div class="box-cards">
                <div class="box-cards-title">
                    <a class="btn-open"><span><g:message code="exception.stack.trace.title"/></span></a>
                </div>
                <div class="box-card-hold">
                    <div class="form-columns">

                        <div class="code stacktrace">
                            <pre><g:each in="${exception.stackTraceLines}">${it.encodeAsHTML()}</g:each></pre>
                        </div>

                    </div>
                </div>
            </div>

            <!-- spacer -->
            <div>
                <br/>&nbsp;
            </div>
        </div>
    </div>
</g:if>


</body>
</html>