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
  _shortcuts

  @author Brian Cowdery
  @since  09-12-2010
--%>

<div id="shortcuts">
    <div class="heading">
        <a class="arrow open"><strong><g:message code="shortcut.title"/></strong></a>
        <div class="drop">
            <ul>
                <g:each var="shortcut" in="${session['shortcuts']}">
                    <li>
                        <g:remoteLink controller="shortcut" action="remove" params="[id: shortcut.id]" 
                            update="shortcuts" class="shortcut2"/>
                        <g:link controller="${shortcut.controller}" action="${shortcut.action}" id="${shortcut.objectId}">
                            <g:message code="${shortcut.messageCode}" args="[shortcut.objectId]"/>
                        </g:link>
                        
                    </li>
                </g:each>
            </ul>
        </div>
    </div>
</div>