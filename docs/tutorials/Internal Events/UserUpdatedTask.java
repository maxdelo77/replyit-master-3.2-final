package com.sapienter.jbilling.server.user.tasks;

import com.sapienter.jbilling.server.order.OrderBL;
import com.sapienter.jbilling.server.order.db.OrderBillingTypeDTO;
import com.sapienter.jbilling.server.order.db.OrderDTO;
import com.sapienter.jbilling.server.order.db.OrderLineDTO;
import com.sapienter.jbilling.server.order.db.OrderPeriodDTO;
import com.sapienter.jbilling.server.pluggableTask.PluggableTask;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskException;
import com.sapienter.jbilling.server.system.event.Event;
import com.sapienter.jbilling.server.system.event.task.IInternalEventsTask;
import com.sapienter.jbilling.server.user.UserBL;
import com.sapienter.jbilling.server.user.db.UserDTO;
import com.sapienter.jbilling.server.user.event.UserCreatedEvent;
import com.sapienter.jbilling.server.user.event.UserUpdatedEvent;
import com.sapienter.jbilling.server.util.Constants;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

public class UserUpdatedTask extends PluggableTask implements IInternalEventsTask {
    private Integer entityId;
    private Integer executorId;

    private static final Class<Event> events[] = new Class[]{
            UserUpdatedEvent.class,
            UserCreatedEvent.class
    };


    public Class<Event>[] getSubscribedEvents() {
        return events;
    }

    public void process(Event event) throws PluggableTaskException {
        UserDTO userDTO;

        if (event instanceof UserUpdatedEvent) {
            UserUpdatedEvent userEvent = (UserUpdatedEvent) event;
            userDTO = userEvent.getUserDTO();
            entityId = userEvent.getEntityId();
            executorId = userEvent.getExecutorId();
        } else if (event instanceof UserCreatedEvent) {
            UserCreatedEvent userEvent = (UserCreatedEvent) event;
            userDTO = new UserBL(userEvent.getUserId()).getDto();
            entityId = userEvent.getEntityId();
            executorId = userEvent.getExecutorId();
        } else {
            userDTO = null;
        }

        if (userDTO != null) {
            OrderDTO orderDTO;
            OrderBL bl = new OrderBL();

            // Create the order
            orderDTO = new OrderDTO();

            orderDTO.setBaseUserByUserId(userDTO);
            orderDTO.setOrderBillingType(new OrderBillingTypeDTO(Constants.ORDER_BILLING_PRE_PAID));
            orderDTO.setOrderPeriod(new OrderPeriodDTO(Constants.ORDER_PERIOD_ONCE));
            orderDTO.setCurrencyId(userDTO.getCurrencyId());
            orderDTO.setNotes("Some notes here.");

            Calendar cal = Calendar.getInstance();
            cal.clear();
            cal.set(2008, 9, 3);
            orderDTO.setActiveSince(cal.getTime());
            orderDTO.setCycleStarts(cal.getTime());

            // Create new Order Line
            OrderLineDTO orderLine = new OrderLineDTO();
            orderLine.setTypeId(Constants.ORDER_LINE_TYPE_ITEM);
            orderLine.setDescription("Some Description for testing the event.");
            orderLine.setCreateDatetime(new Date());
            orderLine.setQuantity(1);
            orderLine.setPrice(new BigDecimal(10));
            orderLine.setAmount(new BigDecimal(10));
            orderLine.setItemId(1);

            // Add order lines to order.
            orderDTO.getLines().add(orderLine);

            bl.create(entityId, executorId, orderDTO);
        }
    }
}
