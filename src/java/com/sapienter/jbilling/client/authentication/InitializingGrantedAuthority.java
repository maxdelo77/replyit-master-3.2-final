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

package com.sapienter.jbilling.client.authentication;

import org.springframework.security.core.GrantedAuthority;

/**
 * Interface for jBilling authoritative classes. These authorities require initialization to
 * ensure that the authority value is available to Spring Security regardless of their
 * dependency on hibernate look-ups, lazy-initialization and other external factors.
 *
 * @author Brian Cowdery
 * @since 05-10-2010
 */
public interface InitializingGrantedAuthority extends GrantedAuthority {
    public void initializeAuthority();
}
