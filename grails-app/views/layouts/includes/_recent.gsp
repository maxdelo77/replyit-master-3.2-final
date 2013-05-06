
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
  Recent items side panel.

  @author Brian Cowdery
  @since  09-12-2010
--%>

<div id="recent-items">
    <div class="heading">
        <strong><g:message code="recent.items.title"/></strong>
    </div>
    <ul class="list">
        <g:each var="item" in="${session['recent_items']?.reverse()}">
            <g:set var="type" value="${item.type}"/>
            <li>
                <g:link controller="${type.controller}" action="${type.action}" id="${item.objectId}" params="${type.params}">
                    <img src="${resource(dir:'images', file:type.icon)}" alt="${type.messageCode}"/>
                    <g:message code="${type.messageCode}" args="[item.objectId]"/>
                </g:link>
            </li>
        </g:each>
    </ul>
</div>