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

package com.sapienter.jbilling.server.process;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import junit.framework.TestCase;

import org.joda.time.DateMidnight;

import com.sapienter.jbilling.common.Util;
import com.sapienter.jbilling.server.invoice.InvoiceWS;
import com.sapienter.jbilling.server.order.OrderProcessWS;
import com.sapienter.jbilling.server.order.OrderWS;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskWS;
import com.sapienter.jbilling.server.user.UserDTOEx;
import com.sapienter.jbilling.server.user.UserWS;
import com.sapienter.jbilling.server.util.Constants;
import com.sapienter.jbilling.server.util.api.JbillingAPI;
import com.sapienter.jbilling.server.util.api.JbillingAPIFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.sapienter.jbilling.test.Asserts.*;
import static org.testng.AssertJUnit.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


/**
 * Points to testOrders: Orders : - next billable day - to_process - start/end of billing period - invoice has (not)
 * generated - billing process relationship - some amounts of the generated invoice Invoices : - if the invoice has been
 * processed or no - to_process - delegated_invoice_id is updated
 *
 * @author Emil
 */
@Test(groups = { "integration", "process", "billing" })
public class BillingProcessTest {

    private JbillingAPI api;

    GregorianCalendar cal;
    Date processDate = null;
    final Integer entityId = 1;
    Integer languageId = null;
    Date runDate = null;
    
    private static final Integer ORDER_PERIOD_PLUGIN_ID = 6;
    private static final Integer BASIC_ORDER_PERIOD_PLUGIN_TYPE_ID = 7; // BasicOrderPeriodTask
    private static final Integer PRO_RATE_ORDER_PERIOD_PLUGIN_TYPE_ID = 37; // ProRateOrderPeriodTask

    @BeforeClass
    protected void setUp() throws Exception {
        api = JbillingAPIFactory.getAPI();

        languageId = new Integer(1);
        cal = new GregorianCalendar();
        cal.clear();
        cal.set(2006, GregorianCalendar.OCTOBER, 26, 0, 0, 0);
        runDate = cal.getTime();
    }

    @Test
    public void test001EndOfMonthCorrection() throws Exception {
        System.out.println("#test001EndOfMonthCorrection");

        // set the configuration to something we are sure about
        /*
        BillingProcessConfigurationWS config = api.getBillingProcessConfiguration();

        config.setNextRunDate(new DateMidnight(2000, 12, 1).toDate());
        config.setRetries(new Integer(1));
        config.setDaysForRetry(new Integer(5));
        config.setGenerateReport(new Integer(1));
        config.setAutoPayment(new Integer(1));
        config.setAutoPaymentApplication(new Integer(1));
        config.setDfFm(new Integer(0));
        config.setDueDateUnitId(Constants.PERIOD_UNIT_MONTH);
        config.setDueDateValue(new Integer(1));
        config.setInvoiceDateProcess(new Integer(1));
        config.setMaximumPeriods(new Integer(10));
        config.setOnlyRecurring(new Integer(1));
        config.setPeriodUnitId(Constants.PERIOD_UNIT_MONTH);
        config.setPeriodValue(new Integer(1));

        System.out.println("A - Setting config to: " + config);
        api.createUpdateBillingProcessConfiguration(config);
        */

        // user for tests
        UserWS user = com.sapienter.jbilling.server.user.WSTest.createUser(true, null, null);
        OrderWS order = com.sapienter.jbilling.server.order.WSTest.createMockOrder(user.getUserId(),
                                                                                   1,
                                                                                   new BigDecimal(60));
        order.setActiveSince(new DateMidnight(2000, 11, 30).toDate());
        order.setBillingTypeId(Constants.ORDER_BILLING_PRE_PAID);
        order.setPeriod(2); // monthly

        Integer orderId = api.createUpdateOrder(order);
        System.out.println("Order id: " + orderId);

        // run the billing process. It should only get this order
        // Date billingDate = new DateMidnight(2000, 12, 1).toDate();
        //api.triggerBilling(billingDate);
        api.createInvoice(user.getUserId(), false);

        System.out.println("User id: " + user.getUserId());

        Integer[] invoiceIds = api.getAllInvoices(user.getUserId());
        System.out.println("Invoice ids: " + Arrays.toString(invoiceIds));


        InvoiceWS invoice = api.getInvoiceWS(invoiceIds[0]);
        System.out.println("TODO: check and write assert. Review invoice: " + invoice);

//        assertEquals("New invoice should be 1 day and one month",
//                     new BigDecimal(62),
//                     invoice.getBalanceAsDecimal());

        // clean up
        api.deleteInvoice(invoice.getId());
        api.deleteOrder(orderId);
        api.deleteUser(user.getUserId());
    }

