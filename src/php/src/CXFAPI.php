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
 * jBilling CXFAPI (Apache CXF) PHP wrapper
 * 
 * @author Jeremy Hahn
 * @copyright Make A Byte
 * @package com.makeabyte.contrib.jbilling.php
 * @version 1.0
 */
class CXFAPI extends SoapClient implements JbillingAPI {

	  private $options = array( 'uri' => 'http://util.server.jbilling.sapienter.com/',
	  							'soapaction' => ''
	  );

      private static $classmap = array(
                                    'invoiceWS' => 'invoiceWS',
                                    'invoiceLineDTO' => 'invoiceLineDTO',
                                    'orderWS' => 'orderWS',
                                    'orderLineWS' => 'orderLineWS',
                                    'itemDTOEx' => 'itemDTOEx',
                                    'itemPriceDTOEx' => 'itemPriceDTOEx',
                                    'paymentWS' => 'paymentWS',
                                    'achDTO' => 'achDTO',
                                    'paymentAuthorizationDTO' => 'paymentAuthorizationDTO',
                                    'paymentInfoChequeDTO' => 'paymentInfoChequeDTO',
                                    'creditCardDTO' => 'creditCardDTO',
                                    'paymentAuthorizationDTOEx' => 'paymentAuthorizationDTOEx',
                                    'contactWS' => 'contactWS',
                                    'userWS' => 'userWS',
                                    'validatePurchaseWS' => 'validatePurchaseWS',
                                    'userTransitionResponseWS' => 'userTransitionResponseWS',
                                    'createResponseWS' => 'createResponseWS',
                                    'SessionInternalError' => 'SessionInternalError',
                                    'getLatestInvoice' => 'getLatestInvoice',
                                    'getLatestInvoiceResponse' => 'getLatestInvoiceResponse',
                                    'deleteInvoice' => 'deleteInvoice',
                                    'deleteInvoiceResponse' => 'deleteInvoiceResponse',
                                    'getLatestOrder' => 'getLatestOrder',
                                    'getLatestOrderResponse' => 'getLatestOrderResponse',
                                    'processPayment' => 'processPayment',
                                    'processPaymentResponse' => 'processPaymentResponse',
                                    'payInvoice' => 'payInvoice',
                                    'payInvoiceResponse' => 'payInvoiceResponse',
                                    'getLastOrders' => 'getLastOrders',
                                    'getLastOrdersResponse' => 'getLastOrdersResponse',
                                    'updateItem' => 'updateItem',
                                    'updateItemResponse' => 'updateItemResponse',
                                    'updateUserContact' => 'updateUserContact',
                                    'updateUserContactResponse' => 'updateUserContactResponse',
                                    'getUserItemsByCategory' => 'getUserItemsByCategory',
                                    'getUserItemsByCategoryResponse' => 'getUserItemsByCategoryResponse',
                                    'getPayment' => 'getPayment',
                                    'getPaymentResponse' => 'getPaymentResponse',
                                    'getUserWS' => 'getUserWS',
                                    'getUserWSResponse' => 'getUserWSResponse',
                                    'validatePurchase' => 'validatePurchase',
                                    'validatePurchaseResponse' => 'validatePurchaseResponse',
                                    'getLastInvoicesByItemType' => 'getLastInvoicesByItemType',
                                    'getLastInvoicesByItemTypeResponse' => 'getLastInvoicesByItemTypeResponse',
                                    'createItem' => 'createItem',
                                    'createItemResponse' => 'createItemResponse',
                                    'createOrderPreAuthorize' => 'createOrderPreAuthorize',
                                    'createOrderPreAuthorizeResponse' => 'createOrderPreAuthorizeResponse',
                                    'getCurrentOrder' => 'getCurrentOrder',
                                    'getCurrentOrderResponse' => 'getCurrentOrderResponse',
                                    'authenticate' => 'authenticate',
                                    'authenticateResponse' => 'authenticateResponse',
                                    'getUsersNotInStatus' => 'getUsersNotInStatus',
                                    'getUsersNotInStatusResponse' => 'getUsersNotInStatusResponse',
                                    'getUsersInStatus' => 'getUsersInStatus',
                                    'getUsersInStatusResponse' => 'getUsersInStatusResponse',
                                    'getLastInvoices' => 'getLastInvoices',
                                    'getLastInvoicesResponse' => 'getLastInvoicesResponse',
                                    'getUserId' => 'getUserId',
                                    'getUserIdResponse' => 'getUserIdResponse',
                                    'getItem' => 'getItem',
                                    'getItemResponse' => 'getItemResponse',
                                    'updateUser' => 'updateUser',
                                    'updateUserResponse' => 'updateUserResponse',
                                    'createOrder' => 'createOrder',
                                    'createOrderResponse' => 'createOrderResponse',
                                    'getUserTransitions' => 'getUserTransitions',
                                    'getUserTransitionsResponse' => 'getUserTransitionsResponse',
                                    'applyPayment' => 'applyPayment',
                                    'applyPaymentResponse' => 'applyPaymentResponse',
                                    'createUser' => 'createUser',
                                    'createUserResponse' => 'createUserResponse',
                                    'getLastPayments' => 'getLastPayments',
                                    'getLastPaymentsResponse' => 'getLastPaymentsResponse',
                                    'getInvoicesByDate' => 'getInvoicesByDate',
                                    'getInvoicesByDateResponse' => 'getInvoicesByDateResponse',
                                    'getUsersByCustomField' => 'getUsersByCustomField',
                                    'getUsersByCustomFieldResponse' => 'getUsersByCustomFieldResponse',
                                    'deleteUser' => 'deleteUser',
                                    'deleteUserResponse' => 'deleteUserResponse',
                                    'isUserSubscribedTo' => 'isUserSubscribedTo',
                                    'isUserSubscribedToResponse' => 'isUserSubscribedToResponse',
                                    'getLatestPayment' => 'getLatestPayment',
                                    'getLatestPaymentResponse' => 'getLatestPaymentResponse',
                                    'updateOrderLine' => 'updateOrderLine',
                                    'updateOrderLineResponse' => 'updateOrderLineResponse',
                                    'deleteOrder' => 'deleteOrder',
                                    'deleteOrderResponse' => 'deleteOrderResponse',
                                    'getUserTransitionsAfterId' => 'getUserTransitionsAfterId',
                                    'getUserTransitionsAfterIdResponse' => 'getUserTransitionsAfterIdResponse',
                                    'createInvoice' => 'createInvoice',
                                    'createInvoiceResponse' => 'createInvoiceResponse',
                                    'create' => 'create',
                                    'createResponse' => 'createResponse',
                                    'getOrder' => 'getOrder',
                                    'getOrderResponse' => 'getOrderResponse',
                                    'createOrderAndInvoice' => 'createOrderAndInvoice',
                                    'createOrderAndInvoiceResponse' => 'createOrderAndInvoiceResponse',
                                    'updateOrder' => 'updateOrder',
                                    'updateOrderResponse' => 'updateOrderResponse',
                                    'getLatestOrderByItemType' => 'getLatestOrderByItemType',
                                    'getLatestOrderByItemTypeResponse' => 'getLatestOrderByItemTypeResponse',
                                    'updateCurrentOrder' => 'updateCurrentOrder',
                                    'updateCurrentOrderResponse' => 'updateCurrentOrderResponse',
                                    'getOrderByPeriod' => 'getOrderByPeriod',
                                    'getOrderByPeriodResponse' => 'getOrderByPeriodResponse',
                                    'getOrderLine' => 'getOrderLine',
                                    'getOrderLineResponse' => 'getOrderLineResponse',
                                    'getLastOrdersByItemType' => 'getLastOrdersByItemType',
                                    'getLastOrdersByItemTypeResponse' => 'getLastOrdersByItemTypeResponse',
                                    'getAllItems' => 'getAllItems',
                                    'getAllItemsResponse' => 'getAllItemsResponse',
                                    'updateCreditCard' => 'updateCreditCard',
                                    'updateCreditCardResponse' => 'updateCreditCardResponse',
                                    'getUsersByStatus' => 'getUsersByStatus',
                                    'getUsersByStatusResponse' => 'getUsersByStatusResponse',
                                    'getLatestInvoiceByItemType' => 'getLatestInvoiceByItemType',
                                    'getLatestInvoiceByItemTypeResponse' => 'getLatestInvoiceByItemTypeResponse',
                                    'rateOrder' => 'rateOrder',
                                    'rateOrderResponse' => 'rateOrderResponse',
                                    'getUsersByCreditCard' => 'getUsersByCreditCard',
                                    'getUsersByCreditCardResponse' => 'getUsersByCreditCardResponse',
                                    'getUserContactsWS' => 'getUserContactsWS',
                                    'getUserContactsWSResponse' => 'getUserContactsWSResponse',
                                    'getInvoiceWS' => 'getInvoiceWS',
                                    'getInvoiceWSResponse' => 'getInvoiceWSResponse',
									'ItemTypeWS' => 'ItemTypeWS',
                                    'createItemCategory' => 'createItemCategory',
                                    'createItemCategoryResponse' => 'createItemCategoryResponse',
                                    'getAuthPaymentType' => 'getAuthPaymentType',
                                    'setAuthPaymentType' => 'setAuthPaymentType',
                                    'getAuthPaymentTypeResponse' => 'getAuthPaymentTypeResponse',
                                    'setAuthPaymentTypeResponse' => 'setAuthPaymentTypeResponse',
                                    'updateAch' => 'updateAch',
                                    'updateAchResponse' => 'updateAchResponse',
                                    'createInvoiceFromOrder' => 'createInvoiceFromOrder',
                                    'createInvoiceFromOrderResponse' => 'createInvoiceFromOrderResponse',
                                    'getPaperInvoicePDF' => 'GetPaperInvoicePDF',
                                    'getPaperInvoicePDFResponse' => 'GetPaperInvoicePDFResponse'
                                    );

