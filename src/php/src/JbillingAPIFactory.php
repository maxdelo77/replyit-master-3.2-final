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
 * JbillingAPIFactory
 * @author Jeremy Hahn
 * @version 2.0
 * @copyright Make A Byte, inc
 * @package com.makeabyte.contrib.jbilling.php
 */

define( 'JBILLINGAPI_TYPE_CXF', 'CXF' );
define( 'JBILLINGAPI_TYPE_HESSIAN', 'HESSIAN' );

require_once 'JbillingAPI.php';
require_once 'JbillingAPIException.php';
require_once 'AchDTO.php';
require_once 'ContactWS.php';
require_once 'CreateResponseWS.php';
require_once 'CreditCardDTO.php';
require_once 'InvoiceLineDTO.php';
require_once 'InvoiceWS.php';
require_once 'ItemDTOEx.php';
require_once 'OrderLineWS.php';
require_once 'OrderWS.php';
require_once 'PaymentAuthorizationDTO.php';
require_once 'PaymentAuthorizationDTOEx.php';
require_once 'PaymentInfoChequeDTO.php';
require_once 'PaymentWS.php';
require_once 'UserTransitionResponseWS.php';
require_once 'UserWS.php';
require_once 'ItemPriceDTOEx.php';
require_once 'ValidatePurchaseWS.php';
require_once 'ItemTypeWS.php';

abstract class JbillingAPIFactory {

      	 private static $api = null;

      	 private function __construct() { }
      	 private function __clone() { }

	     /**
		  * Factory method responsible for returning a jBilling API instance.
		  * 
		  * @access public
		  * @param String $endpoint The location of the endpoint
		  * @param String $username The jBilling endpoint username
		  * @param String $password The jBilling endpoint password
		  * @param String $type The jBilling API type (CXF|HESSIAN).
		  * @return jBilling API instance
		  */
		 public static function getAPI( $endpoint, $username, $password, $type = JBILLINGAPI_TYPE_CXF ) {

		  		try {
		  		 	    if( self::$api == null ) {

			            	switch( $type ) {

				             	    case JBILLINGAPI_TYPE_CXF:
				             	     	 require_once 'CXFAPI.php';
			             	 		 	 self::$api = new CXFAPI( $endpoint, array( 'trace' => 1, 'login' => $username, 'password' => $password ) );
			             	 		 	 break;

			             	 		case JBILLINGAPI_TYPE_HESSIAN:

			             	 			 throw new JbillingAPIException( 'Hessian API implementation is not YET supported. Use CXF instead.' );

			             	 		 	 require_once 'HessianAPI.php';
			             	 		 	 self::$api = new HessianAPI( $endpoint, $username, $password );
			             	 		 	 break;

			             	 		default:
			             	 		 	throw new JbillingAPIException( 'Invalid API type \'' . $type . '\'.' );
			             	}
			            }

			            return self::$api;
		  		}
		  		catch( SoapFault $e ) {

		  		 	   throw new JbillingAPIException( $e );
		  		}
		  		catch( Exception $e ) {

		  		 	   throw new JbillingAPIException( $e->getMessage(), $e->getCode() );
		  		}
		  }

		  abstract function getLastRequest();
		  abstract function getLastRequestHeaders();
		  abstract function getLastResponse();
		  abstract function getLastResponseHeaders();
}
?>