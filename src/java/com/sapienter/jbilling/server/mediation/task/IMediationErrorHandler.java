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

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.sapienter.jbilling.server.mediation.Record;
import com.sapienter.jbilling.server.mediation.db.MediationProcess;
import com.sapienter.jbilling.server.pluggableTask.TaskException;

public interface IMediationErrorHandler {

    public void process(Record record, List<String> errors, Date processingTime, MediationProcess mediationProcess)
            throws TaskException;

    public Map<Record, List<String>> retrieveErrorRecords(Date processingTime, MediationProcess mediationProcess)
            throws TaskException;
}
