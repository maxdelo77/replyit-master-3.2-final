<?php

class ItemTypeWS {

	  /**
	   * Identifier of this item type.
	   * 
	   * @var Integer
	   */
  	  var $id;

  	  /**
  	   * Type of order line for this item. See “Order Line Type Codes” in “Appendix A” for valid values for this field.
  	   * 
  	   * @var Integer
  	   */
      var $orderLineTypeId;
      
      /**
       * Description for this item type.
       * 
       * @var String
       */
      var $description;

      
      /**
       * The ItemTypeWS constructor
       * 
       * @access public
       */
	  public function ItemTypeWS() {
	  }

	  /**
	   * Sets the id property of the ItemTypeWS object
	   * 
	   * @access public
	   * @param Integer $id A unique number that identifies this record.
	   * @return void
	   */
	  public function setId( $id ) {

	  	     $this->id = $id;
	  }
	  /**
	   * Sets the orderLineTypeId property of the ItemTypeWS object
	   * 
	   * @access public
	   * @param Integer $orderLineTypeId. See Appendix A for a list of acceptable values.
	   * @return void
	   */
	  public function setOrderLineTypeId( $orderLineTypeId ) {

	  	     $this->orderLineTypeId = $orderLineTypeId;
	  }
	  /**
	   * Sets the description property of the ItemTypeWS object
	   * 
	   * @access public
	   * @param String $desc The description of this item
	   * @return void
	   */
	  public function setDescription( $desc ) {

	  	     $this->description = $desc;
	  }

	  /**
	   * Gets the id property of the ItemTypeWS object
	   * 
	   * @access public
	   * @return Integer A unique number that identifies this record.
	   */
	  public function getId() {

	  	     return $this->id;
	  }
	  
	  /**
	   * Gets the orderLineTypeId property of the ItemTypeWS object
	   * 
	   * @access public
	   * @return orderLineTypeId. See Appendix A for a list of values.
	   */
	  public function getOrderLineTypeId() {

	  	     return $this->orderLineTypeId;
	  }
	  
	  /**
	   * Gets the description property of the ItemTypeWS object
	   * 
	   * @access public
	   * @return String The description of this item
	   */
	  public function getDescription() {

	  	     return $this->description;
	  }
}
?>