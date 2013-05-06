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

package com.sapienter.jbilling.server.security;

/**
 * A simple implementation of WSSecured used by {@link WSSecurityEntityMapper} and {@link WSSecurityMethodMapper}.
 *
 * @author Brian Cowdery
 * @since 02-11-2010
 */
public class MappedSecuredWS implements WSSecured {

    private final Integer owningEntityId;
    private final Integer owningUserId;

    public MappedSecuredWS(Integer owningEntityId, Integer owningUserId) {
        this.owningEntityId = owningEntityId;
        this.owningUserId = owningUserId;
    }

    public Integer getOwningEntityId() {
        return owningEntityId;
    }

    public Integer getOwningUserId() {
        return owningUserId;
    }

    @Override
    public String toString() {
        return "MappedSecuredWS{"
               + "owningEntityId=" + owningEntityId
               + ", owningUserId=" + owningUserId
               + '}';
    }
}
