<%@ page import="org.apache.commons.lang.StringUtils; com.sapienter.jbilling.server.metafields.db.DataType; com.sapienter.jbilling.server.user.db.CompanyDTO; com.sapienter.jbilling.server.util.db.EnumerationDTO" %>
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
  View template for rendering meta-field input's to create and update meta-field values..

  <g:render template="/metaFields/editMetaFields" model="[availableFields: availableFields, fieldValues: object.metaFields]"/>

  @author Brian Cowdery
  @since 26-Oct-2011
--%>

<g:set var="enumerations" value="${EnumerationDTO.createCriteria().list(){eq('entity', new CompanyDTO(session['company_id']))}}"/>

<g:each var="field" in="${availableFields?.sort{ it.displayOrder }}">
    <g:if test="${!field.disabled}">
        <g:set var="fieldName" value="${StringUtils.abbreviate(message(code: field.name), 50).encodeAsHTML()}"/>
        <g:set var="fieldValue" value="${fieldValues?.find{ it.fieldName == field.name }?.getValue()}"/>
        <g:if test="${fieldValue == null && field.getDefaultValue()}">
            <g:set var="fieldValue" value="${field.getDefaultValue().getValue()}"/>
        </g:if>
        <g:elseif test="${g.ifValuePresent(field:field,fieldsArray: fieldsArray)}">
            <g:set var="fieldValue" value="${g.setFieldValue(field:field, fieldsArray:fieldsArray)}"/>
        </g:elseif>

        <!-- string fields -->
        <g:if test="${field.getDataType() == DataType.STRING}">
            <g:applyLayout name="form/input">
                <content tag="label"><g:message code="${field.name}"/><g:if test="${field.mandatory}"><span id="mandatory-meta-field">*</span></g:if></content>
                <content tag="label.for">metaField_${field.id}.value</content>

                <g:textField name="metaField_${field.id}.value"
                             class="field text"
                             value="${fieldValue}"/>
            </g:applyLayout>
        </g:if>

        <g:if test="${field.getDataType() == DataType.JSON_OBJECT}">
            <g:applyLayout name="form/input">
                <content tag="label"><g:message code="${field.name}"/><g:if test="${field.mandatory}"><span id="mandatory-meta-field">*</span></g:if></content>
                <content tag="label.for">metaField_${field.id}.value</content>

                <g:textField name="metaField_${field.id}.value"
                             class="field text"
                             value="${fieldValue}"/>
            </g:applyLayout>
        </g:if>

        <!-- integer fields -->
        <g:if test="${field.getDataType() == DataType.INTEGER}">
            <g:applyLayout name="form/input">
                <content tag="label"><g:message code="${field.name}"/><g:if test="${field.mandatory}"><span id="mandatory-meta-field">*</span></g:if></content>
                <content tag="label.for">metaField_${field.id}.value</content>

                <g:textField name="metaField_${field.id}.value"
                             class="field text {validate:{ digits: true }}"
                             value="${fieldValue}"/>
            </g:applyLayout>
        </g:if>

        <!-- decimal fields -->
        <g:if test="${field.getDataType() == DataType.DECIMAL}">
            <g:applyLayout name="form/input">
                <content tag="label"><g:message code="${field.name}"/><g:if test="${field.mandatory}"><span id="mandatory-meta-field">*</span></g:if></content>
                <content tag="label.for">metaField_${field.id}.value</content>

                <g:textField name="metaField_${field.id}.value"
                             class="field text {validate:{ number: true }}"
                             value="${fieldValue}"/>
            </g:applyLayout>
        </g:if>

        <!-- boolean fields -->
        <g:if test="${field.getDataType() ==  DataType.BOOLEAN}">
            <g:applyLayout name="form/checkbox">
                <content tag="label"><g:message code="${field.name}"/><g:if test="${field.mandatory}"><span id="mandatory-meta-field">*</span></g:if></content>
                <content tag="label.for">metaField_${field.id}.value</content>
                <g:checkBox class="cb checkbox" name="metaField_${field.id}.value" checked="${fieldValue}"/>
            </g:applyLayout>
        </g:if>

        <!-- date fields -->
        <g:if test="${field.getDataType() == DataType.DATE}">
            <g:applyLayout name="form/date">
                <content tag="label"><g:message code="${field.name}"/><g:if test="${field.mandatory}"><span id="mandatory-meta-field">*</span></g:if></content>
                <content tag="label.for">metaField_${field.id}.value</content>

                <g:textField class="field"
                             name="metaField_${field.id}.value"
                             value="${formatDate(date: fieldValue, formatName: 'datepicker.format')}"/>
            </g:applyLayout>
        </g:if>

        <!-- enumeration fields -->
        <g:if test="${field.getDataType() == DataType.ENUMERATION}">
            <g:set var="enumValues" value="${null}"/>
            <%
                for (EnumerationDTO dto : enumerations) {
                    if (dto.name == field.getName()) {
                        enumValues= []
                        enumValues.addAll(dto.values.collect {it.value})
                    }
                }
            %>
            <g:applyLayout name="form/select">
                <content tag="label"><g:message code="${field.name}"/><g:if test="${field.mandatory}"><span id="mandatory-meta-field">*</span></g:if></content>
                <content tag="label.for">metaField_${field.id}.value</content>
                <g:select
                        class="field ${validationRules}"
                        name="metaField_${field.id}.value"
                        from="${enumValues}"
                        optionKey=""
                        noSelection="['':'Please select a value']"
                        value="${fieldValue}" />
            </g:applyLayout>
        </g:if>

        <!-- list fields -->
        <g:if test="${field.getDataType() == DataType.LIST}">
            <g:set var="enumValues" value="${null}"/>
            <%
                for (EnumerationDTO dto : enumerations) {
                    if (dto.name == field.getName()) {
                        enumValues= []
                        enumValues.addAll(dto.values.collect {it.value})
                    }
                }
            %>
            <g:applyLayout name="form/select">
                <content tag="label"><g:message code="${field.name}"/><g:if test="${field.mandatory}"><span id="mandatory-meta-field">*</span></g:if></content>
                <content tag="label.for">metaField_${field.id}.value</content>
                <g:select
                        class="field ${validationRules}"
                        name="metaField_${field.id}.value"
                        from="${enumValues}"
                        optionKey=""
                        value="${fieldValue}"
                        multiple="true"/>
            </g:applyLayout>
        </g:if>

    </g:if>
</g:each>