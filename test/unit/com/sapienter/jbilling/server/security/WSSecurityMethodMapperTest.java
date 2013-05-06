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

import com.sapienter.jbilling.server.security.methods.SecuredMethodFactory;
import com.sapienter.jbilling.server.security.methods.SecuredMethodSignature;
import com.sapienter.jbilling.server.security.methods.SecuredMethodType;
import com.sapienter.jbilling.server.util.IWebServicesSessionBean;
import junit.framework.TestCase;

import java.lang.reflect.Method;

/**
 * WSSecurityMethodMapperTest
 *
 * @author Brian Cowdery
 * @since 19/05/11
 */
public class WSSecurityMethodMapperTest extends TestCase {

    public WSSecurityMethodMapperTest() {
    }

    public WSSecurityMethodMapperTest(String name) {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Quick initialization test to make sure that the mapped web-services API methods
     * exist and can be initialized by the mapper.
     *
     * Useful for catching initialization errors when the API changes.
     *
     * @throws Exception
     */
    public void testWSSecureMethodEnum() throws Exception {
        // init mapper
        WSSecurityMethodMapper mapper = new WSSecurityMethodMapper();
        SecuredMethodSignature method = SecuredMethodFactory.getSignatureByMethodName("getUserWS");
        assertNotNull(method);
    }

    public void testWSSecureMethodType() throws Exception {
        // init mapper
        WSSecurityMethodMapper mapper = new WSSecurityMethodMapper();
        SecuredMethodSignature method = SecuredMethodFactory.getSignatureByMethodName("getUserWS");
        assertNotNull(method);
        assertEquals(method.getType(), SecuredMethodType.USER);
    }
}
