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
package com.sapienter.jbilling.server.payment.db;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.sapienter.jbilling.common.CommonConstants;
import com.sapienter.jbilling.common.Constants;
import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.server.user.db.UserDAS;
import com.sapienter.jbilling.server.user.db.UserDTO;
import com.sapienter.jbilling.server.util.db.AbstractDAS;
import com.sapienter.jbilling.server.util.db.CurrencyDAS;
import com.sapienter.jbilling.server.util.db.CurrencyDTO;

public class PaymentDAS extends AbstractDAS<PaymentDTO> {
	
	public static final FormatLogger LOG = new FormatLogger(Logger.getLogger(PaymentDAS.class));

    // used for the web services call to get the latest X
    public List<Integer> findIdsByUserLatestFirst(Integer userId, int maxResults) {
        Criteria criteria = getSession().createCriteria(PaymentDTO.class)
                .add(Restrictions.eq("deleted", 0))
                .createAlias("baseUser", "u")
                    .add(Restrictions.eq("u.id", userId))
                .setProjection(Projections.id()).addOrder(Order.desc("id"))
                .setMaxResults(maxResults);
        return criteria.list();
    }

    public PaymentDTO create(BigDecimal amount, PaymentMethodDTO paymentMethod,
            Integer userId, Integer attempt, PaymentResultDTO paymentResult,
            CurrencyDTO currency) {

        PaymentDTO payment = new PaymentDTO();
        payment.setAmount(amount);
        payment.setPaymentMethod(paymentMethod);
        payment.setBaseUser(new UserDAS().find(userId));
        payment.setAttempt(attempt);
        payment.setPaymentResult(paymentResult);
        payment.setCurrency(new CurrencyDAS().find(currency.getId()));
        payment.setCreateDatetime(Calendar.getInstance().getTime());
        payment.setDeleted(new Integer(0));
        payment.setIsRefund(new Integer(0));
        payment.setIsPreauth(new Integer(0));

        return save(payment);

    }

    /**
     * * query="SELECT OBJECT(p) FROM payment p WHERE p.userId = ?1 AND
     * p.balance >= 0.01 AND p.isRefund = 0 AND p.isPreauth = 0 AND p.deleted =
     * 0"
     * 
     * @param userId
     * @return
     */
    public Collection findWithBalance(Integer userId) {

        UserDTO user = new UserDAS().find(userId);

        Criteria criteria = getSession().createCriteria(PaymentDTO.class);
        criteria.add(Restrictions.eq("baseUser", user));
        criteria.add(Restrictions.ge("balance", Constants.BIGDECIMAL_ONE_CENT));
        criteria.add(Restrictions.eq("isRefund", 0));
        criteria.add(Restrictions.eq("isPreauth", 0));
        criteria.add(Restrictions.eq("deleted", 0));

        return criteria.list();
    }

