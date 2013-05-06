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

package com.sapienter.jbilling.server.util;

import com.sapienter.jbilling.common.SessionInternalError;
import com.sapienter.jbilling.server.entity.AchDTO;
import com.sapienter.jbilling.server.invoice.InvoiceWS;
import com.sapienter.jbilling.server.item.ItemDTOEx;
import com.sapienter.jbilling.server.item.ItemTypeWS;
import com.sapienter.jbilling.server.item.PlanItemWS;
import com.sapienter.jbilling.server.item.PlanWS;
import com.sapienter.jbilling.server.mediation.MediationConfigurationWS;
import com.sapienter.jbilling.server.mediation.MediationProcessWS;
import com.sapienter.jbilling.server.mediation.MediationRecordLineWS;
import com.sapienter.jbilling.server.mediation.MediationRecordWS;
import com.sapienter.jbilling.server.mediation.RecordCountWS;
import com.sapienter.jbilling.server.notification.MessageDTO;
import com.sapienter.jbilling.server.order.OrderLineWS;
import com.sapienter.jbilling.server.order.OrderPeriodWS;
import com.sapienter.jbilling.server.order.OrderProcessWS;
import com.sapienter.jbilling.server.order.OrderWS;
import com.sapienter.jbilling.server.payment.PaymentAuthorizationDTOEx;
import com.sapienter.jbilling.server.payment.PaymentWS;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskWS;
import com.sapienter.jbilling.server.pricing.db.RateCardWS;
import com.sapienter.jbilling.server.process.AgeingWS;
import com.sapienter.jbilling.server.process.BillingProcessConfigurationWS;
import com.sapienter.jbilling.server.process.BillingProcessWS;
import com.sapienter.jbilling.server.process.ProcessStatusWS;
import com.sapienter.jbilling.server.user.CardValidationWS;
import com.sapienter.jbilling.server.user.CompanyWS;
import com.sapienter.jbilling.server.user.ContactTypeWS;
import com.sapienter.jbilling.server.user.ContactWS;
import com.sapienter.jbilling.server.user.CreateResponseWS;
import com.sapienter.jbilling.server.user.UserTransitionResponseWS;
import com.sapienter.jbilling.server.user.UserWS;
import com.sapienter.jbilling.server.user.ValidatePurchaseWS;
import com.sapienter.jbilling.server.user.partner.PartnerWS;

import javax.jws.WebService;

import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Web service bean interface. 
 * {@see com.sapienter.jbilling.server.util.WebServicesSessionSpringBean} for documentation.
 */
@WebService
public interface IWebServicesSessionBean {

    public Integer getCallerId();
    public Integer getCallerCompanyId();
    public Integer getCallerLanguageId();
    public Integer getCallerCurrencyId();

    /*
        Users
     */

    public UserWS getUserWS(Integer userId) throws SessionInternalError;
    public Integer createUser(UserWS newUser) throws SessionInternalError;
    public void updateUser(UserWS user) throws SessionInternalError;
    public void deleteUser(Integer userId) throws SessionInternalError;

    public boolean userExistsWithName(String userName);
    public boolean userExistsWithId(Integer userId);

    public ContactWS[] getUserContactsWS(Integer userId) throws SessionInternalError;
    public void updateUserContact(Integer userId, Integer typeId, ContactWS contact) throws SessionInternalError;

    public ContactTypeWS getContactTypeWS(Integer contactTypeId) throws SessionInternalError;
    public Integer createContactTypeWS(ContactTypeWS contactType) throws SessionInternalError;

    public void updateCreditCard(Integer userId, com.sapienter.jbilling.server.entity.CreditCardDTO creditCard) throws SessionInternalError;
    public void deleteCreditCard(Integer userId);
    public void updateAch(Integer userId, AchDTO ach) throws SessionInternalError;
    public void deleteAch(Integer userId);

    public void setAuthPaymentType(Integer userId, Integer autoPaymentType, boolean use) throws SessionInternalError;
    public Integer getAuthPaymentType(Integer userId) throws SessionInternalError;

    public Integer[] getUsersByStatus(Integer statusId, boolean in) throws SessionInternalError;
    public Integer[] getUsersInStatus(Integer statusId) throws SessionInternalError;
    public Integer[] getUsersNotInStatus(Integer statusId) throws SessionInternalError;
    public Integer[] getUsersByCreditCard(String number) throws SessionInternalError;

