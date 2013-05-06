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

<div class="table-box">
<table cellpadding="0" cellspacing="0">
    <thead>
       <tr class="active">
        <th ><g:message code="plugins.category.list.id"/></th>
        <th><g:message code="plugins.category.list.title"/></th>
       </tr>
    </thead>
    <tbody>
    <g:each in="${categories}" status="idx" var="dto">
    <tr>
    	<td>
        	<g:remoteLink action="plugins" id="${dto.id}" before="register(this);" 
                                   onSuccess="render(data, next);"
                                   params="[template:'show']">
                ${dto.getId()}
        	</g:remoteLink>
        </td>
        <td>
        <g:remoteLink action="plugins" id="${dto.id}" before="register(this);" 
                                   onSuccess="render(data, next);"
                                   params="[template:'show']">
             <strong>
                ${dto.getDescription(session['language_id'])}
		     </strong>
		     <em>
                ${dto.getInterfaceName()}
		     </em>
        </g:remoteLink>
        </td>
    </tr>
    </g:each>
    </tbody>
</table>
</div>