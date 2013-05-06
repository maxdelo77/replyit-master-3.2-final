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

import org.hibernate.ScrollableResults;

import com.sapienter.jbilling.server.pluggableTask.PluggableTask;
import com.sapienter.jbilling.server.process.db.BillingProcessDAS;

import java.util.Date;


/**
 * Basic filter task for returning the appropriate customers to run through the billing cycle.
 * The task returns all active customers
 * 
 * @author Kevin Salerno
 *
 */
public class BasicBillingProcessFilterTask extends PluggableTask implements IBillingProcessFilterTask {

    public ScrollableResults findUsersToProcess(Integer theEntityId, Date billingDate){        
        return new BillingProcessDAS().findUsersToProcess(theEntityId);              
    }
}