    public Integer getUserId(String username) throws SessionInternalError;
    public Integer getUserIdByEmail(String email) throws SessionInternalError;

        public UserTransitionResponseWS[] getUserTransitions(Date from, Date to) throws SessionInternalError;
    public UserTransitionResponseWS[] getUserTransitionsAfterId(Integer id) throws SessionInternalError;

    public CreateResponseWS create(UserWS user, OrderWS order) throws SessionInternalError;


    /*
        Partners
     */

    public void triggerPartnerPayoutProcess(Date runDate);
    public void processPartnerPayout(Integer partnerId);
    public void processPartnerPayouts(Date runDate);

    public PartnerWS getPartner(Integer partnerId) throws SessionInternalError;
    public Integer createPartner(UserWS newUser, PartnerWS partner) throws SessionInternalError;
    public void updatePartner(UserWS newUser, PartnerWS partner) throws SessionInternalError;
    public void deletePartner (Integer partnerId) throws SessionInternalError;


    /*
        Items
     */

    public ItemDTOEx getItem(Integer itemId, Integer userId, String pricing);
    public ItemDTOEx[] getAllItems() throws SessionInternalError;
    public Integer createItem(ItemDTOEx item) throws SessionInternalError;
    public void updateItem(ItemDTOEx item);
    public void deleteItem(Integer itemId);

    public ItemDTOEx[] getItemByCategory(Integer itemTypeId);
    public Integer[] getUserItemsByCategory(Integer userId, Integer categoryId);

    public ItemTypeWS[] getAllItemCategories();
    public Integer createItemCategory(ItemTypeWS itemType) throws SessionInternalError;
    public void updateItemCategory(ItemTypeWS itemType) throws SessionInternalError;
    public void deleteItemCategory(Integer itemCategoryId);
    
    public String isUserSubscribedTo(Integer userId, Integer itemId);

    public InvoiceWS getLatestInvoiceByItemType(Integer userId, Integer itemTypeId) throws SessionInternalError;
    public Integer[] getLastInvoicesByItemType(Integer userId, Integer itemTypeId, Integer number) throws SessionInternalError;

    public OrderWS getLatestOrderByItemType(Integer userId, Integer itemTypeId) throws SessionInternalError;
    public Integer[] getLastOrdersByItemType(Integer userId, Integer itemTypeId, Integer number) throws SessionInternalError;

    public ValidatePurchaseWS validatePurchase(Integer userId, Integer itemId, String fields);
    public ValidatePurchaseWS validateMultiPurchase(Integer userId, Integer[] itemId, String[] fields);


    /*
        Orders
     */

    public OrderWS getOrder(Integer orderId) throws SessionInternalError;
    public Integer createOrder(OrderWS order) throws SessionInternalError;
    public void updateOrder(OrderWS order) throws SessionInternalError;
    public Integer createUpdateOrder(OrderWS order) throws SessionInternalError;
    public void deleteOrder(Integer id) throws SessionInternalError;

    public Integer createOrderAndInvoice(OrderWS order) throws SessionInternalError;

    public OrderWS getCurrentOrder(Integer userId, Date date) throws SessionInternalError;
    public OrderWS updateCurrentOrder(Integer userId, OrderLineWS[] lines, String pricing, Date date, String eventDescription) throws SessionInternalError;

    public OrderWS[] getUserSubscriptions(Integer userId) throws SessionInternalError;
    
    public OrderLineWS getOrderLine(Integer orderLineId) throws SessionInternalError;
    public void updateOrderLine(OrderLineWS line) throws SessionInternalError;

    public Integer[] getOrderByPeriod(Integer userId, Integer periodId) throws SessionInternalError;
    public OrderWS getLatestOrder(Integer userId) throws SessionInternalError;
    public Integer[] getLastOrders(Integer userId, Integer number) throws SessionInternalError;

    public OrderWS rateOrder(OrderWS order) throws SessionInternalError;
    public OrderWS[] rateOrders(OrderWS orders[]) throws SessionInternalError;

