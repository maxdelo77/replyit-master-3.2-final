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

<div id="filter-save-dialog" title="Save Filters">
    <g:formRemote name="filter-save-form" url="[controller: 'filter', action: 'save']" update="filtersets">
        <div id="filtersets" class="columns-holder">

            <!-- content rendered using ajax -->

        </div>
    </g:formRemote>
</div>

<script type="text/javascript">
    $(function() {
        $('#filter-save-dialog').dialog({
            autoOpen: false,
            height: 500,
            width: 820,
            modal: true,
            buttons: {
                Close: function() {
                    $(this).dialog("close");
                }
            },
            open: function() {
                $('#filtersets').load("${createLink(controller: 'filter', action: 'filtersets')}");
            },
            close: function() {
                $('#filters').load("${createLink(controller: 'filter', action: 'filters')}");
            }
        });
    });
</script>