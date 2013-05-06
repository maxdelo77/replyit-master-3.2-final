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

<%@ page import="com.sapienter.jbilling.common.CommonConstants; com.sapienter.jbilling.server.util.db.EnumerationDTO; com.sapienter.jbilling.server.util.db.CurrencyDTO; com.sapienter.jbilling.server.util.db.LanguageDTO; com.sapienter.jbilling.server.item.db.ItemTypeDTO" %>

<html>
<head>
    <meta name="layout" content="main" />

    <r:script disposition="head">
        $(document).ready(function() {
            $('#product\\.percentageAsDecimal').blur(function() {
                if ($(this).val()) {
                    $('#pricing :input:not(#startDate)').val('').prop('disabled', 'true');
                    $('#product\\.excludedTypes').prop('disabled', '');
                    closeSlide('#pricing');
                } else {
                    $('#pricing :input').prop('disabled', '');
                    $('#product\\.excludedTypes').val('').prop('disabled', 'true');

                    //the model.i.oldType field takes the value of the first option of te available modelTypes
                    $("[id$='oldType']").each(function(i, o){
                        $(o).val($(o).siblings("[id$='type']").find("option:first").val())
                    });

                    openSlide('#pricing');
                }
            }).blur();
            loadAvailableDecLang();
            
            var validator = $('#save-product-form').validate();
            validator.init();
            validator.hideErrors();
        });

        function addNewDescription(){
            var languageId = $('#newDescriptionLanguage').val();
            var previousDescription = $("#descriptions div:hidden .descLanguage[value='"+languageId+"']");
            if(previousDescription.size()){
                previousDescription.parents('.row:first').show();
                previousDescription.parents('.row:first').find(".descDeleted").val(false);
                previousDescription.parents('.row:first').find(".descContent").val('');
            }else{
                var languageDescription = $('#newDescriptionLanguage option:selected').text();
                var clone = $('#descriptionClone').children().clone();
                var languagesCount = $('#descriptions').children().size();
                var newName = 'product.descriptions['+languagesCount+']';
                clone.find("label").attr('for', newName+'.content');
                var label = clone.find('label').html();
                clone.find('label').html(label.replace('{0}', languageDescription));

                clone.find(".descContent").attr('id',newName+'.content');
                clone.find(".descContent").attr('name',newName+'.content');

                clone.find(".descLanguage").attr('id',newName+'.languageId');
                clone.find(".descLanguage").attr('name',newName+'.languageId');
                clone.find(".descLanguage").val(languageId);

                clone.find(".descDeleted").attr('id',newName+'.deleted');
                clone.find(".descDeleted").attr('name',newName+'.deleted');

                $('#descriptions').append(clone);
            }
            removeSelectedLanguage();
        }

        function removeDescription(elm){
            var div = $(elm).parents('.row:first');
            //set 'deleted'=true;
            div.find('.descDeleted').val(true);
            div.hide();

            if($("#addDescription").is(':hidden')){
                $("#addDescription").show();
            }
            var langId = div.find(".descLanguage").val();
            var langValue = getValueForLangId(langId);
            if(langValue){
                $("#newDescriptionLanguage").append("<option value='"+langId+"'>"+langValue+"</option>");
            }
        }

        function loadAvailableDecLang(){
            var languages = $('#availableDescriptionLanguages').val().split(',')
            if(languages[0]!=''){
                $.each(languages,function(i,lang){
                   var lang = lang.split('-');
                   $("#newDescriptionLanguage").append("<option value='"+lang[0]+"'>"+lang[1]+"</option>");
                });
            }else{
                $('#addDescription').hide();
            }
        }

        function getValueForLangId(langId){
            var languages = $('#allDescriptionLanguages').val().split(',')
            if(languages[0]!=''){
                var value = false;
                $.each(languages,function(i,lang){
                   var lang = lang.split('-');
                   if(lang[0] == langId){
                       value = lang[1];
                   }
                });
                return value;
            }else{
                return false;
            }
            return false;
        }

        function removeSelectedLanguage(){
            $('#newDescriptionLanguage option:selected').remove();
            if(!$('#newDescriptionLanguage option').size()){
                $('#addDescription').hide();
            }
        }
    </r:script>
