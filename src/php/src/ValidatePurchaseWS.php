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
 * ValidatePurchaseWS
 * 
 * @author Jeremy Hahn
 * @version 1.0
 * @copyright Make A Byte, inc
 * @package com.makeabyte.contrib.jbilling.php
 */
class ValidatePurchaseWS {

	  public $authorized; // boolean
  	  public $message; // string
  	  public $quantity; // double
  	  public $success; // boolean

  	  public function ValidatePurchaseWS() {
  	  }

  	  public function setAuthorized( $bool ) {

  	  		 $this->authorized = $bool;
  	  }

  	  public function getAuthorized() {

  	  		 return $this->authorized;
  	  }

  	  public function setMessage( $message ) {

  	  		 $this->message = $message;
  	  }

  	  public function getMessage() {

  	  		 return $this->message;
  	  }

  	  public function setQuantity( $qty ) {

  	  		 $this->quantity = $qty;
  	  }

  	  public function getQuantity() {

  	  		 return $this->quantity;
  	  }

  	  public function setSuccess( $bool ) {

  	  		 $this->success = $bool;
  	  }

  	  public function getSuccess() {

  	  		 return $this->success;
  	  }
}
?>