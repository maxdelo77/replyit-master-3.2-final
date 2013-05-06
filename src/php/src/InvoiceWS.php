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
  * InvoiceWS
  * @author Jeremy Hahn
  * @version 1.0
  * @copyright Make A Byte, inc
  * @package com.makeabyte.contrib.jbilling.php
  */

class InvoiceWS {

	  var $balance;             // The amount of this invoice that is yet to be paid.
	  var $carriedBalance;      // How much of the total belonging to previous unpaid invoices that have been delegated to this one.
	  var $createDateTime;      // This is the invoice date, which is assigned to it by the billing process when it is generated.
	  var $createTimeStamp;     // A time stamp of when this invoice record was created.
	  var $currencyId;          // Identifier of the currency in which the invoice's amounts are being expressed. See Appendix A for a list of all acceptable values.
	  var $customerNotes;       // Notes that are entered in a purchase order can be applied to an invoice. If that is the case, this field will have those user notes.
	  var $delegatedInvoiceId;  // If this invoice has been included in another invoice (usually for lack of payment), this field will indicate to which invoice it has been delegated.
      var $deleted;             // A flag that indicates if this record is logically deleted in the database. This allows for ‘undo’ of deletions. Valid values are: 0 – the record is not deleted 1 – the record is considered deleted.
      var $dueDate;             // The due date of this invoice. After this date, the invoice should have been paid.
      var $id;                  // A unique number that identifies this record.
      var $inProcessPayment;    // A flag indicating if this invoice will be paid using automated payment (through a payment processor), or if it will be paid externally (for example, with a paper check).
      var $invoiceLines;        // A list of objects representing each of this[] invoice’s lines.
      var $isReview;            // This is an internal value that indicates if this invoice is not a 'real' invoice, but one that belongs to a review process.
      var $lastReminder;        // Date and time of when the latest reminder was issued for this invoice.
      var $number;              // The invoice number, which is assigned to it from a 'preference' field (see the user guide for more information). This is not the ID, which is guaranteed to be unique.
      var $orders;              // A list of the ids of the purchase orders which have been included in this invoice.
      var $overdueStep;         // This marks which step is this invoice in for the penalties (interests) process.
      var $paymentAttempts;     // How many payment attempts have been done by the automated payment process to get this invoice paid.
      var $payments;            // A list of ids of the payments that have been applied to this invoice.
      var $toProcess;           // This is '1' if the invoice will be considered by the billing process as unpaid. Otherwise it is '0' and the invoices is either paid or carried over to another invoice.
      var $total;               // The total amount of this invoice.
      var $userId;              // The customer to whom this invoice belongs.
      var $statusId;			// Refund specific field. When a refund is to be issued, this field holds the identifier of the payment that is to be refunded.

      /**
       * The InvoiceWS constructor
       * 
       * @access public
       */
	  public function InvoiceWS() {
	  }