</head>
<body>
<div class="form-edit">

    <g:set var="isNew" value="${!product || !product?.id || product?.id == 0}"/>

    <div class="heading">
        <strong>
            <g:if test="${isNew}">
                <g:message code="product.add.title"/>
            </g:if>
            <g:else>
                <g:message code="product.edit.title"/>
            </g:else>
        </strong>
    </div>

    <div class="form-hold">
        <g:form name="save-product-form" action="saveProduct">
            <g:hiddenField name="selectedCategoryId" value="${categoryId}"/>
            <fieldset>
                <!-- product info -->
                <div class="form-columns">
                    <div class="column">
                        <g:applyLayout name="form/text">
                            <content tag="label"><g:message code="product.id"/></content>

                            <g:if test="${isNew}"><em><g:message code="prompt.id.new"/></em></g:if>
                            <g:else>${product?.id}</g:else>

                            <g:hiddenField name="product.id" value="${product?.id}"/>
                        </g:applyLayout>

                        <g:render template="/product/descriptions" model="[product: product]"/>

                        <g:applyLayout name="form/input">
                            <content tag="label"><g:message code="product.percentage"/></content>
                            <content tag="label.for">product.percentageAsDecimal</content>
                            <g:textField class="field" name="product.percentageAsDecimal" value="${formatNumber(number: product?.percentage, formatName: 'price.format')}" size="6"/>
                        </g:applyLayout>

                        <g:applyLayout name="form/checkbox">
                            <content tag="label"><g:message code="product.allow.decimal.quantity"/></content>
                            <content tag="label.for">product.hasDecimals</content>
                            <g:checkBox class="cb checkbox" name="product.hasDecimals" checked="${product?.hasDecimals > 0}"/>
                        </g:applyLayout>

                        <!-- meta fields -->
                        <g:render template="/metaFields/editMetaFields" model="[availableFields: availableFields, fieldValues: product?.metaFields]"/>
                    </div>

                    <div class="column">
                        <g:applyLayout name="form/input">
                            <content tag="label"><g:message code="product.internal.number"/></content>
                            <content tag="label.for">product.number</content>
                            <g:textField class="field" name="product.number" value="${product?.number}" size="40"/>
                        </g:applyLayout>

                        <g:applyLayout name="form/input">
                            <content tag="label"><g:message code="product.gl.code"/></content>
                            <content tag="label.for">product.glCode</content>
                            <g:textField class="field" name="product.glCode" value="${product?.glCode}" size="40"/>
                        </g:applyLayout>

                        <g:applyLayout name="form/select">
                            <content tag="label"><g:message code="product.categories"/></content>
                            <content tag="label.for">product.types</content>

                            <g:set var="types" value="${product?.types?.collect{ it as Integer }}"/>
                            <g:select name="product.types" multiple="true"
                                      from="${categories}"
                                      optionKey="id"
                                      optionValue="description"
                                      value="${types ?: categoryId}"/>
                        </g:applyLayout>

                        <g:applyLayout name="form/select">
                            <content tag="label"><g:message code="product.excludedCategories"/></content>
                            <content tag="label.for">product.excludedTypes</content>

                            <g:set var="types" value="${product?.excludedTypes?.collect{ it as Integer }}"/>
                            <g:select name="product.excludedTypes" multiple="true"
                                      from="${categories}"
                                      optionKey="id"
                                      optionValue="description"
                                      value="${types}"/>
                        </g:applyLayout>
                    </div>
                </div>

                <!-- spacer -->
                <div>
                    <br/>&nbsp;
                </div>

                <!-- pricing controls -->
                <div id="pricing" class="box-cards box-cards-open">
                    <div class="box-cards-title">
                        <a class="btn-open" href="#"><span><g:message code="product.prices"/></span></a>
                    </div>
                    <div class="box-card-hold">
                        <g:set var="startDate" value="${product ? new Date() : CommonConstants.EPOCH_DATE}"/>
                        <g:render template="/priceModel/model" model="[models: product?.defaultPrices, startDate: startDate,priceModelData:priceModelData]"/>
                    </div>
                </div>

                <!-- spacer -->
                <div>
                    <br/>&nbsp;
                </div>

                <div class="buttons">
                    <ul>
                        <li><a onclick="$('#save-product-form').submit();" class="submit save"><span><g:message code="button.save"/></span></a></li>
                        <li><g:link controller="product" action="list" class="submit cancel"><span><g:message code="button.cancel"/></span></g:link></li>
                    </ul>
                </div>

            </fieldset>
        </g:form>
    </div>

</div>
</body>
</html>
