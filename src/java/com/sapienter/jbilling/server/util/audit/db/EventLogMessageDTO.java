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
package com.sapienter.jbilling.server.util.audit.db;

import com.sapienter.jbilling.server.util.Constants;
import com.sapienter.jbilling.server.util.db.AbstractDescription;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@org.hibernate.annotations.Entity(mutable = false)
@Table(name="event_log_message")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class EventLogMessageDTO extends AbstractDescription {

    @Id 
    @Column(name="id", unique=true, nullable=false)
    private int id;

    @OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="eventLogMessage")
    private Set<EventLogDTO> eventLogs = new HashSet<EventLogDTO>(0);

    @Override
    protected String getTable() {
        return Constants.TABLE_EVENT_LOG_MESSAGE;
    }

    protected EventLogMessageDTO() {
    }
    
    public EventLogMessageDTO(int id) {
        this.id = id;
    }
    public EventLogMessageDTO(int id, Set<EventLogDTO> eventLogs) {
       this.id = id;
       this.eventLogs = eventLogs;
    }
   
    public int getId() {
        return this.id;
    }
    
    public Set<EventLogDTO> getEventLogs() {
        return this.eventLogs;
    }
    
}


