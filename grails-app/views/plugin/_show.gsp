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
  details

  @author E. Conde
  @since  26-11-2010
--%>


<div class="column-hold">

    <!-- the plug-in details -->
    <div class="heading">
        <strong>${plugin?.type?.getDescription(session['language_id'], "title")}</strong>
    </div>
    <div class="box">
      <div class="sub-box">
        <strong><g:message code="plugins.plugin.description"/></strong>
        <p>${plugin?.type?.getDescription(session['language_id'])}</p>
        <br/>
        
        <table class="dataTable">
           <tr>
            <td><g:message code="plugins.plugin.id-long"/></td>
            <td class="value">${plugin?.getId()}</td>
           </tr>
           <tr>
            <td><g:message code="plugins.plugin.notes"/></td>
            <td class="value">
                <g:if test="${plugin?.getNotes() != null}">
                    ${plugin?.getNotes()}
                </g:if>
                <g:else>
                    <g:message code="plugins.plugin.noNotes"/>
                </g:else>
            </td>
           </tr>
           <tr>
            <td><g:message code="plugins.plugin.order"/></td>
            <td class="value">${plugin?.getProcessingOrder()}</td>
           </tr>
           <g:if test="${plugin?.parameters.size() == 0}">
           <tr>
            <td><g:message code="plugins.plugin.noParamaters"/></td>
            <td class="value"><g:message code="plugins.plugin.noParamatersTxt"/></td>
           </tr>
           </g:if>
        </table>
        
        <g:if test="${plugin?.parameters}">
        <table class="innerTable">
             <thead class="innerHeader">
             <tr>
                <th><g:message code="plugins.plugin.parameter"/></th>
                <th><g:message code="plugins.plugin.value"/></th>
             </tr>
             </thead>
             <tbody>
             <g:each in="${plugin?.parameters}">
             <tr>
                <td class="innerContent">${it.name}</td>
                <td class="innerContent">${it.value}</td>
             </tr>         
             </g:each>
             </tbody>
        </table>
        </g:if>
      </div>
    </div>

    <g:render template="/confirm" 
              model="['message':'plugins.delete.confirm','controller':'plugin','action':'delete','id':plugin?.getId()]"/>

    <div class="btn-box">
        <a href="${createLink(controller:'plugin', action:'edit', id:plugin?.getId()) }" class="submit">
            <span><g:message code="plugins.plugin.edit"/></span>
        </a>
        <a onclick="$('#confirm-dialog-delete-${plugin?.id}').dialog('open');" class="submit delete">
            <span><g:message code="plugins.plugin.delete"/></span>
        </a>
    </div>
</div>


