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
  * UserTransitionResponseWS
  * @author Jeremy Hahn
  * @version 1.0
  * @copyright Make A Byte, inc
  * @package com.makeabyte.contrib.jbilling.php
  */

class UserTransitionResponseWS {

      var $fromStatusId;       // Status of the subscription before the transition took place. See Appendix A for acceptable values.
      var $id;                 // Unique identifier for the transition record.
      var $toStatusId;         // Status of the subscription after the transition took place.
      var $transitionDate;     // Date and time the transition took place.
      var $userId;             // Identifies the user account that suffered the subscription status change.
      
      /**
       * The UserTransitionResponseWS constructor
       * 
       * @access public
       */
	  public function UserTransitionResponseWS() {
	  }
	  /**
       * Sets the fromStatusId property of the UserTransitionResponseWS object
       * 
       * @access public
       * @param Integer $id Unique identifier for the transition record.
       * @return void
       */
	  public function setFromStatusId( $id ) {
	  	
	  	     $this->fromStatusId = $id;
	  }
	  /**
       * Sets the id property of the UserTransitionResponseWS object
       * 
       * @access public
       * @param Integer $id 
       * @return void
       */
	  public function setId( $id ) {
	  	
	  	     $this->id = $id;
	  }
	  /**
       * Sets the toStatusId property of the UserTransitionResponseWS object
       * 
       * @access public
       * @param Integer $id Status of the subscription after the transition took place.
       * @return void
       */
	  public function setToStatusId( $id ) {
	  	
	  	     $this->toStatusId = $id;
	  }
	  /**
       * Sets the fromStatusId property of the UserTransitionResponseWS object
       * 
       * @access public
       * @param Date $date Date and time the transition took place.
       * @return void
       */
	  public function setTransitionDate( $date ) {
	  	
	  	     $this->transitionDate = $date;
	  }
	  /**
       * Sets the userId property of the UserTransitionResponseWS object
       * 
       * @access public
       * @param Integer $id Status of the subscription before the transition took place. See Appendix A for acceptable values.
       * @return void
       */
	  public function setUserId( $id ) {
	  	
	  	     $this->userId = $id;
	  }
      /**
       * Gets the fromStatusId property of the UserTransitionResponseWS object
       * 
       * @access public
       * @return Integer Status of the subscription before the transition took place. See Appendix A for acceptable values.
       */
	  public function getFromStatusId() {
	  	
	  	     return $this->fromStatusId;
	  }
	  /**
       * Gets the id property of the UserTransitionResponseWS object
       * 
       * @access public
       * @return Integer Unique identifier for the transition record.
       */
	  public function getId() {
	  	
	  	     return $this->id;
	  }
	  /**
       * Gets the toStatus property of the UserTransitionResponseWS object
       * 
       * @access public
       * @return Integer Status of the subscription after the transition took place.
       */
	  public function getToStatusId() {
	  	
	  	     return $this->toStatusId;
	  }
	  /**
       * Gets the transitionDate property of the UserTransitionResponseWS object
       * 
       * @access public
       * @return Date Date and time the transition took place.
       */
	  public function getTransitionDate() {
	  	
	  	     return $this->transitionDate;
	  }
	  /**
       * Gets the userId property of the UserTransitionResponseWS object
       * 
       * @access public
       * @return Integer Identifies the user account that suffered the subscription status change.
       */
	  public function getUserId() {
	  	
	  	     return $this->userId;
	  }
}
?>