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

<div class="column-hold">
    <div class="heading">
        <strong>
            <g:if test="${selected && !selected.hasErrors()}">
                <g:message code="filters.save.edit.title" args="[selected.name]"/>
            </g:if>
            <g:else>
                <g:message code="filters.save.new.title"/>
            </g:else>
        </strong>
    </div>

    <div class="box">
        <fieldset>
            <div class="form-columns">
                <g:applyLayout name="form/text">
                    <content tag="label"><g:message code="filters.save.label.id"/></content>

                    <g:if test="${selected && !selected.hasErrors()}">
                        ${selected.id}
                        <g:hiddenField name="id" value="${selected?.id}"/>
                    </g:if>
                    <g:else>
                        <em><g:message code="prompt.id.new"/></em>
                    </g:else>
                </g:applyLayout>


 				<g:if test="${selected && !selected.hasErrors()}">
                    <g:applyLayout name="form/text">
                        <content tag="label"><g:message code="filters.save.label.name"/></content>
                        ${selected.name}
                    </g:applyLayout>
                </g:if>
                <g:else>
                    <g:applyLayout name="form/input">
                        <content tag="label"><g:message code="filters.save.label.name"/></content>
                        <content tag="label.for">name</content>
                        <g:textField class="field" name="name" maxlength="30" value="${selected?.name}"/>
                    </g:applyLayout>
                </g:else>
            </div>
        </fieldset>

        <!-- spacer -->
        <div>
            <br/>&nbsp;
        </div>

        <!-- filter values -->
        <table cellpadding="0" cellspacing="0" class="innerTable" width="80%">
            <thead>
            <tr class="innerHeader">
                <th><g:message code="filter.values.th.field"/></th>
                <th><g:message code="filter.values.th.value"/></th>
            </tr>
            </thead>
            <tbody>
            <g:each var="filter" in="${selected?.filters ?: filters}">
                <g:if test="${filter.value}">
                    <tr class="innerContent">
                        <td>${filter.field}</td>
                        <td>${filter.value}</td>
                    </tr>
                </g:if>
            </g:each>
            </tbody>
        </table>
    </div>

    <div class="btn-box">
        <g:if test="${!selected || selected.hasErrors()}">
            <a class="submit save" onclick="$('#filter-save-form').submit();">
                <span><g:message code="button.save"/></span>
            </a>
        </g:if>
        <g:if test="${selected && !selected.hasErrors()}">
            <g:remoteLink class="submit delete" controller="filter" action="delete" id="${selected.id}" update="filtersets">
                <span><g:message code="button.delete"/></span>
            </g:remoteLink>
        </g:if>
    </div>
</div>