    @Test
    public void test002ViewLimit() throws Exception {
        System.out.println("#test002ViewLimit");

        // set the configuration to something we are sure about
        
        BillingProcessConfigurationWS configBackup = api.getBillingProcessConfiguration();
        BillingProcessConfigurationWS config = api.getBillingProcessConfiguration();
        
        // just change it to a day
        config.setMaximumPeriods(100);

        System.out.println("A - Setting config to: " + config);
        api.createUpdateBillingProcessConfiguration(config);
        

        // user for tests
        // user is invoices Monthly on 1 
        UserWS user = com.sapienter.jbilling.server.user.WSTest.createUser(true, null, null);
        OrderWS order = com.sapienter.jbilling.server.order.WSTest.createMockOrder(user.getUserId(),
                                                                                   1,
                                                                                   new BigDecimal(60));
        // active since a little bit more than a month than the current billing process
        // When calling 'createInvoice' the billing process date is set to today, but the period is
        // taken from the configuration (very odd, almost a bug. To fix it, add a parameter to 'createInvoice' with the date.
        // if null, use today).
        order.setActiveSince(new DateMidnight(new Date()).withDayOfMonth(1).minus(10).toDate());
        order.setBillingTypeId(Constants.ORDER_BILLING_PRE_PAID);
        order.setPeriod(2); // monthly

        Integer orderId = api.createUpdateOrder(order);
        System.out.println("Order id: " + orderId);

        // run the billing process. For this user only
        Integer invoiceIds[] = api.createInvoice(user.getUserId(), false);

        System.out.println("Invoice ids: " + Arrays.toString(invoiceIds));
        InvoiceWS invoice = api.getInvoiceWS(invoiceIds[0]);
        
        // customer is invoices on 1 of the month and its evaluation period is 1 month
        assertEquals("New invoice should be 2 months, for a total of 120",
                     new BigDecimal(120),
                     invoice.getBalanceAsDecimal());

        // clean up
        api.deleteInvoice(invoice.getId());
        api.deleteOrder(orderId);
        api.deleteUser(user.getUserId());
        api.createUpdateBillingProcessConfiguration(configBackup);
    }
    
    @Test
    public void test003Retry() throws Exception {
        System.out.println("#test003Retry");

        // set the configuration to something we are sure about
        BillingProcessConfigurationWS config = api.getBillingProcessConfiguration();

        config.setNextRunDate(runDate);
        config.setRetries(new Integer(1));
        config.setDaysForRetry(new Integer(5));
        config.setGenerateReport(new Integer(0));
        config.setAutoPayment(new Integer(1));
        config.setAutoPaymentApplication(new Integer(1));
        config.setDfFm(new Integer(0));
        config.setDueDateUnitId(Constants.PERIOD_UNIT_MONTH);
        config.setDueDateValue(new Integer(1));
        config.setInvoiceDateProcess(new Integer(1));
        config.setMaximumPeriods(new Integer(10));
        config.setOnlyRecurring(new Integer(1));
        System.out.println("B - Setting config to: " + config);
        api.createUpdateBillingProcessConfiguration(config);

        // retries calculate dates using the real date of the run
        // when know of one from the pre-cooked DB
        cal.set(2000, GregorianCalendar.DECEMBER, 19, 0, 0, 0);
        Date retryDate = Util.truncateDate(cal.getTime());

        // let's monitor invoice 45, which is the one to be retried
        // belongs to gandalf (user id 2)
        InvoiceWS invoice = api.getInvoiceWS(45);

        assertEquals("Invoice without payments before retry", 0, invoice.getPaymentAttempts().intValue());
        assertEquals("Invoice without payments before retry - 2", 0, invoice.getPayments().length);

        // get the involved process
        BillingProcessWS billingProcess = api.getBillingProcess(2);

        // run trigger
        api.triggerBilling(retryDate);

        // get the process again
        BillingProcessWS billingProcess2 = api.getBillingProcess(2);
        assertEquals("18 - No retries", 1, billingProcess2.getProcessRuns().size());

        // run trigger 5 days later
        cal.add(GregorianCalendar.DAY_OF_YEAR, 5);
        api.triggerBilling(cal.getTime());

        // get the process again
        // now a retry should be there
        BillingProcessWS billingProcess3 = api.getBillingProcess(2);
        assertEquals("19 - First retry", 2, billingProcess3.getProcessRuns().size());

        // run trigger 10 days later
        cal.setTime(retryDate);
        cal.add(GregorianCalendar.DAY_OF_YEAR, 10);
        api.triggerBilling(cal.getTime());

        // get the process again
        BillingProcessWS billingProcess4 = api.getBillingProcess(2);
        assertEquals("21 - No new retry", 2, billingProcess4.getProcessRuns().size());

        // wait for the asynchronous payment processing to finish
        Thread.sleep(3000);

        // let's monitor invoice 45, which is the one to be retried
        invoice = api.getInvoiceWS(45);

        System.out.println("TODO: add some asserts about the payments and payment attempts on this invoice " + invoice);
        //assertEquals("Invoice without payments after retry", 1, invoice.getPaymentAttempts().intValue());
        //assertEquals("Invoice without payments after retry - 2", 1, invoice.getPayments().length);

        // the billing process has to have a total paid equal to the invoice
        BillingProcessWS process = api.getBillingProcess(2);
        ProcessRunWS run = process.getProcessRuns().get(process.getProcessRuns().size() - 1);
        ProcessRunTotalWS total = run.getProcessRunTotals().get(0);

        assertEquals("Retry total paid equals to invoice total", invoice.getTotalAsDecimal(), total.getTotalPaidAsDecimal());
    }

