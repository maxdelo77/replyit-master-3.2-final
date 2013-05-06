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
  Shows Mediation Configuration.

  @author Vikas Bodani
  @since  05-Oct-2011
--%>

<div class="column-hold">
    <div class="heading">
        <strong>
            ${selected.name}
        <%-- <em>${selected.id}</em> --%>
        </strong>
    </div>

    <div class="box">
        <div class="sub-box">
          <table class="dataTable" cellspacing="0" cellpadding="0">
            <tbody>
                <tr>
                    <td><g:message code="mediation.config.id" />
                    </td>
                    <td class="value">
                        ${selected.id}
                    </td>
                </tr>
                <tr>
                    <td><g:message code="mediation.config.order" />
                    </td>
                    <td class="value">
                        ${selected?.orderValue}
                    </td>
                </tr>
                <tr>
                    <td><g:message code="mediation.config.plugin" />
                    </td>
                    <td class="value">
                        ${'(' + selected.pluggableTask.id + ') ' + selected?.pluggableTask?.type?.getDescription(session.language_id)}
                    </td>
                </tr>
                <tr>
                    <td><g:message code="mediation.config.processor"/></td>
                    <td class="value">
                        ${'(' + selected.processor?.id + ') ' + selected?.processor?.type?.getDescription(session.language_id)}
                    </td>
                </tr>
            </tbody>
        </table>
      </div>
    </div>

    <div class="btn-box">

        <div class="row">
            <g:if test="${fileInjectionEnabled}">
                <g:remoteLink class="submit order" id="${selected.id}" action="showInject" before="register(this);" onSuccess="render(data, second);"
                params="${params + ['fileInjectionEnabled': true]}"
                update="column2">
                    <span><g:message code="button.inject.file"/></span>
                </g:remoteLink>
            </g:if>
            <g:remoteLink class="submit add" id="${selected.id}" action="showInject" before="register(this);" onSuccess="render(data, second);"
            params="${params + ['fileInjectionEnabled': false]}"
            update="column2">
                <span><g:message code="button.inject.record"/></span>
            </g:remoteLink>
        </div>

        <div class="row">
            <g:remoteLink class="submit edit" id="${selected.id}" action="edit" before="register(this);" onSuccess="render(data, second);" update="column2">
                <span><g:message code="button.edit"/></span>
            </g:remoteLink>
            <a onclick="showConfirm('delete-${selected.id}');" class="submit delete"><span><g:message code="button.delete"/></span></a>
        </div>
    </div>

    <g:render template="/confirm"
              model="['message': 'mediation.config.delete.confirm',
                      'controller': 'mediationConfig',
                      'action': 'delete',
                      'id': selected.id,
                      'ajax': true,
                      'update': 'column1',
                      'onYes': 'closePanel(\'#column2\')'
                     ]"/>
</div>
