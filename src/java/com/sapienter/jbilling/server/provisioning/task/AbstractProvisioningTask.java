package com.sapienter.jbilling.server.provisioning.task;

import com.sapienter.jbilling.server.item.PricingField;
import com.sapienter.jbilling.server.item.db.ItemDTO;
import com.sapienter.jbilling.server.order.OrderBL;
import com.sapienter.jbilling.server.order.db.OrderDTO;
import com.sapienter.jbilling.server.order.db.OrderLineDTO;
import com.sapienter.jbilling.server.order.event.NewQuantityEvent;
import com.sapienter.jbilling.server.pluggableTask.PluggableTask;
import com.sapienter.jbilling.server.pluggableTask.TaskException;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskException;
import com.sapienter.jbilling.server.provisioning.event.SubscriptionActiveEvent;
import com.sapienter.jbilling.server.provisioning.event.SubscriptionInactiveEvent;
import com.sapienter.jbilling.server.system.event.Event;
import com.sapienter.jbilling.server.system.event.task.IInternalEventsTask;
import com.sapienter.jbilling.server.util.Constants;
import org.apache.log4j.Logger;

import javax.jms.JMSException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Abstract provisioning plug-in that provides convenience methods and provides processing
 * of internal events to extract relevant provisioning data.
 *
 * @author Brian Cowdery
 * @since 06-Jul-2012
 */
public abstract class AbstractProvisioningTask extends PluggableTask implements IInternalEventsTask {
    private static final Logger LOG = Logger.getLogger(AbstractProvisioningTask.class);

    public static final String ACTIVATED_EVENT_TYPE = "activated";
    public static final String DEACTIVATED_EVENT_TYPE = "deactivated";


    /**
     * Helper that holds the queue of commands to send via JMS to the provisioning message bean.
     *
     * @author othman
     */
    protected static class CommandManager {
        private static final long serialVersionUID = 1L;

        private static final String COMMAND_NAME = "command";
        private static final String ORDER_LINE_ID = "order_line_id";

        private OrderDTO order = null;
        private LinkedList<LinkedList<StringPair>> commands = new LinkedList<LinkedList<StringPair>>();;
        private LinkedList<StringPair> commandQueue;
        private String eventType;

        public CommandManager(String eventType, OrderDTO order) {
            this.eventType = eventType;
            this.order = order;
        }

        public void addCommand(String command, Integer orderLineId) {
            // add current command queue to global queue
            if (commandQueue != null) {
                commands.add(commandQueue);
            }

            // create new queue for "command"
            commandQueue = new LinkedList<StringPair>();

            StringPair param = new StringPair(COMMAND_NAME, command);

            commandQueue.add(param);
            param = new StringPair(ORDER_LINE_ID, orderLineId.toString());
            commandQueue.add(param);
        }

        public void addParameter(String name, String value) {
            commandQueue.add(new StringPair(name, value));
        }

        public OrderDTO getOrder() {
            return order;
        }

        public void setOrder(OrderDTO order) {
            this.order = order;
        }

        public String getEventType() {
            return eventType;
        }

        public LinkedList<LinkedList<StringPair>> getCommands() {
            // parameters command queue should be added
            if (commandQueue != null) {
                commands.add(commandQueue);
            }

            return commands;
        }
    }


    @SuppressWarnings("unchecked")
    private static final Class<Event>[] events = new Class[] {
        SubscriptionActiveEvent.class,
        SubscriptionInactiveEvent.class,
        NewQuantityEvent.class
    };

    public Class<Event>[] getSubscribedEvents() {
        return events;
    }

