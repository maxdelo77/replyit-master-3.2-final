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

package com.sapienter.jbilling.client.editor

import org.springframework.beans.PropertyEditorRegistrar
import org.springframework.beans.PropertyEditorRegistry
import org.springframework.beans.propertyeditors.CustomDateEditor
import java.text.SimpleDateFormat
import org.springframework.context.i18n.LocaleContextHolder

/**
 * CustomPropertyEditorRegistrar 
 *
 * @author Brian Cowdery
 * @since 07/01/11
 */
class CustomPropertyEditorRegistrar implements PropertyEditorRegistrar {

    def messageSource;

    public void registerCustomEditors(PropertyEditorRegistry registry) {
        // parse date values
        registry.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat(messageSource.getMessage('date.format', null, LocaleContextHolder.locale)), true))
    }

}
