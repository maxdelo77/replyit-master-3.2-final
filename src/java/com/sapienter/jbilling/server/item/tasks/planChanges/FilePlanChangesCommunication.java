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

package com.sapienter.jbilling.server.item.tasks.planChanges;

import java.io.File;
import java.io.FileWriter;

import com.sapienter.jbilling.common.Util;
import com.sapienter.jbilling.server.item.db.ItemDTO;
import com.sapienter.jbilling.server.item.db.PlanDTO;
import com.sapienter.jbilling.server.item.event.AbstractPlanEvent;
import com.sapienter.jbilling.server.item.event.NewPlanEvent;
import com.sapienter.jbilling.server.item.event.PlanDeletedEvent;
import com.sapienter.jbilling.server.item.event.PlanUpdatedEvent;
import com.sapienter.jbilling.server.pluggableTask.PluggableTask;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskException;

/**
 * Test implementation of IPlanChangesCommunication that simply writes
 * the plan changes to jbilling/resources/plan_changes.txt
 */
public class FilePlanChangesCommunication implements IPlanChangesCommunication {

    private static final String FILENAME = "plan_changes.txt";

    public void process(NewPlanEvent event) throws PluggableTaskException {
        writeToFile(event, "insert");
    }

    public void process(PlanUpdatedEvent event) throws PluggableTaskException {
        writeToFile(event, "update");
    }

    public void process(PlanDeletedEvent event) throws PluggableTaskException {
        writeToFile(event, "delete");
    }

    private void writeToFile(AbstractPlanEvent event, String action) 
            throws PluggableTaskException {

        PlanDTO plan = event.getPlan();
        ItemDTO item = plan.getItem();

        String output = action + " itemId: " + item.getId() + 
                ", description: " + item.getDescription(
                item.getEntity().getLanguageId()) + "\n";

        String directory = Util.getSysProp("base_dir") + File.separator;

        FileWriter writer = null;
        try {
            writer = new FileWriter(directory + FILENAME, true);
            writer.write(output);
        } catch (Exception e) {
            throw new PluggableTaskException(e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (Exception e) { }
            }
        }
    }
}
