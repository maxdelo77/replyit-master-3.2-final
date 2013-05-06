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
  Text field with a date selector.

  @author Brian Cowdery
  @since  06-Jan-2011
--%>

<g:set var="jquerySelector" value="#${pageProperty(name: 'page.label.for').replaceAll('\\.','\\\\\\\\\\.')}"/>

<div class="row">
    <label for="<g:pageProperty name="page.label.for"/>"><g:pageProperty name="page.label"/></label>
    <div class="inp-bg date">
        <g:layoutBody/>
    </div>

    <script type="text/javascript">
        // wait to initialize the date picker if it's not visible
        setTimeout(
                function() {
                    var options = $.datepicker.regional['${session.locale.language}'];
                    if (options == null) options = $.datepicker.regional[''];

                    options.dateFormat = "${message(code: 'datepicker.jquery.ui.format')}";
                    options.showOn = "both";
                    options.buttonImage = "${resource(dir:'images', file:'icon04.gif')}";
                    options.buttonImageOnly = true;

                    <g:if test="${pageProperty(name: 'page.onClose')}">
                    options.onClose = ${pageProperty(name: 'page.onClose')}
                    </g:if>

                    $("${jquerySelector}").datepicker(options);
                },
                $('${jquerySelector}').is(":visible") ? 0 : 500
        );
    </script>
</div>

