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
  * CreditCardDTO
  * @author Jeremy Hahn
  * @version 1.0
  * @copyright Make A Byte, inc
  * @package com.makeabyte.contrib.jbilling.php
  */

class CreditCardDTO {

      var $deleted;          // If the record has been deleted, this field contains “1”, otherwise it contains “0”. Note that deletion cannot be carried out by simply setting a “1” in this field.
      var $expiry;           // Expiration date of the credit card. Usually, card expiration dates are expressed in month/year form, such as “05/11” or “May 2011”. This field contains the last day the card is valid, in this example, “05/31/2011”.
      var $id;               // Unique identifier for this record.
      var $name;             // Credit card owner's name. This is the name that appears physically on the credit card.
      var $number;           // Credit card number. Usually, a 16 digit number.
      var $securityCode;     // CCV (Credit Card Verification) code of the credit card.
      var $type;             // Credit Card type. See Appendix A for acceptable values.

      /**
       * The CreditCardDTO constructor
       * 
       * @access public
       */
	  public function CreditCardDTO() {
	  }
	  /**
	   * Sets the deleted property of the CreditCardDTO object
	   * 
	   * @access public
	   * @param Integer $int If the record has been deleted, this field contains “1”, otherwise it contains “0”. Note that deletion cannot be carried out by simply setting a “1” in this field.
	   * @return void
	   */
	  public function setDeleted( $int ) {
	  	
	  	     $this->deleted = $int;
	  }
	  /**
	   * Sets the expiry property of the CreditCardDTO object
	   * 
	   * @access public
	   * @param Date $date Expiration date of the credit card. Usually, card expiration dates are expressed in month/year form, such as “05/11” or “May 2011”. This field contains the last day the card is valid, in this example, “05/31/2011”.
	   * @return void
	   */
	  public function setExpiry( $date ) {
	  	
	  	     $this->expiry = $date;
	  }
	  /**
	   * Sets the id property of the CreditCardDTO object
	   * 
	   * @access public
	   * @param Integer $id Unique identifier for this record.
	   * @return void
	   */
	  public function setId( $id ) {
	  	
	  	     $this->id = $id;
	  }
	  /**
	   * Sets the name property of the CreditCardDTO object
	   * 
	   * @access public
	   * @param String $name Credit card owner's name. This is the name that appears physically on the credit card.
	   * @return void
	   */
	  public function setName( $name ) {
	  	
	  	     $this->name = $name;
	  }
	  /**
	   * Sets the number property of the CreditCardDTO object
	   * 
	   * @access public
	   * @param String $number Credit card number. Usually, a 16 digit number.
	   * @return void
	   */
	  public function setNumber( $number ) {
	  	
	  	     $this->number = $number;
	  }
	  /**
	   * Sets the securityCode property of the CreditCardDTO object
	   * 
	   * @access public
	   * @param Integer $code CCV (Credit Card Verification) code of the credit card.
	   * @return void
	   */
	  public function setSecurityCode( $code ) {
	  	
	  	     $this->securityCode = $code;
	  }
	  /**
	   * Sets the type property of the CreditCardDTO object
	   * 
	   * @access public
	   * @param Integer $type Credit Card type. See Appendix A for acceptable values.
	   * @return void
	   */
	  public function setType( $type ) {
	  	
	  	     $this->type = $type;
	  }
	  /**
	   * Gets the deleted property of the CreditCardDTO object
	   * 
	   * @access public
	   * @return Integer If the record has been deleted, this field contains “1”, otherwise it contains “0”. Note that deletion cannot be carried out by simply setting a “1” in this field.
	   */
	  public function getDeleted() {
	  	
	  	     return $this->deleted;
	  }
	  /**
	   * Gets the expiry property of the CreditCardDTO object
	   * 
	   * @access public
	   * @return Date Expiration date of the credit card. Usually, card expiration dates are expressed in month/year form, such as “05/11” or “May 2011”. This field contains the last day the card is valid, in this example, “05/31/2011”.
	   */
	  public function getExpiry() {
	  	
	  	     return $this->expiry;
	  }
	  /**
	   * Gets the id property of the CreditCardDTO object
	   * 
	   * @access public
	   * @return Integer Unique identifier for this record.
	   */
	  public function getId() {
	  	
	  	     return $this->id;
	  }
	  /**
	   * Gets the name property of the CreditCardDTO object
	   * 
	   * @access public
	   * @return String Credit card owner's name. This is the name that appears physically on the credit card.
	   */
	  public function getName() {
	  	
	  	     return $this->name;
	  }
	  /**
	   * Gets the number property of the CreditCardDTO object
	   * 
	   * @access public
	   * @return String Credit card number. Usually, a 16 digit number.
	   */
	  public function getNumber() {
	  	
	  	     return $this->number;
	  }
	  /**
	   * Gets the securityCode property of the CreditCardDTO object
	   * 
	   * @access public
	   * @return Integer CCV (Credit Card Verification) code of the credit card.
	   */
	  public function getSecurityCode() {
	  	
	  	     return $this->securityCode;
	  }
	  /**
	   * Gets the type property of the CreditCardDTO object
	   * 
	   * @access public
	   * @return Integer Credit Card type. See Appendix A for acceptable values.
	   */
	  public function getType() {
	  	
	  	     return $this->type;
	  }	    	  
}
?>