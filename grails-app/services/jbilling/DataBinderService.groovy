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

import groovy.util.slurpersupport.GPathResult
import org.apache.commons.lang.WordUtils
import org.springframework.beans.BeanWrapper
import org.springframework.beans.BeanWrapperImpl
import com.sapienter.jbilling.client.util.CustomDateBinder
/**
 * DataBinderService.
 * @author othman El Moulat
 * @since  6/10/12
 *
 */
class DataBinderService {

    boolean transactional = false
    def grailsApplication
    private BeanWrapper wrapper = new BeanWrapperImpl()

    public List bindAllXmlData (Class targetClass, GPathResult source, List properties) {
        if (targetClass == null || source == null || properties == null) return null
        def resultList = []
        def  className = WordUtils.uncapitalize(targetClass.simpleName)
        source[className]?.each {
            def boundObj = bindXmlData(targetClass, it, properties)
            System.out.println(boundObj)
            resultList.add(boundObj)
        }
        return resultList
    }

    public Object bindXmlData (Class targetClass, GPathResult source, List properties) {
        if (targetClass == null || source == null || properties == null) return null
        def targetObject = grailsApplication.classLoader.loadClass(targetClass.name).newInstance()
        if (targetObject) {
            return bindXmlData(targetObject, source, properties)
        } else {
            return null
        }
    }

    public Object bindXmlData (Object target, GPathResult source, List properties) {
        if (target == null || source == null || properties == null) return null
        wrapper.registerCustomEditor (Date.class, new CustomDateBinder())
        wrapper.setWrappedInstance(target)
        properties.each {String property ->
            if (property.contains('.')) {//This indicates a domain class to bind e.g. experiment.id -> Experiment
                def propertyName = property.tokenize('.')
                def id = source[propertyName[0]]["@${propertyName[1]}"]?.toString()
                if (id != null) {
                    def subdomainInstance = null
                    try {subdomainInstance = grailsApplication.classLoader.loadClass("edu.kit.iism.experimentcenter.${WordUtils.capitalize(propertyName[0])}").get(id)} catch (Exception ex) {}
                    if (subdomainInstance != null) wrapper.setPropertyValue(propertyName[0], subdomainInstance)
                }
            } else if (property.equals('id')) { //The id property is set as an attribute rather than text
                def id = source['@id']?.toString()
                if (id != null) wrapper.setPropertyValue(property, id)
            } else { //regular attributes
                def prop = source[property]?.toString()
                if (prop != null) wrapper.setPropertyValue(property, prop)
            }
        }
        return target
    }
}
