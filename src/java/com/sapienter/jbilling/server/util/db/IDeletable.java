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
package com.sapienter.jbilling.server.util.db;

/**
 * 	Interface that determines wheather the DTO object is deleatable or not
 * <p>
 * 	DTO object should implement this interface
 * 
 * @author Panche.Isajeski
 * @since 11-04-2012
 *
 */
public interface IDeletable {
	
	boolean isDeletable();

}
