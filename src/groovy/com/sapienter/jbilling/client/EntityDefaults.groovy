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

package com.sapienter.jbilling.client

import com.sapienter.jbilling.server.user.db.CompanyDTO
import com.sapienter.jbilling.server.order.db.OrderPeriodDTO
import com.sapienter.jbilling.server.process.db.PeriodUnitDTO
import com.sapienter.jbilling.server.util.Constants
import com.sapienter.jbilling.server.util.db.LanguageDTO
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskDTO
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskTypeDTO
import com.sapienter.jbilling.server.payment.db.PaymentMethodDTO
import com.sapienter.jbilling.server.util.db.PreferenceDTO
import com.sapienter.jbilling.server.util.db.JbillingTable
import com.sapienter.jbilling.server.util.db.PreferenceTypeDTO
import com.sapienter.jbilling.server.user.db.UserDTO
import com.sapienter.jbilling.server.process.db.BillingProcessConfigurationDTO
import org.joda.time.DateMidnight
import com.sapienter.jbilling.server.invoice.db.InvoiceDeliveryMethodDTO
import com.sapienter.jbilling.server.process.db.AgeingEntityStepDTO

import com.sapienter.jbilling.server.user.UserDTOEx
import com.sapienter.jbilling.server.notification.db.NotificationMessageDTO
import com.sapienter.jbilling.server.notification.db.NotificationMessageTypeDTO
import com.sapienter.jbilling.server.notification.db.NotificationMessageSectionDTO
import com.sapienter.jbilling.server.notification.db.NotificationMessageLineDTO
import com.sapienter.jbilling.server.user.UserBL
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskParameterDTO
import com.sapienter.jbilling.server.user.db.UserStatusDAS
import com.sapienter.jbilling.server.util.db.CurrencyDTO
import com.sapienter.jbilling.server.report.db.ReportDTO

/**
 * EntityDefaults 
 *
 * @author Brian Cowdery
 * @since 10/03/11
 */
class EntityDefaults {

    def UserDTO rootUser
    def CompanyDTO company
    def LanguageDTO language
    def JbillingTable entityTable
    def Locale locale

    def ReloadableResourceBundleMessageSource messageSource

    EntityDefaults(CompanyDTO company, UserDTO rootUser, LanguageDTO language, ReloadableResourceBundleMessageSource messageSource) {
        this.company = company
        this.rootUser = rootUser
        this.language = language
        this.entityTable = JbillingTable.findByName(Constants.TABLE_ENTITY)
        this.locale = UserBL.getLocale(rootUser)
        this.messageSource = messageSource
    }

