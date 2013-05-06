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
  Content for the head region of all jBilling layouts.

  @author Brian Cowdery
  @since  23-11-2010
--%>

<meta http-equiv="content-type" content="text/html; charset=utf-8" />

<title><g:layoutTitle default="jBilling" /></title>

<link rel="shortcut icon" href="${resource(dir:'images', file:'favicon.ico')}" type="image/x-icon" />

<r:require modules="jquery, core, ui, input"/>

<g:if test="${ajaxListeners == null || ajaxListeners}">
<r:script disposition='head'>
    function renderMessages() {
        $.ajax({
            url: "${resource(dir:'')}/messages",
            global: false,
            async: false,
            success: function(data) { $("#messages").replaceWith(data); }
        });
    }

    function renderBreadcrumbs() {
        $.ajax({
            url: "${resource(dir:'')}/breadcrumb",
            global: false,
            success: function(data) { $("#breadcrumbs").replaceWith(data); }
       });
    }

    $(document).ajaxSuccess(function(e, xhr, settings) {
        renderMessages();
        renderBreadcrumbs();
    });
    $(document).ajaxError(function(e, xhr, settings) {
        renderMessages();
    });
</r:script>
</g:if>

<r:require module="jquery-ui"/>
<r:external file="/js/jquery-ui/i18n/jquery.ui.datepicker-${session.locale.language}.js"/>

<r:require module="jquery-validate"/>
<r:external file="/js/jquery-validate/i18n/messages_${session.locale.language}.js"/>

<r:script disposition='head'>
    $(document).ready(function() {
        $.validator.setDefaults({
            errorContainer: "#error-messages",
            errorLabelContainer: "#error-messages ul",
            wrapper: "li",
            meta: "validate"
        });

        // minor bug with the filter input fields - this should happen automatically
        // but the 'keyup' event doesn't always bind correctly from the validator itself
        $('#filters-form').delegate('input', 'keyup', function() {
            $('#filters-form').valid();
        });
    })
</r:script>