      /**
       * Initalizes the jBilling Apache CXF API
       * 
       * @param $endpoint The url of the CXF WSDL endpoint
       * @param $opts Options passed into the PHP SoapClient constructor
       * @return void
       */
 	  public function CXFAPI( $endpoint, $opts = array() ) {

	    	 foreach( self::$classmap as $key => $value ) {

	    	 	      if( !isset( $opts['classmap'][$key] ) )
	        			  $opts['classmap'][$key] = $value;
	    	 }

	    	 parent::__construct( $endpoint, $opts );
	  }

	  /**
	   * @see JbillingAPIFactory::getLastRequest()
	   */
	  public function getLastRequest() {

	  		 return $this->api->__getLastRequest();
	  }

	  /**
	   * @see JbillingAPIFactory::getLastRequestHeaders()
	   */
	  public function getLastRequestHeaders() {

	  		 return $this->api->__getLastRequestHeaders();
	  }

	  /**
	   * @see JbillingAPIFactory::getLastResponse()
	   */
	  public function getLastResponse() {

	  		 return $this->api->__getLastResponse();
	  }

	  /**
	   * @see JbillingAPIFactory::getLastResponseHeaders()
	   */
	  public function getLastResponseHeaders() {

	  		 return $this->api->__getLastResponseHeaders();
	  }

  	  /**
   	   * Returns the latest invoice that has been issued for a given user. This is particularly
	   * important because the latest invoice typically represents the account balance of a
	   * customer (this does not have to be the case, but it is a typical jBilling configuration).
   	   * 
   	   * @param Integer $userId Identifier of the customer whose latest invoice is to be retrieved.
   	   * @return getLatestInvoiceResponse
   	   */
  	  public function getLatestInvoice( $userId ) {

  	  		 $o = new getLatestInvoice();
  	  		 $o->arg0 = $userId;

  		 	 return $this->invoke( 'getLatestInvoice', $o );
  	  }

  	  /**
  	   * Calling this method will delete the invoice record from the database, along with all its
	   * invoice lines. It will also remove any links between the invoice and any related
	   * payments, increasing the payments balance accordingly.
  	   *
   	   * @param Integer $invoiceId The unique identifier of the invoice that is to be deleted.
   	   * @return deleteInvoiceResponse
  	   */
  	  public function deleteInvoice( $id ) {

  	  		 $o = new deleteInvoice();
  	  		 $o->arg0 = $id;

  	  		 return $this->invoke( 'deleteInvoice', $o );
  	  }

	  /**
   	   * Retrieves the latest order created for a given user account. This is true unless the order
	   * has been deleted. This method will filter out any deleted order.
   	   *
   	   * @param Integer $userId The user account's unique identifier.
   	   * @return getLatestOrderResponse
   	   */
  	  public function getLatestOrder( $userId ) {

  	  		 $o = new getLatestOrder();
  	  		 $o->arg0 = $userId;

  	  		 return $this->invoke( 'getLatestOrder', $o );
  	  }

  	  /**
   	   * Allows arbitrary payments to be processed without an invoice. The system will call the
	   * payment processor plug-in configured in your system to access a payment gateway and
	   * submit the payment to it.
	   * 
   	   * @param PaymentWS $payment Data of the payment being applied.
   	   * @return processPaymentResponse
   	   */
  	  public function processPayment( PaymentWS $payment ) {

  	  		 $o = new processPayment();
  	  		 $o->arg0 = $payment;

  	  		 return $this->invoke( 'processPayment', $o );
  	  }

  	  /**
	   * The ids of the last n purchase orders belonging to the user id given as a first parameter
	   * will be returned as an array. The first element of the array will be the latest purchase
	   * order. The next element will be the previous purchase order and so on. Subsequent calls
	   * to getOrder are necessary to retrieve the related OrderWS objects. The caller should
	   * check if the purchase order is not deleted by verifying that that deleted == 0. If the
	   * customer does not have any purchase orders, an empty array is returned.
	   *
	   * @param Integer $userId The user account for which the extraction is to be performed.
	   * @param Integer $number The maximum number of orders that should be extracted.
	   * @return getLastOrdersResponse
	   */
  	  public function getLastOrders( $userId, $number ) {

  	  		 $o = new getLastOrders();
  	  		 $o->arg0 = $userId;
  	  		 $o->arg1 = $number;

  	  		 return $this->invoke( 'getLastOrders', $o );
  	  }

	  /**
	   * This method executes a payment for a given invoice. The system will call the payment
	   * processor plug-in configured in your system to access a payment gateway and submit
	   * the payment to it.
	   *
	   * @param Integer $invoiceId Identifier of the invoice that is to be paid.
	   * @return payInvoiceResponse
	   */
  	  public function payInvoice( $invoiceId ) {

  	  		 $o = new payInvoice();
  	  		 $o->arg0 = $invoiceId;

  	  		 return $this->invoke( 'payInvoice', $o );
  	  }

	  /**
	   * Modifies the data associated to a jBilling item.
	   *
	   * @param ItemDTOEx $item Data structure for the item, containing the parameters being
	   *				   		modified (most probably, this structure is retrieved by a previous call to getItem(), and
	   * 						changing the desired values from the structure returned).
	   * 
	   * @return updateItemResponse
	   */
  	  public function updateItem( ItemDTOEx $item ) {

  	  		 $o = new updateItem();
  	  		 $o->arg0 = $item;

  	  		 return $this->invoke( 'updateItem', $o );
  	  }

	  /**
	   * Updates the user contact information.
	   *
	   * @param Integer $userID The identifier of the user whose contact information is being updated.
	   * @param Integer $typeId The contact's type. This is typically a '2' for the primary contact type in
	   *	    an installation with only one company. You would need to query the table contact_type
	   * 	 	to find out the IDs of all the type available in your system.
	   * @param ContactWS $contact Maintains the contact data that is being updated.
	   * @return updateUserContactResponse None. If the parameters provided are null or inexistent, a JbillingAPIException is generated.
	   */
	  public function updateUserContact( $userId, $typeId, ContactWS $contact ) {

	  		 $o = new updateUserContact();
	  		 $o->arg0 = $userId;
	  		 $o->arg1 = $typeId;
	  		 $o->arg2 = $contact;

	  		 return $this->invoke( 'updateUserContact', $o );
	  }

	  /**
	   * Returns the ids of items of the given type (category) which are found in recurring (i.e.,
	   * not one-time) orders for the given user.
	   *
	   * @param Integer $userId The identifier of the user whose orders are searched for items.
	   * @param Integer $categoryId The identifier of the type of item/s that the orders must contain.
	   * @return getUserItemsByCategoryResponse (Integer[]) An array of item ids of categoryId item type found in recurring orders for this userId.
	   */
  	  public function getUserItemsByCategory( $userId, $categoryId ) {

  	  		 $o = new getUserItemsByCategory();
  	  		 $o->arg0 = $userId;
  	  		 $o->arg1 = $categoryId;

  	  		 return $this->invoke( 'getUserItemsByCategory', $o );
  	  }

  	  /**
   	   * This method returns information about a specific payment.
   	   *
   	   * @param Integer $paymentId Identifier of the payment to be retrieved.
   	   * @param PaymentWS $payment Information for the specified payment. If the input parameter is either null
	   *		or does not correspond to a currently existing payment record, a JbillingAPIException is issued.
   	   * @return getPaymentResponse (PaymentWS) Information for the specified payment. If the input parameter is either null 
   	   * 		 or does not correspond to a currently existing payment record, a JbillingAPIException is issued.
   	   */
  	  public function getPayment( $paymentId) {

  	  		 $o = new getPayment();
			 $o->arg0 = $paymentId;

			 return $this->invoke( 'getPayment', $o );
  	  }

