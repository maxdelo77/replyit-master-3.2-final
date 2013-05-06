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
  * OrderWS
  * @author Jeremy Hahn
  * @version 1.0
  * @copyright Make A Byte, inc
  * @package com.makeabyte.contrib.jbilling.php
  */

class OrderWS {

	  /**
	   * The point in time when this order will start being active, reflecting when the 
	   * customer will be invoiced for the items included.  A null value indicates
	   * that the order was active at creation time (see field createDate).
	   * @var Date 
	   */
      var $activeSince;
      
      /**
       * The point in time when this order stops being active. After this date, the order
       * will stop generating new invoices, indicating that the services included in this
       * order should stop being delivered to the customer. A null value would specify an
       * open-ended order. Such order never expires; it is considered on-going and will
       * require explicit cancellation for it to stop generating invoices.
       * 
       * @var Date
       */
      var $activeUntil;

      /**
       * How many periods in advance the order should invoice for. Leave with a '0' unless
       * you have configured the system to work with anticipated periods.
       * 
       * @var Integer
       */
      var $anticipatePeriods;

      /**
       * Indicates if this order is to be paid for before or after the service is provided.
       * Pre-paid orders are invoiced in advance to the customer, while post-paid are only
	   * invoiced once the goods or services included in the order have been delivered. “1”
	   * means “pre-paid”, while “2” means “post-paid”.
	   * 
       * @var Integer
       */
      var $billingTypeId;

      /**
       * This will show the word that represents the billing type. It is ignored when you
       * submit the object. (read-only)
       * 
       * @var String
       */
      var $billingTypeStr;

      /**
       * A time stamp with the date and time when this order was originally created.
       * 
       * @var Date
       */
      var $createDate;

      /**
       * The id of the user that has created this order.
       * 
       * @var Integer
       */
      var $createdBy;
      
      /**
       * Currency code. Refer to Appendix A for a list of acceptable values.
       * 
       * @var Integer
       */
      var $currencyId;
      
      /**
       * A flag that indicates if this record is logically  deleted in the database.
       * This allows for ‘undo’ of deletions. Valid values are 0 – the record is not
       * deleted 1 – the record is considered deleted.
       * 
       * @var Integer 
       */
      var $deleted;
      
      /**
       * Only used for specific Italian business rules.
       * 
       * @var Integer
       */
      var $dfFm;
      
      /**
       * If this order has a specified due date, this will the the units (days, months, years).
       * See Appendix A for valid values.
       * 
       * @var Integer
       */
      var $dueDateUnitId;
      
      /**
       * How many units will be used for the due date.
       * 
       * @var Integer
       */
      var $dueDateValue;
      
      /**
       * A unique number that identifies this record.
       * 
       * @var Integer
       */
      var $id;
      
      /**
       * When the order has expiration notification, this field tells when the last one was sent.
       * 
       * @var Date
       */
      var $lastNotified;
      
      /**
       * The date when this order should generate a new invoice. Meaning that until that date
       * (and excluding that date), the customer has been invoiced for the service included in
       * this order.
       * 
       * @var Date
       */
      var $nextBillableDay;
      
      /**
       * A free text field for your notes.
       * 
       * @var String
       */
      var $notes;
      
      /**
       * “1” if this order's notes will be included in the invoice, or “0” if not.
       * 
       * @var Integer
       */
      var $notesInInvoice;
      
      /**
       * What step has been completed in the order
       * 
       * @var Integer
       */
      var $notificationStep;

      /**
       * If this order will generate notification as the 'active since' date approaches.
       * 
       * @var Integer
       */
      var $notify;

      /**
       *  The order lines belonging to this order. These objects will specify the items
       *  included in this order with their prices and quantities.
       *  
       *  @var array
       *  @see OrderLineWS
       */
      var $orderLines;

      /**
       * A flag to indicate if this order should generate an invoice on its own.
       * The default behavior is that many orders can generate one invoice.
       * 
       * @var Integer
       */
      var $ownInvoice;

      /**
       *  Indicates the periodicity of this order. In other words, how often this order will
       *  generate an invoice. Examples of periods are: one time, monthly, weekly, etc. Period
       *  codes can be seen in jbilling's User Interface under “Orders -> Periods”.
       *  
       *  @var Integer
       */
      var $period;

      /**
       * The description of the order period. (read-only)
       *
       * @var String
       */
      var $periodStr;

      /** 
       * An order has to be on status ‘Active’ in order to generate invoices. An order usually
       * starts in active status, and only goes to suspended or finished when the customer
       * fails to make the required payments. The steps and actions taken due to late payments
       * are part of the ageing process. See Appendix A for a list of acceptable order status codes.
       * 
       * @var Integer
       */
      var $statusId;

