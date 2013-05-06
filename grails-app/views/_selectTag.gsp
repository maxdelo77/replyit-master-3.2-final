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

<select name="${name}" id="${name}" class="${cssClass}">
	<g:each var="sarr" in="${list}" status="idx">
		<g:if test="${value == sarr[0]}">
			<option selected value="${sarr[0]}">${sarr[1]}</option>
		</g:if>
		<g:else>
			<option value="${sarr[0]}">${sarr[1]}</option>
		</g:else>
	</g:each>
</select>