	  /**
   	   * This method returns the user data contained in jBilling.
	   *
	   * @param Integer $userId The identifier for the user whose account data is being retrieved.
	   * @return getUserWSResponse (UserWS) The account information, or null if the supplied userId is not assigned to an
	   * 		 existing user. If the parameter is null or inexistent, a JbillingAPIException is generated.
	   */
  	  public function getUserWS( UserWS $user ) {

  	  		 $o = new getUserWS();
  	  		 $o->arg0 = $user->getUserId();

  	  		 return $this->invoke( 'getUserWS', $o );  	  		 
  	  }

	  /**
	   * Returns the quantity available to the user of the given item when priced by the given
	   * pricing fields. The customer's dynamic balance is used for this calculating the maximum
	   * quantity that can be purchased before the customer's credit limit is reached or prepaid
	   * balance is reduced to zero.
	   *
	   * @param Integer $userId The identifier of the user whose available quantity is being calculated.
	   * @param Integer $itemId The identifier of the item to have the quantity returned.
	   * @param PricingField[] $fields Array of PricingField structures specifying optional pricing parameters
	   * 		to be passed to the rules engine for evaluation. Pricing fields are descriptors that provide
	   * 		further information to the pricing engine and aid in forming the price for the order itself.
	   * 		This parameter is optional and used in conjunction with the pricing rules engine.
	   * 		See section “A word on pricing” for a more detailed explanation of the use of pricing
	   * 		fields and the purpose of rule-based pricing.
	   * 		Note to SOAP based integration implementors: the pricingFields parameter of this
	   * 		call is implemented as an array of PricingField structures only in the API. Direct calls to
	   * 		the SOAP layer require you to encode and decode this array of values into a serialized
	   * 		string (so, for SOAP calls, the third parameter is actually a String). For more details on
	   * 		how to serialize these structures into strings, see section “A word on pricing”.
	   * @return validatePurchaseResponse (Double) The maximum quantity of the itemId that the user can purchase
	   * 		 according to their dynamic balance (available credit remaining or prepaid balance). Returns null
	   * 		 if userId or itemId is null.
	   */
  	  public function validatePurchase( $userId, $itemId, array $fields ) {

  	  		 $o = new validatePurchase();
  	  		 $o->arg0 = $userId;
  	  		 $o->arg1 = $itemdId;
  	  		 $o->arg2 = $fields;

  	  		 return $this->invoke( 'validatePurchase', $o );
  	  }

	  /**
	   * Returns the ids of the last invoices for the given user that contain item/s of the given item type.
	   * The number parameter limits the maximum amount of invoice ids returned.
	   *
	   * @param Integer $userId The identifier of the user whose invoice is to be returned.
	   * @param Integer $itemTypeId The identifier of the type of item/s that the invoice must contain.
	   * @param Integer $number The maximum number of invoice ids to be returned.
	   * @return getLastInvoicesByItemTypeResponse (Integer[]) An array of the invoice ids for the given user that contain item's
	   * 		 of the given type. Returns null if the userId, itemTypeId or number parameters are null.
	   */
  	  public function getLastInvoicesByItemType( $userId, $itemTypeId, $number ) {

  	  		 $o = new getLastInvoicesByItemType();
  	  		 $o->arg0 = $userId;
  	  		 $o->arg1 = $itemTypeId;
  	  		 $o->arg2 = $number;

  	  		 return $this->invoke( 'getLastInvoicesByItemType', $o );
  	  }

	  /**
	   * This method creates a new item. A new record with this item information will be inserted
	   * in the database. This method is frequently used to automate the uploading of a large
	   * number of items during the initial setup.
	   *
	   * @param ItemDTOEx $dto Information for the item that is being created.
	   * @return createItemResponse (Integer) The newly created item's identifier. If required fields in the input data structure
	   * 		 are missing, a JbillingAPIException is thrown to signal the error.
	   */
	  public function createItem( ItemDTOEx $dto ) {

	  		 $o = new createItem();
	  		 $o->arg0 = $dto;

	  		 return $this->invoke( 'createItem', $o );
  	  }

	 /**
	  * This method creates an order. Then it submits pre-authorization payment request to a
	  * payment gateway. The result of this request is returned as a PaymentAuthorizationDTOEx object.
	  * Regardless of the pre-authorization result, the order remains in the system after this call.
	  * This method is typically called with an order whose 'active since' is set to a future date:
	  * this would represent a 'free trial', where the paying period starts on the 'active since'
	  * date, but you need to verify the paying capabilities of the potential customer.
	  * The billing process will generate the invoice at the 'active since' date, and then 'capture'
	  * the pre-authorization for the first payment. This guarantees that the payment will be
	  * successful, since the payment gateway had already pre-authorized it.
   	  *
   	  * @param OrderWS $order Data for the order whose payment data is to be validated.
	  * @return createOrderPreAuthorizeResponse PaymentAuthorizationDTOEx: Data structure containing the outcome of the
	  * 		payment verification process. If you need to know the ID of the new order, you will have to call 'getLatestOrder'.
	  */
  	 public function createOrderPreAuthorize( OrderWS $order ) {

  	 		$o = new createOrderPreAuthorize();
  	 		$o->arg0 = $order;

  	 		return $this->invoke( 'createOrderPreAuthorize', $o );
	 }

	 /**
	  * Returns the mediation current one-time order for this user for the given date. See the
	  * “Telecom Guide” for more information about the mediation module and current one-time orders.
	  *
   	  * @param Integer $userId The identifier of the user whose current one-time order is being returned.
   	  * @param Date $date The date that determines which current one-time order should be returned. The
   	  * 	   date will fall within the order's active period.
	  * @return getCurrentOrderResponse (OrderWS) The current one-time order for the user for the given date. If
	  * 		the user has no mediation main-subscription order, null is returned.
	  */
  	 public function getCurrentOrder( $userId, $date ) {

  	 		$o = new getCurrentOrder();
  	 		$o->arg0 = $userId;
  	 		$o->arg1 = $date;

  	 		return $this->invoke( 'getCurrentOrder', $o );
  	 }

	 /**
   	  * This method provides user authentication. It is a shorthand for calling getUserId() 
   	  * and getUserWS() in sequence, while also checking the password in the process.
	  *
	  * @param String $username The user's login name.
	  * @param String $password The user's password.
	  * @return authenticateResponse (Integer)
	  *         0: The user was successfully authenticated and his current status allows him
	  *            entrance to the system.
	  *         1: Invalid credentials. The user name or password are not correct.
	  *         2: Locked account: The user name or password are incorrect, and this is the last
	  *            allowed login attempt. From now on, the account is locked.
	  *         3: Password expired: The credentials are valid, but the password is expired. The
	  *            user needs to change it before logging in.
	  *         If the input data is null or missing, this method generates a JbillingAPIException that signals the problem.
	  */
  	 public function authenticate( $username, $password ) {

  	 		$o = new authenticate();
  	 		$o->arg0 = $username;
  	 		$o->arg1 = $password;

  	 		return $this->invoke( 'authenticate', $o );
  	 }

	 /**
   	  * The opposite of getUsersInStatus(), this method provides a list of all users that are not currently in the given status.
	  *
	  * @param Integer $statusId Indicates what status will be used for extraction. See Appendix A for a list of acceptable status codes.
	  * @return getUsersNotInStatusResponse Integer[]: An array containing the identifiers of all users that were in any status different
	  * 		to the one given as input in the moment the method was called (status transitions could occur after the method was called,
	  * 		so this information should not be used to grant access to the site or to substitute the authorize() or getUserWS() methods
	  * 		for authentication).
   	  */
	 public function getUsersNotInStatus( $statusId ) {

	 		$o = new getUsersNotInStatus();
	 		$o->arg0 = $statusId;

	 		return $this->invoke( 'getUsersNotInStatus', $o );
  	 }

	 /**
	  * Returns a list of all users in a given status. Useful for making synchronization calls between your application and
	  * jBilling, or to otherwise process this information (for example, for a report).
	  *
	  * @param Integer $statusId Indicates what status will be used for extraction. See Appendix A for a list of acceptable status codes.
	  * @return getUsersInStatusResponse Integer[]: An array containing the identifiers of all users that were in the status given 
	  * 		as input in the moment the method was called (status transitions could occur after the method was called, so this
	  * 		information should not be used to grant access to the site or to substitute the authorize() or getUserWS() methods for authentication).
   	  */
	 public function getUsersInStatus( $statusId ) {

	 		$o = new getUsersInStatus();
	 		$o->arg0 = $statusId;

	 		return $this->invoke( 'getUsersInStatus', $o );
  	 }

