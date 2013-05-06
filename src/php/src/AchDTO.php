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
  * AchDTO
  * @author Jeremy Hahn
  * @version 1.0
  * @copyright Make A Byte, inc
  * @package com.makeabyte.contrib.jbilling.php
  */
class AchDTO {

      var $abaRouting;          // ABA routing number
      var $accountName;         // The account name
      var $accountType;         // If this is chequings or a savings account.
      var $bankAccount;         // The account number
      var $bankName;            // The bank name
      var $id;                  // The unique identifier of this record.

      /**
       * The AchDTO constructor
       * 
       * @access public
       */
	  public function AchDTO() {
	  }	    	
      /**
       * Sets the abaRouting property of the AchDTO object
       * 
       * @access public
       * @param String $num ABA routing number
       * @return void
       */
	  public function setAbaRouting( $num ) {

	         $this->abaRouting = $num;
	  }
	  /**
       * Sets the accountName property of the AchDTO object
       * 
       * @access public
       * @param String $name The account name
       * @return void
       */
	  public function setAccountName( $name ) {

	  	     $this->accountName = $name;
	  }
	  /**
       * Sets the accountType property of the AchDTO object
       * 
       * @access public
       * @param Integer $int If this is chequings or a savings account.
       * @return void
       */
	  public function setAccountType( $type ) {

	         $this->accountType = $type;
	  }
	  /**
       * Sets the bankAccount property of the AchDTO object
       * 
       * @access public
       * @param String $acct The account number
       * @return void
       */
	  public function setBankAccount( $acct ) {

             $this->bankAccount = $acct;
	  }
	  /**
       * Sets the bankName property of the AchDTO object
       * 
       * @access public
       * @param String $acct The bank name
       * @return void
       */
	  public function setBankName( $name ) {
	  
	         $this->bankName = $name;
	  }
	  /**
       * Sets the id property of the AchDTO object
       * 
       * @access public
       * @param Integer $id The unique identifier of this record
       * @return void
       */
	  public function setId( $id ) {

	  	     $this->id = $id;
	  }
	  /**
       * Gets the abaRouting property of the AchDTO object
       * 
       * @access public
       * @return ABA routing number
       */
	  public function getAbaRouting() {

	         return $this->abaRouting;
	  }
	  /**
       * Gets the accountName property of the AchDTO object
       * 
       * @access public
       * @return The account name
       */
	  public function getAccountName() {

	  	     return $this->accountName;
	  }
	  /**
       * Gets the accountType property of the AchDTO object
       * 
       * @access public
       * @return If this is chequings or a savings account.
       */
	  public function getAccountType() {

	         return $this->accountType;
	  }
	  /**
       * Gets the bankAccount property of the AchDTO object
       * 
       * @access public
       * @return String $acct The account number
       */
	  public function getBankAccount() {

             return $this->bankAccount;
	  }
	  /**
       * Gets the bankName property of the AchDTO object
       * 
       * @access public
       * @return The bank name
       */
	  public function getBankName() {

	         return $this->bankName;
	  }
	  /**
       * Gets the id property of the AchDTO object
       * 
       * @access public
       * @return The unique identifier of this record
       */
	  public function getId() {

	  	     return $this->id;
	  }
}
?>