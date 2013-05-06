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

<%@ page contentType="text/html;charset=UTF-8" %>

<%--
  Plan builder view

  This view doubles as a way to render partial page templates by setting the 'template' parameter. This
  is used as a workaround for rendering AJAX responses from within the web-flow.

  @author Brian Cowdery
  @since 01-Feb-2011
--%>

<g:if test="${params.template}">
    <!-- render the template -->
    <g:render template="${params.template}"/>
</g:if>

<g:else>
    <!-- render the main builder view -->
    <html>
    <head>
        <meta name="layout" content="builder"/>

        <r:script disposition="head">
            $(document).ready(function() {
                $('#builder-tabs').tabs();
            });


            /*
                Controls for refreshing the main components of the view
             */
            var timeline = {
                refresh: function() {
                    return $.ajax({
                        type: 'GET',
                        url: '${createLink(action: 'edit', params:['_eventId': 'timeline'])}',
                        success: function(data) { $('#timeline').replaceWith(data); }
                    });
                }
            };

            var details = {
                refresh: function() {
                    if ($('#timeline').is(':visible')) {
                        return $.ajax({
                            type: 'GET',
                            url: '${createLink(action: 'edit', params:['_eventId': 'details'])}',
                            success: function(data) { $('#details-box').replaceWith(data); }
                         });
                    }
                    return undefined;
                }
            };

            var review = {
                refresh: function () {
                    return $.ajax({
                        type: 'GET',
                        url: '${createLink(action: 'edit', params:['_eventId': 'review'])}',
                        success: function(data) { $('#column2').replaceWith(data); }
                    });
                }
            };
        </r:script>
    </head>
    <body>

    <content tag="top">
        <g:render template="timeline"/>
    </content>

    <content tag="builder">
        <div id="builder-tabs">
            <ul>
                <li><a href="${createLink(action: 'edit', event: 'details')}"><g:message code="builder.details.title"/></a></li>
                <li><a href="${createLink(action: 'edit', event: 'products')}"><g:message code="builder.products.title"/></a></li>
            </ul>
        </div>
    </content>

    <content tag="review">
        <g:render template="review"/>
    </content>

    </body>
    </html>
</g:else>