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

package com.sapienter.jbilling.server.mediation;

import com.sapienter.jbilling.common.Util;
import com.sapienter.jbilling.server.item.PricingField;
import com.sapienter.jbilling.server.mediation.task.SaveToJDBCMediationErrorHandler;
import com.sapienter.jbilling.server.order.OrderLineWS;
import com.sapienter.jbilling.server.order.OrderWS;
import com.sapienter.jbilling.server.process.ProcessStatusWS;
import com.sapienter.jbilling.server.util.Constants;
import com.sapienter.jbilling.server.util.api.JbillingAPI;
import com.sapienter.jbilling.server.util.api.JbillingAPIFactory;
import junit.framework.TestCase;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static com.sapienter.jbilling.test.Asserts.*;
import static org.testng.AssertJUnit.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Test(groups = { "integration", "mediation" })
public class MediationTest {

    private final static Integer ASTERISK_TEST_CFG_ID = 10;
    private final static Integer JDBC_READER_HIPERSONIC_CDR_CFG_ID = 20;
    private final static Integer JDBC_READER_JBILLING_DB_CFG_ID = 30;

    private JbillingAPI api;

    @BeforeClass
    protected void setUp() throws Exception {
        api = JbillingAPIFactory.getAPI();
    }

    @Test
    public void test00TriggerAndCheckStatus() throws Exception {
        System.out.println("testTrigger");

        Integer processId = api.triggerMediationByConfiguration(ASTERISK_TEST_CFG_ID);
        boolean isProcessing = api.isMediationProcessRunning();
        ProcessStatusWS status = api.getMediationProcessStatus();
        Integer repeatProcessId = api.triggerMediationByConfiguration(ASTERISK_TEST_CFG_ID);

        // check previous calls
        assertNotNull("Mediation should be triggered at first time!", processId);
        // todo: possible fail if incorrect mediation configuration or 'quick' machines
        // in common case 2d-4d calls to api should be parall to processing first call
        assertTrue("In common case first process should be in running state. Try again on your machine or comment assert statement", isProcessing);
        assertNotNull("Status should be retrieved!", status);
        assertNotNull("Start date should be presented", status.getStart());
        assertNull("End date should be empty in common case", status.getEnd());
        assertEquals("Status should be RUNNING in common case", ProcessStatusWS.State.RUNNING, status.getState());
        assertNull("Second mediation process to the same cfg should not be runned (in common case)", repeatProcessId);

        Integer secondProcess = api.triggerMediationByConfiguration(JDBC_READER_HIPERSONIC_CDR_CFG_ID);
        assertNotNull("Another configuration for same entity should be triggered successfully", secondProcess);
        Integer thirdProcess = api.triggerMediationByConfiguration(JDBC_READER_JBILLING_DB_CFG_ID);
        assertNotNull("Another configuration for same entity should be triggered successfully");
        // now wait until finishing all mediation processes
        waitForMediationComplete(25 * 60 * 1000);  // todo: possible adjust interval

        ProcessStatusWS completedStatus = api.getMediationProcessStatus();
        assertNotNull("Status should be retrieved", completedStatus);
        if (completedStatus.getProcessId().equals(processId)) { // asterisk test
            assertEquals("Asterisk test has error records, status should be FAILED", ProcessStatusWS.State.FAILED, completedStatus.getState());
        } else if (completedStatus.getProcessId().equals(secondProcess) || completedStatus.getProcessId().equals(thirdProcess)) {
            List<MediationRecordWS> processedRecords = api.getMediationRecordsByMediationProcess(completedStatus.getProcessId());
            boolean hasErrors = false;
            for (MediationRecordWS record : processedRecords) {
                if (record.getRecordStatusId().equals(Constants.MEDIATION_RECORD_STATUS_ERROR_DECLARED)
                        || record.getRecordStatusId().equals(Constants.MEDIATION_RECORD_STATUS_ERROR_DETECTED)) {
                    hasErrors = true;
                    break;
                }
            }
            if (hasErrors) {
                assertEquals("Process status should be FAILED", ProcessStatusWS.State.FAILED, completedStatus.getState());
            } else {
                assertEquals("Process status should be FINISHED", ProcessStatusWS.State.FINISHED, completedStatus.getState());
            }
        }
        MediationProcessWS processWS = api.getMediationProcess(completedStatus.getProcessId());
        assertNotNull("Mediation process should be retrieved", processWS);
        assertEquals("Mediation process should be filled", processWS.getId(), completedStatus.getProcessId());
    }

