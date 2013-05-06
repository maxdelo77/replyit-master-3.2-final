<?php
/**
 * jbilling-php-api
 * Copyright (C) 2007-2009  Make A Byte, inc
 * http://www.makeabyte.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * @package com.makeabyte.contrib.jbilling.php
 */

/**
  * PaymentWS
  * @author Jeremy Hahn
  * @version 1.0
  * @copyright Make A Byte, inc
  * @package com.makeabyte.contrib.jbilling.php
  */

class PaymentWS {

      var $id;              // Unique identifier of the paymentrecord
	  var $amount;          // The amount of the payment operation.
	  var $balance;         // Balance of this payment. If greater than 0, this payment could pay part of another invoice. If 0, this payment has already been applied to an invoice, lowering the invoice's balance.
	  var $createDateTime;  // Date in which this payment record was created.
	  var $updateDateTime;  // Date in which this payment record was last updated.
	  var $paymentDate;     // Date of the payment.
	  var $attempt;         // Number of the attempt to process this payment
	  var $deleted;         // “1” if this record has been deleted, “0” otherwise.
      var $methodId;        // Identifier of the payment method. Refer to Appendix A for a list of acceptable values.
      var $resultId;        // Identifier of the result of the payment attempt. Refer to Appendix A for a list of acceptable values.
      var $isRefund;        // Integer “1” if this payment constitutes a refund operation, “0” otherwise.
      var $isPreauth;       // “1” if this payment is a pre-authorization, “0” otherwise.
      var $currencyId;      // Identifier of the currency in which the payment is being made. See Appendix A for a list of acceptable values.
      var $userId;          // Identifier of the user this payment record belongs to.
      var $cheque;          // If this payment is done via check, this property contains information about the cheque, otherwise it contains "null”.
      var $creditCard;      // If this is a credit card payment, this property contains information about the credit card, otherwise it contains "null”.
      var $ach;             // If this is a payment done with Automatic Clearing House (ACH), this property contains the banking information needed.
      var $method;          // Name of the payment method used.
      var $invoiceIds;      // Contains the list of invoices this payment is paying.
      var $paymentId;       // Refund specific field. When a refund is to be issued, this field holds the identifier of the payment that is to be refunded.
      var $authorizationId;   // Refund specific field. Contains the DTO authorization field for the refund.

