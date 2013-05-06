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
package com.sapienter.jbilling.server.process.db;

import com.sapienter.jbilling.server.user.db.UserDTO;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.Date;

@Entity
@TableGenerator(
        name = "process_run_user_GEN",
        table = "jbilling_seqs",
        pkColumnName = "name",
        valueColumnName = "next_id",
        pkColumnValue = "process_run_user",
        allocationSize = 100)
@Table(name = "process_run_user")
// No cache
public class ProcessRunUserDTO implements java.io.Serializable {

    public static final Integer STATUS_FAILED = 0;
    public static final Integer STATUS_SUCCEEDED = 1;

    private int id;
    private ProcessRunDTO processRun;
    private UserDTO user;
    private Integer status;
    private Date created;

    private int versionNum;

    public ProcessRunUserDTO() {
    }

    public ProcessRunUserDTO(int id, ProcessRunDTO processRun, UserDTO user, Integer status, Date created) {
        this.id = id;
        this.processRun = processRun;
        this.user = user;
        this.status = status;
        this.created = created;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "process_run_user_GEN")
    @Column(name = "id", unique = true, nullable = false)
    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "process_run_id")
    public ProcessRunDTO getProcessRun() {
        return this.processRun;
    }

    public void setProcessRun(ProcessRunDTO processRun) {
        this.processRun = processRun;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    public UserDTO getUser() {
        return this.user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    @Column(name = "status", nullable = false)
    public Integer getStatus() {
        return this.status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Column(name = "created", nullable = false, length = 29)
    public Date getCreated() {
        return this.created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @Version
    @Column(name = "OPTLOCK")
    public int getVersionNum() {
        return versionNum;
    }

    public void setVersionNum(int versionNum) {
        this.versionNum = versionNum;
    }

    @Override
    public String toString() {
        StringBuffer ret = new StringBuffer();
        ret.append(" ProcessRunUserDTO: id: ")
                .append(id)
                .append(" user: ")
                .append(user)
                .append(" created: ")
                .append(created)
                .append(" status: ")
                .append(status);

        return ret.toString();
    }
}
