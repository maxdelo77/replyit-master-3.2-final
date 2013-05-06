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

package jbilling

import java.io.File;

import com.sapienter.jbilling.server.entity.AchDTO
import com.sapienter.jbilling.server.entity.CreditCardDTO
import com.sapienter.jbilling.server.invoice.InvoiceWS
import com.sapienter.jbilling.server.item.ItemDTOEx
import com.sapienter.jbilling.server.item.ItemTypeWS
import com.sapienter.jbilling.server.item.PlanItemWS
import com.sapienter.jbilling.server.item.PlanWS
import com.sapienter.jbilling.server.mediation.MediationConfigurationWS
import com.sapienter.jbilling.server.mediation.MediationProcessWS
import com.sapienter.jbilling.server.mediation.MediationRecordLineWS
import com.sapienter.jbilling.server.mediation.MediationRecordWS
import com.sapienter.jbilling.server.mediation.RecordCountWS
import com.sapienter.jbilling.server.notification.MessageDTO
import com.sapienter.jbilling.server.order.OrderLineWS
import com.sapienter.jbilling.server.order.OrderPeriodWS
import com.sapienter.jbilling.server.order.OrderProcessWS
import com.sapienter.jbilling.server.order.OrderWS
import com.sapienter.jbilling.server.payment.PaymentAuthorizationDTOEx
import com.sapienter.jbilling.server.payment.PaymentWS
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskWS
import com.sapienter.jbilling.server.pricing.RateCardBL;
import com.sapienter.jbilling.server.pricing.db.RateCardWS;
import com.sapienter.jbilling.server.process.AgeingWS
import com.sapienter.jbilling.server.process.BillingProcessConfigurationWS
import com.sapienter.jbilling.server.process.BillingProcessWS
import com.sapienter.jbilling.server.process.ProcessStatusWS
import com.sapienter.jbilling.server.user.CardValidationWS
import com.sapienter.jbilling.server.user.CompanyWS
import com.sapienter.jbilling.server.user.ContactTypeWS
import com.sapienter.jbilling.server.user.ContactWS
import com.sapienter.jbilling.server.user.CreateResponseWS
import com.sapienter.jbilling.server.user.UserTransitionResponseWS
import com.sapienter.jbilling.server.user.UserWS
import com.sapienter.jbilling.server.user.ValidatePurchaseWS
import com.sapienter.jbilling.server.user.partner.PartnerWS
import com.sapienter.jbilling.server.util.CurrencyWS
import com.sapienter.jbilling.server.util.IWebServicesSessionBean
import com.sapienter.jbilling.server.util.PreferenceWS

/**
 * Grails managed remote service bean for exported web-services. This bean delegates to
 * the WebServicesSessionBean just like the core JbillingAPI.
 */
class ApiService implements IWebServicesSessionBean {

    def IWebServicesSessionBean webServicesSession

    static transactional = true

    static expose = ['hessian', 'cxfjax', 'httpinvoker']

    public Integer getCallerId() {
        return webServicesSession.getCallerId();
    }

    public Integer getCallerCompanyId() {
        return webServicesSession.getCallerCompanyId();
    }

    public Integer getCallerLanguageId() {
        return webServicesSession.getCallerLanguageId();
    }
    
    public Integer getCallerCurrencyId() {
        return webServicesSession.getCallerCurrencyId();
    }

    public InvoiceWS getInvoiceWS(Integer invoiceId) {
        return webServicesSession.getInvoiceWS(invoiceId)
    }

	public InvoiceWS[] getAllInvoicesForUser(Integer userId) {
		return webServicesSession.getAllInvoicesForUser(userId)
	}
	
	public boolean notifyInvoiceByEmail(Integer invoiceId) {
		webServicesSession.notifyInvoiceByEmail (invoiceId)
	}
    
    public boolean notifyPaymentByEmail(Integer paymentId) { 
        webServicesSession.notifyPaymentByEmail(paymentId)
    }
    
