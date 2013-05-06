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

<%@ page import="com.sapienter.jbilling.server.metafields.db.MetaField" %>

<g:set var="ipAddressType" value="${MetaField.list().find{ it.name ==~ /.*ip_address.*/ }}"/>

<div class="form-edit">
    <div class="heading">
        <strong><g:message code="blacklist.title"/></strong>
    </div>
    <div class="form-hold">
        <fieldset>
            <div class="form-columns single">
                <div class="column single">

                    <!-- blacklist upload -->
                    <g:uploadForm name="save-blacklist-form" url="[action: 'save']">
                        <g:applyLayout name="form/text">
                            <content tag="label"><g:message code="blacklist.label.csv.file"/></content>
                            <input type="file" name="csv"/>
                        </g:applyLayout>

                        <g:applyLayout name="form/radio">
                            <content tag="label">&nbsp;</content>

                            <g:radio class="rb" id="csvUpload.add" name="csvUpload" value="add" />
                            <label class="rb" for="csvUpload.add"><g:message code="blacklist.label.upload.type.add"/></label>

                            <g:radio class="rb" id="csvUpload.modify" name="csvUpload" value="modify" checked="${true}"/>
                            <label class="rb" for="csvUpload.modify"><g:message code="blacklist.label.upload.type.upload"/></label>
                        </g:applyLayout>

                        <div class="btn-row">
                            <br/>
                            <a onclick="$('#save-blacklist-form').submit();" class="submit save"><span><g:message code="button.update"/></span></a>
                        </div>
                    </g:uploadForm>


                    <!-- separator -->
                    <div>
                        <hr/>
                    </div>

                    <!-- blacklist entry list -->
                    <g:formRemote name="filter-form" url="[action: 'filter']" update="blacklist">
                        <g:applyLayout name="form/input">
                            <content tag="label"><g:message code="filters.title"/></content>
                            <content tag="label.for">filterBy</content>
                            <g:textField name="filterBy" class="field default" placeholder="${message(code: 'blacklist.filter.by.default')}" value="${params.filterBy}"/>
                        </g:applyLayout>

                        <script type="text/javascript">
                            $('#filterBy').blur(function() { $('#filter-form').submit(); });
                            placeholder();
                        </script>
                    </g:formRemote>


                    <div id="blacklist" style="height: 400px; overflow-y: scroll; margin: 10px 0; border: 1px solid #bbb;">
                        <g:render template="entryList" model="[blacklist: blacklist]"/>
                    </div>

                    <strong><g:message code="blacklist.label.entries" args="[blacklist.size()]"/></strong>

                    <!-- spacer -->
                    <div>
                        <br/>&nbsp;
                    </div>

                </div>
            </div>
        </fieldset>
    </div>
</div>