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

package com.sapienter.jbilling.server.mediation.task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.common.Util;
import com.sapienter.jbilling.server.item.PricingField;
import com.sapienter.jbilling.server.mediation.Record;
import com.sapienter.jbilling.server.mediation.db.MediationProcess;
import com.sapienter.jbilling.server.pluggableTask.PluggableTask;
import com.sapienter.jbilling.server.pluggableTask.TaskException;
import com.sapienter.jbilling.server.pluggableTask.admin.ParameterDescription;



public class SaveToFileMediationErrorHandler extends PluggableTask
        implements IMediationErrorHandler {

    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(SaveToFileMediationErrorHandler.class));

    // plug-in parameters
    protected static final ParameterDescription PARAM_DIRECTORY_NAME = 
    	new ParameterDescription("directory", false, ParameterDescription.Type.STR);
    protected static final ParameterDescription PARAM_FILE_NAME = 
    	new ParameterDescription("file_name", false, ParameterDescription.Type.STR);
    protected static final ParameterDescription PARAM_ROTATE_FILE_DAILY = 
    	new ParameterDescription("rotate_file_daily", false, ParameterDescription.Type.STR);
    protected static final ParameterDescription PARAM_MEDIATION_CONFIGURATION_ID = 
    	new ParameterDescription("mediation_cfg_id", false, ParameterDescription.Type.STR);

    // default values
    protected final static String DEFAULT_DIRECTORY_NAME = "mediation" + File.separator + "errors";
    protected final static String DEFAULT_FILE_NAME = "mediation-errors";
    protected final static String DEFAULT_FILE_EXTENSION = ".csv";
    protected final static String DEFAULT_CSV_FILE_SEPARATOR = ",";

    //initializer for pluggable params
    { 
    	descriptions.add(PARAM_DIRECTORY_NAME);
        descriptions.add(PARAM_FILE_NAME);
        descriptions.add(PARAM_ROTATE_FILE_DAILY);
        descriptions.add(PARAM_MEDIATION_CONFIGURATION_ID);
    }


    public void process(Record record, List<String> errors, Date processingTime, MediationProcess mediationProcess)
            throws TaskException {

        if (mediationProcess.getConfiguration() != null
                &&  parameters.get(PARAM_MEDIATION_CONFIGURATION_ID.getName()) != null) {
            try {
                Integer configId = Integer.parseInt((String) parameters.get(PARAM_MEDIATION_CONFIGURATION_ID.getName()));
                if (!mediationProcess.getConfiguration().getId().equals(configId)) {
                    return;
                }
            } catch (NumberFormatException ex) {
                LOG.error("Error during plug-in parameters parsing, check the configuration", ex);
            }
        }
        File file = getFileForDate(processingTime);
        LOG.debug("Perform saving errors to file " + file.getAbsolutePath());
        FileWriter writer = null;
        try {
            writer = new FileWriter(file, true);

            List<String> columns = new ArrayList<String>();
            for (PricingField field : record.getFields()) {
                columns.add(PricingField.encode(field));
            }
            columns.add(com.sapienter.jbilling.server.util.Util.join(errors, " "));
            columns.add(new SimpleDateFormat("yyyyMMdd-HHmmss").format(processingTime));
            columns.add(mediationProcess.getId().toString());
            columns.add(record.getKey());

            String line = com.sapienter.jbilling.server.util.Util.concatCsvLine(columns, DEFAULT_CSV_FILE_SEPARATOR);
            if (line != null) {
                writer.write(line + "\r\n");
            }
        } catch (IOException e) {
            LOG.error(e);
            throw new TaskException(e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    LOG.error(e);
                }
            }
        }
    }

    public Map<Record, List<String>> retrieveErrorRecords(Date processingTime,
                                                          MediationProcess mediationProcess)
            throws TaskException {

        Map<Record, List<String>> errorRecordMap = new HashMap<Record, List<String>>();
        if (mediationProcess.getConfiguration() != null
                &&  parameters.get(PARAM_MEDIATION_CONFIGURATION_ID.getName()) != null) {
            try {
                Integer configId = Integer.parseInt((String) parameters.get(PARAM_MEDIATION_CONFIGURATION_ID.getName()));
                if (!mediationProcess.getConfiguration().getId().equals(configId)) {
                    return errorRecordMap;
                }
            } catch (NumberFormatException ex) {
                LOG.error("Error during plug-in parameters parsing, check the configuration", ex);
            }
        }

        File file = getFileForDate(processingTime);
        String fileNotFoundMsg = "Mediation error file does not exists for processing time: ";

        if (!file.exists()) {
            LOG.error(fileNotFoundMsg + processingTime);
            throw new TaskException(fileNotFoundMsg + processingTime);
        }
        LOG.debug("File to be processed for error retrieving: " + file.getAbsolutePath());

        FileReader fileReader = null;
        BufferedReader bufferedReader = null;

        try {
            fileReader = new FileReader(file);
            bufferedReader = new BufferedReader(fileReader);
            String line;

            while((line = bufferedReader.readLine()) != null) {

                Integer mediationProcessId = resolveMediationProcess(line);
                // if mediation process id does not match - skip
                // consider saving errors in per mediation process files
                if (mediationProcessId == null || !mediationProcessId.equals(mediationProcess.getId())) {
                    continue;
                }

                Record newErrorRecord = new Record();
                PricingField[] pricingFieldsValue = PricingField.getPricingFieldsValue(line);
                // if no records found - skip
                if (pricingFieldsValue.length == 0) {
                    continue;
                }
                String recordKey = resolveRecordKey(line);
                for (PricingField field : pricingFieldsValue) {
                    newErrorRecord.addField(field, false);
                }

                newErrorRecord.setKey(recordKey);

                List<String> recordErrors = resolveErrors(line, pricingFieldsValue.length);
                errorRecordMap.put(newErrorRecord, recordErrors);
            }

        } catch (FileNotFoundException e) {
            LOG.error(fileNotFoundMsg + processingTime);
            throw new TaskException(e);
        } catch (IOException e) {
            LOG.error(e);
            throw new TaskException(e);
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    throw  new TaskException(e);
                }
            }
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException e) {
                    throw  new TaskException(e);
                }
            }
        }
        return errorRecordMap;
    }

    private String resolveRecordKey(String line) {
        try {

            String [] lines = line.split(",");
            return lines[lines.length - 1];

        } catch (Exception e) {
            return null;
        }
    }

    private Integer resolveMediationProcess(String line) {
        // mediation process id is appended at the end of the record
        try {

            String [] lines = line.split(",");
            return Integer.valueOf(lines[lines.length - 2]);

        } catch (Exception e) {
            return null;
        }
    }

    private List<String> resolveErrors(String line, int length) {

        List<String> resolvedErrors = new ArrayList<String>();
        String[] fields = line.split(",");
        if (fields == null || fields.length == 0 || fields.length < length + 1) {
            LOG.warn("No error records found in line: " + line);
            return resolvedErrors;
        }
        String errorField = fields[length];
        String[] errors = errorField.split(" ");
        Collections.addAll(resolvedErrors, errors);

        return resolvedErrors;
    }

    protected String getDirectory() {
        return parameters.get(PARAM_DIRECTORY_NAME.getName()) == null
                ? Util.getSysProp("base_dir") + DEFAULT_DIRECTORY_NAME
                : (String) parameters.get(PARAM_DIRECTORY_NAME.getName());
    }

    protected String getFileName(Date date) {
        String fileName = parameters.get(PARAM_FILE_NAME.getName()) == null
                ? DEFAULT_FILE_NAME
                : (String) parameters.get(PARAM_FILE_NAME.getName());
        String suffix = parameters.get(PARAM_ROTATE_FILE_DAILY.getName()) == null
                || Boolean.valueOf((String) parameters.get(PARAM_ROTATE_FILE_DAILY.getName())).equals(Boolean.FALSE)
                ? "" : "_" + new SimpleDateFormat("yyyyMMdd").format(date);
        return fileName + suffix + DEFAULT_FILE_EXTENSION;
    }

    protected File getFileForDate(Date date) {
        return new File(getDirectory() + File.separator + getFileName(date));
    }


}
