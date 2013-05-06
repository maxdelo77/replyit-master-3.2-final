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

import com.sapienter.jbilling.server.pluggableTask.PluggableTask;
import com.sapienter.jbilling.server.process.db.BillingProcessDAS;
import org.hibernate.ScrollableResults;

import java.util.Date;

/**
 * BillableUserOrdersBillingProcessFilterTask
 *
 * @author Brian Cowdery
 * @since 28-10-2010
 */
public class BillableUserOrdersBillingProcessFilterTask extends PluggableTask implements IBillingProcessFilterTask {

    public ScrollableResults findUsersToProcess(Integer theEntityId, Date billingDate){
        return new BillingProcessDAS().findBillableUsersWithOrdersToProcess(theEntityId, billingDate);
    }
}
