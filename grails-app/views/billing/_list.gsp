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

<%@ page import="com.sapienter.jbilling.server.util.db.CurrencyDTO" %>
<%@ page import="com.sapienter.jbilling.server.process.db.BillingProcessDTO"%>
<%@ page import=" com.sapienter.jbilling.server.invoice.db.InvoiceDAS"%>
	
<div class="table-box">
	<div class="table-scroll">
    	<table id="processes" cellspacing="0" cellpadding="0">
			<thead>
				<tr>
					<th class="small">
                        <g:remoteSort action="list" sort="id" update="column1">
                            <g:message code="label.billing.cycle.id"/>
                        </g:remoteSort>
                    </th>
					<th class="medium">
                        <g:remoteSort action="list" sort="billingDate" update="column1">
                            <g:message code="label.billing.cycle.date"/>
                        </g:remoteSort>
                    </th>
                    <th class="small">
                        <g:message code="label.billing.order.count"/>
                    </th>
					<th class="small">
                        <g:message code="label.billing.invoice.count"/>
                    </th>
					<th class="medium">
                        <g:message code="label.billing.total.invoiced"/>
                    </th>
				</tr>
			</thead>
	
			<tbody>
                <g:each var="process" in="${processes}">
                    <tr id="process-${process.id}" class="${selected?.id == process.id ? 'active' : ''} ${process?.isReview > 0 ? 'isReview' : ''}">
                        <td class="small">
                            <g:link class="cell" action="show" id="${process.id}">
                                ${process.id}
                            </g:link>
                        </td>
                        <td class="medium">
                            <g:link class="cell" action="show" id="${process.id}">
                                <g:formatDate date="${process.billingDate}" formatName="date.pretty.format"/>
                            </g:link>
                        </td>
                        <td class="small">
                            <g:link class="cell" action="show" id="${process.id}">
                                ${process.orderProcesses?.size()}
                            </g:link>
                        </td>
                        <td class="small">
                            <%
                                def invoices = new InvoiceDAS().findByProcess(process)
                            %>
                            <g:link class="cell" action="show" id="${process.id}">
                                ${invoices?.size()}
                            </g:link>
                        </td>
                        <td class="medium">
                            <%
                                def invoiced = [:]
                                invoices.each { invoice ->
                                    invoiced[invoice.currency] = invoiced.get(invoice.currency, BigDecimal.ZERO).add(invoice.total)
                                }
                            %>
                            <g:link class="cell" action="show" id="${process.id}">
                                <g:if test="${invoiced.keySet().size() == 1}">
                                    <g:each var="total" in="${invoiced.entrySet()}">
                                        <g:formatNumber number="${total.value}" type="currency" currencySymbol="${total.key.symbol}"/> <br/>
                                    </g:each>
                                </g:if>
                                <g:else>
                                    <em><g:message code="label.billing.multi.currency"/></em>
                                </g:else>
                            </g:link>
                        </td>
                    </tr>
                </g:each>
			</tbody>
		</table>
	</div>
</div>

<g:if test="${processes?.totalCount > params.max}">
    <div class="pager-box">
        <util:remotePaginate controller="billing" action="index" params="[applyFilter: true]" total="${processes?.totalCount}" update="column1"/>
    </div>
</g:if>

<div class="pager-box">
    <div class="row">
        <div class="results">
            <g:render template="/layouts/includes/pagerShowResults" model="[steps: [10, 20, 50], update: 'column1']"/>
        </div>
    </div>

    <div class="row">
        <util:remotePaginate controller="mediation" action="list" params="${sortableParams(params: [partial: true])}" total="${processes?.totalCount ?: 0}" update="column1"/>
    </div>
</div>

<div class="btn-box">
    <div class="row"></div>
</div>
