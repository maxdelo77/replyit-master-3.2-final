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

<%@ page import="com.sapienter.jbilling.server.user.contact.db.ContactDTO" %>

<%--
	Invoice list template. 
	
	@author Vikas Bodani
	@since 24-Dec-2010
 --%>
 
<div class="table-box">
	<div class="table-scroll">
		<table id="invoices" cellspacing="0" cellpadding="0">
			<thead>
                <tr>
                    <th class="small">
                        <g:remoteSort action="list" sort="id" update="column1">
                            <g:message code="invoice.label.id"/>
                        </g:remoteSort>
                    </th>
                    <th class="large">
                        <g:remoteSort action="list" sort="contact.firstName, contact.lastName, contact.organizationName, baseUser.userName" alias="[contact: 'baseUser.contact']" update="column1">
                            <g:message code="invoice.label.customer"/>
                        </g:remoteSort>
                    </th>
                    <th class="medium">
                        <g:remoteSort action="list" sort="dueDate" update="column1">
                            <g:message code="invoice.label.duedate"/>
                        </g:remoteSort>
                    </th>
                    <th class="tiny2">
                        <g:remoteSort action="list" sort="invoiceStatus.id" update="column1">
                            <g:message code="invoice.label.status"/>
                        </g:remoteSort>
                    </th>
                    <th class="small">
                        <g:remoteSort action="list" sort="total" update="column1">
                            <g:message code="invoice.label.amount"/>
                        </g:remoteSort>
                    </th>
                    <th class="small">
                        <g:remoteSort action="list" sort="balance" update="column1">
                            <g:message code="invoice.label.balance"/>
                        </g:remoteSort>
                    </th>
                </tr>
	        </thead>
	        
	        <tbody>
			<g:each var="inv" in="${invoices}">
            
                <g:set var="currency" value="${currencies.find{ it.id == inv?.currencyId}}"/>
                <g:set var="contact" value="${ContactDTO.findByUserId(inv?.baseUser?.id)}"/>
                
				<tr id="invoice-${inv.id}" class="${invoice?.id == inv.id ? 'active' : ''}">
					<td class="medium">
						<g:remoteLink breadcrumb="id" class="cell" action="show" id="${inv.id}" params="['template': 'show']" before="register(this);" onSuccess="render(data, next);">
                            <strong>${inv.publicNumber}</strong>
                            <em><g:message code="table.id.format" args="[inv.id as String]"/></em>
						</g:remoteLink>
					</td>
                    <td>
                        <g:remoteLink breadcrumb="id" class="cell double" action="show" id="${inv.id}" params="['template': 'show']" before="register(this);" onSuccess="render(data, next);">
                            <strong>
                                <g:if test="${contact?.firstName || contact?.lastName}">
                                    ${contact.firstName} &nbsp;${contact.lastName}
                                </g:if>
                                <g:else>
                                    ${inv?.baseUser?.userName}
                                </g:else>
                            </strong>
                            <em>${contact?.organizationName}</em>
                        </g:remoteLink>
                    </td>
	            	<td>
						<g:remoteLink breadcrumb="id" class="cell" action="show" id="${inv.id}" params="['template': 'show']" before="register(this);" onSuccess="render(data, next);">
                            <g:formatDate date="${inv?.dueDate}" formatName="date.pretty.format"/>
						</g:remoteLink>
					</td>
					<td>
						<g:remoteLink breadcrumb="id" class="cell" action="show" id="${inv.id}" params="['template': 'show']" before="register(this);" onSuccess="render(data, next);">
                            <g:if test="${inv.isReview == 1}">
                                <g:message code="invoice.status.review"/>
                            </g:if>
                            <g:else>
                                ${inv.getInvoiceStatus().getDescription(session['language_id']) }
                            </g:else>
						</g:remoteLink>
					</td>
					<td>
						<g:remoteLink breadcrumb="id" class="cell" action="show" id="${inv.id}" params="['template': 'show']" before="register(this);" onSuccess="render(data, next);">
                            <g:formatNumber number="${inv.total}"  type="currency" currencySymbol="${currency?.symbol}"/>
						</g:remoteLink>
					</td>
					<td>
						<g:remoteLink breadcrumb="id" class="cell" action="show" id="${inv.id}" params="['template': 'show']" before="register(this);" onSuccess="render(data, next);">
                            <g:formatNumber number="${inv.balance}" type="currency" currencySymbol="${currency?.symbol}"/>
						</g:remoteLink>
					</td>
				</tr>
			</g:each>
			</tbody>
		</table>
	</div>
</div>

<div class="pager-box">
    <div class="row">
        <div class="results">
            <g:render template="/layouts/includes/pagerShowResults" model="[steps: [10, 20, 50], update: 'column1', contactFieldTypes: contactFieldTypes]"/>
        </div>
        <div class="download">
            <sec:access url="/invoice/csv">
                <g:link action="csv" id="${invoice?.id}" params="${sortableParams(params: [partial: true, contactFieldTypes: contactFieldTypes])}">
                    <g:message code="download.csv.link"/>
                </g:link>
            </sec:access>
        </div>
    </div>

    <div class="row">
        <util:remotePaginate controller="invoice" action="list" params="${sortableParams(params: [partial: true, contactFieldTypes: contactFieldTypes])}" total="${invoices?.totalCount ?: 0}" update="column1"/>
    </div>
</div>

<div class="btn-box">
    <div class="row"></div>
</div>
