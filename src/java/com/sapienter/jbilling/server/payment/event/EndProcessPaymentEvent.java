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

package com.sapienter.jbilling.server.payment.event;

import com.sapienter.jbilling.server.system.event.Event;

public class EndProcessPaymentEvent implements Event {

    private final Integer runId;
    private final Integer entityId;
    
    public EndProcessPaymentEvent(Integer runId,Integer entityId) {
        this.runId = runId;
        this.entityId= entityId;
    }

    public Integer getEntityId() {
        return entityId;
    }
    
    public Integer getRunId() {
        return runId;
    }

    public String getName() {
        return "End of asych payment processing event";
    }

}
