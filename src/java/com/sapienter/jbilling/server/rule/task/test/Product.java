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

package com.sapienter.jbilling.server.rule.task.test;

import java.util.List;

import com.sapienter.jbilling.server.item.db.ItemDAS;
import com.sapienter.jbilling.server.item.db.ItemDTO;

public class Product {
    private String name;
    private ItemDTO item;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setItem(ItemDTO item) {
        this.item = item;
    }

    public ItemDTO getItem() {
        if (item == null && name != null) {
            // lookup the id using the internalNumber
            List<ItemDTO> items = new ItemDAS().findItemsByInternalNumber(name);
            // just get first one
            if (!items.isEmpty()) {
                item = items.get(0);
            }
        }
        return item;
    }

    public Integer getItemId() {
        if (getItem() != null) {
            return item.getId();
        } else {
            return null;
        }
    }
}
