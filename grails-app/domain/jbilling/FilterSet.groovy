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
 * FilterSet
 
 * @author Brian Cowdery
 * @since  03-12-2010
 */
class FilterSet implements Serializable {

    static mapping = {
        id generator: 'org.hibernate.id.enhanced.TableGenerator',
            params: [
                table_name: 'jbilling_seqs',
                segment_column_name: 'name',
                value_column_name: 'next_id',
                segment_value: 'filter_set'
            ]
        filters cascade: "all,delete-orphan"
    }

    static hasMany = [filters: Filter]   

    String name
    Integer userId

    static constraints = {
        name(blank: false, unique: true)
    }

    public String toString ( ) {
        return "FilterSet{id=${id}, name=${name}, userId=${userId}, filters=${filters?.size()}}"
    }
}
