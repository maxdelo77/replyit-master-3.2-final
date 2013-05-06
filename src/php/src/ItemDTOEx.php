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
  * ItemDTOEx
  * @author Jeremy Hahn
  * @version 1.0
  * @copyright Make A Byte, inc
  * @package com.makeabyte.contrib.jbilling.php
  */

class ItemDTOEx {

	  /**
	   * Identifier for the currency in which the item's price is expressed. See Appendix A for a list of acceptable values.
	   * 
	   * @var Integer
	   */
  	  var $currencyId;

  	  /**
  	   * A flag that indicates if this record is logically deleted in the database. This allows for ‘undo’ of deletions.
  	   * Valid values are: 0 – the record is not deleted 1 – the record is considered deleted.
  	   * 
  	   * @var Integer
  	   */
      var $deleted;
      
      /**
       * The description of this item entityId  Integer Identifier for the entity to which this item belongs.
       * 
       * @var String
       */
      var $description;
      
      /**
       * A unique number that identifies this record.
       * 
       * @var 
       */
      var $id;
      
      /**
       * This can be used to identify this item following an external coding system. For example,
       *  books can be identified by their ISBN codes.
       * 
       * @var String
       */
      var $number;
      
      /**
       * The order type that this item will generate, such a 'taxes', or 'items'.
       * 
       * @var Integer
       */
      var $orderLineTypeId;
      
      /**
       * If this is a percentage item, its rate is specified in this field.
       * 
       * @var Float
       */
      var $percentage;
      
      /**
       * The price of this item or null if this is a percentage item.
       * 
       * @var Float
       */
      var $price;

      /**
       * A flag that indicates if this item will allow manual edition of the price when a
       * purchase order is being placed with the web-based application. A value of 1 will
       * allow manual editing, while a value of 0 will display the price as a read-only field.
       * 
       * @var Integer
       */
      var $priceManual;
      
      /**
       * An item can have many prices, one per currency. This is an array of ItemPriceDTOEx
       * with all the available prices for this item.
       * 
       * @var array
       */
      var $prices;
      
      /**
       * If this item is related to a promotion, this is the code that identifies the promotion.
       * 
       * @var String
       */
      var $promoCode;
      
      /**
       * A list of type identifiers that indicates to which types[] (categories) this item belongs.
       * An item has to belong to at least one type. Item types are created from the web-based GUI
       * by the billing administrator.
       * 
       * @var Integer[]
       */
      var $types;
      
      // Added in jBilling 2.0
      
      var $hasDecimals;
      var $entityId;

