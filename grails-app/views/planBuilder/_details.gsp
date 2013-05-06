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

<%@ page import="com.sapienter.jbilling.server.pricing.PriceModelBL; com.sapienter.jbilling.server.util.Constants; com.sapienter.jbilling.server.user.db.CompanyDTO" %>

<%--
  Order details form. Allows editing of primary order attributes.

  @author Brian Cowdery
  @since 01-Feb-2011
--%>

<div id="details-box">
    <g:formRemote name="plan-details-form" url="[action: 'edit']" update="column2" method="GET">
        <g:hiddenField name="_eventId" value="update"/>
        <g:hiddenField name="execution" value="${flowExecutionKey}"/>

        <div class="form-columns">
            <g:applyLayout name="form/input">
                <content tag="label"><g:message code="product.internal.number"/></content>
                <content tag="label.for">product.number</content>
                <g:textField class="field text" name="product.number" value="${product?.number}" size="40"/>
            </g:applyLayout>

            <g:applyLayout name="form/input">
                <content tag="label"><g:message code="product.description"/></content>
                <content tag="label.for">product.description</content>
                <g:textField class="field text" name="product.description" value="${product?.description}" size="40"/>
            </g:applyLayout>

            <g:applyLayout name="form/select">
                <content tag="label"><g:message code="order.label.period"/></content>
                <content tag="label.for">plan.periodId</content>
                <g:select from="${orderPeriods}"
                          optionKey="id" optionValue="${{it.getDescription(session['language_id'])}}"
                          name="plan.periodId"
                          value="${plan?.periodId}"/>
            </g:applyLayout>

            <g:set var="defaultProductPrice" value="${PriceModelBL.getWsPriceForDate(product.defaultPrices, startDate)}"/>

            <g:applyLayout name="form/select">
                <content tag="label"><g:message code="prompt.user.currency"/></content>
                <content tag="label.for">price.currencyId</content>
                <g:select name="price.currencyId" from="${currencies}"
                          optionKey="id" optionValue="description"
                          value="${defaultProductPrice?.currencyId}" />
            </g:applyLayout>

            <g:applyLayout name="form/input">
                <content tag="label"><g:message code="plan.model.rate"/></content>
                <content tag="label.for">price.rateAsDecimal</content>
                <g:textField class="field text" name="price.rateAsDecimal" value="${formatNumber(number: defaultProductPrice?.rate, formatName: 'money.format')}"/>
            </g:applyLayout>
        </div>

        <hr/>

        <div class="form-columns">
            <div class="box-text">
                <label class="lb"><g:message code="plan.description"/></label>
                <g:textArea name="plan.description" rows="5" cols="60" value="${plan?.description}"/>
            </div>
        </div>
    </g:formRemote>

    <script type="text/javascript">
        $('#plan-details-form').find('select').change(function() {
            $('#plan-details-form').submit();
        });

        $('#plan-details-form').find('input:checkbox').change(function() {
            $('#plan-details-form').submit();
        });

        $('#plan-details-form').find('input.text').blur(function() {
            $('#plan-details-form').submit();
        });

        $('#plan-details-form').find('textarea').blur(function() {
            $('#plan-details-form').submit();
        });
    </script>
</div>