    public Integer[] getAllInvoices(Integer userId) {
        return webServicesSession.getAllInvoices(userId)
    }

    public InvoiceWS getLatestInvoice(Integer userId) {
        return webServicesSession.getLatestInvoice(userId)
    }

    public Integer[] getLastInvoices(Integer userId, Integer number) {
        return webServicesSession.getLastInvoices(userId, number)
    }

    public Integer[] getInvoicesByDate(String since, String until) {
        return webServicesSession.getInvoicesByDate(since, until)
    }

    public Integer[] getUserInvoicesByDate(Integer userId, String since, String until) {
        return webServicesSession.getUserInvoicesByDate(userId, since, until)
    }

    public Integer[] getUnpaidInvoices(Integer userId) {
        return webServicesSession.getUnpaidInvoices(userId);
    }

    public byte[] getPaperInvoicePDF(Integer invoiceId) {
        return webServicesSession.getPaperInvoicePDF(invoiceId)
    }

    public void deleteInvoice(Integer invoiceId) {
        webServicesSession.deleteInvoice(invoiceId)
    }

    public Integer[] createInvoice(Integer userId, boolean onlyRecurring) {
        return webServicesSession.createInvoice(userId, onlyRecurring)
    }

    public Integer[] createInvoiceWithDate(Integer userId, Date billingDate, Integer dueDatePeriodId, Integer dueDatePeriodValue, boolean onlyRecurring) {
        return webServicesSession.createInvoiceWithDate(userId, billingDate, dueDatePeriodId, dueDatePeriodValue, onlyRecurring)
    }

    public Integer createInvoiceFromOrder(Integer orderId, Integer invoiceId) {
        return webServicesSession.createInvoiceFromOrder(orderId, invoiceId)
    }

    public Integer applyOrderToInvoice(Integer orderId, InvoiceWS invoiceWs) {
        return webServicesSession.applyOrderToInvoice(orderId, invoiceWs)
    }

    public Integer createUser(UserWS newUser) {
        return webServicesSession.createUser(newUser)
    }

    public void deleteUser(Integer userId) {
        webServicesSession.deleteUser(userId)
    }

    public boolean userExistsWithName(String userName) {
        return webServicesSession.userExistsWithName(userName)
    }

    public boolean userExistsWithId(Integer userId) {
        return webServicesSession.userExistsWithId(userId)
    }

    public ContactTypeWS getContactTypeWS(Integer contactTypeId) {
        return webServicesSession.getContactTypeWS(contactTypeId);
    }

    public Integer createContactTypeWS(ContactTypeWS contactType) {
        return webServicesSession.createContactTypeWS(contactType);
    }

    public void updateUserContact(Integer userId, Integer typeId, ContactWS contact) {
        webServicesSession.updateUserContact(userId, typeId, contact)
    }

    public void updateUser(UserWS user) {
        webServicesSession.updateUser(user)
    }

    public UserWS getUserWS(Integer userId) {
        return webServicesSession.getUserWS(userId)
    }

    public ContactWS[] getUserContactsWS(Integer userId) {
        return webServicesSession.getUserContactsWS(userId)
    }

    public Integer getUserId(String username) {
        return webServicesSession.getUserId(username)
    }

    public Integer getUserIdByEmail(String email) {
        return webServicesSession.getUserIdByEmail(email);
    }

    public Integer[] getUsersInStatus(Integer statusId) {
        return webServicesSession.getUsersInStatus(statusId)
    }

    public Integer[] getUsersNotInStatus(Integer statusId) {
        return webServicesSession.getUsersNotInStatus(statusId)
    }

    public Integer[] getUsersByCreditCard(String number) {
        return webServicesSession.getUsersByCreditCard(number)
    }

    public Integer[] getUsersByStatus(Integer statusId, boolean inStatus) {
        return webServicesSession.getUsersByStatus(statusId, inStatus)
    }

