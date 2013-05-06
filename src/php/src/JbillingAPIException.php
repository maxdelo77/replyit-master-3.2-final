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
 * JbillingAPIException
 * @author Jeremy Hahn
 * @version 2.0
 * @copyright Make A Byte, inc
 * @package com.makeabyte.contrib.jbilling.php
 */

class JbillingAPIException extends Exception {

	  private $e;

	  /**
	   * Initalizes JbillingAPIException
	   * 
	   * @param $message The exception message string or an instance of SoapFault
	   * @param $code The exception code
	   * @return void
	   */
      public function __construct( $message, $code=0 ) {

      		 if( $message instanceof SoapFault ) {

      		 	 $this->e = $message;
      		 	 parent::__construct( (string)$message->faultstring, (integer)$message->faultcode );
      		 }
      		 else
	      	     parent::__construct( $message, $code );
      }

      /**
       * Displays the state of the exception instance 
       * 
       * @return void
       */
      public function debug() {

      		 print_r( $this->e );
      }
}
?>