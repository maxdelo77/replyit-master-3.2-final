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
package com.sapienter.jbilling.server.invoice.db;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.server.metafields.MetaFieldHelper;
import com.sapienter.jbilling.server.metafields.db.CustomizedEntity;
import com.sapienter.jbilling.server.metafields.db.EntityType;
import com.sapienter.jbilling.server.metafields.db.MetaFieldValue;
import com.sapienter.jbilling.server.util.csv.Exportable;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.annotations.*;

import com.sapienter.jbilling.server.order.db.OrderProcessDTO;
import com.sapienter.jbilling.server.payment.db.PaymentInvoiceMapDTO;
import com.sapienter.jbilling.server.process.db.BillingProcessDTO;
import com.sapienter.jbilling.server.user.db.UserDTO;
import com.sapienter.jbilling.server.util.Constants;
import com.sapienter.jbilling.server.util.db.CurrencyDTO;
import com.sapienter.jbilling.server.process.db.PaperInvoiceBatchDTO;
import org.hibernate.annotations.Cache;

import java.math.BigDecimal;
import java.util.ArrayList;

@Entity
@TableGenerator(
        name = "invoice_GEN", 
        table = "jbilling_seqs", 
        pkColumnName = "name", 
        valueColumnName = "next_id", 
        pkColumnValue = "invoice", 
        allocationSize = 100)