    public CreateResponseWS create(UserWS user, OrderWS order) {
        return webServicesSession.create(user, order)
    }

    void triggerPartnerPayoutProcess(Date runDate) {
        webServicesSession.triggerPartnerPayoutProcess(runDate)
    }

    void processPartnerPayout(Integer partnerId) {
        webServicesSession.processPartnerPayout(partnerId)
    }

    void processPartnerPayouts(Date runDate) {
        webServicesSession.processPartnerPayouts(runDate)
    }

    public PartnerWS getPartner(Integer partnerId) {
        return webServicesSession.getPartner(partnerId)
    }

    public Integer createPartner(UserWS newUser, PartnerWS partner) {
        return webServicesSession.createPartner(newUser, partner)
    }
    
    public void updatePartner(UserWS newUser, PartnerWS partner) {
        webServicesSession.updatePartner(newUser, partner)
    }
    
    public void deletePartner (Integer partnerId){
        webServicesSession.deletePartner(partnerId);
    }

    public PaymentAuthorizationDTOEx createOrderPreAuthorize(OrderWS order) {
        return webServicesSession.createOrderPreAuthorize(order)
    }

    public Integer createOrder(OrderWS order) {
        return webServicesSession.createOrder(order)
    }

    public OrderWS rateOrder(OrderWS order) {
        return webServicesSession.rateOrder(order)
    }

    public OrderWS[] rateOrders(OrderWS[] orders) {
        return webServicesSession.rateOrders(orders)
    }

    public void updateItem(ItemDTOEx item) {
        webServicesSession.updateItem(item)
    }

    void deleteItem(Integer itemId) {
        webServicesSession.deleteItem(itemId)
    }

    public Integer createOrderAndInvoice(OrderWS order) {
        return webServicesSession.createOrderAndInvoice(order)
    }

    public void updateOrder(OrderWS order) {
        webServicesSession.updateOrder(order)
    }

    public Integer createUpdateOrder(OrderWS order) {
        return webServicesSession.createUpdateOrder(order);
    }

    public OrderWS getOrder(Integer orderId) {
        return webServicesSession.getOrder(orderId)
    }

    public Integer[] getOrderByPeriod(Integer userId, Integer periodId) {
        return webServicesSession.getOrderByPeriod(userId, periodId)
    }

    public OrderLineWS getOrderLine(Integer orderLineId) {
        return webServicesSession.getOrderLine(orderLineId)
    }

    public void updateOrderLine(OrderLineWS line) {
        webServicesSession.updateOrderLine(line)
    }

    public OrderWS getLatestOrder(Integer userId) {
        return webServicesSession.getLatestOrder(userId)
    }

    public Integer[] getLastOrders(Integer userId, Integer number) {
        return webServicesSession.getLastOrders(userId, number)
    }

    public void deleteOrder(Integer id) {
        webServicesSession.deleteOrder(id)
    }

    public OrderWS getCurrentOrder(Integer userId, Date date) {
        return webServicesSession.getCurrentOrder(userId, date)
    }

    public OrderWS updateCurrentOrder(Integer userId, OrderLineWS[] lines, String pricing, Date date, String eventDescription) {
        return webServicesSession.updateCurrentOrder(userId, lines, pricing, date, eventDescription)
    }

	public OrderWS[] getUserSubscriptions(Integer userId) {
		return webServicesSession.getUserSubscriptions(userId);
	}

	public boolean updateOrderPeriods(OrderPeriodWS[] orderPeriods) {
		return webServicesSession.updateOrderPeriods(orderPeriods);
	}
	
    public boolean updateOrCreateOrderPeriod(OrderPeriodWS orderPeriod) {
        return webServicesSession.updateOrCreateOrderPeriod(orderPeriod);
    }
    
    
	public boolean deleteOrderPeriod(Integer periodId) {
		return webServicesSession.deleteOrderPeriod(periodId);
	}
	
    public Integer createPayment(PaymentWS payment) {
        return webServicesSession.createPayment(payment);
    }

