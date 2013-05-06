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

<%@ page import="com.sapienter.jbilling.server.metafields.db.MetaField; com.sapienter.jbilling.server.payment.blacklist.db.BlacklistDTO; com.sapienter.jbilling.server.util.Constants" %>

<div class="column-hold">
    <div class="heading">
        <strong>
            <g:message code="blacklist.entry.title"/>
            <em>${selected.id}</em>
        </strong>
    </div>

    <div class="box">
        <div class="sub-box">
            <fieldset>
                <div class="form-columns">

                    <table cellpadding="0" cellspacing="0" class="dataTable">
                        <tbody>
                            <tr>
                                <td><g:message code="blacklist.entry.label.type"/></td>
                                <td class="value">
                                    <g:message code="blacklist.type.${selected.type}"/>
                                </td>
                            </tr>
                            <tr>
                                <td><g:message code="blacklist.entry.label.source"/></td>
                                <td class="value">
                                    <g:message code="blacklist.source.${selected.source}"/>
                                </td>
                            </tr>

                            <g:if test="${selected.type == BlacklistDTO.TYPE_USER_ID}">
                                <tr>
                                    <td><g:message code="blacklist.entry.label.user.id"/></td>
                                    <td class="value">${selected.user?.id}</td>
                                </tr>
                            </g:if>

                            <g:if test="${selected.type == BlacklistDTO.TYPE_NAME}">
                                <tr>
                                    <td><g:message code="blacklist.entry.label.name"/></td>
                                    <td class="value">
                                        <g:if test="${selected.contact?.firstName || selected.contact?.lastName}">
                                            ${selected.contact.firstName} ${selected.contact.lastName}
                                        </g:if>
                                    </td>
                                </tr>
                                <tr>
                                    <td><g:message code="blacklist.entry.label.organization.name"/></td>
                                    <td class="value">${selected.contact?.organizationName}</td>
                                </tr>
                                <tr>
                                    <td><g:message code="blacklist.entry.label.email"/></td>
                                    <td class="value">${selected.contact?.email}</td>
                                </tr>
                            </g:if>

                            <g:if test="${selected.type == BlacklistDTO.TYPE_IP_ADDRESS}">
                                <g:set var="ipAddressType" value="${MetaField.list().find{ it.name ==~ /.*ip_address.*/ }}"/>
                                <g:if test="${ipAddressType}">
                                    <tr>
                                        <td><g:message code="blacklist.entry.label.ip.address"/></td>
                                        <td class="value">
                                            ${selected.metaFieldValue?.value}
                                        </td>
                                    </tr>
                                </g:if>
                            </g:if>

                            <g:if test="${selected.type == BlacklistDTO.TYPE_PHONE_NUMBER}">
                                <tr>
                                    <td><g:message code="blacklist.entry.label.phone.number"/></td>
                                    <td class="value">
                                        <g:phoneNumber countryCode="${selected.contact?.phoneCountryCode}" 
                                            areaCode="${selected.contact?.phoneAreaCode}" number="${selected.contact?.phoneNumber}"/>
                                    </td>
                                </tr>
                            </g:if>

                        </tbody>
                    </table>

                </div>
            </fieldset>
        </div>
    </div>

    <g:if test="${selected.type == BlacklistDTO.TYPE_CC_NUMBER}">
        <div class="heading">
            <strong><g:message code="blacklist.entry.credit.card.title"/></strong>
        </div>
        <div class="box">
            <div class="sub-box">
                <table class="dataTable" cellspacing="0" cellpadding="0">
                    <tbody>
                        <tr>
                            <td><g:message code="customer.detail.payment.credit.card"/></td>
                            <td class="value">
                                <g:if test="${selected.creditCard?.number}">
                                    %{-- obscure credit card by default, or if the preference is explicitly set --}%
                                    <g:preferenceIsNullOrEquals preferenceId="${Constants.PREFERENCE_HIDE_CC_NUMBERS}" value="1">
                                        <g:set var="creditCardNumber" value="${selected.creditCard.number.replaceAll('^\\d{12}','************')}"/>
                                        ${creditCardNumber}
                                    </g:preferenceIsNullOrEquals>

                                    <g:preferenceEquals preferenceId="${Constants.PREFERENCE_HIDE_CC_NUMBERS}" value="0">
                                        ${selected.creditCard?.number}
                                    </g:preferenceEquals>
                                </g:if>
                            </td>
                        </tr>

                        <tr>
                            <td><g:message code="customer.detail.payment.credit.card.expiry"/></td>
                            <td class="value"><g:formatDate date="${selected.creditCard?.ccExpiry}"/></td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </g:if>

    <g:if test="${selected.type == BlacklistDTO.TYPE_ADDRESS}">
        <div class="heading">
            <strong><g:message code="blacklist.entry.address.title"/></strong>
        </div>
        <div class="box">
            <div class="sub-box">
                <table class="dataTable" cellspacing="0" cellpadding="0">
                    <tbody>
                        <tr>
                            <td><g:message code="customer.detail.contact.address"/></td>
                            <td class="value">${selected.contact?.address1} ${selected.contact?.address2}</td>
                        </tr>
                        <tr>
                            <td><g:message code="customer.detail.contact.city"/></td>
                            <td class="value">${selected.contact?.city}</td>
                        </tr>
                        <tr>
                            <td><g:message code="customer.detail.contact.state"/></td>
                            <td class="value">${selected.contact?.stateProvince}</td>
                        </tr>
                        <tr>
                            <td><g:message code="customer.detail.contact.country"/></td>
                            <td class="value">${selected.contact?.countryCode}</td>
                        </tr>
                        <tr>
                            <td><g:message code="customer.detail.contact.zip"/></td>
                            <td class="value">${selected.contact?.postalCode}</td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </g:if>

    <div class="btn-box buttons">
        <div class="row"></div>
    </div>

</div>
