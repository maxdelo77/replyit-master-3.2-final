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

<div class="table-box">
    <table cellpadding="0" cellspacing="0">
        <thead>
            <th><g:message code="metaField.category.th.name" />
            </th>
        </thead>
        <tbody>
            <g:each in="${lst}" status="idx" var="dto">
                <tr class="${dto.name() == (selectedCategory)? 'active' : '' }">
                    <td><g:remoteLink id="${dto.name()}" class="cell"
                            action="list"
                            params="['template': 'list', selectedCategory: dto.name()]"
                            before="register(this);"
                            onSuccess="render(data, next);"
                        >
                            <strong> ${dto.name()}
                            </strong>
                        </g:remoteLink></td>
                </tr>
            </g:each>
        </tbody>
    </table>
</div>
<div class="btn-box">
    <div class="row"></div>
</div>