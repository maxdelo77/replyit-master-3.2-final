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

<%@page import="com.sapienter.jbilling.server.process.db.PeriodUnitDTO" %>

<%-- 
<g:javascript library="ui.core"/>
<g:javascript library="ui.spinner"/>
--%>

<div class="form-edit" style="width:650px">

    <div class="heading">
        <strong><g:message code="configuration.title.billing"/></strong>
    </div>

    <div class="form-hold">
        <g:form name="save-billing-form" action="saveConfig">
            <fieldset>
                <div class="form-columns">
                	<%--Use two columns --%>
                    <div class="one_column" style="width:650px">
                    	<div class="row">
							<g:applyLayout name="form/date">
	                             <content tag="label"><g:message code="billing.next.run.date"/></content>
	                             <content tag="label.for">nextRunDate</content>
	                             <g:textField class="field" name="nextRunDate" value="${formatDate(date: configuration?.nextRunDate, formatName:'datepicker.format')}" onblur="validateDate(this)"/>
	                        </g:applyLayout>
                        </div>

						<div class="row">
	                        <g:applyLayout name="form/checkbox">
	                            <content tag="label"><g:message code="billing.generate.report"/></content>
	                            <content tag="label.for">generateReport</content>
	                            <g:checkBox class="cb checkbox" name="generateReport" checked="${configuration?.generateReport > 0}"/>
	                        </g:applyLayout>
                        </div>
                        <div class="row">
	                        <g:applyLayout name="form/input">
	                            <content tag="label"><g:message code="billing.days.to.review"/></content>
	                            <content tag="label.for">daysForReport</content>
	                            <content tag="style">inp4</content>
	                            <g:textField class="field numericOnly" name="daysForReport" value="${configuration?.daysForReport}" maxlength="2" size="2"/>
	                        </g:applyLayout>
                        </div>
                        
                        <div class="row">
	                        <g:applyLayout name="form/input">
	                            <content tag="label"><g:message code="billing.number.retries"/></content>
	                            <content tag="label.for">retries</content>
	                            <content tag="style">inp4</content>
	                            <g:textField class="field numericOnly" name="retries" value="${configuration?.retries}" maxlength="2" size="2"/>
	                        </g:applyLayout>
                        </div>
                        
                        <div class="row">
	                        <g:applyLayout name="form/input">
	                            <content tag="label"><g:message code="billing.days.for.retry"/></content>
	                            <content tag="label.for">daysForRetry</content>
	                            <content tag="style">inp4</content>
	                            <g:textField class="field numericOnly" name="daysForRetry" value="${configuration?.daysForRetry}" maxlength="2" size="2"/>
	                        </g:applyLayout>
                        </div>
                        
                        <div class="row">
	                        <g:applyLayout name="form/input">
	                            <content tag="label"><g:message code="billing.due.date"/></content>
	                            <content tag="label.for">dueDateValue</content>
	                            <g:textField class="field numericOnly" name="dueDateValue" value="${configuration?.dueDateValue}" maxlength="2" size="2"/>
	                            <g:select style="float: right; position: relative; top: -20px;width:70px"  class="field" name="dueDateUnitId" from="${PeriodUnitDTO.list()}"
	                                 optionKey="id" optionValue="description" value="${configuration?.dueDateUnitId}" />
						    </g:applyLayout>
                        </div>
                        
                        <div class="row">
	                        <g:applyLayout name="form/checkbox">
	                            <content tag="label"><g:message code="billing.require.recurring"/></content>
	                            <content tag="label.for">onlyRecurring</content>
	                            <g:checkBox class="cb checkbox" name="onlyRecurring" checked="${configuration?.onlyRecurring > 0}"/>
	                        </g:applyLayout>
                        </div>
                        
                        <div class="row">
	                        <g:applyLayout name="form/checkbox">
	                            <content tag="label"><g:message code="billing.use.process.date"/></content>
	                            <content tag="label.for">invoiceDateProcess</content>
	                            <g:checkBox class="cb checkbox" name="invoiceDateProcess" checked="${configuration?.invoiceDateProcess > 0}"/>
	                        </g:applyLayout>
                        </div>
                        
                        <div class="row">
	                        <g:applyLayout name="form/checkbox">
	                            <content tag="label"><g:message code="billing.auto.payment"/></content>
	                            <content tag="label.for">autoPayment</content>
	                            <g:checkBox class="cb checkbox" name="autoPayment" checked="${configuration?.autoPayment > 0}"/>
	                        </g:applyLayout>
                        </div>
                        
                        <div class="row">
	                        <g:applyLayout name="form/input">
	                            <content tag="label"><g:message code="billing.maximum.period"/></content>
	                            <content tag="label.for">maximumPeriods</content>
	                            <content tag="style">inp4</content>
	                            <g:textField class="field numericOnly" name="maximumPeriods" value="${configuration?.maximumPeriods}" maxlength="2" size="2"/>
	                        </g:applyLayout>
                        </div>
                        
                        <div class="row">
	                        <g:applyLayout name="form/checkbox">
	                            <content tag="label"><g:message code="billing.auto.payment.application"/></content>
	                            <content tag="label.for">autoPaymentApplication</content>
	                            <g:checkBox class="cb checkbox" name="autoPaymentApplication" 
	                            	checked="${configuration?.autoPaymentApplication > 0}"/>
	                        </g:applyLayout>
                        </div>
                    </div>
                </div>
                
                
            </fieldset>
                

                <div class="btn-box">
                       	<a onclick="$('#save-billing-form').submit();" class="submit save"><span><g:message code="button.save"/></span></a>
                       	<g:link controller="config" action="index" class="submit cancel"><span><g:message code="button.cancel"/></span></g:link>
                        <g:if test="${isBillingRunning}">
                        </g:if>
                        <g:else>
                           	<g:link controller="billingconfiguration" action="runBilling" class="submit "><span><g:message code="button.run.billing"/></span></g:link>
                        </g:else>
                </div>
            <script type="text/javascript">
                $(".numericOnly").keydown(function(event){
                    // Allow only backspace, delete, left & right
                    if ( event.keyCode==37 || event.keyCode== 39 || event.keyCode == 46 || event.keyCode == 8 || event.keyCode == 9 ) {
                        // let it happen, don't do anything
                    }
                    else {
                        // Ensure that it is a number and stop the keypress
                        if (event.keyCode < 48 || event.keyCode > 57 ) {
                            event.preventDefault();
                        }
                    }
                });
            </script>
        </g:form>
    </div>
</div>