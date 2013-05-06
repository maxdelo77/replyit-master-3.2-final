%{--
  jBilling - The Enterprise Open Source Billing System
  Copyright (C) 2003-2012 Enterprise jBilling Software Ltd. and Emiliano Conde

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

<%@ page contentType="text/html;charset=UTF-8" %>

<%--
  Inject mediation data form for mediation config

  This will translate either into file upload or record(s) insertion

  @author Panche.Isajeski
  @since  29-May-2012
--%>

<div class="column-hold">

    <div class="heading">
        <strong>
            <g:if test="${fileInjectionEnabled}">
                <g:message code="mediation.config.inject.file.title"/>
            </g:if>
            <g:else>
                <g:message code="mediation.config.inject.record.title"/>
            </g:else>
        </strong>
    </div>

    <g:uploadForm id="inject-config-form" name="mediation-config-inject-form" url="[action: 'doInject']">

        <div class="box">
          <div class="sub-box">
            <fieldset>
                <div class="form-columns">

                    <div class="column" style="width: 70%">
                        <g:hiddenField name="id" value="${config?.id?:0}"/>
                        <g:hiddenField name="entityId" value="${session['company_id']}"/>
                        <g:hiddenField name="fileInjectionEnabled" value="${fileInjectionEnabled}"/>

                        <div class="row">
                            <label><g:message code="mediation.config.name"/>:</label>
                            ${config?.name}
                        </div>

                        <div class="row">
                            <label><g:message code="mediation.config.plugin"/>:</label>
                            ${'(' + config.pluggableTask.id + ') ' + config?.pluggableTask?.type?.getDescription(session.language_id)}
                        </div>

                        <div class="row">
                            <g:if test="${fileInjectionEnabled}">
                                <label><g:message code="mediation.config.inject.file"/>:</label>
                                <input type="file" name="events"/>
                            </g:if>
                            <g:else>
                                <label><g:message code="mediation.config.inject.record"/>:</label>
                                <g:textArea cols="40" rows="10" name="recordsString"/>
                            </g:else>
                        </div>
                    </div>
                </div>
            </fieldset>
          </div>
        </div>

    </g:uploadForm>

    <div class="btn-box buttons">
        <ul>
            <li><a class="submit save" onclick="$('#inject-config-form').submit();"><span><g:message code="button.save"/></span></a></li>
            <li><a class="submit cancel" onclick="closePanel(this);"><span><g:message code="button.cancel"/></span></a></li>
        </ul>
    </div>
</div>