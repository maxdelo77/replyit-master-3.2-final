
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
  Report types list.

  @author Brian Cowdery
  @since  07-Mar-2011
--%>

<div class="table-box">
    <div class="table-scroll">
        <table id="report-types" cellspacing="0" cellpadding="0">
            <thead>
                <tr>
                    <th><g:message code="report.th.type"/></th>
                    <th class="small"><g:message code="report.th.count"/></th>
                </tr>
            </thead>
            <tbody>

            <g:each var="type" in="${types}">

                <tr id="type-${type.id}" class="${selectedTypeId == type.id ? 'active' : ''}">
                    <td>
                        <g:remoteLink class="cell double" action="reports" id="${type.id}" before="register(this);" onSuccess="render(data, next);">
                            <strong>${type.getDescription(session['language_id'])}</strong>
                            <em></em>
                        </g:remoteLink>
                    </td>
                    <td class="small">
                        <g:remoteLink class="cell" action="reports" id="${type.id}" before="register(this);" onSuccess="render(data, next);">
                            <span>${type.reports.size()}</span>
                        </g:remoteLink>
                    </td>
                </tr>

            </g:each>

            </tbody>
        </table>
    </div>
</div>

<div class="btn-box">
    <div class="row"></div>
</div>



