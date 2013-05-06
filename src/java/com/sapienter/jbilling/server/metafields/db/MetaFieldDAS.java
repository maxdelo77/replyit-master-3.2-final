/*
 jBilling - The Enterprise Open Source Billing System
 Copyright (C) 2003-2011 Enterprise jBilling Software Ltd. and Emiliano Conde

 This file is part of jbilling.

 jbilling is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 jbilling is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with jbilling.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.sapienter.jbilling.server.metafields.db;

import com.sapienter.jbilling.server.util.db.AbstractDAS;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Brian Cowdery
 * @since 03-Oct-2011
 */
public class MetaFieldDAS extends AbstractDAS<MetaField> {
    private static final String findCountByDTypeName =
            "SELECT count(*) " +
                    "  FROM MetaField a " +
                    " WHERE a.dataType = :dataType "+
                    " AND a.name = :name";

    private static final String findAllIdsByDataTypeNameSQL =
            "SELECT id " +
                    "  FROM MetaField a " +
                    " WHERE a.dataType = :dataType "+
                    " AND a.name = :name";

    @SuppressWarnings("unchecked")
    public List<MetaField> getAvailableFields(Integer entityId, EntityType entityType) {
        DetachedCriteria query = DetachedCriteria.forClass(MetaField.class);
        query.add(Restrictions.eq("entity.id", entityId));
        query.add(Restrictions.eq("entityType", entityType));
        query.addOrder(Order.asc("displayOrder"));
        return getHibernateTemplate().findByCriteria(query);
    }

    @SuppressWarnings("unchecked")
    public MetaField getFieldByName(Integer entityId, EntityType entityType, String name) {
        DetachedCriteria query = DetachedCriteria.forClass(MetaField.class);
        query.add(Restrictions.eq("entity.id", entityId));
        query.add(Restrictions.eq("entityType", entityType));
        query.add(Restrictions.eq("name", name));
        List<MetaField> fields = getHibernateTemplate().findByCriteria(query);
        return !fields.isEmpty() ? fields.get(0) : null;
    }

    public void deleteMetaFieldValuesForEntity(EntityType entityType, int metaFieldId) {
        Session session = getSession();
        List<String> deleteEntitiesList = new ArrayList<String>();
        
        switch (entityType) {
           case INVOICE:
        	   deleteEntitiesList.add(" invoice_meta_field_map ");
               break;
           case CUSTOMER:
        	   deleteEntitiesList.add(" customer_meta_field_map ");
        	   deleteEntitiesList.add(" partner_meta_field_map ");
               break;
           case PRODUCT:
        	   deleteEntitiesList.add(" item_meta_field_map ");
               break;
           case ORDER:
        	   deleteEntitiesList.add(" order_meta_field_map ");
               break;
           case PAYMENT:
        	   deleteEntitiesList.add(" payment_meta_field_map ");
               break;
        }
        
        String deleteFromSql = "delete from ";
        String deleteWhereSql = " where meta_field_value_id in " +
                "(select val.id from meta_field_value val where meta_field_name_id = " + metaFieldId + " )";
        
        for (String deleteSingleEntity : deleteEntitiesList) {
        	
        	StringBuilder sqlBuilder = new StringBuilder();
        	sqlBuilder.append(deleteFromSql).append(deleteSingleEntity).append(deleteWhereSql);
        	session.createSQLQuery(sqlBuilder.toString()).executeUpdate();
        }

        String deleteValuesHql = "delete from " + MetaFieldValue.class.getSimpleName() + " where field.id = ?";
        getHibernateTemplate().bulkUpdate(deleteValuesHql, metaFieldId);
    }

    /**
     * Useful to delete meta field values for a given {@link EntityType} entityType and ID id
     * @param id
     * @param entityType
     * @param values
     */
    public void deleteMetaFieldValues(Integer id, EntityType entityType, List<MetaFieldValue> values) {
        Session session = getSession();
        List<String> deleteEntitiesList = new ArrayList<String>();
        
        String metaFieldValuesToDelete= "delete from meta_field_value where id in (";
        
        StringBuffer csvID= new StringBuffer();
        for(MetaFieldValue value: values) {
            csvID.append(value.getId()).append(',');
        }
        metaFieldValuesToDelete += csvID.substring(0, csvID.length()-1) + ")";
        
        switch (entityType) {
           case INVOICE:
               deleteEntitiesList.add(" invoice_meta_field_map where invoice_id = " + id);
               break;
           case CUSTOMER:
               deleteEntitiesList.add(" customer_meta_field_map where customer_id = " + id);
               deleteEntitiesList.add(" partner_meta_field_map where partner_id = " + id);
               break;
           case PRODUCT:
               deleteEntitiesList.add(" item_meta_field_map where item_id =" + id);
               break;
           case ORDER:
               deleteEntitiesList.add(" order_meta_field_map where order_id = " + id);
               break;
           case PAYMENT:
               deleteEntitiesList.add(" payment_meta_field_map where payment_id = " + id);
               break;
        }
        
        String deleteFromSql = "delete from ";
        for (String deleteSingleEntity : deleteEntitiesList) {
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append(deleteFromSql).append(deleteSingleEntity);
            session.createSQLQuery(sqlBuilder.toString()).executeUpdate();
        }
        session.createSQLQuery(metaFieldValuesToDelete).executeUpdate();
    }    
    