    public boolean updateOrderPeriods(OrderPeriodWS[] orderPeriods) throws SessionInternalError;
    public boolean updateOrCreateOrderPeriod(OrderPeriodWS orderPeriod) throws SessionInternalError;
    public boolean deleteOrderPeriod(Integer periodId) throws SessionInternalError;
    
    public PaymentAuthorizationDTOEx createOrderPreAuthorize(OrderWS order) throws SessionInternalError;


    /*
        Invoices
     */

    public InvoiceWS getInvoiceWS(Integer invoiceId) throws SessionInternalError;
    public Integer[] createInvoice(Integer userId, boolean onlyRecurring);
    public Integer[] createInvoiceWithDate(Integer userId, Date billingDate, Integer dueDatePeriodId, Integer dueDatePeriodValue, boolean onlyRecurring);
    public Integer createInvoiceFromOrder(Integer orderId, Integer invoiceId) throws SessionInternalError;
    public Integer applyOrderToInvoice(Integer orderId, InvoiceWS invoiceWs);
    public void deleteInvoice(Integer invoiceId);

    public InvoiceWS[] getAllInvoicesForUser(Integer userId);
    public Integer[] getAllInvoices(Integer userId);
    public InvoiceWS getLatestInvoice(Integer userId) throws SessionInternalError;
    public Integer[] getLastInvoices(Integer userId, Integer number) throws SessionInternalError;

    public Integer[] getInvoicesByDate(String since, String until) throws SessionInternalError;
    public Integer[] getUserInvoicesByDate(Integer userId, String since, String until) throws SessionInternalError;
    public Integer[] getUnpaidInvoices(Integer userId) throws SessionInternalError;

    public byte[] getPaperInvoicePDF(Integer invoiceId) throws SessionInternalError;
    public boolean notifyInvoiceByEmail(Integer invoiceId);
    public boolean notifyPaymentByEmail(Integer paymentId);

    /*
        Payments
     */

    public PaymentWS getPayment(Integer paymentId) throws SessionInternalError;
    public PaymentWS getLatestPayment(Integer userId) throws SessionInternalError;
    public Integer[] getLastPayments(Integer userId, Integer number) throws SessionInternalError;
    public BigDecimal getTotalRevenueByUser (Integer userId) throws SessionInternalError;

    public PaymentWS getUserPaymentInstrument(Integer userId) throws SessionInternalError;

    public Integer createPayment(PaymentWS payment);
    public void updatePayment(PaymentWS payment);
    public void deletePayment(Integer paymentId);

    public void removePaymentLink(Integer invoiceId, Integer paymentId) throws SessionInternalError;
    public void createPaymentLink(Integer invoiceId, Integer paymentId);

    public PaymentAuthorizationDTOEx payInvoice(Integer invoiceId) throws SessionInternalError;
    public Integer applyPayment(PaymentWS payment, Integer invoiceId) throws SessionInternalError;
    public PaymentAuthorizationDTOEx processPayment(PaymentWS payment, Integer invoiceId);

    public CardValidationWS validateCreditCard(com.sapienter.jbilling.server.entity.CreditCardDTO creditCard, ContactWS contact, int level);

    
    /*
        Billing process
     */

    public void triggerBillingAsync(final Date runDate);
    public boolean triggerBilling(Date runDate);
    public boolean isBillingProcessRunning();
    public ProcessStatusWS getBillingProcessStatus();

    public void triggerAgeing(Date runDate);
    public boolean isAgeingProcessRunning();
    public ProcessStatusWS getAgeingProcessStatus();

    public BillingProcessConfigurationWS getBillingProcessConfiguration() throws SessionInternalError;
    public Integer createUpdateBillingProcessConfiguration(BillingProcessConfigurationWS ws) throws SessionInternalError;

    public BillingProcessWS getBillingProcess(Integer processId);
    public Integer getLastBillingProcess() throws SessionInternalError;
    
    public List<OrderProcessWS> getOrderProcesses(Integer orderId);
    public List<OrderProcessWS> getOrderProcessesByInvoice(Integer invoiceId);

    public BillingProcessWS getReviewBillingProcess();
    public BillingProcessConfigurationWS setReviewApproval(Boolean flag) throws SessionInternalError;

    public List<Integer> getBillingProcessGeneratedInvoices(Integer processId);

