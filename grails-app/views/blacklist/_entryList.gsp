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

<%@ page import="com.sapienter.jbilling.server.metafields.MetaFieldHelper; com.sapienter.jbilling.client.user.UserHelper; com.sapienter.jbilling.common.Constants" %>
<table cellpadding="0" cellspacing="0" class="blacklist" width="100%">
    <thead>
    <tr>
        <th class="medium"><g:message code="blacklist.th.name"/></th>
        <th class="small2"><g:message code="blacklist.th.credit.card"/></th>
        <th class="small2"><g:message code="blacklist.th.ip.address"/></th>
    </tr>
    </thead>

    <tbody>
    <g:each var="entry" status="i" in="${blacklist}">
        <tr class="${i % 2 == 0 ? 'even' : 'odd'}">
            <td id="entry-${entry.id}">
                <g:remoteLink class="cell" action="show" id="${entry.id}" before="register(this);" onSuccess="render(data, next);">
                    <g:set var="name" value="${UserHelper.getDisplayName(entry.user, entry.contact)}"/>
                    ${name ?: entry.user?.id ?: entry.contact?.userId ?: entry.contact?.id}
                </g:remoteLink>
            </td>
            <td>
                <g:remoteLink class="cell" action="show" id="${entry.id}" before="register(this);" onSuccess="render(data, next);">
                    <g:if test="${entry.creditCard?.number}">
                    %{-- obscure credit card by default, or if the preference is explicitly set --}%
                        <g:preferenceIsNullOrEquals preferenceId="${Constants.PREFERENCE_HIDE_CC_NUMBERS}" value="1">
                            <g:set var="creditCardNumber" value="${entry.creditCard.number.replaceAll('^\\d{12}','************')}"/>
                            ${creditCardNumber}
                        </g:preferenceIsNullOrEquals>

                        <g:preferenceEquals preferenceId="${Constants.PREFERENCE_HIDE_CC_NUMBERS}" value="0">
                            ${entry?.creditCard?.number}
                        </g:preferenceEquals>
                    </g:if>
                </g:remoteLink>
            </td>
            <td>
                <g:set var="customer" value="${entry.user?.customer}"/>
                <g:if test="${customer}">
                    <g:remoteLink class="cell" action="show" id="${entry.id}" before="register(this);" onSuccess="render(data, next);">
                        ${MetaFieldHelper.getMetaField(customer, ipAddressType?.name)?.field}
                    </g:remoteLink>
                </g:if>
            </td>
        </tr>
    </g:each>
    </tbody>
</table>