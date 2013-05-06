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

package com.sapienter.jbilling.server.pricing.db;

/**
 * Definition of attributes expected by a pricing strategy.
 *
 * @see com.sapienter.jbilling.server.pricing.util.AttributeUtils
 *
 * @author Brian Cowdery
 * @since 31/01/11
 */
public class AttributeDefinition {

    public enum Type { STRING, TIME, INTEGER, DECIMAL }

    private String name;
    private Type type = Type.STRING;
    private boolean required = false;

    public AttributeDefinition() {
    }

    public AttributeDefinition(String name) {
        this.name = name;
    }

    public AttributeDefinition(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    public AttributeDefinition(String name, Type type, boolean required) {
        this.name = name;
        this.type = type;
        this.required = required;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    @Override
    public String toString() {
        return "Attribute{" + type.name() + ": " + name + (required ? "(required)" : "") + "}";
    }
}
