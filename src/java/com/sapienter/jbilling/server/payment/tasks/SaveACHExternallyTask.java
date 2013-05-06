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

package com.sapienter.jbilling.server.payment.tasks;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.server.payment.IExternalCreditCardStorage;
import com.sapienter.jbilling.server.pluggableTask.PluggableTask;
import com.sapienter.jbilling.server.pluggableTask.admin.ParameterDescription;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskBL;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskException;
import com.sapienter.jbilling.server.system.event.Event;
import com.sapienter.jbilling.server.system.event.task.IInternalEventsTask;
import com.sapienter.jbilling.server.user.db.AchDAS;
import com.sapienter.jbilling.server.user.db.AchDTO;
import com.sapienter.jbilling.server.user.event.AchDeleteEvent;
import com.sapienter.jbilling.server.user.event.AchUpdateEvent;
import org.apache.log4j.Logger;

import static com.sapienter.jbilling.server.pluggableTask.admin.ParameterDescription.Type.*;

/**
 * @author Brian Cowdery
 * @since 14-09-2010
 */
public class SaveACHExternallyTask extends PluggableTask implements IInternalEventsTask {
    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(SaveCreditCardExternallyTask.class));

    private static final ParameterDescription PARAM_CONTACT_TYPE = new ParameterDescription("contactType", false, INT);
    private static final ParameterDescription PARAM_EXTERNAL_SAVING_PLUGIN_ID = new ParameterDescription("externalSavingPluginId", true, INT);
    private static final ParameterDescription PARAM_OBSCURE_ON_FAIL = new ParameterDescription("obscureOnFail", false, BOOLEAN);

    private static final boolean DEFAULT_OBSCURE_ON_FAIL = false;

    //initializer for pluggable params
    {
    	descriptions.add(PARAM_CONTACT_TYPE);
        descriptions.add(PARAM_EXTERNAL_SAVING_PLUGIN_ID);
        descriptions.add(PARAM_OBSCURE_ON_FAIL);
    }

    private Integer contactType;
    private Integer externalSavingPluginId;

    @SuppressWarnings("unchecked")
    private static final Class<Event> events[] = new Class[] {            
            AchUpdateEvent.class,
            AchDeleteEvent.class
    };

    public Class<Event>[] getSubscribedEvents() { return events; }

    /**
     * Returns the configured contact type as an integer.
     *
     * @return contact type
     * @throws com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskException if type cannot be converted to an integer
     */
    public Integer getContactType() throws PluggableTaskException {
        if (contactType == null) {
            try {
                if (parameters.get(PARAM_CONTACT_TYPE.getName()) == null) {
                    contactType = 1; // default if not configured
                } else {
                    contactType = Integer.parseInt(parameters.get(PARAM_CONTACT_TYPE.getName()));
                }
            } catch (NumberFormatException e) {
                throw new PluggableTaskException("Configured contactType must be an integer!", e);
            }
        }
        return contactType;
    }

    /**
     * Returns the configured external saving event plugin id ({@link com.sapienter.jbilling.server.payment.IExternalCreditCardStorage})
     * as an integer.
     *
     * @return plugin id of the configured external saving event plugin
     * @throws PluggableTaskException if id cannot be converted to an integer
     */
    public Integer getExternalSavingPluginId() throws PluggableTaskException {
        if (externalSavingPluginId == null) {
            try {
                externalSavingPluginId = Integer.parseInt(parameters.get(PARAM_EXTERNAL_SAVING_PLUGIN_ID.getName()));
            } catch (NumberFormatException e) {
                throw new PluggableTaskException("Configured externalSavingPluginId must be an integer!", e);
            }
        }
        return externalSavingPluginId;
    }

    /**
     * @see IInternalEventsTask#process(com.sapienter.jbilling.server.system.event.Event)
     *
     * @param event event to process
     * @throws PluggableTaskException
     */
    public void process(Event event) throws PluggableTaskException {
        PluggableTaskBL<IExternalCreditCardStorage> ptbl = new PluggableTaskBL<IExternalCreditCardStorage>(getExternalSavingPluginId());
        IExternalCreditCardStorage externalCCStorage = ptbl.instantiateTask();

        if (event instanceof AchUpdateEvent) {
            LOG.debug("Processing AchUpdateEvent ...");
            AchUpdateEvent ev = (AchUpdateEvent) event;
            String gateWayKey = externalCCStorage.storeCreditCard(null, null, ev.getAch());
            updateAch(ev.getAch(), gateWayKey);
        } else if (event instanceof AchDeleteEvent) {
            LOG.debug("Processing AchDeleteEvent ...");
            AchDeleteEvent ev = (AchDeleteEvent) event;
            String gateWayKey = externalCCStorage.deleteCreditCard(null, null, ev.getAch());
            deleteAch(ev.getAch(), gateWayKey);
        } else {
            throw new PluggableTaskException("Cant not process event " + event);
        }
    }

    /**
     * Update the ACH object with the given gateway key.
     *
     * @param ach - ACH object to update
     * @param gatewayKey gateway key from external storage, null if storage failed.
     */
    private void updateAch(AchDTO ach, String gatewayKey) {
        if (gatewayKey != null) {
            LOG.debug("Storing ach gateway key: " + gatewayKey);
            ach.setGatewayKey(gatewayKey);
            ach.obscureBankAccount();
            new AchDAS().makePersistent(ach);
        } else {
            if (getParameter(PARAM_OBSCURE_ON_FAIL.getName(), DEFAULT_OBSCURE_ON_FAIL)) {
                ach.obscureBankAccount();
                new AchDAS().makePersistent(ach);
                LOG.warn("gateway key returned from external store is null, obscuring ach with no key");
            } else {
                LOG.warn("gateway key returned from external store is null, ach will not be obscured!");
            }
        }
    }

    /**
     * Delete the ACH Object
     * @param ach
     * @param gatewayKey
     */
    private void deleteAch(AchDTO ach, String gatewayKey) {
        if (gatewayKey == null) {
            LOG.debug("Failed to delete the ACH Record - gateway key returned null." );
        }
    }
}
