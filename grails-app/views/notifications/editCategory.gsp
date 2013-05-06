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

<%@ page contentType="text/html;charset=UTF-8" %>

<%--
  Shows an edit form for a currency (used to create new currencies).

  @author Shweta Gupta
  @since  11-Jun-2012
--%>

<html>
<head>
    <meta name="layout" content="main"/>
</head>

<body>
<div class="form-edit">

    <g:set var="isNew" value="${!category || !category?.id || category?.id == 0}"/>

    <div class="heading">
        <strong>
            <g:if test="${isNew}">
                <g:message code="notification.category.add.title"/>
            </g:if>
            <g:else>
                <g:message code="notification.category.edit.title"/>
            </g:else>
        </strong>
    </div>

    <div class="form-hold">
        <g:form name="save-category-form" url="[action: 'saveCategory']">
            <fieldset>
                <div class="form-columns">
                    <div class="column">
                        <g:applyLayout name="form/text">
                            <content tag="label"><g:message code="notification.category.id"/></content>

                            <g:if test="${isNew}"><em><g:message code="prompt.id.new"/></em></g:if>
                            <g:else>${category?.id}</g:else>

                            <g:hiddenField name="id" value="${category?.id}"/>
                        </g:applyLayout>

                        <g:applyLayout name="form/input">
                            <content tag="label"><g:message code="notification.category.name"/></content>
                            <content tag="label.for">description</content>
                            <g:textField class="field" name="description" value="${category?.description}"/>
                        </g:applyLayout>
                    </div>
                </div>

                <div>
                    <br/>&nbsp;
                </div>

                <div class="buttons">
                    <ul>
                        <li><a onclick="$('#save-category-form').submit();" class="submit save"><span><g:message
                                code="button.save"/></span></a></li>
                        <li><g:link action="listCategories" class="submit cancel"><span><g:message
                                code="button.cancel"/></span></g:link></li>
                    </ul>
                </div>
            </fieldset>
        </g:form>
    </div>

</div>
</div>
</body>
</html>
