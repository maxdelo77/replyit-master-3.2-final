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

package com.sapienter.jbilling.server.pluggableTask;

import com.sapienter.jbilling.server.order.db.OrderDTO;
import com.sapienter.jbilling.server.order.db.OrderPeriodDTO;
import com.sapienter.jbilling.server.process.PeriodOfTime;
import com.sapienter.jbilling.server.user.db.CompanyDTO;
import com.sapienter.jbilling.server.user.db.UserDTO;
import junit.framework.TestCase;
import org.joda.time.DateMidnight;
import org.joda.time.DateTimeZone;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * BasicCompositionTaskTest
 *
 * @author Brian Cowdery
 * @since 13/09/11
 */
public class BasicCompositionTaskTest extends TestCase {

    private final TimeZone defaultTimeZone = TimeZone.getDefault();

    /**
     * BasicCompositionTask extended for testing. The locale is settable and this
     * will never attempt to look up the entity preference for appending the order
     * id to the invoice line.
     *
     * This class is needed so that the invoice line description composition can be
     * tested without the need for a live container.
     */
    private class TestBasicCompositionTask extends BasicCompositionTask {

        private Locale locale = Locale.getDefault();

        public void setLocale(Locale locale) {
            this.locale = locale;
        }

        @Override
        protected Locale getLocale(Integer userId) {
            return locale; // for testing, return whatever locale is set
        }

        @Override
        protected boolean appendOrderId(Integer entityId) {
            return false; // for testing, never append the order ID
        }
    }

    // class under test
    private TestBasicCompositionTask task = new TestBasicCompositionTask();


    public BasicCompositionTaskTest() {
    }

    public BasicCompositionTaskTest(String name) {
        super(name);
    }


    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

        // reset timezone back to default
        TimeZone.setDefault(defaultTimeZone);
        DateTimeZone.setDefault(DateTimeZone.forTimeZone(defaultTimeZone));
    }

    public void testComposeDescription() {
        // period being processed
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.clear();

        calendar.set(2011, Calendar.SEPTEMBER, 1);
        Date start = calendar.getTime();

        calendar.set(2011, Calendar.OCTOBER, 1);
        Date end = calendar.getTime();

        PeriodOfTime period = new PeriodOfTime(start, end, 0, 0);

        // verify description
        String description = task.composeDescription(getMockOrder(), period, "Line description");
        assertEquals("Line description Period from 09/01/2011 to 09/30/2011", description);
    }

    public void testComposeDescriptionTZ() {
        // try composing in a different time zone.
        TimeZone EDT = TimeZone.getTimeZone("EDT");
        TimeZone.setDefault(EDT);
        DateTimeZone.setDefault(DateTimeZone.forTimeZone(EDT));

        // period being processed
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.clear();

        calendar.set(2011, Calendar.SEPTEMBER, 1);
        Date start = calendar.getTime();

        calendar.set(2011, Calendar.OCTOBER, 1);
        Date end = calendar.getTime();

        PeriodOfTime period = new PeriodOfTime(start, end, 0, 0);

        // verify description, different timezone shouldn't have affected the dates
        String description = task.composeDescription(getMockOrder(), period, "Line description");
        assertEquals("Line description Period from 09/01/2011 to 09/30/2011", description);
    }

    private OrderDTO getMockOrder() {
        UserDTO user = new UserDTO(1);
        user.setCompany(new CompanyDTO(1));

        OrderDTO order = new OrderDTO();
        order.setBaseUserByUserId(user);
        order.setOrderPeriod(new OrderPeriodDTO(2)); // not a one time period

        return order;
    }
}
