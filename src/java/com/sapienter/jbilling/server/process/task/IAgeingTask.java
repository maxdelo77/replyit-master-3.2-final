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

import com.sapienter.jbilling.server.invoice.db.InvoiceDTO;
import com.sapienter.jbilling.server.process.db.AgeingEntityStepDTO;
import com.sapienter.jbilling.server.user.db.UserDTO;
import com.sapienter.jbilling.server.user.db.UserStatusDTO;

import java.util.Date;
import java.util.Set;

/**
 * IAgeingTask
 *
 * @author Brian Cowdery
 * @since 28/04/11
 */
public interface IAgeingTask {

    /**
     * Review all users for the given day, and age those that have outstanding invoices over
     * the set number of days for an ageing step.
     *
     * @param entityId entity to review
     * @param steps ageing steps
     * @param today today's date
     * @param executorId executor id
     */
    public void reviewAllUsers(Integer entityId, Set<AgeingEntityStepDTO> steps, Date today, Integer executorId);

    /**
     * Moves a user one step forward in the ageing process (move from active -> suspended etc.).
     *
     * @param steps ageing steps
     * @param user user to age
     * @param today today's date
     * @param executorId executor id
     * @return the resulting ageing step for the user after ageing
     */
    public AgeingEntityStepDTO ageUser(Set<AgeingEntityStepDTO> steps, UserDTO user, Date today, Integer executorId);

    /**
     * Removes a user from the ageing process (makes them active).
     *
     * @param user user to make active
     * @param excludedInvoiceId invoice id to ignore when determining if the user CAN be made active
     * @param executorId executor id
     */
    public void removeUser(UserDTO user, Integer excludedInvoiceId, Integer executorId);

    /**
     * Returns true if the given invoice is overdue.
     *
     * @param invoice invoice to check
     * @param user user owning the invoice
     * @param gracePeriod company wide grace period
     * @param today today's date
     * @return true if invoice is overdue, false if not
     */
    public boolean isInvoiceOverdue(InvoiceDTO invoice, UserDTO user, Integer gracePeriod, Date today);

    /**
     * Returns true if the user requires ageing.
     *
     * @param user user being reviewed
     * @param currentStep current ageing step of the user
     * @param today today's date
     * @return true if user requires ageing, false if not
     */
    public boolean isAgeingRequired(UserDTO user, AgeingEntityStepDTO currentStep, Date today);

    /**
     * Sets the users status.
     *
     * @param user user
     * @param status status to set
     * @param today today's date
     * @param executorId executor id
     */
    public void setUserStatus(UserDTO user, UserStatusDTO status, Date today, Integer executorId);


    /**
     * Get the status for the next step in the ageing process, based on the users
     * current status.
     *
     * @param steps configured ageing steps
     * @param currentStatusId the current user status
     */
    public UserStatusDTO getNextAgeingStep(Set<AgeingEntityStepDTO> steps, Integer currentStatusId);

}