    public AgeingWS[] getAgeingConfiguration(Integer languageId) throws SessionInternalError ;
    public void saveAgeingConfiguration(AgeingWS[] steps, Integer gracePeriod, Integer languageId) throws SessionInternalError;


    /*
        Mediation process
     */

    public void triggerMediation();
    public Integer triggerMediationByConfiguration(Integer cfgId);
    public boolean isMediationProcessRunning();
    public ProcessStatusWS getMediationProcessStatus();

    public MediationProcessWS getMediationProcess(Integer mediationProcessId);
    public List<MediationProcessWS> getAllMediationProcesses();
    public List<MediationRecordLineWS> getMediationEventsForOrder(Integer orderId);
    public List<MediationRecordLineWS> getMediationEventsForInvoice(Integer invoiceId);
    public List<MediationRecordWS> getMediationRecordsByMediationProcess(Integer mediationProcessId);
    public List<RecordCountWS> getNumberOfMediationRecordsByStatuses();
    public List<RecordCountWS> getNumberOfMediationRecordsByStatusesByMediationProcess(Integer mediationProcess);

    public List<MediationConfigurationWS> getAllMediationConfigurations();
    public void createMediationConfiguration(MediationConfigurationWS cfg);
    public List<Integer> updateAllMediationConfigurations(List<MediationConfigurationWS> configurations) throws SessionInternalError;
    public void deleteMediationConfiguration(Integer cfgId);


    /*
        Provisioning process
     */

    public void triggerProvisioning();

    public void updateOrderAndLineProvisioningStatus(Integer inOrderId, Integer inLineId, String result);
    public void updateLineProvisioningStatus(Integer orderLineId, Integer provisioningStatus);


    /*
        Preferences
     */

    public void updatePreferences(PreferenceWS[] prefList);
    public void updatePreference(PreferenceWS preference);
    public PreferenceWS getPreference(Integer preferenceTypeId);


    /*
        Currencies
     */

    public CurrencyWS[] getCurrencies();
    public void updateCurrencies(CurrencyWS[] currencies);
    public void updateCurrency(CurrencyWS currency);
    public Integer createCurrency(CurrencyWS currency);
    public boolean deleteCurrency(Integer currencyId);

    public CompanyWS getCompany();
    public void updateCompany(CompanyWS companyWS);
    
    /*
        Notifications
    */

    public void createUpdateNotification(Integer messageId, MessageDTO dto);
    public void saveCustomerNotes(Integer userId, String notes);


    /*
        Plug-ins
     */

    public PluggableTaskWS getPluginWS(Integer pluginId);
    public Integer createPlugin(PluggableTaskWS plugin);
    public void updatePlugin(PluggableTaskWS plugin);
    public void deletePlugin(Integer plugin);

	/*
	 * Quartz jobs
	 */
	public void rescheduleScheduledPlugin(Integer pluginId);
    public void unscheduleScheduledPlugin(Integer pluginId);

    /*
        Plans and special pricing
     */

    public PlanWS getPlanWS(Integer planId);
    public List<PlanWS> getAllPlans();
    public Integer createPlan(PlanWS plan);
    public void updatePlan(PlanWS plan);
    public void deletePlan(Integer planId);
    public void addPlanPrice(Integer planId, PlanItemWS price);

    public boolean isCustomerSubscribed(Integer planId, Integer userId);
    public Integer[] getSubscribedCustomers(Integer planId);
    public Integer[] getPlansBySubscriptionItem(Integer itemId);
    public Integer[] getPlansByAffectedItem(Integer itemId);

    public PlanItemWS createCustomerPrice(Integer userId, PlanItemWS planItem);
    public void updateCustomerPrice(Integer userId, PlanItemWS planItem);
    public void deleteCustomerPrice(Integer userId, Integer planItemId);

    public PlanItemWS[] getCustomerPrices(Integer userId);
    public PlanItemWS getCustomerPrice(Integer userId, Integer itemId);
    
    /*
     *  Rate Card
     */
    
    public Integer createRateCard(RateCardWS rateCard, File rateCardFile);
    public void updateRateCard(RateCardWS rateCard, File rateCardFile);
    public void deleteRateCard(Integer rateCardId);

}
