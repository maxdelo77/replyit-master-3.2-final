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

<%@ page import="com.sapienter.jbilling.server.util.db.CountryDTO" %>
<%@ page import="com.sapienter.jbilling.server.util.db.CurrencyDTO" %>
<%@ page import="com.sapienter.jbilling.server.util.db.LanguageDTO" %>
<%@ page import="com.sapienter.jbilling.server.user.contact.db.ContactMapDTO" %>
<%@ page import="com.sapienter.jbilling.server.user.contact.db.ContactTypeDTO" %>

<g:set var="contact" value="${company?.contact}"/>

<div class="form-edit">
    <div class="heading">
        <strong><g:message code="configuration.title.company" />
        </strong>
    </div>
    <div class="form-hold">
        <g:form name="save-company-form" action="saveCompany">
            <!-- company details -->
            <fieldset>
                <div class="form-columns">
                    <%--Use two columns --%>
                    <div class="column">
                        <div class="row">
                            <g:applyLayout name="form/input">
                                <content tag="label"><g:message code="config.company.description"/></content>
                                 <content tag="label.for">description</content>
                                <g:textField class="field" name="description" value="${company.description}"/>
                            </g:applyLayout>
                            <g:applyLayout name="form/input">
                                <content tag="label"><g:message code="prompt.address1"/></content>
                                <content tag="label.for">address1</content>
                                <g:textField class="field" name="address1" value="${contact?.address1}" />
                            </g:applyLayout>
                        
                            <g:applyLayout name="form/input">
                                <content tag="label"><g:message code="prompt.address2"/></content>
                                <content tag="label.for">address2</content>
                                <g:textField class="field" name="address2" value="${contact?.address2}" />
                            </g:applyLayout>
                        
                            <g:applyLayout name="form/input">
                                <content tag="label"><g:message code="prompt.city"/></content>
                                <content tag="label.for">city</content>
                                <g:textField class="field" name="city" value="${contact?.city}" />
                            </g:applyLayout>
                        
                            <g:applyLayout name="form/input">
                                <content tag="label"><g:message code="prompt.state"/></content>
                                <content tag="label.for">stateProvince</content>
                                <g:textField class="field" name="stateProvince" value="${contact?.stateProvince}" />
                            </g:applyLayout>
                        
                            <g:applyLayout name="form/input">
                                <content tag="label"><g:message code="prompt.zip"/></content>
                                <content tag="label.for">postalCode</content>
                                <g:textField class="field" name="postalCode" value="${contact?.postalCode}" />
                            </g:applyLayout>
                        
                            <g:applyLayout name="form/select">
                                <content tag="label"><g:message code="prompt.country"/></content>
                                <content tag="label.for">countryCode</content>
                        
                                <g:select name="countryCode"
                                          from="${CountryDTO.list()}"
                                          optionKey="code"
                                          optionValue="${{ it.getDescription(session['language_id']) }}"
                                          noSelection="['': message(code: 'default.no.selection')]"
                                          value="${contact?.countryCode}"/>
                            </g:applyLayout>
                        </div>
                        <!-- two columns do not work in configuration page 
                    </div>
                    <div class="column">
                    -->
                        <g:applyLayout name="form/select">
                            <content tag="label"><g:message code="prompt.company.currency"/></content>
                            <content tag="label.for">currencyId</content>
                            <g:select name="currencyId" 
                                      from="${CurrencyDTO.list()}"
                                      optionKey="id"
                                      optionValue="${{it.getDescription(session['language_id'])}}"
                                      value="${company.currencyId}" />
                        </g:applyLayout>
                        <g:applyLayout name="form/select">
                            <content tag="label"><g:message code="prompt.company.language"/></content>
                            <content tag="label.for">languageId</content>
                            <g:select name="languageId" from="${LanguageDTO.list()}"
                                    optionKey="id" optionValue="description" value="${company.languageId}"  />
                        </g:applyLayout>
                    </div>
                </div>
            </fieldset>
            <div class="btn-box">
                    <a onclick="$('#save-company-form').submit();" class="submit save"><span><g:message code="button.save"/></span></a>
                    <g:link controller="config" action="index" class="submit cancel"><span><g:message code="button.cancel"/></span></g:link>
            </div>
        </g:form>
    </div>
</div>