      /**
       * The ItemDTOEx constructor
       * 
       * @access public
       */
	  public function ItemDTOEx() {
	  }
	  /**
	   * Sets the currencyId property of the ItemDTOEx object
	   * 
	   * @access public
	   * @param Integer $id Identifier for the currency in which the item's price is expressed. See Appendix A for a list of acceptable values.
	   * @return void
	   */
	  public function setCurrencyId( $id ) {
	  	
	  	     $this->currencyId = $id;
	  }
	  /**
	   * Sets the deleted property of the ItemDTOEx object
	   * 
	   * @access public
	   * @param Integer $id A flag that indicates if this record is logically deleted in the database. This allows for ‘undo’ of deletions. Valid values are: 0 – the record is not deleted 1 – the record is considered deleted.
	   * @return void
	   */
	  public function setDeleted( $id ) {

	  	     $this->deleted = $id;
	  }
	  /**
	   * Sets the description property of the ItemDTOEx object
	   * 
	   * @access public
	   * @param String $desc The description of this item
	   * @return void
	   */
	  public function setDescription( $desc ) {

	  	     $this->description = $desc;
	  }
	  /**
	   * Sets the entityId property of the ItemDTOEx object
	   * 
	   * @access public
	   * @param Integer $id Identifier for the entity to which this item belongs.
	   * @return void
	   */
	  public function setEntityId( $id ) {

	  	     $this->entityId = $id;
	  }
	  /**
	   * Sets the id property of the ItemDTOEx object
	   * 
	   * @access public
	   * @param Integer $id A unique number that identifies this record.
	   * @return void
	   */
	  public function setId( $id ) {

	  	     $this->id = $id;
	  }
	  /**
	   * Sets the number property of the ItemDTOEx object
	   * 
	   * @access public
	   * @param String $str This can be used to identify this item following an external coding system. For example, books can be identified by their ISBN codes.
	   * @return void
	   */
	  public function setNumber( $int ) {

	  	     $this->number = $int;
	  }
	  /**
	   * Sets the orderLineTypeId property of the ItemDTOEx object
	   * 
	   * @access public
	   * @param String $str
	   * @return void
	   */
	  public function setOrderLineTypeId( $id ) {

	  	     $this->orderLineTypeId = $id;
	  }
	  /**
	   * Sets the percentage property of the ItemDTOEx object
	   * 
	   * @access public
	   * @param Float $price If this is a percentage item, its rate is specified in this field.
	   * @return void
	   */
	  public function setPercentage( $percentage ) {

	  	     $this->percentage = $percentage;
	  }
	  /**
	   * Sets the price property of the ItemDTOEx object
	   * 
	   * @access public
	   * @param Float $price The price of this item or null if this is a percentage item.
	   * @return void
	   */
	  public function setPrice( $price ) {

	  	     $this->price = $price;
	  }
	  /**
	   * Sets the priceManual property of the ItemDTOEx object
	   * 
	   * @access public
	   * @param Integer $flag A flag that indicates if this item will allow manual edition of the price when a purchase order is being placed with the web-based application. A value of 1 will allow manual editing, while a value of 0 will display the price as a read-only field.
	   * @return void
	   */
	  public function setPriceManual( $flag ) {

	  	    $this->priceManual = $flag;
	  }
	  /**
	   * Sets the prices property of the ItemDTOEx object
	   * 
	   * @access public
	   * @param Array $flag An item can have many prices, one per currency. This is an array of ItemPriceDTOEx with all the available prices for this item.
	   * @return void
	   */
	  public function setPrices( $prices ) {

	  	     $this->prices = $prices;
	  }
	  /**
	   * Sets the promoCode property of the ItemDTOEx object
	   * 
	   * @access public
	   * @param Integer $flag
	   * @return void
	   */
	  public function setPromoCode( $code ) {

	  	     $this->promoCode = $code;
	  }
	  /**
	   * Sets the types property of the ItemDTOEx object
	   * 
	   * @access public
	   * @param Integer[] $types If this item is related to a promotion, this is the code that identifies the promotion.
	   * @return void
	   */
	  public function setTypes( $types ) {

	         $this->types = $types;
	  }
	  /**
	   * Gets the currencyId property of the ItemDTOEx object
	   * 
	   * @access public
	   * @return Integer Identifier for the currency in which the item's price is expressed. See Appendix A for a list of acceptable values.
	   */
	  public function getCurrencyId() {
	  	
	  	     return $this->currencyId;
	  }
	  /**
	   * Gets the deleted property of the ItemDTOEx object
	   * 
	   * @access public
	   * @return Integer A flag that indicates if this record is logically deleted in the database. This allows for ‘undo’ of deletions. Valid values are: 0 – the record is not deleted 1 – the record is considered deleted.
	   */
	  public function getDeleted() {

	  	     return $this->deleted ;
	  }
	  /**
	   * Gets the description property of the ItemDTOEx object
	   * 
	   * @access public
	   * @return String The description of this item
	   */
	  public function getDescription() {

	  	     return $this->description;
	  }
	  /**
	   * Gets the entityId property of the ItemDTOEx object
	   * 
	   * @access public
	   * @return Integer Identifier for the entity to which this item belongs.
	   */
	  public function getEntityId() {

	  	     return $this->entityId;
	  }
	  /**
	   * Gets the id property of the ItemDTOEx object
	   * 
	   * @access public
	   * @return Integer A unique number that identifies this record.
	   */
	  public function getId() {

	  	     return $this->id;
	  }
	  /**
	   * Gets the number property of the ItemDTOEx object
	   * 
	   * @access public
	   * @return String This can be used to identify this item following an external coding system. For example, books can be identified by their ISBN codes.
	   */
	  public function getNumber() {

	  	     return $this->number;
	  }
	  /**
	   * Gets the orderLineTypeId property of the ItemDTOEx object
	   * 
	   * @access public
	   * @return String The order type that this item will generate, such a 'taxes', or 'items'.
	   */
	  public function getOrderLineTypeId() {

	  	     return $this->orderLineTypeId;
	  }
	  /**
	   * Gets the percentage property of the ItemDTOEx object
	   * 
	   * @access public
	   * @return Float If this is a percentage item, its rate is specified in this field.
	   */
	  public function getPercentage() {

	  	     return $this->percentage;
	  }
	  /**
	   * Gets the price property of the ItemDTOEx object
	   * 
	   * @access public
	   * @return Float The price of this item or null if this is a percentage item.
	   */
	  public function getPrice() {

	  	     return $this->price;
	  }
	  /**
	   * Gets the priceManual property of the ItemDTOEx object
	   * 
	   * @access public
	   * @return Integer A flag that indicates if this item will allow manual edition of the price when a purchase order is being placed with the web-based application. A value of 1 will allow manual editing, while a value of 0 will display the price as a read-only field.
	   */
	  public function getPriceManual() {

	  	     return $this->priceManual;
	  }
	  /**
	   * Gets the prices property of the ItemDTOEx object
	   * 
	   * @access public
	   * @return Array An item can have many prices, one per currency. This is an array of ItemPriceDTOEx with all the available prices for this item.
	   */
	  public function getPrices() {

	  	     return $this->prices;
	  }
	  /**
	   * Gets the promoCode property of the ItemDTOEx object
	   * 
	   * @access public
	   * @return Integer If this item is related to a promotion, this is the code that identifies the promotion.
	   */
	  public function getPromoCode() {

	  	     return $this->promoCode;
	  }
	  /**
	   * Gets the types property of the ItemDTOEx object
	   * 
	   * @access public
	   * @return Integer[] If this item is related to a promotion, this is the code that identifies the promotion.
	   */
	  public function getTypes() {

	         return $this->types;
	  }
	  /**
	   * Sets the ItemDTOEx::hasDecimals field
	   * 
	   * @param Integer
	   * @return void
	   */
	  public function setHasDecimals( $int ) {

	  		 $this->hasDecimals = $int;
	  }
	  /**
	   * Returns the ItemDTOEx::hasDecimals field
	   *
	   * @return void
	   */
	  public function getHasDecimals() {

	  		 return $this->hasDecimals;
	  }
}
?>