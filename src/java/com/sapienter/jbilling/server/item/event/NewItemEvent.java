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

package com.sapienter.jbilling.server.item.event;

import com.sapienter.jbilling.server.item.db.ItemDTO;

public class NewItemEvent extends AbstractItemEvent {

    public NewItemEvent(ItemDTO newItem) {
        super(newItem);
    }

    public String getName() {
        return "New item event";
    }
}
