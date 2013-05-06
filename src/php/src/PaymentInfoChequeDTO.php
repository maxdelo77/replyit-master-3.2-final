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
  * PaymentInfoChequeDTO
  * @author Jeremy Hahn
  * @version 1.0
  * @copyright Make A Byte, inc
  * @package com.makeabyte.contrib.jbilling.php
  */

class PaymentInfoChequeDTO {

      var $id;         // The unique identifier of this record.
      var $bank;       // The name of the bank this cheque's account belongs to.
      var $number;     // The cheque's number. 
      var $date;       // The cheque's date.
      
      /**
       * The PaymentInfoChequeDTO constructor
       * 
       * @access public
       */
	  public function PaymentInfoChequeDTO() {
	  }
	  /**
       * Sets the id property of the PaymentInfoChequeDTO object
       * 
       * @access public
       * @param Integer $id The unique identifier of this record.
       * @return void
       */
	  public function setId( $id ) {
	  	
	  	     $this->id = $id;
	  }
	  /**
       * Sets the bank property of the PaymentInfoChequeDTO object
       * 
       * @access public
       * @param String $name The name of the bank this cheque's account belongs to.
       * @return void
       */
	  public function setBank( $name ) {
	  	
	  	     $this->bank = $name;
	  }
	  /**
       * Sets the number property of the PaymentInfoChequeDTO object
       * 
       * @access public
       * @param Integer $num The cheque's number.
       * @return void
       */
	  public function setNumber( $num ) {

	  	     $this->number = $num;
	  }
	  /**
       * Sets the date property of the PaymentInfoChequeDTO object
       * 
       * @access public
       * @param Date $date The cheque's date.
       * @return void
       */
	  public function setDate( $date ) {
	  
	         $this->date = $date;
	  }
	  /**
       * Gets the id property of the PaymentInfoChequeDTO object
       * 
       * @access public
       * @return Integer The unique identifier of this record
       */
	  public function getId() {

	  	     return $this->id;
	  }
	  /**
       * Gets the bank property of the PaymentInfoChequeDTO object
       * 
       * @access public
       * @return String The name of the bank this cheque's account belongs to.
       */
	  public function getBank() {

	  	     return $this->bank;
	  }
	  /**
       * Gets the bank property of the PaymentInfoChequeDTO object
       * 
       * @access public
       * @return String The cheque's number.
       */
	  public function getNumber() {

             return $this->number;
	  }
	  /**
       * Gets the bank property of the PaymentInfoChequeDTO object
       * 
       * @access public
       * @return String The cheque's number.
       */
	  public function getDate() {

	  	     return $this->date;
	  }
}
?>