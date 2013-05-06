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

/**
* JBillingDisplayTagLib
*
* @author Vikas Bodani
* @since 03/09/11
*/

class JBillingDisplayTagLib {

    
    def showProperCase = { attrs, body -> 
        
        StringBuffer sb= new StringBuffer("")
        
        String str= attrs.value
        if (str) {
            sb.append(str.charAt(0).toUpperCase())
            if (str.length() > 1) 
                sb.append(str.substring(1))
        }
        
        out << sb.toString()
    }
    
    /**
     * Prints the phone number is a nice format
     */
    def phoneNumber = { attrs, body ->
        
        def countryCode= attrs.countryCode
        def areaCode= attrs.areaCode 
        def number= attrs.number
        
        StringBuffer sb= new StringBuffer("");
        
        if (countryCode) {
            sb.append(countryCode).append("-")
        }
        
        if (areaCode) {
            sb.append(areaCode).append("-")
        }
        if (number) {
            
            if (number.length() > 4) {
                char[] nums= number.getChars()
                
                int i=0;
                for(char c: nums) {
                   //check if this value is a number between 0 and 9
                   if (c < 58 && c > 47 ) {
                       if (i<3) {
                           sb.append(c)
                           i++
                       } else if (i == 3) {
                           sb.append("-").append(c)
                           i++
                       } else {
                           sb.append(c)
                           i++
                       }
                   }
                }
            } else {
                sb.append(number)
            }
        }
        
        out << sb.toString()
    }

}