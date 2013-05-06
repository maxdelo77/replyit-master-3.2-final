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

<%@page import="org.apache.commons.lang.StringUtils; com.sapienter.jbilling.server.order.db.OrderLineTypeDTO"%>

<%--
  Categories list

  @author Brian Cowdery
  @since  16-Dec-2010
--%>

<div class="table-box">
    <div class="table-scroll">
        <table id="categories" cellspacing="0" cellpadding="0">
            <thead>
                <tr>
                    <th><g:message code="product.category.th.name"/></th>
                    <th class="small"><g:message code="product.category.th.type"/></th>
                </tr>
            </thead>
            <tbody>
            <g:each var="category" in="${categories}">
                <g:set var="lineType" value="${new OrderLineTypeDTO(category.orderLineTypeId, 0)}"/>

                    <tr id="category-${category.id}" class="${selectedCategoryId == category.id ? 'active' : ''}">
                        <td>
                            <g:remoteLink class="cell double" action="products" id="${category.id}" before="register(this);" onSuccess="render(data, next);">
                                <strong>${StringUtils.abbreviate(category.description, 45).encodeAsHTML()}</strong>
                                <em><g:message code="table.id.format" args="[category.id as String]"/></em>
                            </g:remoteLink>
                        </td>
                        <td class="small">
                            <g:remoteLink class="cell" action="products" id="${category.id}" before="register(this);" onSuccess="render(data, next);">
                                <span>${lineType.description}</span>
                            </g:remoteLink>
                        </td>
                    </tr>

                </g:each>
            </tbody>
        </table>
    </div>
</div>

<g:if test="${categories?.totalCount > params.max}">
    <div class="pager-box">
        <div class="row left">
            <g:render template="/layouts/includes/pagerShowResults" model="[steps: [10, 20, 50], action: 'categories', update: 'column1']"/>
        </div>
        <div class="row">
            <util:remotePaginate controller="product" action="categories" total="${categories.totalCount}" update="column1"/>
        </div>
    </div>
</g:if>

<div class="btn-box">
    <sec:ifAllGranted roles="PRODUCT_CATEGORY_50">
        <g:link action="editCategory" class="submit add" params="${[add: true]}"><span><g:message code="button.create.category"/></span></g:link>
    </sec:ifAllGranted>

    <sec:ifAllGranted roles="PRODUCT_CATEGORY_51">
        <a href="#" onclick="return editCategory();" class="submit edit"><span><g:message code="button.edit"/></span></a>
    </sec:ifAllGranted>
</div>


<!-- edit category control form -->
<g:form name="category-edit-form" controller="product" action="editCategory">
    <g:hiddenField name="id" value="${selectedCategoryId}"/>
</g:form>

<script type="text/javascript">
    function editCategory() {
        $('#category-edit-form input#id').val(getSelectedId('#categories'));
        $('#category-edit-form').submit();
        return false;
    }
</script>
