<?php 

require_once '../lib/HessianPHP/dist/HessianClient.php';

class HessianAPI {

	  private $url;
	  private $proxy;

	  public function HessianAPI( $endpoint, $username, $password ) {

	  		 try {
	  			  Hessian::errorReporting( HESSIAN_EXCEPTION ); 
	  			  $this->proxy = &new HessianClient( $endpoint, array( 'username' => $username, 'password' => $password ) );

			  	  if( Hessian::error() )
					  throw new JbillingAPIException( Hessian::error() );
	  		 }
	  		 catch( Exception $e ) {

	  			    throw new JbillingAPIException( $e->getMessage() );
	  		 }
	  }

	  public function createUser( UserWS $user ) {

	  		 $this->proxy->createUser( $user );
	  }
}
?>