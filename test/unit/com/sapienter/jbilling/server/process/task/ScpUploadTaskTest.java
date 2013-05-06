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

package com.sapienter.jbilling.server.process.task;

import com.sapienter.jbilling.common.Util;
import junit.framework.TestCase;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Brian Cowdery
 * @since 08-06-2010
 */
public class ScpUploadTaskTest extends TestCase {

    private static final String BASE_DIR = Util.getSysProp("base_dir");

    private ScpUploadTask task = new ScpUploadTask(); // task under test

    public ScpUploadTaskTest() {
    }

    public ScpUploadTaskTest(String name) {
        super(name);
    }

    public void testCollectFilesNonRecursive() throws Exception {
        File path = new File(BASE_DIR + File.separator + "logos");

        // entityNotification.properties should be the only file
        // located on the root jbilling/resources/ path.
        List<File> files = task.collectFiles(path, ".*\\.jpg", false);

        assertEquals(1, files.size());
        assertEquals("entity-1.jpg", files.get(0).getName());
    }

    public void testCollectFilesRecursive() throws Exception {
        File path = new File(BASE_DIR);

        // all jasper report designs are in a sub directory of the root
        // jbilling/resources/ path, and won't be found unless we scan recursively.
        List<File> nonRecursive = task.collectFiles(path, ".*designs.*\\.jasper", false);
        assertEquals(0, nonRecursive.size());

        // jasper report designs from jbilling/resources/designs/
        // found because we're scanning recursively.
        List<File> files = task.collectFiles(path, ".*designs.*\\.jasper", true);
        Collections.sort(files);

        assertEquals(7, files.size());
        assertEquals("invoice_design.jasper", files.get(0).getName());
        assertEquals("invoice_design_page2.jasper", files.get(1).getName());
        assertEquals("invoice_design_sub.jasper", files.get(2).getName());
        assertEquals("simple_invoice.jasper", files.get(3).getName());
        assertEquals("simple_invoice_b2b.jasper", files.get(4).getName());
        assertEquals("simple_invoice_telco.jasper", files.get(5).getName());
        assertEquals("simple_invoice_telco_events.jasper", files.get(6).getName());
    }

    public void testCollectFilesCompoundRegex() throws Exception {
        File path = new File(BASE_DIR);

        // look for multiple files recursively matching *.jasper and *.jpg
        // should find files in jbilling/resources/design/ and jbilling/resources/logos/
        List<File> files = task.collectFiles(path, "(.*designs.*\\.jasper|.*\\.jpg)", true);
        Collections.sort(files);

        assertEquals(8, files.size());
        assertEquals("invoice_design.jasper", files.get(0).getName());
        assertEquals("invoice_design_page2.jasper", files.get(1).getName());
        assertEquals("invoice_design_sub.jasper", files.get(2).getName());
        assertEquals("simple_invoice.jasper", files.get(3).getName());
        assertEquals("simple_invoice_b2b.jasper", files.get(4).getName());
        assertEquals("simple_invoice_telco.jasper", files.get(5).getName());
        assertEquals("simple_invoice_telco_events.jasper", files.get(6).getName());
        assertEquals("entity-1.jpg", files.get(7).getName());
    }

/*
    public void testScpUpload() throws Exception {
        File path = new File(baseDir);
        List<File> files = task.collectFiles(path, ".*entity-1\\.jpg$", true);

        assertEquals(1, files.size());

        // todo: fill in when testing
        String host = "";
        String username = "";
        String password = "";

        task.upload(files, null, host, username, password);        
    }
*/
}