    public void updatePayment(PaymentWS payment) {
        webServicesSession.updatePayment(payment);
    }

    public void deletePayment(Integer paymentId) {
        webServicesSession.deletePayment(paymentId);
    }

    public void removePaymentLink(Integer invoiceId, Integer paymentId) {
        webServicesSession.removePaymentLink (invoiceId, paymentId)
    }

    void createPaymentLink(Integer invoiceId, Integer paymentId) {
        webServicesSession.createPaymentLink(invoiceId, paymentId);
    }

    public PaymentAuthorizationDTOEx payInvoice(Integer invoiceId) {
        return webServicesSession.payInvoice(invoiceId)
    }

    public Integer applyPayment(PaymentWS payment, Integer invoiceId) {
        return webServicesSession.applyPayment(payment, invoiceId)
    }

    public PaymentWS getPayment(Integer paymentId) {
        return webServicesSession.getPayment(paymentId)
    }

    public PaymentWS getLatestPayment(Integer userId) {
        return webServicesSession.getLatestPayment(userId)
    }

    public Integer[] getLastPayments(Integer userId, Integer number) {
        return webServicesSession.getLastPayments(userId, number)
    }

    public PaymentWS getUserPaymentInstrument(Integer userId) {
        return webServicesSession.getUserPaymentInstrument(userId)
    }

	public BigDecimal getTotalRevenueByUser (Integer userId) {
		return webServicesSession.getTotalRevenueByUser(userId);
	}

    public PaymentAuthorizationDTOEx processPayment(PaymentWS payment, Integer invoiceId) {
        return webServicesSession.processPayment(payment, invoiceId)
    }

    public CardValidationWS validateCreditCard(com.sapienter.jbilling.server.entity.CreditCardDTO
                           creditCard, ContactWS contact, int level) {
           return webServicesSession.validateCreditCard(creditCard, contact, level);
    }

    public ValidatePurchaseWS validatePurchase(Integer userId, Integer itemId, String fields) {
        return webServicesSession.validatePurchase(userId, itemId, fields)
    }

    public ValidatePurchaseWS validateMultiPurchase(Integer userId, Integer[] itemId, String[] fields) {
        return webServicesSession.validateMultiPurchase(userId, itemId, fields)
    }

    public Integer createItem(ItemDTOEx item) {
        return webServicesSession.createItem(item)
    }

    public ItemDTOEx[] getAllItems() {
        return webServicesSession.getAllItems()
    }

    public UserTransitionResponseWS[] getUserTransitions(Date from, Date to) {
        return webServicesSession.getUserTransitions(from, to)
    }

    public UserTransitionResponseWS[] getUserTransitionsAfterId(Integer id) {
        return webServicesSession.getUserTransitionsAfterId(id)
    }

    public ItemDTOEx getItem(Integer itemId, Integer userId, String pricing) {
        return webServicesSession.getItem(itemId, userId, pricing)
    }

    public InvoiceWS getLatestInvoiceByItemType(Integer userId, Integer itemTypeId) {
        return webServicesSession.getLatestInvoiceByItemType(userId, itemTypeId)
    }

    public Integer[] getLastInvoicesByItemType(Integer userId, Integer itemTypeId, Integer number) {
        return webServicesSession.getLastInvoicesByItemType(userId, itemTypeId, number)
    }

    public OrderWS getLatestOrderByItemType(Integer userId, Integer itemTypeId) {
        return webServicesSession.getLatestOrderByItemType(userId, itemTypeId)
    }

    public Integer[] getLastOrdersByItemType(Integer userId, Integer itemTypeId, Integer number) {
        return webServicesSession.getLastOrdersByItemType(userId, itemTypeId, number)
    }

    public String isUserSubscribedTo(Integer userId, Integer itemId) {
        return webServicesSession.isUserSubscribedTo(userId, itemId)
    }

