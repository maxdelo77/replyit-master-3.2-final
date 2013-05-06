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

package com.sapienter.jbilling.server.pricing.util;

import com.sapienter.jbilling.server.order.Usage;
import com.sapienter.jbilling.server.order.UsageBL;

import java.math.BigDecimal;

/**
 * Simple utility to count the total number of items on purchase orders for
 * a given user, within the users current billing period.
 *
 * The number of purchased items represents the "pool" of usage allowed by a
 * customer.
 *
 * @author Brian Cowdery
 * @since 24/03/11
 */
public class ItemPoolUtils {

    /**
     * Counts the pool size for the given user id and item, with no multiplier.
     *
     * @param userId user id
     * @param itemId item id
     * @return pooled quantity
     */
    public static BigDecimal getPoolSize(Integer userId, Integer itemId) {
        return getPoolSize(userId, itemId, BigDecimal.ONE);
    }

    /**
     * Counts the pool size for the given user id and item, multiplied by
     * the given multiplier.
     *
     * @param userId user id
     * @param itemId item id
     * @param multiplier multiplier
     * @return pool size
     */
    public static BigDecimal getPoolSize(Integer userId, Integer itemId, BigDecimal multiplier) {
        Usage pool = new UsageBL(userId).getItemUsage(itemId);
        return pool.getQuantity().multiply(multiplier);
    }
}