    /**
     * Revenue = Payments minus Refunds
     * @param userId
     * @return
     */
    public BigDecimal findTotalRevenueByUser(Integer userId) {
        Criteria criteria = getSession().createCriteria(PaymentDTO.class);
        criteria.add(Restrictions.eq("deleted", 0))
                .createAlias("baseUser", "u")
                    .add(Restrictions.eq("u.id", userId))
        		.createAlias("paymentResult", "pr");
        
        Criterion PAYMENT_SUCCESSFUL = Restrictions.eq("pr.id", CommonConstants.PAYMENT_RESULT_SUCCESSFUL);
        Criterion PAYMENT_ENTERED = Restrictions.eq("pr.id", CommonConstants.PAYMENT_RESULT_ENTERED);
        		
        LogicalExpression successOrEntered= Restrictions.or(PAYMENT_ENTERED, PAYMENT_SUCCESSFUL);
        
        // Criteria or condition
        criteria.add(successOrEntered);
        		
        criteria.add(Restrictions.eq("isRefund", 0));
        criteria.setProjection(Projections.sum("amount"));
        criteria.setComment("PaymentDAS.findTotalRevenueByUser-Gross Receipts");

        BigDecimal grossReceipts= criteria.uniqueResult() == null ? BigDecimal.ZERO : (BigDecimal) criteria.uniqueResult();
        
        //Calculate Refunds
        Criteria criteria2 = getSession().createCriteria(PaymentDTO.class);
        criteria2.add(Restrictions.eq("deleted", 0))
                .createAlias("baseUser", "u")
                    .add(Restrictions.eq("u.id", userId))
            		.createAlias("paymentResult", "pr");

		// Criteria or condition
        criteria2.add(successOrEntered);
            		
        criteria2.add(Restrictions.eq("isRefund", 1));
        criteria2.setProjection(Projections.sum("amount"));
        criteria2.setComment("PaymentDAS.findTotalRevenueByUser-Gross Refunds");
        
        BigDecimal refunds= criteria2.uniqueResult() == null ? BigDecimal.ZERO : (BigDecimal) criteria2.uniqueResult();
        
        //net revenue = gross - all refunds
        BigDecimal netRevenueFromUser= grossReceipts.subtract(refunds);
        
		LOG.debug("Gross receipts " + grossReceipts + " minus Gross Refunds " + refunds + ": " + netRevenueFromUser);
        
        return netRevenueFromUser;
    }
    
    public BigDecimal findTotalBalanceByUser(Integer userId) {
        
        //user's payments which are not refunds
        Criteria criteria = getSession().createCriteria(PaymentDTO.class);
        criteria.add(Restrictions.eq("deleted", 0))
            .createAlias("baseUser", "u")
	            .add(Restrictions.eq("u.id", userId))
	            .add(Restrictions.eq("isRefund", 0))
        	.createAlias("paymentResult", "pr");
		
        Criterion PAYMENT_SUCCESSFUL = Restrictions.eq("pr.id", CommonConstants.PAYMENT_RESULT_SUCCESSFUL);
        Criterion PAYMENT_ENTERED = Restrictions.eq("pr.id", CommonConstants.PAYMENT_RESULT_ENTERED);
        		
        LogicalExpression successOrEntered= Restrictions.or(PAYMENT_ENTERED, PAYMENT_SUCCESSFUL);
        
        // Criteria or condition
        criteria.add(successOrEntered);
        
        criteria.setProjection(Projections.sum("balance"));
        criteria.setComment("PaymentDAS.findTotalBalanceByUser");
        BigDecimal paymentBalances = (criteria.uniqueResult() == null ? BigDecimal.ZERO : (BigDecimal) criteria.uniqueResult());

        return paymentBalances;
    }
    

    /**
     * 
     * query="SELECT OBJECT(p) FROM payment p WHERE 
     * p.userId = ?1 AND 
     * p.balance >= 0.01 AND 
     * p.isRefund = 0 AND 
     * p.isPreauth = 1 AND 
     * p.deleted = 0"
     * 
     * @param userId
     * @return
     */
    public Collection<PaymentDTO> findPreauth(Integer userId) {
        
        UserDTO user = new UserDAS().find(userId);

        Criteria criteria = getSession().createCriteria(PaymentDTO.class);
        criteria.add(Restrictions.eq("baseUser", user));
        criteria.add(Restrictions.ge("balance", Constants.BIGDECIMAL_ONE_CENT));
        criteria.add(Restrictions.eq("isRefund", 0));
        criteria.add(Restrictions.eq("isPreauth", 1));
        criteria.add(Restrictions.eq("deleted", 0));

        return criteria.list();

    }

    private static final String BILLING_PROCESS_GENERATED_PAYMENTS_HQL =
            "select payment "
            + " from PaymentDTO payment "
            + " join payment.invoicesMap as invoiceMap "
            + " where invoiceMap.invoiceEntity.billingProcess.id = :billing_process_id "
            + " and payment.deleted = 0 "
            + " and payment.createDatetime >= :start "
            + " and payment.createDatetime <= :end";

