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
package com.sapienter.jbilling.tools;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.server.util.Util;
import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Alexander Aksenov
 * @since 14.03.2010
 */
public class RulesGenerator {

    private final static FormatLogger log = new FormatLogger(Logger.getLogger(RulesGenerator.class));

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.err.println("Template file path and data file path are required!");
            System.exit(1);
            return;
        }
        System.out.println("Rules generation for template " + args[0] + " and data file " + args[1] + "...");
        generateRules(args[0], args[1]);
    }

    public static void generateRules(String templateFilePath, String dataFilePath) throws IOException {
        File data = new File(dataFilePath);
        File template = new File(templateFilePath);

        // output generated rule to the data directory, named for the template
        String name = template.getName().substring(0, template.getName().lastIndexOf(".vm"));
        File output = new File(data.getParent() + File.separator + name + ".drl");

        generateRules(template, data, ',', output);
    }

    public static void generateRules(File templateFile, File dataFile, char fieldSeparator, File outputFile) throws IOException {
        VelocityEngine velocity = new VelocityEngine();
        FileWriter writer = null;
        BufferedReader dataReader = null;
        String line;

        try {
            dataReader = new BufferedReader(new FileReader(dataFile));
            // read template from file
            String template = readFileToString(templateFile);

            // count rows number in input data file
            Long totalRows = 0L;
            while (dataReader.readLine() != null) {
                totalRows++;
            }
            try {
                dataReader.close();
            } catch (IOException e) {
                log.error(e);
            }
            // reopen for second pass
            dataReader = new BufferedReader(new FileReader(dataFile));

            writer = new FileWriter(outputFile);

            // reading lines from input file and process template with obtained data
            Long rowNumber = 0L;
            while ((line = dataReader.readLine()) != null) {
                // read data from current line
                String[] inputData = Util.csvSplitLine(line, fieldSeparator);
                Map<String, Object> parameters = new HashMap<String, Object>();
                // put data into velocity context
                parameters.put("total_rows", totalRows);
                parameters.put("row_number", rowNumber);
                parameters.put("total_columns", inputData.length);
                for (int i = 0; i < inputData.length; i++) {
                    parameters.put("field_" + (i + 1), inputData[i]);
                }
                VelocityContext velocityContext = new VelocityContext(parameters);
                // process template for current row and write it to common writer (for all passes)
                velocity.evaluate(velocityContext, writer, "Error template as string?", template);

                rowNumber++;
            }
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    log.error(e);
                }
            }
            if (dataReader != null) {
                try {
                    dataReader.close();
                } catch (IOException e) {
                    log.error(e);
                }
            }
        }

    }

    private static String readFileToString(File file) throws IOException {
        StringBuffer fileData = new StringBuffer();
        BufferedReader reader = new BufferedReader(
                new FileReader(file));
        char[] buf = new char[1024];
        int numRead = 0;
        while ((numRead = reader.read(buf)) != -1) {
            fileData.append(buf, 0, numRead);
        }
        reader.close();
        return fileData.toString();
    }

}