    public Integer[] getUserItemsByCategory(Integer userId, Integer categoryId) {
        return webServicesSession.getUserItemsByCategory(userId, categoryId)
    }

    public ItemDTOEx[] getItemByCategory(Integer itemTypeId) {
        return webServicesSession.getItemByCategory(itemTypeId)
    }

    public ItemTypeWS[] getAllItemCategories() {
        return webServicesSession.getAllItemCategories()
    }

    public Integer createItemCategory(ItemTypeWS itemType) {
        return webServicesSession.createItemCategory(itemType)
    }

    public void updateItemCategory(ItemTypeWS itemType) {
        webServicesSession.updateItemCategory(itemType)
    }

    void deleteItemCategory(Integer itemCategoryId) {
        webServicesSession.deleteItemCategory(itemCategoryId)
    }

    public void updateCreditCard(Integer userId, CreditCardDTO creditCard) {
        webServicesSession.updateCreditCard(userId, creditCard)
    }

    void deleteCreditCard(Integer userId) {
        webServicesSession.deleteCreditCard(userId)
    }

    public void updateAch(Integer userId, AchDTO ach) {
        webServicesSession.updateAch(userId, ach)
    }

    void deleteAch(Integer userId) {
        webServicesSession.deleteAch(userId)
    }

    public void setAuthPaymentType(Integer userId, Integer autoPaymentType, boolean use) {
        webServicesSession.setAuthPaymentType(userId, autoPaymentType, use)
    }

    public Integer getAuthPaymentType(Integer userId) {
        return webServicesSession.getAuthPaymentType(userId)
    }

	public AgeingWS[] getAgeingConfiguration(Integer languageId) {
		webServicesSession.getAgeingConfiguration(languageId) 
	}
	
	public void saveAgeingConfiguration(AgeingWS[] steps, Integer gracePeriod, Integer languageId) {
		webServicesSession.saveAgeingConfiguration(steps, gracePeriod, languageId)
	}
    

	/*
        Billing process
     */

	public void triggerBillingAsync(final Date runDate) {
		webServicesSession.triggerBillingAsync(runDate)
	}
	
    public boolean triggerBilling(Date runDate) {
        return webServicesSession.triggerBilling(runDate)
    }

	public boolean isBillingProcessRunning() {
		return webServicesSession.isBillingProcessRunning()
	}

    public ProcessStatusWS getBillingProcessStatus() {
        return webServicesSession.getBillingProcessStatus()
    }

    public void triggerAgeing(Date runDate) {
        webServicesSession.triggerAgeing(runDate)
    }

    public boolean isAgeingProcessRunning() {
        return webServicesSession.isAgeingProcessRunning()
    }

    public ProcessStatusWS getAgeingProcessStatus() {
        return webServicesSession.getAgeingProcessStatus()
    }

    public BillingProcessConfigurationWS getBillingProcessConfiguration() {
        return webServicesSession.getBillingProcessConfiguration()
    }

    public Integer createUpdateBillingProcessConfiguration(BillingProcessConfigurationWS ws) {
        return webServicesSession.createUpdateBillingProcessConfiguration(ws)
    }

    public BillingProcessWS getBillingProcess(Integer processId) {
        return webServicesSession.getBillingProcess(processId)
    }

    public Integer getLastBillingProcess() {
        return webServicesSession.getLastBillingProcess()
    }

    public List<OrderProcessWS> getOrderProcesses(Integer orderId) {
        return webServicesSession.getOrderProcesses(orderId);
    }

    public List<OrderProcessWS> getOrderProcessesByInvoice(Integer invoiceId) {
        return webServicesSession.getOrderProcessesByInvoice(invoiceId);
    }

    public BillingProcessWS getReviewBillingProcess() {
        return webServicesSession.getReviewBillingProcess()
    }

    public BillingProcessConfigurationWS setReviewApproval(Boolean flag) {
        return webServicesSession.setReviewApproval(flag)
    }