    /**
     * Initialize the entity, creating the necessary preferences, plugins and other defaults.
     */
    def init() {
        // add company currency to the entity currency map
        // it's annoying that we need to build this association manually, it would be better mapped through CompanyDTO
        company.currency.entities_1 << company

        /*
            Order periods
         */
        def monthly = new OrderPeriodDTO(company: company, value: 1, periodUnit: new PeriodUnitDTO(Constants.PERIOD_UNIT_MONTH)).save()
        monthly.setDescription("Monthly", language.id)


        /*
            Ageing steps
         */
        def welcome = new AgeingEntityStepDTO(company: company, userStatus: new UserStatusDAS().find(UserDTOEx.STATUS_ACTIVE), days: 0).save()
        welcome.setDescription('welcome_message', language.id, getMessage('signup.default.welcome.message', [ company.description ]))


        /*
            Payment methods
         */
        PaymentMethodDTO.get(Constants.PAYMENT_METHOD_CHEQUE).entities << company
        PaymentMethodDTO.get(Constants.PAYMENT_METHOD_VISA).entities << company
        PaymentMethodDTO.get(Constants.PAYMENT_METHOD_MASTERCARD).entities << company


        /*
            Invoice delivery methods
         */
        InvoiceDeliveryMethodDTO.get(Constants.D_METHOD_EMAIL).entities << company
        InvoiceDeliveryMethodDTO.get(Constants.D_METHOD_PAPER).entities << company
        InvoiceDeliveryMethodDTO.get(Constants.D_METHOD_EMAIL_AND_PAPER).entities << company


        /*
            Reports
         */
        ReportDTO.list().each { report ->
            company.reports << report
        }


        /*
            Billing process configuration
         */
        new BillingProcessConfigurationDTO(
                entity: company,
                nextRunDate: new DateMidnight().plusMonths(1).toDate(),
                generateReport: 1,
                retries: 0,
                daysForRetry: 1,
                daysForReport: 3,
                reviewStatus: 1,
                dueDateUnitId: 1,
                dueDateValue: 1,
                onlyRecurring: 1,
                invoiceDateProcess: 0,
                autoPayment: 1,
                maximumPeriods: 1,
                autoPaymentApplication: 1
        ).save()


        /*
            Pluggable tasks
         */

        // PaymentFakeTask
        def paymentTask = new PluggableTaskDTO(entityId: company.id, type: new PluggableTaskTypeDTO(21), processingOrder: 1).save()
        new PluggableTaskParameterDTO(task: paymentTask, name: 'all', strValue: 'yes').save()

        // BasicEmailNotificationTask
        def emailTask = new PluggableTaskDTO(entityId: company.id, type: new PluggableTaskTypeDTO(9), processingOrder: 1).save()
        new PluggableTaskParameterDTO(task: emailTask, name: 'smtp_server', strValue: 'localhost').save()
        new PluggableTaskParameterDTO(task: emailTask, name: 'port', strValue: '25').save()
        new PluggableTaskParameterDTO(task: emailTask, name: 'ssl_auth', strValue: 'false').save()
        new PluggableTaskParameterDTO(task: emailTask, name: 'tls', strValue: 'false').save()
        new PluggableTaskParameterDTO(task: emailTask, name: 'username', strValue: 'username').save()
        new PluggableTaskParameterDTO(task: emailTask, name: 'password', strValue: 'password').save()

        // PaperInvoiceNotificationTask
        def notificationTask = new PluggableTaskDTO(entityId: company.id, type: new PluggableTaskTypeDTO(12), processingOrder: 2,).save()
        new PluggableTaskParameterDTO(task: notificationTask, name: 'design', strValue: 'simple_invoice_b2b').save()

        new PluggableTaskDTO(entityId: company.id, type: new PluggableTaskTypeDTO(1), processingOrder: 1).save()    // BasicLineTotalTask
        new PluggableTaskDTO(entityId: company.id, type: new PluggableTaskTypeDTO(3), processingOrder: 1).save()    // CalculateDueDate
        new PluggableTaskDTO(entityId: company.id, type: new PluggableTaskTypeDTO(4), processingOrder: 2).save()    // BasicCompositionTask
        new PluggableTaskDTO(entityId: company.id, type: new PluggableTaskTypeDTO(5), processingOrder: 1).save()    // BasicOrderFilterTask
        new PluggableTaskDTO(entityId: company.id, type: new PluggableTaskTypeDTO(6), processingOrder: 1).save()    // BasicInvoiceFilterTask
        new PluggableTaskDTO(entityId: company.id, type: new PluggableTaskTypeDTO(7), processingOrder: 1).save()    // BasicOrderPeriodTask
        new PluggableTaskDTO(entityId: company.id, type: new PluggableTaskTypeDTO(10), processingOrder: 1).save()   // BasicPaymentInfoTask
        new PluggableTaskDTO(entityId: company.id, type: new PluggableTaskTypeDTO(25), processingOrder: 1).save()   // NoAsyncParameters
        new PluggableTaskDTO(entityId: company.id, type: new PluggableTaskTypeDTO(79), processingOrder: 1).save()   // PriceModelPricingTask
        new PluggableTaskDTO(entityId: company.id, type: new PluggableTaskTypeDTO(28), processingOrder: 1).save()   // BasicItemManager
        new PluggableTaskDTO(entityId: company.id, type: new PluggableTaskTypeDTO(33), processingOrder: 1).save()   // RulesMediationTask
        new PluggableTaskDTO(entityId: company.id, type: new PluggableTaskTypeDTO(54), processingOrder: 1).save()   // DynamicBalanceManagerTask
        new PluggableTaskDTO(entityId: company.id, type: new PluggableTaskTypeDTO(82), processingOrder: 1).save()   // BillingProcessTask
        new PluggableTaskDTO(entityId: company.id, type: new PluggableTaskTypeDTO(88), processingOrder: 2).save()   // AgeingProcessTask
        new PluggableTaskDTO(entityId: company.id, type: new PluggableTaskTypeDTO(87), processingOrder: 1).save()   // BasicAgeingTask
		new PluggableTaskDTO(entityId: company.id, type: new PluggableTaskTypeDTO(69), processingOrder: 1).save()    // BasicBillingProcessFilterTask
		

        /*
            Preferences
         */
        new PreferenceDTO(jbillingTable: entityTable, foreignId: company.id, preferenceType: new PreferenceTypeDTO(Constants.PREFERENCE_GRACE_PERIOD), value: 5).save()
        new PreferenceDTO(jbillingTable: entityTable, foreignId: company.id, preferenceType: new PreferenceTypeDTO(Constants.PREFERENCE_SHOW_NOTE_IN_INVOICE), value: 1).save()


        /*
            Notification messages
         */
        createNotificationMessage(Constants.NOTIFICATION_TYPE_INVOICE_EMAIL, 'signup.notification.email.title', 'signup.notification.email')
        createNotificationMessage(Constants.NOTIFICATION_TYPE_USER_REACTIVATED, 'signup.notification.user.reactivated.title', 'signup.notification.user.reactivated')
        createNotificationMessage(Constants.NOTIFICATION_TYPE_USER_OVERDUE, 'signup.notification.overdue.title', 'signup.notification.overdue')
        createNotificationMessage(Constants.NOTIFICATION_TYPE_ORDER_EXPIRE_1, 'signup.notification.order.expire.1.title', 'signup.notification.order.expire.1')
        createNotificationMessage(Constants.NOTIFICATION_TYPE_PAYMENT_SUCCESS, 'signup.notification.payment.success.title', 'signup.notification.payment.success')
        createNotificationMessage(Constants.NOTIFICATION_TYPE_PAYMENT_FAILED, 'signup.notification.payment.failed.title', 'signup.notification.payment.failed')
        createNotificationMessage(Constants.NOTIFICATION_TYPE_INVOICE_REMINDER, 'signup.notification.invoice.reminder.title', 'signup.notification.invoice.reminder')
        createNotificationMessage(Constants.NOTIFICATION_TYPE_CREDIT_CARD_UPDATE, 'signup.notification.credit.card.update.title', 'signup.notification.credit.card.update')
    }

