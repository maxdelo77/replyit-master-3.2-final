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

<div class="column-hold">
	<div class="heading">
	    <strong style="width:100%">
			<g:message code="prompt.notifications.preferences"/>
	    </strong>
	</div>

	<div class="box">
    <div class="sub-box">
  		<table class="dataTable">
  			<g:set var="dto" value="${subList.get(Constants.PREFERENCE_TYPE_SELF_DELIVER_PAPER_INVOICES)}" />
  			<tr><td><g:message code="notification.preference.selfDeliver.prompt"/>:</td>
  			<td class="value">${ ((dto?.getIntValue() != 0) ? "Yes": "No") }</td>
              </tr>
  			<g:set var="dto" value="${subList.get(Constants.PREFERENCE_TYPE_INCLUDE_CUSTOMER_NOTES)}" />
  			<tr><td><g:message code="notification.preference.showNotes.prompt"/>:</td>
  			<td class="value">${ (dto?.getIntValue() != 0)? "Yes": "No"}</td>
              </tr>
  			<g:set var="dto" value="${subList.get(Constants.PREFERENCE_TYPE_DAY_BEFORE_ORDER_NOTIF_EXP)}" />
  			<tr><td><g:message code="notification.preference.orderDays1.prompt"/>:</td>
  			<td class="value">${dto?.getIntValue()}</td></tr>
  			<g:set var="dto" value="${subList.get(Constants.PREFERENCE_TYPE_DAY_BEFORE_ORDER_NOTIF_EXP2)}" />
  			<tr><td><g:message code="notification.preference.orderDays2.prompt"/>:</td>
  			<td class="value">${dto?.getIntValue()}</td></tr>

  			<g:set var="dto" value="${subList.get(Constants.PREFERENCE_TYPE_DAY_BEFORE_ORDER_NOTIF_EXP3)}" />
  			<tr><td><g:message code="notification.preference.orderDays3.prompt"/>:</td>
  			<td class="value">${dto?.getIntValue()}</td></tr>

  			<g:set var="dto" value="${subList.get(Constants.PREFERENCE_TYPE_USE_INVOICE_REMINDERS)}" />
  			<tr><td><g:message code="notification.preference.invoiceRemiders.prompt"/>:</td>
  			<td class="value">${(dto?.getIntValue() != 0)?"Yes":"No"}</td></tr>

  			<g:set var="dto" value="${subList.get(Constants.PREFERENCE_TYPE_NO_OF_DAYS_INVOICE_GEN_1_REMINDER)}"/>
  			<tr><td><g:message code="notification.preference.reminders.first"/>:</td>
  			<td class="value">${dto?.getIntValue()}</td></tr>

  			<g:set var="dto" value="${subList.get(Constants.PREFERENCE_TYPE_NO_OF_DAYS_NEXT_REMINDER)}" />
  			<tr><td><g:message code="notification.preference.reminders.next"/>:</td>
  			<td class="value">${dto?.getIntValue()}</td></tr>
  		</table>
    </div>
	</div>
	<div class="btn-box">
		<a href="${createLink(action: 'editPreferences')}" class="submit edit">
	    	<span><g:message code="button.edit"/></span></a>
	</div>
</div>