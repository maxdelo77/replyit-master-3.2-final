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

<%@ page import="com.sapienter.jbilling.server.pricing.db.PriceModelStrategy; com.sapienter.jbilling.server.util.Util"%>

<%--
  Product details template. This template shows a product and all the relevant product details.

  @author Brian Cowdery
  @since  16-Dec-2010
--%>


<div class="column-hold">
    <div class="heading">
	    <strong>
	    	${selectedProduct.internalNumber}
	    	<g:if test="${selectedProduct.deleted}">
                <span style="color: #ff0000;">(<g:message code="object.deleted.title"/>)</span>
            </g:if>
	    </strong>
	</div>

	<div class="box">
        <div class="sub-box">
            <!-- product info -->
            <table class="dataTable" cellspacing="0" cellpadding="0">
                <tbody>
                    <tr>
                        <td><g:message code="product.detail.id"/></td>
                        <td class="value">${selectedProduct.id}</td>
                    </tr>
                    <tr>
                        <td><g:message code="product.detail.internal.number"/></td>
                        <td class="value">${selectedProduct.internalNumber}</td>
                    </tr>
                    <tr>
                        <td><g:message code="product.detail.gl.code"/></td>
                        <td class="value">${selectedProduct.glCode}</td>
                    </tr>
                    <tr>
                        <td><g:message code="product.detail.percentage"/></td>
                        <td class="value">
                            <g:if test="${selectedProduct.percentage}">
                                <g:formatNumber number="${selectedProduct.percentage}" formatName="percentage.format"/>
                            </g:if>
                            <g:else>
                                -
                            </g:else>
                        </td>
                    </tr>
                </tbody>
            </table>

            <!-- percentage excluded categories -->
            <g:if test="${selectedProduct.percentage}">
            <table class="dataTable" cellspacing="0" cellpadding="0" width="100%">
                <tbody>
                    <tr class="price">
                        <td><g:message code="product.excludedCategories"/></td>
                        <td class="value">
                            <g:each var="category" status="i" in="${selectedProduct.excludedTypes.sort{ it.description }}">
                                ${category.description}<g:if test="${i < selectedProduct.excludedTypes.size()-1}">, </g:if>
                            </g:each>
                        </td>
                    </tr>
                </tbody>
            </table>
            </g:if>

            <!-- pricing -->
            <g:if test="${selectedProduct.defaultPrices}">
            <table class="dataTable" cellspacing="0" cellpadding="0" width="100%">
                <tbody>
                    <g:render template="/plan/priceModel" model="[model: selectedProduct.getPrice(new Date())]"/>
                </tbody>
            </table>
            </g:if>

            <!-- flags & meta fields -->
            <table class="dataTable" cellspacing="0" cellpadding="0">
                <tbody>
                    <tr>
                        <td><em><g:message code="product.detail.decimal"/></em></td>
                        <td class="value"><em><g:formatBoolean boolean="${selectedProduct.hasDecimals > 0}"/></em></td>
                    </tr>

                    <g:if test="${selectedProduct?.metaFields}">
                        <!-- empty spacer row -->
                        <tr>
                            <td colspan="2"><br/></td>
                        </tr>
                        <g:render template="/metaFields/metaFields" model="[metaFields: selectedProduct?.metaFields]"/>
                    </g:if>
                </tbody>
            </table>

            <p class="description">
                ${selectedProduct.description}
            </p>

            <!-- product categories cloud -->
            <div class="box-cards box-cards-open">
                <div class="box-cards-title">
                    <span><g:message code="product.detail.categories.title"/></span>
                </div>
                <div class="box-card-hold">
                    <div class="content">
                        <ul class="cloud">
                            <g:each var="category" in="${selectedProduct.itemTypes.sort{ it.description }}">
                                <li>
                                    <g:link action="list" id="${category.id}">${category.description}</g:link>
                                </li>
                            </g:each>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="btn-box">
    <g:if test="${!selectedProduct.deleted}">
        <sec:ifAllGranted roles="PRODUCT_41">
            <g:link action="editProduct" id="${selectedProduct.id}" class="submit edit"><span><g:message code="button.edit"/></span></g:link>
        </sec:ifAllGranted>

        <sec:ifAllGranted roles="PRODUCT_42">
            <a onclick="showConfirm('deleteProduct-${selectedProduct.id}');" class="submit delete"><span><g:message code="button.delete"/></span></a>
        </sec:ifAllGranted>
	</g:if>
    </div>

    <g:render template="/confirm"
              model="['message': 'product.delete.confirm',
                      'controller': 'product',
                      'action': 'deleteProduct',
                      'id': selectedProduct.id,
                      'formParams': ['category': selectedCategoryId],
                      'ajax': true,
                      'update': 'column1',
                      'onYes': 'closePanel(\'#column2\')'
                     ]"/>
</div>

