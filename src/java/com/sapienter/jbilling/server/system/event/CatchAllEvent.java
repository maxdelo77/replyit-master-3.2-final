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
package com.sapienter.jbilling.server.system.event;

/**
 * Marker event that can be used as an IInternalEventTask subscription
 * to denote a subscription to any and all internal events.
 * 
 * <code>
 *   // IInternalEventTaskImpl:
 *   public Class<Event>[] getSubscribedEvents() {
 *       return new Class[] { CatchAllEvent.class };
 *   }
 * </code>
 *
 * This event class cannot be instantiated!
 *
 * @author Brian Cowdery
 * @since 07-04-2010
 */
public class CatchAllEvent implements Event {

    private CatchAllEvent() {
        // cannot be instantiated!
    }

    public String getName() {
        return "any/all events";
    }

    public Integer getEntityId() {
        return null;
    }
}