      /**
       * This order belongs to the user specified by this field.
       *
       * @var Integer
       */
      var $userId;

      // Added to support jbilling 1_1_2

      var $isCurrent;
      var $versionNum;
      var $cycleStarts;
      var $pricingFields; 
      var $statusStr;
      var $timeUnitStr;

      /**
       * The OrderWS constructor
       * 
       * @access public
       */
	  public function OrderWS() {
	  }
	  /**
	   * Sets the activeSince property for the OrderWS object
	   * 
	   * @access public
	   * @param Date $date The point in time when this order will start being active, reflecting when
	   * 				   the customer will be invoiced for the items included.  A null value indicates
	   * 				   that the order was active at creation time (see field createDate).
	   * @return void 
	   */
	  public function setActiveSince( $date ) {

	         $this->activeSince = $date;
	  }
	  /**
	   * Sets the activeUntil property for the OrderWS object
	   * 
	   * @access public
	   * @param Date $date The point in time when this order stops being active. After this date, the
	   * 				   order will stop generating new invoices, indicating that the services included
	   * 				   in this order should stop being delivered to the customer. A null value would
	   * 				   specify an open-ended order. Such order never expires; it is considered on-going
	   * 				   and will require explicit cancellation for it to stop generating invoices.
	   * @return void 
	   */
	  public function setActiveUntil( $date ) {
	  	
	  	     $this->activeUntil = $date;
	  }
	  /**
	   * Sets the anticipatedPeriods property for the OrderWS object
	   * 
	   * @access public
	   * @param Integer $int How many periods in advance the order should invoice for. Leave with a '0'
	   * 					 unless you have configured the system to work with anticipated periods.
	   * @return void 
	   */
	  public function setAnticipatedPeriods( $int ) {
	   	
	   	      $this->anticipatedPeriods = (integer)$int;
	  }
	  /**
 	   * Sets the billingTypeId property for the OrderWS object
	   * 
	   * @access public
	   * @param Integer $int Indicates if this order is to be paid for before or after the service is provided. 
	   * 					 Pre-paid orders are invoiced in advance to the customer, while post-paid are only
	   * 					 invoiced once the goods or services included in the order have been delivered.
	   * 					 “1” means “pre-paid”, while “2” means “post-paid”. 
	   * @return void 
	   */
      public function setBillingTypeId( $id ) {

             $this->billingTypeId = (integer)$id;
      }
      /**
 	   * Sets the billingTypeStr property for the OrderWS object
	   * 
	   * @access public
	   * @param String $str This will show the word that represents the billing type. It is
	   * 					ignored when you submit the object. (read only)   
	   * @return void 
	   */
      public function setBillingTypeStr( $str ) {

             $this->billingTypeId = (string)$str;
      }
      /**
 	   * Sets the createDate property for the OrderWS object
	   * 
	   * @access public
	   * @param Date $date (read only). A time stamp with the date and time when this order was originally created.   
	   * @return void 
	   */
      public function setCreateDate( $date ) {
      	
      	     $this->createDate = $date;
      }
      /**
 	   * Sets the createdBy property for the OrderWS object
	   * 
	   * @access public
	   * @param Integer $id The id of the user that has created this order.   
	   * @return void 
	   */
      public function setCreatedBy( $id ) {
      	
      	     $this->createdBy = (integer)$id;
      }
      /**
 	   * Sets the currentId property for the OrderWS object
	   * 
	   * @access public
	   * @param Integer $id Currency code. Refer to Appendix A for a list of acceptable values.   
	   * @return void
	   */
	   public function setCurrencyId( $id ) {
	   	
	   	      $this->currencyId = (integer)$id;
	   }
	   /**
 	    * Sets the deleted property for the OrderWS object
	    * 
	    * @access public
	    * @param Integer $id A flag that indicates if this record is logically  deleted in the database. 
	    * 					 This allows for ‘undo’ of deletions. Valid values are 0 – the record is not
	    * 					 deleted 1 – the record is considered deleted.   
	    * @return void
	    */
       public function setDeleted( $id ) {

              $this->deleted = (int)$id;
       }
       /**
 	    * Sets the dfFm property for the OrderWS object
	    * 
	    * @access public
	    * @param Integer $id Only used for specific Italian business rules.   
	    * @return void
	    */
	   public function setDfFm( $id ) {
	   	
	   	      $this->dfFm = (integer)$id;
	   } 
       /**
 	    * Sets the dueDateUbitId property for the OrderWS object
	    * 
	    * @access public
	    * @param Integer $id If this order has a specified due date, this will the the units (days, months, years).
	    * 				     See Appendix A for valid values.   
	    * @return void
	    */
       public function setDueDateUnitId ( $id ) {
       	
       	      $this->dueDateUnitId = (integer)$id;
       }
       /**
 	    * Sets the dueDateValue property for the OrderWS object
	    * 
	    * @access public
	    * @param Integer $val How many units will be used for the due date.   
	    * @return void
	    */     
       public function setDueDateValue( $val ) {
      	
      	     $this->dueDateValue = (integer)$val;
       }
       /**
 	    * Sets the id property for the OrderWS object
	    * 
	    * @access public
	    * @param Integer $id A unique number that identifies this record.
	    * @return void
	    */
       public function setId( $val ) {

      	     $this->id = (integer)$val;
       }
       /**
 	    * Sets the lastNotified property for the OrderWS object
	    * 
	    * @access public
	    * @param Date $date When the order has expiration notification, this field tells when the last one was sent.
	    * @return void
	    */
       public function setLastNotified( $date ) {
       	
       	      $this->lastNotified = $date;
       }
       /**
 	    * Sets the nextBillableDay property for the OrderWS object
	    * 
	    * @access public
	    * @param Date $date The date when this order should generate a new invoice. Meaning that until that date
	    * 					(and excluding that date), the customer has been invoiced for the service included in
	    * 					this order.
	    * @return void
	    */
	   public function setNextBillableDay( $date ) {
	    
	           $this->nextBillableDay = $date;	
	   }
	   /**
  	    * Sets the notes property for the OrderWS object
	    * 
	    * @access public
	    * @param String $note A free text field for your notes.
	    * @return void
	    */
	   public function setNotes( $note ) {
	   	
	          $this->notes = (string)$note;
	   }
       /**
  	    * Sets the notesInInvoice property for the OrderWS object
	    * 
	    * @access public
	    * @param Integer $id “1” if this order's notes will be included in the invoice, or “0” if not.
	    * @return void
	    */
       public function setNotesInInvoice( $id ) {
       	
       	      $this->notesInInvoice = (integer)$id;
       }
       /**
  	    * Sets the notificationStep property for the OrderWS object
	    * 
	    * @access public
	    * @param Integer $id What step has been completed in the order
	    * @return void
	    */
       public function setNotificationStep( $id ) {
       	
       	      $this->notificationStep = (integer)$id;
       }
       /**
  	    * Sets the notify property for the OrderWS object
	    * 
	    * @access public
	    * @param Integer $id If this order will generate notification as the 'active since' date approaches.
	    * @return void
	    */
       public function setNotify( $id ) {
       	
       	      $this->notify = (integer)$id;
       }
       /**
  	    * Sets the orderLines property for the OrderWS object
	    * 
	    * @access public
	    * @param Array $orderLines The order lines belonging to this order. These objects will specify the items
	    * 						   included in this order with their prices and quantities. See the OrderLineWS
	    * 						   specification for more information.
	    * @return void
	    */
       public function setOrderLines( $orderLines ) {

       	      $this->orderLines = $orderLines;
       }
       /**
  	    * Sets the invoice property for the OrderWS object
	    * 
	    * @access public
	    * @param Integer $flag A flag to indicate if this order should generate an invoice on its own. The default
	    * 					   behavior is that many orders can generate one invoice.
	    * @return void
	    */
	   public function setInvoice( $flag ) {
	   	
	   	      $this->invoice = (integer)$flag;
	   }
	   /**
  	    * Sets the period property for the OrderWS object
	    * 
	    * @access public
	    * @param Integer $int Indicates the periodicity of this order. In other words, how often this order will
	    * 					  generate an invoice. Examples of periods are: one time, monthly, weekly, etc. Period
	    * 					  codes can be seen in jbilling's User Interface under “Orders -> Periods”.
	    * @return void
	    */
       public function setPeriod( $int ) {
       
              $this->period = (integer)$int;
       }      
       /**
  	    * Sets the periodStr property for the OrderWS object
	    * 
	    * @access public
	    * @param String $str (read only). The description of the order period. 
	    * @return void
	    */
       public function setPeriodStr( $str ) {
       	
       	      $this->periodStr = (string)$str;
       }
       /**
  	    * Sets the statusId property for the OrderWS object
	    * 
	    * @access public
	    * @param Integer $id An order has to be on status ‘Active’ in order to generate invoices. An order usually starts
	    * 				     in active status, and only goes to suspended or finished when the customer fails to make the
	    * 					 required payments. The steps and actions taken due to late payments are part of the ageing
	    * 					 process. See Appendix A for a list of acceptable order status codes. 
	    * @return void
	    */
       public function setStatusId( $id ) {

       	      $this->statusId = (integer)$id;
       }
      /**
  	   * Sets the userId property for the OrderWS object
	   * 
	   * @access public
	   * @param Integer $id This order belongs to the user specified by this field. 
	   * @return void
	   */
	  public function setUserId( $id ) {
	    
	           $this->userId = (integer)$id;
      }
      /**
	   * Gets the activeSince property for the OrderWS object
	   * 
	   * @access public
	   * @return Date The point in time when this order will start being active, reflecting when the customer will be invoiced
	   * 			  for the items included.  A null value indicates that the order was active at creation time (see field createDate).
	   */
	  public function getActiveSince() {

	         return $this->activeSince;
	  }
	  /**
	   * Gets the activeUntil property for the OrderWS object
	   * 
	   * @access public
	   * @return Date The point in time when this order stops being active. After this date, the order will stop generating new
	   * 			  invoices, indicating that the services included in this order should stop being delivered to the customer.
	   * 			  A null value would specify an open-ended order. Such order never expires; it is considered on-going and
	   * 			  will require explicit cancellation for it to stop generating invoices.
	   */
	  public function getActiveUntil() {
	  	
	  	     return $this->activeUntil;
	  }
	  /**
	   * Gets the anticipatedPeriods property for the OrderWS object
	   * 
	   * @access public
	   * @return Integer How many periods in advance the order should invoice for. Leave with a '0' unless you have configured
	   * 				 the system to work with anticipated periods.
	   */
	  public function getAnticipatedPeriods() {
	   	
	   	      return $this->anticipatedPeriods;
	  }
	  /**
 	   * Gets the billingTypeId property for the OrderWS object
	   * 
	   * @access public
	   * @return Integer Indicates if this order is to be paid for before or after the service is provided. Pre-paid orders are
	   * 				 invoiced in advance to the customer, while post-paid are only invoiced once the goods or services included
	   * 				 in the order have been delivered. “1” means “pre-paid”, while “2” means “post-paid”. 
	   */
      public function getBillingTypeId() {

             return $this->billingTypeId;
      }
      /**
 	   * Gets the billingTypeStr property for the OrderWS object
	   * 
	   * @access public
	   * @return String (read only).This will show the word that represents the billing type. It is ignored when you submit the object.   
	   */
      public function getBillingTypeStr() {

             return $this->billingTypeStr;
      }
      /**
 	   * Gets the createDate property for the OrderWS object
	   * 
	   * @access public
	   * @return Date (read only). A time stamp with the date and time when this order was originally created.   
	   */
      public function getCreateDate() {
      	
      	     return $this->createDate;
      }
      /**
 	   * Gets the createdBy property for the OrderWS object
	   * 
	   * @access public
	   * @return Integer The id of the user that has created this order.   
	   */
      public function getCreatedBy() {
      	
      	     return $this->createdBy;
      }
      /**
 	   * Gets the currentId property for the OrderWS object
	   * 
	   * @access public
	   * @return Integer Currency code. Refer to Appendix A for a list of acceptable values.   
	   */
	   public function getCurrencyId() {
	   	
	   	      return $this->currencyId;
	   }
	   /**
 	    * Gets the deleted property for the OrderWS object
	    * 
	    * @access public
	    * @return Integer A flag that indicates if this record is logically  deleted in the database. This allows for ‘undo’ of deletions.
	    * 				  Valid values are 0 – the record is not deleted 1 – the record is considered deleted.   
	    */
       public function getDeleted() {
       
              return $this->deleted;
       }
       /**
 	    * Gets the dfFm property for the OrderWS object
	    * 
	    * @access public
	    * @return Integer Only used for specific Italian business rules.   
	    */
	   public function getDfFm() {
	   	
	   	      return $this->dfFm;
	   } 
       /**
 	    * Gets the dueDateUbitId property for the OrderWS object
	    * 
	    * @access public
	    * @return Integer If this order has a specified due date, this will the the units (days, months, years). See Appendix A for valid values.   
	    */
       public function getDueDateUnitId() {
       	
       	      return $this->dueDateUnitId;
       }
       /**
 	    * Gets the dueDateValue property for the OrderWS object
	    * 
	    * @access public
	    * @return Integer How many units will be used for the due date.   
	    */     
       public function getDueDateValue() {
      	
      	     return $this->dueDateValue;
       }
       /**
 	    * Gets the id property for the OrderWS object
	    * 
	    * @access public
	    * @return Integer A unique number that identifies this record.
	    */
       public function getId( ) {

      	     return $this->id;
       }
       /**
 	    * Gets the lastNotified property for the OrderWS object
	    * 
	    * @access public
	    * @return Date When the order has expiration notification, this field tells when the last one was sent.
	    */
       public function getLastNotified() {
       	
       	      return $this->lastNotified;
       }
       /**
 	    * Gets the nextBillableDay property for the OrderWS object
	    * 
	    * @access public
	    * @return Date The date when this order should generate a new invoice. Meaning that until that date (and excluding that date), the
	    * 			   customer has been invoiced for the service included in this order.
	    */
	   public function nextBillableDay() {
	    
	           return $this->nextBillableDay;	
	   }
	   /**
  	    * Gets the notes property for the OrderWS object
	    * 
	    * @access public
	    * @return String A free text field for your notes.
	    */
	   public function getNotes() {
	   	
	          return $this->notes;
	   }
       /**
  	    * Gets the notesInInvoice property for the OrderWS object
	    * 
	    * @access public
	    * @return Integer “1” if this order's notes will be included in the invoice, or “0” if not.
	    */
       public function notesInInvoice() {
       	
       	      return $this->notesInInvoice;
       }
       /**
  	    * Gets the notificationStep property for the OrderWS object
	    * 
	    * @access public
	    * @return Integer What step has been completed in the order
	    */
       public function getNotificationStep() {
       	
       	      return $this->notificationStep;
       }
       /**
  	    * Gets the notify property for the OrderWS object
	    * 
	    * @access public
	    * @return Integer If this order will generate notification as the 'active since' date approaches.
	    */
       public function getNotify() {
       	
       	      return $this->notify;
       }
       /**
  	    * Gets the orderLines property for the OrderWS object
	    * 
	    * @access public
	    * @return Array The order lines belonging to this order. These objects will specify the items included in this order with
	    * 				their prices and quantities. See the OrderLineWS specification for more information.
	    */
       public function getOrderLines() {
       	
       	      return $this->orderLines;
       }
       /**
  	    * Gets the invoice property for the OrderWS object
	    * 
	    * @access public
	    * @return Integer A flag to indicate if this order should generate an invoice on its own. The default behavior is that
	    * 				  many orders can generate one invoice.
	    */
	   public function getInvoice() {
	   	
	   	      return $this->invoice;
	   }
	   /**
  	    * Gets the period property for the OrderWS object
	    * 
	    * @access public
	    * @return Integer Indicates the periodicity of this order. In other words, how often this order will generate an invoice.
	    * 				  Examples of periods are: one time, monthly, weekly, etc. Period codes can be seen in jbilling's User
	    * 				  Interface under “Orders -> Periods”.
	    */
       public function getPeriod() {

              return $this->period;
       }      
       /**
  	    * Gets the periodStr property for the OrderWS object
	    * 
	    * @access public
	    * @return String (read only). The description of the order period. 
	    */
       public function getPeriodStr() {

       	      return $this->periodStr;
       }
       /**
  	    * Gets the statusId property for the OrderWS object
	    * 
	    * @access public
	    * @return Integer An order has to be on status ‘Active’ in order to generate invoices. An order usually starts in active
	    * 				  status, and only goes to suspended or finished when the customer fails to make the required payments. The
	    * 				  steps and actions taken due to late payments are part of the ageing process. See Appendix A for a list of
	    * 				  acceptable order status codes. 
	    */
       public function getStatusId() {

       	      return $this->statusId;
       }
       /**
  	    * Gets the userId property for the OrderWS object
	    * 
	    * @access public
	    * @return Integer This order belongs to the user specified by this field. 
	    */
	   public function getUserId() {

	          return $this->userId;
	   }

       public function setIsCurrent( $bool ) {

              $this->isCurrent = (bool)$bool;
       }

       public function getIsCurrent() {

              return $this->isCurrent;
       }

       public function setVersionNum( $num ) {

              $this->versionNum = (integer)$num;
       }

       public function getVersionNum() {

              return $this->versionNum;
       }

       public function setCycleStarts( $iso8601Date ) {

              $this->cycleStarts = $iso8601Date;
       }

       public function getCycleStarts() {

              return $this->cycleStarts;
       }

       public function setPricingFields( $fields ) {

              $this->pricingFields = (string)$fields;
       }

       public function getPricingFields() {

              return $this->pricingFields;
       }

       public function setStatusStr( $str ) {

              $this->statusStr = (string)$str;
       }

       public function getStatusStr() {

              return $this->statusStr;
       }

       public function setTimeUnitStr( $str ) {

              $this->timeUnitStr = $str;
       }

       public function getTimeUnitStr() {

              return $this->timeUnitStr;
       }
}
?>