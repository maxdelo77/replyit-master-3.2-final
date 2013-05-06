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

package com.sapienter.jbilling.server.notification;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import com.sapienter.jbilling.common.Constants;

import javax.persistence.Column;

public class MessageDTO implements Serializable {
    // message type definitions (synch with DB)
    public static final Integer TYPE_INVOICE_EMAIL = new Integer(1);
    public static final Integer TYPE_AGEING = new Integer(2); // take from 2 to 9
    public static final Integer TYPE_CLERK_PAYOUT = new Integer(10);
    public static final Integer TYPE_PAYOUT = new Integer(11);
    public static final Integer TYPE_INVOICE_PAPER = new Integer(12);
    public static final Integer TYPE_ORDER_NOTIF = new Integer(13); // take from 13 to 15
    public static final Integer TYPE_PAYMENT = new Integer(16); // 16 & 17
    public static final Integer TYPE_INVOICE_REMINDER = new Integer(18);
    public static final Integer TYPE_CREDIT_CARD = new Integer(19);
    public static final Integer TYPE_FORGETPASSWORD_EMAIL = new Integer(20);
    public static final Integer TYPE_PAYMENT_ENTERED = new Integer(22);
    
    //below threshold message
    public static final Integer TYPE_BAL_BELOW_THRESHOLD_EMAIL = new Integer(21);

    // max length of a line (as defined in DB schema
    public static final Integer LINE_MAX = new Integer(1000);
    // most messages are emails. If they have an attachment the file name is here
    private String attachmentFile = null;
    
    private Integer typeId;
    private Integer languageId;
    private Boolean useFlag;
    private Integer deliveryMethodId;
    /*
     * The parameters to be used to get the replacements in the text
     */
    private HashMap parameters = null;
    // this is the message itself, after being loaded from the DB
    private List content = null;

    private Integer includeAttachment;
    private String attachmentDesign;
    private String attachmentType;

    private Integer notifyAdmin = 0;
    private Integer notifyPartner = 0;
    private Integer notifyParent = 0;
    private Integer notifyAllParents = 0;

    public Integer getIncludeAttachment() {
        return includeAttachment;
    }

    public void setIncludeAttachment(Integer includeAttachment) {
        this.includeAttachment = includeAttachment;
    }

    public String getAttachmentType() {
        return attachmentType;
    }

    public void setAttachmentType(String attachmentType) {
        this.attachmentType = attachmentType;
    }

    public String getAttachmentDesign() {
        return attachmentDesign;
    }

    public void setAttachmentDesign(String attachmentDesign) {
        this.attachmentDesign = attachmentDesign;
    }

    public MessageDTO() {
        parameters = new HashMap();
        content = new Vector();
        deliveryMethodId = Constants.D_METHOD_EMAIL;
    }
    /**
     * @return
     */
    public MessageSection[] getContent() {
        return (MessageSection[]) content.toArray(new MessageSection[0]);
    }
    
    public void setContent(MessageSection[] lines) {
        for (int f = 0; f < lines.length; f++) {
            addSection(lines[f]);
        }
    }

    /**
     * @return
     */
    public HashMap getParameters() {
        return parameters;
    }

    /**
     * @return
     */
    public Integer getTypeId() {
        return typeId;
    }

    /**
     * @param string
     */
    public void addSection(MessageSection line) {
        content.add(line);
    }

    /**
     * @param hashtable
     */
    public void addParameter(String name, Object value) {
        parameters.put(name, value);
    }

    /**
     * @param integer
     */
    public void setTypeId(Integer integer) {
        typeId = integer;
    }

    public boolean validate() {
        if (typeId == null || parameters == null || content == null ||
                content.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }
    /**
     * @return
     */
    public Integer getLanguageId() {
        return languageId;
    }

    /**
     * @param languageId
     */
    public void setLanguageId(Integer languageId) {
        this.languageId = languageId;
    }

    /**
     * @return
     */
    public Integer getDeliveryMethodId() {
        return deliveryMethodId;
    }

    /**
     * @param deliveryMethodId
     */
    public void setDeliveryMethodId(Integer deliveryMethodId) {
        this.deliveryMethodId = deliveryMethodId;
    }

    public Boolean getUseFlag() {
        return useFlag;
    }
    public void setUseFlag(Boolean useFlag) {
        this.useFlag = useFlag;
    }
    
    public String toString(){
        String ret = "language = " + languageId + " type = " + typeId + " use = " +
                useFlag + " content = ";
        for (int f = 0; f < content.size(); f++) {
            ret += "[" + content.get(f) + "]";
        }
        
        return ret;
    }
    public String getAttachmentFile() {
        return attachmentFile;
    }
    public void setAttachmentFile(String attachmentFile) {
        this.attachmentFile = attachmentFile;
    }
    
    public void setContentSize(int i) {
        ((Vector) content).setSize(i);
    }

    public Integer getNotifyAdmin() {
        return this.notifyAdmin;
    }

    public void setNotifyAdmin(Integer notifyAdmin) {
        this.notifyAdmin = notifyAdmin;
    }

    public Integer getNotifyPartner() {
        return this.notifyPartner;
    }

    public void setNotifyPartner(Integer notifyPartner) {
        this.notifyPartner = notifyPartner;
    }

    public Integer getNotifyParent() {
        return this.notifyParent;
    }

    public void setNotifyParent(Integer notifyParent) {
        this.notifyParent = notifyParent;
    }

    public Integer getNotifyAllParents() {
        return this.notifyAllParents;
    }

    public void setNotifyAllParents(Integer notifyAllParents) {
        this.notifyAllParents = notifyAllParents;
    }
}
