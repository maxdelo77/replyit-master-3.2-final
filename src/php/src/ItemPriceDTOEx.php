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
 * ItemPriceDTOEx
 * 
 * @author Jeremy Hahn
 * @version 1.0
 * @copyright Make A Byte, inc
 * @package com.makeabyte.contrib.jbilling.php
 */

class ItemPriceDTOEx {

	  /**
	   * 
	   * @var Integer
	   */
	  public $currencyId;
	  
	  /**
	   * 
	   * @var Integer
	   */
  	  public $id;
  	  
  	  /**
  	   * 
  	   * @var String
  	   */
  	  public $name;
  	  
  	  /**
  	   * 
  	   * @var Float
  	   */
  	  public $price;
  	  
  	  /**
  	   * 
  	   * @var String
  	   */
  	  public $priceForm;

  	  public function ItemPriceDTOEx() { }

  	  public function setCurrencyId( $id ) {

  	  		 $this->currencyId = $id;
  	  }

  	  public function getCurrencyId() {

  	  		 return $this->currencyId;
  	  }

  	  public function setName( $name ) {

  	  		 $this->name = $name;
  	  }

  	  public function getName() {

  	  		 return $this->name;
  	  }

  	  public function setPrice( $price ) {

  	  		 $this->price = $price;
  	  }

  	  public function getPrice() {

  	  		 return $this->price;
  	  }

  	  public function setPriceForm( $form ) {

  	  		 $this->priceForm = $form;
  	  }

  	  public function getPriceForm() {

  	  		 return $this->priceForm;
  	  }
}
?>