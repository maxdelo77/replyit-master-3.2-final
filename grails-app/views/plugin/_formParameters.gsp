<%@ page import="com.sapienter.jbilling.server.util.Constants; org.quartz.SimpleTrigger" %>
<div id="plugin-parameters" class="box-card-hold">
    <div class="form-columns">
		<div class="one_column">
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

            <g:each var="${param}" in="${parametersDesc}">
                <div class="row">
                    <label>
                        ${param.name}
                    </label>
                    <div class="inp-bg inp4">
                        <g:set var="value" value="${pluginws?.getParameters()?.get(param.name)}"/>
                        <g:if test="${param.name == Constants.PARAM_REPEAT}">
                            <g:textField class="field" name="plg-parm-${param.name}" value="${value?:SimpleTrigger.REPEAT_INDEFINITELY}" />
                        </g:if>
                        <g:else>
                            <g:textField class="field" name="plg-parm-${param.name}" value="${value}" />
                        </g:else>
                    </div>
                </div>
            </g:each>
        </div>
    </div>
</div>