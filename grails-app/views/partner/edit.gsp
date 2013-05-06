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

<%@ page import="com.sapienter.jbilling.server.user.contact.db.ContactTypeDTO" %> 
<%@ page import="com.sapienter.jbilling.server.user.db.CompanyDTO" %>
<%@ page import="com.sapienter.jbilling.server.user.permisson.db.RoleDTO" %> 
<%@ page import="com.sapienter.jbilling.common.Constants" %>
<%@ page import="com.sapienter.jbilling.server.util.db.LanguageDTO" %>
<%@ page import="com.sapienter.jbilling.server.util.db.EnumerationDTO" %>
<%@ page import="com.sapienter.jbilling.server.process.db.PeriodUnitDTO"  %>
<%@ page import="com.sapienter.jbilling.client.user.UserHelper"%>

<html>
<head>
    <meta name="layout" content="main" />


    <script type="text/javascript">
        $(document).ready(function() {
            $('#contactType').change(function() {
                var selected = $('#contact-' + $(this).val());
                $(selected).show();
                $('div.contact').not(selected).hide();
            }).change();

            $('#percentageRate').blur(function() {
                if ($(this).val() != "") {
                    $('#referralFee').prop('disabled', 'true');
                } else {
                    $('#referralFee').prop('disabled', '');
                }
            }).blur();

            $('#referralFee').blur(function() {
                if ($(this).val() != "") {
                    $('#percentageRate').prop('disabled', 'true');
                } else {
                    $('#percentageRate').prop('disabled', '');
                }
            }).blur();
        });
    </script>
