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

<div class="column-hold">
    <div class="heading">
        <strong>
                <g:message code="notification.add.title"/>
        </strong>
    </div>

    <g:form name="save-notification-form" url="[action: 'saveNotificationMessage']">

        <div class="box">
            <div class="sub-box">
              <fieldset>
                <div class="form-columns">
                    <g:applyLayout name="form/text">
                        <content tag="label"><g:message code="notification.id"/></content>

                        <em><g:message code="prompt.id.new"/></em>
                    </g:applyLayout>

                    <g:applyLayout name="form/text">
                        <content tag="label"><g:message code="notification.category.description"/></content>

                        <em>${category?.description}</em>

                        <g:hiddenField name="categoryId" value="${category?.id}"/>
                    </g:applyLayout>


                    <g:applyLayout name="form/input">
                        <content tag="label"><g:message code="notification.description"/></content>
                        <content tag="label.for">description</content>
                        <g:textField class="field" name="description" />
                    </g:applyLayout>
                </div>
            </fieldset>
          </div>
        </div>

    </g:form>

    <div class="buttons">
        <ul>
            <li><a onclick="$('#save-notification-form').submit();" class="submit save"><span><g:message code="button.save"/></span></a></li>
            <li><g:link action="listCategories" id="${selectedCategoryId}" class="submit cancel"><span><g:message code="button.cancel"/></span></g:link></li>
        </ul>
    </div>
</div>
