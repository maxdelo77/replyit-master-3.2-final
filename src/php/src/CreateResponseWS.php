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
  * CreateResponseWS
  * @author Jeremy Hahn
  * @version 1.0
  * @copyright Make A Byte, inc
  * @package com.makeabyte.contrib.jbilling.php
  */

class CreateResponseWS {

      var $invoiceId;            // Identifier of the invoice that was generated.
      var $orderId;              // Identifier of the order that was created.
      var $paymentId;            // Identifier of the payment that was generated to pay the invoice.
      var $paymentResult;        // Outcome of the payment operation.
      var $userId;               // Identifier of the new user created and for which the order, invoice and payment were created.
  	  
  	  
  	  public function CreateResponseWS() {
  	  }
  	  /**
  	   * Sets the invoiceId property of the CreateResponseWS object
  	   * 
  	   * @access public
  	   * @param Integer $id Identifier of the invoice that was generated.
  	   * @return void
  	   */
  	  public function setInvoiceId( $id ) {
  	  	
  	  	     $this->invoiceId = $id;
  	  }
  	  /**
  	   * Sets the orderId property of the CreateResponseWS object
  	   * 
  	   * @access public
  	   * @param Integer $id Identifier of the order that was created.
  	   * @return void
  	   */
  	  public function setOrderId( $id ) {
  	  
  	         $this->orderId = $id;
  	  }
  	  /**
  	   * Sets the paymentId property of the CreateResponseWS object
  	   * 
  	   * @access public
  	   * @param Integer $id Identifier of the payment that was generated to pay the invoice.
  	   * @return void
  	   */
  	  public function setPaymentId( $id ) {
  	  	     
  	  	     $this->paymentId = $id;
  	  }
  	  /**
  	   * Sets the paymentResult property of the CreateResponseWS object
  	   * 
  	   * @access public
  	   * @param PaymentAuthorizationDTOEx $obj Outcome of the payment operation.
  	   * @return void
  	   */
  	  public function setPaymentResult( $result ) {
  	  	
  	  	     $this->paymentResult = $result;
  	  }
  	  /**
  	   * Sets the userId property of the CreateResponseWS object
  	   * 
  	   * @access public
  	   * @param Integer $id Identifier of the new user created and for which the order, invoice and payment were created.
  	   * @return void
  	   */
  	  public function setUserId( $id ) {
  	  
  	         $this->userId = $id;
  	  }
  	  /**
  	   * Gets the invoiceId property of the CreateResponseWS object
  	   * 
  	   * @access public
  	   * @return Identifier of the invoice that was generated.
  	   */
  	  public function getInvoiceId() {
  	  	
  	  	     return $this->invoiceId;
  	  }
  	  /**
  	   * Gets the orderId property of the CreateResponseWS object
  	   * 
  	   * @access public
  	   * @return Identifier of the order that was created.
  	   */
  	  public function getOrderId() {
  	  
  	         return $this->orderId;
  	  }
  	  /**
  	   * Gets the paymentId property of the CreateResponseWS object
  	   * 
  	   * @access public
  	   * @return Identifier of the payment that was generated to pay the invoice.
  	   */
  	  public function getPaymentId() {
  	  	     
  	  	     return $this->paymentId;
  	  }
  	  /**
  	   * Gets the paymentResult property of the CreateResponseWS object
  	   * 
  	   * @access public
  	   * @return Outcome of the payment operation.
  	   */
  	  public function getPaymentResult() {
  	  	
  	  	     return $this->paymentResult;
  	  }
  	  /**
  	   * Gets the userId property of the CreateResponseWS object
  	   * 
  	   * @access public
  	   * @return Identifier of the new user created and for which the order, invoice and payment were created.
  	   */
  	  public function getUserId() {
  	  
  	         return $this->userId;
  	  }
}
?>