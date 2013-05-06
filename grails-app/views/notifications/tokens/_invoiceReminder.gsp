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

<div class="row">
    <div>
        <span>
            <u><g:message code="label.token.invoice.details"/></u>
        </span>
    </div>
</div>
<div class="row">
    <div>
        <a href="javascript:void(0)" onclick="testfunc('$days');"
            class=""
        ><span><g:message
                    code="label.token.days.before.invoice.is.due"
                />
        </span>
        </a>
    </div>
</div>
<div class="row">
    <div>
        <a href="javascript:void(0)" onclick="testfunc('$dueDate');"
            class=""
        ><span><g:message
                    code="label.token.invoice.due.date"
                />
        </span>
        </a>
    </div>
</div>
<div class="row">
    <div>
        <a href="javascript:void(0)" onclick="testfunc('$number');"
            class=""
        ><span><g:message code="label.token.invoice.number" />
        </span>
        </a>
    </div>
</div>
<div class="row">
    <div>
        <a href="javascript:void(0)" onclick="testfunc('$total');"
            class=""
        ><span><g:message code="label.token.invoice.total.due" />
        </span>
        </a>
    </div>
</div>
<div class="row">
    <div>
        <a href="javascript:void(0)" onclick="testfunc('$date');"
            class=""
        ><span><g:message
                    code="label.token.invoice.sent.date"
                />
        </span>
        </a>
    </div>
</div>
