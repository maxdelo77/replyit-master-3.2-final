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
package com.sapienter.jbilling.server.mediation;

import java.io.File;
import java.util.*;

import javax.persistence.EntityNotFoundException;

import com.sapienter.jbilling.server.item.PricingField;
import com.sapienter.jbilling.server.mediation.db.MediationRecordStatusDAS;
import com.sapienter.jbilling.server.mediation.db.MediationRecordStatusDTO;
import com.sapienter.jbilling.server.mediation.task.*;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskException;
import com.sapienter.jbilling.server.process.ProcessStatusWS;
import org.apache.log4j.Logger;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.common.InvalidArgumentException;
import com.sapienter.jbilling.common.SessionInternalError;
import com.sapienter.jbilling.server.mediation.db.MediationConfiguration;
import com.sapienter.jbilling.server.mediation.db.MediationConfigurationDAS;
import com.sapienter.jbilling.server.mediation.db.MediationMapDAS;
import com.sapienter.jbilling.server.mediation.db.MediationOrderMap;
import com.sapienter.jbilling.server.mediation.db.MediationProcess;
import com.sapienter.jbilling.server.mediation.db.MediationProcessDAS;
import com.sapienter.jbilling.server.mediation.db.MediationRecordDAS;
import com.sapienter.jbilling.server.mediation.db.MediationRecordDTO;
import com.sapienter.jbilling.server.mediation.db.MediationRecordLineDAS;
import com.sapienter.jbilling.server.mediation.db.MediationRecordLineDTO;
import com.sapienter.jbilling.server.order.db.OrderLineDAS;
import com.sapienter.jbilling.server.order.db.OrderLineDTO;
import com.sapienter.jbilling.server.pluggableTask.TaskException;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskBL;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskDAS;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskDTO;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskManager;
import com.sapienter.jbilling.server.user.EntityBL;
import com.sapienter.jbilling.server.util.Constants;
import com.sapienter.jbilling.server.util.Context;
import com.sapienter.jbilling.server.util.audit.EventLogger;

import org.springframework.util.StopWatch;

/**
 *
 * @author emilc
 **/
