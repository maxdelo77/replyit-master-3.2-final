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
  * UserWS
  * @author Jeremy Hahn
  * @version 1.0
  * @copyright Make A Byte, inc
  * @package com.makeabyte.contrib.jbilling.php
  */

class UserWS {

      var $contact;                         // The primary contact information for this user.
      var $createDateTime;                  // Creation date of this data record.
      var $creditCard;                      // Credit card information for this user. Not required for the user creation process.
      var $ach;                             // ACH account information. Not required for the user creation process.
      var $currencyId;                      // Contains the currency code for this user. (Refer to Appendix A for acceptable values.)
      var $deleted;                         // If the record has been deleted, this field contains "1", otherwise it contains "0". Note that deletion cannot be carried out by simply setting a "1" in this field.
      var $failedAttempts;                  // Number of login attempts that have been failed by this user (i.e., the user has entered the wrong password).
      var $invoiceChild;                    // "true" if this is a sub-account (child of a parent account), but this user will still receive invoices.
      var $isParent;                        // "true" if this record is a "parent" user. A parent user can have sub-accounts (children).
      var $language;                        // Name of the language (i.e. "English").
      var $languageId;                      // Contains the preferred language code for this user. Refer to Appendix A for acceptable values.
      var $lastLogin;                       // Date of the last login performed by this user.
      var $lastStatusChange;                // Date of the last status change incurred by this user.
      var $mainRoleId;                      // The level of privilege granted to the user when logged into the system. See Appendix A for acceptable values.
      var $parentId;                        // If the user belongs to a parent record, this field contains the identifier of the parent record.
      var $partnerId;                       // Identifier of the partner this user belongs to.
      var $password;                        // Authenticates the user's identity during login. This could be meaningless if the password is encrypted.
      var $role;                            // The name of the role (i.e. "Clerk" or "Customer").
      var $status;                          // Name of the current status (i.e. "Suspended" or "Active").
      var $statusId;                        // Current status of the user. See Appendix A for acceptable values.
      var $subscriberStatusId;              // Subscriber status for this user. See Appendix A for acceptable values.
      var $userId;                          // A unique number that identifies the customer
      var $userName;                        // Identifies the user during login

      // Added in jBilling 2.0

      /**
       * The type of dynamic balance for this user. Refer to Appendix A for acceptable values.
       * 
       * @var Integer
       */
      public $balanceType;
      
      /**
       * Lists any blacklist matches for this user. See the "jBilling User Guide" for more
       * information on blacklists.
       * 
       * @var String[]
       */
      public $blacklistMatches;

      /**
       * The identifiers of any sub-accounts for this user.
       * 
       * @var Integer
       */
      public $childIds;

      /**
       * The credit limit. Only valid if balanceType is of credit limit type.
       *  
       * @var Double
       */
      public $creditLimit;

      /**
       * The dynamic balance. If balanceType is credit limit, this represents the amount of
       * credit used on the account. If balanceType is pre paid, this represents the pre paid
       * balance remaining.
       * 
       * @var Double
       */
      public $dynamicBalance;

      /**
       * The id of the main order for this customer. This is the order that sets the customer's
       * billing cycle for the management of 'current' orders'. Current orders gather usage.
       * You can set this value to an order and call 'updateUser' to set a new order as the main
       * order.
       *  
       * @var Integer
       */
      public $mainOrderId;

      /**
       * A real-time calculated owing balance.
       * 
       * @var Double
       */
      public $owingBalance;

      /**
       * "true" if the user id is blacklisted. See the "jBilling User Guide" for more information on blacklists.
       *  
       * @var Boolean
       */
      public $userIdBlackListed;

