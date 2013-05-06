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

<%@ page import="com.sapienter.jbilling.server.util.db.PreferenceDTO; com.sapienter.jbilling.server.util.Constants; org.apache.commons.lang.StringUtils; org.apache.commons.lang.WordUtils" contentType="text/html;charset=UTF-8" %>

<%--
  Shows a list of all preferences

  @author Brian Cowdery
  @since  01-Apr-2011
--%>

<div class="table-box">
    <table id="users" cellspacing="0" cellpadding="0">
        <thead>
            <tr>
                <th><g:message code="preference.th.type"/></th>
                <th class="medium2"><g:message code="preference.th.value"/></th>
            </tr>
        </thead>

        <tbody>
            <g:each var="type" in="${preferenceTypes}">
                <tr id="type-${type.id}" class="${selected?.id == type.id ? 'active' : ''}">
                    <td>
                        <g:remoteLink class="cell double" action="show" id="${type.id}" before="register(this);" onSuccess="render(data, next);">
                            <strong>${StringUtils.abbreviate(type.getDescription(session['language_id']), 50)}</strong>
                            <em>Id: ${type.id}</em>
                        </g:remoteLink>
                    </td>

                    <td class="medium2">
                        <g:remoteLink class="cell" action="show" id="${type.id}" before="register(this);" onSuccess="render(data, next);">

                            <g:if test="${type.preferences}">
                                %{
                                    PreferenceDTO preferenceDto = null;            
									for (PreferenceDTO preference : type.preferences) {
										if (preference.jbillingTable.name == Constants.TABLE_ENTITY && preference.foreignId == session['company_id']) {
											preferenceDto = preference;
											break; 
										}
									}
                                }%
                                ${preferenceDto != null ? preferenceDto.value : type.defaultValue}
                            </g:if>
                            <g:else>
                                ${type.defaultValue}
                            </g:else>

                        </g:remoteLink>
                    </td>
                </tr>
            </g:each>

        </tbody>
    </table>
</div>