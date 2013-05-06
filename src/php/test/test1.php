<?php
require_once( "../src/JbillingAPIFactory.php" );
$api = jbillingAPIFactory::getAPI( 
  "http://localhost:8080/jbilling/services/api?wsdl",
	"admin;1", "123qwe", JBILLINGAPI_TYPE_CXF );

$UserWS = new UserWS();
$OrderWS = new OrderWS();
$ContactWS = new ContactWS();
$OrderLineWS = new OrderLineWS();
$CreditCardDTO = new CreditCardDTO();

// USER CREATION PROCESS
//
// Define jBilling user properties
$UserWS->setUserName( "PHP-TEST-2222222222222222" );
$UserWS->setPassword( "secret123" );
$UserWS->setLanguageId( 1 ); // English
$UserWS->setMainRoleId( 5 ); // Customer
$UserWS->setRole( "Customer" );
$UserWS->setStatusId( 1 ); // Active
$UserWS->setSubscriberStatusId( 1 ); // Pre-paid

// Define contact data for the user created
$ContactWS->setFirstName( "PHP3" );
$ContactWS->setLastName( "Testing3" );
$ContactWS->setPhoneNumber( "123-456-7890" );
$ContactWS->setEmail( "test@test.com" );
$ContactWS->setAddress1( "123 Anywhere St" );
$ContactWS->setCity( "Some City" );
$ContactWS->setStateProvince( "Some State" );
$ContactWS->setPostalCode( "12345" );
$ContactWS->setFieldNames(array());

// Apply contact object to user contact property
$UserWS->setContact( $ContactWS );

// Define credit card data for the user being created
$CreditCardDTO->setName( "PHP Testing 2" );
$CreditCardDTO->setNumber( "4012888888881881" );
$CreditCardDTO->setSecurityCode( 123 );
$CreditCardDTO->setType( 2 ); // Visa
// Define date as ISO 8601 format
$CreditCardDTO->setExpiry( date("c", strtotime( "now" ) ) );

// Add the credit card to the user credit card property
$UserWS->setCreditCard( $CreditCardDTO );

// ORDER CREATION PROCESS
//
// Create an order line for the order
$OrderLineWS->setUseItem( true );
$OrderLineWS->setItemId( 3300 ); // MAKE SURE THIS ITEM MATCHES AN ITEM YOUR SYSTEM!
$OrderLineWS->setTypeId( 1 ); // Item
$OrderLineWS->setQuantity( 1 );
$OrderLineWS->setDescription( "test from php api" );

// Set jBilling purchase order properties
$OrderWS->setPeriod( 1 ); // Monthly
$OrderWS->setOrderLines( array( $OrderLineWS ) );
$OrderWS->setBillingTypeId( 1 );
$OrderWS->setCurrencyId( 1 ); // US Dollar
$OrderWS->setBillingTypeId( 1 ); // Pre-paid
$OrderWS->setActiveSince( date("c", strtotime( "now" ) ) );
$OrderWS->setIsCurrent( 1 );

// Create order line for update.

$newLine = new OrderLineWS();
$newLine->setUseItem( true );
$newLine->setItemId( 3 );
$newLine->setTypeId( 1 );   // Item line type
$newLine->setQuantity( 2 );
$newLine->setDescription( "test modified" );
/* Updating current order. */



// CREATE THE USER AND THE ORDER
try {
    $result = $api->createUser( $UserWS );
    print_r( $result ) ;
    $OrderWS->setUserId( $result->return );
    print_r( $api->createOrder( $OrderWS ) );

    $api->updateCurrentOrder( $result->return,
        array ($newLine), array(null),
        date("c", strtotime( "now" ) ),
        "New SIM card supply fee");
    print_r($api->getLastRequest());
    print_r($api->getLastResponse());
}
catch( JbillingAPIFactory $e){
    print_r( $e );
}
//print_r( $api->create( $UserWS, $OrderWS ) ); // New id's returned
//print_r( $api->createUser( $UserWS ) ); // New user id returned
//print_r( $api->createOrder( $OrderWS ) ); // New order id returned
//print_r( $api->getUserWS( 22 ) ); // User details for penny bright
?>
