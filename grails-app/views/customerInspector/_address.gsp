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

<%--
  Address data-table template for the customer inspector.

  @author Brian Cowdery
  @since  11-Jan-2011
--%>

<table class="dataTable" cellspacing="0" cellpadding="0">
    <tbody>
    <tr>
        <td><g:message code="customer.detail.contact.name"/></td>
        <td class="value">${contact.firstName} ${contact.initial} ${contact.lastName}</td>

        <td><g:message code="customer.detail.contact.organization"/></td>
        <td class="value">${contact.organizationName}</td>
    </tr>
    <tr>
        <td><g:message code="customer.detail.contact.telephone"/></td>
        <td class="value">
            <g:phoneNumber countryCode="${contact?.phoneCountryCode}" 
                    areaCode="${contact?.phoneAreaCode}" number="${contact?.phoneNumber}"/>
        </td>

        <td><g:message code="customer.detail.contact.fax"/></td>
        <td class="value">
            <g:if test="${contact?.faxCountryCode}">${contact?.faxCountryCode}.</g:if>
            <g:if test="${contact?.faxAreaCode}">${contact?.faxAreaCode}.</g:if>
            ${contact?.faxNumber}
        </td>

        <td><g:message code="customer.detail.user.email"/></td>
        <td class="value">${contact?.email}</td>
    </tr>
    <tr>
        <td><g:message code="customer.detail.contact.address"/></td>
        <td class="value">${contact?.address1} ${contact?.address2}</td>
    </tr>
    <tr>
        <td><g:message code="customer.detail.contact.city"/></td>
        <td class="value">${contact?.city}</td>

        <td><g:message code="customer.detail.contact.state"/></td>
        <td class="value">${contact?.stateProvince}</td>

        <td><g:message code="customer.detail.contact.zip"/></td>
        <td class="value">${contact?.postalCode}</td>
    </tr>
    <tr>
        <td><g:message code="customer.detail.contact.country"/></td>
        <td class="value">${contact?.countryCode}</td>
    </tr>
    <tr>
        <td><g:message code="prompt.include.in.notifications"/></td>
        <td class="value"><g:formatBoolean boolean="${ (contact?.include instanceof Boolean) ? contact.include : (contact.include > 0) }"/></td>
    </tr>
    </tbody>
</table>



