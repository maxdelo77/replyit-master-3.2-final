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
package com.sapienter.jbilling.server.rule;

import com.sapienter.jbilling.server.util.IdGenerator;

/**
 *
 * @author emilc
 */
public abstract class Result {
    private boolean done = false;
    private final long id = IdGenerator.getInstance().getId();

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public long getId() {
        return id;
    }

    public String toString() {
        return
                "id=" + id + " " +
                "done=" + done + " ";
    }
}