    @Test
    public void test004Run() {
        System.out.println("#test004Run");
        try {
            // get the latest process
            Integer processId = api.getLastBillingProcess();
            BillingProcessWS process = api.getBillingProcess(processId);

            // run trigger but too early
            cal.set(2005, GregorianCalendar.JANUARY, 26);
            api.triggerBilling(cal.getTime());

            // get the latest process (after triggered run)
            Integer processId2 = api.getLastBillingProcess();
            BillingProcessWS process2 = api.getBillingProcess(processId2);

            // no new process should have run
            assertEquals("No new process run", process.getId(), process2.getId());

            // no retry should have run
            assertEquals("No new process run (retries)",
                         process.getProcessRuns().size(),
                         process2.getProcessRuns().size());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception:" + e);
        }
    }

    @Test
    public void test005Review() throws Exception {
        System.out.println("#test005Review");
        
        enableProRateOrderPeriodTask(api);

        // get the latest process
        Integer abid = api.getLastBillingProcess();
        BillingProcessWS lastDto = api.getBillingProcess(abid);

        // get the review
        BillingProcessWS reviewDto = api.getReviewBillingProcess();

        // not review should be there
        System.out.println("TODO: should there be already a review?");
        // assertNotNull("3 - The test DB should have one review", reviewDto);

        // set the configuration to something we are sure about
        BillingProcessConfigurationWS config = api.getBillingProcessConfiguration();
        config.setDaysForReport(new Integer(5));
        config.setGenerateReport(new Integer(1));
        System.out.println("C - Setting config to: " + config);
        api.createUpdateBillingProcessConfiguration(config);

        // disapprove the review (that just run before this one)
        api.setReviewApproval(false);

        // run trigger, this time it should run and generate a report
        Thread reviewThread = new Thread() {
            @Override
            public void run() {
                api.triggerBilling(runDate);
            }
        };

        reviewThread.start();

        // trying immediatelly after, should not run
        Thread.sleep(1000); // take it easy
        assertFalse("It should not run, a review is running already", api.triggerBilling(runDate));

        // now wait until that thread is done
        while (reviewThread.isAlive()) {
            Thread.sleep(1000); // take it easy
        }

        // get the latest process
        BillingProcessWS lastDtoB = api.getBillingProcess(api.getLastBillingProcess());

        // no new process should have run
        assertEquals("4 - No new process", lastDto.getId(), lastDtoB.getId());

        // get the review
        // now review should be there
        reviewDto = api.getReviewBillingProcess();
        assertNotNull("5 - Review should be there", reviewDto);

        // the review should have invoices
        // todo: Billing process WS grand totals
//            assertTrue("6 - Invoices in review", reviewDto.getGrandTotal().getInvoicesGenerated() > 0);

        // validate that the review generated an invoice for user 121
        System.out.println("Validating invoice delegation");

        Integer[] invoiceIds = api.getAllInvoices(121);
        assertEquals("User 121 should have two invoices", 2, invoiceIds.length);

        InvoiceWS invoice = getReviewInvoice(invoiceIds);

        assertNotNull("Review invoice present", invoice);
        assertEquals("Review invoice has to be total 448.55",
                     new BigDecimal("448.55"),
                     invoice.getTotalAsDecimal());
        assertNull("Review invoice not delegated", invoice.getDelegatedInvoiceId());

        Integer reviewInvoiceId = invoice.getId();

        invoice = getNonReviewInvoice(invoiceIds);

        assertNull("Overdue invoice not delegated", invoice.getDelegatedInvoiceId());
        assertEquals("Overdue invoice should remain 'unpaid', since this is only a review",
                     Constants.INVOICE_STATUS_UNPAID,
                     invoice.getStatusId());

        assertEquals("Overdue invoice balance 15", new BigDecimal("15.0"), invoice.getBalanceAsDecimal());
        Integer overdueInvoiceId = invoice.getId();

        // validate that the review left the order 107600 is still active
        // This is a pro-rated order with only a fraction of a period to
        // invoice.

        OrderWS proRatedOrder = api.getOrder(107600);
        assertEquals("Pro-rate order should remain active",
                     Constants.ORDER_STATUS_ACTIVE,
                     proRatedOrder.getStatusId());

        // disapprove the review
        api.setReviewApproval(false);

        invoiceIds = api.getAllInvoices(121);
        invoice = getNonReviewInvoice(invoiceIds);

        assertNotNull("Overdue invoice still there", invoice);
        assertEquals("Overdue invoice should remain 'unpaid', after disapproval",
                     Constants.INVOICE_STATUS_UNPAID,
                     invoice.getStatusId());

        assertEquals("Overdue invoice balance 15", new BigDecimal("15.0"), invoice.getBalanceAsDecimal());

        // run trigger, but too early (six days, instead of 5)
        cal.set(2006, GregorianCalendar.OCTOBER, 20);
        api.triggerBilling(cal.getTime());

        // get the latest process
        // no new process should have run
        lastDtoB = api.getBillingProcess(api.getLastBillingProcess());
        assertEquals("7 - No new process, too early", lastDto.getId(), lastDtoB.getId());

        // get the review
        BillingProcessWS reviewDto2 = api.getReviewBillingProcess();
        assertEquals("8 - No new review run", reviewDto.getId(), reviewDto2.getId());

        // status of the review should still be disapproved
        config = api.getBillingProcessConfiguration();
        assertEquals("9 - Review still disapproved",
                     config.getReviewStatus(),
                     Constants.REVIEW_STATUS_DISAPPROVED.intValue());

        // run trigger this time has to generate a review report
        cal.set(2006, GregorianCalendar.OCTOBER, 22);
        api.triggerBilling(cal.getTime());

        invoice = api.getInvoiceWS(overdueInvoiceId);

        assertNotNull("Overdue invoice still there", invoice);
        assertEquals("Overdue invoice should remain 'unpaid', after disapproval",
                     Constants.INVOICE_STATUS_UNPAID,
                     invoice.getStatusId());

        assertEquals("Overdue invoice balance 15", new BigDecimal("15.0"), invoice.getBalanceAsDecimal());

        try {
            invoice = api.getInvoiceWS(reviewInvoiceId);
            fail("Invoice does not exist, should throw a Hibernate exception.");
        } catch (Exception e) {
        }

        // get the latest process
        // no new process should have run
        lastDtoB = api.getBillingProcess(api.getLastBillingProcess());
        assertEquals("10 - No new process, review disapproved", lastDto.getId(), lastDtoB.getId());

        // get the review
        // since the last one was disapproved, a new one has to be created
        reviewDto2 = api.getReviewBillingProcess();
        assertNotSame("11 - New review run", reviewDto.getId(), reviewDto2.getId());

        // status of the review should now be generated
        config = api.getBillingProcessConfiguration();
        assertEquals("12 - Review generated",
                     config.getReviewStatus(),
                     Constants.REVIEW_STATUS_GENERATED.intValue());

        // run trigger, date is good, but the review is not approved
        cal.set(2006, GregorianCalendar.OCTOBER, 22);
        api.triggerBilling(cal.getTime());

        // get the review
        // the status is generated, so it should not be a new review
        reviewDto = api.getReviewBillingProcess();
        assertEquals("13 - No new review run", reviewDto.getId(), reviewDto2.getId());

        // run trigger report still not approved, no process then
        cal.set(2006, GregorianCalendar.OCTOBER, 22);
        api.triggerBilling(cal.getTime());

        // get the latest process
        // no new process should have run
        lastDtoB = api.getBillingProcess(api.getLastBillingProcess());
        assertEquals("14 - No new process, review not yet approved", lastDto.getId(), lastDtoB.getId());

        // disapprove the review so it should run again
        api.setReviewApproval(false);


        //
        //  Run the review and approve it to allow the process to run
        //
        cal.clear();
        cal.set(2006, GregorianCalendar.OCTOBER, 26);
        cal.add(GregorianCalendar.DATE, -4);
        api.triggerBilling(cal.getTime());

        // get the review
        // since the last one was disapproved, a new one has to be created
        reviewDto2 = api.getReviewBillingProcess();
        assertFalse("14.2 - New review run", reviewDto.getId().equals(reviewDto2.getId()));

        // finally, approve the review. The billing process is next
        api.setReviewApproval(true);
        
        enableBasicOrderPeriodTask(api);
    }