      /**
       * Sets the balance property of the InvoceWS object
       * 
       * @access public
       * @param Float $balance The amount of this invoice that is yet to be paid.
       * @return void
       */
      public function setBalance( $balance ) {

      	     $this->balance = $balance;
      }
      /**
       * Sets the carriedBalance property of the InvoceWS object
       * 
       * @access public
       * @param Float $balance How much of the total belonging to previous unpaid invoices that have been delegated to this one.
       * @return void
       */
      public function setCarriedBalance( $balance ) {

      	     $this->carriedBalance = $balance;
      }
      /**
       * Sets the createDateTime property of the InvoceWS object
       * 
       * @access public
       * @param Date $date This is the invoice date, which is assigned to it by the billing process when it is generated.
       * @return void
       */
      public function setCreateDateTime( $date ) {

      	     $this->createDateTime = $date;
      }
      /**
       * Sets the createTimeStamp property of the InvoceWS object
       * 
       * @access public
       * @param Date $balance The amount of this invoice that is yet to be paid.
       * @return void
       */
      public function setCreateTimeStamp( $date ) {

      	     $this->createTimeStamp = $date;
      }
      /**
       * Sets the currencyId property of the InvoceWS object
       * 
       * @access public
       * @param Integer $id Identifier of the currency in which the invoice's amounts are being expressed. See Appendix A for a list of all acceptable values.
       * @return void
       */
      public function setCurrencyId( $id ) {

      	     $this->currencyId = $id;
      }
      /**
       * Sets the customerNotes property of the InvoceWS object
       * 
       * @access public
       * @param String $notes Notes that are entered in a purchase order can be applied to an invoice. If that is the case, this field will have those user notes.
       * @return void
       */
      public function setCustomerNotes( $notes ) {

      	     $this->customerNotes = $notes;
      }
      /**
       * Sets the delegatedInvoiceId property of the InvoceWS object
       * 
       * @access public
       * @param Integer $id If this invoice has been included in another invoice (usually for lack of payment), this field will indicate to which invoice it has been delegated.
       * @return void
       */
      public function setDelegatedInvocieId( $id ) {

      	     $this->delegatedInvoiceId = $id;
      }
      /**
       * Sets the deleted property of the InvoceWS object
       * 
       * @access public
       * @param Integer $id A flag that indicates if this record is logically deleted in the database. This allows for ‘undo’ of deletions. Valid values are: 0 – the record is not deleted 1 – the record is considered deleted.
       * @return void
       */
      public function setDeleted( $id ) {

      	     $this->deleted = $id;
      }
      /**
       * Sets the dueDate property of the InvoceWS object
       * 
       * @access public
       * @param Date $date The due date of this invoice. After this date, the invoice should have been paid.
       * @return void
       */
      public function setDueDate( $date ) {

      	     $this->dueDate = $date;
      }
      /**
       * Sets the id property of the InvoceWS object
       * 
       * @access public
       * @param Integer $id A unique number that identifies this record.
       * @return void
       */
      public function setId( $id ) {

      	     $this->id = $id;
      }
      /**
       * Sets the inProcessPayment property of the InvoceWS object
       * 
       * @access public
       * @param Integer $id A flag indicating if this invoice will be paid using automated payment (through a payment processor), or if it will be paid externally (for example, with a paper check).
       * @return void
       */
      public function setInProcessPayment( $id ) {

      	     $this->inProcessPayment = $id;
      }
      /**
       * Sets the invoiceLines property of the InvoceWS object
       * 
       * @access public
       * @param InvoiceLineDTO[] $lines A list of objects representing each of this[] invoice’s lines.
       * @return void
       */
      public function setInvoiceLines( $lines ) {

      	     $this->invoiceLines = $lines;
      }
      /**
       * Sets the isReview property of the InvoceWS object
       * 
       * @access public
       * @param Integer $int This is an internal value that indicates if this invoice is not a 'real' invoice, but one that belongs to a review process.
       * @return void
       */
      public function setIsReview( $int ) {

      	     $this->isReview = $int;
      }
      /**
       * Sets the lastReminder property of the InvoceWS object
       * 
       * @access public
       * @param Date $date Date and time of when the latest reminder was issued for this invoice.
       * @return void
       */
      public function setLastReminder( $date ) {

      	     $this->lastReminder = $date;
      }
      /**
       * Sets the number property of the InvoceWS object
       * 
       * @access public
       * @param String $str The invoice number, which is assigned to it from a 'preference' field (see the user guide for more information). This is not the ID, which is guaranteed to be unique.
       * @return void
       */
      public function setNumber( $str ) {

      	     $this->number = $str;
      }
      /**
       * Sets the orders property of the InvoceWS object
       * 
       * @access public
       * @param Integer[] $balance A list of the ids of the purchase orders which have been included in this invoice.
       * @return void
       */
      public function setOrders( $orders ) {

      	     $this->orders = $orders;
      }
      /**
       * Sets the overdueStep property of the InvoceWS object
       * 
       * @access public
       * @param Integer $id This marks which step is this invoice in for the penalties (interests) process.
       * @return void
       */
      public function setOverdueStep( $id ) {

      	     $this->overdueStep = $id;
      }
      /**
       * Sets the paymentAttempts property of the InvoceWS object
       * 
       * @access public
       * @param Integer $balance How many payment attempts have been done by the automated payment process to get this invoice paid.
       * @return void
       */
      public function setPaymentAttempts( $id ) {

      	     $this->paymentAttempts = $id;
      }
      /**
       * Sets the payments property of the InvoceWS object
       * 
       * @access public
       * @param Integer[] $int A list of ids of the payments that have been applied to this invoice.
       * @return void
       */
      public function setPayments( $int ) {

      	     $this->payments = $int;
      }
      /**
       * Sets the toProcess property of the InvoceWS object
       * 
       * @access public
       * @param Integer $id This is '1' if the invoice will be considered by the billing process as unpaid. Otherwise it is '0' and the invoices is either paid or carried over to another invoice.
       * @return void
       */
      public function setToProcess( $id ) {

      	     $this->toProcess = $id;
      }
      /**
       * Sets the total property of the InvoceWS object
       * 
       * @access public
       * @param Float $total The total amount of this invoice.
       * @return void
       */
      public function setTotal( $total ) {

      	     $this->total = $total;
      } 
      /**
       * Sets the userId property of the InvoceWS object
       * 
       * @access public
       * @param Integer $balance The customer to whom this invoice belongs.
       * @return void
       */
      public function setUserId( $id ) {

      	     $this->userId = $id;
      }
	  /**
       * Sets the statusId property of the InvoceWS object
       * 
       * @access public
       * @param Integer $statusId.
       * @return void
       */
      public function setStatusId( $id ) {

      	     $this->statusId = $id;
      }
      /**
       * Gets the balance property of the InvoceWS object
       * 
       * @access public
       * @return The amount of this invoice that is yet to be paid.
       */
      public function getBalance() {

      	     return $this->balance;
      }
      /**
       * Gets the carriedBalance property of the InvoceWS object
       * 
       * @access public
       * @return How much of the total belonging to previous unpaid invoices that have been delegated to this one.
       */
      public function getCarriedBalance() {

      	     return $this->carriedBalance;
      }
      /**
       * Gets the createDateTime property of the InvoceWS object
       * 
       * @access public
       * @return This is the invoice date, which is assigned to it by the billing process when it is generated.
       */
      public function getCreateDateTime() {

      	     return $this->createDateTime;
      }
      /**
       * Gets the createTimeStamp property of the InvoceWS object
       * 
       * @access public
       * @return The amount of this invoice that is yet to be paid.
       */
      public function getCreateTimeStamp() {

      	     return $this->createTimeStamp;
      }
      /**
       * Gets the currencyId property of the InvoceWS object
       * 
       * @access public
       * @return Identifier of the currency in which the invoice's amounts are being expressed. See Appendix A for a list of all acceptable values.
       */
      public function getCurrencyId() {

      	     return $this->currencyId;
      }
      /**
       * Gets the customerNotes property of the InvoceWS object
       * 
       * @access public
       * @return Notes that are entered in a purchase order can be applied to an invoice. If that is the case, this field will have those user notes.
       */
      public function getCustomerNotes() {

      	     return $this->customerNotes;
      }
      /**
       * Gets the delegatedInvoiceId property of the InvoceWS object
       * 
       * @access public
       * @return If this invoice has been included in another invoice (usually for lack of payment), this field will indicate to which invoice it has been delegated.
       */
      public function getDelegatedInvocieId() {

      	     return $this->delegatedInvoiceId;
      }
      /**
       * Gets the deleted property of the InvoceWS object
       * 
       * @access public
       * @return A flag that indicates if this record is logically deleted in the database. This allows for ‘undo’ of deletions. Valid values are: 0 – the record is not deleted 1 – the record is considered deleted.
       */
      public function getDeleted() {

      	     return $this->deleted;
      }
      /**
       * Gets the dueDate property of the InvoceWS object
       * 
       * @access public
       * @return The due date of this invoice. After this date, the invoice should have been paid.
       */
      public function getDueDate() {

      	     return $this->dueDate;
      }
      /**
       * Gets the id property of the InvoceWS object
       * 
       * @access public
       * @return A unique number that identifies this record.
       */
      public function getId() {

      	     return $this->id;
      }
      /**
       * Gets the inProcessPayment property of the InvoceWS object
       * 
       * @access public
       * @return A flag indicating if this invoice will be paid using automated payment (through a payment processor), or if it will be paid externally (for example, with a paper check).
       */
      public function getInProcessPayment() {

      	     return $this->inProcessPayment;
      }
      /**
       * Gets the invoiceLines property of the InvoceWS object
       * 
       * @access public
       * @return A list of objects representing each of this[] invoice’s lines.
       */
      public function getInvoiceLines() {

      	     return $this->invoiceLines;
      }
      /**
       * Gets the isReview property of the InvoceWS object
       * 
       * @access public
       * @return This is an internal value that indicates if this invoice is not a 'real' invoice, but one that belongs to a review process.
       */
      public function getIsReview() {

      	     return $this->isReview;
      }
      /**
       * Gets the lastReminder property of the InvoceWS object
       * 
       * @access public
       * @return Date and time of when the latest reminder was issued for this invoice.
       */
      public function getLastReminder() {

      	     return $this->lastReminder;
      }
      /**
       * Gets the number property of the InvoceWS object
       * 
       * @access public
       * @return The invoice number, which is assigned to it from a 'preference' field (see the user guide for more information). This is not the ID, which is guaranteed to be unique.
       */
      public function getNumber() {

      	     return $this->number;
      }
      /**
       * Gets the orders property of the InvoceWS object
       * 
       * @access public
       * @return $balance A list of the ids of the purchase orders which have been included in this invoice.
       */
      public function getOrders() {

      	     return $this->orders;
      }
      /**
       * Gets the overdueStep property of the InvoceWS object
       * 
       * @access public
       * @return This marks which step is this invoice in for the penalties (interests) process.
       */
      public function getOverdueStep() {

      	     return $this->overdueStep;
      }
      /**
       * Gets the paymentAttempts property of the InvoceWS object
       * 
       * @access public
       * @return How many payment attempts have been done by the automated payment process to get this invoice paid.
       */
      public function getPaymentAttempts() {

      	     return $this->paymentAttempts;
      }
      /**
       * Gets the payments property of the InvoceWS object
       * 
       * @access public
       * @return A list of ids of the payments that have been applied to this invoice.
       */
      public function getPayments() {

      	     return $this->payments;
      }
      /**
       * Gets the toProcess property of the InvoceWS object
       * 
       * @access public
       * @return This is '1' if the invoice will be considered by the billing process as unpaid. Otherwise it is '0' and the invoices is either paid or carried over to another invoice.
       */
      public function getToProcess() {

      	     return $this->toProcess;
      }
      /**
       * Gets the total property of the InvoceWS object
       * 
       * @access public
       * @return The total amount of this invoice.
       */
      public function getTotal() {

      	     return $this->total;
      } 
      /**
       * Gets the userId property of the InvoceWS object
       * 
       * @access public
       * @return The customer to whom this invoice belongs.
       */
      public function getUserId() {

      	     return $this->userId;
      }
	  /**
       * Gets the statusId property of the InvoceWS object
       * 
       * @access public
       * @return The status Id of the InvoiceWS object.
       */
      public function getStatusId() {

      	     return $this->statusId;
      }
}
?>