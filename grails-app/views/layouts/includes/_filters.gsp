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

<%@ page import="jbilling.FilterType; com.sapienter.jbilling.server.user.db.CompanyDTO; jbilling.FilterSet" %>

<%--
  Filter side panel template. Prints all filters contained in the "filters" page variable.

  @author Brian Cowdery
  @since  03-12-2010
--%>

<g:set var="company" value="${CompanyDTO.get(session['company_id'])}"/>
<g:set var="filters" value="${filters.sort{ it.field }}"/>
<g:set var="filtersets" value="${FilterSet.findAllByUserId(session['user_id'])}"/>

<%
    filtersets = filtersets?.findAll{ filterset->
        !filterset.filters.find{ it.type != FilterType.ALL && it.type != session['current_filter_type'] }
    }
%>


%{--
    filtersets = filtersets?.findAll{ filterset->
        filterset.filters
        if (filterset.filters && filterset.filters.asList().first().type != session['current_filter_type']) {
            return false
        }
        return true
    }
--}%



<div id="filters">
    <div class="heading">
        <strong><g:message code="filters.title"/></strong>
    </div>

    <!-- filters -->
    <ul class="accordion">
        <g:each var="filter" in="${filters}">
            <g:if test="${filter.visible}">
                <li>

                    <g:render template="/filter/${filter.template}" model="[filter: filter, company: company]"/>
                </li>
            </g:if>
        </g:each>
    </ul>

    <!-- filter controls -->
    <div class="btn-hold">
        <!-- apply filters -->
        <a class="submit apply" onclick="submitApply();">
            <span><g:message code="filters.apply.button"/></span>
        </a>

        <!-- add another filter -->
        <g:if test="${filters.find { !it.visible }}">
            <div class="dropdown">
                <a class="submit add open"><span><g:message code="filters.add.button"/></span></a>
                <div class="drop">
                    <ul>
                        <g:each var="filter" in="${filters}">
                            <g:if test="${!filter.visible}">
                                <li>
                                    <g:remoteLink controller="filter" action="add" params="[name: filter.name]" update="filters">
                                        <g:message code="filters.${filter.field}.title"/>
                                    </g:remoteLink>
                                </li>
                            </g:if>
                        </g:each>
                    </ul>
                </div>
            </div>
        </g:if>

        <!-- save current filter set-->
        <a class="submit2 save" onclick="$('#filter-save-dialog').dialog('open');">
            <span><g:message code="filters.save.button"/></span>
        </a>

        <!-- load saved filter set -->
        <div class="dropdown">
            <a class="submit2 load open"><span><g:message code="filters.load.button"/></span></a>
            <g:if test="${filtersets}">
                <div class="drop">
                    <ul>
                        <g:each var="filterset" in="${filtersets.sort{ it.id }}">
                            <li>
                                <g:remoteLink controller="filter" action="load" id="${filterset.id}" update="filters">
                                    ${filterset.name}
                                </g:remoteLink>
                            </li>
                        </g:each>
                    </ul>
                </div>
            </g:if>
        </div>

        <script type="text/javascript">
            $(function() {
                // reset popups and validations
                setTimeout(
                    function() {
                        initPopups();
                        initScript();

                        var validator = $('#filters-form').validate();
                        validator.init();
                        validator.hideErrors();
                    }, 500);

                // highlight active filters
                $('body').delegate('#filters-form', 'submit', function() {
                    $(this).find('li').each(function() {
                        var title = $(this).find('.title');

                        if ($(this).find(':input[value!=""]').not(':checkbox').length > 0) {
                            title.addClass('active');
                        } else if ($(this).find(':checkbox:checked').length > 0) {
                            title.addClass('active');
                        } else {
                            title.removeClass('active');
                        }
                    });
                });
            });

            function submitApply () {
                if ($('#filters-form .error').size() < 1) {
                    $('#filters-form').submit();
                }
            }
        </script>
    </div>
</div>