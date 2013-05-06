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
 *
 */
package com.sapienter.jbilling.tools;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import com.sapienter.jbilling.common.Util;
import com.sapienter.jbilling.server.invoice.IInvoiceSessionBean;
import com.sapienter.jbilling.server.invoice.NewInvoiceDTO;
import com.sapienter.jbilling.server.invoice.db.InvoiceLineDTO;
import com.sapienter.jbilling.server.invoice.db.InvoiceLineTypeDTO;
import com.sapienter.jbilling.server.item.db.ItemDAS;
import com.sapienter.jbilling.server.item.db.ItemDTO;
import com.sapienter.jbilling.server.util.RemoteContext;
import com.sapienter.jbilling.server.util.db.CurrencyDAS;
import com.sapienter.jbilling.server.util.db.CurrencyDTO;
import java.math.BigDecimal;

/**
 * @author Emil
 */
public class UploadInvoices {

    public static void main(String[] args) {
        
        // for each field that will be sent to the server we need an index
        int number = -1;
        int date = -1;
        int user_id = -1;
        int due_date = -1;
        int total = -1;
        int payable = -1;
        int balance = -1;
        int currency_id = -1;
        int notes = -1;
        
        String record = null;
        try {
            // see if all the properties are in place
            Properties prop = new Properties();
            FileInputStream gpFile = new FileInputStream("upload.properties");
            prop.load(gpFile);
            
            Integer entityId = Integer.valueOf(prop.getProperty("entity_id"));
            String fileName = prop.getProperty("file");
            System.out.println("Processing file " + fileName + " for entity " + 
                    entityId);
    
            // open the file
            BufferedReader file = new BufferedReader(new FileReader(fileName));
            // TODO: use standard API
/*            IInvoiceSessionBean remoteSession = (IInvoiceSessionBean) 
                    RemoteContext.getBean(
                    RemoteContext.Name.INVOICE_REMOTE_SESSION);
*/
            IInvoiceSessionBean remoteSession = null;
            String header = file.readLine();
            String columns[] = header.split("\t");
            for (int f = 0; f < columns.length; f++) {
                // scan for the columns
                if (columns[f].equalsIgnoreCase("number")) {
                    number = f;
                } else if (columns[f].equalsIgnoreCase("date")) {
                    date = f;
                } else if (columns[f].equalsIgnoreCase("user_id")) {
                    user_id = f;
                } else if (columns[f].equalsIgnoreCase("due_date")) {
                    due_date = f;
                } else if (columns[f].equalsIgnoreCase("total")) {
                    total = f;
                } else if (columns[f].equalsIgnoreCase("payable")) {
                    payable = f;
                } else if (columns[f].equalsIgnoreCase("balance")) {
                    balance = f;
                } else if (columns[f].equalsIgnoreCase("notes")) {
                    notes = f;
                } else if (columns[f].equalsIgnoreCase("currency_id")) {
                    currency_id = f;
                } 
            }
            
            int totalRows = 0;
            record = readLine(file);
            while (record != null) {
                totalRows++;
                String fields[] = record.split("\t");
                
                // get the user object ready
                NewInvoiceDTO invoice = new NewInvoiceDTO();
                Integer userId = null;
                
                if (number >= 0) {
                    invoice.setPublicNumber(fields[number].trim());
                }
                if (date >= 0) {
                    invoice.setBillingDate(Util.parseDate(fields[date].trim()));
                }
                if (user_id >= 0) {
                    userId = Integer.valueOf(fields[user_id].trim());
                }
                if (due_date >= 0) {
                    invoice.setDueDate(Util.parseDate(fields[due_date].trim()));
                }
                if (total >= 0) {
                    invoice.setTotal(new BigDecimal(fields[total].trim()));
                }
                if (payable >= 0) {
                    invoice.setToProcess(Integer.valueOf(fields[payable].trim()));
                }
                if (balance >= 0) {
                    invoice.setBalance(new BigDecimal(fields[balance].trim()));
                }
                if (currency_id >= 0) {
                    CurrencyDTO currency = new CurrencyDAS().find(Integer.valueOf(fields[currency_id].trim()));
                    invoice.setCurrency(currency);
                }
                if (notes >= 0) {
                    invoice.setCustomerNotes(fields[notes].trim());
                }
                
                // get the lines
                readInvoiceLines(invoice, fileName);
                
                // final tweaks
                invoice.setCarriedBalance(BigDecimal.ZERO);
                invoice.setInProcessPayment(new Integer(0));
                invoice.setIsReview(new Integer(0));
                
                remoteSession.create(entityId, userId, invoice);
                
                record = readLine(file);
            }
            
            file.close();

            System.out.println("Total invoices uploaded: " + totalRows);
            

        } catch (Exception e) {
            System.err.println("Exception on record " + record + " : " 
                    + e.getMessage());      
            e.printStackTrace();
        } 
    }
    
    public static void readInvoiceLines(NewInvoiceDTO invoice, String fileName) 
            throws FileNotFoundException, IOException {
        BufferedReader file = new BufferedReader(new FileReader(fileName + "_lines"));
        String header = file.readLine(); // just ignore it
        String record = readLine(file);

        while (record != null) {
            String fields[] = record.split("\t");
            if (invoice.getPublicNumber().equals(fields[0].trim())) {
                InvoiceLineDTO line = new InvoiceLineDTO();

                line.setAmount(new BigDecimal(fields[1].trim()));
                line.setQuantity(new BigDecimal(fields[2].trim()));
                line.setPrice(new BigDecimal(fields[3].trim()));
                if (fields[4].trim().length() > 0) {
                    ItemDTO item = new ItemDAS().find(Integer.valueOf(fields[4].trim()));
                    line.setItem(item);
                } else {
                    
                    line.setItem(null);
                }
                line.setDescription(fields[5].trim());
                line.setInvoiceLineType(new InvoiceLineTypeDTO(Integer.valueOf(fields[6].trim())));
                
                invoice.getResultLines().add(line);
            }
            record = readLine(file);
        }
        
        file.close();
    }
    
    static String readLine(BufferedReader file) 
            throws IOException {
        StringBuffer retValue = new StringBuffer();
        
        int aByte = file.read();
        boolean inString = false;
        while (aByte != -1) {
            if (aByte == '"') {
                inString = !inString;
            } else {
                if (!inString && aByte == '\n') {
                    break;
                } 
                retValue.append((char)aByte);
            }
            aByte = file.read();
        }
        
        //System.out.println("Read [" + retValue + "]");
        return retValue.length() > 0 ? retValue.toString() : null;
    }
}