    @Test
    public void test006BillingProcessStatus() throws Exception {
        System.out.println("#test006BillingProcessStatus");

        // no active processes now, all calls was sync
        assertFalse("No active billing processes now!", api.isBillingProcessRunning());

        ProcessStatusWS completedStatus = api.getBillingProcessStatus();
        assertNotNull("Status should be retrieved", completedStatus);
        assertNotNull("Start date should be filled", completedStatus.getStart());
        assertNotNull("End date should be filled", completedStatus.getEnd());
        assertEquals("Process status should be FINISHED", ProcessStatusWS.State.FINISHED, completedStatus.getState());
    }

    @Test
    public void test007Process() throws Exception {
        System.out.println("#test007Process");
        
        enableProRateOrderPeriodTask(api);

        // get the latest process
        BillingProcessWS lastDto = api.getBillingProcess(api.getLastBillingProcess());

        // get the review, so we can later check that what id had
        // is the same that is generated in the real process
        BillingProcessWS reviewDto = api.getReviewBillingProcess();

        // check that the next billing date is updated
        BillingProcessConfigurationWS config = api.getBillingProcessConfiguration();
        assertEquals("14.9 - Next billing date starting point",
                     new Date(2006 - 1900, 10 - 1, 26),
                     config.getNextRunDate());

        // run trigger on the run date
        api.triggerBillingAsync(runDate);

        // continually check the process status until the API says that the billing process is no longer running.
        ProcessStatusWS runningStatus = null;
        while (api.isBillingProcessRunning()) {
            runningStatus = api.getBillingProcessStatus();
            Thread.sleep(5000);
        }

        // validate the process status that was recorded while the billing process was running
        System.out.println("found running status for process: " + runningStatus);

        assertNotNull("Process had a running status", runningStatus);
        assertEquals("Status should be RUNNING", ProcessStatusWS.State.RUNNING, runningStatus.getState());
        assertNotNull("Status should have start date", runningStatus.getStart());
        assertNull("Status end date should be empty while running", runningStatus.getEnd());

        // validate process status after the billing process finished
        ProcessStatusWS completedStatus = api.getBillingProcessStatus();
        System.out.println("completed status for process: " + completedStatus);

        assertNotNull("Process had a status upon completion", completedStatus);
        assertEquals("Status should be FINISHED", ProcessStatusWS.State.FINISHED, completedStatus.getState());
        assertNotNull("Status should have start date", completedStatus.getStart());
        assertNotNull("Status should have end date", completedStatus.getEnd());

        // validate invoice delegation
        InvoiceWS invoice = api.getInvoiceWS(8500);
        assertNotNull("Overdue invoice still there", invoice);
        assertEquals("Overdue invoice is not 'paid'", 0, invoice.getToProcess().intValue());
        assertEquals("Overdue invoice is now 'carried over'",
                     Constants.INVOICE_STATUS_UNPAID_AND_CARRIED,
                     invoice.getStatusId());

        assertEquals("Overdue invoice balance remains the same",
                     new BigDecimal("15.0"),
                     invoice.getBalanceAsDecimal());

        assertNotNull("Overdue invoice is now delegated", invoice.getDelegatedInvoiceId());

        // get the latest process
        // this is the one and only new process run
        BillingProcessWS lastDtoB = api.getBillingProcess(api.getLastBillingProcess());
        assertFalse("15 - New Process", lastDto.getId().equals(lastDtoB.getId()));

        // initially, runs should be 1
        assertEquals("16 - Only one run", 1, lastDtoB.getProcessRuns().size());

        // check that the next billing date is updated
        config = api.getBillingProcessConfiguration();
        assertEquals("17 - Next billing date for a month later",
                     new Date(2006 - 1900, 11 - 1, 25),
                     config.getNextRunDate());

        // verify that what just have run, is the same that was displayed
        // in the review
        // todo: Billing process WS grand totals
//            assertEquals("17.1 - Review invoices = Process invoices",
//                         reviewDto.getGrandTotal().getInvoicesGenerated().intValue(),
//                         lastDtoB.getGrandTotal().getInvoicesGenerated().intValue());
//
//            BillingProcessRunTotalDTOEx aTotal = (BillingProcessRunTotalDTOEx)
//                    reviewDto.getGrandTotal().getTotals().get(0);
//            BillingProcessRunTotalDTOEx bTotal = (BillingProcessRunTotalDTOEx)
//                    lastDtoB.getGrandTotal().getTotals().get(0);
//            assertEquals("17.2 - Review invoiced = Process invoiced",
//                         aTotal.getTotalInvoiced(),
//                         bTotal.getTotalInvoiced());
        
        // verify that the transition from pending unsubscription to unsubscribed worked
        Integer userId = api.getUserId("pendunsus1");
        UserWS user = api.getUserWS(userId);

        assertEquals("User should stay on pending unsubscription",
                     UserDTOEx.SUBSCRIBER_PENDING_UNSUBSCRIPTION,
                     user.getSubscriberStatusId());

        userId = api.getUserId("pendunsus2");
        user = api.getUserWS(userId);

        assertEquals("User should have changed to unsubscribed",
                     UserDTOEx.SUBSCRIBER_UNSUBSCRIBED,
                     user.getSubscriberStatusId());
        
        enableBasicOrderPeriodTask(api);
    }