    /**
     * Returns a list of all payments that were made to invoices generated by
     * the billing process between the processes start & end times.
     *
     * Payments represent the amount automatically paid by the billing process.
     *
     * @param processId billing process id
     * @param start process run start date
     * @param end process run end date
     * @return list of payments generated by the billing process.
     */
    @SuppressWarnings("unchecked")
    public List<PaymentDTO> findBillingProcessGeneratedPayments(Integer processId, Date start, Date end) {
        Query query = getSession().createQuery(BILLING_PROCESS_GENERATED_PAYMENTS_HQL);
        query.setParameter("billing_process_id", processId);
        query.setParameter("start", start);
        query.setParameter("end", end);

        return query.list();
    }

    private static final String BILLING_PROCESS_PAYMENTS_HQL =
            "select payment "
            + " from PaymentDTO payment "
            + " join payment.invoicesMap as invoiceMap "
            + " where invoiceMap.invoiceEntity.billingProcess.id = :billing_process_id "
            + " and payment.deleted = 0 "
            + " and payment.createDatetime > :end";

    /**
     * Returns a list of all payments that were made to invoices generated by
     * the billing process, after the billing process run had ended.
     *
     * Payments made to generated invoices after the process has finished are still
     * relevant to the process as it shows how much of the balance was paid by
     * users (or paid in a retry process) for this billing period.
     *
     * @param processId billing process id
     * @param end process run end date
     * @return list of payments applied to the billing processes invoices.
     */
    @SuppressWarnings("unchecked")
    public List<PaymentDTO> findBillingProcessPayments(Integer processId, Date end) {
        Query query = getSession().createQuery(BILLING_PROCESS_PAYMENTS_HQL);
        query.setParameter("billing_process_id", processId);
        query.setParameter("end", end);

        return query.list();
    }

    public List<PaymentDTO> findAllPaymentByBaseUserAndIsRefund(Integer userId, Integer isRefund) {

        UserDTO user = new UserDAS().find(userId);
        Criteria criteria = getSession().createCriteria(PaymentDTO.class);
        criteria.add(Restrictions.eq("baseUser", user));
        criteria.add(Restrictions.eq("isRefund",isRefund));
        criteria.add(Restrictions.eq("deleted", 0));

        return criteria.list();

    }

    public List<PaymentDTO> getRefundablePayments(Integer userId) {

        UserDTO user = new UserDAS().find(userId);
        Criteria criteria = getSession().createCriteria(PaymentDTO.class);
        criteria.add(Restrictions.eq("baseUser", user));
        criteria.add(Restrictions.eq("isRefund",0));
        criteria.add(Restrictions.eq("deleted", 0));
        criteria.add(Restrictions.gt("balance", BigDecimal.ZERO));
        // all payments of the given user which are not refund payments
        @SuppressWarnings("unchecked")
		List<PaymentDTO> allPayments =  criteria.list();

        return allPayments;
    }

    /**
     * Find if the passed payment id has been refunded at all.
     * @param paymentId
     * @return
     */
    public boolean isRefundedPartiallyOrFully(Integer paymentId) {
		Criteria criteria = getSession().createCriteria(PaymentDTO.class);
		criteria.add(Restrictions.eq("isRefund", 1));
		criteria.add(Restrictions.ne("id", paymentId));
		criteria.add(Restrictions.eq("payment.id", paymentId));
		return criteria.list().size() > 0;
    }
    
    /**
     * Get the total Refunded amount for this Payment ID
     * @param isRefund
     * @return
     */

    public BigDecimal getRefundedAmount(Integer paymentId) {

        Criteria criteria = getSession().createCriteria(PaymentDTO.class);
        criteria.add(Restrictions.eq("isRefund", 1));
		criteria.add(Restrictions.ne("id", paymentId));
		criteria.add(Restrictions.eq("payment.id", paymentId));
		criteria.setProjection(Projections.sum("amount"));
		criteria.setComment("PaymentDAS.getRefundedAmount - for paymentId");
        
        BigDecimal amountRefunded= criteria.uniqueResult() == null ? BigDecimal.ZERO : (BigDecimal) criteria.uniqueResult();
        return amountRefunded;
    }
    
}
