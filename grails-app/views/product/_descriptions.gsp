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

<%@ page import="com.sapienter.jbilling.server.util.db.CurrencyDTO; com.sapienter.jbilling.server.util.db.LanguageDTO;" %>

<%--
  Editor form for price model attributes.

  This template is not the same as the attribute UI in the plan builder. The plan builder
  uses remote AJAX calls that can only be used in a web-flow. This template is to be used
  for standard .gsp pages.

  @author Brian Cowdery
  @since  02-Feb-2011
--%>

<div class="row" id='addDescription'>
    <div class="add-desc">
        <label><g:message code='product.detail.description.add.title'/></label>
        <select name="newDescriptionLanguage" id="newDescriptionLanguage"></select>
        <a onclick="addNewDescription()">
            <img src="${resource(dir:'images', file:'add.png')}" alt="remove"/>
        </a>
    </div>
</div>

<div id="descriptionClone" style="display: none;">
    <g:applyLayout name="form/description">
        <content tag="label"><g:message code="product.detail.description.label"/></content>
        <content tag="label.for">desCloneContent</content>

        <input type="text" id="desCloneContent" class="descContent field" size="26" value="" name="desCloneContent">
        <input type="hidden" id="desCloneLangId" class="descLanguage" value="" name="desCloneLangId">
        <input type="hidden" id="desCloneDeleted" class="descDeleted" value="" name="desCloneDeleted">
        <a onclick="removeDescription(this)">
            <img src="${resource(dir:'images', file:'cross.png')}" alt="remove"/>
        </a>
    </g:applyLayout>
</div>

<g:set var="availableDescriptionLanguages" value="${LanguageDTO.list().collect {it.id+'-'+it.description}}"></g:set>

<div id="descriptions">
    <g:each in="${product?.descriptions}" var="description" status="index">
        <g:if test="${description?.languageId}">
            <g:applyLayout name="form/description">
                <g:set var="currentLang" value="${LanguageDTO.get(product?.descriptions[index]?.languageId)}"></g:set>
                <g:set var="availableDescriptionLanguages" value="${availableDescriptionLanguages - (currentLang?.id+'-'+currentLang?.description)}"></g:set>

                <content tag="label"><g:message code="product.detail.description.label" args="${[currentLang?.description]}"/></content>
                <content tag="label.for">product?.descriptions[${index}]?.content</content>

                <g:textField name="product.descriptions[${index}].content" class="descContent field" value="${product?.descriptions[index]?.content}"/>
                <g:hiddenField name="product.descriptions[${index}].languageId" class="descLanguage" value="${currentLang?.id}"/>
                <g:hiddenField name="product.descriptions[${index}].deleted" value="" class="descDeleted"/>
                <a onclick="removeDescription(this)">
                    <img src="${resource(dir:'images', file:'cross.png')}" alt="remove"/>
                </a>
            </g:applyLayout>
        </g:if>
    </g:each>
</div>

<g:set var="allDescriptionLanguages" value="${LanguageDTO.list().collect {it.id+'-'+it.description}}"></g:set>
<g:hiddenField name="allDescriptionLanguages" value="${allDescriptionLanguages?.join(',')}"/>
<g:hiddenField name="availableDescriptionLanguages" value="${availableDescriptionLanguages?.join(',')}"/>