    @Test
    public void test008GeneratedInvoices() throws Exception {
        System.out.println("#test008GeneratedInvoices");

        List<Integer> invoiceIds = api.getBillingProcessGeneratedInvoices(api.getLastBillingProcess());
        assertEquals("Invoices generated", 13, invoiceIds.size());

        // validate each invoice and check that the invoiced total matches the
        // sum of the comprising order totals.
        for (Integer id : invoiceIds) {
            InvoiceWS invoice = api.getInvoiceWS(id);

            // calculate the total value from the source orders
            BigDecimal orderTotal = BigDecimal.ZERO;

            for (OrderProcessWS orderProcess : api.getOrderProcessesByInvoice(id)) {
                OrderWS orderDto = api.getOrder(orderProcess.getOrderId());
                orderTotal = orderTotal.add(orderDto.getTotalAsDecimal());

                // validate the invoice total for non pro-rated invoices
                if (orderProcess.getOrderId() == 102) {
                    BigDecimal invoiceTotal = invoice.getTotalAsDecimal().subtract(invoice.getCarriedBalanceAsDecimal());
                    assertEquals("sum of orders does not equal total for invoice " + invoice.getId()
                                 + " (total: " + invoice.getTotal() + ", carried: " + invoice.getCarriedBalance() + ")",
                                 orderTotal, invoiceTotal);
                }
            }

        }

        assertTrue("invoice should not be generated", api.getAllInvoices(1067).length == 0);
    }

