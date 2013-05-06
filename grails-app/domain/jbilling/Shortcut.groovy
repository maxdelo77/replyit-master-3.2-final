/*
 * JBILLING CONFIDENTIAL
 * _____________________
 *
 * [2003] - [2012] Enterprise jBilling Software Ltd.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Enterprise jBilling Software.
 * The intellectual and technical concepts contained
 * herein are proprietary to Enterprise jBilling Software
 * and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden.
 */

package jbilling

class Shortcut implements Serializable {
	
	static transients = [ "messageCode" ]
	
	static mapping = {
		id generator: 'org.hibernate.id.enhanced.TableGenerator',
		   params: [
		   table_name: 'jbilling_seqs',
		   segment_column_name: 'name',
		   value_column_name: 'next_id',
		   segment_value: 'shortcut'
		   ]
	}

    static constraints = {
        action(blank:true, nullable:true)
        name(blank:true, nullable:true)
        objectId(nullable:true)
    }
	
	Integer userId
	String controller
	String action
	String name
	Integer objectId
	
	def String getMessageCode() {
		StringBuilder builder = new StringBuilder();

		//with breadcrumb as prefix, we maintain the same i18n properties
		builder.append("breadcrumb") 
		if (controller) builder.append('.').append(controller)
		if (action) builder.append('.').append(action)
		if (name) builder.append('.').append(name)
		if (!name && objectId) builder.append('.id')

	   return builder.toString()
	}
	
	
	@Override
	def boolean equals(other) {
		if (this.is(other)) return true;
		if (getClass() != other.class) return false;

		Shortcut that= (Shortcut) other
		if (this.action != that.action) return false;
		if (this.controller != that.controller) return false;
		if (this.name != that.name) return false;
		if (this.objectId != that.objectId) return false;
		return true;
	}
	
	@Override
	def int hashCode() {
		int result;

		result = controller.hashCode();
		result = 31 * result + (action != null ? action.hashCode() : 0);
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + (objectId != null ? objectId.hashCode() : 0);
		return result;
	}

	@Override
	def String toString() {
		return "Shortcut {id=${id}, userId=${userId}, controller=${controller}, action=${action}, objectId=${objectId}, name=${name}}"
	}
}