  	 /**
	  * Use this method to retrieve several invoices belonging to a customer, starting from the last one. The method will return the
	  * ids of the invoices, so you will have to call getInvoice to get the complete object related to each ID. The results will be
	  * returned in inverse chronological order. This is a handy method to provide your customers with historical data regarding their
	  * invoices. For example, getLastInvoices(1234, 12) will be returning the last 12 invoices of the customer id 1234, and if you invoice
	  * your customers in a monthly basis, that means one year of invoices.
	  *
	  * @param Integer $userId Identifier of the user whose latest invoices are to be retrieved.
	  * @param Integer $number The number of invoices that are to be retrieved for this user.
	  * @return getLastInvoicesResponse (Integer[]) Array of invoice identifiers of the latest n invoices for this customer. It is possible
	  * 		that the customer has not generated as many invoices as requested (for example, you ask for the latest 10 invoices, but the
	  * 		system has generated only 8 invoices so far, you'll be returned those 8 invoices, so it is good practice to double check the
	  * 		size of the returned array). The caller should check if the invoice is not deleted by verifying that that deleted == 0. If the
	  * 		customer does not have any invoices, an empty array is returned. If the userId provided as parameter is either null or does not
	  * 		represent a currently existing user, a JbillingAPIException will be thrown, to indicate the error condition.
	  */
  	 public function getLastInvoices( $userId, $number ) {

  	 		$o = new getLastInvoices();
  	 		$o->arg0 = $userId;
  	 		$o->arg1 = $number;

  	 		return $this->invoke( 'getLastInvoices', $o );
  	 }

	 /**
	  * Returns the unique identifier associated to a user in jBilling. This method is meaningful
	  * specially during login, where you use it to retrieve the user's ID from the supplied user name.
	  * 
	  * @param String $username The user's login name.
	  * @return getUserIdResponse Integer: The user's identification number. If the parameter provided is null
	  * 		or there isn't a user with the user name give as a parameter, a JbillingAPIException is thrown.
	  */
  	 public function getUserId( $username ) {

  	 		$o = new getUserId();
  	 		$o->arg0 = $username;

  	 		return $this->invoke( 'getUserId', $o );
  	 }

	 /**
	  * This method returns the item description for an item. If business rules exist that affect
	  * the item being retrieved, they are applied and their effects are reflected in the returned
	  * data structure.
	  *
	  * @param Integer $itemId Identifier of the item that is being retrieved. This is the only required
	  * 			   parameter, if your intention is to simply retrieve the item data.
	  * @param Integer $userId Identifier of the user for which this item is being retrieved (useful to
	  * 			   determine if any specific pricing rules apply to this customer). This parameter is optional
	  * 			   and used in conjunction with the pricing rules engine.
	  * @param PricingField[] $pricingFields Array of PricingField structures specifying
	  * 					  optional pricing parameters to be passed to the rules engine for evaluation. Pricing fields
	  * 					  are descriptors that provide further information to the pricing engine and aid in forming
	  * 					  the price for the order itself. This parameter is optional and used in conjunction with the
	  * 					  pricing rules engine.
	  * See section “A word on pricing” for a more detailed explanation of the use of pricing
	  * fields and the purpose of rule-based pricing.
	  * Note to SOAP based integration implementors: the pricingFields parameter of this
	  * call is implemented as an array of PricingField structures only in the API. Direct calls to
	  * the SOAP layer require you to encode and decode this array of values into a serialized
	  * string (so, for SOAP calls, the third parameter is actually a String). For more details on
	  * how to serialize these structures into strings, see section “A word on pricing”.
	  * 
	  * @return getItemResponse The item description structure for the item being retrieved.
	  */
  	 public function getItem( $itemId, $userId = null, $pricingFields = array() ) {

  	 		$o = new getItem();
  	 		$o->arg0 = $itemId;
  	 		$o->arg1 = $userId;
  	 		$o->arg2 = $pricingFields;

  	 		return $this->invoke( 'getItem', $o );
  	 }

	 /**
	  * Creates an order. When creating an order, you can indicate that the information of an order line should be fetched from
	  * the properties of an existing item. This is done through the field OrderLineWS.useItem. If this flag is set to ‘true’, then
	  * the price of the order line will be the price of the item designated in OrderLineWS.itemId. The behavior of some fields change
	  * depending on this flag:
	  *
	  * @param OrderWS $order The order data.
	  * @return createOrderResponse (Integer) Identifier for the newly created invoice, if any, or null if the order data supplied was
	  * 		incorrect, insufficient or with attributes that do not generate an invoice (for example, an active since in the future).
	  * 		If you need to retrieve the ID of the order generated by a call to this method, call 'getLatestOrder'.
	  */
  	 public function createOrder( OrderWS $order ) {

  	 		$o = new createOrder();
  	 		$o->arg0 = $order;

  	 		return $this->invoke( 'createOrder', $o );
  	 }

  	 /**
	  * This method updates the user account information in jBilling. This includes the contact
	  * and credit card information.
	  *
	  * @param UserWS $user The user's data. Can be obtained by a previous call to getUserWS() or
	  * 					generated directly by your application (be careful, if you don't put
	  * 					values into fields that haven't changed, you'll loose those values, so it is best to first retrieve the data
	  *						record with getUserWS() and just change the required fields and resubmit the
	  *						UserWS structure). The supplied UserWS structure must contain a valid user
	  *						identification number in its userId field.
	  *						Three fields of the UserWS input parameter can be null. If the following fields are null,
	  *						they will simply be ignored: password, creditCard and contact. If you do not want
	  *						those fields to be updated, simply set their value to null.
	  *						This is useful on occasions where the jBilling configuration encrypts or does not make
	  *						available certain values. The password and credit card are among them. If that is the
	  *						case, a call to getUserWS() will return values for those fields that won't pass the
	  *						validations of a subsequent call to updateUser.
	  *	
	  * @return None. If the user identifier supplied is incorrect or the parameter is null or invalid, this
	  * 		method throws a JbillingAPIException to signal the problem.
	  */
  	 public function updateUser( UserWS $user ) {

  	 		$o = new updateUser();
  	 		$o->arg0 = $user;

  	 		return $this->invoke( 'updateUser', $o );
  	 }

  	 /**
   	  * Returns a list of subscription transitions that have taken place in a given period of time. See the section Subscription status
   	  * for an explanation on how this function can be used.
   	  *
   	  * @param Date $from Starting date of the extraction period. Can be null (in which case, the extraction period starts from the last
   	  * 	   time this function was called, or from the first transition if the function has not yet been called).
   	  * @param Date $to Ending date of the extraction period. Can be null (in which case the extraction has no upper limit, i.e. It extracts
   	  * 	   all records that have happened to this moment).
   	  * @return getUserTransitionsResponse (UserTransitionResponseWS[]) Array of transition records containing the transition information
   	  * 		for all registered changes in subscription status.
   	  */
  	 public function getUserTransitions( $to, $from ) {

  	 		$o = new getUserTransitions();
  	 		$o->arg0 = $to;
  	 		$o->arg1 = $from;

  	 		return $this->invoke( 'getUserTransitions', $o );
  	 }

	 /**
   	  * This method enters a payment to the user account. It does not invoke any payment
	  * processes, it just signals the payment as “entered”. It is useful to signal payments done
	  * via external payment processes (a cheque being cashed, for example).
	  * This method can apply a payment of any type. The parameter for the related invoice,
	  * although optional, should always be specified to allow the system to properly trace late
	  * payments.
	  *
	  * @param PaymentWS $payment Data of the payment being applied.
	  * @param $invoiceId Optionally identifies an invoice that is being paid by this
	  * 			      payment. This parameter can be null (indicating this payment does not cover a specific
	  * 				  invoice, or covers more than one).
	  * 
	  * @return applyPaymentResponse
	  */
  	 public function applyPayment( PaymentWS $payment, $invoiceId ) {

  	 		$o = new applyPayment();
  	 		$o->arg0 = $payment;
  	 		$o->arg1 = $invoiceId;

  	 		return $this->invoke( 'applyPayment', $o );
  	 }

	 /**
	  * This method creates a new user.
	  *
	  * @param UserWS $newUser The user data that will be used to create the new user record. The username field of this
	  * 	   structure must have a valid and unused user name string. The userId field can be set to -1 since the unique
	  * 	   identifier has not been generated yet (jBilling generates it during this call). If the contact or credit card
	  * 	   information are present, they will be created for the new user as well.
	  * @return createUserResponse (Integer) If the user has been successfully created, the return value is the newly created
	  * 		user's ID. If the user name has already been used by another user record, it returns null. If the input data
	  * 		is null or missing, this method generates a JbillingAPIException that signals the problem.
	  */
  	 public function createUser( UserWS $user ) {

  			$o = new createUser();
  			$o->arg0 = $user;

 			return $this->invoke( 'createUser', $o );
  	 }