    @Test
    public void test009PeriodsBilled() throws Exception {
        System.out.println("#test009PeriodsBilled");
        
        String dateRanges[][] = {
                {"2006-10-26", "2006-11-01", "1"}, // 100
                {"2006-10-01", "2006-11-01", "1"}, // 102
                {"2006-10-16", "2006-11-01", "1"}, // 103
                {"2006-10-15", "2006-11-01", "1"}, // 104
                {"2006-09-05", "2006-11-01", "2"}, // 105
                {"2006-09-03", "2006-11-01", "2"}, // 106
                {"2006-09-30", "2006-10-29", "2"}, // 107
                {"2006-08-10", "2006-10-20", "3"} // 108
        };

        int orders[] = {100, 102, 103, 104, 105, 106, 107, 108};


        // get the latest process
        BillingProcessWS lastDto = api.getBillingProcess(api.getLastBillingProcess());

        for (int f = 0; f < orders.length; f++) {
            OrderWS order = api.getOrder(orders[f]);
            Date from = parseDate(dateRanges[f][0]);
            Date to = parseDate(dateRanges[f][1]);
            Integer number = Integer.valueOf(dateRanges[f][2]);

            List<OrderProcessWS> processes = api.getOrderProcesses(order.getId());

            // find first non-review process
            OrderProcessWS period = null;
            for (OrderProcessWS process : processes) {
                if (process.getReview() == 0) {
                    period = process;
                    break;
                }
            }

            assertEquals("(from) Order " + order.getId(), from, period.getPeriodStart());
            assertEquals("(to) Order " + order.getId(), to, period.getPeriodEnd());
            assertEquals("(number) Order " + order.getId(), number, period.getPeriodsIncluded());

            // order has been processed
            OrderProcessWS process = processes.get(0);
            assertEquals("(process) Order " + order.getId(),
                         lastDto.getId(),
                         process.getBillingProcessId());
        }
    }

    @Test
    public void test010NextBillingProcess() throws Exception {
        System.out.println("#test010NextBillingProcess");
    	
        // re-run the billing process
        System.out.println("Running the billing process again");
        enableProRateOrderPeriodTask(api);
        BillingProcessConfigurationWS config = api.getBillingProcessConfiguration();
        api.triggerBilling(config.getNextRunDate());
        
        // get the review
        // now review should be there
        BillingProcessWS reviewDto = api.getReviewBillingProcess();
        assertNotNull("Review should be there", reviewDto);
        
        api.setReviewApproval(true);
        
        // run trigger on the run date
        api.triggerBillingAsync(config.getNextRunDate());

        // continually check the process status until the API says that the billing process is no longer running.
        ProcessStatusWS runningStatus = null;
        while (api.isBillingProcessRunning()) {
            runningStatus = api.getBillingProcessStatus();
            Thread.sleep(5000);
        }
        
        assertNotNull("Process had a running status", runningStatus);
        assertEquals("Status should be RUNNING", ProcessStatusWS.State.RUNNING, runningStatus.getState());
        assertNotNull("Status should have start date", runningStatus.getStart());
        assertNull("Status end date should be empty while running", runningStatus.getEnd());
        
        // validate process status after the billing process finished
        ProcessStatusWS completedStatus = api.getBillingProcessStatus();
        System.out.println("completed status for process: " + completedStatus);

        assertNotNull("Process had a status upon completion", completedStatus);
        assertEquals("Status should be FINISHED", ProcessStatusWS.State.FINISHED, completedStatus.getState());
        assertNotNull("Status should have start date", completedStatus.getStart());
        assertNotNull("Status should have end date", completedStatus.getEnd());
        
        config = api.getBillingProcessConfiguration();
        
        assertEquals("17 - Next billing date for a month later",
                new Date(2006 - 1900, 12 - 1, 25),
                config.getNextRunDate());
        
        List<Integer> invoiceIds = api.getBillingProcessGeneratedInvoices(api.getLastBillingProcess());
        assertEquals("Invoices generated", 1000, invoiceIds.size());
        
        assertTrue("invoice should be generated", api.getAllInvoices(1067).length != 0);
        
        enableBasicOrderPeriodTask(api);
    	
    }

