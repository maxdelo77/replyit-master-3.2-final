package com.sapienter.jbilling.server.user.event;

import com.sapienter.jbilling.server.system.event.Event;
import com.sapienter.jbilling.server.user.db.UserDTO;

public class UserUpdatedEvent implements Event {
    UserDTO userDTO;
    Integer entityId;
    Integer executorId;

    public UserUpdatedEvent(UserDTO userDTO, Integer entityId, Integer executorId) {
        this.userDTO = userDTO;
        this.entityId = entityId;
        this.executorId = executorId;
    }

    public UserDTO getUserDTO() {
        return this.userDTO;
    }

    public String getName() {
        return "User updated event.";
    }

    public Integer getEntityId() {
        return this.entityId;
    }

    public Integer getExecutorId() {
        return this.executorId;
    }
}
