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

package jbilling

/**
 * FilterConstraint
 
 * @author Brian Cowdery
 * @since  01-12-2010
 */
enum FilterConstraint {

    EQ, LIKE, DATE_BETWEEN, NUMBER_BETWEEN, SIZE_BETWEEN, IS_EMPTY, IS_NOT_EMPTY, IS_NULL, IS_NOT_NULL, STATUS    
}