    @Test
    public void test011NextPeriodsBilled() throws Exception {
        System.out.println("#test011NextPeriodsBilled");
        
        // postpaid orders
        String dateRanges[][] = {
                {"2006-10-25", "2006-12-01", "2"}, // 110
                {"2006-10-15", "2006-12-01", "2"}, // 112
                {"2006-10-15", "2006-11-05", "2"} // 113
        };

        int orders[] = {110, 112, 113};


        // get the latest process
        BillingProcessWS lastDto = api.getBillingProcess(api.getLastBillingProcess());

        for (int f = 0; f < orders.length; f++) {
            OrderWS order = api.getOrder(orders[f]);
            Date from = parseDate(dateRanges[f][0]);
            Date to = parseDate(dateRanges[f][1]);
            Integer number = Integer.valueOf(dateRanges[f][2]);

            List<OrderProcessWS> processes = api.getOrderProcesses(order.getId());

            OrderProcessWS period = null;
            for (OrderProcessWS process : processes) {
                if (process.getReview() == 0) {
                    period = process;
                    break;
                }
            }

            assertEquals("(from) Order " + order.getId(), from, period.getPeriodStart());
            assertEquals("(to) Order " + order.getId(), to, period.getPeriodEnd());
            assertEquals("(number) Order " + order.getId(), number, period.getPeriodsIncluded());

            // order has been processed
            OrderProcessWS process = processes.get(0);
            assertEquals("(process) Order " + order.getId(),
                         lastDto.getId(),
                         process.getBillingProcessId());
        }
    }

    @Test
    public void test012Payments() {
        System.out.println("#test012Payments");
        try {
            BillingProcessWS process = api.getBillingProcess(api.getLastBillingProcess());
            assertNotNull("The process should be there", process);
            assertNotNull("The run should be there", process.getProcessRuns());
            assertEquals("Only one run should be present", 1, process.getProcessRuns().size());

            ProcessRunWS run = process.getProcessRuns().get(0);

            for (int myTry = 0; myTry < 10 && run.getPaymentFinished() == null; myTry++) {
                System.out.println("Waiting for payment processing ... " + myTry);
                Thread.sleep(1000);

                process = api.getBillingProcess(api.getLastBillingProcess());
                run = process.getProcessRuns().get(0);
            }

            assertNotNull("The payment processing did not run", run.getPaymentFinished());

            // we know that the only one invoice will be payed in full
            // todo: Billing process WS grand totals
//            assertEquals("Invoices in the grand total",
//                         new Integer(998),
//                         process.getGrandTotal().getInvoicesGenerated());
//
//            assertTrue("Total invoiced is consitent",
//                       ((BillingProcessRunTotalDTOEx) process.getGrandTotal().getTotals().get(0)).getTotalInvoiced()
//                               .subtract(((BillingProcessRunTotalDTOEx) process.getGrandTotal()
//                                       .getTotals()
//                                       .get(0)).getTotalPaid())
//                               .subtract(((BillingProcessRunTotalDTOEx) process.getGrandTotal()
//                                       .getTotals()
//                                       .get(0)).getTotalNotPaid())
//                               .abs()
//                               .floatValue() < 1);

            Integer[] invoiceIds = api.getAllInvoices(1067);
            assertTrue("invoice found", invoiceIds.length != 0);

            InvoiceWS invoice = api.getInvoiceWS(invoiceIds[0]);
            assertEquals("Invoice is paid", new Integer(0), invoice.getToProcess());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception:" + e);
        }
    }

    /*
     * VALIDATE ORDERS
     */

