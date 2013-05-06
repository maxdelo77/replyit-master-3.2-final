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
    <table cellpadding="0" cellspacing="0" id="categories">
        <thead>
            <th><g:message code="title.notification.category" />
            </th>
        </thead>
        <tbody>
            <g:each in="${lst}" status="idx" var="dto">
                <tr class="${selectedCategoryId == dto.id ? 'active' : ''}" >
                    <td><g:remoteLink breadcrumb="id" class="cell"
                            action="list" id="${dto.id}"
                            params="['template': 'list', categoryId:dto.id]"
                            before="register(this);"
                            onSuccess="render1('${dto.id}',data, next);"
                        >
                            <strong> ${dto.getDescription(session['language_id'])}
                            </strong>
                            <em><g:message code="table.id.format" args="[dto.id as String]"/></em>
                        </g:remoteLink>
                    </td>
                </tr>
            </g:each>
        </tbody>
    </table>
</div>
<div class="btn-box">
        <g:link controller="notifications" action="editCategory" class="submit add" params="${[add: true]}"><span><g:message code="button.create.notification.category"/></span></g:link>

        <a href="#" onclick="return editCategory();" class="submit edit"><span><g:message code="button.edit.notification.category"/></span></a>
</div>

<!-- edit category control form -->
<g:form name="category-edit-form" controller="notifications" action="editCategory">
    <g:hiddenField name="categoryId" value="${selectedCategoryId}"/>
</g:form>

<script type="text/javascript">
    function render1(id, data, next){
        $('input[name="categoryId"]').attr('value',id);

        render(data, next);
    }

    function editCategory() {
        $('#category-edit-form input#id').val(getSelectedId('#categories'));
        $('#category-edit-form').submit();

        return false;
    }
</script>