</head>
<body>
<div class="form-edit">

    <g:set var="isNew" value="${!user || !user?.userId || user?.userId == 0}"/>

    <div class="heading">
        <strong>
            <g:if test="${isNew}">
                <g:message code="partner.create.title"/>
            </g:if>
            <g:else>
                <g:message code="partner.edit.title"/>
            </g:else>
        </strong>
    </div>

    <div class="form-hold">
        <g:form name="user-edit-form" action="save">
            <fieldset>
                <div class="form-columns">

                    <!-- user details column -->
                    <div class="column">
                        <g:applyLayout name="form/text">
                            <content tag="label"><g:message code="prompt.customer.number"/></content>

                            <g:if test="${!isNew}">
                                <span>${partner.id}</span>
                            </g:if>
                            <g:else>
                                <em><g:message code="prompt.id.new"/></em>
                            </g:else>

                            <g:hiddenField name="user.userId" value="${user?.userId}"/>
                            <g:hiddenField name="id" value="${partner?.id}"/>
                            
                            <g:hiddenField name="totalPayments" value="${partner?.totalPayments?: 0}"/>
                            <g:hiddenField name="totalRefunds" value="${partner?.totalRefunds?: 0}"/>
                            <g:hiddenField name="totalPayouts" value="${partner?.totalPayouts?: 0}"/>
                            <g:hiddenField name="duePayout" value="${partner?.duePayout?: 0}"/>
                            
                        </g:applyLayout>

                        <g:if test="${isNew}">
                            <g:applyLayout name="form/input">
                                <content tag="label"><g:message code="prompt.login.name"/></content>
                                <content tag="label.for">user.userName</content>
                                <g:textField class="field" name="user.userName" value="${user?.userName}"/>
                            </g:applyLayout>
                        </g:if>
                        <g:else>
                            <g:applyLayout name="form/text">
                                <content tag="label"><g:message code="prompt.login.name"/></content>

                                ${user?.userName}
                                <g:hiddenField name="user.userName" value="${user?.userName}"/>
                            </g:applyLayout>
                        </g:else>

                        <g:if test="${!isNew}">
                             <g:applyLayout name="form/input">
                                <content tag="label"><g:message code="prompt.current.password"/></content>
                                <content tag="label.for">oldPassword</content>
                                <g:passwordField class="field" name="oldPassword"/>
                            </g:applyLayout>
                        </g:if>

                        <g:applyLayout name="form/input">
                            <content tag="label"><g:message code="prompt.password"/></content>
                            <content tag="label.for">newPassword</content>
                            <g:passwordField class="field" name="newPassword"/>
                        </g:applyLayout>

                        <g:applyLayout name="form/input">
                            <content tag="label"><g:message code="prompt.verify.password"/></content>
                            <content tag="label.for">verifiedPassword</content>
                            <g:passwordField class="field" name="verifiedPassword"/>
                        </g:applyLayout>

                        <g:applyLayout name="form/select">
                            <content tag="label"><g:message code="prompt.user.status"/></content>
                            <content tag="label.for">user.statusId</content>
                            <g:userStatus name="user.statusId" value="${user?.statusId}" languageId="${session['language_id']}" />
                        </g:applyLayout>

                        <g:applyLayout name="form/select">
                            <content tag="label"><g:message code="prompt.user.language"/></content>
                            <content tag="label.for">user.languageId</content>
                            <g:select name="user.languageId" from="${LanguageDTO.list()}"
                                    optionKey="id" optionValue="description" value="${user?.languageId}"  />
                        </g:applyLayout>

                        <g:applyLayout name="form/select">
                            <content tag="label"><g:message code="prompt.user.currency"/></content>
                            <content tag="label.for">user.currencyId</content>
                            <g:select name="user.currencyId"
                                      from="${currencies}"
                                      optionKey="id"
                                      optionValue="${{it.getDescription(session['language_id'])}}"
                                      value="${user?.currencyId}" />
                        </g:applyLayout>

                        <g:applyLayout name="form/text">
                            <content tag="label"><g:message code="prompt.user.role"/></content>
                            <content tag="label.for">user.mainRoleId</content>

                            <g:hiddenField name="user.mainRoleId" value="${Constants.TYPE_PARTNER}"/>
                            ${RoleDTO.findByRoleTypeId(Constants.TYPE_PARTNER)?.getTitle(session['language_id'])}
                        </g:applyLayout>
                        
                        <!--  Partner DTO specific fields. -->
                        
                        <g:applyLayout name="form/input">
                            <content tag="label"><g:message code="prompt.partner.balance"/></content>
                            <content tag="label.for">balance</content>
                            <g:textField class="field" name="balanceAsDecimal" value="${formatNumber(number: partner?.balanceAsDecimal ?: 0, formatName: 'money.format')}"/>
                        </g:applyLayout>
                        
                        <g:applyLayout name="form/input">
                            <content tag="label"><g:message code="prompt.partner.percentageRate"/></content>
                            <content tag="label.for">percentageRate</content>
                            <g:textField class="field" name="percentageRateAsDecimal" value="${formatNumber(number: partner?.percentageRateAsDecimal, formatName: 'money.format')}"/>
                        </g:applyLayout>
                        
                        <g:applyLayout name="form/input">
                            <content tag="label"><g:message code="prompt.partner.referralFee"/></content>
                            <content tag="label.for">referralFee</content>
                            <g:textField class="field" name="referralFeeAsDecimal" value="${formatNumber(number: partner?.referralFeeAsDecimal, formatName: 'money.format')}"/>
                        </g:applyLayout>
                        
                        <g:applyLayout name="form/select">
                            <content tag="label"><g:message code="prompt.partner.fee.currency"/></content>
                            <content tag="label.for">feeCurrencyId</content>
                            <g:select name="feeCurrencyId"
                                      from="${currencies}"
                                      optionKey="id"
                                      optionValue="${{it.getDescription(session['language_id'])}}"
                                      value="${partner?.feeCurrencyId}" />
                        </g:applyLayout>
                        
                        <g:applyLayout name="form/checkbox">
                            <content tag="label"><g:message code="prompt.partner.onetimefee"/></content>
                            <content tag="label.for">oneTime</content>
                            <g:checkBox class="cb checkbox" name="oneTime" checked="${partner?.oneTime}"/>
                        </g:applyLayout>
                        
                        <g:applyLayout name="form/select">
                            <content tag="label"><g:message code="prompt.partner.payoutPeriod"/></content>
                            <content tag="label.for">periodValue</content>
                            <span>
                                <g:select class="field" name="periodValue" style="float: left; position: relative; width:50px" 
                                          from="${1..99}"
                                          value="${partner?.periodValue}" />
                                          &nbsp;
                                <g:select class="field" name="periodUnitId" style="float: center; position: justified; top: -20px;width:70px" 
                                          from="${PeriodUnitDTO.list()}"
                                          optionKey="id"
                                          optionValue="${{it.getDescription(session['language_id'])}}"
                                          value="${partner?.periodUnitId}" />
                                </span>
                        </g:applyLayout>
                        
                        <g:applyLayout name="form/date">
                            <content tag="label"><g:message code="prompt.partner.nextPayoutDate"/></content>
                            <content tag="label.for">nextPayoutDate</content>
                            <g:textField class="field" name="nextPayoutDate" value="${formatDate(date: partner?.nextPayoutDate, formatName: 'datepicker.format')}"/>
                        </g:applyLayout>
                                    
                        
                        <g:applyLayout name="form/checkbox">
                            <content tag="label"><g:message code="prompt.partner.batchPayout"/></content>
                            <content tag="label.for">automaticProcess</content>
                            <g:checkBox class="cb checkbox" name="automaticProcess" checked="${partner?.automaticProcess}"/>
                        </g:applyLayout>
                        
                        <g:applyLayout name="form/select">
                            <content tag="label"><g:message code="prompt.partner.clerk"/></content>
                            <content tag="label.for">relatedClerkUserId</content>
                            <g:select name="relatedClerkUserId"
                                      from="${clerks}"
                                      optionKey="id"
                                      optionValue="${{UserHelper.getDisplayName(it, it?.contact)}}"
                                      noSelection="['': message(code: 'default.no.selection')]" 
                                      value="${partner?.relatedClerkUserId}" />
                        </g:applyLayout>
                        
                    </div>


                    <!-- contact information column -->
                    <g:set var="contactTypes" value="${company.contactTypes.asList()}"/>
                    <g:set var="primaryContactType" value="${contactTypes.find{ it.isPrimary == 1 }}"/>
                    <g:hiddenField name="primaryContactTypeId" value="${primaryContactType.id}"/>

                    <div class="column">
                        <g:if test="${contactTypes.size > 1}">
                            <g:applyLayout name="form/select">
                                <content tag="label"><g:message code="prompt.contact.type"/></content>
                                <g:select name="contactType"
                                          from="${contactTypes}"
                                          optionKey="id"
                                          optionValue="${{it.getDescription(session['language_id'])}}"
                                          value="${primaryContactType.id}"/>
                            </g:applyLayout>
                        </g:if>
                        <g:else>
                            <g:applyLayout name="form/text">
                                <content tag="label"><g:message code="prompt.contact.type"/></content>
                                <span>${(contact?.type ?: contactTypes?.get(0)).getDescription(session['language_id'])}</span>
                            </g:applyLayout>
                        </g:else>

                        <!-- print a hidden block for each contact type, will be toggled by contact type dropdown -->
                        <g:each var="contactType" in="${contactTypes}">
                            <g:set var="contact" value="${contacts.find{ it.type == contactType.id }}"/>
                            <g:render template="/customer/contact" model="[contactType: contactType, contact: contact]"/>
                        </g:each>

                        <br/>&nbsp;

                        <!-- customer meta fields -->
                        <g:render template="/metaFields/editMetaFields" model="[ availableFields: availableFields, fieldValues: user?.metaFields ]"/>
                    </div>
                </div>

                <div><br/></div>

                <div class="buttons">
                    <ul>
                        <li>
                            <a onclick="$('#user-edit-form').submit()" class="submit save"><span><g:message code="button.save"/></span></a>
                        </li>
                        <li>
                            <g:link action="list" class="submit cancel"><span><g:message code="button.cancel"/></span></g:link>
                        </li>
                    </ul>
                </div>

            </fieldset>
        </g:form>
    </div>
</div>
</body>
</html>