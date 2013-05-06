package com.sapienter.jbilling.server.security.methods;

import com.sapienter.jbilling.server.invoice.db.InvoiceDAS;
import com.sapienter.jbilling.server.invoice.db.InvoiceDTO;
import com.sapienter.jbilling.server.item.db.*;
import com.sapienter.jbilling.server.mediation.db.MediationConfiguration;
import com.sapienter.jbilling.server.mediation.db.MediationConfigurationDAS;
import com.sapienter.jbilling.server.mediation.db.MediationProcess;
import com.sapienter.jbilling.server.mediation.db.MediationProcessDAS;
import com.sapienter.jbilling.server.order.db.OrderDAS;
import com.sapienter.jbilling.server.order.db.OrderDTO;
import com.sapienter.jbilling.server.order.db.OrderLineDAS;
import com.sapienter.jbilling.server.order.db.OrderLineDTO;
import com.sapienter.jbilling.server.payment.db.PaymentDAS;
import com.sapienter.jbilling.server.payment.db.PaymentDTO;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskBL;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskDTO;
import com.sapienter.jbilling.server.process.db.BillingProcessDAS;
import com.sapienter.jbilling.server.process.db.BillingProcessDTO;
import com.sapienter.jbilling.server.security.MappedSecuredWS;
import com.sapienter.jbilling.server.security.WSSecured;
import com.sapienter.jbilling.server.user.partner.db.Partner;
import com.sapienter.jbilling.server.user.partner.db.PartnerDAS;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: bcowdery
 * Date: 14/05/12
 * Time: 9:28 PM
 * To change this template use File | Settings | File Templates.
 */
public enum SecuredMethodType {

        USER {
            public WSSecured getMappedSecuredWS(Serializable id) {
                return id != null ? new MappedSecuredWS(null, (Integer) id) : null;
            }
        },

        PARTNER {
            public WSSecured getMappedSecuredWS(Serializable id) {
                Partner partner = new PartnerDAS().find(id);
                return partner != null ? new MappedSecuredWS(null, partner.getUser().getId()) : null;
            }
        },

        ITEM {
            public WSSecured getMappedSecuredWS(Serializable id) {
                ItemDTO item = new ItemDAS().find(id);
                return item != null ? new MappedSecuredWS(item.getEntity().getId(), null) : null;
            }
        },

        ITEM_CATEGORY {
            public WSSecured getMappedSecuredWS(Serializable id) {
                ItemTypeDTO itemType = new ItemTypeDAS().find(id);
                return itemType != null ? new MappedSecuredWS(itemType.getEntity().getId(), null) : null;
            }
        },

        ORDER {
            public WSSecured getMappedSecuredWS(Serializable id) {
                OrderDTO order = new OrderDAS().find(id);
                return order != null ? new MappedSecuredWS(null, order.getUserId()) : null;
            }
        },

        ORDER_LINE {
            public WSSecured getMappedSecuredWS(Serializable id) {
                OrderLineDTO line = new OrderLineDAS().find(id);
                return line != null ? new MappedSecuredWS(null, line.getPurchaseOrder().getUserId()) : null;
            }
        },

        INVOICE {
            public WSSecured getMappedSecuredWS(Serializable id) {
                InvoiceDTO invoice = new InvoiceDAS().find(id);
                return invoice != null ? new MappedSecuredWS(null, invoice.getUserId()) : null;
            }
        },

        PAYMENT {
            public WSSecured getMappedSecuredWS(Serializable id) {
                PaymentDTO payment = new PaymentDAS().find(id);
                return payment != null ? new MappedSecuredWS(null, payment.getBaseUser().getId()) : null;
            }
        },

        BILLING_PROCESS {
            public WSSecured getMappedSecuredWS(Serializable id) {
                BillingProcessDTO process = new BillingProcessDAS().find(id);
                return process != null ? new MappedSecuredWS(process.getEntity().getId(), null) : null;
            }
        },

        MEDIATION_PROCESS {
            public WSSecured getMappedSecuredWS(Serializable id) {
                MediationProcess process = new MediationProcessDAS().find(id);
                return process != null ? new MappedSecuredWS(process.getConfiguration().getEntityId(), null) : null;
            }
        },

        MEDIATION_CONFIGURATION {
            public WSSecured getMappedSecuredWS(Serializable id) {
                MediationConfiguration config = new MediationConfigurationDAS().find(id);
                return config != null ? new MappedSecuredWS(config.getEntityId(), null) : null;
            }
        },

        PLUG_IN {
            public WSSecured getMappedSecuredWS(Serializable id) {
                PluggableTaskDTO task = new PluggableTaskBL((Integer)id).getDTO();
                return task != null ? new MappedSecuredWS(task.getEntityId(), null) : null;
            }
        },

        PLAN {
            public WSSecured getMappedSecuredWS(Serializable id) {
                PlanDTO plan = new PlanDAS().find(id);
                return plan != null ? new MappedSecuredWS(plan.getItem().getEntity().getId(), null) : null;
            }
        };



        /**
         * implemented by each Type to return a secure object for validation based on the given ID.
         *
         * @param id id of the object type
         * @return secure object for validation
         */
        public abstract WSSecured getMappedSecuredWS(Serializable id);
}
