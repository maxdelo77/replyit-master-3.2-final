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
package com.sapienter.jbilling.client.util


import java.beans.PropertyEditorSupport;
import java.util.Date;
/**
 * DataBinderService.
 * @author othman El Moulat
 * @since  6/10/12
 *
 */
public class CustomDateBinder extends PropertyEditorSupport {

    @Override
    public void setAsText(String s) throws IllegalArgumentException {
        if (s != null && !s.equals("")) {
            Long longValue = Long.valueOf(s);
            setValue(new Date(longValue));
        }
    }
}