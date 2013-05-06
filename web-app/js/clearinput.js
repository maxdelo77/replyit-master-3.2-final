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

/**
 * Initialize input field place-holder values.
 */
function placeholder() {
    $(':input.default').not('[placeholder=*]').each(function() {
        var element = $(this);

        if (element.attr('placeholder') == null && element.attr('value') != null)
            element.attr('placeholder', this.getAttribute('value'));

        if (element.val() == null || element.val().length == 0)
            element.val(element.attr('placeholder'));
    });
}

/*
    A rough approximation of the HTML5 input field place-holder text. Provides
    a visible default for the input field that is cleared when the input field
    gains focus.
 */
$(document).ready(function() {
    $('body').delegate(':input.default', 'focus', function() {
        // clear placeholder text on focus
        var element = $(this);
        if (element.val() == element.attr('placeholder'))
            element.val('');


    }).delegate(':input.default', 'blur', function() {
        // no text entered, show placeholder text
        var element = $(this);
        if (element.val().length == 0)
            element.val(element.attr('placeholder'));
    });

    placeholder();
});