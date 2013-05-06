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
package com.sapienter.jbilling.server.pluggableTask;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.server.payment.PaymentDTOEx;
import com.sapienter.jbilling.server.payment.db.PaymentAuthorizationDTO;
import com.sapienter.jbilling.server.payment.db.PaymentResultDAS;
import com.sapienter.jbilling.server.pluggableTask.admin.ParameterDescription;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskDTO;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskException;
import com.sapienter.jbilling.server.user.db.AchDTO;
import com.sapienter.jbilling.server.user.db.CreditCardDTO;
import com.sapienter.jbilling.server.util.Constants;

/**
 * Fake payment processor, providing the ability to configure a jbilling
 * installation for testing, where credit card transactions are not processed by
 * a real payment processor.
 * 
 * Behaviour of this processor completely depends on the actual credit card
 * number. In particlular:
 * 
 * <ul>
 * <li>If the number ends with an even number, it will always result on a
 * successful payment</li>
 * <li>If the number ends with an odd number it will always result on a failed
 * payment</li>
 * <li>If the number ends with "0" or with any not number symbol, then the
 * result will be 'unavailable'.</li>
 * </ul>
 */
public class PaymentFakeTask extends PaymentTaskBase implements PaymentTask {

	// pluggable task parameters names
    public static final ParameterDescription PARAM_PROCESSOR_NAME_OPTIONAL = 
    	new ParameterDescription("processor_name", false, ParameterDescription.Type.STR);
    public static final ParameterDescription PARAM_CODE1_OPTIONAL = 
    	new ParameterDescription("code", false, ParameterDescription.Type.STR);    
    public static final ParameterDescription PARAM_HANDLE_ALL_REQUESTS = 
    	new ParameterDescription("all", false, ParameterDescription.Type.STR);
    public static final ParameterDescription PARAM_NAME_PREFIX = 
    	new ParameterDescription("name_prefix", false, ParameterDescription.Type.STR);
    public static final ParameterDescription PARAM_ACCEPT_ACH = 
    	new ParameterDescription("accept-ach", false, ParameterDescription.Type.STR);

    public static final String  VALUE_PROCESSOR_NAME_DEFAULT = "fake-processor";
    public static final String  VALUE_CODE1_DEFAULT = "fake-code-default";
    private static final String PREAUTH_TRANSACTION_PREFIX = "pAuth-";
    
    //initializer for pluggable params
    { 
    	descriptions.add(PARAM_ACCEPT_ACH);
        descriptions.add(PARAM_CODE1_OPTIONAL);
        descriptions.add(PARAM_HANDLE_ALL_REQUESTS);
        descriptions.add(PARAM_NAME_PREFIX);
        descriptions.add(PARAM_PROCESSOR_NAME_OPTIONAL);
    }
    
    private boolean myShouldBlockOtherProcessors;
    private boolean acceptAch;
    
