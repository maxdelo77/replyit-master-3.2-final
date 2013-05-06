<%@ page import="com.sapienter.jbilling.server.util.db.EnumerationDTO; com.sapienter.jbilling.server.user.db.CompanyDTO; com.sapienter.jbilling.server.metafields.db.DataType" %>
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

<html>
<head>
    <meta name="layout" content="main" />

    <r:script disposition="head">
        $(document).ready(function() {
            $('#metaField\\.dataType').change(function() {
                if ($(this).val() == '${DataType.ENUMERATION}' || $(this).val() == '${DataType.LIST}') {
                    $('#field-name').hide().find('input').prop('disabled', 'true');
                    $('#field-enumeration').show().find('select').prop('disabled', '');
                } else {
                    $('#field-name').show().find('input').prop('disabled', '');
                    $('#field-enumeration').hide().find('select').prop('disabled', 'true');
                }
            }).change();
        });
    </r:script>
</head>
<body>
<div class="form-edit">

    <g:set var="isNew" value="${!metaField || !metaField?.id || metaField?.id == 0}"/>

    <div class="heading">
        <strong>
            <g:if test="${isNew}">
                <g:message code="metaField.add.title"/>
            </g:if>
            <g:else>
                <g:message code="metaField.edit.title"/>
            </g:else>
        </strong>
    </div>

    <div class="form-hold">
        <g:form name="metaField-edit-form" action="save">
            <fieldset>

                <!-- role information -->
                <div class="form-columns">
                    <div class="column">
                        <g:hiddenField name="entityType" value="${isNew ? params.entityType : metaField?.entityType?.name()}"/>

                        <g:applyLayout name="form/text">
                            <content tag="label"><g:message code="metaField.label.id"/></content>

                            <g:if test="${!isNew}">
                                <span>${metaField.id}</span>
                            </g:if>
                            <g:else>
                                <em><g:message code="prompt.id.new"/></em>
                            </g:else>

                            <g:hiddenField name="metaField.id" value="${metaField?.id}"/>
                        </g:applyLayout>

                        <div id="field-name">
                            <g:applyLayout name="form/input">
                                <content tag="label"><g:message code="metaField.label.name"/></content>
                                <content tag="label.for">metaField.name</content>
                                <g:textField class="field" name="metaField.name" value="${metaField?.name}"/>
                            </g:applyLayout>
                        </div>
                        <div id="field-enumeration" style="display: none;">
                            <g:applyLayout name="form/select">
                                <content tag="label"><g:message code="metaField.label.name"/></content>
                                <content tag="label.for">metaField.name</content>
                                <g:select name="metaField.name" class="field"
                                          from="${EnumerationDTO.findAllByEntity(new CompanyDTO(session['company_id']))}"
                                          value="${metaField?.name}"
                                          optionKey="name"
                                          optionValue="name"/>
                            </g:applyLayout>
                        </div>

                        <g:applyLayout name="form/select">
                            <content tag="label"><g:message code="metaField.label.dataType"/></content>
                            <content tag="label.for">metaField.dataType</content>
                            <g:set var="dataTypes" value="${DataType.values()}"/>
                            <g:select
                                disabled="${!isNew}"
                                class="field"
                                name="metaField.dataType"
                                from="${dataTypes}"
                                value="${metaField?.dataType}" />
                            <g:if test="${!isNew}">
                              <g:hiddenField name="metaField.dataType" value="${metaField?.dataType}"/>
                            </g:if>
                        </g:applyLayout>

                        <g:applyLayout name="form/checkbox">
                            <content tag="label"><g:message code="metaField.label.mandatory"/></content>
                            <content tag="label.for">metaField.mandatory</content>
                            <g:checkBox class="cb checkbox" name="metaField.mandatory" checked="${metaField?.mandatory}"/>
                        </g:applyLayout>

                        <g:applyLayout name="form/checkbox">
                            <content tag="label"><g:message code="metaField.label.disabled"/></content>
                            <content tag="label.for">metaField.disabled</content>
                            <g:checkBox class="cb checkbox" name="metaField.disabled" checked="${metaField?.disabled}"/>
                        </g:applyLayout>

                        <g:applyLayout name="form/input">
                            <content tag="label"><g:message code="metaField.label.displayOrder"/></content>
                            <content tag="label.for">metaField.displayOrder</content>
                            <g:textField class="field" name="metaField.displayOrder" value="${metaField?.displayOrder}"/>
                        </g:applyLayout>

                        <g:applyLayout name="form/input">
                            <content tag="label"><g:message code="metaField.label.defaultValue"/></content>
                            <content tag="label.for">defaultValue</content>
                            <g:textField class="field" name="defaultValue" value="${metaField?.defaultValue?.value}"/>
                        </g:applyLayout>
                    </div>
                </div>

                <!-- spacer -->
                <div>
                    &nbsp;<br/>
                </div>

                <div class="buttons">
                    <ul>
                        <li>
                            <a onclick="$('#metaField-edit-form').submit()" class="submit save"><span><g:message code="button.save"/></span></a>
                        </li>
                        <li>
                            <g:link action="listCategories" class="submit cancel"><span><g:message code="button.cancel"/></span></g:link>
                        </li>
                    </ul>
                </div>

            </fieldset>
        </g:form>
    </div>
</div>
</body>
</html>