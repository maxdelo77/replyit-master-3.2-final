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

<%--
  Layout for labeled and styled radio groups.

  Usage:
  
    <g:applyLayout name="form/radio">
        <content tag="label">Radio Group Label</content>

        <input type="radio" class="rb" name="radio_group_name" id="option_1" />
        <label for="option_1" class="rb">Option 1</label>

        <input type="radio" class="rb" name="radio_group_name" id="option_2" />
        <label for="option_2" class="rb">Option 2</label>        
    </g:applyLayout>


  @author Brian Cowdery
  @since  25-11-2010
--%>

%{-- todo: CSS/Javascript Doesn't allow more than 2 radio elements or allow HTML elements to be marked as 'checked'--}%
<div class="row">
    <label><g:pageProperty name="page.label"/></label>
    <g:layoutBody/>
</div>