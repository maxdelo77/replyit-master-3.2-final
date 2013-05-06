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

<%@ page import="com.sapienter.jbilling.server.util.Constants" %>

<div class="form-edit">
    <div class="heading">
        <strong><g:message code="invoice.config.title"/></strong>
    </div>
    <div class="form-hold">
        <g:uploadForm name="save-invoice-form" url="[action: 'saveInvoice']">
            <fieldset>
                <div class="form-columns">
                    <div class="column single">

                        <!-- invoice numbering -->
                        <g:applyLayout name="form/input">
                            <content tag="label"><g:message code="invoice.config.label.number"/></content>
                            <content tag="label.for">number</content>
                            <g:textField name="number" class="field" value="${number.value ?: number.preferenceType.defaultValue}"/>

                        </g:applyLayout>

                        <g:applyLayout name="form/input">
                            <content tag="label"><g:message code="invoice.config.label.prefix"/></content>
                            <content tag="label.for">prefix</content>
                            <g:textField name="prefix" class="field" value="${prefix.value ?: prefix.preferenceType.defaultValue}"/>
                        </g:applyLayout>


                        <!-- spacer -->
                        <div>
                            <br/>&nbsp;
                        </div>


                        <!-- invoice logo upload -->
                        <g:applyLayout name="form/text">
                            <content tag="label"><g:message code="invoice.config.label.logo"/></content>
                            <img src="${createLink(action: 'entityLogo')}" alt="logo"/>
                        </g:applyLayout>

                        <g:applyLayout name="form/text">
                            <content tag="label">&nbsp;</content>
                            ${logoPath}
                        </g:applyLayout>

                        <g:applyLayout name="form/text">
                            <content tag="label">&nbsp;</content>
                            <input type="file" name="logo"/>
                        </g:applyLayout>


                        <!-- spacer -->
                        <div>
                            <br/>&nbsp;
                        </div>

                    </div>
                </div>
            </fieldset>
        </g:uploadForm>
    </div>

    <div class="btn-box">
        <a onclick="$('#save-invoice-form').submit();" class="submit save"><span><g:message code="button.save"/></span></a>
        <g:link controller="config" action="index" class="submit cancel"><span><g:message code="button.cancel"/></span></g:link>
    </div>
</div>