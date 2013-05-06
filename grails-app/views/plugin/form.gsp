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

<html>
<head>
<title>Main Page</title>
<meta name="layout" content="main" />

</head>
<body>
            <div class="form-edit">
                <div class="heading">
                    <strong>
                    <g:if test="${isEdit}">
                        <g:message code="plugins.update.title"/>
                    </g:if>
                    <g:else>
                        <g:message code="plugins.create.title"/>
                    </g:else>
                    </strong>
                </div>
                <div class="form-hold">
                    <g:form name="plugin-form" action="save">
                        <g:hiddenField name="isEdit" value="${isEdit}"/>
                        <fieldset>
                            <div class="form-columns">
                                <div class="one_column" style="width:650px">
                                    <g:hiddenField name="versionNumber" value="${pluginws?.versionNumber}" />
                                    <g:if test="${pluginws?.id > 0}">
                                        <g:set var="this_plugin_id" value="${pluginws?.id}"/>
                                        <g:hiddenField name="id" value="${pluginws?.id}" />
                                        <div class="row">
                                        	<p><g:message code="plugins.plugin.id-long"/></p>
                                        	<span>${pluginws?.id}</span>
                                    	</div>
                                    </g:if>
                                    <g:else>
                                        <g:set var="this_plugin_id" value="0"/>
                                    </g:else>
                                    <div class="row">
                                        <p><g:message code="plugins.create.category"/></p>
                                        <span>${description}</span>
                                    </div>
                                    <div class="row">
                                        <p><g:message code="plugins.plugin.type"/></p>
                                        <span>
                                           <g:select name="typeId"
                                                     from="${types}"
                                                     optionKey="id"
                                                     optionValue="className"
                                                     value="${pluginws?.typeId}" 
                                                     style="width:450px"/>
                                        </span>
                                    </div>
                                    <div class="row">
                                        <label> <g:message code="plugins.plugin.order"/> </label>
                                        <div class="inp-bg inp4">
                                           <g:textField class="field" name="processingOrder" size="2" 
                                                        value="${pluginws?.processingOrder}" />
                                        </div>
                                    </div>
                                </div>
                            </div>
 
                            <!-- box cards -->
                            <div class="box-cards box-cards-open">
                                <div class="box-cards-title">
                                    <span style="float:left;padding-left:20px;">
                                         <g:message code="plugins.create.parameters"/>
                                    </span>
                                </div>
                                <g:render template="formParameters" model="[parametersDesc:parametersDesc]"/>
                            </div>
                            <!-- box text -->
                            <div class="box-text">
                                <g:textArea name="notes" rows="7" cols="63" value="${pluginws?.notes}" />
                            </div>
                            <div class="buttons">
                                <ul>
                                    <li><a class="submit save" onclick="$('#plugin-form').submit();" href="#">
                                        <span><g:message code="plugins.create.save"/></span>
                                    </a></li>
                                    <li><a class="submit cancel" href="${createLink(action:'cancel',params:[plugin_id:this_plugin_id])}"><span>Cancel</span></a></li>
                                </ul>
                            </div>
                        </fieldset>
                    </g:form>
                </div>
            </div>

<script type="text/javascript">
    $("#typeId").change(function () {
        var typeSelected = "";
        $("#typeId option:selected").each(function () {
            typeSelected = $(this).val();
        });
        $.post("${createLink(controller:'plugin', action:'getTypeParametersDescriptions')}",
                {typeId:typeSelected},
                function (msg) {
                    $("#plugin-parameters").html(msg)
                });
    });
</script>
</body>
</html>
