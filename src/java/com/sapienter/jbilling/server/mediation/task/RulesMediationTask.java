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


import org.apache.log4j.Logger;


import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.server.item.PricingField;
import com.sapienter.jbilling.server.mediation.Record;
import com.sapienter.jbilling.server.pluggableTask.TaskException;
import com.sapienter.jbilling.server.rule.RulesBaseTask;
import com.sapienter.jbilling.server.user.db.CompanyDAS;
import java.util.List;

@Deprecated
public class RulesMediationTask extends RulesBaseTask implements
        IMediationProcess {

    protected FormatLogger getLog() {
        return new FormatLogger(Logger.getLogger(RulesMediationTask.class));
    }
        
    public void process(List<Record> records, List<MediationResult> results, String configurationName)
            throws TaskException {
 
        // this plug-in gets called many times for the same instance
        rulesMemoryContext.clear();

        int index = -1; // to track the results list
        // if results are passed, there has to be one per record
        if (results != null && results.size() > 0) {
            if (records.size() != results.size()) {
                throw new TaskException("If results are passed, there have to be the same number as" +
                        " records");
            }
            index = 0;
        } else if (results == null) {
            throw new TaskException("The results array can not be null");
        }

        for (Record record: records) {
            // one result per record
            MediationResult result = null;
            if (index >= 0) {
                result = results.get(index++);
            } else {
                result = new MediationResult(configurationName, true);
            }
            result.setRecordKey(record.getKey());
            rulesMemoryContext.add(result);
            results.add(result); // for easy retrival later

            for (PricingField field: record.getFields()) {
                field.setResultId(result.getId());
                rulesMemoryContext.add(field);
            }
        }

        // add the company
        rulesMemoryContext.add(new CompanyDAS().find(getEntityId()));
        
        // then execute the rules
        executeRules();
    }
}