    public List<Integer> getBillingProcessGeneratedInvoices(Integer processId) {
        return webServicesSession.getBillingProcessGeneratedInvoices(processId)
    }


    /*
       Mediation process
    */

    public void triggerMediation() {
        webServicesSession.triggerMediation()
    }

    public Integer triggerMediationByConfiguration(Integer cfgId) {
        return webServicesSession.triggerMediationByConfiguration(cfgId)
    }

    public boolean isMediationProcessRunning() {
        return webServicesSession.isMediationProcessRunning()
    }

    public MediationProcessWS getMediationProcess(Integer mediationProcessId) {
        return webServicesSession.getMediationProcess(mediationProcessId)
    }

    public ProcessStatusWS getMediationProcessStatus() {
        return webServicesSession.getMediationProcessStatus()
    }

    public List<MediationProcessWS> getAllMediationProcesses() {
        return webServicesSession.getAllMediationProcesses()
    }

    public List<MediationRecordLineWS> getMediationEventsForOrder(Integer orderId) {
        return webServicesSession.getMediationEventsForOrder(orderId)
    }

    public List<MediationRecordLineWS> getMediationEventsForInvoice(Integer invoiceId) {
        return webServicesSession.getMediationEventsForInvoice(invoiceId);
    }

    public List<MediationRecordWS> getMediationRecordsByMediationProcess(Integer mediationProcessId) {
        return webServicesSession.getMediationRecordsByMediationProcess(mediationProcessId)
    }

    public List<RecordCountWS> getNumberOfMediationRecordsByStatuses() {
        return webServicesSession.getNumberOfMediationRecordsByStatuses()
    }

    public List<RecordCountWS> getNumberOfMediationRecordsByStatusesByMediationProcess(Integer mediationProcessId) {
        return webServicesSession.getNumberOfMediationRecordsByStatusesByMediationProcess(mediationProcessId)
    }

    public List<MediationConfigurationWS> getAllMediationConfigurations() {
        return webServicesSession.getAllMediationConfigurations()
    }

    public void createMediationConfiguration(MediationConfigurationWS cfg) {
        webServicesSession.createMediationConfiguration(cfg)
    }

    public List<Integer> updateAllMediationConfigurations(List<MediationConfigurationWS> configurations) {
        return webServicesSession.updateAllMediationConfigurations(configurations)
    }

    public void deleteMediationConfiguration(Integer cfgId) {
        webServicesSession.deleteMediationConfiguration(cfgId)
    }


    /*
       Provisioning process
    */

    public void triggerProvisioning() {
        webServicesSession.triggerProvisioning()
    }

    public void updateOrderAndLineProvisioningStatus(Integer inOrderId, Integer inLineId, String result) {
        webServicesSession.updateOrderAndLineProvisioningStatus(inOrderId, inLineId, result)
    }

    public void updateLineProvisioningStatus(Integer orderLineId, Integer provisioningStatus) {
        webServicesSession.updateLineProvisioningStatus(orderLineId, provisioningStatus)
    }


    /*
        Preferences
     */

    public void updatePreferences(PreferenceWS[] prefList) {
        webServicesSession.updatePreferences(prefList)
    }

    void updatePreference(PreferenceWS preference) {
        webServicesSession.updatePreference(preference)
    }

    public PreferenceWS getPreference(Integer preferenceTypeId) {
        webServicesSession.getPreference(preferenceTypeId);
    }


    /*
        Currencies
     */

    public CurrencyWS[] getCurrencies() {
        return webServicesSession.getCurrencies()
    }

    void updateCurrencies(CurrencyWS[] currencies) {
        webServicesSession.updateCurrencies(currencies)
    }

    public void updateCurrency(CurrencyWS currency) {
        webServicesSession.updateCurrency(currency)
    }

    public Integer createCurrency(CurrencyWS currency) {
        webServicesSession.createCurrency(currency)
    }
	
	public boolean deleteCurrency(Integer currencyId) {
		return webServicesSession.deleteCurrency(currencyId);
	}