	 /**
	  * Use this method to retrieve all the invoices created in a given period of time. The method will return the ids of the
	  * invoices, so you will have to call getInvoice to get the complete object related to each ID. The results will be returned
	  * in no particular order. This method can help you synchronize jBilling with other applications that require an updated list
	  * of invoices. For example, to get all the invoices for January 2005, you would call, getInvoicesByDate(“2005-01-01”, “2005-01-31”).
	  *
	  * @param Date $since The starting date for the data extraction.
	  * @param Date $until The ending date for the data extraction.
	  * @return getInvoicesByDateResponse (Integer[]): This method returns the invoices generated within the period specified by the parameters.
	  * 		Both dates are included in the query. The date used for the query is the actual creation of the invoices (time stamp), regardless
	  * 		of the ‘invoice date’, that is assigned following the billing rules and configuration parameters. Subsequent calls to getInvoice
	  * 		are necessary to retrieve the related InvoiceWS objects. If the no invoices where generated for the specified period, an empty
	  * 		array is returned. If the parameters do not follow the required format (yyyy-mm-dd), null is returned.
	  */
	 public function getInvoicesByDate( $since, $until ) {

	 		$o = new getInvoicesByDate();
	 		$o->arg0 = $since;
	 		$o->arg1 = $until;

	 		return $this->invoke( 'getInvoicesByDate', $o );
  	 }

  	 /**
   	  * Use this method to retrieve several payments belonging to a customer, starting from the last one. The method will return the ids of the
   	  * payments, so you will have to call getPayment to get the complete object related to each ID. The results will be returned in inverse
   	  * chronological order. This is a handy method to provide your customers with historical data regarding their payments. For example,
   	  * getLastPayments(1234, 12) will be returning the last 12 payments of the customer id 1234.
   	  *
   	  * @param Integer $userId Identifier of the customer whose payment information is to be retrieved.
   	  * @param Integer $number The number of payments to retrieve.
   	  * @return getLastPaymentsResponse (Integer[]) Array of payment identifiers for the latest n payments processed for the user. If the
   	  * 		input parameters are missing or incorrect, a JbillingAPIException is issued.
   	  */
  	 public function getLastPayments( $userId, $number ) {

  	 		$o = new getLastPayments();
  	 		$o->arg0 = $userId;
  	 		$o->arg1 = $number;

  	 	    return $this->invoke( 'getLastPayments', $o );
  	 }

	 /**
   	  * Returns all users that share a common custom contact field with a specific value. See
   	  * the section Custom Contact Fields for an explanation on how to use these fields.
	  *
	  * @param Integer $typeId Identifier of the custom contact field ID.
	  * @param String $value The value that will be tested for all users in order to determine which users should be extracted.
	  * @return getUsersByCustomFieldResponse (Integer[]) An array of all users that have the indicated custom field set to the specified value.
	  */
  	 public function getUsersByCustomField( $typeId, $value ) {

  	 		$o = new getUsersByCustomField();
  	 		$o->arg0 = $typeId;
  	 		$o->arg1 = $value;

  	 		return $this->invoke( 'getUsersByCustomField', $o );
  	 }

  	 /**
	  * Deletes an existing user. It will only mark the user as deleted, the record will remain in the database. It will do the same for all
	  * the orders, but will leave the invoices and payment untouched.
	  *
	  * @param Integer $userID The jBilling identifier for the user that is being deleted. This id is retrieved either from your application's
	  * 	   data or by a previous call to getUserId().
	  * @return deleteUserResponse None. If the userId provided is null or inexistent, a JbillingAPIException is generated.
	  */
  	 public function deleteUser( $userId ) {

  	 		$o = new deleteUser();
  	 		$o->arg0 = $userId;

  	 		return $this->invoke( 'deleteUser', $o );
  	 }

  	 /**
	  * This method returns the latest payment entered or processed for a specific customer.
	  *
	  * @param Integer $userId Identifier of the customer whose payment information is to be retrieved.
	  * @return getLatestPaymentResponse (PaymentWS) Information for this customer's latest payment. Can be null (no payments present for this customer). 
	  */
  	 public function getLatestPayment( $userId ) {

  	 		$o = new getLatestPayment();
  	 		$o->arg0 = $userId;

  	 		return $this->invoke( 'getLatestPayment', $o );
  	 }

  	 /**
   	  * Returns the quantity of the given item the user is subscribed to, that is, has recurring orders for.
   	  *
      * @param Integer $userId The identifier of the user whose subscription is being checked.
      * @param Integer $itemId The identifier of the item the user is being checked for subscription to.
   	  * @return isUserSubscribedToResponse (Double) The quantity of the item the user is subscribed to.
   	  */
  	 public function isUserSubscribedTo( $userId, $itemId ) {

  	 		$o = new isUserSubscribedTo();
  	 		$o->arg0 = $userId;
  	 		$o->arg1 = $itemId;

  	 		return $this->invoke( 'isUserSubscribedTo', $o );
  	 }

  	 /**
	  * Updates the order line with the supplied information. This can be used to also delete an
	  * order line. If the 'quantity' field of the order line is set to '0', the order line is not updated,
	  * it is instead removed.
	  *
	  * @param OrderLineWS $line The updated order line data to be stored in jBilling.
	  * @return updateOrderLineResponse None. If the provided order line data is invalid or does not correspond to an existing
	  * order line, the method generates a JbillingAPIException.
	  */
  	  public function updateOrderLine( OrderLineWS $line ) {
    
  	  		 $o = new updateOrderLine();
  	  		 $o->arg0 = $line;

  	  		 return $this->invoke( 'updateOrderLine', $o );
  	  }

	  /**
	   * Calling this method will mark a purchase order record as deleted, along with all its order lines.
	   *
	   * @param Integer $orderId The unique identifier of the order that is to be deleted.
	   * @return deleteOrderResponse None. If the order identifier provided as input is null or does not correspond
	   * 		 to an existing order, the method throws a JbillingAPIException.
	   */
	  public function deleteOrder( $orderId ) {

	  		 $o = new deleteOrder();
	  		 $o->arg0 = $orderId;

	  		 return $this->invoke( 'deleteOrder', $o );
  	  }

	  /**
	   * Generates invoices for orders not yet invoiced for the given user. Optionally only allows
	   * recurring orders to generate invoices. Returns the ids of the newly created invoices.
	   * Calling this method is the equivalent as running the billing process for one single
	   * customer. The system will go over all the applicable orders (and overdue invoices) to
	   * generate one invoice. This is different than calling 'createOrderAndInvoice'. In that
	   * method, the invoice comes from one single order, while in this one (crateInvoice), it
	   * comes from potentially many orders.
	   * 
	   * @param $userId The identifier of the user that is having the invoices created.
	   * @param $onlyRecurring If true, only recurring orders can generate invoices. If
	   *					   false, both one-time and recurring orders can generate invoices. Please note that if the
	   *					   preference 'ALLOW_INVOICES_WITHOUT_ORDERS' (46) is set to true, this will take
	   * 					   precedence to any value coming from this parameter. If you have prefence 46 set to true
	   *					   and you call createInvoice with onlyRecurring equal to false, the system will use a 'true' anyway.
	   *
	   * @return createInvoiceResponse
	   */
	  public function createInvoice( $userId, $onlyRecurring = false ) {

	  		 $o = new createInvoice();
	  		 $o->arg0 = $userId;
	  		 $o->arg1 = $onlyRecurring; 

	  		 return $this->invoke( 'createInvoice', $o );
	  }

	  /**
	   * @todo Get documentation
	   *
	   * @param Integer $id
	   * @return getUserTransitionsAfterIdResponse
	   */
  	  public function getUserTransitionsAfterId( $id ) {

  	  		 $o = new getUserTransitionsAfterId();
  	  		 $o->arg0 = $id;

  	  		 return $this->invoke( 'getUserTransitionsAfterId', $o );
  	  }

  	  /**
   	   * This method facilitates the sign-up of a new customer. It will create the new user, and all
	   * the related objects: purchase order, invoice and payment.
	   * The payment will only be created and processed if a credit card is included in the
	   * UserWS parameter. Therefore, the caller will always get values for the fields userId,
	   * orderId and invoiceId, but paymentId and paymentResult would remain null if not
	   * credit card information is found in the UserWS parameter.
	   * The payment will be submitted for immediate, real-time processing to the payment
	   * processor. The value in paymentResult will reflect the response of the payment
	   * processor. An email notification will also be sent to the customer with the result of this
	   * transaction.
	   * To summarize, these are the tasks involved in this method:
  	   * 1. Creation of a new user
  	   * 2. Creation of a new purchase order for this new user.
  	   * 3. Generation of an invoice based on the purchase order.
  	   * 4. Real-time processing of a payment for the invoice through a payment processor.
  	   * 5. Notification via email of the result of this payment to the customer (if applicable, see
	   * the Notifications documentation for more information).
	   * Steps 4 and 5 only take place if a credit card is present in the UserWS parameter.
	   * See the description of the createUser and createOrder methods for more information.
	   * 
	   * @param UserWS $user The account information for the user that is being created.
	   * @param OrderWS $order The purchase order information to assign to the new user. The
	   *					   userId of this object can be null and will be ignored, since this value will be assigned
	   *					   automatically with the userId of the newly created customer.
	   *
	   * @return createResponse
	   */
	  public function create( UserWS $user, OrderWS $order ) {

	  		 $o = new create();
	  		 $o->arg0 = $user;
	  		 $o->arg1 = $order;

	  		 return $this->invoke( 'create', $o );
  	  }

