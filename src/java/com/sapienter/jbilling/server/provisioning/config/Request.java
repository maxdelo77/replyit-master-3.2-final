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

package com.sapienter.jbilling.server.provisioning.config;

public class Request implements Comparable<Request> {
    private int order = 0;
    private String submit = null;
    private String rollback = null;
    private boolean postResult = false;
    private String continueOnType = null;

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getSubmit() {
        return submit;
    }

    public void setSubmit(String submit) {
        this.submit = submit;
    }

    public String getRollback() {
        return rollback;
    }

    public void setRollback(String rollback) {
        this.rollback = rollback;
    }

    public boolean getPostResult() {
        return postResult;
    }

    public void setPostResult(boolean postResult) {
        this.postResult = postResult;
    }

    public String getContinueOnType() {
        return continueOnType;
    }

    public void setContinueOnType(String continueOnType) {
        this.continueOnType = continueOnType;
    }

    public int compareTo(Request other) {
        return order - other.order;
    }
}
