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
  * OrderLineWS
  * @author Jeremy Hahn
  * @version 1.1
  * @copyright Make A Byte, inc
  * @package com.makeabyte.contrib.jbilling.php
  */

class OrderLineWS {

	  /**
	   * A unique number that identifies this record.
	   * 
	   * @var Integer
	   */
      var $id;

      var $orderId;
      
      /**
       * The total amount of this line. Usually, this field should respond to the formula price
	   * quantity. This amount will be the one added to calculate the purchase order total. The
	   * currency of this field is the one specified in its parent order.
	   * 
       * @var Float
       */
   	  var $amount;

   	  /**
   	   * The quantity of the items included in the line, or null, if a quantity doesn’t apply.
   	   * 
   	   * @var Integer
   	   */
   	  var $quantity;

   	  /**
   	   * The price of one item, or null if there is no related item.
   	   *  
   	   * @var Float
   	   */
      var $price;

      /**
       * If this line is using the price from the item, rather than its own.
       * 
       * @var Integer
       */
      var $itemPrice;

      /**
       * A time stamp applied when this record is created.
       * 
       * @var Date
       */
      var $createDatetime;
      
      /**
       * A flag that indicates if this record is logically deleted in the database. This 
       * allows for ‘undo’ of deletions. Valid values are
       * 0 – the record is not deleted
       * 1 – the record is considered deleted.
       *  
       * @var Integer
       */
      var $deleted;
      
      /**
       * A descriptive text for the services being included. This usually copies the
       * description of the item related to this line.
       * 
       * @var String
       */
      var $description;

      var $versionNum;

      /**
       * Indicates whether this order line is editable or not (i.e., it cannot be submitted for update).
       * 
       * @var Boolean
       */
      var $editable = null;

      //provisioning fields
      
      /**
       * The provisioning status id for this order line. See Appendix A for valid values.
       * 
       * @var Integer
       */      
      var $provisioningStatusId;
      
      /**
       * The provisioning request UUID for this order line, if it exists.
       * 
       * @var String
       */
      var $provisioningRequestId;

      // other fields, non-persistent
      
      /**
       * A text related to this line's pricing.
       * 
       * @var String
       */
      var $priceStr = null;
      
      /**
       * 
       * @var ItemDTOEx
       */
      var $itemDto = null;
      
      /**
       * 
       * @var unknown_type
       */
      var $typeId = null;
      
      /**
       * If true, when submitted, this line will take the price and description from the item.
       * This means that you would not need to give a price and description for the line.
       * Instead, you only provide the id of the item. See the createOrder section for details.
       * 
       * @var Boolean
       */
      var $useItem = null;

      /**
       * The id of the item associated with this line, or null if this line is not directly
       * related to an item. It is consider a good practice to have all order lines related
       * to an item. This allows for better reporting.
       * 
       * @var Integer
       */
      var $itemId = null;

      public function OrderLineWS() { }

      public function setTypeId( $id ) {

             $this->typeId = (int)$id;
      }

      public function getTypeId() {

             return $this->typeId;
      }

      public function setOrderId( $id ) {

             $this->orderId = (integer)$id;
      }

      public function getOrderId() {

             return $this->orderId;
      }

      public function setAmount( $amount ) {

             $this->amount = (float)$amount;
      }

      public function getAmount() {

             return $this->amount;
      }

      public function setQuantity( $qty ) {

             $this->quantity = $qty;
      }

      public function getQuantity() {

             return $this->quantity;
      }

      public function setPrice( $price ) {

             $this->price = (float)$price;
      }

      public function getPrice() {

             return $this->price;
      }

      public function setItemPrice( $price ) {

             $this->itemPrice = (float)$price;
      }

      public function getItemPrice() {

             return $this->itemPrice;
      }

      public function setCreateDateTime( $iso8601Date ) {

             $this->createDateTime = $iso8601Date;
      }

      public function getCreateDateTime() {

             return $this->createDatetime;
      }

      public function setDeleted( $val ) {

             $this->deleted = (int)$val;
      }

      public function getDeleted() {

             return $this->deleted;
      }

      public function setDescription( $description ) {

             $this->description = (string) $description;
      }

      public function getDescription() {

             return $this->description;
      }

      public function setVersionNum( $num ) {

             $this->versionNum = (integer)$num;
      }

      public function getVersionNum() {

             return $this->versionNum;
      }

      public function setEditable( $bool ) {

             $this->editable = (bool)$bool;
      }

      public function getEditable() {

             return $this->editable;
      }

      public function setProvisioningStatus( $status ) {

             $this->provisioningStatus = (integer)$status;
      }

      public function getProvisioningStatus() {

             return $this->provisioningStatus;
      }

      public function setProvisioningRequestId( $id ) {

             $this->provisioningRequestId = (string)$id;
      }

      public function setPriceStr( $str ) {

             $this->priceStr = (string)$str;
      }

      public function getPriceStr() {

             return $this->priceStr;
      }

      public function setItemDto( $dto ) {

             if( ! $dto instanceof ItemDTOEx )
                 throw new JbillingAPIException( "parameter passed to setItemDTO is not ItemDTOEx type" );

                 $this->itemDto = $dto;
      }

      public function getItemDto() {

             return $this->itemDto;
      }

      public function setUseItem( $bool ) {

             $this->useItem = (bool)$bool;
      }

      public function getUseItem() {

             return $this->useItem;
      }

      public function setItemId( $id ) {

             $this->itemId = (integer)$id;
      }

      public function getItemId() {

             return $this->itemId;
      }
}
?>