      /**
       * The UserWS constructor
       * 
       * @access public
       */
	  public function UserWS() {
	  }
	  /**
	   * Sets the contact property of the UserWS object
	   * 
	   * @access public
	   * @param ContactWS $contact The primary contact information for this user
	   * @return void
	   */
	  public function setContact( $contact ) {
	  	
	  	     $this->contact = $contact;
	  }
	  /**
	   * Sets the createDateTime property of the UserWS object
	   * 
	   * @access public
	   * @param Date $dateTime Creation date of this data record
	   * @return void
	   */
	  public function setCreateDateTime( $dateTime ) {
	  	
	  	     $this->createDateTime = $dateTime; 
	  }
	  /**
	   * Sets the creditCard property of the UserWS object
	   * 
	   * @access public
	   * @param CreditCardDTO $creditCardDTO Credit card information for this user. Not required for the user creation process.
	   * @return void
	   */
	  public function setCreditCard( $creditCardDTO ) {
	  	
	  	     $this->creditCard = $creditCardDTO;
	  }
          /**
           * Sets the ach property of the UserWS object
           * 
           * @access public
           * @param AchDTO $achDTO ACH data for this user. This is not required when creating the user.
           */
          public function setAch( $achDTO ) {
                     $this->ach = $achDTO;
          }
	  /**
	   * Sets the currencyId property of the UserWS object
	   * 
	   * @access public
	   * @param Integer $id Contains the currency code for this user. (Refer to Appendix A for acceptable values.)
	   * @return void
	   */
	  public function setCurrencyId( $id ) {
	  	
	  	     $this->currencyId = $id;
	  }
	  /**
	   * Sets the deleted property of the UserWS object
	   * 
	   * @access public
	   * @param Integer $int If the record has been deleted, this field contains "1", otherwise it contains "0". Note that deletion cannot be carried out by simply setting a "1" in this field.
	   * @return void
	   */
	  public function setDeleted( $int ) {
	  	
	  	     $this->deleted = $int;
	  }
	  /**
	   * Sets the failedAttempts property of the UserWS object
	   * 
	   * @access public
	   * @param Integer $int Number of login attempts that have been failed by this user (i.e., the user has entered the wrong password).
	   * @return void
	   */
	  public function setFailedAttempts( $int ) {
	  	
	  	    $this->failedAttempts = $int;
	  }
	  /**
	   * Sets the invoiceChild property of the UserWS object
	   * 
	   * @access public
	   * @param bool $bool "true" if this is a sub-account (child of a parent account), but this user will still receive invoices.
	   * @return void
	   */
	  public function setInvoiceChild( $bool ) {
	  	
	  	     $this->invoiceChild = $bool;
	  }
	  /**
	   * Sets the isParent property of the UserWS object
	   * 
	   * @access public
	   * @param bool $bool "true" if this record is a "parent" user. A parent user can have sub-accounts (children).
	   * @return void
	   */
	  public function setIsParent( $bool ) {
	  	
	  	     $this->isParent = $bool;
	  }
	  /**
	   * Sets the language property of the UserWS object
	   * 
	   * @access public
	   * @param String $land Name of the language (i.e. "English").
	   * @return void
	   */
	  public function setLanguage( $lang ) {
	  	
	  	     $this->language = $lang;
	  }
	  /**
	   * Sets the languageId property of the UserWS object
	   * 
	   * @access public
	   * @param Integer $id Contains the preferred language code for this user. Refer to Appendix A for acceptable values.
	   * @return void
	   */
	  public function setLanguageId( $id ) {
	  	
	  	     $this->languageId = $id;
	  }
	  /**
	   * Sets the lastLogin property of the UserWS object
	   * 
	   * @access public
	   * @param Date $date Date of the last login performed by this user.
	   * @return void
	   */
	  public function setLastLogin( $date ) {
	  	
	  	     $this->lastLogin = $date;
	  }
	  /**
	   * Sets the lastStatusChange property of the UserWS class
	   * 
	   * @access public
	   * @param Date $date Date of the last status change incurred by this user.
	   * @return void
	   */
	  public function setLastStatusChange( $date ) {
	  	
	  	     $this->lastStatusChange = $date;
	  }
	  /**
	   * Sets the mainRoleId property of the UserWS class
	   * 
	   * @access public
	   * @param Integer $id The level of privilege granted to the user when logged into the system. See Appendix A for acceptable values.
	   * @return void
	   */
	  public function setMainRoleId( $id ) {
	  	
	  	     $this->mainRoleId = $id;
	  }
	  /**
	   * Sets the parentId property of the UserWS object
	   * 
	   * @access public
	   * @param Integer $int If the user belongs to a parent record, this field contains the identifier of the parent record.
	   * @return void 
	   */
	  public function setParentId( $id ) {
	  	
	  	     $this->parentId = $id;
	  }
	  /**
	   * Sets the partnerId property of the UserWS object
	   * 
	   * @access public
	   * @param Integer $id Identifier of the partner this user belongs to.
	   * @return void
	   */
	  public function setPartnerId( $id ) {
	  	
	  	     $this->partnerId = $id;
	  }
	  /**
	   * Sets the password property of the UserWS object
	   * 
	   * @access public
	   * @param String $password Authenticates the user's identity during login. This could be meaningless if the password is encrypted.
	   * @return void
	   */
	  public function setPassword( $password ) {
	  	
	  	     $this->password = $password;
	  }
	  /**
	   * Sets the role property of the UserWS object
	   * 
	   * @access public
	   * @param Integer $int The name of the role (i.e. "Clerk" or "Customer").
	   * @return void
	   */
	  public function setRole( $int ) {
	  	
	  	     $this->role = $int;
	  }
	  /**
	   * Sets the status property of the UserWS object
	   * 
	   * @access public
	   * @param String $status Name of the current status (i.e. "Suspended" or "Active").
	   * @return void
	   */
	  public function setStatus( $status ) {
	  	
	  	     $this->status = $status;
	  }
	  /**
	   * Sets the statusId property of the UserWS object
	   * 
	   * @access public
	   * @param Integer $id Current status of the user. See Appendix A for acceptable values.
	   * @return void
	   */
	  public function setStatusId( $id ) {
	  	
	  	     $this->statusId = $id;
	  }
	  /**
	   * Sets the subscriberStatusId property of the UserWS object
	   * 
	   * @access public
	   * @param Integer $id Subscriber status for this user. See Appendix A for acceptable values.
	   * $return void
	   */
	  public function setSubscriberStatusId( $id ) {
	  	
	  	     $this->subscriberStatusId = $id;
	  }
	  /**
	   * Sets the userId property of the UserWS object
	   * 
	   * @access public
	   * @param Integer $id A unique number that identifies the customer
	   * @return void
	   */
	  public function setUserId( $id ) {
	  	
	  	     $this->userId = $id;
	  }
	  /**
	   * Sets the username property of the UserWS object
	   * 
	   * @access public
	   * @param String $username Identifies the user during login
	   * @return void
	   */
	  public function setUserName( $username ) {
	  	
	  	     $this->userName = $username;
	  }
	  /**
	   * Gets the contact property of the UserWS object
	   * 
	   * @access public
	   * @return ContactWS The primary contact information for this user 
	   */
	  public function getContact() {
	  	
	  	     return $this->contact;
	  }
	  /**
	   * Gets the createDateTime property of the UserWS object
	   * 
	   * @access public
	   * @return Date Creation date of this data record
	   */
	  public function getCreateDateTime() {
	  	
	  	     return $this->createDateTime; 
	  }
	  /**
	   * Gets the creditCard property of the UserWS object
	   * 
	   * @access public
	   * @return CreditCardDTO Credit card information for this user. Not required for the user creation process.
	   */
	  public function getCreditCard() {
	  	
	  	     return $this->creditCard;
	  }
	  /**
	   * Gets the currencyId property of the UserWS object
	   * 
	   * @access public
	   * @return Integer Contains the currency code for this user. (Refer to Appendix A for acceptable values.)
	   */
	  public function getCurrencyId() {
	  	
	  	     return $this->currencyId;
	  }
	  /**
	   * Gets the deleted property of the UserWS object
	   * 
	   * @access public
	   * @return Integer If the record has been deleted, this field contains "1", otherwise it contains "0". Note that deletion cannot be carried out by simply getting a "1" in this field.
	   */
	  public function getDeleted() {
	  	
	  	     return $this->deleted;
	  }
	  /**
	   * Gets the failedAttempts property of the UserWS object
	   * 
	   * @access public
	   * @return Integer Number of login attempts that have been failed by this user (i.e., the user has entered the wrong password).
	   */
	  public function getFailedAttempts() {
	  	
	  	     return $this->failedAttempts;
	  }
	  /**
	   * Gets the invoiceChild property of the UserWS object
	   * 
	   * @access public
	   * @return bool "true" if this is a sub-account (child of a parent account), but this user will still receive invoices.
	   */
	  public function getInvoiceChild() {
	  	
	  	     return $this->invoiceChild;
	  }
	  /**
	   * Gets the isParent property of the UserWS object
	   * 
	   * @access public
	   * @return bool "true" if this record is a "parent" user. A parent user can have sub-accounts (children).
	   */
	  public function getIsParent() {
	  	
	  	     return $this->isParent;
	  }
	  /**
	   * Gets the language property of the UserWS object
	   * 
	   * @access public
	   * @return String Name of the language (i.e. "English").
	   */
	  public function getLanguage() {
	  	
	  	     return $this->language;
	  }
	  /**
	   * Gets the languageId property of the UserWS object
	   * 
	   * @access public
	   * @return Integer Contains the preferred language code for this user. Refer to Appendix A for acceptable values.
	   */
	  public function getLanguageId() {
	  	
	  	     return $this->languageId;
	  }
	  /**
	   * Gets the lastLogin property of the UserWS object
	   * 
	   * @access public
	   * @return Date Date of the last login performed by this user.
	   */
	  public function getLastLogin() {
	  	
	  	     return $this->lastLogin;
	  }
	  /**
	   * Gets the lastStatusChange property of the UserWS class
	   * 
	   * @access public
	   * @return Date Date of the last status change incurred by this user.
	   */
	  public function getLastStatusChange() {
	  	
	  	     return $this->lastStatusChange;
	  }
	  /**
	   * Gets the mainRoleId property of the UserWS class
	   * 
	   * @access public
	   * @return Integer The level of privilege granted to the user when logged into the system. See Appendix A for acceptable values.
	   */
	  public function getMainRoleId() {
	  	
	  	     return $this->mainRoleId;
	  }
	  /**
	   * Gets the parentId property of the UserWS object
	   * 
	   * @access public
	   * @return Integer If the user belongs to a parent record, this field contains the identifier of the parent record.

	   */
	  public function getParentId() {
	  	
	  	     return $this->parentId;
	  }
	  /**
	   * Gets the partnerId property of the UserWS object
	   * 
	   * @access public
	   * @return Integer Identifier of the partner this user belongs to.
	   */
	  public function getPartnerId() {
	  	
	  	     return $this->partnerId;
	  }
	  /**
	   * Gets the password property of the UserWS object
	   * 
	   * @access public
	   * @return String Authenticates the user's identity during login. This could be meaningless if the password is encrypted.
	   */
	  public function getPassword() {
	  	
	  	     return $this->password;
	  }
	  /**
	   * Gets the role property of the UserWS object
	   * 
	   * @access public
	   * @return Integer The name of the role (i.e. "Clerk" or "Customer").
	   */
	  public function getRole() {
	  	
	  	     return $this->role;
	  }
	  /**
	   * Gets the status property of the UserWS object
	   * 
	   * @access public
	   * @return String Name of the current status (i.e. "Suspended" or "Active").
	   */
	  public function getStatus() {
	  	
	  	     return $this->status;
	  }
	  /**
	   * Gets the statusId property of the UserWS object
	   * 
	   * @access public
	   * @return Integer Current status of the user. See Appendix A for acceptable values.
	   */
	  public function getStatusId() {
	  	
	  	     return $this->statusId;
	  }
	  /**
	   * Gets the subscriberStatusId property of the UserWS object
	   * 
	   * @access public
	   * @return Integer Subscriber status for this user. See Appendix A for acceptable values.
	   */
	  public function getSubscriberStatusId() {
	  	
	  	     return $this->subscriberStatusId;
	  }
	  /**
	   * Gets the userId property of the UserWS object
	   * 
	   * @access public
	   * @return Integer A unique number that identifies the customer
	   */
	  public function getUserId() {
	  	
	  	     return $this->userId;
	  }
	  /**
	   * Gets the username property of the UserWS object
	   * 
	   * @access public
	   * @return String Identifies the user during login
	   */
	  public function getUserName() {
	  	
	  	     return $this->userName;
	  }
	  