    @Test
    public void test01Trigger() throws Exception {
        System.out.println("#test01Trigger");

        // 1 existing process (for duplicate testing), 3 new pricesses from trigger();
        List<MediationProcessWS> all = api.getAllMediationProcesses();
        assertNotNull("process list can't be null", all);
        assertEquals("There should be four processes after running the mediation process", 4, all.size());

        Collection <MediationRecordWS> processedRecords = null;
        for (MediationProcessWS process : all) {
            if (process.getConfigurationId().equals(ASTERISK_TEST_CFG_ID) && process.getRecordsAffected() > 0) {
                // total orders touched should equal the number of records processed minus errors & non billable
                // 10131 events - 2 errors - 1 non billable = 10128
                assertEquals("The process touches an order for each event", new Integer(10128), process.getRecordsAffected());
                System.out.println("Orders affected: " + process.getRecordsAffected());
                processedRecords = api.getMediationRecordsByMediationProcess(process.getId());
            }
        }

        assertNotNull("Collection of processed records should be presented", processedRecords);
        assertEquals("Should be processed ten records", 10134, processedRecords.size());

        Integer checkedRecords = 0;
        for (MediationRecordWS rec : processedRecords) {
            if (rec.getKey().equals("07")) {
                assertEquals("Record with key 07 should be done and billable",
                             (Integer) rec.getRecordStatusId(), Constants.MEDIATION_RECORD_STATUS_DONE_AND_BILLABLE);
                checkedRecords++;
            } else if (rec.getKey().equals("08")) {
                assertEquals("Record with key 08 should be done and not billable (not answered)",
                             (Integer) rec.getRecordStatusId(), Constants.MEDIATION_RECORD_STATUS_DONE_AND_NOT_BILLABLE);
                checkedRecords++;
            } else if (rec.getKey().equals("09")) {
                assertEquals("Record with key 09 should be declared as error (negative duration)",
                             (Integer) rec.getRecordStatusId(), Constants.MEDIATION_RECORD_STATUS_ERROR_DECLARED);
                checkedRecords++;
            } else if (rec.getKey().equals("10")) {
                assertEquals("Record with key 10 should be detected as error (undefined user)",
                             (Integer) rec.getRecordStatusId(), Constants.MEDIATION_RECORD_STATUS_ERROR_DETECTED);
                checkedRecords++;
            }
        }
        assertEquals("Records with keys 07, 08, 09 and 10 should be in results of processing", 4, checkedRecords.intValue());

        List allCfg = api.getAllMediationConfigurations();
        assertNotNull("config list can't be null", allCfg);
        assertEquals("There should be four configurations present", 4, allCfg.size());


        // validate calls for Gandalf (userid 2) - asterisk-sample1.csv
        // 5 valid calls
        System.out.println("Validating one-time orders...");
        JbillingAPI api = JbillingAPIFactory.getAPI();

        boolean foundFirst = false;
        boolean foundSecond = false;

        GregorianCalendar cal = new GregorianCalendar();
        cal.set(2007, GregorianCalendar.OCTOBER, 15);
        Date d1015 = cal.getTime();

        cal.set(2007, GregorianCalendar.NOVEMBER, 15);
        Date d1115 = cal.getTime();

        for (Integer orderId : api.getLastOrders(2, 100)) {
            OrderWS order = api.getOrder(orderId);

            if (order.getPeriod().equals(Constants.ORDER_PERIOD_ONCE)
                    && Util.equal(Util.truncateDate(order.getActiveSince()), Util.truncateDate(d1015))) {

                // asterisk-sample.csv
                // 4 calls for october, total 1300 minutes
                foundFirst = true;
                assertEquals("Quantity of should be the combiend of all events",
                             new BigDecimal("1300.0"),
                             order.getOrderLines()[0].getQuantityAsDecimal());
            }

            if (order.getPeriod().equals(Constants.ORDER_PERIOD_ONCE)
                    && Util.equal(Util.truncateDate(order.getActiveSince()), Util.truncateDate(d1115))) {

                // asterisk-sample1.csv
                // 1 call for november, total 300 minutes
                System.out.println("Order ID 1: " + order.getId());
                foundSecond = true;
                assertEquals("Quantity of second order should be 300 ",
                             new BigDecimal("300.0"),
                             order.getOrderLines()[0].getQuantityAsDecimal());
            }
        }

        assertTrue("The one time order for 10/15 is missing", foundFirst);
        assertTrue("The one time order for 11/15 is missing", foundSecond);


        // validate calls for testUserName-1189624498100 (userid 1055) - asterisk-sample1.csv
        // 2 valid calls, total 700 minutes
        OrderWS order = api.getLatestOrder(1055);
        BigDecimal total = BigDecimal.ZERO;
        for (OrderLineWS line : order.getOrderLines()) {
            total = total.add(line.getAmountAsDecimal());
        }

        // call to 512 rated at $7.00/minute
        // other calls at $0.00
        //      200 x 7.00 = 1400
        //      500 x 0.00 = 0
        //                 = $1400.00 totals
        assertEquals("Total of mixed price order", new BigDecimal("1400"), total);
    }

