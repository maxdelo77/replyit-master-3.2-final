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
package com.sapienter.jbilling.server.pluggableTask.admin;

public class ParameterDescription {
	public enum Type { STR, INT, FLOAT, DATE, BOOLEAN };
	
	private final String name;
	private final boolean required;
	private final Type type;
	
	public ParameterDescription(String name, boolean required, Type type) {
		super();
		this.name = name;
		this.required = required;
		this.type = type;
	}
	
	public String getName() {
		return name;
	}
	public boolean isRequired() {
		return required;
	}
	public Type getType() {
		return type;
	}

	@Override
	public String toString() {
		return "ParameterDescription [name=" + name + ", required=" + required
				+ ", type=" + type + "]";
	}
}
