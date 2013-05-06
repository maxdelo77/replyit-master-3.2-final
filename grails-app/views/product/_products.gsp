<%@ page import="org.apache.commons.lang.StringUtils" %>

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
  Products list

  @author Brian Cowdery
  @since  16-Dec-2010
--%>

<g:set var="paginateAction" value="${actionName == 'products' || actionName == 'list' ? 'products' : 'allProducts'}"/>

<%-- list of products --%>
<g:if test="${products}">
    <div class="table-box">
        <div class="table-scroll">
            <table id="products" cellspacing="0" cellpadding="0">
                <thead>
                <tr>
                    <th>
                        <g:remoteSort action="${paginateAction}" id="${selectedCategoryId}" sort="id" update="column2">
                            <g:message code="product.th.name"/>
                        </g:remoteSort>
                    </th>
                    <th class="medium">
                        <g:remoteSort action="${paginateAction}" id="${selectedCategoryId}" sort="internalNumber" update="column2">
                            <g:message code="product.th.internal.number"/>
                        </g:remoteSort>
                    </th>
                </tr>
                </thead>
                <tbody>

                <g:each var="product" in="${products}">

                    <tr id="product-${product.id}" class="${selectedProduct?.id == product.id ? 'active' : ''}">
                        <td>
                            <g:remoteLink class="cell double" action="show" id="${product.id}" params="['template': 'show', 'category': selectedCategoryId]" before="register(this);" onSuccess="render(data, next);">
                                <strong>${StringUtils.abbreviate(product.getDescription(session['language_id']), 45).encodeAsHTML()}</strong>
                                <em><g:message code="table.id.format" args="[product.id as String]"/></em>
                            </g:remoteLink>
                        </td>
                        <td class="medium">
                            <g:remoteLink class="cell" action="show" id="${product.id}" params="['template': 'show', 'category': selectedCategoryId]" before="register(this);" onSuccess="render(data, next);">
                                <span>${product.internalNumber}</span>
                            </g:remoteLink>
                        </td>
                    </tr>

                </g:each>

                </tbody>
            </table>
        </div>
    </div>
</g:if>

<%-- no products to show --%>
<g:if test="${!products}">
    <div class="heading"><strong><em><g:message code="product.category.no.products.title"/></em></strong></div>
    <div class="box">
      <div class="sub-box">
        <g:if test="${selectedCategoryId}">
            <em><g:message code="product.category.no.products.warning"/></em>
        </g:if>
        <g:else>
            <em><g:message code="product.category.not.selected.message"/></em>
        </g:else>
      </div>
    </div>
</g:if>

<div class="pager-box">
    <div class="row">
        <div class="results">
            <g:render template="/layouts/includes/pagerShowResults" model="[steps: [10, 20, 50], action: paginateAction, update: 'column2',id:selectedCategoryId]"/>
        </div>
        <div class="download">
            <sec:access url="/product/csv">
                <g:link action="csv" id="${selectedCategoryId}">
                    <g:message code="download.csv.link"/>
                </g:link>
            </sec:access>
        </div>
    </div>

    <div class="row">
        <util:remotePaginate controller="product" action="${paginateAction}" id="${selectedCategoryId}" params="${sortableParams(params: [partial: true])}" total="${products?.totalCount ?: 0}" update="column1"/>
    </div>
</div>

<div class="btn-box">
    <g:if test="${selectedCategoryId}">
        <sec:ifAllGranted roles="PRODUCT_40">
            <g:link action="editProduct" params="['category': selectedCategoryId]" class="submit add"><span><g:message code="button.create.product"/></span></g:link>
        </sec:ifAllGranted>

        <g:if test="${!products}">
            <sec:ifAllGranted roles="PRODUCT_CATEGORY_52">
                <a onclick="showConfirm('deleteCategory-${selectedCategoryId}');" class="submit delete"><span><g:message code="button.delete.category"/></span></a>
            </sec:ifAllGranted>
        </g:if>
    </g:if>
    <g:else>
        <em><g:message code="product.category.not.selected.message"/></em>
    </g:else>
    <sec:access url="/product/allProducts">
        <g:remoteLink action="allProducts" update="column2" class="submit show" onSuccess="\$('.submit.show').hide();" ><span><g:message code="button.show.all"/></span></g:remoteLink>
    </sec:access>
</div>

<g:render template="/confirm"
          model="['message':'product.category.delete.confirm',
                  'controller':'product',
                  'action':'deleteCategory',
                  'id':selectedCategoryId,
                  'ajax':true,
                  'update':'column1',
                  'onYes': 'closePanel(\'#column2\')'
                 ]"/>

<script type="text/javascript">
$(function(){
    $('div#paginate a').click(function(){
        $('#column2').html('');
    });
});
</script>
