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
 * Created on Jul 26, 2004
 */
package com.sapienter.jbilling.tools;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Properties;

/**
 * @author Emil
 */
public class UploadInternationalData {
    
    public static void main(String[] args) {
        
        String sqlInsert = "insert into international_description (table_id," +
                "foreign_id, psudo_column, language_id, content) values ( " +
                "?,?,?,?,?)";
        String sqlUpdate= "update international_description set content = ? " +
            " where table_id = ? and foreign_id = ? and psudo_column = ? " +
            "    and language_id = ? ";

        int inserted = 0;
        int updated = 0;
        Connection conn = null;
        try {
            
            if (args.length != 2) {
                System.err.println("Usage: UploadInternationalData file languageId");
                return;
            }
            String fileName = args[0];
            int languageId = Integer.valueOf(args[1]).intValue();
            
            // get the JDBC propertis from the same file the entity signup uses
            // see if all the properties are in place
            Properties globalProperties = new Properties();
            FileInputStream gpFile = new FileInputStream("signup.properties");
            globalProperties.load(gpFile);
            
            Class.forName(globalProperties.getProperty("driver_class"));
            conn = DriverManager.getConnection(
                    globalProperties.getProperty("connection_url"),
                    globalProperties.getProperty("connection_username"),
                    globalProperties.getProperty("connection_password"));

            conn.setAutoCommit(false);
            
            System.out.println("Processing file " + fileName + " for " +
                    "language " + languageId);
    
            // open the file
            BufferedReader file = new BufferedReader(new FileReader(fileName));
            // prepare the statement
            PreparedStatement insertStmt = conn.prepareStatement(sqlInsert);
            PreparedStatement updateStmt = conn.prepareStatement(sqlUpdate);
            Statement checkStmt = conn.createStatement();
            
            String record = file.readLine();
            while (record != null) {
                String fields[] = record.split("\\|");
                
                // the position of the columns is fixed
                int tableId = Integer.valueOf(fields[0].trim()).intValue();
                // the second field is just help for the translator
                int foreignId = Integer.valueOf(fields[2].trim()).intValue();
                
                /* this was necessary because a table was added in between the
                // file was generated and now. 
                if (tableId >= 44) {
                    tableId++; // compensate for the new ACH table
                }
                */
                
                String pColumn = fields[3].trim();
                String content = fields[4].trim();
                
                // see if it is there
                boolean isThere = checkStmt.execute("select 1 from international_description " +
                        "where table_id = " + tableId + " and foreign_id = " + foreignId +
                        " and psudo_column = '" + pColumn + "' and language_id = " + 
                        languageId);
                if (isThere) {
                    updateStmt.setInt(2, tableId);
                    updateStmt.setInt(3, foreignId);
                    updateStmt.setString(4, pColumn);
                    updateStmt.setInt(5, languageId);
                    updateStmt.setString(1, content);
                    updateStmt.executeUpdate();
                    updated++;
                } else {
                    insertStmt.setInt(1, tableId);
                    insertStmt.setInt(2, foreignId);
                    insertStmt.setString(3, pColumn);
                    insertStmt.setInt(4, languageId);
                    insertStmt.setString(5, content);
                    insertStmt.executeUpdate();
                    inserted++;
                }
                
                record = file.readLine();
            }
            
            file.close();
            conn.commit();
            insertStmt.close();
            conn.close();

            System.out.println("Total rows inserted: " + inserted
                    + " updated: " + updated);;
            
        } catch (Exception e) {
            System.err.println("Exception in row: " + inserted + " - " +
                    e.getMessage());        
            e.printStackTrace();
            if (conn != null) {
                try { conn.close(); } catch(Exception e1) {}
            }
        } 
    }
}