    private Filter myFilter = Filter.ACCEPT_ALL;
    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(PaymentFakeTask.class));

    public void failure(Integer userId, Integer retry) {
    // nothing to do -- ageing process would probably started by real
    // implementation
    }

    @Override
    public void initializeParamters(PluggableTaskDTO task) throws PluggableTaskException {
        super.initializeParamters(task);

        myShouldBlockOtherProcessors = Boolean.parseBoolean((String) parameters.get(PARAM_HANDLE_ALL_REQUESTS.getName()));
        acceptAch = Boolean.parseBoolean((String)parameters.get(PARAM_ACCEPT_ACH.getName()));
        myFilter = Filter.ACCEPT_ALL;
        if (!myShouldBlockOtherProcessors) {
            myFilter = createFilter((String) parameters.get(PARAM_NAME_PREFIX.getName()));
        }
    }

    public boolean process(PaymentDTOEx paymentInfo) throws PluggableTaskException {
        LOG.debug("Processing payment: " + paymentInfo);

        if (paymentInfo.getCreditCard() != null)
            LOG.debug("Processing with credit card number: " + paymentInfo.getCreditCard().getNumber());

        if(paymentInfo.getAch() != null)
            LOG.debug("Processing with ach payment: " + paymentInfo.getAch());

        Result result = doFakeAuthorization(paymentInfo, null);
        LOG.debug("Fake result: " + result);

        return result.shouldCallOtherProcessors();
    }

    /**
     * Fake pre-authorization of a payment. If using a credit card with a CVV security code, the
     * pre-authorization result will be "Successful" if CVV is an even number, or "Failed" if the
     * CVV is an odd number. If the card does not have a CVV security code then the normal
     * PaymentFakeTask payment result logic applies (result based off of the credit card number).
     *
     *
     * @param paymentInfo payment information to pre-auth
     * @return pre-authorization result
     * @throws PluggableTaskException
     */
    public boolean preAuth(PaymentDTOEx paymentInfo) throws PluggableTaskException {
        LOG.debug("Pre-authorization payment: " + paymentInfo);
        String transactionId = generatePreAuthTransactionId();

        if (paymentInfo.getCreditCard() != null && !StringUtils.isBlank(paymentInfo.getCreditCard().getSecurityCode())) {
            // pre-auth result using credit card CVV
            LOG.debug("Processing pre-authorization, generating result from card CVV security code.");

            String cvv = paymentInfo.getCreditCard().getSecurityCode();
            Integer resultId = getProcessResultId(cvv);

            paymentInfo.setPaymentResult(new PaymentResultDAS().find(resultId));
            PaymentAuthorizationDTO authInfo = createAuthorizationDTO(resultId, transactionId);
            storeProcessedAuthorization(paymentInfo, authInfo);

            LOG.debug("Pre-authorization result: " + authInfo);

            boolean wasProcessed = (Constants.RESULT_FAIL.equals(resultId) || Constants.RESULT_OK.equals(resultId));
            return !wasProcessed && !myShouldBlockOtherProcessors;

        } else {
            // pre-auth result using credit card number
            LOG.debug("Processing pre-authorization, generating using card number & amount purchased.");

            Result result = doFakeAuthorization(paymentInfo, transactionId);

            LOG.debug("Pre-authorization result: " + result.getAuthorizationData());

            return result.shouldCallOtherProcessors();
        }
    }

    public boolean confirmPreAuth(PaymentAuthorizationDTO auth, PaymentDTOEx paymentInfo)
            throws PluggableTaskException {
        LOG.debug("confirmPreAuth" + auth + " payment " + paymentInfo);
        if (!getFakeProcessorName().equals(auth.getProcessor())) {
            LOG.warn("name of processor does not match " + getFakeProcessorName() +
                    " " + auth.getProcessor());
        }

        if (!isPreAuthTransactionId(auth.getTransactionId())) {
            LOG.warn("AuthorizationDTOEx with transaction id: " + auth.getTransactionId() + " is used as preauth data");
        }

        Result result = doFakeAuthorization(paymentInfo, null);

        LOG.debug("returning " + result);
        return result.shouldCallOtherProcessors();
    }

    private Result doFakeAuthorization(PaymentDTOEx payment, String transactionId) throws PluggableTaskException {
        CreditCardDTO creditCard = payment.getCreditCard();
        AchDTO ach = payment.getAch();
        boolean isAch = false;
        
        if (creditCard == null || !myFilter.accept(creditCard)) {
            //give real processors a chance
            if (!acceptAch || ach == null) {
                return new Result(null, true);
            }
            isAch = true;
        }

        Integer resultId;
        if (!isAch) {
            resultId = getProcessResultId(creditCard.getNumber());
        } else {
            String val = payment.getAmount().toPlainString();
            resultId = (Integer.parseInt(val.substring(val.length() - 1)) % 2 == 0) ? 
                    Constants.RESULT_OK : Constants.RESULT_FAIL;
        }
        payment.setPaymentResult(new PaymentResultDAS().find(resultId));
        PaymentAuthorizationDTO authInfo = createAuthorizationDTO(resultId, transactionId);
        storeProcessedAuthorization(payment, authInfo);

        boolean wasProcessed = (Constants.RESULT_FAIL.equals(resultId) || Constants.RESULT_OK.equals(resultId));
        boolean shouldCallOthers = !wasProcessed && !myShouldBlockOtherProcessors;
        return new Result(authInfo, shouldCallOthers);
    }

    private String generatePreAuthTransactionId() {
        String retValue = PREAUTH_TRANSACTION_PREFIX + System.currentTimeMillis();
        if (retValue.length() > 20) {
            return retValue.substring(0, 20);
        }
        return retValue;
    }

    private boolean isPreAuthTransactionId(String transactionId) {
        return transactionId != null && transactionId.startsWith(PREAUTH_TRANSACTION_PREFIX);
    }

    /**
     * Returns a result ID based on the last digit of the given number. Even digits will
     * return RESULT_OK, odd digits will return RESULT_FAIL. A zero or non-numerical digit
     * will return RESULT_UNAVAILABLE.
     *
     * @param number number to parse
     * @return process result id based off of the last digit of the given number (even = pass, odd = fail)
     */
    private Integer getProcessResultId(String number) {
        char last = (number.length() == 0) ? ' ' : number.charAt(number.length() - 1);

        switch (last) {
            case '2':
            case '4':
            case '6':
            case '8':
                return Constants.RESULT_OK;

            case '1':
            case '3':
            case '5':
            case '7':
            case '9':
                return Constants.RESULT_FAIL;

            default:
                return Constants.RESULT_UNAVAILABLE;
        }
    }

    private PaymentAuthorizationDTO createAuthorizationDTO(Integer resultConstant, String transactionId) {
        return createAuthorizationDTO(Constants.RESULT_OK.equals(resultConstant), transactionId);
    }

    private PaymentAuthorizationDTO createAuthorizationDTO(boolean isAuthorized, String transactionId) {
        PaymentAuthorizationDTO auth = new PaymentAuthorizationDTO();
        auth.setProcessor(getFakeProcessorName());
        auth.setCode1(getFakeCode1());
        auth.setTransactionId(transactionId);
        auth.setResponseMessage(isAuthorized ? "The transaction has been approved" : "Transaction failed");
        return auth;
    }

    private String getFakeProcessorName() {
        String result = (String) parameters.get(PARAM_PROCESSOR_NAME_OPTIONAL.getName());
        if (result == null) {
            result = VALUE_PROCESSOR_NAME_DEFAULT;
        }
        return result;
    }

    private String getFakeCode1() {
        String result = (String) parameters.get(PARAM_CODE1_OPTIONAL.getName());
        if (result == null) {
            result = VALUE_CODE1_DEFAULT;
        }
        return result;
    }

    @Override
    public String toString() {
        return "PaymentFakeTask: " + System.identityHashCode(this) +
                ", blocking: " + myShouldBlockOtherProcessors +
                ", filter: " + myFilter.toString() +
                ", code1: " + getFakeCode1();
    }

    private static Filter createFilter(String prefix) {
        return (prefix == null || prefix.trim().length() == 0) ? Filter.ACCEPT_ALL : new NameStartsWithFilter(prefix);
    }

    /**
     * Selects requests that should be processed by fake implementation
     */
    private static interface Filter {

        public boolean accept(CreditCardDTO card);
        public static Filter ACCEPT_ALL = new Filter() {

            public boolean accept(CreditCardDTO card) {
                return true;
            }

            @Override
            public String toString() {
                return "Filter#ACCEPT_ALL";
            }
        };
    }

    private static class NameStartsWithFilter implements Filter {

        private final String myPrefix;

        public NameStartsWithFilter(String prefix) {
            myPrefix = prefix;
        }

        public boolean accept(CreditCardDTO card) {
            String name = card.getName();
            return name != null && name.startsWith(myPrefix);
        }

        @Override
        public String toString() {
            return "Filter#startsWith:" + myPrefix;
        }
    }
}
