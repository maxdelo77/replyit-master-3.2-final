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
package com.sapienter.jbilling.server.notification.db;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Version;

import com.sapienter.jbilling.server.user.db.UserDTO;

@Entity
@TableGenerator(
        name = "notification_message_arch_GEN", 
        table = "jbilling_seqs", 
        pkColumnName = "name", 
        valueColumnName = "next_id", 
        pkColumnValue = "notification_message_arch", 
        allocationSize = 100)
@Table(name = "notification_message_arch")
public class NotificationMessageArchDTO implements Serializable {

    private int id;
    private UserDTO baseUser;
    private Integer typeId;
    private Date createDatetime;
    private String resultMessage;
    private Set<NotificationMessageArchLineDTO> notificationMessageArchLines =
            new HashSet<NotificationMessageArchLineDTO>(0);
    private int versionNum;

    public NotificationMessageArchDTO() {
    }

    public NotificationMessageArchDTO(int id, Date createDatetime) {
        this.id = id;
        this.createDatetime = createDatetime;
    }

    public NotificationMessageArchDTO(int id, UserDTO baseUser, Integer typeId,
            Date createDatetime, String resultMessage,
            Set<NotificationMessageArchLineDTO> notificationMessageArchLines) {
        this.id = id;
        this.baseUser = baseUser;
        this.typeId = typeId;
        this.createDatetime = createDatetime;
        this.resultMessage = resultMessage;
        this.notificationMessageArchLines = notificationMessageArchLines;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "notification_message_arch_GEN")
    @Column(name = "id", unique = true, nullable = false)
    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    public UserDTO getBaseUser() {
        return this.baseUser;
    }

    public void setBaseUser(UserDTO baseUser) {
        this.baseUser = baseUser;
    }

    @Column(name = "type_id")
    public Integer getTypeId() {
        return this.typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    @Column(name = "create_datetime", nullable = false, length = 29)
    public Date getCreateDatetime() {
        return this.createDatetime;
    }

    public void setCreateDatetime(Date createDatetime) {
        this.createDatetime = createDatetime;
    }

    @Column(name = "result_message", length = 200)
    public String getResultMessage() {
        return this.resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "notificationMessageArch")
    public Set<NotificationMessageArchLineDTO> getNotificationMessageArchLines() {
        return this.notificationMessageArchLines;
    }

    public void setNotificationMessageArchLines(
            Set<NotificationMessageArchLineDTO> notificationMessageArchLines) {
        this.notificationMessageArchLines = notificationMessageArchLines;
    }

    @Version
    @Column(name="OPTLOCK")
    public int getVersionNum() {
        return versionNum;
    }

    public void setVersionNum(int versionNum) {
        this.versionNum = versionNum;
    }
}