    // test that the last 2 orders for gandalf have all the CDRs
    @Test
    public void test02OrderLineEvents() {
        System.out.println("#test02OrderLineEvents");

        try {

            JbillingAPI api = JbillingAPIFactory.getAPI();
            Integer ids[] = api.getLastOrders(2, 2); // last two orders for user 2
            for (Integer id : ids) {
                OrderWS order = api.getOrder(id);
                List<MediationRecordLineWS> lines = api.getMediationEventsForOrder(order.getId());

                BigDecimal total = BigDecimal.ZERO;
                BigDecimal quantity = BigDecimal.ZERO;
                for (MediationRecordLineWS line : lines) {
                    total = total.add(line.getAmount());
                    quantity = quantity.add(line.getQuantity());
                }

                assertEquals("Total of order " + id, BigDecimal.ZERO, total.subtract(order.getOrderLines()[0].getAmountAsDecimal()));
                assertEquals("Qty of order " + id, BigDecimal.ZERO, quantity.subtract(order.getOrderLines()[0].getQuantityAsDecimal()));
                System.out.println("Order adds up: " + id);
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception!" + e.getMessage());
        }
    }

    @Test
    public void test03LongDistancePlanA() throws Exception {
        System.out.println("#test03LongDistancePlanA");

        final Integer MEDIATION_TEST_1_USER = 10760;

        JbillingAPI api = JbillingAPIFactory.getAPI();

        Integer[] ids = api.getLastOrders(MEDIATION_TEST_1_USER, 1);
        OrderWS order = api.getOrder(ids[0]);

        // long distance plan A, $0.20/min
        //      56240 x 0.20
        //      = $11,248.00
        assertEquals("1 line added", 1, order.getOrderLines().length);
        assertEquals("56240 minutes", new BigDecimal("56240"), order.getOrderLines()[0].getQuantityAsDecimal());
        assertEquals("$11,248 total", new BigDecimal("11248"), order.getOrderLines()[0].getAmountAsDecimal());
    }

    @Test
    public void test04LongDistancePlanB() throws Exception {
        System.out.println("#test04LongDistancePlanB");

        final Integer MEDIATION_TEST_2_USER = 10761;

        JbillingAPI api = JbillingAPIFactory.getAPI();

        Integer[] ids = api.getLastOrders(MEDIATION_TEST_2_USER, 1);
        OrderWS order = api.getOrder(ids[0]);

        // long distance plan B, $0.05/min
        //      29250 x 0.05
        //      = $1,462.50
        assertEquals("1 line added", 1, order.getOrderLines().length);
        assertEquals("29250 minutes", new BigDecimal("29250"), order.getOrderLines()[0].getQuantityAsDecimal());
        assertEquals("$1,462.50 total", new BigDecimal("1462.50"), order.getOrderLines()[0].getAmountAsDecimal());
    }

    @Test
    public void test05LongDistancePlanIncludedItems() throws Exception {
        System.out.println("#test05LongDistancePlanIncludedItems");

        final Integer MEDIATION_TEST_3_USER = 10762;

        JbillingAPI api = JbillingAPIFactory.getAPI();

        Integer[] ids = api.getLastOrders(MEDIATION_TEST_3_USER, 1);
        OrderWS order = api.getOrder(ids[0]);

        assertEquals("1 lines added", 1, order.getOrderLines().length);

        // long distance plan, 1000 minutes included
        //      650 minutes of calls @ 0.30/min
        //      1000 minutes included @ 0.00/min
        //
        //      650 x 0.30
        //      = $195
        for (OrderLineWS line: order.getOrderLines()) {
            if (line.getItemId() == 2800) {
                assertEquals("1650 units",  new BigDecimal("1650"), line.getQuantityAsDecimal());
                assertEquals("$195 total", new BigDecimal("195.00"), line.getAmountAsDecimal());
            }
        }
    }

    @Test
    public void test06RateCard() throws Exception {
        System.out.println("#test06RateCard");

        final Integer MEDIATION_TEST_4_USER = 10770; // mediation-batch-test-04
        final Integer MEDIATION_TEST_5_USER = 10771;
        final Integer MEDIATION_TEST_6_USER = 10772;
        final Integer MEDIATION_TEST_7_USER = 10773;
        final Integer MEDIATION_TEST_8_USER = 10774;
        final Integer MEDIATION_TEST_9_USER = 10775;
        final Integer MEDIATION_TEST_10_USER = 10776;

        // 150 calls to 1876999* @ 0.470/min - only 150 of the calls for this user are rated!
        Integer[] ids = api.getLastOrders(MEDIATION_TEST_4_USER, 1);
        OrderWS order = api.getOrder(ids[0]);

        assertEquals("1 line added", 1, order.getOrderLines().length);
        assertEquals("67800 minutes", new BigDecimal("67800"), order.getOrderLines()[0].getQuantityAsDecimal());
        assertEquals("$5322.75 total", new BigDecimal("5322.75"), order.getOrderLines()[0].getAmountAsDecimal());

        // 150 calls to 1809986* @ 0.260/min - only 150 of the calls for this user are rated!
        ids = api.getLastOrders(MEDIATION_TEST_5_USER, 1);
        order = api.getOrder(ids[0]);

        assertEquals("1 line added", 1, order.getOrderLines().length);
        assertEquals("67800 minutes", new BigDecimal("67800"), order.getOrderLines()[0].getQuantityAsDecimal());
        assertEquals("$2944.50 total", new BigDecimal("2944.50"), order.getOrderLines()[0].getAmountAsDecimal());

        // 150 calls to 1784593* @ 0.490/min - only 150 of the calls for this user are rated!
        ids = api.getLastOrders(MEDIATION_TEST_6_USER, 1);
        order = api.getOrder(ids[0]);

        assertEquals("1 line added", 1, order.getOrderLines().length);
        assertEquals("112950 minutes", new BigDecimal("112950"), order.getOrderLines()[0].getQuantityAsDecimal());
        assertEquals("$5549.25 total", new BigDecimal("5549.25"), order.getOrderLines()[0].getAmountAsDecimal());


        // 300 calls to 502979* @ 0.250/min - two batches of events totalling 300 rated calls.
        ids = api.getLastOrders(MEDIATION_TEST_7_USER, 1);
        order = api.getOrder(ids[0]);

        assertEquals("1 line added", 1, order.getOrderLines().length);
        assertEquals("154125 minutes", new BigDecimal("154125"), order.getOrderLines()[0].getQuantityAsDecimal());
        assertEquals("$13125.00 total", new BigDecimal("13125"), order.getOrderLines()[0].getAmountAsDecimal());


        // 300 calls to 52* @ 0.180/min - two batches or calls, one being processed at the beginning and the other
        //                                at the end of the mediation run, totalling 300 rated calls
        ids = api.getLastOrders(MEDIATION_TEST_8_USER, 1);
        order = api.getOrder(ids[0]);

        assertEquals("1 line added", 1, order.getOrderLines().length);
        assertEquals("154125 minutes", new BigDecimal("154125"), order.getOrderLines()[0].getQuantityAsDecimal());
        assertEquals("$9450.00 total", new BigDecimal("9450"), order.getOrderLines()[0].getAmountAsDecimal());

        // 300 calls to 40* @ 0.130/min - two batches of events totalling 300 rated calls.
        ids = api.getLastOrders(MEDIATION_TEST_9_USER, 1);
        order = api.getOrder(ids[0]);

        assertEquals("1 line added", 1, order.getOrderLines().length);
        assertEquals("154125 minutes", new BigDecimal("154125"), order.getOrderLines()[0].getQuantityAsDecimal());
        assertEquals("$6825.00 total", new BigDecimal("6825.00"), order.getOrderLines()[0].getAmountAsDecimal());

        // 900 calls to various numbers, with corresponding ratings.
        ids = api.getLastOrders(MEDIATION_TEST_10_USER, 1);
        order = api.getOrder(ids[0]);

        assertEquals("1 line added", 1, order.getOrderLines().length);
        assertEquals("79275  minutes", new BigDecimal("79275"), order.getOrderLines()[0].getQuantityAsDecimal());
        assertEquals("$23,556.00 total", new BigDecimal("23556.00"), order.getOrderLines()[0].getAmountAsDecimal());
    }

    @Test
    public void test07DuplicateEvents() throws Exception {
        System.out.println("#test07DuplicateEvents");

        final Integer MEDIATION_TEST_11_USER = 10777;

        JbillingAPI api = JbillingAPIFactory.getAPI();

        // 300 calls to 40* @ 0.130/min - 150 of those calls have a duplicate account code and will be ignored
        Integer[] ids = api.getLastOrders(MEDIATION_TEST_11_USER, 1);
        OrderWS order = api.getOrder(ids[0]);

        assertEquals("1 line added", 1, order.getOrderLines().length);
        assertEquals("112950 minutes", new BigDecimal("112950"), order.getOrderLines()[0].getQuantityAsDecimal());
        assertEquals("$1472.25 total", new BigDecimal("1472.25"), order.getOrderLines()[0].getAmountAsDecimal());
    }

    /**
     * This test case checks the content of files with saved errors on serve
     * BUT will work only for local machine and running test from ant target
     */
    @Test
    public void test08SavingErrorsToFile() {
        System.out.println("#test08SavingErrorsToFile");

        String dir = System.getProperty("mediation.errors.dir");
        String serverFilePath = (dir != null ?  dir + File.separator  : "") + "mediation-errors.csv";

        System.out.println("File server path defined as " + serverFilePath);
        File errorsFile = new File(serverFilePath);

        // TODO: test will work only for local server and for running from ant target
        // else it is impossible to obtain file path
        if (errorsFile.exists()) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(errorsFile));
                String line;
                boolean record9Found = false;
                boolean record10Found = false;
                while ((line = reader.readLine()) != null) {
                    String[] columns = com.sapienter.jbilling.server.util.Util.csvSplitLine(line, ',');
                    //last 4 columns will contain errors, processing date, record key and mediation process id
                    for (int i = 0; i < columns.length - 4; i++) {
                        PricingField field = new PricingField(columns[i]);
                        if (field.getName().equals("accountcode")) {
                            if (field.getValue().equals("09")) {
                                record9Found = true;
                                String errors = columns[columns.length - 4];
                                assertTrue("Custom error ERR-DURATION should be saved for record with key 09",
                                           errors.indexOf("ERR-DURATION") != -1);
                            } else if (field.getValue().equals("10")) {
                                record10Found = true;
                                String errors = columns[columns.length - 4];
                                assertTrue("Error JB-NO_USER should be presented for record with key 10",
                                           errors.indexOf("JB-NO_USER") != -1);
                            } else if (field.getValue().equals("07")) {
                                fail("Record with key 07 should not be saved");
                            }
                            break;
                        }
                    }
                }
                assertTrue("Record with key 09 should be presented", record9Found);
                assertTrue("Record with key 10 should be presented", record10Found);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                fail("Exception! " + e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
                fail("Exception! " + e.getMessage());
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Test
     public void test09SavingErrorsToJDBC() {
         System.out.println("#test09SavingErrorsToJDBC");

         Connection connection = null;
         try {
             connection = getConnection();
             String query = "select count(*) as REC_COUNT from MEDIATION_ERRORS err where err.accountcode = ? and err.error_message like ?";
             PreparedStatement statement = connection.prepareStatement(query);

             statement.setString(1, "09");
             statement.setString(2, "%ERR-DURATION%");
             ResultSet result = statement.executeQuery();
             result.next();
             Long recordsCount = result.getLong("REC_COUNT");
             assertTrue("Custom error ERR-DURATION should be saved for record with key 09", recordsCount.equals(1L));

             statement.setString(1, "10");
             statement.setString(2, "%JB-NO_USER%");
             result = statement.executeQuery();
             result.next();
             recordsCount = result.getLong("REC_COUNT");
             assertTrue("Error JB-NO_USER should be presented for record with key 10", recordsCount.equals(1L));

             statement.setString(1, "07");
             statement.setString(2, "%");
             result = statement.executeQuery();
             result.next();
             recordsCount = result.getLong("REC_COUNT");
             assertTrue("Record with key 07 should not be saved", recordsCount.equals(0L));
         } catch (SQLException e) {
             e.printStackTrace();
             fail("Exception: " + e);
         } catch (ClassNotFoundException e) {
             e.printStackTrace();
             fail("Exception: " + e);
         } finally {
             if (connection != null) {
                 try {
                     connection.close();
                 } catch (SQLException e) {
                     e.printStackTrace();
                 }
             }
         }
     }

    @Test
     public void test10JDBCReader() throws Exception {
         System.out.println("#test10JDBCReader");

         List<MediationProcessWS> all = api.getAllMediationProcesses();
         assertNotNull("process list can't be null", all);

         Collection <MediationRecordWS> processedRecordsFromJbillingTest = null;
         Collection <MediationRecordWS> processedRecordsFromHipersonic = null;
         for (MediationProcessWS process : all) {
             if (process.getConfigurationId().equals(JDBC_READER_JBILLING_DB_CFG_ID)) {
                 // JDBCReader from jbilliing_test
                 processedRecordsFromJbillingTest = api.getMediationRecordsByMediationProcess(process.getId());
             } else if (process.getConfigurationId().equals(JDBC_READER_HIPERSONIC_CDR_CFG_ID)) {
                 // JDBCReader from hipersonic jbilling_cdr DB
                 processedRecordsFromHipersonic = api.getMediationRecordsByMediationProcess(process.getId());
             }
         }

         assertEquals("Records read from jbilling_test.cdrentries ", 1, processedRecordsFromJbillingTest.size());
         assertEquals("Records read from HSQL jbilling_cdr ", 7, processedRecordsFromHipersonic.size());
         
         boolean jdbcRecordsProcessed = false;
         for (MediationRecordWS rec : processedRecordsFromJbillingTest) {
             if (rec.getKey().equals("20121")) {
                 jdbcRecordsProcessed = true;
             }
         }
         assertTrue("Record with key 20121 from DB should be processed", jdbcRecordsProcessed);
     }

    @Test
    public void test11ReprocessErrorRecord() throws Exception{
        System.out.println("#test11ReprocessErrorRecord");

        final String EXISTING_RECORD_ID = "20120";

        /*
           There is an existing record in mediation_record with an error status, this record
           should not be counted as an existing record and should allow another record with the
           same id_key to be re-processed on another pass.

           Records that error-ed out can be re-processed by another mediation process...
        */
        String query = "select id, id_key, status_id from mediation_record where id_key = ? order by id desc";
        Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, EXISTING_RECORD_ID);
        ResultSet result = statement.executeQuery();

        // new record should have status 29 "Done and billable"
        result.next();
        Integer newId = result.getInt("id");
        assertEquals("new record id_key is 20120", EXISTING_RECORD_ID, result.getString("id_key"));
        assertEquals("new record status is 'done and billable'", 29, result.getInt("status_id"));

        // old record should be untouched, existing status of 32 "Error declared"
        result.next();
        Integer oldId = result.getInt("id");
        assertEquals("old record has id 1", 1, oldId.intValue());
        assertEquals("old record id_key is 20120", EXISTING_RECORD_ID, result.getString("id_key"));
        assertEquals("old record status is 'error declared'", 32, result.getInt("status_id"));

        assertFalse("different record ids", newId.equals(oldId));

        connection.close();
    }

     private Connection getConnection() throws SQLException, ClassNotFoundException {
         String driver = SaveToJDBCMediationErrorHandler.DRIVER_DEFAULT;
         String url = "jdbc:postgresql://localhost:5432/jbilling_test";
         String username = "jbilling";
         String password = SaveToJDBCMediationErrorHandler.DATABASE_PASSWORD_DEFAULT;

         // create connection
         Class.forName(driver); // load driver
         return DriverManager.getConnection(url, username, password);
     }

    private void waitForMediationComplete(Integer maxTime) {
        Long start = new Date().getTime();
        while (api.isMediationProcessRunning() && new Date().getTime() < maxTime + start) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (new Date().getTime() > maxTime + start) {
            fail("Max time for mediation completion is exceeded");
        }
    }
}