@Table(name = "invoice")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class InvoiceDTO extends CustomizedEntity implements Serializable, Exportable {

    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(InvoiceDTO.class));

    private static final int PROCESS = 1;
    public static final int DO_NOT_PROCESS = 0;

    private int id;
    private BillingProcessDTO billingProcessDTO;
    private UserDTO baseUser;
    private CurrencyDTO currencyDTO;
    private InvoiceDTO invoice;
    private PaperInvoiceBatchDTO paperInvoiceBatch;
    private Date createDatetime;
    private Date dueDate;
    private BigDecimal total;
    private int paymentAttempts;
    private InvoiceStatusDTO invoiceStatus;
    private BigDecimal balance;
    private BigDecimal carriedBalance;
    private int inProcessPayment;
    private Integer isReview;
    private Integer deleted;
    private String customerNotes;
    private String publicNumber;
    private Date lastReminder;
    private Integer overdueStep;
    private Date createTimestamp;
    private Collection<InvoiceLineDTO> invoiceLines = new HashSet<InvoiceLineDTO>(0);
    private Set<InvoiceDTO> invoices = new HashSet<InvoiceDTO>(0);
    private Set<OrderProcessDTO> orderProcesses = new HashSet<OrderProcessDTO>(0);
    private Collection<PaymentInvoiceMapDTO> paymentMap = new HashSet<PaymentInvoiceMapDTO>(0);
    private int versionNum;
    
    // for transition to JPA
    private String currencyName;
    private String currencySymbol;

    public InvoiceDTO() {
    }

    public InvoiceDTO(InvoiceDTO invoice) {
        this.setBalance(invoice.getBalance());
        this.setBaseUser(invoice.getBaseUser());
        this.setBillingProcess(invoice.getBillingProcess());
        this.setCarriedBalance(invoice.getCarriedBalance());
        this.setCreateDatetime(invoice.getCreateDatetime());
        this.setCreateTimestamp(invoice.getCreateTimestamp());
        this.setCurrency(invoice.getCurrency());
        this.setCustomerNotes(invoice.getCustomerNotes());
        this.setDeleted(invoice.getDeleted());
        this.setDueDate(invoice.getDueDate());
        this.setId(invoice.getId());
        this.setInProcessPayment(invoice.getInProcessPayment());
        this.setInvoice(invoice.getInvoice());
        this.setInvoiceLines(invoice.getInvoiceLines());
        this.setInvoices(invoice.getInvoices());
        this.setIsReview(invoice.getIsReview());
        this.setLastReminder(invoice.getLastReminder());
        this.setOrderProcesses(invoice.getOrderProcesses());
        this.setOverdueStep(invoice.getOverdueStep());
        this.setPaperInvoiceBatch(invoice.getPaperInvoiceBatch());
        this.setPaymentAttempts(invoice.getPaymentAttempts());
        this.setPaymentMap(invoice.getPaymentMap());
        this.setPublicNumber(invoice.getPublicNumber());        
        this.setInvoiceStatus(invoice.getInvoiceStatus());
        this.setTotal(invoice.getTotal());
        setInvoiceLines(new ArrayList<InvoiceLineDTO>(invoice.getInvoiceLines()));
        setInvoices(new HashSet<InvoiceDTO>(invoice.getInvoices()));
        setOrderProcesses(new HashSet<OrderProcessDTO>(invoice.getOrderProcesses()));
        setPaymentMap(new ArrayList<PaymentInvoiceMapDTO>(invoice.getPaymentMap()));
        setMetaFields(new ArrayList<MetaFieldValue>(invoice.getMetaFields()));
    }

    public InvoiceDTO(int id, CurrencyDTO currencyDTO, Date createDatetime,
            Date dueDate, BigDecimal total, int paymentAttempts, InvoiceStatusDTO invoiceStatus,
            BigDecimal carriedBalance, int inProcessPayment, int isReview,
            Integer deleted, Date createTimestamp) {
        this.id = id;
        this.currencyDTO = currencyDTO;
        this.createDatetime = createDatetime;
        this.dueDate = dueDate;
        this.total = total;
        this.paymentAttempts = paymentAttempts;
        this.invoiceStatus = invoiceStatus;
        this.carriedBalance = carriedBalance;
        this.inProcessPayment = inProcessPayment;
        this.isReview = isReview;
        this.deleted = deleted;
        this.createTimestamp = createTimestamp;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "invoice_GEN")
    @Column(name = "id", unique = true, nullable = false)
    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_process_id")
    public BillingProcessDTO getBillingProcess() {
        return this.billingProcessDTO;
    }

    public void setBillingProcess(BillingProcessDTO billingProcessDTO) {
        this.billingProcessDTO = billingProcessDTO;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    public UserDTO getBaseUser() {
        return this.baseUser;
    }

    public void setBaseUser(UserDTO baseUser) {
        this.baseUser = baseUser;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "currency_id", nullable = false)
    public CurrencyDTO getCurrency() {
        return this.currencyDTO;
    }

    public void setCurrency(CurrencyDTO currencyDTO) {
        this.currencyDTO = currencyDTO;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delegated_invoice_id")
    public InvoiceDTO getInvoice() {
        return this.invoice;
    }

    public void setInvoice(InvoiceDTO invoice) {
        this.invoice = invoice;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paper_invoice_batch_id")
    public PaperInvoiceBatchDTO getPaperInvoiceBatch() {
        return this.paperInvoiceBatch;
    }

    public void setPaperInvoiceBatch(PaperInvoiceBatchDTO paperInvoiceBatch) {
        this.paperInvoiceBatch = paperInvoiceBatch;
    }

    @Column(name = "create_datetime", nullable = false, length = 29)
    public Date getCreateDatetime() {
        return this.createDatetime;
    }

    public void setCreateDatetime(Date createDatetime) {
        this.createDatetime = createDatetime;
    }

    @Column(name = "due_date", nullable = false, length = 13)
    public Date getDueDate() {
        return this.dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    /**
     * Sum total of the invoice lines for the current period. This amount will not change
     * when the invoice is paid, and will always show the dollar value of this invoice for
     * historical purposes.
     *
     * Since a carried invoice balance is added as an invoice line, this total automatically
     * includes the total for the current month plus the carried balances of old un-paid invoices.
     *
     * @return sum total of the invoice lines
     */
    @Column(name = "total", nullable = false, precision = 17, scale = 17)
    public BigDecimal getTotal() {
        return this.total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    @Column(name = "payment_attempts", nullable = false)
    public int getPaymentAttempts() {
        return this.paymentAttempts;
    }

    public void setPaymentAttempts(int paymentAttempts) {
        this.paymentAttempts = paymentAttempts;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "status_id", nullable = false)
    public InvoiceStatusDTO getInvoiceStatus() {
        return invoiceStatus;
    }

    public void setInvoiceStatus(InvoiceStatusDTO invoiceStatus) {
        this.invoiceStatus = invoiceStatus;
    }

    /**
     * Returns 1 if this invoice is to be processed as part of the current billing
     * cycle. If the invoice has already been paid or it's balance was carried over
     * to another invoice this method will return 0, and should not be processed.
     *
     * @return returns 1 if this invoice is to be processed, 0 if not
     */
    @Transient
    public Integer getToProcess() {        
        if (getInvoiceStatus() != null) {
            if (Constants.INVOICE_STATUS_PAID.equals(getInvoiceStatus().getId()))
                return DO_NOT_PROCESS;

            if (Constants.INVOICE_STATUS_UNPAID.equals(getInvoiceStatus().getId()))
                return PROCESS;

            if (Constants.INVOICE_STATUS_UNPAID_AND_CARRIED.equals(getInvoiceStatus().getId()))
                return DO_NOT_PROCESS;
        }

        return PROCESS;
    }

    public void setToProcess(Integer toProcess) {
        if (toProcess == null) {
            setInvoiceStatus(null);
            return;
        }

        setInvoiceStatus(new InvoiceStatusDAS().find(
                (toProcess == DO_NOT_PROCESS
                        ? Constants.INVOICE_STATUS_PAID
                        : Constants.INVOICE_STATUS_UNPAID)
        ));
    }

    /**
     * The total amount owing that must be paid for this single invoice to be brought to zero and marked
     * paid. The initial balance is calculated as the invoice total {@link #getTotal()} - carried
     * balance {@link #getCarriedBalance()}. The initial balance of the invoice <strong>represents the
     * current periods charges only</strong>, not including any carried balances.
     *
     * As payments are applied, this balance will be reduced until it reaches zero and the invoice
     * is marked as paid.
     *
     * @return total owing balance
     */
    @Column(name = "balance", precision = 17, scale = 17)
    public BigDecimal getBalance() {
        return this.balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    /**
     * The sum total of all remaining un-paid or partially paid invoice balances. This represents
     * the portion of the invoice total amount {@link #getTotal()} that was brought forward from
     * an old un-paid invoice.
     *
     * @return portion of the invoice total that was carried forward.
     */
    @Column(name = "carried_balance", nullable = false, precision = 17, scale = 17)
    public BigDecimal getCarriedBalance() {
        return this.carriedBalance;
    }

    public void setCarriedBalance(BigDecimal carriedBalance) {
        this.carriedBalance = carriedBalance;
    }

    @Column(name = "in_process_payment", nullable = false)
    public int getInProcessPayment() {
        return this.inProcessPayment;
    }

    public void setInProcessPayment(int inProcessPayment) {
        this.inProcessPayment = inProcessPayment;
    }

    @Column(name = "is_review", nullable = false)
    public Integer getIsReview() {
        return this.isReview;
    }

    public void setIsReview(Integer isReview) {
        this.isReview = isReview;
    }

    @Column(name = "deleted", nullable = false)
    public Integer getDeleted() {
        return this.deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    @Column(name = "customer_notes", length = 1000)
    public String getCustomerNotes() {
        return this.customerNotes;
    }

    public void setCustomerNotes(String customerNotes) {
        this.customerNotes = customerNotes;
    }

    @Column(name = "public_number", length = 40)
    public String getPublicNumber() {
        return this.publicNumber;
    }

    public void setPublicNumber(String publicNumber) {
        this.publicNumber = publicNumber;
    }

    @Column(name = "last_reminder", length = 29)
    public Date getLastReminder() {
        return this.lastReminder;
    }

    public void setLastReminder(Date lastReminder) {
        this.lastReminder = lastReminder;
    }

    @Column(name = "overdue_step")
    public Integer getOverdueStep() {
        return this.overdueStep;
    }

    public void setOverdueStep(Integer overdueStep) {
        this.overdueStep = overdueStep;
    }

    @Column(name = "create_timestamp", nullable = false, length = 29)
    public Date getCreateTimestamp() {
        return this.createTimestamp;
    }

    public void setCreateTimestamp(Date createTimestamp) {
        this.createTimestamp = createTimestamp;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "invoice")
    public Collection<InvoiceLineDTO> getInvoiceLines() {
        return this.invoiceLines;
    }

    public void setInvoiceLines(Collection<InvoiceLineDTO> invoiceLines) {
        this.invoiceLines = invoiceLines;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "invoice")
    public Set<InvoiceDTO> getInvoices() {
        return this.invoices;
    }

    public void setInvoices(Set<InvoiceDTO> invoices) {
        this.invoices = invoices;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "invoice")
    @Fetch(FetchMode.SUBSELECT)
    public Set<OrderProcessDTO> getOrderProcesses() {
        return this.orderProcesses;
    }

    public void setOrderProcesses(Set<OrderProcessDTO> orderProcesses) {
        this.orderProcesses = orderProcesses;
    }

    public void setPaymentMap(Collection<PaymentInvoiceMapDTO> paymentMap) {
        this.paymentMap = paymentMap;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "invoiceEntity")
    public Collection<PaymentInvoiceMapDTO> getPaymentMap() {
        return paymentMap;
    }

    @Version
    @Column(name = "OPTLOCK")
    public int getVersionNum() {
        return versionNum;
    }

    public void setVersionNum(int versionNum) {
        this.versionNum = versionNum;
    }

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    @JoinTable(
            name = "invoice_meta_field_map",
            joinColumns = @JoinColumn(name = "invoice_id"),
            inverseJoinColumns = @JoinColumn(name = "meta_field_value_id")
    )
    @Sort(type = SortType.COMPARATOR, comparator = MetaFieldHelper.MetaFieldValuesOrderComparator.class)
    public List<MetaFieldValue> getMetaFields() {
        return getMetaFieldsList();
    }

    @Transient
    public EntityType getCustomizedEntityType() {
        return EntityType.INVOICE;
    }
    
    // Helpers, for JPA migration
    @Transient
    public Integer getDelegatedInvoiceId() {
        if (getInvoice() != null) return getInvoice().getId();
        
        return null;
    }

    @Transient
    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    @Transient
    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }
    
    @Transient
    public Integer getUserId() {
        return getBaseUser().getId();
    }
    
    @Transient
    public boolean hasSubAccounts() {
        for(InvoiceLineDTO line: getInvoiceLines()) {
            if (line.getInvoiceLineType().getId() == Constants.INVOICE_LINE_TYPE_SUB_ACCOUNT) return true;
        }
        return false;
    }

    @Transient
    public String getNumber() {
        return getPublicNumber();
    }

    /**
     * Touch this InvoiceDTO and initialize all lazy-loaded fields.
     */
    public void touch() {
        getBillingProcess();
        getBaseUser();
        getCurrency();
        getInvoice();
        getPaperInvoiceBatch();

        if (getInvoiceLines() != null) getInvoiceLines().size();
        if (getInvoices() != null) getInvoices().size();
        if (getPaymentMap() != null) getPaymentMap().size();
        if (getOrderProcesses() != null) {
            for (OrderProcessDTO process : getOrderProcesses())
                process.getPurchaseOrder().touch();
        }
    }

    @Transient
    public String[] getFieldNames() {
        return new String[] {
                "id",
                "publicNumber",
                "userId",
                "userName",
                "status",
                "currency",
                "delegatedInvoices",
                "carriedBalance",
                "total",
                "balance",
                "createdDate",
                "dueDate",
                "paymentAttempts",
                "payments",
                "isReview",
                "notes",

                // invoice lines
                "lineItemId",
                "lineProductCode",
                "lineQuantity",
                "linePrice",
                "lineAmount",
                "lineDescription"
        };
    }

    @Transient
    public Object[][] getFieldValues() {
        StringBuffer delegatedInvoiceIds = new StringBuffer();
        for (Iterator<InvoiceDTO> it = invoices.iterator(); it.hasNext();) {
            delegatedInvoiceIds.append(it.next().getId());
            if (it.hasNext()) delegatedInvoiceIds.append(", ");
        }

        StringBuffer paymentIds = new StringBuffer();
        for (Iterator<PaymentInvoiceMapDTO> it = paymentMap.iterator(); it.hasNext();) {
            paymentIds.append(it.next().getPayment().getId());
            if (it.hasNext()) paymentIds.append(", ");
        }

        List<Object[]> values = new ArrayList<Object[]>();

        // main invoice row
        values.add(
            new Object[] {
                id,
                publicNumber,
                (baseUser != null ? baseUser.getId() : null),
                (baseUser != null ? baseUser.getUserName() : null),
                (invoiceStatus != null ? invoiceStatus.getDescription() : null),
                (currencyDTO != null ? currencyDTO.getDescription() : null),
                delegatedInvoiceIds.toString(),
                carriedBalance,
                total,
                balance,
                createDatetime,
                dueDate,
                paymentAttempts,
                paymentIds.toString(),
                isReview,
                customerNotes
            }
        );

        // indented row for each invoice line
        for (InvoiceLineDTO line : invoiceLines) {
            if (line.getDeleted().equals(0)) {
                values.add(
                    new Object[] {
                        // padding for the main invoice columns
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,

                        // invoice line
                        (line.getItem() != null ? line.getItem().getId() : null),
                        (line.getItem() != null ? line.getItem().getInternalNumber() : null),
                        line.getQuantity(),
                        line.getPrice(),
                        line.getAmount(),
                        line.getDescription()
                    }
                );
            }
        }

        return values.toArray(new Object[values.size()][]);
    }
}
