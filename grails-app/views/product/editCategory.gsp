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

<%@ page import="com.sapienter.jbilling.server.order.db.OrderLineTypeDTO" %>

<html>
<head>
    <meta name="layout" content="main" />
</head>
<body>
<div class="form-edit">

    <g:set var="isNew" value="${!category || !category?.id || category?.id == 0}"/>

    <div class="heading">
        <strong>
            <g:if test="${isNew}">
                <g:message code="product.category.add.title"/>
            </g:if>
            <g:else>
                <g:message code="product.category.edit.title"/>
            </g:else>
        </strong>
    </div>

    <div class="form-hold">
        <g:form name="save-category-form" action="saveCategory">
            <fieldset>
                <div class="form-columns">
                    <div class="column">
                        <g:applyLayout name="form/text">
                            <content tag="label"><g:message code="product.category.id"/></content>

                            <g:if test="${isNew}"><em><g:message code="prompt.id.new"/></em></g:if>
                            <g:else>${category?.id}</g:else>

                            <g:hiddenField name="id" value="${category?.id}"/>
                        </g:applyLayout>

                        <g:applyLayout name="form/select">
                            <content tag="label"><g:message code="product.category.type"/></content>
                            <g:select name="orderLineTypeId" from="${OrderLineTypeDTO.list()}"
                                      optionKey="id" optionValue="description"
                                      value="${category?.orderLineTypeId}" />
                        </g:applyLayout>
                    </div>

                    <div class="column">
                        <g:applyLayout name="form/input">
                            <content tag="label"><g:message code="product.category.name"/></content>
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
                        <li><a onclick="$('#save-category-form').submit();" class="submit save"><span><g:message code="button.save"/></span></a></li>
                        <li><g:link action="list" class="submit cancel"><span><g:message code="button.cancel"/></span></g:link></li>
                    </ul>
                </div>
            </fieldset>
        </g:form>
    </div>

</div>
</body>
</html>