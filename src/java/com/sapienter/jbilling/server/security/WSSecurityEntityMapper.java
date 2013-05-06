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

package com.sapienter.jbilling.server.security;

import com.sapienter.jbilling.server.item.ItemBL;
import com.sapienter.jbilling.server.item.ItemTypeWS;
import com.sapienter.jbilling.server.item.PlanBL;
import com.sapienter.jbilling.server.item.PlanItemBL;
import com.sapienter.jbilling.server.item.PlanItemWS;
import com.sapienter.jbilling.server.item.PlanWS;
import com.sapienter.jbilling.server.item.db.ItemDTO;
import com.sapienter.jbilling.server.item.db.ItemTypeDAS;
import com.sapienter.jbilling.server.item.db.ItemTypeDTO;
import com.sapienter.jbilling.server.item.db.PlanDTO;
import com.sapienter.jbilling.server.item.db.PlanItemDTO;
import com.sapienter.jbilling.server.order.OrderLineWS;
import com.sapienter.jbilling.server.order.db.OrderDAS;
import com.sapienter.jbilling.server.order.db.OrderDTO;

/**
 * The WSSecurityEntityMapper converts WS classes to a simple implementation of WSSecured so that
 * access can be validated.
 *
 * This mapper is intended to be used in situations where the WS class cannot be made to implement the
 * WSSecured interface itself. Generally these are situations where the addition of a userId or entityId
 * field would be impractical or confusing.
 *
 * @see com.sapienter.jbilling.server.security.WSSecurityAdvice
 *
 * @author Brian Cowdery
 * @since 02-11-2010
 */
public class WSSecurityEntityMapper {

    /**
     * Return a WSSecured object mapped from the given entity for validation. This method
     * converts legacy WS classes that cannot be made to implement the WSSecured interface.
     *
     * @param o object to convert
     * @return instance of WSSecured mapped from the given entity, null if entity could not be mapped.
     */
    public static WSSecured getMappedSecuredWS(Object o) {
        if (o instanceof OrderLineWS)
            return fromOrderLineWS((OrderLineWS) o);

        if (o instanceof ItemTypeWS)
            return fromItemTypeWS((ItemTypeWS) o);

        if (o instanceof PlanWS)
            return fromPlanWS((PlanWS) o);

        if (o instanceof PlanItemWS)
            return fromPlanItemWS((PlanItemWS) o);

        return null;
    }

    private static WSSecured fromOrderLineWS(OrderLineWS orderLine) {
        OrderDTO order = new OrderDAS().find(orderLine.getOrderId());
        return order != null ? new MappedSecuredWS(null, order.getUserId()) : null; // user id
    }

    private static WSSecured fromItemTypeWS(ItemTypeWS type) {
        ItemTypeDTO dto = new ItemTypeDAS().find(type.getId());
        return dto != null ? new MappedSecuredWS(dto.getEntity().getId(), null) : null; // entity id
    }

    private static WSSecured fromPlanWS(PlanWS ws) {
        ItemDTO item = new ItemBL(ws.getItemId()).getEntity();
        return item != null ? new MappedSecuredWS(item.getEntity().getId(), null) : null; // entity id of plan item
    }

    private static WSSecured fromPlanItemWS(PlanItemWS ws) {
        ItemDTO item = new ItemBL(ws.getItemId()).getEntity();
        return item != null ? new MappedSecuredWS(item.getEntity().getId(), null) : null; // entity id of priced item
    }
}
