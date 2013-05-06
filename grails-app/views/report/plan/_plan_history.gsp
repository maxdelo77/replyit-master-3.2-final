<%@ page import="com.sapienter.jbilling.server.item.db.PlanDTO; com.sapienter.jbilling.server.user.db.CompanyDTO" %>
%{--
  jBilling - The Enterprise Open Source Billing System
  Copyright (C) 2003-2011 Enterprise jBilling Software Ltd. and Emiliano Conde

  This file is part of jbilling.

  jbilling is free software: you can redistribute it and/or modify
  it under the terms of the GNU Affero General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  jbilling is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU Affero General Public License for more details.

  You should have received a copy of the GNU Affero General Public License
  along with jbilling.  If not, see <http://www.gnu.org/licenses/>.
  --}%

<%--
  Parameters for the Plan Pricing History report.

  @author Brian Cowdery
  @since  28-Sept-2011
--%>

<div class="form-columns">

    <%
        def plans =  PlanDTO.createCriteria().list() {
            item {
                eq('entity', new CompanyDTO(session['company_id']))
            }
        }
    %>

    <script type="text/javascript">
        var plans = {
            <g:each var="plan" in="${plans}">
                ${plan.id}: { plan_code: "${plan.item.internalNumber}", plan_description: "${plan.item.description.replaceAll("\"","'")}" },
            </g:each>
        };
    </script>

    <g:applyLayout name="form/select">
        <content tag="label"><g:message code="plan_id"/></content>
        <content tag="label.for">plan_id</content>
        <g:select from="${plans}"
                  name="plan_id"
                  optionKey="id"
                  optionValue="${{it.item.description}}" />

        <g:hiddenField name="plan_code"/>
        <g:hiddenField name="plan_description"/>
    </g:applyLayout>

    <script type="text/javascript">
        setTimeout(
            function() {
                $('#plan_id').change(function() {
                    var plan = plans[$(this).val()];
                    $('#plan_code').val(plan.plan_code);
                    $('#plan_description').val(plan.plan_description);
                }).change();
            }
        , 500);
    </script>

</div>