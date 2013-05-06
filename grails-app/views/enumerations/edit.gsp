%{--
  jBilling - The Enterprise Open Source Billing System
  Copyright (C) 2003-2011 Enterprise jBilling Software Ltd. and Emiliano Conde

  This file is part of jbilling.

  jbilling is free software: you can redistribute it and/or modify
  it under the terms of the GNU Affero General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  jbilling is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU Affero General Public License for more details.

  You should have received a copy of the GNU Affero General Public License
  along with jbilling.  If not, see <http://www.gnu.org/licenses/>.
  --}%

<html>
<head>
    <meta name="layout" content="main" />
</head>
<body>

<r:script disposition="head">
var jCount=0;
var newHtml= "<div class='row'><label for='values[jCount].value'>jCount</label><span>"
    + "<input id='values[jCount].id' type='hidden' value='0' name='values[jCount].id'>" 
    + "<input id='values[jCount].value' class='field' type='text' value='' name='values[jCount].value'>"
    + "<a onclick='removeEnumerationValue($(this))'><img alt='remove this value' src='/jbilling/images/cross.png'>"
    + "</a></span></div>";

function addEnumerationValue(element) {

    //alert('Event from ' + element);
    //alert ('newHtml was: ' + newHtml);
    modHtml= newHtml.replace(/jCount/g, jCount);
    //alert ('modHtml is: ' + modHtml);
    
    //var existHtml= document.getElementById('addNewHere').innerHTML;
    //alert ('existHtml is: ' + existHtml);
    //if (existHtml != null ) {
    //    modHtml= existHtml + modHtml;
        //alert ('modHtml will be: ' + modHtml);
    //}
    
    var divId= "addNewHere" + jCount;
    var div= document.getElementById(divId)
    //alert('div found' + div);
    div.innerHTML= modHtml;
    ++jCount;
    var newStr="<div id='addNewHere" + jCount + "'></div>"; 
    //alert (newStr);
    $(newStr).insertAfter(div);
    //alert(jCount);
    //form.find('[name=action_name]').val('add');
    //form.submit();
}

function removeEnumerationValue(element) {
    //alert ('remove values ' + _idVal);
    //alert("Remove called from " + element);
    var divRow = $(element).parents('.row')[0];
    //alert(divRow.innerHTML);
    $(divRow).empty();
    $(divRow).remove();
    //$(divRow).find('[type=text]').attr('disabled', true);
    //var form = $(element).parents('form');
    //alert('Found parent ' + divRow);
    //form.find('[name=action_name]').val('remove');
    //form.find('[name=remove_id]').val(_idVal);
    //form.submit();
}
</r:script>

<div class="form-edit">

    <g:set var="isNew" value="${!enumeration || !enumeration?.id || enumeration?.id == 0}"/>

    <div class="heading">
        <strong>
            <g:if test="${isNew}">
                <g:message code="enumeration.add.title"/>
            </g:if>
            <g:else>
                <g:message code="enumeration.edit.title"/>
            </g:else>
        </strong>
    </div>
    
    <div class="form-hold">
        <g:form name="enumeration-edit-form" action="save">
            
            <g:hiddenField name="action_name" value=""/>
            <g:hiddenField name="remove_id" value=""/>

            <fieldset>

                <!-- enumeration -->
                <div class="form-columns">
                    <div class="column">
                    <%-- 
                        <g:applyLayout name="form/text">
                            <content tag="label"><g:message code="enumeration.label.id"/></content>

                            <g:if test="${!isNew}">
                                <span>${enumeration?.id}</span>
                            </g:if>
                            <g:else>
                                <em><g:message code="prompt.id.new"/></em>
                            </g:else>

                        </g:applyLayout>
                    --%>
                        <g:hiddenField name="id" value="${enumeration?.id}"/>
                        <g:hiddenField name="entity" value="${enumeration?.entity?.id}"/>
                        <g:applyLayout name="form/input">
                            <content tag="label"><g:message code="enumeration.label.name"/></content>
                            <content tag="label.for">enumeration?.name</content>
                            <g:textField class="field" name="name" value="${enumeration?.name}"/>
                        </g:applyLayout>

                    </div>
                </div>

                <!-- enumeration values -->
                <div class="form-columns">
                    
                    <!-- column 1 -->
                    <div class="column">
                        <g:set var="count" value="${-1}"/>
                        <g:applyLayout name="form/text">
                            <content tag="label"></content>
                            <content tag="label.for">${''}</content>
                            <g:message code="enumeration.label.message"/>
                            <a onclick="addEnumerationValue(this)">
                                <img src="${resource(dir:'images', file:'add.png')}"
                                    alt="Add more values"/>
                            </a>
                        </g:applyLayout>
                        <g:each var="values" status="n" in="${enumeration?.values}">
                            <g:set var="count" value="${n}"/>
                            <g:applyLayout name="form/text">
                                <content tag="label">${count}</content>
                                <content tag="label.for">values[${count}].value</content>
                                <g:hiddenField name="values[${n}].id" value="${values?.id}"/>
                                <g:textField 
                                    class="field"
                                    name="values[${n}].value"
                                    value="${values.value}"
                                />
                                <a onclick="removeEnumerationValue($(this))">
                                    <img src="${resource(dir:'images', file:'cross.png')}" alt="remove this value"/>
                                </a>
                            </g:applyLayout>
                        </g:each>
                        <g:if test="${count.toInteger() == Integer.valueOf(-1)}"> 
                            <g:set var="count" value="${0}"/>
                             <g:applyLayout name="form/text">
                                <content tag="label">${count}</content>
                                <content tag="label.for">values[${count}].value</content>
                                <g:textField 
                                    name="values[${count}].value"
                                    class="field"
                                    value=""/>
                                <g:hiddenField name="values[${count}].id" value="0"/>
                            </g:applyLayout>
                        </g:if>
                        <script type="text/javascript">
                             jCount= ${++count}
                        </script>
                        <div id="addNewHere${count}"></div>
                    </div>
                </div>
                
                <!-- spacer -->
                <div>
                    &nbsp;<br/>
                </div>

                <div class="buttons">
                    <ul>
                        <li>
                            <a onclick="$('#enumeration-edit-form').submit()" class="submit save"><span><g:message code="button.save"/></span></a>
                        </li>
                        <li>
                            <g:link action="list" class="submit cancel"><span><g:message code="button.cancel"/></span></g:link>
                        </li>
                    </ul>
                </div>

            </fieldset>
        
            
        </g:form>
    </div>
</div>
</body>
</html>