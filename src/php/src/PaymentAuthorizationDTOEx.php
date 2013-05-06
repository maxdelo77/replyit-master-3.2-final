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
  * PaymentAuthorizationDTOEx
  * @author Jeremy Hahn
  * @version 1.0
  * @copyright Make A Byte, inc
  * @package com.makeabyte.contrib.jbilling.php
  */

class PaymentAuthorizationDTOEx extends PaymentAuthorizationDTO {

      var $result;

      /**
       * The PaymentAuthorizationDTOEx constructor
       * 
       * @access public
       */
	  public function PaymentAuthorizationDTOEx() {
	  }	  
	  /**
	   * Sets the result property of the PaymentAuthorizationDTOEx object
	   * 
	   * @access public
	   * @param Boolean $bool “true” if the authorization succeeded, “false” otherwise.
	   * @return void
	   */
	  public function setResult( $bool ) {

	  	    $this->result = $bool;
	  }
	  /**
	   * Gets the result property of the PaymentAuthorizationDTOEx object
	   * 
	   * @access public
	   * @return Boolean “true” if the authorization succeeded, “false” otherwise.
	   */
	  public function getResult() {

	  	     return $this->result;
	  }  	  
}
?>