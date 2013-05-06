<?php
require_once( "../src/JbillingAPIFactory.php" );
$api = jbillingAPIFactory::getAPI( 
	"http://localhost:8080/jbilling/services/api?wsdl",
	"admin;1", "123qwe", JBILLINGAPI_TYPE_CXF );
$bytes = $api->getUserId('brunei');
print_r( $bytes->return );
print_r ( date( "c", strtotime("+1 year")) );
?>