    public void process(Event event) throws PluggableTaskException {
        if (event instanceof SubscriptionActiveEvent) {
            SubscriptionActiveEvent activeEvent = (SubscriptionActiveEvent) event;

            LOG.debug("Processing order " + activeEvent.getOrder().getId() + " subscription activation");
            doActivate(activeEvent.getOrder(), activeEvent.getOrder().getLines());


        } else if (event instanceof SubscriptionInactiveEvent) {
            SubscriptionInactiveEvent inactiveEvent = (SubscriptionInactiveEvent) event;

            LOG.debug("Processing order " + inactiveEvent.getOrder().getId() + " subscription deactivation");
            doDeactivate(inactiveEvent.getOrder(), inactiveEvent.getOrder().getLines());


        } else if (event instanceof NewQuantityEvent) {
            NewQuantityEvent quantityEvent = (NewQuantityEvent) event;

            if (BigDecimal.ZERO.compareTo(quantityEvent.getOldQuantity()) != 0
                    && BigDecimal.ZERO.compareTo(quantityEvent.getNewQuantity()) != 0) {
                LOG.debug("Order line quantities did not change, no provisioning necessary.");
                return;
            }

            OrderDTO order = new OrderBL(quantityEvent.getOrderId()).getEntity();
            if (!isOrderProvisionable(order)) {
                LOG.warn("Order is not active and cannot be provisioned.");
                return;
            }

            if (BigDecimal.ZERO.compareTo(quantityEvent.getOldQuantity()) == 0) {
                LOG.debug("Processing order " + order.getId() + " activation");
                doActivate(order, Arrays.asList(quantityEvent.getOrderLine()));
            }

            if (BigDecimal.ZERO.compareTo(quantityEvent.getNewQuantity()) == 0) {
                LOG.debug("Processing order " + order.getId() + " deactivation");
                doDeactivate(order, Arrays.asList(quantityEvent.getOrderLine()));
            }

        } else {
            throw new PluggableTaskException("Cannot process event " + event);
        }
    }

    private boolean isOrderProvisionable(OrderDTO order) {
        if (order != null) {

            Date today = new Date();

            if (order.getOrderStatus() != null && order.getOrderStatus().getId() == Constants.ORDER_STATUS_ACTIVE) {
                if (order.getActiveSince() != null
                        && order.getActiveSince().before(today)
                        && order.getActiveUntil() != null
                        && order.getActiveUntil().after(today)) {

                    return true;
                }
            }
        }

        return false;
    }

    private void sendCommandQueue(String eventType, CommandManager c, OrderDTO order) throws PluggableTaskException {
        LOG.debug("Publishing command queue to JMS");

        try {
            CommandsQueueSender cmdSender = new CommandsQueueSender(order);
            cmdSender.postCommandsQueue(c.getCommands(), eventType);

        } catch (JMSException e) {
            throw new PluggableTaskException(e);
        }
    }

    protected void doActivate(OrderDTO order, List<OrderLineDTO> lines) throws PluggableTaskException {
        CommandManager manager = new CommandManager(ACTIVATED_EVENT_TYPE, order);
        activate(order, lines, manager);
        sendCommandQueue(ACTIVATED_EVENT_TYPE, manager, order);
    }

    protected void doDeactivate(OrderDTO order, List<OrderLineDTO> lines) throws PluggableTaskException {
        CommandManager manager = new CommandManager(DEACTIVATED_EVENT_TYPE, order);
        deactivate(order, lines, manager);
        sendCommandQueue(DEACTIVATED_EVENT_TYPE, manager, order);
    }



    /*
        Abstract methods to be implemented to do the actual provisioning work.
     */

    abstract void activate(OrderDTO order, List<OrderLineDTO> lines, CommandManager c);
    abstract void deactivate(OrderDTO order, List<OrderLineDTO> lines, CommandManager c);



    /**
     * Convenience method to find a pricing field by name.
     *
     * @param fields pricing fields
     * @param fieldName name
     * @return found pricing field or null if no field found.
     */
    public static PricingField find(List<PricingField> fields, String fieldName) {
        if (fields != null) {
            for (PricingField field : fields) {
                if (field.getName().equals(fieldName))
                    return field;
            }
        }
        return null;
    }

    /**
     * Convenience method to find a specific order line by item ID.
     *
     * @param lines order lines
     * @param itemId item id
     * @return order line
     */
    public static OrderLineDTO findLine(List<OrderLineDTO> lines, Integer itemId) {
        if (lines != null) {
            for (OrderLineDTO line : lines) {
                if (line.getItemId().equals(itemId)) {
                    return line;
                }
            }
        }
        return null;
    }
}