    /**
     * Create a new, 2 section notification message for the given type id and messages. Messages are
     * resolved from the grails 'messages.properties' bundle.
     *
     * @param typeId notification type id
     * @param titleCode message code for the notification message title
     * @param bodyCode message code for the notification message body
     */
    def createNotificationMessage(Integer typeId, String titleCode, String bodyCode) {
        def message = new NotificationMessageDTO(
                entity: company,
                language: language,
                useFlag: 1,
                notificationMessageType: new NotificationMessageTypeDTO(id: typeId)
        )

        def titleSection = new NotificationMessageSectionDTO(notificationMessage: message, section: 1)
        titleSection.notificationMessageLines << new NotificationMessageLineDTO(notificationMessageSection: titleSection, content: getMessage(titleCode))
        message.notificationMessageSections << titleSection

        def bodySection = new NotificationMessageSectionDTO(notificationMessage: message, section: 2)
        bodySection.notificationMessageLines << new NotificationMessageLineDTO(content: getMessage(bodyCode))
        message.notificationMessageSections << bodySection

        message.save()
    }

    def String getMessage(String code) {
        return messageSource.getMessage(code, new Object[0], code, locale)
    }

    def String getMessage(String code, Object[] args) {
        return messageSource.getMessage(code, args, code, locale)
    }

}