@Transactional( propagation = Propagation.REQUIRED )
public class MediationSessionBean implements IMediationSessionBean {
    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(MediationSessionBean.class));

    /**
     * Triggers all the mediation process for all configurations.
     *
     * @param entityId entity id
     * @see MediationSessionBean#triggerMediationByConfiguration(Integer, Integer)
     */
    public void trigger(Integer entityId) {
        LOG.debug("Running mediation trigger for entity %s", entityId);
        StopWatch watch = new StopWatch("trigger watch");
        watch.start();

        // local instance of this bean to invoke transactional methods
        IMediationSessionBean local = Context.getBean(Context.Name.MEDIATION_SESSION);

        List<String> errorMessages = new ArrayList<String>();

        // process each mediation configuration for this entity
        for (MediationConfiguration cfg : local.getAllConfigurations(entityId)) {
            try {
                local.triggerMediationByConfiguration(cfg.getId(), entityId);
            } catch (Exception ex) {
                LOG.error("Exception occurred triggering mediation configuration %s", cfg.getId(), ex);
                errorMessages.add(ex.getMessage());
            }
        }

        // throw a SessionInternalError of errors were returned from the configuration run (possible plugin errors)
        if (!errorMessages.isEmpty()) {
            StringBuilder builder = new StringBuilder("Errors during mediation triggering: \n");
            for (String message : errorMessages) {
                builder.append(message).append('\n');
            }
            throw new SessionInternalError(builder.toString());
        }

        watch.stop();
        LOG.debug("Mediation process finished running. Duration (ms):%s", watch.getTotalTimeMillis());
    }

    public Integer triggerMediationByConfiguration(final Integer configId, final Integer entityId) {
        return triggerMediationByConfigurationWithDataInjection(configId, entityId, null, null);
    }

    public Integer triggerMediationByConfigurationWithFileInjection(Integer configId, Integer entityId, File eventFile) {
        return triggerMediationByConfigurationWithDataInjection(configId, entityId, eventFile, null);
    }

    public Integer  triggerMediationByConfigurationWithRecordsInjection(Integer configId, Integer entityId,
                                                                       List<String> encodedEventRecords) {

        List<Record> eventRecords = new ArrayList<Record>();

        for (String encodedRecord : encodedEventRecords) {

            String clearEncodedRecord = encodedRecord.replace("\"", "");
            Record record = new Record();
            // record key is at the last position
            String [] lines = clearEncodedRecord.split(",");
            List<String> recordKeys = Arrays.asList(lines[lines.length - 1].split(":"));

            if (recordKeys.isEmpty()) {
                throw new SessionInternalError("No records keys found in encoded event record: " + clearEncodedRecord);
            }

            // decode pricing fields
            PricingField[] pricingFields = PricingField.getPricingFieldsValue(clearEncodedRecord);

            for (PricingField field : pricingFields) {
                record.addField(field, recordKeys.contains(field.getStrValue()));
            }
            eventRecords.add(record);
        }

        return triggerMediationByConfigurationWithDataInjection(configId, entityId, null, Arrays.asList(eventRecords));
    }

    /**
     * Triggers the mediation process for a specific configuration with data injection
     *
     * Only one mediation process can be run for a configuration at a time. Multiple configurations can be
     * run, asynchronously but we do not allow the same configuration to overlap.
     *
     * @param configId configuration id to run
     * @param entityId entity id
     * @return running mediation process id
     */
    private Integer triggerMediationByConfigurationWithDataInjection(final Integer configId, final Integer entityId,
                                                                     final File eventFile, final List<List<Record>> eventRecords) {

        LOG.debug("Running mediation trigger for entity " + entityId + " and configuration " + configId +
            " Injected data: event file: " + eventFile + " records: " + eventRecords);

        // get the local bean & DAS early on to prevent delays recording process start
        IMediationSessionBean local = Context.getBean(Context.Name.MEDIATION_SESSION);
        MediationProcessDAS mediationDAS = new MediationProcessDAS();

        // get the mediation configuration to run
        MediationConfiguration cfg = getMediationConfiguration(configId);
        if (cfg == null || !cfg.getEntityId().equals(entityId)) {
            LOG.error("Mediation configuration " + configId + " does not exists!");
            return null;
        }


        /*
            There can only be one process running for this entity, check that there is
            no other mediation process running for this entity before continuing.
         */
        if (mediationDAS.isConfigurationProcessing(cfg.getId())) {
            LOG.debug("Entity %s already has a running mediation process for configuration %s, skipping run", entityId , configId);
            return null;
        }

        // fetch mediation processing plug in (usually a rules based processor)
        final IMediationProcess processTask;
        try {
            PluggableTaskBL<IMediationProcess> pluggableTaskBL= new PluggableTaskBL<IMediationProcess>();
            pluggableTaskBL.set(cfg.getProcessor());
            processTask= pluggableTaskBL.instantiateTask();
        } catch (PluggableTaskException e) {
            throw new SessionInternalError("Could not retrieve mediation process plug-in.", e);
        }

        if (processTask == null) {
            LOG.debug("Entity %s does not have a mediation process plug-in", entityId);
            return null;
        }


        /*
            Double check that we're still the only mediation process running for configuration before we create
            a new MediationProcess record. The mediation processing plug-in may have a long
            instantiation time which leaves a window for another overlapping process to be created.
         */
        if (mediationDAS.isConfigurationProcessing(cfg.getId())) {
            LOG.debug("Entity %s already has an existing mediation process for configuration %s, skipping run", 
                    entityId, configId);
            return null;
        }

        final Integer processId = local.createProcessRecord(cfg).getId(); // create process record and mark start time
        final Integer executorId = new EntityBL().getRootUser(entityId); // root user of this entity to be used for order updates

        // run in separate thread
        Thread mediationThread = new Thread(new Runnable() {
            IMediationSessionBean local = (IMediationSessionBean) Context.getBean(Context.Name.MEDIATION_SESSION);
            public void run() {
                local.performMediation(processTask, configId, processId, executorId, entityId, eventFile, eventRecords);
            }
        });
        mediationThread.start();

        return processId;
    }

    /**
     * Perform the actual mediation by instantiating the reader plug-in for the configuration, reading in the
     * records and processing them with the given {@link IMediationProcess} task.
     *
     * @param processTask process task plug-in to use for processing records
     * @param configurationId mediation configuration id to run
     * @param processId process id to attach to
     * @param executorId user id to use for database updates
     * @param entityId entity id
     * @param eventFile injected event File
     * @param eventRecords injected event records
     */
    public void performMediation(IMediationProcess processTask, Integer configurationId, Integer processId,
                                 Integer executorId, Integer entityId, File eventFile, List<List<Record>> eventRecords) {

        MediationConfiguration cfg = new MediationConfigurationDAS().find(configurationId);
        MediationProcessDAS processDAS = new MediationProcessDAS();

        MediationProcess process = processDAS.find(processId);
        // the process needs to be detached from this session
        processDAS.detach(process);

        Iterator<List<Record>> mediationRecordIterator = null;
        IMediationReader reader = null;

        // process injected event records first
        if (eventRecords != null && !eventRecords.isEmpty()) {
            mediationRecordIterator = eventRecords.iterator();
        } else {
            try {
                // fetch mediation reader plug-in
                PluggableTaskBL<IMediationReader> readerTask = new PluggableTaskBL<IMediationReader>();
                readerTask.set(cfg.getPluggableTask());
                reader = readerTask.instantiateTask();
            } catch (PluggableTaskException e) {
                throw new SessionInternalError("Could not instantiate mediation reader plug-in.", e);
            }
        }

        // local instance of this bean to invoke transactional methods
        IMediationSessionBean local = Context.getBean(Context.Name.MEDIATION_SESSION);
        List<String> errorMessages = new ArrayList<String>();

        try {
            // try with mediation reader if there are no injected event records
            if (mediationRecordIterator == null) {

                // process injected event file first
                if (eventFile != null) {
                    if (reader instanceof AbstractFileReader) {
                        AbstractFileReader fileReader = (AbstractFileReader) reader;
                        // override "directory" and "suffix" reader params
                        fileReader.overrideFileReaderParameters(eventFile.getParent(), eventFile.getName());
                    }
                }

                if (reader.validate(errorMessages)) {
                    mediationRecordIterator = reader.iterator();
                }
            }

            if (mediationRecordIterator != null) {
                /*
                   Catch exceptions and log errors instead of re-throwing as SessionInternalError
                   so that the remaining mediation configurations can be run, and so that this
                   process can be "completed" by setting the end date.
                */
                try {
                    StopWatch stopWatch = new StopWatch();
                    stopWatch.start("Reading records");
                    while (mediationRecordIterator.hasNext()) {
                        List<Record> thisGroup = mediationRecordIterator.next();
                        stopWatch.stop();
                        LOG.debug("Now processing %s records.", thisGroup.size());
                        local.normalizeRecordGroup(processTask, executorId, process, thisGroup, entityId, cfg);
                        LOG.debug(stopWatch.prettyPrint());
                        stopWatch = new StopWatch();
                        stopWatch.start("Reading records");
                    }
                    stopWatch.stop();
                } catch (TaskException e) {
                    LOG.error("Exception occurred processing mediation records.", e);
                } catch (Throwable t) {
                    LOG.error("Unhandled exception occurred during mediation.", t);
                }
            }
        } finally {
            // process should be "ended' anyway
            // mark process end date
            local.updateProcessRecord(process, new Date());
            LOG.debug("Configuration '" + cfg.getName() + "' finished at " + process.getEndDatetime());

            // delete the temp event file
            if (eventFile != null) {
                eventFile.delete();
            }
        }

        // throw a SessionInternalError of errors were returned from the reader plug-in
        if (!errorMessages.isEmpty()) {
            StringBuilder builder = new StringBuilder("Invalid reader plug-in configuration \n");
            for (String message : errorMessages) {
                builder.append("ERROR: ")
                    .append(message)
                    .append('\n');
            }
            throw new SessionInternalError(builder.toString());
        }
    }


    /**
     * Create a new MediationProcess for the given configuration, marking the
     * start time of the process and initializing the affected order count.
     *
     * @param cfg mediation configuration
     * @return new MediationProcess record
     */
    @Transactional( propagation = Propagation.REQUIRES_NEW )
    public MediationProcess createProcessRecord(MediationConfiguration cfg) {
        MediationProcessDAS processDAS = new MediationProcessDAS();
        MediationProcess process = new MediationProcess();
        process.setConfiguration(cfg);
        process.setStartDatetime(Calendar.getInstance().getTime());
        process.setRecordsAffected(0);
        process = processDAS.save(process);
        processDAS.flush();

        return process;
    }

    /**
     * Updated the end time of the given MediationProcess, effectively marking
     * the process as completed.
     *
     * @param process MediationProcess to update
     * @param enddate end time to set
     * @return updated MediationProcess record
     */
    @Transactional( propagation = Propagation.REQUIRES_NEW )
    public MediationProcess updateProcessRecord(MediationProcess process, Date enddate) {
        new MediationProcessDAS().reattach(process);
        process.setEndDatetime(enddate);
        return process;
    }

    /**
     * Returns true if a running MediationProcess exists for the given entity id. A
     * process is considered to be running if it does not have an end time.
     *
     * @param entityId entity id to check
     * @return true if a process is running for the given entity, false if not
     */
    @Transactional( propagation = Propagation.REQUIRES_NEW )
    public boolean isMediationProcessRunning(Integer entityId) {
        return new MediationProcessDAS().isProcessing(entityId);
    }

    public ProcessStatusWS getMediationProcessStatus(Integer entityId) {
        MediationProcessDAS processDAS = new MediationProcessDAS();
        MediationProcess process = processDAS.getLatestMediationProcess(entityId);
        if (process == null) {
            return null;
        } else {
            ProcessStatusWS result = new ProcessStatusWS();
            result.setStart(process.getStartDatetime());
            result.setEnd(process.getEndDatetime());
            result.setProcessId(process.getId());
            if (process.getEndDatetime() == null) {
                result.setState(ProcessStatusWS.State.RUNNING);
            } else if (processDAS.isMediationProcessHasFailedRecords(process.getId())) {
                result.setState(ProcessStatusWS.State.FAILED);
            } else {
                result.setState(ProcessStatusWS.State.FINISHED);
            }
            return result;
        }
    }

    public List<MediationProcess> getAll(Integer entityId) {
        MediationProcessDAS processDAS = new MediationProcessDAS();
        List<MediationProcess> result = processDAS.findAllByEntity(entityId);
        processDAS.touch(result);
        return result;

    }

    /**
     * Returns a list of all MediationConfiguration's for the given entity id.
     *
     * @param entityId entity id
     * @return list of mediation configurations for entity, empty list if none found
     */
    public List<MediationConfiguration> getAllConfigurations(Integer entityId) {
        MediationConfigurationDAS cfgDAS = new MediationConfigurationDAS();
        return cfgDAS.findAllByEntity(entityId);
    }

    protected MediationConfiguration getMediationConfiguration(Integer configurationId) {
        MediationConfigurationDAS cfgDAS = new MediationConfigurationDAS();
        return cfgDAS.find(configurationId);
    }

    public void createConfiguration(MediationConfiguration cfg) {
        MediationConfigurationDAS cfgDAS = new MediationConfigurationDAS();

        cfg.setCreateDatetime(Calendar.getInstance().getTime());
        cfgDAS.save(cfg);

    }

    public List<MediationConfiguration> updateAllConfiguration(Integer executorId, List<MediationConfiguration> configurations)
            throws InvalidArgumentException {
        MediationConfigurationDAS cfgDAS = new MediationConfigurationDAS();
        List<MediationConfiguration> retValue = new ArrayList<MediationConfiguration>();
        try {

            for (MediationConfiguration cfg : configurations) {
                // if the configuration is new, the task needs to be loaded
                if (cfg.getPluggableTask().getEntityId() == null) {
                    PluggableTaskDAS pt = (PluggableTaskDAS) Context.getBean(Context.Name.PLUGGABLE_TASK_DAS);
                    PluggableTaskDTO task = pt.find(cfg.getPluggableTask().getId());
                    if (task != null && task.getEntityId().equals(cfg.getEntityId())) {
                        cfg.setPluggableTask(task);
                    } else {
                        throw new InvalidArgumentException("Task not found or " +
                                "entity of pluggable task is not the same when " +
                                "creating a new mediation configuration", 1);
                    }
                }
                retValue.add(cfgDAS.save(cfg));
            }
            return retValue;
        } catch (EntityNotFoundException e1) {
            throw new InvalidArgumentException("Wrong data saving mediation configuration", 1, e1);
        } catch (InvalidArgumentException e2) {
            throw new InvalidArgumentException(e2);
        } catch (Exception e) {
            throw new SessionInternalError("Exception updating mediation configurations ", MediationSessionBean.class, e);
        }
    }

    public void delete(Integer executorId, Integer cfgId) {
        MediationConfigurationDAS cfgDAS = new MediationConfigurationDAS();

        cfgDAS.delete(cfgDAS.find(cfgId));
        EventLogger.getInstance().audit(executorId, null,
                                        Constants.TABLE_MEDIATION_CFG, cfgId,
                                        EventLogger.MODULE_MEDIATION, EventLogger.ROW_DELETED, null,
                                        null, null);
    }

    /**
     * Calculation number of records for each of the existing mediation record statuses
     *
     * @param entityId EntityId for searching mediationRecords
     * @return map of mediation status as a key and long value as a number of records whit given status
     */
    public Map<MediationRecordStatusDTO, Long> getNumberOfRecordsByStatuses(Integer entityId, Integer mediationProcessId) {
        MediationRecordDAS recordDas = new MediationRecordDAS();
        MediationRecordStatusDAS recordStatusDas = new MediationRecordStatusDAS();
        Map<MediationRecordStatusDTO, Long> resultMap = new HashMap<MediationRecordStatusDTO, Long>();
        List<MediationRecordStatusDTO> statuses = recordStatusDas.findAll();

        //propagate proxy objects for using out of the transaction
        recordStatusDas.touch(statuses);
        for (MediationRecordStatusDTO status : statuses) {
            Long recordsCount = mediationProcessId != null ?
                    recordDas.countMediationRecordsByEntityIdMediationProcessAndStatus(entityId, status, mediationProcessId) :
                    recordDas.countMediationRecordsByEntityIdAndStatus(entityId, status);
            resultMap.put(status, recordsCount);
        }
        return resultMap;
    }

    public boolean hasBeenProcessed(MediationProcess process, Record record) {
        MediationRecordDAS recordDas = new MediationRecordDAS();

        // validate that this group has not been already processed
        if (recordDas.processed(record.getKey())) {
            LOG.debug("Detected duplicated of record: %s", record.getKey());
            return true;
        }
        LOG.debug("Detected record as a new event: %s",record.getKey());

        // assign to record DONE_AND_BILLABLE status as default before processing
        // after actual processing it will be updated
        MediationRecordStatusDTO status = new MediationRecordStatusDAS().find(Constants.MEDIATION_RECORD_STATUS_DONE_AND_BILLABLE);
        MediationRecordDTO dbRecord = new MediationRecordDTO(record.getKey(),
                                                             Calendar.getInstance().getTime(),
                                                             process,
                                                             status);
        recordDas.save(dbRecord);
        recordDas.flush();

        return false;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void normalizeRecordGroup(IMediationProcess processTask, Integer executorId,
                                     MediationProcess process, List<Record> thisGroup, Integer entityId,
                                     MediationConfiguration cfg) throws TaskException {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start("Pre-processing");

        LOG.debug("Normalizing %s records ...",thisGroup.size());

        // this process came from a different transaction (persistent context)
        new MediationProcessDAS().reattachUnmodified(process);

        // validate that these records have not been already processed
        for (Iterator<Record> it = thisGroup.iterator(); it.hasNext();) {
            if (hasBeenProcessed(process, it.next())) it.remove();
        }

        if (thisGroup.size() == 0) {
            return; // it could be that they all have been processed already
        }

        ArrayList<MediationResult> results = new ArrayList<MediationResult>(0);

        // call the plug-in to resolve these records
        stopWatch.stop();
        stopWatch.start("Processing");
        processTask.process(thisGroup, results, cfg.getName());
        stopWatch.stop();
        stopWatch.start("Post-Processing");

        LOG.debug("Processing %s records took: %s ms, or %s records/sec", thisGroup.size()
                ,stopWatch.getLastTaskTimeMillis(), 
                new Double(thisGroup.size()) / stopWatch.getLastTaskTimeMillis() * 1000D);

        // go over the results
        for (MediationResult result : results) {
            if (!result.isDone()) {
                // this is an error, the rules failed somewhere because the
                // 'done' flag is still false.
                LOG.debug("Record result is not done");

                // errors presented, status of record should be updated
                assignStatusToMediationRecord(result.getRecordKey(),
                                              new MediationRecordStatusDAS().find(Constants.MEDIATION_RECORD_STATUS_ERROR_DETECTED));

                // call error handler for mediation errors
                handleMediationErrors(findRecordByKey(thisGroup, result.getRecordKey()),
                                      resolveMediationResultErrors(result),
                                      entityId, process);

            } else if (!result.getErrors().isEmpty()) {
                // There are some user-detected errors
                LOG.debug("Record result is done with errors");

                //done, but errors assigned by rules. status of record should be updated
                assignStatusToMediationRecord(result.getRecordKey(),
                                              new MediationRecordStatusDAS().find(Constants.MEDIATION_RECORD_STATUS_ERROR_DECLARED));
                // call error handler for rules errors
                handleMediationErrors(findRecordByKey(thisGroup, result.getRecordKey()),
                                      result.getErrors(),
                                      entityId, process);
            } else {
                // this record was process without any errors
                LOG.debug("Record result is done");

                if (result.getLines() == null || result.getLines().isEmpty()) {
                    //record was processed, but order lines was not affected
                    //now record has status DONE_AND_BILLABLE, it should be changed
                    assignStatusToMediationRecord(result.getRecordKey(),
                                                  new MediationRecordStatusDAS().find(Constants.MEDIATION_RECORD_STATUS_DONE_AND_NOT_BILLABLE));
                    //not needed to update order affected or lines in this case

                } else {
                    //record has status DONE_AND_BILLABLE, only needed to save processed lines
                    process.setRecordsAffected(process.getRecordsAffected() + result.getLines().size());

                    // relate this order with this process
                    MediationOrderMap map = new MediationOrderMap();
                    map.setMediationProcessId(process.getId());
                    map.setOrderId(result.getCurrentOrder().getId());

                    MediationMapDAS mapDas = new MediationMapDAS();
                    mapDas.save(map);

                    // add the record lines
                    // todo: could be problematic if asynchronous mediation processes are running.
                    // a better approach is to link MediationResult to the record by the unique ID -- future enhancement
                    saveEventRecordLines(result.getDiffLines(), new MediationRecordDAS().findNewestByKey(result.getRecordKey()),
                                         result.getEventDate(),
                                         result.getDescription());
                }
            }
        }

        stopWatch.stop();
    }

    public void saveEventRecordLines(List<OrderLineDTO> newLines, MediationRecordDTO record, Date eventDate,
                                     String description) {

        MediationRecordLineDAS mediationRecordLineDas = new MediationRecordLineDAS();

        for (OrderLineDTO line : newLines) {
            MediationRecordLineDTO recordLine = new MediationRecordLineDTO();

            recordLine.setEventDate(eventDate);
            OrderLineDTO dbLine = new OrderLineDAS().find(line.getId());
            recordLine.setOrderLine(dbLine);
            recordLine.setAmount(line.getAmount());
            recordLine.setQuantity(line.getQuantity());
            recordLine.setRecord(record);
            recordLine.setDescription(description);

            recordLine = mediationRecordLineDas.save(recordLine);
            // no need to link to the parent record. The association is completed already
            // record.getLines().add(recordLine);
        }
    }

    public List<MediationRecordLineDTO> getMediationRecordLinesForOrder(Integer orderId) {
        List<MediationRecordLineDTO> events = new MediationRecordLineDAS().findByOrder(orderId);
        for (MediationRecordLineDTO line : events) {
            line.toString(); //as a touch
        }
        return events;
    }

    public List<MediationRecordLineDTO> getMediationRecordLinesForInvoice(Integer invoiceId) {
        return new MediationRecordLineDAS().findByInvoice(invoiceId);
    }

    public List<MediationRecordDTO> getMediationRecordsByMediationProcess(Integer mediationProcessId) {
        return new MediationRecordDAS().findByProcess(mediationProcessId);
    }

    public List<MediationRecordDTO> getMediationRecordsByMediationProcessAndStatus(
            Integer mediationProcessId, Integer mediationRecordStatusId) {

        return new MediationRecordDAS().findByProcessAndStatuses(mediationProcessId,
                Arrays.asList(mediationRecordStatusId));
    }

    public List<MediationErrorRecordWS> getMediationErrorRecordsByMediationProcess(
            Integer entityId, Integer mediationProcessId, Integer mediationRecordStatusId) {

        MediationProcess mediationProcess = new MediationProcessDAS().find(mediationProcessId);
        Map<Record, List<String>> mediationErrorRecordsFromErrorHandler = null;

        StopWatch watch = new StopWatch("getting errors watch");
        watch.start();
        LOG.debug("Getting mediation result errors for mediation process: " + mediationProcess.getId()) ;

        try {
            PluggableTaskManager<IMediationErrorHandler> tm = new PluggableTaskManager<IMediationErrorHandler>(entityId,
                    Constants.PLUGGABLE_TASK_MEDIATION_ERROR_HANDLER);
            IMediationErrorHandler errorHandler;
            // iterate through all error handlers for current entityId
            // and try to find the error records
            while ((errorHandler = tm.getNextClass()) != null) {
                try {
                    mediationErrorRecordsFromErrorHandler = errorHandler.retrieveErrorRecords(
                            mediationProcess.getStartDatetime(), mediationProcess);
                    // if error records found exist
                    if (mediationErrorRecordsFromErrorHandler != null
                            && !mediationErrorRecordsFromErrorHandler.isEmpty()) {
                        break;
                    }
                } catch (TaskException e) {
                    // exception catched for opportunity of processing errors by other handlers
                    // and continue mediation process for other records
                    // TO-DO: check requirements about error handling in that case
                    LOG.error(e);
                }
            }

        } catch (PluggableTaskException e) {
            LOG.error(e);
            // it's possible plugin configuration exception
            // TO-DO: check requirements about error handling
            // may be rethrow exception
        }

        watch.stop();
        LOG.debug("Getting mediation result errors done. Duration (mls):" + watch.getTotalTimeMillis());

        if (mediationErrorRecordsFromErrorHandler == null || mediationErrorRecordsFromErrorHandler.isEmpty()) {
            LOG.warn("Mediation error records not found in any of the configured plugins. " +
                    "No plugins configured or error in processing ?");
            return Collections.emptyList();
        }

        List<Integer> statuses = mediationRecordStatusId != null ?
                Arrays.asList(mediationRecordStatusId) :
                Arrays.asList(Constants.MEDIATION_RECORD_STATUS_ERROR_DETECTED,
                Constants.MEDIATION_RECORD_STATUS_ERROR_DECLARED);


        List<MediationRecordDTO> mediationErrorRecordsFromDb = new MediationRecordDAS().findByProcessAndStatuses(
                mediationProcessId, statuses);

        List<MediationErrorRecordWS> mediationErrorRecordsWS = new ArrayList<MediationErrorRecordWS>();

        for (MediationRecordDTO dto : mediationErrorRecordsFromDb) {

            MediationErrorRecordWS mediationErrorRecordWS = null;
            for (Map.Entry<Record, List<String>> entry : mediationErrorRecordsFromErrorHandler.entrySet()) {
                Record record = entry.getKey();
                if (record.getKey().equalsIgnoreCase(dto.getKey())) {
                    mediationErrorRecordWS = new MediationErrorRecordWS(dto, record.getFields(), entry.getValue());
                    break;
                }
            }

            if (mediationErrorRecordWS != null) {
                mediationErrorRecordsWS.add(mediationErrorRecordWS);
            } else {
                LOG.warn("Mediation error record not found in error handler: " + dto);
            }

        }

        return mediationErrorRecordsWS;
    }


    private void assignStatusToMediationRecord(String key, MediationRecordStatusDTO status) {
        MediationRecordDAS recordDas = new MediationRecordDAS();
        MediationRecordDTO recordDto = recordDas.findNewestByKey(key);
        if (recordDto != null) {
            recordDto.setRecordStatus(status);
            recordDas.save(recordDto);
        } else {
            LOG.debug("Mediation record with key=%s not found", key);
        }
    }

    private List<String> resolveMediationResultErrors(MediationResult result) {
        List<String> errors = new LinkedList<String>();
        if (result.getLines() == null || result.getLines().isEmpty()) {
            errors.add("JB-NO_LINE");
        }
        if (result.getDiffLines() == null || result.getDiffLines().isEmpty()) {
            errors.add("JB-NO_DIFF");
        }
        if (result.getCurrentOrder() == null) {
            errors.add("JB-NO_ORDER");
        }
        if (result.getUserId() == null) {
            errors.add("JB-NO_USER");
        }
        if (result.getCurrencyId() == null) {
            errors.add("JB-NO_CURRENCY");
        }
        if (result.getEventDate() == null) {
            errors.add("JB-NO_DATE");
        }
        errors.addAll(result.getErrors());
        return errors;
    }

    private Record findRecordByKey(List<Record> records, String key) {
        for (Record r : records) {
            if (r.getKey().equals(key)) {
                return r;
            }
        }
        return null;
    }

    private void handleMediationErrors(Record record,
                                       List<String> errors,
                                       Integer entityId,
                                       MediationProcess process) {
        if (record == null) return;
        StopWatch watch = new StopWatch("saving errors watch");
        watch.start();
        LOG.debug("Saving mediation result errors: %s", errors.size());

        try {
            PluggableTaskManager<IMediationErrorHandler> tm = new PluggableTaskManager<IMediationErrorHandler>(entityId,
                    Constants.PLUGGABLE_TASK_MEDIATION_ERROR_HANDLER);
            IMediationErrorHandler errorHandler;
            // iterate through all error handlers for current entityId
            // and process errors
            while ((errorHandler = tm.getNextClass()) != null) {
                try {
                    errorHandler.process(record, errors, process.getStartDatetime(), process);
                } catch (TaskException e) {
                    // exception catched for opportunity of processing errorsgetMEdait by other handlers
                    // and continue mediation process for other records
                    // TO-DO: check requirements about error handling in that case
                    LOG.error(e);
                }
            }

        } catch (PluggableTaskException e) {
            LOG.error(e);
            // it's possible plugin configuration exception
            // TO-DO: check requirements about error handling
            // may be rethrow exception
        }

        watch.stop();
        LOG.debug("Saving mediation result errors done. Duration (mls):%s", watch.getTotalTimeMillis());
    }
}
