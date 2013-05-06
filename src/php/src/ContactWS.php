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
  * ContactWS
  * @author Jeremy Hahn
  * @version 1.0
  * @copyright Make A Byte, inc
  * @package com.makeabyte.contrib.jbilling.php
  */
  
class ContactWS {

      var $address1;                // First line for the address.
      var $address2;                // Second line for the address.
      var $city;                    // City of this contact.
      var $countryCode;             // Country code for this contact (Appendix A contains a list of acceptable country codes).
      var $createDate;              // Date this contact record was first created.
      var $deleted;                 // If the record has been deleted, this field contains “1”, otherwise it contains “0”. Note that deletion cannot be carried out by simply setting a “1” in this field.
      var $email;                   // E-Mail address of this contact.
      var $faxAreaCode;             // Area Code for the fax number, if any.
      var $faxCountryCode;          // Country Code for the fax number, if any.
      var $faxNumber;               // Fax number.
      var $firstName;               // First name for the contact.
      var $id;                      // Unique identifier of this contact.
      var $include;                 // “1”, if this contact is marked as included in notifications.
      var $initial;                 // Middle name initials, if any.
      var $lastName;                // Contact's surname.
      var $organizationName;        // Name of the organization the contact belongs to.
      var $phoneAreaCode;           // Phone number Area Code.
      var $phoneCountryCode;        // Country Code.
      var $phoneNumber;             // Phone Number.
      var $postalCode;              // ZIP Code for the contact's address.
      var $stateProvince;           // State or Province of the contact's address.
      var $title;                   // Title for the contact, such as “Mr.” or “Dr.”.
      var $userId;                  // Identifies the user account to which this contact belongs to.

      public $fieldNames; // string
  	  public $fieldValues; // string

