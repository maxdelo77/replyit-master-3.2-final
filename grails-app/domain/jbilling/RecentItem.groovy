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
 * RecentItem
 
 * @author Brian Cowdery
 * @since  07-12-2010
 */
class RecentItem implements Serializable {

    static mapping = {
        id generator: 'org.hibernate.id.enhanced.TableGenerator',
           params: [
           table_name: 'jbilling_seqs',
           segment_column_name: 'name',
           value_column_name: 'next_id',
           segment_value: 'recent_item'
           ]
    }

    static constraints = {
        userId(nullable: false)
        objectId(nullable: false)
        type(nullable: false)
    }

    Integer userId
    Integer objectId
    RecentItemType type

    boolean equals(o) {
        if (this.is(o)) return true;
        if (getClass() != o.class) return false;

        RecentItem that = (RecentItem) o;

        if (objectId != that.objectId) return false;
        if (type != that.type) return false;
        if (userId != that.userId) return false;

        return true;
    }

    int hashCode() {
        int result;
        result = (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (objectId != null ? objectId.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    def String toString() {
        return "RecentItem{id=${id}, type=${type}, objectId=${objectId}}"
    }
}