    public CompanyWS getCompany() {
        webServicesSession.getCompany();
    }
    
    public void updateCompany(CompanyWS companyWS) {
        webServicesSession.updateCompany(companyWS);
    }
    
    /*
        Notification
    */

    public void createUpdateNotification(Integer messageId, MessageDTO dto) {
       webServicesSession.createUpdateNotification(messageId, dto)
    }

	public void saveCustomerNotes(Integer userId, String notes) {
		webServicesSession.saveCustomerNotes(userId, notes)
	}
    
    /*
     * Plug-ins
     */
    PluggableTaskWS getPluginWS(Integer pluginId) {
        return webServicesSession.getPluginWS(pluginId);
    }

    Integer createPlugin(PluggableTaskWS plugin) {
        return webServicesSession.createPlugin(plugin);
	}
    
    void updatePlugin(PluggableTaskWS plugin) {
        webServicesSession.updatePlugin(plugin);
	}
    
    void deletePlugin(Integer plugin) {
        webServicesSession.deletePlugin(plugin);
	}
	
	/*
	 * Quartz jobs
	 */
	public void rescheduleScheduledPlugin(Integer pluginId) {
		webServicesSession.rescheduleScheduledPlugin(pluginId);
	}

    public void unscheduleScheduledPlugin(Integer pluginId) {
        webServicesSession.unscheduleScheduledPlugin(pluginId);
    }

    /*
        Plans and special pricing
     */

    public PlanWS getPlanWS(Integer planId) {
        return webServicesSession.getPlanWS(planId);
    }

    public List<PlanWS> getAllPlans() {
        return webServicesSession.getAllPlans();
    }

    public Integer createPlan(PlanWS plan) {
        return webServicesSession.createPlan(plan);
    }

    public void updatePlan(PlanWS plan) {
        webServicesSession.updatePlan(plan);
    }

    public void deletePlan(Integer planId) {
        webServicesSession.deletePlan(planId);
    }

    public void addPlanPrice(Integer planId, PlanItemWS price) {
        webServicesSession.addPlanPrice(planId, price);
    }

    public boolean isCustomerSubscribed(Integer planId, Integer userId) {
        return webServicesSession.isCustomerSubscribed(planId, userId);
    }

    public Integer[] getSubscribedCustomers(Integer planId) {
        return webServicesSession.getSubscribedCustomers(planId);
    }

    public Integer[] getPlansBySubscriptionItem(Integer itemId) {
        return webServicesSession.getPlansBySubscriptionItem(itemId);
    }

    public Integer[] getPlansByAffectedItem(Integer itemId) {
        return webServicesSession.getPlansByAffectedItem(itemId);
    }

    public PlanItemWS createCustomerPrice(Integer userId, PlanItemWS planItem) {
        return webServicesSession.createCustomerPrice(userId, planItem);
    }

    public void updateCustomerPrice(Integer userId, PlanItemWS planItem) {
        webServicesSession.updateCustomerPrice(userId, planItem);
    }

    public void deleteCustomerPrice(Integer userId, Integer planItemId) {
        webServicesSession.deleteCustomerPrice(userId, planItemId);
    }

    public PlanItemWS[] getCustomerPrices(Integer userId) {
        return webServicesSession.getCustomerPrices(userId);
    }

    public PlanItemWS getCustomerPrice(Integer userId, Integer itemId) {
        return webServicesSession.getCustomerPrice(userId, itemId);
    }
	
	public Integer createRateCard(RateCardWS rateCard, File rateCardFile) {
		return webServicesSession.createRateCard(rateCard, rateCardFile);
	}

	public void updateRateCard(RateCardWS rateCard, File rateCardFile) {
		webServicesSession.updateRateCard(rateCard, rateCardFile);
	}

	public void deleteRateCard(Integer rateCardId) {
		 webServicesSession.deleteRateCard(rateCardId);
	}
}