	public Long getFieldCountByDataTypeAndName(DataType dataType, String name) {
		Query query = getSession().createQuery(findCountByDTypeName);
		query.setParameter("dataType", dataType);
		query.setParameter("name", name);
		return (Long) query.uniqueResult();
	}

    /**
     * Method to search entities (Customer, Order, Product, Invoice etc) with matching Meta Field values
     * @param metaField
     * @param value - currently supported to search a string value, can be extended for others.
     * @return
     */
    public final List<Integer> findEntitiesByMetaFieldValue(MetaField metaField,
			String value) {
		List<Integer> customizedEntityList = null;
		Session session = getSession();
		try {
			String temp = "select val.id from meta_field_value val where meta_field_name_id="
					+ metaField.getId();
			switch (metaField.getDataType()) {
				case STRING:
					System.out.println("Data type is string.");
					temp += " and string_value='" + value + "'";
					break;
			}
			System.out.println("Query is: " + temp);
			List<Integer> values = session.createSQLQuery(temp).list();

			List<String> queries = new ArrayList<String>();
			if (!values.isEmpty()) {
				for (Integer id : values) {
					switch (metaField.getEntityType()) {
					case INVOICE:
						queries.add("select map.invoice_id from invoice_meta_field_map map, invoice i where map.meta_field_value_id = "
										+ id + " and map.invoice_id = i.id and i.deleted = 0");
						break;
					case CUSTOMER:
						queries.add("select customer_id from customer_meta_field_map where meta_field_value_id = "
										+ id + " and customer_id not in (select c.id from customer c, base_user bu where c.user_id = bu.id and bu.deleted > 0)");
						// queries.add("select partner_id from partner_meta_field_map where meta_field_value_id="
						// + id);
						break;
					case PRODUCT:
						queries.add("select map.item_id from item_meta_field_map map, item i where map.meta_field_value_id="
										+ id + " i.id = map.item_id and i.deleted = 0");
						break;
					case ORDER:
						queries.add("select map.order_id from order_meta_field_map map, purchase_order po where meta_field_value_id="
										+ id + " po.id = map.order_id and po.deleted = 0");
						break;
					case PAYMENT:
						queries.add("select map.payment_id from payment_meta_field_map map, payment p where meta_field_value_id="
										+ id + " p.id = map.payment_id and p.deleted = 0");
						break;
					}
				}
				customizedEntityList = new ArrayList<Integer>();
				for (String query : queries) {
					customizedEntityList.addAll(session.createSQLQuery(query)
							.list());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			// do something esle?
		}

		return customizedEntityList;
	}
    
    public Long countMetaFieldValuesForEntity(EntityType entityType, int metaFieldId) {
        Session session = getSession();
        List<String> entitiesList = new ArrayList<String>();

        switch (entityType) {
            case INVOICE:
                entitiesList.add(" invoice_meta_field_map ");
                break;
            case CUSTOMER:
                entitiesList.add(" customer_meta_field_map ");
                entitiesList.add(" partner_meta_field_map ");
                break;
            case PRODUCT:
                entitiesList.add(" item_meta_field_map ");
                break;
            case ORDER:
                entitiesList.add(" order_meta_field_map ");
                break;
            case PAYMENT:
                entitiesList.add(" payment_meta_field_map ");
                break;
        }

        List entityTypeList;
        Long count = 0L;
        String sql;
        String countSql = "select count(*) from ";
        String countWhereSql = " where meta_field_value_id in " +
                "(select val.id from meta_field_value val where meta_field_name_id = " + metaFieldId +
                " and ( boolean_value is not null or date_value is not null or decimal_value is not null or " +
                " integer_value is not null or (string_value is not null and string_value <> '') ) )";

        for (String entity : entitiesList) {
            sql = countSql+entity+countWhereSql;
            Number temp= (Number) session.createSQLQuery(sql).uniqueResult();
            System.out.println("Count from DB " + temp);
            count  = count + (temp == null ? 0L : temp.longValue());
        }
        
        return count;
    }
    
    public Long getTotalFieldCount(int metaFieldId){
        long totalCount = 0L;
        for(EntityType entityType : EntityType.values()){
            totalCount = totalCount + countMetaFieldValuesForEntity(entityType, metaFieldId);
        }

        return totalCount;
    }

    /**
     * Returns All IDs with matching criteria
     * @param dataType
     * @param name
     * @return
     */
    public List<Integer> getAllIdsByDataTypeAndName(DataType dataType, String name){
        Query query = getSession().createQuery(findAllIdsByDataTypeNameSQL);
        query.setParameter("dataType", dataType);
        query.setParameter("name", name);
        return   query.list();
    }

}
