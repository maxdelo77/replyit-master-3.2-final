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

/*
 * Created on Nov 15, 2004
 *
 */
package com.sapienter.jbilling.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Properties;
import java.util.List;

/**
 * @author Emil
 *
 */
public class DocumentationIndex {

    static private BufferedReader reader = null;
    
    public static void main(String[] args) {
        try {
            // find my properties
            Properties globalProperties = new Properties();
            FileInputStream gpFile = new FileInputStream("indexing.properties");
            globalProperties.load(gpFile);
            
            // read the directory
            String dirName = globalProperties.getProperty("directory");
            File dir = new File(dirName);
            String filesNames[] = dir.list();
            
            List entries = new ArrayList();
            for (int f = 0; f < filesNames.length; f++) {
                File thisFile = new File(dirName + "/" + filesNames[f]);
                // skip directories
                if (!thisFile.isDirectory() && !filesNames[f].equals(
                        "index.html")) {
                    entries.add(filesNames[f]);
                }
            }
            // sort them by name
            Collections.sort(entries);
            
            // create the result file
            FileOutputStream result = new FileOutputStream(new File(dirName + 
                    "/index.html"));
            result.write("<html><body>".getBytes());
            
            for (Iterator it = entries.iterator(); it.hasNext();) {
                String entry = (String) it.next();
                System.out.println("Adding entry" + entry);

                if (entry.endsWith(".htm")) {
                    // it is an html page, process it
                    String htmlentry = entry.replaceAll(" ", "%20");
                    String link = "<a href=" + htmlentry+ ">";
                    
                    reader = new BufferedReader( new FileReader(
                            dir + "/" + entry) ); 
                    // find the title
                    String title = getText("title");
                    link += title + "</a><br/>\n";
                    
                    result.write(link.getBytes());
                }

            }
            result.write("</body></html>".getBytes());
            result.close();
            
            System.out.println("Done.");
            
        } catch (FileNotFoundException e) {
            System.err.println("Could not open file. " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    static String getText(String tagName) throws IOException {
        StringBuffer retValue = new StringBuffer();
        String line = reader.readLine();
        while (line != null) {
            if (line.indexOf("<" + tagName + ">") >= 0) {
                return line.substring(line.indexOf(">") + 1, line.lastIndexOf('<'));
            }
            
            line = reader.readLine();
        }
        
        return null;
    }

}
