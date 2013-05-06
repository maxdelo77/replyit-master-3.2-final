
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

$(document).ready(function() {
    /*
        Highlight clicked rows in table lists and store the selected row id.
     */
    $('body').delegate('.table-box tr', 'click', function() {
        var box = $(this).parents('.table-box')[0];
        $(box).find('tr.active').removeClass('active');
        $(this).addClass('active');
    });
});