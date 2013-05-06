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

<%@page import="com.sapienter.jbilling.server.process.db.AgeingEntityStepDTO" %>

<script type="text/javascript">
$(".numericOnly").keydown(function(event){
    // Allow only backspace, delete, left & right
    if ( event.keyCode==37 || event.keyCode== 39 || event.keyCode == 46 || event.keyCode == 8 || event.keyCode == 9 ) {
        // let it happen, don't do anything
    }
    else {
        // Ensure that it is a number and stop the keypress
        if (event.keyCode < 48 || event.keyCode > 57 ) {
            event.preventDefault();
        }
    }
});

$(".numericOnly").change(function (event){
    //alert( $(this).parent().parent().find(":input[type=checkbox]").is(':checked'));
    if ($(this).val() > 0 ) {
        $(this).parent().parent().find(':input[type=checkbox]').attr('checked', true);
    //} else {
        //$(this).parent().parent().find(':input[type=checkbox]').attr('checked', false);
    }
});
</script>

<div class="form-edit" style="width:650px">

    <div class="heading">
        <strong><g:message code="configuration.title.aging"/></strong>
    </div>

    <div class="form-hold">
        <g:form name="save-aging-form" action="saveAging">
            <g:hiddenField name="recCnt" value="${ageingSteps?.length}"/>
            <fieldset>
                <div class="form-columns" style="width:650px">
                    <div class="one_column" style="padding-right: 0px;">
                    <table class="innerTable" >
                        <thead class="innerHeader">
                             <tr>
                                <th class="tiny"><g:message code="config.ageing.step"/></th>
                                <th class="tiny"><g:message code="config.ageing.forDays"/></th>
                                <th class="tiny"><g:message code="config.ageing.inUse"/></th>
                             </tr>
                         </thead>
                         <tbody>
                            <g:each status="iter" var="step" in="${ageingSteps}">
                                <tr>
                                    <td class="tiny"><label>${step?.statusStr}</label></td>
                                    <td class="tiny">
                                        <g:if test="${step.statusId == 1 }">
                                            <g:textField class="inp-bg numericOnly inp4" name="gracePeriod" value="${gracePeriod}"/>
                                        </g:if>
                                        <g:else>
                                            <g:textField class="inp-bg numericOnly inp4" name="obj[${iter}].days" value="${step.days}"/>
                                        </g:else>
                                    </td>
                                    <td class="tiny">
                                    <g:if test="${step.statusId == 1 }">
                                        <label for="obj[${iter}].days" style="text-align:left; font-weight:bold">
                                        <g:message code="config.ageing.gracePeriod"/></label>
                                        <g:hiddenField value="${true}" name="obj[${iter}].inUse"/>
                                    </g:if>
                                    <g:else>
                                        <g:checkBox class="cb checkbox" name="obj[${iter}].inUse" checked="${step.inUse }"/>
                                    </g:else>
                                    <g:hiddenField value="${step?.statusId}" name="obj[${iter}].statusId"/>
                                    <g:hiddenField value="placeholder_text" name="obj[${iter}].welcomeMessage"/>
                                    <g:hiddenField value="placeholder_text" name="obj[${iter}].failedLoginMessage"/>
                                    </td>
                                </tr>
                            </g:each>
                        </tbody>
                    </table>
                    <div class="row">&nbsp;</div>
                    </div>
                </div>
            </fieldset>
             <div class="btn-box">
                <a onclick="$('#save-aging-form').submit();" class="submit save"><span><g:message code="button.save"/></span></a>
                <g:link controller="config" action="index" class="submit cancel"><span><g:message code="button.cancel"/></span></g:link>
            </div>
        </g:form>
    </div>
</div>