	  /**
	   * Creates a new order and contextually generates the corresponding invoice. Typically, you only create the order and then
	   * let the billing process take care of the invoice generation. However, at times you might want to subscribe an existing
	   * user (if the user does not exist, then it's better to call 'create') and immediately process the payment. In that case, you
	   * will call this method and then call 'payInvoice' using the return value of this method. If you need to generate an invoice
	   * based in many orders, you will need to call 'createOrder' many times, followed by 'createInvoice'.
	   *
	   * @param OrderWS $order The order data.
	   * @return createOrderAndInvoiceResponse Integer: Identifier for the newly created invoice, if any, or null if the order data
	   * 		 supplied was incorrect, insufficient or with attributes that do not generate an invoice (for example, an active since
	   * 		 in the future). If you need to retrieve the ID of the order generated by a call to this method, call 'getLatestOrder'.
	   */
	  public function createOrderAndInvoice( OrderWS $order ) {

	  		 $o = new createOrderAndInvoice();
	  		 $o->arg0 = $order;

	  		 return $this->invoke( 'createOrderAndInvoice', $o );
  	  }

	  /**
	   * Returns the order data for a specific order.
	   *
	   * @param Integer $orderId Unique identifier for the order.
	   * @return getOrderResponse (OrderWS) The order information, or an exception if the supplied order ID is invalid or if
	   *		 the order ID belongs to a deleted order. The object will have all the related order lines.
	   */
	  public function getOrder( $orderId ) {

	  		 $o = new getOrder();
	  		 $o->arg0 = $orderId;

	  		 return $this->invoke( 'getOrder', $o );
  	  }

	  /**
	   * Updates an order's data. Calling this method will modify an existing purchase order.
	   * Since this method updates all the order fields and all the order lines, normally the
	   * following steps are followed:
	   *
	   * 1. A call to get to retrieve the current information
       * 2. Modify the fields to update. Add, update or remove order lines.
       * 3. Call update
       *
	   * The existing order lines will be deleted, and new ones will be created with those
	   * provided in the OrderWS object passed as a parameter. In the end, the order identified
	   * by the field id will look exactly the same as the parameter passed.
	   * The flag useItem present in each order line (OrderLineWS) works the same way when
	   * updating an order as it does when creating one. This is something to consider when you
 	   * want to add order lines to an existing purchase order. You could also use this flag when
	   * updating an order line, but is not as common. Keep in mind that when your retrieve an
	   * order from the system, all its order lines will be having the flag useItem equal to false,
	   * regardless on how the were created.
	   *
	   * @param OrderWS $order The updated order data. This can be either obtained from a previous
	   *		call to getOrder() or created directly by your application, although the latter is unadvised
	   *		(if you do not fill a field, its previous content is lost, so the advised procedure is to first
	   *		retrieve the data to update, make the changes needed and resubmit that same data back).
	   *
	   * @param updateOrder $parameters
	   * @return updateOrderResponse
	   */
	  public function updateOrder( OrderWS $order ) {

	  		 $o = new updateOrder();
	  		 $o->arg0 = $order;

	  		 return $this->invoke( 'updateOrder', $o );
  	  }

	  /**
	   * Returns the latest order for the given user that contains item/s of the given item type.
	   *
	   * @param Integer $userId The identifier of the user whose order is to be returned.
	   * @param Integer $itemTypeId The identifier of the type of item/s that the order must contain.
	   * @return getLatestOrderByItemTypeResponse (OrderWS) The latest order for the given user that contains item's of
	   * 		 the given item type. Returns null if no such order exists. If the userId or itemTypeId are null, a
	   * 		 JbillingAPIException is thrown.
	   */
	  public function getLatestOrderByItemType( $userId, $itemTypeId ) {

	  		 $o = new getLatestOrderByItemType();
	  		 $o->arg0 = $userId;
	  		 $o->arg1 = $itemTypeId;

	  		 return $this->invoke( 'getLatestOrderByItemType', $o );
  	  }

	  /**
	   * Returns a list of all orders of a specific periodicity for a given user. The method only
	   * returns the order's IDs, so subsequent calls to 'getOrder' are necessary if you need the objects.
	   *
	   * @param Integer $userId The user for which the extraction is desired.
	   * @param Integer $periodId Identifier for the period type. This value can be obtained from the jBilling
	   * 		User Interface under “Orders -> Periods” or from your billing administrator.
	   * @return getOrderByPeriodResponse (Integer[]) Array containing the identifiers for the orders that respond to the input parameters.
	   */
	  public function getOrderByPeriod( $userId, $periodId ) {

	  		 $o = new getOrderByPeriod();
	  		 $o->arg0 = $userId;
	  		 $o->arg1 = $periodId;

	  		 return $this->invoke( 'getOrderByPeriod', $o );
	  }

	  /**
	   * Allows the mediation current one-time order to be updated with an event for this user for the given date. This method is
	   * used for real-time mediation of events. See the “Telecom Guide” for more information about the mediation module, mediation
	   * events and current one-time orders. The order lines update existing lines or create new lines, depending on what items are
	   * already in the order. The pricing fields determine the pricing for this event, The event description is saved in the mediation
	   * event record. Returns the updated order.
	   *
	   * @param Integer $userId The identifier of the user whose current one-time order is being updated and returned.
	   * @param OrderLineWS[] $lines The order lines to be added to, or used to update, the current one-time order, depending on whether
	   * 		the items are already in the order.
	   * @param PricingField[] $fields Array of PricingField structures specifying optional pricing parameters to be passed to the rules
	   * 		engine for evaluation. Pricing fields are descriptors that provide further information to the pricing engine and aid in
	   * 		forming the price for the border itself. This parameter is optional and used in conjunction with the pricing rules engine.
	   * 		See section “A word on pricing” for a more detailed explanation of the use of pricing fields and the purpose of rule-based pricing.
	   * 		Note to SOAP based integration implementors: the pricingFields parameter of this call is implemented as an array of PricingField
	   * 		structures only in the API. Direct calls to the SOAP layer require you to encode and decode this array of values into a serialized
	   * 		string (so, for SOAP calls, the third parameter is actually a String). For more details on how to serialize these structures into
	   * 		strings, see section “A word on pricing”.
	   * @param Date $date The date that determines which current one-time order should be updated and returned. The date will fall within
	   * 		the order's active period.
	   * @param String $eventDescription The description to be used for the mediation event record.
	   * @return updateCurrentOrderResponse OrderWS: The current one-time order for the user for the given date. If the user has no 
	   * 		 mediation main-subscription order, a JbillingAPIException is thrown.
	   */
  	  public function updateCurrentOrder( $userId, array $lines, array $fields, $date, $eventDescription ) {

  	  		 $o = new updateCurrentOrder();
  	  		 $o->arg0 = $userId;
  	  		 $o->arg1 = $lines;
  	  		 $o->arg2 = $fields;
  	  		 $o->arg3 = $date;
  	  		 $o->arg4 = $eventDescription;
                         
  	  		 return $this->invoke( 'updateCurrentOrder', $o );
  	  }

	  /**
	   * Retrieves a specific order line by supplying its identifier.
	   *
	   * @param Integer $orderLineId Unique identifier of the desired order line. You would know
	   * 		about this ID by first getting a complete order.
	   * @return getOrderLineResponse (OrderLineWS) The order line's information.
	   */
	  public function getOrderLine( $orderLineId ) {

	  		 $o = new getOrderLine();
	  		 $o->arg0 = $orderLineId;

	  		 return $this->invoke( 'getOrderLine', $o );
	  }

	  /**
	   * Returns the ids of the last orders for the given user that contain item's of the given item type. The number
	   * parameter limits the maximum amount of order ids returned.
	   *
	   * @param Integer $userId The identifier of the user whose order ids are to be returned.
	   * @param Integer $itemTypeId The identifier of the type of item's that the orders must contain.
	   * @param Integer $number The maximum number of order ids to be returned.
	   * @return getLastOrdersByItemTypeResponse (Integer[]) An array of the order ids for the given user that contain
	   * 		 item's of the given type. Returns null if the userId or number parameters are null.
	   */
	  public function getLastOrdersByItemType( $userId, $itemTypeId, $number ) {

	  		 $o = new getLastOrdersByItemType();
	  		 $o->arg0 = $userId;
	  		 $o->arg1 = $itemTypeId;
	  		 $o->arg2 = $number;

	  		 return $this->invoke( 'getLastOrdersByItemType', $o );
  	  }

