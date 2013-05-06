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

<%@page import="com.sapienter.jbilling.server.util.Constants;"%>

<html>
<head>
    <meta name="layout" content="main" />
    <r:script disposition="head">

	$(document).ready(function() {

		$('.form-columns checkbox').each(function() {
			//alert($(this).attr('name'));
			if ($(this).is(':checked')){
				$(this).parent().attr('class', 'checkboxAreaChecked');
			} else {
				$(this).parent().attr('class', 'checkboxArea');
			}
		});

	    $('.form-columns checkbox').bind('click', function() {
	    	//alert($(this).attr('name'));
	    	if ($(this).is(':checked')){
				$(this).parent().attr('class', 'checkboxAreaChecked');
			} else {
				$(this).parent().attr('class', 'checkboxArea');
			}
	    })
	});
	</r:script>
</head>
<body>

<div class="form-edit">
	<div class="heading">
		<strong>
			<g:message code="prompt.notifications.preferences"/>
		</strong>
	</div>

	<div class="form-hold">
		<g:form name="notifications" controller="notifications" action="savePrefs">
		<g:hiddenField name="recCnt" value="8"/>
			
			<fieldset>
				<div class="form-columns">
					<div class="column">
						<g:set var="dto" value="${subList.get(Constants.PREFERENCE_TYPE_SELF_DELIVER_PAPER_INVOICES)}" />
						<div class="row">
							<label><g:message code="notification.preference.selfDeliver.prompt"/>:</label>
							<div class="checkboxArea"><g:hiddenField value="${dto?.id}" name="pref[0].id"/>
								<g:hiddenField value="${Constants.ENTITY_TABLE_ID}" name="pref[0].tableId"/>
								<g:hiddenField value="${Constants.PREFERENCE_TYPE_SELF_DELIVER_PAPER_INVOICES}" name="pref[0].preferenceType.id"/>
								<g:checkBox  class="cb" name="pref[0].value" checked="${dto?.getIntValue() != 0}"/>
							</div>
						</div>
						<g:set var="dto" value="${subList.get(Constants.PREFERENCE_TYPE_INCLUDE_CUSTOMER_NOTES)}" />
						<div class="row">
							<label><g:message code="notification.preference.showNotes.prompt"/>:</label>
							<div class="checkboxArea">
								<g:hiddenField value="${dto?.id}" name="pref[1].id"/>
								<g:hiddenField value="${Constants.ENTITY_TABLE_ID}" name="pref[1].tableId"/>
								<g:hiddenField value="${Constants.PREFERENCE_TYPE_INCLUDE_CUSTOMER_NOTES}" name="pref[1].preferenceType.id"/>		
								<g:checkBox  class="cb" name="pref[1].value" checked="${dto?.getIntValue() != 0}"/>
							</div>
						</div>
						<g:set var="dto" value="${subList.get(Constants.PREFERENCE_TYPE_DAY_BEFORE_ORDER_NOTIF_EXP)}" />
						<div class="row">
							<label><g:message code="notification.preference.orderDays1.prompt"/>:</label>
							<div class="inp-bg">
								<g:hiddenField value="${dto?.id}" name="pref[2].id"/>
								<g:hiddenField value="${Constants.ENTITY_TABLE_ID}" name="pref[2].tableId"/>
								<g:hiddenField value="${Constants.PREFERENCE_TYPE_DAY_BEFORE_ORDER_NOTIF_EXP}" name="pref[2].preferenceType.id"/>		
								<g:textField class="field" size="4" name="pref[2].value" value="${dto?.getIntValue()}"/>
							</div>
						</div>
						<g:set var="dto" value="${subList.get(Constants.PREFERENCE_TYPE_DAY_BEFORE_ORDER_NOTIF_EXP2)}" />
						<div class="row">
							<label><g:message code="notification.preference.orderDays2.prompt"/>:</label>
							<div class="inp-bg">
								<g:hiddenField value="${dto?.id}" name="pref[3].id"/>
								<g:hiddenField value="${Constants.ENTITY_TABLE_ID}" name="pref[3].tableId"/>
								<g:hiddenField value="${Constants.PREFERENCE_TYPE_DAY_BEFORE_ORDER_NOTIF_EXP2}" name="pref[3].preferenceType.id"/>		
								<g:textField class="field" size="4" name="pref[3].value" value="${dto?.getIntValue()}"/>
							</div>
						</div>
						<g:set var="dto" value="${subList.get(Constants.PREFERENCE_TYPE_DAY_BEFORE_ORDER_NOTIF_EXP3)}" />
						<div class="row">
							<label><g:message code="notification.preference.orderDays3.prompt"/>:</label>
							<div class="inp-bg">
								<g:hiddenField value="${dto?.id}" name="pref[4].id"/>
								<g:hiddenField value="${Constants.ENTITY_TABLE_ID}" name="pref[4].tableId"/>
								<g:hiddenField value="${Constants.PREFERENCE_TYPE_DAY_BEFORE_ORDER_NOTIF_EXP3}" name="pref[4].preferenceType.id"/>		
								<g:textField class="field" size="4" name="pref[4].value" value="${dto?.getIntValue()}"/>
							</div>
						</div>
						<g:set var="dto" value="${subList.get(Constants.PREFERENCE_TYPE_USE_INVOICE_REMINDERS)}" />
						<div class="row">
							<label><g:message code="notification.preference.invoiceRemiders.prompt"/>:</label>
							<div class="checkboxArea">
								<g:hiddenField value="${dto?.id}" name="pref[5].id"/>
								<g:hiddenField value="${Constants.ENTITY_TABLE_ID}" name="pref[5].tableId"/>
								<g:hiddenField value="${Constants.PREFERENCE_TYPE_USE_INVOICE_REMINDERS}" name="pref[5].preferenceType.id"/>		
								<g:checkBox  class="cb" name="pref[5].value" checked="${dto?.getIntValue() != 0}"/>
							</div>
						</div>
						<g:set var="dto" value="${subList.get(Constants.PREFERENCE_TYPE_NO_OF_DAYS_INVOICE_GEN_1_REMINDER)}" />
						<div class="row">
							<label><g:message code="notification.preference.reminders.first"/>:</label>
							<div class="inp-bg">
								<g:hiddenField value="${dto?.id}" name="pref[6].id"/>
								<g:hiddenField value="${Constants.ENTITY_TABLE_ID}" name="pref[6].tableId"/>
								<g:hiddenField value="${Constants.PREFERENCE_TYPE_NO_OF_DAYS_INVOICE_GEN_1_REMINDER}" name="pref[6].preferenceType.id"/>		
								<g:textField class="field" size="4" name="pref[6].value" value="${dto?.getIntValue()}"/>
							</div>
						</div>
						<g:set var="dto" value="${subList.get(Constants.PREFERENCE_TYPE_NO_OF_DAYS_NEXT_REMINDER)}" />
						<div class="row">
							<label><g:message code="notification.preference.reminders.next"/>:</label>
							<div class="inp-bg">
								<g:hiddenField value="${dto?.id}" name="pref[7].id"/>
								<g:hiddenField value="${Constants.ENTITY_TABLE_ID}" name="pref[7].tableId"/>
								<g:hiddenField value="${Constants.PREFERENCE_TYPE_NO_OF_DAYS_NEXT_REMINDER}" name="pref[7].preferenceType.id"/>		
								<g:textField class="field" size="4" name="pref[7].value" value="${dto?.getIntValue()}"/>
							</div>
						</div>
					</div>
				</div>
			</fieldset>
			
			<div class="btn-box">
			    <a href="javascript:void(0)" onclick="$('#notifications').submit();" class="submit save">
			    	<span><g:message code="button.save"/></span></a>
			    <a href="${createLink(action: 'cancelEditPrefs')}" class="submit cancel">
						<span><g:message code="button.cancel"/></span></a>
			</div>
		</g:form>
	</div>
</div>
</body>
</html>