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
  * PaymentAuthorizationDTO
  * @author Jeremy Hahn
  * @version 1.0
  * @copyright Make A Byte, inc
  * @package com.makeabyte.contrib.jbilling.php
  */

class PaymentAuthorizationDTO {

      var $id;                      // Unique identifier of the payment authorization.
      var $processor;               // Name of the payment processor.
      var $code1;                   // Request code number 1.
      var $code2;                   // Request code number 2.
      var $code3;                   // Request code number 3. 
      var $approvalCode;            // Approval code provided by the processor.
      var $AVS;                     // A code with the results of address verification.
      var $transactionId;           // Identifier of the processor transaction.
      var $MD5;                     // Hash for the transaction.
      var $cardCode;                // Payment card code.
      var $createDate;              // The creation date for this payment authorization record.
      var $responseMessage;         // The response provided by the processor.

      /**
       * The PaymentAuthorizationDTO constructor
       * 
       * @access public
       */
	  public function PaymentAuthorizationDTO() {
	  }
	  /**
	   * Sets the id property of the PaymentAuthorizationDTO object
	   * 
	   * @access public
	   * @param Integer $id Unique identifier of the payment authorization.
	   */
	  public function setId( $id ) {

	  	    $this->id = $id;
	  }
	  /**
	   * Sets the processor property of the PaymentAuthorizationDTO object
	   * 
	   * @access public
	   * @param String $name The name of the payment processor.
	   */
	  public function setProcessor( $name ) {

	  	     $this->result = $name;
	  }
	  /**
	   * Sets the code1 property of the PaymentAuthorizationDTO object
	   * 
	   * @access public
	   * @param String $code1 Request code number 1.
	   */
	  public function setCode1( $code ) {

	  	     $this->code1 = $code;
	  }
	  /**
	   * Sets the code2 property of the PaymentAuthorizationDTO object
	   * 
	   * @access public
	   * @param String $code1 Request code number 2.
	   */
	  public function setCode2( $code ) {

	  	     $this->code2 = $code;
	  }
	  /**
	   * Sets the code3 property of the PaymentAuthorizationDTO object
	   * 
	   * @access public
	   * @param String $code3 Request code number 3.
	   */
	  public function setCode3( $code ) {

	  	     $this->code3 = $code;
	  }
	  /**
	   * Sets the AVS property of the PaymentAuthorizationDTO object
	   * 
	   * @access public
	   * @param String $code A code with the results of address verification.
	   */
	  public function setAVS( $code ) {

	  	     $this->AVS = $code;
	  }
	  /**
	   * Sets the transactionId property of the PaymentAuthorizationDTO object
	   * 
	   * @access public
	   * @param String $id Identifier of the processor transaction.
	   */
	  public function setTransactionId( $id ) {

	  	     $this->transactionId = $id;
	  }
	  /**
	   * Sets the MD5 property of the PaymentAuthorizationDTO object
	   * 
	   * @access public
	   * @param String $hash Hash for the transaction.
	   */
	  public function setMD5( $id ) {

	  	     $this->MD5 = $id;
	  }
	  /**
	   * Sets the cardCode property of the PaymentAuthorizationDTO object
	   * 
	   * @access public
	   * @param String $code Payment card code.
	   */
	  public function setCardCode( $code ) {

	  	     $this->cardCode = $code;
	  }
	  /**
	   * Sets the createDate property of the PaymentAuthorizationDTO object
	   * 
	   * @access public
	   * @param Date $date The creation date for this payment authorization record.
	   */
	  public function setCreateDate( $date ) {

	  	     $this->createDate = $date;
	  }
	  /**
	   * Sets the responseMessage property of the PaymentAuthorizationDTO object
	   * 
	   * @access public
	   * @param String $message The creation date for this payment authorization record.
	   */
	  public function setResponseMessage( $message ) {

	  	     $this->responseMessage = $message;
	  }

      /**
	   * Gets the id property of the PaymentAuthorizationDTO object
	   * 
	   * @access public
	   * @return Unique identifier of the payment authorization.
	   */
	  public function getId() {

	  	     return $this->id;
	  }
	  /**
	   * Gets the processor property of the PaymentAuthorizationDTO object
	   * 
	   * @access public
	   * @return The name of the payment processor.
	   */
	  public function getProcessor() {

	  	      return $this->result;
	  }
	  /**
	   * Gets the code1 property of the PaymentAuthorizationDTO object
	   * 
	   * @access public
	   * @return Request code number 1.
	   */
	  public function getCode1() {

	  	     return $this->code1;
	  }
	  /**
	   * Gets the code2 property of the PaymentAuthorizationDTO object
	   * 
	   * @access public
	   * @return Request code number 2.
	   */
	  public function getCode2() {

	  	     return $this->code2;
	  }
	  /**
	   * Gets the code3 property of the PaymentAuthorizationDTO object
	   * 
	   * @access public
	   * @return Request code number 3.
	   */
	  public function getCode3() {

	  	     return $this->code3;
	  }
	  /**
	   * Gets the AVS property of the PaymentAuthorizationDTO object
	   * 
	   * @access public
	   * @return A code with the results of address verification.
	   */
	  public function getAVS() {

	  	     return $this->AVS;
	  }
	  /**
	   * Gets the transactionId property of the PaymentAuthorizationDTO object
	   * 
	   * @access public
	   * @return Identifier of the processor transaction.
	   */
	  public function getTransactionId() {

	  	     return $this->transactionId;
	  }
	  /**
	   * Gets the MD5 property of the PaymentAuthorizationDTO object
	   * 
	   * @access public
	   * @return Hash for the transaction.
	   */
	  public function getMD5() {

	  	     return $this->MD5;
	  }
	  /**
	   * Gets the cardCode property of the PaymentAuthorizationDTO object
	   * 
	   * @access public
	   * @return Payment card code.
	   */
	  public function getCardCode() {

	  	     return $this->cardCode;
	  }
	  /**
	   * Gets the createDate property of the PaymentAuthorizationDTO object
	   * 
	   * @access public
	   * @return The creation date for this payment authorization record.
	   */
	  public function getCreateDate() {

	  	     return $this->createDate;
	  }
	  /**
	   * Gets the responseMessage property of the PaymentAuthorizationDTO object
	   * 
	   * @access public
	   * @return The creation date for this payment authorization record.
	   */
	  public function getResponseMessage() {

	  	     return $this->responseMessage;
	  }
}
?>