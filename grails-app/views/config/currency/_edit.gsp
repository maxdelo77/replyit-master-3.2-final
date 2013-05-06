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

<%@ page contentType="text/html;charset=UTF-8" %>

<%--
  Shows an edit form for a currency (used to create new currencies).

  @author Brian Cowdery
  @since  07-Apr-2011
--%>

<div class="column-hold">
    <div class="heading">
        <strong><g:message code="currency.config.new.currency.title"/></strong>
    </div>

    <g:form name="save-currency-form" url="[action: 'saveCurrency']">

    <div class="box">
      <div class="sub-box">
        <fieldset>
            <div class="form-columns">
                <g:hiddenField name="id" value="${currency?.id}"/>

                <g:applyLayout name="form/input">
                    <content tag="label"><g:message code="currency.config.label.name"/></content>
                    <content tag="label.for">description</content>
                    <g:textField name="description" class="field" value="${currency?.description}"/>
                </g:applyLayout>

                <g:applyLayout name="form/input">
                    <content tag="label"><g:message code="currency.config.label.code"/></content>
                    <content tag="label.for">code</content>
                    <g:textField name="code" class="field" value="${currency?.code}" maxlength="3"/>
                </g:applyLayout>

                <g:applyLayout name="form/input">
                    <content tag="label"><g:message code="currency.config.label.symbol"/></content>
                    <content tag="label.for">symbol</content>
                    <g:textField name="symbol" class="field" value="${currency?.symbol}" maxlength="10"/>
                </g:applyLayout>

                <g:applyLayout name="form/input">
                    <content tag="label"><g:message code="currency.config.label.countryCode"/></content>
                    <content tag="label.for">countryCode</content>
                    <g:textField name="countryCode" class="field" value="${currency?.countryCode}" maxlength="2"/>
                </g:applyLayout>

                <g:applyLayout name="form/input">
                    <content tag="label"><g:message code="currency.config.label.rate"/></content>
                    <content tag="label.for">rate</content>
                    <g:textField name="rate" class="field" value="${currency?.rate}"/>
                </g:applyLayout>

                <g:applyLayout name="form/input">
                    <content tag="label"><g:message code="currency.config.label.sysRate"/></content>
                    <content tag="label.for">sysRate</content>
                    <g:textField name="sysRate" class="field" value="${currency?.sysRate ?: formatNumber(number: BigDecimal.ONE, formatName: 'exchange.format')}"/>
                </g:applyLayout>

                <g:applyLayout name="form/checkbox">
                    <content tag="label"><g:message code="currency.config.label.active"/></content>
                    <content tag="label.for">inUse</content>
                    <g:checkBox name="inUse" class="cb" value="${currency?.inUse}"/>
                </g:applyLayout>

            </div>
        </fieldset>
      </div>
    </div>

    </g:form>

    <div class="btn-box buttons">
        <ul>
            <li><a class="submit save" onclick="$('#save-currency-form').submit();"><span><g:message code="button.save"/></span></a></li>
            <li><a class="submit cancel" onclick="closePanel(this);"><span><g:message code="button.cancel"/></span></a></li>
        </ul>
    </div>
</div>