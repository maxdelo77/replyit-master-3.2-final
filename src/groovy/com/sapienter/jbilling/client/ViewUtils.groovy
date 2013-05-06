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

package com.sapienter.jbilling.client

import java.util.List;

import org.codehaus.groovy.grails.web.servlet.GrailsFlashScope 
import org.hibernate.StaleObjectStateException;
import org.springframework.context.support.ReloadableResourceBundleMessageSource 
import com.sapienter.jbilling.common.SessionInternalError;

class ViewUtils {
    // thanks Groovy for adding the setters and getters for me
    ReloadableResourceBundleMessageSource messageSource;

    /**
     * Will add to flash.errorMessages a list of string with each error message, if any.
     * @param flash
     * @param locale
     * @param exception
     * @return
     * true if there are validation errors, otherwise false
     */

    boolean resolveException(flash, Locale locale, Exception exception,List <String> emptyFields=null) {
        List<String> messages = new ArrayList<String>();
        if (exception instanceof SessionInternalError && exception.getErrorMessages()?.length > 0) {
            int i=0
            List <String> errorLabelList = [];
            for (String message : exception.getErrorMessages()) {
                List<String> fields = message.split(",");
                String type = messageSource.getMessage("bean." + fields[0], null, locale);
                String property = messageSource.getMessage("bean." + fields[0] + "." + fields[1], null, locale);
                List restOfFields = null;
                if (fields.size() >= 4) {
                    restOfFields = fields[3..fields.size()-1];
                }
                String errorMessage = messageSource.getMessage(fields[2], restOfFields as Object[] , locale);
                String finalMessage
                if (emptyFields){
                    if (emptyFields.getAt(i)){
                        errorLabelList.add(messageSource.getMessage("validation.error.email.preference.${emptyFields.getAt(i)}",
                                [type, property, errorMessage] as Object[], locale))
                    }
                }else{
                    errorMessage = messageSource.getMessage(fields[2], restOfFields as Object[] , locale);
                    finalMessage = messageSource.getMessage("validation.message",
                            [type, property, errorMessage] as Object[], locale);
                    finalMessage = type.equals("Meta Field") ? errorMessage : finalMessage
                    messages.add finalMessage;
                }
                i++
            }
            if (emptyFields){
                errorLabelList.sort();
                errorLabelList.each {messages.add(it)}
            }
            flash.errorMessages = messages;
            return true;
        } else if (exception.getCause() instanceof StaleObjectStateException) {
            // this is two people trying to update the same data
            StaleObjectStateException ex = exception.getCause();
            flash.error = messageSource.getMessage("error.dobule_update", null, locale);
        } else {
            // generic error
            flash.error = messageSource.getMessage("error.exception", [exception.getCause().getMessage()] as Object[], locale);
        }

        return false;
    }

}