    @Test
    public void test013OrdersProcessedDate() {
        System.out.println("#test013OrdersProcessedDate");

        String dates[] = {
                "2006-12-01", "2006-12-01", null,   // 100 - 102
                "2006-12-01", null, null,   // 103 - 105
                "2006-12-01", null, null,   // 106 - 108
                null, "2006-12-01", "2006-12-01",   // 109 - 111
                "2006-11-15", null,    // 112 - 113
        };

        try {
            for (int f = 100; f < dates.length; f++) {
                OrderWS order = api.getOrder(f);

                if (order.getNextBillableDay() != null) {
                    if (dates[f] == null) {
                        assertNull("Order " + order.getId(), order.getNextBillableDay());
                    } else {
                        assertEquals("Order " + order.getId(), parseDate(dates[f]), order.getNextBillableDay());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception:" + e);
        }
    }

    @Test
    public void test014OrdersFlaggedOut() {
        System.out.println("#test014OrdersFlaggedOut");

        int orders[] = {102, 104, 105, 107, 108, 109, 113};

        try {
            for (int f = 0; f < orders.length; f++) {
                OrderWS order = api.getOrder(orders[f]);
                assertEquals("Order " + order.getId(), order.getStatusId(), Constants.ORDER_STATUS_FINISHED);
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception:" + e);
        }
    }

    @Test
    public void test015OrdersStillIn() {
        System.out.println("#test015OrdersStillIn");

        int orders[] = {100, 101, 103, 106, 110, 111, 112};

        try {
            for (int f = 0; f < orders.length; f++) {
                OrderWS order = api.getOrder(orders[f]);
                assertEquals("Order " + order.getId(), order.getStatusId(), Constants.ORDER_STATUS_ACTIVE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception:" + e);
        }
    }

    @Test
    public void test016Excluded() {
        System.out.println("#test016Excluded");
        int orders[] = {109};
        try {
            for (int f = 0; f < orders.length; f++) {
                OrderWS order = api.getOrder(orders[f]);
                List<OrderProcessWS> processes = api.getOrderProcesses(order.getId());

                assertTrue("Order not processed", processes.isEmpty());
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception:" + e);
        }
    }

    /**
     * Test that the BillingProcess will fail with status "Finished: failed" if an exception occurs and that resolving
     * the failure allows the process to complete successfully in a later run.
     *
     * @throws Exception testing
     */
    @Test
    public void test017BillingProcessFailure() throws Exception {
        System.out.println("#test017BillingProcessFailure");

        // order period aligned with the 13th
        Date runDate = new DateMidnight(2007, 12, 13).toDate();

        // create testing user and order
        UserWS user = com.sapienter.jbilling.server.user.WSTest
                .createUser(true, null, null);

        OrderWS brokenOrder = com.sapienter.jbilling.server.order.WSTest
                .createMockOrder(user.getUserId(), 1, new BigDecimal(10));

        brokenOrder.setActiveSince(runDate);
        brokenOrder.setPeriod(2);        // monthly
        brokenOrder.setBillingTypeId(9); // invalid billing type id to trigger failure

        Integer orderId = api.createUpdateOrder(brokenOrder);
        System.out.println("Order id: " + orderId);

        // set the configuration to include the corrupt order
        BillingProcessConfigurationWS config = api.getBillingProcessConfiguration();
        config.setNextRunDate(runDate);
        config.setRetries(1);
        config.setDaysForRetry(5);
        config.setGenerateReport(0);
        config.setAutoPayment(1);
        config.setAutoPaymentApplication(1);
        config.setDfFm(0);
        config.setDueDateUnitId(Constants.PERIOD_UNIT_MONTH);
        config.setDueDateValue(1);
        config.setInvoiceDateProcess(1);
        config.setMaximumPeriods(10);
        config.setOnlyRecurring(1);

        // trigger billing
        // process should finish with status "failed" because of the corrupt order
        System.out.println("D - Setting config to: " + config);
        api.createUpdateBillingProcessConfiguration(config);
        System.out.println("Running proces for : " + runDate);
        api.triggerBilling(runDate);

        Integer billingProcessId = api.getLastBillingProcess();
        BillingProcessWS billingProcess = api.getBillingProcess(billingProcessId);
        ProcessRunWS run = billingProcess.getProcessRuns().get(0);

        assertEquals("Last billing process run should have failed.", "Finished: failed", run.getStatusStr());

        // fix the order by setting billing type ID to a proper value
        OrderWS fixedOrder = api.getOrder(orderId);
        fixedOrder.setBillingTypeId(Constants.ORDER_BILLING_POST_PAID);
        api.updateOrder(fixedOrder);

        // reset the configuration and retry
        // process should finish with status "successful"
        System.out.println("E - Setting config to: " + config);
        api.createUpdateBillingProcessConfiguration(config);
        System.out.println("Running proces for : " + runDate);
        api.triggerBilling(runDate);

        billingProcessId = api.getLastBillingProcess();
        billingProcess = api.getBillingProcess(billingProcessId);
        run = billingProcess.getProcessRuns().get(0);

        assertEquals("Last billing process run should have passed.", "Finished: successful", run.getStatusStr());

        // cleanup
        api.deleteOrder(orderId);
        api.deleteUser(user.getUserId());
    }

    private static Date parseDate(String str) throws Exception {
        if (str == null) {
            return null;
        }

        if (str.length() != 10 || str.charAt(4) != '-' || str.charAt(7) != '-') {
            throw new Exception("Can't parse " + str);
        }

        try {
            int year = Integer.valueOf(str.substring(0, 4)).intValue();
            int month = Integer.valueOf(str.substring(5, 7)).intValue();
            int day = Integer.valueOf(str.substring(8, 10)).intValue();

            Calendar cal = GregorianCalendar.getInstance();
            cal.clear();
            cal.set(year, month - 1, day);

            return cal.getTime();
        } catch (Exception e) {
            throw new Exception("Can't parse " + str);
        }
    }

    private InvoiceWS getReviewInvoice(Integer[] invoiceIds) {
        for (Integer id : invoiceIds) {
            InvoiceWS invoice = api.getInvoiceWS(id);
            if (invoice != null && invoice.getIsReview() == 1)
                return invoice;
        }
        return null;
    }

    private InvoiceWS getNonReviewInvoice(Integer[] invoiceIds) {
        for (Integer id : invoiceIds) {
            InvoiceWS invoice = api.getInvoiceWS(id);
            if (invoice != null && invoice.getIsReview() == 0)
                return invoice;
        }
        return null;
    }
    
    private void enableBasicOrderPeriodTask(JbillingAPI api) {
    	PluggableTaskWS plugin = api.getPluginWS(ORDER_PERIOD_PLUGIN_ID);
    	plugin.setTypeId(BASIC_ORDER_PERIOD_PLUGIN_TYPE_ID);

    	api.updatePlugin(plugin);
    }

    private void enableProRateOrderPeriodTask(JbillingAPI api) {
    	PluggableTaskWS plugin = api.getPluginWS(ORDER_PERIOD_PLUGIN_ID);
    	plugin.setTypeId(PRO_RATE_ORDER_PERIOD_PLUGIN_TYPE_ID);

    	api.updatePlugin(plugin);
    }
}
