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
package com.sapienter.jbilling.server.payment;

import com.sapienter.jbilling.server.user.contact.db.ContactDTO;
import com.sapienter.jbilling.server.user.db.CreditCardDTO;
import com.sapienter.jbilling.server.user.db.AchDTO;

public interface IExternalCreditCardStorage {

    /**
     * Store the given credit card using the payment gateways storage mechanism.
     *
     * This method should return null for storage failures, so that the
     * {@link com.sapienter.jbilling.server.payment.tasks.SaveCreditCardExternallyTask }
     * can perform failure handling.
     *
     * If an obscured and stored credit card is encountered, this method should still return a
     * gateway key for the card and not a null value. It is up to the implementation
     * to decide whether or not to re-store the card or to leave it as-is.
     *
     * @param contact ContactDTO from NewContactEvent, may be null if triggered by NewCreditCardEvent
     * @param creditCard credit card to store, may be null if triggered by NewContactEvent without credit card.
     * @param ach ach to store
     * @return gateway key of stored credit card, null if storage failed
     */
    public String storeCreditCard(ContactDTO contact, CreditCardDTO creditCard, AchDTO ach);
    
    /**
     * Delete the existing credit card details or the Ach payment details.
     * 
     * This method should return null for storage failures, so that the
     * {@link com.sapienter.jbilling.server.payment.tasks.SaveCreditCardExternallyTask }
     * can perform failure handling.
     *
     * @param contact contact to process
     * @param creditCard credit card to process
     * @param ach ach to process
     * @return resulting unique gateway key for the credit card/contact
     */
    public String deleteCreditCard(ContactDTO contact, CreditCardDTO creditCard, AchDTO ach);
}
