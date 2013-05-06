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

import java.io.Serializable;
import java.lang.reflect.Method;

import org.hibernate.ObjectNotFoundException;

import com.sapienter.jbilling.server.security.methods.SecuredMethodFactory;
import com.sapienter.jbilling.server.security.methods.SecuredMethodSignature;
import com.sapienter.jbilling.server.security.methods.SecuredMethodType;

/**
 * WSSecurityMethodMapper
 *
 * @author Brian Cowdery
 * @since 02-11-2010
 */
public class WSSecurityMethodMapper {

    static {
        SecuredMethodFactory.add("getUserWS", 0, SecuredMethodType.USER);
        SecuredMethodFactory.add("deleteUser", 0, SecuredMethodType.USER);
        SecuredMethodFactory.add("getUserContactsWS", 0, SecuredMethodType.USER);
        SecuredMethodFactory.add("updateUserContact", 0, SecuredMethodType.USER);   // todo: should validate user and contact type ids
        SecuredMethodFactory.add("updateCreditCard", 0, SecuredMethodType.USER);
        SecuredMethodFactory.add("updateAch", 0, SecuredMethodType.USER);
        SecuredMethodFactory.add("setAuthPaymentType", 0, SecuredMethodType.USER);
        SecuredMethodFactory.add("getAuthPaymentType", 0, SecuredMethodType.USER);
        SecuredMethodFactory.add("getPartner", 0, SecuredMethodType.PARTNER);

        SecuredMethodFactory.add("getItem", 0, SecuredMethodType.ITEM);             // todo: should validate item id and user id
        SecuredMethodFactory.add("deleteItem", 0, SecuredMethodType.ITEM);
        SecuredMethodFactory.add("deleteItemCategory", 0, SecuredMethodType.ITEM_CATEGORY);
        SecuredMethodFactory.add("getUserItemsByCategory", 0, SecuredMethodType.USER);
        SecuredMethodFactory.add("isUserSubscribedTo", 0, SecuredMethodType.USER);
        SecuredMethodFactory.add("getLatestInvoiceByItemType", 0, SecuredMethodType.USER);
        SecuredMethodFactory.add("getLastInvoicesByItemType", 0, SecuredMethodType.USER);
        SecuredMethodFactory.add("getLatestOrderByItemType", 0, SecuredMethodType.USER);
        SecuredMethodFactory.add("getLastOrdersByItemType", 0, SecuredMethodType.USER);

        SecuredMethodFactory.add("validatePurchase", 0, SecuredMethodType.USER);
        SecuredMethodFactory.add("validateMultiPurchase", 0, SecuredMethodType.USER);

        SecuredMethodFactory.add("getOrder", 0, SecuredMethodType.ORDER);
        SecuredMethodFactory.add("deleteOrder", 0, SecuredMethodType.ORDER);
        SecuredMethodFactory.add("getCurrentOrder", 0, SecuredMethodType.USER);
        SecuredMethodFactory.add("updateCurrentOrder", 0, SecuredMethodType.USER);
        SecuredMethodFactory.add("getOrderLine", 0, SecuredMethodType.ORDER_LINE);
        SecuredMethodFactory.add("getOrderByPeriod", 0, SecuredMethodType.USER);
        SecuredMethodFactory.add("getLatestOrder", 0, SecuredMethodType.USER);
        SecuredMethodFactory.add("getLastOrders", 0, SecuredMethodType.USER);

        SecuredMethodFactory.add("getInvoiceWS", 0, SecuredMethodType.INVOICE);
        SecuredMethodFactory.add("createInvoice", 0, SecuredMethodType.USER);
        SecuredMethodFactory.add("createInvoiceFromOrder", 0, SecuredMethodType.ORDER);
        SecuredMethodFactory.add("deleteInvoice", 0, SecuredMethodType.INVOICE);
        SecuredMethodFactory.add("getAllInvoices", 0, SecuredMethodType.USER);
        SecuredMethodFactory.add("getLatestInvoice", 0, SecuredMethodType.USER);
        SecuredMethodFactory.add("getLastInvoices", 0, SecuredMethodType.USER);
        SecuredMethodFactory.add("getUserInvoicesByDate", 0, SecuredMethodType.USER);
        SecuredMethodFactory.add("getPaperInvoicePDF", 0, SecuredMethodType.INVOICE);

        SecuredMethodFactory.add("getPayment", 0, SecuredMethodType.PAYMENT);
        SecuredMethodFactory.add("getLatestPayment", 0, SecuredMethodType.USER);
        SecuredMethodFactory.add("getLastPayments", 0, SecuredMethodType.USER);
        SecuredMethodFactory.add("payInvoice", 0, SecuredMethodType.INVOICE);

        SecuredMethodFactory.add("getBillingProcess", 0, SecuredMethodType.BILLING_PROCESS);
        SecuredMethodFactory.add("getBillingProcessGeneratedInvoices", 0, SecuredMethodType.BILLING_PROCESS);

        SecuredMethodFactory.add("getMediationEventsForOrder", 0, SecuredMethodType.ORDER);
        SecuredMethodFactory.add("getMediationRecordsByMediationProcess", 0, SecuredMethodType.MEDIATION_PROCESS);
        SecuredMethodFactory.add("deleteMediationConfiguration", 0, SecuredMethodType.MEDIATION_CONFIGURATION);

        SecuredMethodFactory.add("updateOrderAndLineProvisioningStatus", 0, SecuredMethodType.ORDER);
        SecuredMethodFactory.add("updateLineProvisioningStatus", 0, SecuredMethodType.ORDER_LINE);
        SecuredMethodFactory.add("saveCustomerNotes", 0, SecuredMethodType.USER);
        SecuredMethodFactory.add("notifyInvoiceByEmail", 0, SecuredMethodType.INVOICE);
        SecuredMethodFactory.add("notifyPaymentByEmail", 0, SecuredMethodType.PAYMENT);
        SecuredMethodFactory.add("deletePlugin", 0, SecuredMethodType.PLUG_IN);

        SecuredMethodFactory.add("getPlanWS", 0, SecuredMethodType.PLAN);
        SecuredMethodFactory.add("deletePlan", 0, SecuredMethodType.PLAN);
        SecuredMethodFactory.add("addPlanPrice", 0, SecuredMethodType.PLAN);
        SecuredMethodFactory.add("isCustomerSubscribed", 0, SecuredMethodType.PLAN);
        SecuredMethodFactory.add("getSubscribedCustomers", 0, SecuredMethodType.PLAN);
        SecuredMethodFactory.add("getPlansBySubscriptionItem", 0, SecuredMethodType.ITEM);
        SecuredMethodFactory.add("getPlansByAffectedItem", 0, SecuredMethodType.ITEM);
        SecuredMethodFactory.add("getCustomerPrice", 0, SecuredMethodType.USER);
    }

    /**
     * Return a WSSecured object mapped from the given method and method arguments for validation.
     * This produced a secure object for validation from web-service method calls that only accept and return
     * ID's instead of WS objects that can be individually validated.
     *
     * @param method method to map
     * @param args method arguments
     * @return instance of WSSecured mapped from the given entity, null if entity could not be mapped.
     */
    public static WSSecured getMappedSecuredWS(Method method, Object[] args) {
        if (method != null) {

            SecuredMethodSignature sig = SecuredMethodFactory.getSignature(method);
            if (sig != null && sig.getIdArgIndex() <= args.length) {
                try {
                    return sig.getType().getMappedSecuredWS((Serializable) args[sig.getIdArgIndex()]);
                } catch (ObjectNotFoundException e) {
                    // hibernate complains loudly... object does not exist, no reason to validate.
                    return null;
                }
            }
        }

        return null;
    }
}
