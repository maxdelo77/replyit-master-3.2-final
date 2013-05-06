
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

function toggleSlide(element) {
    var parent = $(element).is('.box-cards') ? element : $(element).parents('.box-cards');

    if ($(parent).is('.box-cards-open')) {
        closeSlide(parent);
    } else {
        openSlide(parent);
    }
}

function openSlide(parent) {
    if ($(parent).not('.box-cards-open')) {
        $(parent).addClass('box-cards-open');
        $(parent).find('.box-card-hold').slideDown(500, function() {
            eval($(parent).attr('onOpen'));
            eval($(parent).attr('onSlide'));
        });
    }
}

function closeSlide(parent) {
    if ($(parent).is('.box-cards-open')) {
        $(parent).removeClass('box-cards-open');
        $(parent).find('.box-card-hold').slideUp(500, function() {
            eval($(parent).attr('onClose'));
            eval($(parent).attr('onSlide'));
        });
    }
}

$(document).ready(function(){
    // hide closed box-cards
    $('.box-cards').each(function(){
        if (!$(this).is('.box-cards-open'))
            $(this).find('.box-card-hold').css('display','none');
    });

    // toggle box-cards on click
    $('a.btn-open', '.box-cards').click(function() {
        toggleSlide(this);
        return false;
    });
});
