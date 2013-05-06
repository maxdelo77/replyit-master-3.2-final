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

import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskException;
import com.sapienter.jbilling.server.process.event.NewUserStatusEvent;
import com.sapienter.jbilling.server.system.event.task.IInternalEventsTask;
import junit.framework.TestCase;

/**
 * @author Brian Cowdery
 * @since 08-04-2010
 */
@SuppressWarnings("unchecked")
public class InternalEventProcessorTest extends TestCase {

    // class under test
    public static final InternalEventProcessor processor = new InternalEventProcessor();

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testIsProcessable() throws Exception {
        TestInternalEventTask task = new TestInternalEventTask();
        task.setSubscribedEvents(new Class[]{ TestEvent.class });

        assertTrue("subscribed to TestEvent, processing TestEvent",
                   processor.isProcessable(task, new TestEvent()));
    }

    public void testIsProcessableNegativeCase() {
        TestInternalEventTask task = new TestInternalEventTask();
        task.setSubscribedEvents(new Class[]{ TestEvent.class });

        assertFalse("subscribed to NewContactEvent, processing TestEvent",
                    processor.isProcessable(task, new NewUserStatusEvent(1, 2, 3, 4)));
    }

    public void testIsProcessableCatchAll() throws Exception {
        TestInternalEventTask task = new TestInternalEventTask();
        task.setSubscribedEvents(new Class[]{ CatchAllEvent.class });
        
        assertTrue("subscribed to CatchAllEvents (any and all events accepted), processing TestEvent",
                   processor.isProcessable(task, new TestEvent()));
    }

    public void testIsProcessableEmptySubscribedEvents() throws Exception {
        TestInternalEventTask task = new TestInternalEventTask();
        task.setSubscribedEvents(new Class[]{});

        assertFalse("not subscribed to any event, processing TestEvent",
                    processor.isProcessable(task, new TestEvent()));
    }

    public void testIsProcessableNullSubscribedEvents() throws Exception {
        TestInternalEventTask task = new TestInternalEventTask();
        task.setSubscribedEvents(null);

        assertFalse("subscribed event list is null, processing TestEvent",
                    processor.isProcessable(task, new TestEvent()));
    }

    /**
     * Test event class
     */
    public static class TestEvent implements Event {
        public String getName() { return "test event"; }
        public Integer getEntityId() { return null; }
    }

    /**
     * Test internal event plug-in
     */
    public static class TestInternalEventTask implements IInternalEventsTask {
        private Class<Event>[] events = new Class[]{};
        
        public Class<Event>[] getSubscribedEvents() { return events; }
        public void setSubscribedEvents(Class<Event>[] events) { this.events = events; }

        public void process(Event event) throws PluggableTaskException { /* noop */ }
    }
}
