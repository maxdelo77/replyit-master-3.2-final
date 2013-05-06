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

<%@ page import="com.sapienter.jbilling.server.util.db.LanguageDTO"%>
<div class="column-hold">
	
	<div class="heading">
		<strong style="width: 100%">
			${dto?.notificationMessageType?.getDescription(languageDto.id)}
			<!-- <g:message code="prompt.edit.notification"/> -->
		</strong>
	</div>
	
	<div class="box">
        <div class="sub-box">
    		<table class="dataTable">
                <tr>
            		<td><g:message code="title.notification.active" />:</td>
            		<td class="value"><g:if test="${(dto?.getUseFlag() > 0)}">
            				<g:message code="prompt.yes"/>
            			</g:if>
            			<g:else>
            				<g:message code="prompt.no"/>
            			</g:else>
            		</td>
                </tr>
                <tr>
            		<td><g:message code="notification.label.language"/>:</td>
            		<td class="value">${languageDto.getDescription()}</td>
        		</tr>
        		<g:set var="flag" value="${true}" />
                <tr>
        		<td><g:message code="prompt.edit.notification.subject" />:</td>
        		<td class="value">
        			<g:each in="${dto?.getNotificationMessageSections()}"
        				var="section">
        				<g:if test="${section.section == 1}">
        					<g:hiddenField
        						name="messageSections[${section.section}].id"
        						value="${section.id}" />
        					<g:hiddenField
        						name="messageSections[${section.section}].section"
        						value="${section.section}" />
        					<g:set var="tempContent" value="" />
        					<g:each in="${section.getNotificationMessageLines().sort{it.id}}"
        						var="line">
        						<g:set var="tempContent"
        							value="${tempContent=tempContent + line?.getContent()}" />
        					</g:each>
        					${tempContent}
        					<g:set var="flag" value="${false}" />
        				</g:if>
        			</g:each>
        		</td></tr>
        	
        		<g:set var="flag" value="${true}" />
                <tr>
        		<td><g:message code="prompt.edit.notification.bodytext" />:</td>
        		<td class="value"><g:each in="${dto?.getNotificationMessageSections()}"
        				var="section">
        				<g:if test="${section.section == 2}">
        					<g:hiddenField
        						name="messageSections[${section.section}].id"
        						value="${section.id}" />
        					<g:hiddenField
        						name="messageSections[${section.section}].section"
        						value="${section.section}" />
        					<g:set var="tempContent" value="" />
        					<g:each in="${section.getNotificationMessageLines().sort{it.id}}"
        						var="line">
        						<g:set var="tempContent"
        							value="${tempContent=tempContent + line?.getContent()}" />
        					</g:each>
        					${tempContent}
        					<g:set var="flag" value="${false}" />
        				</g:if>
        			</g:each>
        		</td></tr>
        	
        		<g:set var="flag" value="${true}" />
                <tr>
        		<td><g:message code="prompt.edit.notification.bodyhtml" />:</td>
        		<td class="value"><g:each in="${dto?.getNotificationMessageSections()}"
        				var="section">
        				<g:if test="${section?.section == 3}">
        					<g:hiddenField
        						name="messageSections[${section.section}].id"
        						value="${section?.id}" />
        					<g:hiddenField
        						name="messageSections[${section.section}].section"
        						value="${section?.section}" />
        					<g:set var="tempContent" value="" />
        					<g:each in="${section?.getNotificationMessageLines().sort{it.id}}"
        						var="line">
        						<g:set var="tempContent"
        							value="${tempContent=tempContent + line?.getContent()}" />
        					</g:each>
        					${tempContent}
        					<g:set var="flag" value="${false}" />
        				</g:if>
        			</g:each>
        		</td></tr>
                <tr>
                    <td><g:message code="notification.attachment.value" />:</td>
                    <td class="value">
                        <g:if test="${dto?.getIncludeAttachment()==1}">
                            <g:message code="prompt.yes"/>
                        </g:if>
                        <g:else>
                            <g:message code="prompt.no"/>
                        </g:else>
                </tr>
                <tr>
                    <td><g:message code="notification.attachmentType" />:</td>
                    <td class="value">${dto?.getAttachmentType()}</td>
                </tr>
                <tr>
                    <td><g:message code="notification.attachmentDesign" />:</td>
                    <td class="value">${dto?.getAttachmentDesign()}</td>
                </tr>
                <tr>
                    <td><g:message code="prompt.notification.admin" />:</td>
                    <td class="value"><g:if test="${(dto?.getNotifyAdmin() > 0)}">
                        <g:message code="prompt.yes"/>
                    </g:if>
                    <g:else>
                        <g:message code="prompt.no"/>
                    </g:else>
                    </td>
                </tr>
                <tr>
                    <td><g:message code="prompt.notification.partner" />:</td>
                    <td class="value"><g:if test="${(dto?.getNotifyPartner() > 0)}">
                        <g:message code="prompt.yes"/>
                    </g:if>
                    <g:else>
                        <g:message code="prompt.no"/>
                    </g:else>
                    </td>
                </tr>
                <tr>
                    <td><g:message code="prompt.notification.parent" />:</td>
                    <td class="value"><g:if test="${(dto?.getNotifyParent() > 0)}">
                        <g:message code="prompt.yes"/>
                    </g:if>
                    <g:else>
                        <g:message code="prompt.no"/>
                    </g:else>
                    </td>
                </tr>
                <tr>
                    <td><g:message code="prompt.notification.all.parents" />:</td>
                    <td class="value"><g:if test="${(dto?.getNotifyAllParents() > 0)}">
                        <g:message code="prompt.yes"/>
                    </g:if>
                    <g:else>
                        <g:message code="prompt.no"/>
                    </g:else>
                    </td>
                </tr>
        	</table>
        </div>
	</div>
	
	<div class="btn-box">
	    <a href="${createLink(action: 'edit', params: [id:messageTypeId])}" class="submit edit">
	    	<span><g:message code="button.edit"/></span></a>
	</div>
</div>