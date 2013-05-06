/*
 * JBILLING CONFIDENTIAL
 * _____________________
 *
 * [2003] - [2012] Enterprise jBilling Software Ltd.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Enterprise jBilling Software.
 * The intellectual and technical concepts contained
 * herein are proprietary to Enterprise jBilling Software
 * and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden.
 */

function singleSelectCheckbox(checkbox) {
	$(checkbox).addClass('checked-checkbox');
	var aGroup = $(checkbox).parents('div.form-hold');
	var allInputs = aGroup.find('input.check');
	allInputs.each(function () {
		if ($(this).hasClass('checked-checkbox')) {
			$(this).removeClass('checked-checkbox');
		} else {
			$(this).attr('checked', false);
			$(this).parent().find('.checkboxAreaChecked').attr('class', 'checkboxArea');
		}
	});
}