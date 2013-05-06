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
  
<%@ page contentType="text/html;charset=UTF-8" %>

<%--
  Shows a list of Mediation COnfigurations.

  @author Vikas Bodani
  @since  05-Oct-2011
--%>

<g:if test="${lastMediationProcessStatus}">
    <div class="msg-box wide info" >

        <strong> <g:message code="mediation.config.last.process"/> </strong>
        <g:link controller="mediation" action="show" id="${lastMediationProcessStatus.processId}">
            ${lastMediationProcessStatus.processId}
        </g:link>
    </div>
</g:if>

<div class="table-box">
    <table id="tbl-mediation-config" cellspacing="0" cellpadding="0">
        <thead>
            <tr>
                <th><g:message code="mediation.config.name"/></th>
                <th><g:message code="mediation.config.order"/></th>
                <th><g:message code="mediation.config.plugin"/></th>
            </tr>
        </thead>

        <tbody>
            <g:each var="config" in="${types}">
            
                <g:set var="configReader" value="${readers.find{ it.id == config.pluggableTaskId}}"/>

                <tr id="config-${config.id}" class="${selected?.id == config.id ? 'active' : ''}">
                    <!-- Name ID -->
                    <td>
                        <g:remoteLink class="cell double" action="show" id="${config.id}" before="register(this);" onSuccess="render(data, next);">
                            <strong>${config?.name}</strong>
                            <em><g:message code="table.id.format" args="[config.id as String]"/></em>
                        </g:remoteLink>
                    </td>
                    
                    <!-- Order -->
                    <td>
                        <g:remoteLink class="cell double" action="show" id="${config.id}" before="register(this);" onSuccess="render(data, next);">
                            ${config?.orderValue}
                        </g:remoteLink>
                    </td>
                    
                    <!-- Plugin -->
                    <td>
                        <g:remoteLink class="cell double" action="show" id="${config.id}" before="register(this);" onSuccess="render(data, next);">
                            
                            ${'(' + configReader.id + ') ' + configReader.type?.getDescription(session.language_id)}
                            
                        </g:remoteLink>
                    </td>
                    
                </tr>

            </g:each>
        </tbody>
    </table>
</div>

<div class="btn-box">
    <g:remoteLink class="submit add" action="edit" before="register(this);" onSuccess="render(data, next);">
        <span><g:message code="button.create"/></span>
    </g:remoteLink>

    <g:if test="${!isMediationProcessRunning && types}">
        <g:link controller="mediationConfig" action="run" class="submit apply"><span><g:message code="button.run.mediation"/></span></g:link>
    </g:if>
</div>