      /**
       * The PaymentWS constructor
       * 
       * @access public
       */
	  public function PaymentWS() {
	  }
	  /**
	   * Sets the id property of the PaymentWS object
	   * 
	   * @access public
	   * @param Integer $id Unique identifier of the paymentrecord
	   * @return void
	   */
	  public function setId( $id ) {
	  	
	  	     $this->id = $id;
	  }
	  /**
	   * Sets the amount property of the PaymentWS object
	   * 
	   * @access public
	   * @param Float $amount The amount of the payment operation.
	   * @return void
	   */
	  public function setAmount( $amount ) {
	  	
	  	     $this->amount = $amount;
	  }
	  /**
	   * Sets the balance property of the PaymentWS object
	   * 
	   * @access public
	   * @param Float $bal Balance of this payment. If greater than 0, this payment could pay part of another invoice. If 0, this payment has already been applied to an invoice, lowering the invoice's balance.
	   * @return void
	   */
	  public function setBalance( $bal ) {
	  	
	  	     $this->balance = $bal;
	  }
	  /**
	   * Sets the createDateTime property of the PaymentWS object
	   * 
	   * @access public
	   * @param Date $date Date in which this payment record was created.
	   * @return void
	   */
	  public function setCreateDateTime( $date ) {
	  	
	  	     $this->createDateTime = $date;
	  }
	  /**
	   * Sets the updateDateTime property of the PaymentWS object
	   * 
	   * @access public
	   * @param Date $date Date in which this payment record was last updated.
	   * @return void
	   */
	  public function setUpdateDateTime( $date ) {
	  
	         $this->updateDateTime = $date;
	  }
	  /**
	   * Sets the paymentDate property of the PaymentWS object
	   * 
	   * @access public
	   * @param Date $date Date of the payment.
	   * @return void
	   */
	  public function setPaymentDate( $date ) {
	  	
	  	     $this->paymentDate = $date;
	  }
	  /**
	   * Sets the attempt property of the PaymentWS object
	   * 
	   * @access public
	   * @param Integer $int Number of the attempt to process this payment
	   * @return void
	   */
	  public function setAttempt( $attempt ) {

	  	     $this->attempt = $attempt;
	  }
	  /**
	   * Sets the deleted property of the PaymentWS object
	   * 
	   * @access public
	   * @param Integer $int “1” if this record has been deleted, “0” otherwise.
	   * @return void
	   */
	  public function setDeleted( $deleted ) {
	  	
	  	     $this->deleted = $deleted;
	  }
	  /**
	   * Sets the methodId property of the PaymentWS object
	   * 
	   * @access public
	   * @param Integer $id Identifier of the payment method. Refer to Appendix A for a list of acceptable values.
	   * @return void
	   */
	  public function setMethodId( $id ) {
	  	
	  	     $this->methodId = $id;
	  }
	  /**
	   * Sets the resultId property of the PaymentWS object
	   * 
	   * @access public
	   * @param Integer $id Identifier of the result of the payment attempt. Refer to Appendix A for a list of acceptable values.
	   * @return void
	   */
	  public function setResultId( $id ) {
	  	
	  	     $this->resultId = $id;
	  }
	  /**
	   * Sets the isRefund property of the PaymentWS object
	   * 
	   * @access public
	   * @param Integer $id Integer “1” if this payment constitutes a refund operation, “0” otherwise.
	   * @return void
	   */
	  public function setIsRefund( $int ) {
	  	
	  	     $this->isRefund = $int;
	  }
	  /**
	   * Sets the isPreAuth property of the PaymentWS object
	   * 
	   * @access public
	   * @param Integer $id “1” if this payment is a pre-authorization, “0” otherwise.
	   * @return void
	   */
	  public function setIsPreAuth( $int ) {
	  	
	  	     $this->isPreauth = $int;
	  }
	  /**
	   * Sets the currencyId property of the PaymentWS object
	   * 
	   * @access public
	   * @param Integer $id Identifier of the currency in which the payment is being made. See Appendix A for a list of acceptable values. 
	   * @return void
	   */
	  public function setCurrencyId( $id ) {
	  	
	  	     $this->currencyId = $id;
	  }
	  /**
	   * Sets the userId property of the PaymentWS object
	   * 
	   * @access public
	   * @param Integer $id Identifier of the user this payment record belongs to. 
	   * @return void
	   */
	  public function setUserId( $id ) {
	  	
	  	     $this->userId = $id;
	  }
	  /**
	   * Sets the cheque property of the PaymentWS object
	   * 
	   * @access public
	   * @param PaymentInfoChequeDTO $cheque If this payment is done via check, this property contains information about the cheque, otherwise it contains "null”. 
	   * @return void
	   */
	  public function setCheque( $cheque ) {
	  	
	  	     $this->cheque = $cheque;
	  }
	  /**
	   * Sets the creditCard property of the PaymentWS object
	   * 
	   * @access public
	   * @param CreditCardDTO $cc If this is a credit card payment, this property contains information about the credit card, otherwise it contains "null”. 
	   * @return void
	   */
	  public function setCreditCard( $cc ) {
	  	
	  	     $this->creditCard = $cc;
	  }
	  /**
	   * Sets the ach property of the PaymentWS object
	   * 
	   * @access public
	   * @param  AchDTO $ach If this is a payment done with Automatic Clearing House (ACH), this property contains the banking information needed.
	   * @return void
	   */
	  public function setAch( $ach ) {
	  	
	  	     $this->ach = $ach;
	  }
	  /**
	   * Sets the method property of the PaymentWS object
	   * 
	   * @access public
	   * @param String $method Name of the payment method used.
	   * @return void
	   */
	  public function setMethod( $method ) {

	  	     $this->method = $method;
	  }
	  /**
	   * Sets the invoice property of the PaymentWS object
	   * 
	   * @access public
	   * @param Integer[] $id Contains the list of invoices this payment is paying.
	   * @return void
	   */
	  public function setInvoiceId( $id ) {
	  	
	  	     $this->invoiceId = $id;
	  }
	  /**
	   * Sets the payment property of the PaymentWS object
	   * 
	   * @access public
	   * @param Integer $id Refund specific field. When a refund is to be issued, this field holds the identifier of the payment that is to be refunded.
	   * @return void
	   */
	  public function setPaymentId( $id ) {
	  	     
	  	     $this->paymentId = $id;
	  }
	  /**
	   * Sets the payment property of the PaymentWS object
	   * 
	   * @access public
	   * @param PaymentAuthorizationDTO $auth Refund specific field. Contains the DTO authorization field for the refund.
	   * @return void
	   */
	  public function setAuthorization( $auth ) {
	  	
	  	    $this->authorizationId = $auth;
	  } 
	  /**
	   * Gets the id property of the PaymentWS object
	   * 
	   * @access public
	   * @return Unique identifier of the paymentrecord
	   */
	  public function getId() {

	  	     return $this->id;
	  }
	  /**
	   * Gets the amount property of the PaymentWS object
	   * 
	   * @access public
	   * @return The amount of the payment operation.
	   */
	  public function getAmount() {

	  	     return $this->amount;
	  }
	  /**
	   * Gets the balance property of the PaymentWS object
	   * 
	   * @access public
	   * @return Balance of this payment. If greater than 0, this payment could pay part of another invoice. If 0, this payment has already been applied to an invoice, lowering the invoice's balance.
       */
	  public function getBalance() {

	  	     return $this->balance;
	  }
	  /**
	   * Gets the createDateTime property of the PaymentWS object
	   * 
	   * @access public
	   * @return Date in which this payment record was created.
	   */
	  public function getCreateDateTime() {

	  	     return $this->createDateTime;
	  }
	  /**
	   * Gets the updateDateTime property of the PaymentWS object
	   * 
	   * @access public
	   * @return Date in which this payment record was last updated.
       */
	  public function getUpdateDateTime() {

	         return $this->updateDateTime;
	  }
	  /**
	   * Gets the paymentDate property of the PaymentWS object
	   * 
	   * @access public
	   * @return Date of the payment.
	   */
	  public function getPaymentDate() {

	  	     return $this->paymentDate;
	  }
	  /**
	   * Gets the attempt property of the PaymentWS object
	   * 
	   * @access public
	   * @return Number of the attempt to process this payment
	   */
	  public function getAttempt() {

	  	     return $this->attempt;
	  }
	  /**
	   * Gets the deleted property of the PaymentWS object
	   * 
	   * @access public
	   * @return “1” if this record has been deleted, “0” otherwise.
	   */
	  public function getDeleted() {

	  	     return $this->deleted;
	  }
	  /**
	   * Gets the methodId property of the PaymentWS object
	   * 
	   * @access public
	   * @return Identifier of the payment method. Refer to Appendix A for a list of acceptable values.
	   */
	  public function getMethodId() {

	  	     return $this->methodId;
	  }
	  /**
	   * Gets the resultId property of the PaymentWS object
	   * 
	   * @access public
	   * @return Identifier of the result of the payment attempt. Refer to Appendix A for a list of acceptable values.
	   */
	  public function getResultId() {

	  	     return $this->resultId;
	  }
	  /**
	   * Gets the isRefund property of the PaymentWS object
	   * 
	   * @access public
	   * @return Integer “1” if this payment constitutes a refund operation, “0” otherwise.
	   */
	  public function getIsRefund() {

	  	     return $this->isRefund;
	  }
	  /**
	   * Gets the isPreAuth property of the PaymentWS object
	   * 
	   * @access public
	   * @return “1” if this payment is a pre-authorization, “0” otherwise.
	   */
	  public function getIsPreAuth() {

	  	     return $this->isPreauth;
	  }
	  /**
	   * Gets the currencyId property of the PaymentWS object
	   * 
	   * @access public
	   * @return Identifier of the currency in which the payment is being made. See Appendix A for a list of acceptable values. 
	   */
	  public function getCurrencyId() {

	  	     return $this->currencyId;
	  }
	  /**
	   * Gets the userId property of the PaymentWS object
	   * 
	   * @access public
	   * @return Identifier of the user this payment record belongs to. 
	   */
	  public function getUserId() {

	  	     return $this->userId;
	  }
	  /**
	   * Gets the cheque property of the PaymentWS object
	   * 
	   * @access public
	   * @return If this payment is done via check, this property contains information about the cheque, otherwise it contains "null”. 
	   */
	  public function getCheque() {

	  	     return $this->cheque;
	  }
	  /**
	   * Gets the creditCard property of the PaymentWS object
	   * 
	   * @access public
	   * @return If this is a credit card payment, this property contains information about the credit card, otherwise it contains "null”. 
	   */
	  public function getCreditCard() {

	  	     return $this->creditCard;
	  }
	  /**
	   * Gets the ach property of the PaymentWS object
	   * 
	   * @access public
	   * @return If this is a payment done with Automatic Clearing House (ACH), this property contains the banking information needed.
	   */
	  public function getAch() {

	  	     return $this->ach;
	  }
	  /**
	   * Gets the method property of the PaymentWS object
	   * 
	   * @access public
	   * @return Name of the payment method used.
	   */
	  public function getMethod() {

	  	     return $this->method;
	  }
	  /**
	   * Gets the invoice property of the PaymentWS object
	   * 
	   * @access public
	   * @return Contains the list of invoices this payment is paying.
	   */
	  public function getInvoiceId() {

	  	     return $this->invoiceId;
	  }
	  /**
	   * Gets the payment property of the PaymentWS object
	   * 
	   * @access public
	   * @return Refund specific field. When a refund is to be issued, this field holds the identifier of the payment that is to be refunded.
	   */
	  public function getPaymentId() {

	  	     return $this->paymentId;
	  }
	  /**
	   * Gets the payment property of the PaymentWS object
	   * 
	   * @access public
	   * @return Refund specific field. Contains the DTO authorization field for the refund.
	   */
	  public function getAuthorization() {

	  	    return $this->authorizationId;
	  }
}
?>