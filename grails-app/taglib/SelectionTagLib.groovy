
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

import java.util.List;

import com.sapienter.jbilling.server.user.permisson.db.RoleDTO;
import com.sapienter.jbilling.server.user.db.CompanyDTO
import com.sapienter.jbilling.server.user.db.UserStatusDTO;
import com.sapienter.jbilling.server.user.db.SubscriberStatusDTO;
import com.sapienter.jbilling.server.process.db.PeriodUnitDTO
import com.sapienter.jbilling.server.metafields.MetaFieldValueWS
import com.sapienter.jbilling.server.metafields.db.MetaField;

class SelectionTagLib {
	
	def accountType = { attrs, bodyy -> 
		println System.currentTimeMillis();
		String checking, savings;
		savings=checking= ""
		println "taglib: accountType=" + attrs.value + " " + attrs.name
		
		if (attrs.value == 1 ) {
			checking= "checked=checked" 
		} else if (attrs.value == 2 ){
			savings="checked=checked"
		}
		
		out << "Checking<input type='radio' name=\'" + attrs.name + "\' value=\'1\' " + checking + ">"
		out << "Savings<input type='radio' name=\'"  + attrs.name + "\' value=\'2\' "  + savings + ">"
		
	}

    /**
     * Sets the value if the Meta Field Value is present
     * @attr field REQUIRED the Meta Field
     * @attr fieldsArray REQUIRED the Meta Fields Array Object
     */
    def setFieldValue = { attrs,body->

        List<MetaFieldValueWS> fieldValueWSList = attrs?.fieldsArray
        MetaField field = attrs?.field

        // iterate on list, match the field name with name and set the value
        fieldValueWSList.each { MetaFieldValueWS fieldValueWS->

            if (fieldValueWS.fieldName == field.name) {
                // got it
                out << fieldValueWS.value
            }
        }
    }

    /**
     * Checks if the Meta Field Value is present
     * @attr field REQUIRED the Meta Field
     * @attr fieldsArray REQUIRED the Meta Fields Array Object
     */
    def ifValuePresent = { attrs,body->

        List<MetaFieldValueWS> fieldValueWSList = attrs?.fieldsArray
        MetaField field = attrs?.field

        if (fieldValueWSList*.any {it.fieldName == field.name} ) {
            out << "true"
        }
    }

	def selectRoles = { attrs, body ->
		
		Integer langId= attrs.languageId?.toInteger();
		String name= attrs.name;		
		String value = attrs.value?.toString()
		
		List list= new ArrayList();
		String[] sarr= null;
		for (RoleDTO role: RoleDTO.createCriteria.list(){
			eq('company', new CompanyDTO(session['company_id']))
			order('roleTypeId', 'asc')
		}) {
			String title= role.getTitle(langId);
			sarr=new String[2]
			sarr[0]= role.getRoleTypeId()
			sarr[1]= title
			list.add(sarr)
		}
		out << render(template:"/selectTag", model:[name:name, list:list, value:value])
	}

	def userStatus = { attrs, body ->
		
		Integer langId= attrs.languageId?.toInteger();
		String name= attrs.name;
		String value = attrs.value?.toString()
        List except = attrs.except ?: []

		List list= new ArrayList();
		String[] sarr= null;
		for (UserStatusDTO status: UserStatusDTO.list()) {
			String title= status.getDescription(langId);			
			sarr=new String[2]
			sarr[0]= status.getId()
			sarr[1]= title
            // add the status if its id is not in the exception List
            if(!except.contains(status.getId())){
                list.add(sarr)
            }
		}
		out << render(template:"/selectTag", model:[name:name, list:list, value:value])
		
	}
	
	def subscriberStatus = { attrs, body ->
		
		Integer langId= attrs.languageId?.toInteger();
		String name= attrs.name;
		String value = attrs.value?.toString()
        String cssClass = attrs.cssClass?.toString()

		log.info "Value of tagName=" + name + " is " + value
		
		List list= new ArrayList();
		String[] sarr= null;
		for (SubscriberStatusDTO status: SubscriberStatusDTO.list()) {
			String title= status.getDescription(langId);
			sarr=new String[2]
			sarr[0]= status.getId()
			sarr[1]= title
			list.add(sarr)
		}
		
		out << render(template:"/selectTag", model:[name:name, list:list, value:value, cssClass: cssClass])
	}

	def periodUnit = { attrs, body ->
		
		Integer langId= attrs.languageId?.toInteger();
		String name= attrs.name;
		String value = attrs.value?.toString()

		log.info "Value of tagName=" + name + " is " + value
		
		List list= new ArrayList();
		String[] sarr= null;
		for (PeriodUnitDTO period: PeriodUnitDTO.list()) {
			String title= period.getDescription(langId);
			sarr=new String[2]
			sarr[0]= period.getId()
			sarr[1]= title
			list.add(sarr)
		}
		
		out << render(template:"/selectTag", model:[name:name, list:list, value:value])
	}
	
}