	  /**
	   * This method returns a list of all items registered for a company in jBilling at the moment
	   * the call is placed. It is useful to keep the data in other application synchronized with jBilling.
	   *
	   * @return getAllItemsResponse (ItemDTOEx[]) Array of item description data structures containing the information
	   * 		 for all items currently registered in jBilling.
	   */
  	  public function getAllItems() {

  	  		 $o = new getAllItems();

  	  		 return $this->invoke( 'getAllItems', $o );
	  }

	  /**
	   * @todo Get documentation
	   *
	   * @param getUsersByStatus $parameters
	   * @return getUsersByStatusResponse
	   */
	  public function getUsersByStatus(getUsersByStatus $parameters) {

	  		 return $this->__soapCall( 'getUsersByStatus', array($parameters), $this->options );
	  }

	  /**
	   * Updates credit card information for a user. A user can have more than one credit card record. After calling this
	   * method, the user will only have this credit card. This method will remove all the existing credit cards and then
	   * assign the one given as a parameter.
	   *
	   * @param Integer $userId The identifier of the user whose credit card information is being updated.
	   * @param CreditCardDTO $creditCard The credit card's data with the updated values.
	   * @return updateCreditCardResponse None. If the parameters provided are null or incorrect, a JbillingAPIException is thrown.
	   */
	  public function updateCreditCard( $userId, CreditCardDTO $creditCard ) {

	  		 $o = new updateCreditCard();
	  		 $o->arg0 = $userId;
	  		 $o->arg1 = $creditCard;

	  		 return $this->invoke( 'updateCreditCard', $o );
  	  }

	  /**
	   * Returns the latest invoice for the given user that contains item's of the given item type.
	   * 
	   * @param Integer $userId The identifier of the user whose invoice is to be returned.
	   * @param Integer $itemTypeId The identifier of the type of item's that the invoice must contain.
	   * @return getLatestInvoiceByItemTypeResponse InvoiceWS: The latest invoice for the given user that contains item's
	   * 		 of the given item type. Returns null if no such invoice exists or the userId is null.
	   */
	  public function getLatestInvoiceByItemType( $userId, $itemTypeId ) {

	  		 $o = new getLatestInvoiceByItemType();
	  		 $o->arg0 = $userId;
	  		 $o->arg1 = $itemTypeId;

	  		 return $this->invoke( 'getLatestInvoiceByItemType', $o );
	  }

	  /**
	   * Performs pricing calculations on an order as if it was inserted, but does not actually
	   * create the order. Useful if you need to have immediate feedback on the pricing applied
	   * to a specific customer or item combinations, specially if you use rule-driven pricing (see
 	   * the “Extensions Guide” for more information on rule-based pricing).
	   *
	   * @param OrderWS $order Holds the data for the order being rated
	   * @return rateOrderResponse (OrderWS) The data for the order, as generated by the pricing engine. Mostly, it holds the
	   *		 data you passed as input, except for the price and amount fields, that should contain
	   *		 proper values calculated for the order according to pricing rules.
	   */
	  public function rateOrder( OrderWS $order ) {

	  		 $o = new rateOrder();
	  		 $o->arg0 = $order;

	  		 return $this->invoke( 'rateOrder', $o );
	  }

	  /**
	   * This method retrieves the invoice information regarding a specific invoice in the system. 
	   *
	   * @param Integer $invoiceId Identifier for the invoice that should be retrieved.
	   * @return getInvoiceWSResponse (InvoiceWS) Contains the information regarding the invoice that was requested.
	   * 		 If the identifier supplied as argument is null or does not represent a currently existing invoice, a
	   * 		 JbillingAPIException is thrown.
	   */
	  public function getInvoiceWS( $invoiceId ) {

	  		 $o = new getInvoiceWS();
	  		 $o->arg0 = $invoiceId;

	  		 return $this->invoke( 'getInvoiceWS', $o );
	  }

	  /**
	   * @todo Get documentation 
	   *
	   * @param getUserContactsWS $parameters
	   * @return getUserContactsWSResponse
	   */
	  public function getUserContactsWS($userId) {
	  		 
	  		 $o = new getUserContactsWS();
 			 $o->arg0 = $userId;
 			 
	  		 return $this->__soapCall( 'getUserContactsWS', array($o), $this->options );
	  }

	  /**
	   * @todo Get documentation  
	   *
	   * @param getUsersByCreditCard $parameters
	   * @return getUsersByCreditCardResponse
	   */
	  public function getUsersByCreditCard(getUsersByCreditCard $parameters) {

	  		 return $this->__soapCall( 'getUsersByCreditCard', array($parameters), $this->options );
	  }
	  


	  /**
	   * This method creates a new item category. A new record with this item category information will be inserted
	   * in the database. 
	   *
	   * @param ItemTypeWS $itemTypeWS Information for the item category that is being created.
	   * @return createItemCategoryResponse (Integer) The newly created item's category identifier. If required fields in the input data structure
	   * 		 are missing, a JbillingAPIException is thrown to signal the error.
	   */
	  public function createItemCategory( ItemTypeWS $itemTypeWS ) {

	  		 $o = new createItemCategory();
	  		 $o->arg0 = $itemTypeWS;

	  		 return $this->invoke( 'createItemCategory', $o );
  	  }

	  /**
	   * Performs the remote method invocation to the jBilling CXF API
	   * 
	   * @param $function The remote method/function name to invoke
	   * @param $parameters The appropriate jBilling WSDL (object) parameter for the method call
	   * @return The WebService response
	   * @throws JbillingAPIException
	   */
	  private function invoke( $function, $parameters ) {

			  try {
	  		  	     $result = $this->__soapCall( $function, array( $parameters ), $this->options );
	  		  	     if( is_soap_fault( $result ) )
	  		  	     	 throw new JbillingAPIException( $result );
			  }
			  catch( Exception $e ) {

					 throw new JbillingAPIException( $this->__soap_fault );
			  }

			  return $result;
	  }
	  
      /**
	   * Updates ach information for a user.
	   *
	   * @param Integer $userId The identifier of the user whose ACH information is being updated.
	   * @param AchDTO $ach The ACH data with the updated values.
	   * @return updateAchResponse None. If the parameters provided are null or incorrect, a JbillingAPIException is thrown.
	   */
	  public function updateAch( $userId, AchDTO $ach ) {

	  		 $o = new updateAch();
	  		 $o->arg0 = $userId;
	  		 $o->arg1 = $ach;

	  		 return $this->invoke( 'updateAch', $o );
  	  }
  	  
      /**
	   * Obtains auto payment mode for a user
	   *
	   * @param Integer $userId The identifier of the user whose payment mode is being queried.
	   * @return setAutoPaymentTypeResponse (Integer) The current payment type for the user, or 
	   * null if the user has no automatic payment type associated.
	   */
	  public function getAuthPaymentType( $userId ) {

	  		 $o = new getAuthPaymentType();
	  		 $o->arg0 = $userId;
	  		 
	  		 return $this->invoke( 'getAuthPaymentType', $o );
  	  }
  	  
      /**
	   * Sets the auto payment mode for a user
	   *
	   * @param Integer $userId The identifier of the user whose payment mode is being queried.
	   * @param Integer $autoPaymentType The identifier of the payment type that is to be set or reset.
	   * @param Integer $use TRUE if the payment type is to be set, FALSE if the payment type is to be reset.
	   * @return setAutoPaymentTypeResponse None. If the parameters provided are null or incorrect, a JbillingAPIException is thrown.
	   */
	  public function setAuthPaymentType( $userId, $autoPaymentType, $use ) {

	  		 $o = new setAuthPaymentType();
	  		 $o->arg0 = $userId;
	  		 $o->arg1 = $autoPaymentType;
	  		 $o->arg2 = $use;
	  		 
	  		 return $this->invoke( 'setAuthPaymentType', $o );
  	  }
  	  
      /**
	   * Creates an invoice from a given order ID.
	   *
	   * @param Integer $orderId The identifier of the order from which to create the invoice.
	   * @param Integer $invoiceId The identifier of the invoice.
	   * @return createInvoiceFromOrderResponse (Integer) The ID of the newly created invoice.
	   * If the parameters provided are null or incorrect, a JbillingAPIException is thrown.
	   */
	  public function createInvoiceFromOrder( $orderId, $invoiceId ) {

	  		 $o = new createInvoiceFromOrder();
	  		 $o->arg0 = $orderId;
	  		 $o->arg1 = $invoiceId;
	  		 
	  		 return $this->invoke( 'createInvoiceFromOrder', $o );
  	  }
  	  
