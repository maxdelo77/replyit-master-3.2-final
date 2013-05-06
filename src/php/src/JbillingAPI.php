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
  * JbillingAPI
  * @author Jeremy Hahn
  * @version 1.0
  * @copyright Make A Byte, inc
  * @package com.makeabyte.contrib.jbilling.php
  */

interface JbillingAPI {

		  public function getLatestInvoice( $userId );
		  public function deleteInvoice( $id );
		  public function getLatestOrder( $userId );
		  public function processPayment( PaymentWS $payment );
		  public function getLastOrders( $userId, $number );
		  public function payInvoice( $invoiceId );
		  public function updateItem( ItemDTOEx $item );
		  public function updateUserContact( $userId, $typeId, ContactWS $contact );
		  public function getUserItemsByCategory( $userId, $categoryId );
		  public function getPayment( $paymentId);
		  public function getUserWS( UserWS $user );
		  public function validatePurchase( $userId, $itemId, array $fields );
		  public function getLastInvoicesByItemType( $userId, $itemTypeId, $number );
		  public function createItem( ItemDTOEx $dto );
		  public function createOrderPreAuthorize( OrderWS $order );
		  public function getCurrentOrder( $userId, $date );
		  public function authenticate( $username, $password );
		  public function getUsersNotInStatus( $statusId );
		  public function getUsersInStatus( $statusId );
		  public function getLastInvoices( $userId, $number );
		  public function getUserId( $username );
		  public function getItem( $itemId, $userId = null, $pricingFields = array() );
		  public function createOrder( OrderWS $order );
		  public function updateUser( UserWS $user );
		  public function getUserTransitions( $to, $from );
		  public function applyPayment( PaymentWS $payment, $invoiceId );
		  public function createUser( UserWS $user );
		  public function getInvoicesByDate( $since, $until );
		  public function getLastPayments( $userId, $number );
		  public function getUsersByCustomField( $typeId, $value );
		  public function deleteUser( $userId );
		  public function getLatestPayment( $userId );
		  public function isUserSubscribedTo( $userId, $itemId );
		  public function updateOrderLine( OrderLineWS $line );
		  public function deleteOrder( $orderId );
		  public function createInvoice( $userId, $onlyRecurring = false );
		  public function getUserTransitionsAfterId( $id );
		  public function create( UserWS $user, OrderWS $order );
		  public function createOrderAndInvoice( OrderWS $order );
		  public function getOrder( $orderId );
		  public function updateOrder( OrderWS $order );
		  public function getLatestOrderByItemType( $userId, $itemTypeId );
		  public function getOrderByPeriod( $userId, $periodId );
		  public function updateCurrentOrder( $userId, array $lines, array $fields, $date, $eventDescription );
		  public function getOrderLine( $orderLineId );
		  public function getLastOrdersByItemType( $userId, $itemTypeId, $number );
		  public function getAllItems();
		  public function updateCreditCard( $userId, CreditCardDTO $creditCard );
		  public function getLatestInvoiceByItemType( $userId, $itemTypeId );
		  public function rateOrder( OrderWS $order );
		  public function getInvoiceWS( $invoiceId );
		  public function createItemCategory( ItemTypeWS $itemTypeWS );
		  public function getAuthPaymentType( $userId );
		  public function setAuthPaymentType( $userId, $autoPaymentType, $use );
		  public function updateAch( $userId, AchDTO $ach );
		  public function createInvoiceFromOrder( $orderId, $invoiceId );
		  public function getPaperInvoicePDF($invoiceId);

		  // Need documentation to complete these methods
		  // public function getUsersByStatus(getUsersByStatus $parameters)
		  // public function getUserContactsWS(getUserContactsWS $parameters)
		  // public function getUsersByCreditCard(getUsersByCreditCard $parameters)
}
?>
