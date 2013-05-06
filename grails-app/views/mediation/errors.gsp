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

<html>
<head>
	<meta name="layout" content="main"/>
</head>
<body>

    <g:if test="${record}">
        <div class="table-info" >
            <em>
                <g:message code="event.error.record.mediation.id"/>
                <strong>${record.mediationProcessId}</strong>
            </em>
            <em>
                <g:message code="event.error.record.status.id"/>
                <strong><g:formatNumber number="${record.recordStatusId}"/></strong>
            </em>
        </div>

        <div class="table-area">
            <table>
                <thead>
                    <tr>
                        <td class="first"><g:message code="event.error.th.id"/></td>
                        <td><g:message code="event.error.th.key"/></td>
                        <g:if test="${record}">
                            <g:each var="field" in="${record.fields}">
                                <td>${field.name}</td>
                            </g:each>
                        </g:if>
                        <td class="last"><g:message code="event.error.th.codes"/></td>
                    </tr>
                </thead>
                <tbody>


                <g:each var="recordLine" in="${records}">
                    <tr>
                        <td class="col02">
                            ${recordLine.recordId}
                        </td>
                        <td>
                            ${recordLine.key}
                        </td>

                        <g:each var="pricingField" in="${recordLine.fields}">
                            <td>${pricingField.value}</td>
                        </g:each>

                        <td>
                            <strong>
                                ${recordLine.errors}
                            </strong>
                        </td>
                    </tr>
                </g:each>
                </tbody>
            </table>
        </div>

        <div class="pager-box">
            <div class="row">
                <div class="download">
                    <sec:access url="/mediation/mediationErrorsCsv">
                        <g:link action="mediationErrorsCsv" id="${record?.mediationProcessId}"
                                params="${params + ['status': record?.recordStatusId]}">
                            <g:message code="download.csv.link"/>
                        </g:link>
                    </sec:access>
                </div>
            </div>
        </div>

    </g:if>

</body>
</html>