      /**
	   * Returns the invoice PDF for the given invoice ID.
	   *
	   * @param Integer $invoiceId The identifier of the invoice.
	   * @return getPaperInvoicePDF (bytes) The invoice PDF bytes.
	   * If the parameters provided are null or incorrect, a JbillingAPIException is thrown.
	   */
	  public function getPaperInvoicePDF( $invoiceId ) {

	  		 $o = new getPaperInvoicePDF();
	  		 $o->arg0 = $invoiceId;
	  		 
	  		 return $this->invoke( 'getPaperInvoicePDF', $o );
  	  }
}

class SessionInternalError {
}

class getLatestInvoice {
  public $arg0; // int
}

class getLatestInvoiceResponse {
  public $return; // invoiceWS
}

class deleteInvoice {
  public $arg0; // int
}

class deleteInvoiceResponse {
}

class getLatestOrder {
  public $arg0; // int
}

class getLatestOrderResponse {
  public $return; // orderWS
}

class processPayment {
  public $arg0; // paymentWS
}

class processPaymentResponse {
  public $return; // paymentAuthorizationDTOEx
}

class payInvoice {
  public $arg0; // int
}

class payInvoiceResponse {
  public $return; // paymentAuthorizationDTOEx
}

class getLastOrders {
  public $arg0; // int
  public $arg1; // int
}

class getLastOrdersResponse {
  public $return; // int
}

class updateItem {
  public $arg0; // itemDTOEx
}

class updateItemResponse {
}

class updateUserContact {
  public $arg0; // int
  public $arg1; // int
  public $arg2; // contactWS
}

class updateUserContactResponse {
}

class getUserItemsByCategory {
  public $arg0; // int
  public $arg1; // int
}

class getUserItemsByCategoryResponse {
  public $return; // int
}

class getPayment {
  public $arg0; // int
}

class getPaymentResponse {
  public $return; // paymentWS
}

class getUserWS {
  public $arg0; // int
}

class getUserWSResponse {
  public $return; // userWS
}

class validatePurchase {
  public $arg0; // int
  public $arg1; // int
  public $arg2; // string
}

class validatePurchaseResponse {
  public $return; // validatePurchaseWS
}

class getLastInvoicesByItemType {
  public $arg0; // int
  public $arg1; // int
  public $arg2; // int
}

class getLastInvoicesByItemTypeResponse {
  public $return; // int
}

class createItem {
  public $arg0; // itemDTOEx
}

class createItemResponse {
  public $return; // int
}

class createOrderPreAuthorize {
  public $arg0; // orderWS
}

class createOrderPreAuthorizeResponse {
  public $return; // paymentAuthorizationDTOEx
}

class getCurrentOrder {
  public $arg0; // int
  public $arg1; // dateTime
}

class getCurrentOrderResponse {
  public $return; // orderWS
}

class authenticate {
  public $arg0; // string
  public $arg1; // string
}

class authenticateResponse {
  public $return; // int
}

class getUsersNotInStatus {
  public $arg0; // int
}

class getUsersNotInStatusResponse {
  public $return; // int
}

class getUsersInStatus {
  public $arg0; // int
}

class getUsersInStatusResponse {
  public $return; // int
}

class getLastInvoices {
  public $arg0; // int
  public $arg1; // int
}

class getLastInvoicesResponse {
  public $return; // int
}

class getUserId {
  public $arg0; // string
}

class getUserIdResponse {
  public $return; // int
}

class getItem {
  public $arg0; // int
  public $arg1; // int
  public $arg2; // string
}

class getItemResponse {
  public $return; // itemDTOEx
}

class updateUser {
  public $arg0; // userWS
}

class updateUserResponse {
}

class createOrder {
  public $arg0; // orderWS
}

class createOrderResponse {
  public $return; // int
}

class getUserTransitions {
  public $arg0; // dateTime
  public $arg1; // dateTime
}

class getUserTransitionsResponse {
  public $return; // userTransitionResponseWS
}

class applyPayment {
  public $arg0; // paymentWS
  public $arg1; // int
}

class applyPaymentResponse {
  public $return; // int
}

class createUser {
  public $arg0; // userWS
}

class createUserResponse {
  public $return; // int
}

class getLastPayments {
  public $arg0; // int
  public $arg1; // int
}

class getLastPaymentsResponse {
  public $return; // int
}

class getInvoicesByDate {
  public $arg0; // string
  public $arg1; // string
}

class getInvoicesByDateResponse {
  public $return; // int
}

class getUsersByCustomField {
  public $arg0; // int
  public $arg1; // string
}

class getUsersByCustomFieldResponse {
  public $return; // int
}

class deleteUser {
  public $arg0; // int
}

class deleteUserResponse {
}

class isUserSubscribedTo {
  public $arg0; // int
  public $arg1; // int
}

class isUserSubscribedToResponse {
  public $return; // double
}

class getLatestPayment {
  public $arg0; // int
}

class getLatestPaymentResponse {
  public $return; // paymentWS
}

class updateOrderLine {
  public $arg0; // orderLineWS
}

class updateOrderLineResponse {
}

class deleteOrder {
  public $arg0; // int
}

class deleteOrderResponse {
}

class getUserTransitionsAfterId {
  public $arg0; // int
}

class getUserTransitionsAfterIdResponse {
  public $return; // userTransitionResponseWS
}

class createInvoice {
  public $arg0; // int
  public $arg1; // boolean
}

class createInvoiceResponse {
  public $return; // int
}

class create {
  public $arg0; // userWS
  public $arg1; // orderWS
}

class createResponse {
  public $return; // createResponseWS
}

class getOrder {
  public $arg0; // int
}

class getOrderResponse {
  public $return; // orderWS
}

class createOrderAndInvoice {
  public $arg0; // orderWS
}

class createOrderAndInvoiceResponse {
  public $return; // int
}

class updateOrder {
  public $arg0; // orderWS
}

class updateOrderResponse {
}

class getLatestOrderByItemType {
  public $arg0; // int
  public $arg1; // int
}

class getLatestOrderByItemTypeResponse {
  public $return; // orderWS
}

class updateCurrentOrder {
  public $arg0; // int
  public $arg1; // orderLineWS
  public $arg2; // string
  public $arg3; // dateTime
  public $arg4; // string
}

class updateCurrentOrderResponse {
  public $return; // orderWS
}

class getOrderByPeriod {
  public $arg0; // int
  public $arg1; // int
}

class getOrderByPeriodResponse {
  public $return; // int
}

class getOrderLine {
  public $arg0; // int
}

class getOrderLineResponse {
  public $return; // orderLineWS
}

class getLastOrdersByItemType {
  public $arg0; // int
  public $arg1; // int
  public $arg2; // int
}

class getLastOrdersByItemTypeResponse {
  public $return; // int
}

class getAllItems {
}

class getAllItemsResponse {
  public $return; // itemDTOEx
}

class updateCreditCard {
  public $arg0; // int
  public $arg1; // creditCardDTO
}

class updateCreditCardResponse {
}

class getUsersByStatus {
  public $arg0; // int
  public $arg1; // int
  public $arg2; // boolean
}

class getUsersByStatusResponse {
  public $return; // int
}

class getLatestInvoiceByItemType {
  public $arg0; // int
  public $arg1; // int
}

class getLatestInvoiceByItemTypeResponse {
  public $return; // invoiceWS
}

class rateOrder {
  public $arg0; // orderWS
}

class rateOrderResponse {
  public $return; // orderWS
}

class getUsersByCreditCard {
  public $arg0; // string
}

class getUsersByCreditCardResponse {
  public $return; // int
}

class getUserContactsWS {
  public $arg0; // int
}

class getUserContactsWSResponse {
  public $return; // contactWS
}

class getInvoiceWS {
  public $arg0; // int
}

class getInvoiceWSResponse {
  public $return; // invoiceWS
}

class createItemCategory {
  public $arg0; // itemTypeWS
}

class createItemCategoryResponse {
  public $return; // int
}
class getAuthPaymentType {
  public $arg0;  // int
}
class getAuthPaymentTypeResponse {
  public $return; // int
}
class setAuthPaymentType {
  public $arg0; // int
  public $arg1; // int
  public $arg2; // boolean
}
class setAuthPaymentTypeResponse {
}
class updateAch {
  public $arg0; // int
  public $arg1; // AchDTO
}
class updateAchResponse {
}
class createInvoiceFromOrder {
	public $arg0; // int
	public $arg1; // int
}
class createInvoiceFromOrderResponse {
	public $return; // int
}
class getPaperInvoicePDF {
	public $arg0; // int
}
class getPaperInvoicePDFResponse {
	public $return; // byte[]
}
?>
