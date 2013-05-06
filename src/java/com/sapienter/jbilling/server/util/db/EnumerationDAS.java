/**
 * 
 */
package com.sapienter.jbilling.server.util.db;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.sapienter.jbilling.server.util.db.AbstractDAS;

/**
 * @author Vikas Bodani
 * @since 10-Aug-2011
 *
 */
public class EnumerationDAS extends AbstractDAS<EnumerationDTO> {

	@SuppressWarnings("unchecked")
    public boolean exists(Integer id, String name, Integer entityId) {
        DetachedCriteria query = DetachedCriteria.forClass(getPersistentClass());
        query.add(Restrictions.ne("id", id));
        query.add(Restrictions.eq("name", name).ignoreCase());
        query.add(Restrictions.eq("entity.id", entityId));
        List<EnumerationDTO> enumerations = getHibernateTemplate().findByCriteria(query);
        return !enumerations.isEmpty() ? true : false;
    }
	
}