	  public function setBalanceType( $value ) {

		     $this->balanceType = $value;
	  }

	  public function setBlacklistMatches( $value ) {

			 $this->blacklistMatches = $value;
	  }

	  public function setChildIds( $value ) {

			 $this->childIds = $value;
	  }

	  public function setCreditLimit( $value ) {

			 $this->creditLimit = $value;
	  }

	  public function setDynamicBalance( $value ) {

			 $this->dynamicBalance = $value;
	  }

	  public function setMainOrderId( $value ) {

			 $this->mainOrderId = $value;
  	  }

	  public function setOwingBalance( $value ) {

			 $this->owingBalance = $value;
	  }

	  public function setUserIdBlackListed( $value ) {

			 $this->userIdBlackListed = $value;
	  }
	  
	  public function getBalanceType() {

			 return $this->balanceType;
	  }

	  public function getBlacklistMatches() {

			 return $this->blacklistMatches;
	  }

	  public function getChildIds() {

			 return $this->childIds;
	  }

	  public function getCreditLimit() {

			 return $this->creditLimit;
	  }

	  public function getDynamicBalance() {

			 return $this->dynamicBalance;
	  }

	  public function getMainOrderId() {

			 return $this->mainOrderId;
	  }

	  public function getOwingBalance() {

			 return $this->owingBalance;
	  }

	  public function getUserIdBlackListed() {

			 return $this->userIdBlackListed;
	  }
	  
	  public function getAch() {
	  		 return $this->ach;
	  }
}
?>