      /**
       * The ContactDTO constructor
       * 
       * @access public
       */
	  public function ContactDTO() {
	  }
	  /**
	   * Sets the address1 property of the ContactDTO object
	   * 
	   * @access public
	   * @param String $address First line for the address.
	   * @return void
	   */
	  public function setAddress1( $address ) {
	  	
	  	     $this->address1 = $address;
	  }
	  /**
	   * Sets the address2 property of the ContactDTO object
	   * 
	   * @access public
	   * @param String $address Second line for the address.
	   * @return void
	   */
	  public function setAddress2( $address ) {
	  	     
	  	     $this->address2 = $address;
	  }
	  /**
	   * Sets the city property of the ContactDTO object
	   * 
	   * @access public
	   * @param String $city City of this contact.
	   * @return void
	   */
	  public function setCity( $city ) {
	  	
	  	     $this->city = $city;
	  }
	  /**
	   * Sets the city property of the ContactDTO object
	   * 
	   * @access public
	   * @param String $city City of this contact.
	   * @return void
	   */
	  public function setCountryCode( $code ) {
	  	
	  	     $this->countryCode = $code;
	  }
	  /**
	   * Sets the createDate property of the ContactDTO object
	   * 
	   * @access public
	   * @param Date $date Date this contact record was first created.
	   * @return void
	   */
	  public function setCreateDate( $date ) {
	  	
	  	     $this->createDate = $date;
	  }
	  /**
	   * Sets the deleted property of the ContactDTO object
	   * 
	   * @access public
	   * @param Integer $int If the record has been deleted, this field contains “1”, otherwise it contains “0”. Note that deletion cannot be carried out by simply setting a “1” in this field.
	   * @return void
	   */
	  public function setDeleted( $int ) {
	  	
	  	     $this->deleted = $int;
	  }
	  /**
	   * Sets the email property of the ContactDTO object
	   * 
	   * @access public
	   * @param String $email E-Mail address of this contact.
	   * @return void
	   */
	  public function setEmail( $email ) {
	  	
	  	     $this->email = $email;
	  }
	  /**
	   * Sets the faxAreaCode property of the ContactDTO object
	   * 
	   * @access public
	   * @param Integer $code Area Code for the fax number, if any.
	   * @return void
	   */
	  public function setFaxAreaCode( $code ) {
	  	
	  	     $this->faxAreaCode = $code;
	  }
	  /**
	   * Sets the faxCountryCode property of the ContactDTO object
	   * 
	   * @access public
	   * @param Integer $code Country Code for the fax number, if any.
	   * @return void
	   */
	  public function setFaxCountryCode( $code ) {
	  	
	  	     $this->faxCountryCode = $code;
	  }
	  /**
	   * Sets the faxNumber property of the ContactDTO object
	   * 
	   * @access public
	   * @param Integer $number Fax number. 
	   * @return void
	   */
	  public function setFaxNumber( $number ) {
	  	
	  	     $this->faxNumber = $number;
	  }
	  /**
	   * Sets the firstName property of the ContactDTO object
	   * 
	   * @access public
	   * @param String $name First name for the contact.
	   * @return void 
	   */
	  public function setFirstName( $name ) {
	  	
	  	     $this->firstName = $name;
	  }
	  /**
	   * Sets the id property of the ContactDTO object
	   * 
	   * @access public
	   * @param Integer $id Unique identifier of this contact. 
	   * @return void
	   */
	  public function setId( $id ) {
	  	
	  	     $this->id = $id;
	  }
	  /**
	   * Sets the include property of the ContactDTO object
	   * 
	   * @access public
	   * @param Integer $inc “1”, if this contact is marked as included in notifications. 
	   * @return void
	   */
	  public function setInclude( $inc ) {
	  	
	  	     $this->include = $inc;
	  }
	  /**
	   * Sets the initial property of the ContactDTO object
	   * 
	   * @access public
	   * @param Integer $inc Middle name initials, if any. 
	   * @return void
	   */
	  public function setInitial( $initial ) {
	  	
	  	     $this->initial = $initial;
	  }
	  /**
	   * Sets the lastName property of the ContactDTO object
	   * 
	   * @access public
	   * @param String $name Contact's surname.
	   * @return void
	   */
	  public function setLastName( $name ) {
	  	
	  	     $this->lastName = $name;
	  }
	  /**
	   * Sets the organizationName property of the ContactDTO object
	   * 
	   * @access public
	   * @param String $name Name of the organization the contact belongs to.
	   * @return void
	   */
	  public function setOrganizationName( $name ) {
	  	
	  	     $this->organizationName = $name;
	  }
	  /**
	   * Sets the phoneAreaCode property of the ContactDTO object
	   * 
	   * @access public
	   * @param Integer $code Phone number Area Code.
	   * @return void
	   */
	  public function setPhoneAreaCode( $code ) {
	  	   
	  	     $this->phoneAreaCode = $code;
	  }
	  /**
	   * Sets the phoneCountryCode property of the ContactDTO object
	   * 
	   * @access public
	   * @param Integer $code Country Code.
	   * @return void
	   */
	  public function setPhoneCountryCode( $code ) {
	  	
	  	     $this->phoneCountryCode = $code;
	  }
	  /**
	   * Sets the phoneNumber property of the ContactDTO object
	   * 
	   * @access public
	   * @param String $number Phone Number.
	   * @return void
	   */
	  public function setPhoneNumber( $number ) {
	  	
	  	     $this->phoneNumber = $number;
	  }
	  /**
	   * Sets the postalCode property of the ContactDTO object
	   * 
	   * @access public
	   * @param String $code ZIP Code for the contact's address.
	   * @return void
	   */
	  public function setPostalCode( $code ) {
	  	     
	  	     $this->postalCode = $code;
	  }
	  /**
	   * Sets the stateProvince property of the ContactDTO object
	   * 
	   * @access public
	   * @param String $st State or Province of the contact's address.
	   * @return void
	   */
	  public function setStateProvince( $st ) {
	  	
	  	     $this->stateProvince = $st;
	  }
	  /**
	   * Sets the title property of the ContactDTO object
	   * 
	   * @access public
	   * @param String $title Title for the contact, such as “Mr.” or “Dr.”.
	   * @return void
	   */
	  public function setTitle( $title ) {
	  	
	  	     $this->title = $title;
	  }
	  /**
	   * Sets the userId property of the ContactDTO object
	   * 
	   * @access public
	   * @param Integer $id Identifies the user account to which this contact belongs to.
	   * @return void
	   */
	  public function setUserId( $id ) {
	  	
	  	     $this->userId = $id;
	  }
	  /**
	   * Gets the address1 property of the ContactDTO object
	   * 
	   * @access public
	   * @return String First line for the address.
	   */
	  public function getAddress1() {
	  	
	  	     return $this->address1;
	  }
	  /**
	   * Gets the address2 property of the ContactDTO object
	   * 
	   * @access public
	   * @return String Second line for the address.
	   */
	  public function getAddress2() {
	  	     
	  	     return $this->address2;
	  }
	  /**
	   * Gets the city property of the ContactDTO object
	   * 
	   * @access public
	   * @return String City of this contact.
	   */
	  public function getCity() {
	  	
	  	     return $this->city;
	  }
	  /**
	   * Gets the city property of the ContactDTO object
	   * 
	   * @access public
	   * @return String City of this contact.
	   */
	  public function getCountryCode() {
	  	
	  	     return $this->countryCode;
	  }
	  /**
	   * Gets the createDate property of the ContactDTO object
	   * 
	   * @access public
	   * @return Date Date this contact record was first created.
	   */
	  public function getCreateDate() {
	  	
	  	     return $this->createDate;
	  }
	  /**
	   * Gets the deleted property of the ContactDTO object
	   * 
	   * @access public
	   * @return Integer If the record has been deleted, this field contains “1”, otherwise it contains “0”. Note that deletion cannot be carried out by simply setting a “1” in this field.
	   */
	  public function getDeleted() {
	  	
	  	     return $this->deleted;
	  }
	  /**
	   * Gets the email property of the ContactDTO object
	   * 
	   * @access public
	   * @return String E-Mail address of this contact.
	   */
	  public function getEmail() {
	  	
	  	     return $this->email;
	  }
	  /**
	   * Gets the faxAreaCode property of the ContactDTO object
	   * 
	   * @access public
	   * @return Integer Area Code for the fax number, if any.
	   */
	  public function getFaxAreaCode() {
	  	
	  	     return $this->faxAreaCode;
	  }
	  /**
	   * Gets the faxCountryCode property of the ContactDTO object
	   * 
	   * @access public
	   * @return Integer Country Code for the fax number, if any.
	   */
	  public function getFaxCountryCode() {
	  	
	  	     return $this->faxCountryCode;
	  }
	  /**
	   * Gets the faxNumber property of the ContactDTO object
	   * 
	   * @access public
	   * @return Integer Fax number.
	   */
	  public function getFaxNumber() {
	  	
	  	     return $this->faxNumber;
	  }
	  /**
	   * Gets the firstName property of the ContactDTO object
	   * 
	   * @access public
	   * @return String First name for the contact.
	   */
	  public function getFirstName() {
	  	
	  	     return $this->firstName;
	  }
	  /**
	   * Gets the id property of the ContactDTO object
	   * 
	   * @access public
	   * @return Integer Unique identifier of this contact. 
	   */
	  public function getId() {
	  	
	  	     return $this->id;
	  }
	  /**
	   * Gets the include property of the ContactDTO object
	   * 
	   * @access public
	   * @return Integer “1”, if this contact is marked as included in notifications. 
	   */
	  public function getInclude() {
	  	
	  	     return $this->include;
	  }
	  /**
	   * Gets the initial property of the ContactDTO object
	   * 
	   * @access public
	   * @return Integer Middle name initials, if any. 
	   */
	  public function getInitial() {
	  	
	  	     return $this->initial;
	  }
	  /**
	   * Gets the lastName property of the ContactDTO object
	   * 
	   * @access public
	   * @return String Contact's surname.
	   */
	  public function getLastName() {
	  	
	  	     return $this->lastName;
	  }
	  /**
	   * Gets the organizationName property of the ContactDTO object
	   * 
	   * @access public
	   * @return String Name of the organization the contact belongs to.
	   */
	  public function getOrganizationName() {
	  	
	  	     return $this->organizationName;
	  }
	  /**
	   * Gets the phoneAreaCode property of the ContactDTO object
	   * 
	   * @access public
	   * @return Integer Phone number Area Code.
	   */
	  public function getPhoneAreaCode() {
	  	   
	  	     return $this->phoneAreaCode;
	  }
	  /**
	   * Gets the phoneCountryCode property of the ContactDTO object
	   * 
	   * @access public
	   * @return Integer Country Code.
	   */
	  public function getPhoneCountryCode() {
	  	
	  	     return $this->phoneCountryCode;
	  }
	  /**
	   * Gets the phoneNumber property of the ContactDTO object
	   * 
	   * @access public
	   * @return String Phone Number.
	   */
	  public function getPhoneNumber() {
	  	
	  	     return $this->phoneNumber;
	  }
	  /**
	   * Gets the postalCode property of the ContactDTO object
	   * 
	   * @access public
	   * @return String ZIP Code for the contact's address.
	   */
	  public function getPostalCode() {
	  	     
	  	     return $this->postalCode;
	  }
	  /**
	   * Gets the stateProvince property of the ContactDTO object
	   * 
	   * @access public
	   * @return String State or Province of the contact's address.
	   */
	  public function getStateProvince() {
	  	
	  	     return $this->stateProvince;
	  }
	  /**
	   * Gets the title property of the ContactDTO object
	   * 
	   * @access public
	   * @return String Title for the contact, such as “Mr.” or “Dr.”.
	   */
	  public function getTitle() {
	  	
	  	     return $this->title;
	  }
	  /**
	   * Gets the userId property of the ContactDTO object
	   * 
	   * @access public
	   * @return Integer Identifies the user account to which this contact belongs to.
	   */
	  public function getUserId() {
	  	
	  	     return $this->userId;
	  }

	  public function setFieldNames( $names ) {

	  		 $this->fieldNames = $names;
	  }

	  public function getFieldNames() {

	  		 return $this->fieldNames;
	  }

	  public function setFieldValues( $values ) {

	  		 $this->values = $values;
	  }

	  public function getFieldValues() {

	  		 return $this->fieldValues;
	  }
}
?>