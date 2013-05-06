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

<head>
    <meta name='layout' content='main' />
    <title>Denied</title>
</head>

<body>

    <div class="form-edit">
        <div class="heading">
            <strong><g:message code="denied.title"/></strong>
        </div>

        <div class="form-hold">
            <div class="form-columns">
                <p><g:message code="denied.message"/></p>
                <sec:ifSwitched>
                    <p><g:message code="denied.message.user.switched"/></p>
                </sec:ifSwitched>
            </div>

            <!-- spacer -->
            <div>
                <br/>&nbsp;
            </div>
        </div>
    </div>

</body>
