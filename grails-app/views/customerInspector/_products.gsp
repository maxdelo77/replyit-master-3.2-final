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

<%@ page import="com.sapienter.jbilling.server.user.db.CompanyDTO; com.sapienter.jbilling.server.item.db.ItemTypeDTO" %>

<%--
  Shows the product list and provides some basic filtering capabilities.

  @author Brian Cowdery
  @since 28-Feb-2011
--%>

<div class="heading">
    <strong><g:message code="builder.products.title"/></strong>
</div>

<div class="box no-buttons">
    <div class="sub-box">
      <!-- filter -->
        <div class="form-columns">
          <g:formRemote name="products-filter-form" url="[action: 'filterProducts']" update="products-column">
              <g:hiddenField name="userId" value="${user?.id ?: params.userId}"/>
  
              <g:applyLayout name="form/input">
                  <content tag="label"><g:message code="filters.title"/></content>
                  <content tag="label.for">filterBy</content>
                  <g:textField name="filterBy" class="field default" placeholder="${message(code: 'products.filter.by.default')}" value="${params.filterBy}"/>
              </g:applyLayout>
  
              <g:applyLayout name="form/select">
                  <content tag="label"><g:message code="order.label.products.category"/></content>
                  <content tag="label.for">typeId</content>
                  <g:select name="typeId" from="${itemTypes}"
                            noSelection="['': message(code: 'filters.item.type.empty')]"
                            optionKey="id" optionValue="description"
                            value="${params.typeId}"/>
              </g:applyLayout>
          </g:formRemote>
  
          <script type="text/javascript">
              $('#filterBy').blur(function() { $('#products-filter-form').submit(); });
              $('#typeId').change(function() { $('#products-filter-form').submit(); });
              placeholder();
          </script>
      </div>
  
      <!-- product list -->
      <div class="table-box tab-table">
          <div class="table-scroll">
              <table id="products" cellspacing="0" cellpadding="0">
                  <tbody>
  
                  <g:each var="product" in="${products}">
                      <tr>
                          <td>
                              <g:remoteLink class="cell double" action="productPrices" id="${product.id}" params="[userId: user?.id ?: params.userId]" update="prices-column">
                                  <strong>${product.getDescription(session['language_id'])}</strong>
                              </g:remoteLink>
                          </td>
                          <td class="small">
                              <g:remoteLink class="cell" action="productPrices" id="${product.id}" params="[userId: user?.id ?: params.userId]" update="prices-column">
                                  <span>${product.internalNumber}</span>
                              </g:remoteLink>
                          </td>
                          <td class="medium">
                              <g:remoteLink class="cell" action="productPrices" id="${product.id}" params="[userId: user?.id ?: params.userId]" update="prices-column">
                                  <g:if test="${product.percentage}">
                                      %<g:formatNumber number="${product.percentage}" formatName="money.format"/>
                                  </g:if>
                                  <g:else>
                                      <g:set var="price" value="${product.getPrice(new Date())}"/>
                                      <g:formatNumber number="${price?.rate}" type="currency" currencySymbol="${price?.currency?.symbol}"/>
                                  </g:else>
                              </g:remoteLink>
                          </td>
                      </tr>
                  </g:each>
  
                  </tbody>
              </table>
          </div>
      </div>
    </div>
</div>