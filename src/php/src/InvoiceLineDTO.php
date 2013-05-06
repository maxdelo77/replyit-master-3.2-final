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
  * InvoiceLineDTO
  * @author Jeremy Hahn
  * @version 1.0
  * @copyright Make A Byte, inc
  * @package com.makeabyte.contrib.jbilling.php
  */

class InvoiceLineDTO {

  	  var $amount;        // The total amount for this line. Usually would follow the formula price * quantity.
	  var $deleted;       // A flag that indicates if this record is logically deleted in the database. This allows for ‘undo’ of deletions. Valid values are: 0 – the record is not deleted 1 – the record is considered deleted.
      var $description;   // This description will be displayed in the invoice delivered to the customer.
      var $id;            // A unique number that identifies this record.
      var $isPercentage;  // Indicates whether the item referenced by this invoice line is a percentage item or not. This is used to aid how the order line is displayed to the customer.
	  var $itemId;        // Identifier of the item referenced by this invoice line.
	  var $price;         // The pricing of a single unit of this item.
	  var $quantity;      // The number of units of the item being invoiced.
	  var $sourceUserId;  // This field is useful only when many sub-accounts is invoiced together. This field would have the ID of the user that originally purchase an item.

      /**
       * The InvoiceLineDTO constructor
       * 
       * @access public
       */
	  public function InvoiceLineDTO() {
	  }
	  /**
	   * Sets the amount property in the InvoiceLineDTO object
	   * 
	   * @access public
	   * @param Float $amount The total amount for this line. Usually would follow the formula price * quantity.
	   * @return void
	   */
	  public function setAmount( $amount ) {
	  	
	  	      $this->amount = $amount;
	  }
	  /**
	   * Sets the deleted property in the InvoiceLineDTO object
	   * 
	   * @access public
	   * @param Integer $id A flag that indicates if this record is logically deleted in the database. This allows for ‘undo’ of deletions. Valid values are: 0 – the record is not deleted 1 – the record is considered deleted.
	   * @return void
	   */
	  public function setDeleted( $id ) {
	 	
	  	     $this->deleted = $id;
	  }
	  /**
	   * Sets the description property in the InvoiceLineDTO object
	   * 
	   * @access public
	   * @param String $desc This description will be displayed in the invoice delivered to the customer.
	   * @return void
	   */
	  public function setDescription( $desc ) {
	 
	         $this->description = $desc;
	  }
	  /**
	   * Sets the id property in the InvoiceLineDTO object
	   * 
	   * @access public
	   * @param Integer $id A unique number that identifies this record.
	   * @return void
	   */
	  public function setId( $id ) {
	  	
	  	     $this->id = $id;
	  }
	  /**
	   * Sets the isPercentage property in the InvoiceLineDTO object
	   * 
	   * @access public
	   * @param Integer $int Indicates whether the item referenced by this invoice line is a percentage item or not. This is used to aid how the order line is displayed to the customer.
	   * @return void
	   */
	  public function setIsPercentage( $int ) {
	  	
	  	     $this->isPercentage = $int;
	  }
	  /**
	   * Sets the itemId property in the InvoiceLineDTO object
	   * 
	   * @access public
	   * @param Integer $id Identifier of the item referenced by this invoice line.
	   * @return void
	   */
	  public function setItemId( $id ) {
	  	
	  	     $this->itemId = $id;
	  }
	  /**
	   * Sets the price property in the InvoiceLineDTO object
	   * 
	   * @access public
	   * @param Float $amount The pricing of a single unit of this item.
	   * @return void
	   */
	  public function setPrice( $amount ) {
	  	
	  	     $this->price = $amount;
	  }
	  /**
	   * Sets the quantity property in the InvoiceLineDTO object
	   * 
	   * @access public
	   * @param Integer $qty The number of units of the item being invoiced.
	   * @return void
	   */
	  public function setQuantity( $qty ) {
	  	
	  	     $this->quantity = $qty;    
	  }
	  /**
	   * Sets the sourceUserId property in the InvoiceLineDTO object
	   * 
	   * @access public
	   * @param Integer $int This field is useful only when many sub-accounts is invoiced together. This field would have the ID of the user that originally purchase an item.
	   * @return void
	   */
	  public function setSourceUserId( $int ) {
	  	
	  	     $this->sourceUserId = $int;
      }
	  /**
	   * Gets the amount property in the InvoiceLineDTO object
	   * 
	   * @access public
	   * @return The total amount for this line. Usually would follow the formula price * quantity.
	   */
	  public function getAmount() {
	  	
	  	      return $this->amount;
	  }
	  /**
	   * Gets the deleted property in the InvoiceLineDTO object
	   * 
	   * @access public
	   * @return A flag that indicates if this record is logically deleted in the database. This allows for ‘undo’ of deletions. Valid values are: 0 – the record is not deleted 1 – the record is considered deleted.
	   */
	  public function getDeleted() {
	 	
	  	     return $this->deleted;
	  }
	  /**
	   * Gets the description property in the InvoiceLineDTO object
	   * 
	   * @access public
	   * @return This description will be displayed in the invoice delivered to the customer.
	   */
	  public function getDescription() {
	 
	         return $this->description;
	  }
	  /**
	   * Gets the id property in the InvoiceLineDTO object
	   * 
	   * @access public
	   * @return A unique number that identifies this record.
	   */
	  public function getId() {
	  	
	  	     return $this->id;
	  }
	  /**
	   * Gets the isPercentage property in the InvoiceLineDTO object
	   * 
	   * @access public
	   * @return Indicates whether the item referenced by this invoice line is a percentage item or not. This is used to aid how the order line is displayed to the customer.
	   */
	  public function getIsPercentage() {
	  	
	  	     return $this->isPercentage;
	  }
	  /**
	   * Gets the itemId property in the InvoiceLineDTO object
	   * 
	   * @access public
	   * @return Identifier of the item referenced by this invoice line.
	   */
	  public function getItemId() {
	  	
	  	     return $this->itemId;
	  }
	  /**
	   * Gets the price property in the InvoiceLineDTO object
	   * 
	   * @access public
	   * @return The pricing of a single unit of this item.
       */
	  public function getPrice() {
	  	
	  	     return $this->price;
	  }
	  /**
	   * Gets the quantity property in the InvoiceLineDTO object
	   * 
	   * @access public
	   * @return The number of units of the item being invoiced.
	   */
	  public function getQuantity() {
	  	
	  	     return $this->quantity;
	  }
	  /**
	   * Gets the sourceUserId property in the InvoiceLineDTO object
	   * 
	   * @access public
	   * @return This field is useful only when many sub-accounts is invoiced together. This field would have the ID of the user that originally purchase an item.
       */
	  public function getSourceUserId() {
	  	
	  	     return $this->sourceUserId;
      }
}
?>