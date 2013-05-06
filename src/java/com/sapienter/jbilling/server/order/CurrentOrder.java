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

package com.sapienter.jbilling.server.order;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;


import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.common.SessionInternalError;
import com.sapienter.jbilling.common.Util;
import com.sapienter.jbilling.server.order.db.OrderDAS;
import com.sapienter.jbilling.server.order.db.OrderDTO;
import com.sapienter.jbilling.server.user.EntityBL;
import com.sapienter.jbilling.server.user.UserBL;
import com.sapienter.jbilling.server.user.db.MainSubscriptionDTO;
import com.sapienter.jbilling.server.util.Constants;
import com.sapienter.jbilling.server.util.Context;
import com.sapienter.jbilling.server.util.MapPeriodToCalendar;
import com.sapienter.jbilling.server.util.PreferenceBL;
import com.sapienter.jbilling.server.util.audit.EventLogger;
import com.sapienter.jbilling.server.util.db.CurrencyDTO;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springmodules.cache.CachingModel;
import org.springmodules.cache.FlushingModel;
import org.springmodules.cache.provider.CacheProviderFacade;

public class CurrentOrder {
    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(CurrentOrder.class));

    private final EventLogger eLogger = EventLogger.getInstance();

    private final Date eventDate;
    private final Integer userId;
    private final UserBL user;

    // current order
    private OrderBL order = null;

    // cache management
    private CacheProviderFacade cache;
    private CachingModel cacheModel;
    private FlushingModel flushModel;

    public CurrentOrder(Integer userId, Date eventDate) {
        if (userId == null) throw new IllegalArgumentException("Parameter userId cannot be null!");
        if (eventDate == null) throw new IllegalArgumentException("Parameter eventDate cannot be null!");

        this.userId = userId;
        this.eventDate = eventDate;
        this.user = new UserBL(userId);

        cache = (CacheProviderFacade) Context.getBean(Context.Name.CACHE);
        cacheModel = (CachingModel) Context.getBean(Context.Name.CACHE_MODEL_RW);
        flushModel = (FlushingModel) Context.getBean(Context.Name.CACHE_FLUSH_MODEL_RW);

        LOG.debug("Current order constructed with user %s event date %s", userId, eventDate);
    }
    
    /**
     * Returns the ID of a one-time order, where to add an event.
     * Returns null if no applicable order
     *
     * @return order ID of the current order
     */
    public Integer getCurrent() {

        // find in the cache
        String cacheKey = userId.toString() + eventDate;
        Integer retValue = (Integer) cache.getFromCache(cacheKey, cacheModel);
        LOG.debug("Retrieved from cache '%s', order id: %s", cacheKey, retValue);

        // a hit is only a hit if the order is still active and is not deleted. Sometimes when the order gets deleted
        // it wouldn't be removed from the cache.
        OrderDTO cachedOrder = new OrderDAS().findByIdAndIsDeleted(retValue, false);
        if (cachedOrder != null && Constants.ORDER_STATUS_ACTIVE.equals(cachedOrder.getStatusId())) {
            LOG.debug("Cache hit for %s", retValue);
            return retValue;
        }

        MainSubscriptionDTO mainSubscription = user.getEntity().getCustomer().getMainSubscription();
        Integer entityId = null;
        Integer currencyId = null;
        if (mainSubscription == null) {
            return null;
        }

        // find user entity & currency
        try {
            entityId = user.getEntity().getCompany().getId();
            currencyId = user.getEntity().getCurrency().getId();
        } catch (Exception e) {
            throw new SessionInternalError("Error looking for user entity of currency",
                    CurrentOrder.class, e);
        }
        
        // if main subscription preference is not set 
        // do not use the main subscription
        if (!isMainSubscriptionUsed(entityId)) {
        	return null;
        }

        // loop through future periods until we find a usable current order
        int futurePeriods = 0;
        boolean orderFound = false;
        // create the order
        if (order == null) {
            order = new OrderBL();
        }
        do {
            final Date newOrderDate = calculateDate(futurePeriods, mainSubscription);
            LOG.debug("Calculated one timer date: " + newOrderDate + ", for future periods: " + futurePeriods);

            if (newOrderDate == null) {
                // this is an error, there isn't a good date give the event date and
                // the main subscription order
                LOG.error("Could not calculate order date for event. Event date is before the order active since date.");
                return null;
            }

            // now that the date is set, let's see if there is a one-time order for that date
            boolean somePresent = false;
            try {
                List<OrderDTO> rows = new OrderDAS().findOneTimersByDate(userId, newOrderDate);
                LOG.debug("Found %s one-time orders for new order date: %s", rows.size(), newOrderDate);
                for (OrderDTO oneTime : rows) {
                    somePresent = true;
                    order.set(oneTime.getId());
                    if (order.getEntity().getStatusId().equals(Constants.ORDER_STATUS_FINISHED)) {
                        LOG.debug("Found one timer %s but status is finished", oneTime.getId());
                    } else {
                        orderFound = true;
                        LOG.debug("Found existing one-time order");
                        break;
                    }
                }
            } catch (Exception e) {
                throw new SessionInternalError("Error looking for one time orders", CurrentOrder.class, e);
            }

            if (somePresent && !orderFound) {
                eLogger.auditBySystem(entityId, userId,
                                      Constants.TABLE_PUCHASE_ORDER,
                                      order.getEntity().getId(),
                                      EventLogger.MODULE_MEDIATION,
                                      EventLogger.CURRENT_ORDER_FINISHED,
                                      null, null, null);

            } else if (!somePresent) {
                // there aren't any one-time orders for this date at all, create one
                create(newOrderDate, currencyId, entityId);
                orderFound = true;
                LOG.debug("Created new one-time order");
            }

            // non present -> create new one with correct date
            // some present & none found -> try next date
            // some present & found -> use the found one
            futurePeriods++;
        } while (!orderFound);  
        
        // the result is in 'order'
        retValue = order.getEntity().getId();

        LOG.debug("Caching order %s with key '%s'", retValue, cacheKey);
        cache.putInCache(cacheKey, cacheModel, retValue);

        LOG.debug("Returning %s", retValue);
        return retValue;
    }
    
    /**
     * Assumes that main subscription already exists for the customer
     * @param futurePeriods date for N periods into the future
     * @param mainSubscription Customer main subscription
     * @return calculated period date for N future periods
     */
    private Date calculateDate(int futurePeriods, MainSubscriptionDTO mainSubscription) {
    	
        GregorianCalendar cal = new GregorianCalendar();

        // calculate the event date with the added future periods
        // default cal to actual event date
        Date actualEventDate = eventDate;
        cal.setTime(actualEventDate);
        
        for (int f = 0; f < futurePeriods; f++) {
        	cal.add(MapPeriodToCalendar.map(mainSubscription.getSubscriptionPeriod().getPeriodUnit().getId()), 
                                            mainSubscription.getSubscriptionPeriod().getValue());
        }
        // set actual event date based on future periods
        actualEventDate = cal.getTime();
        
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.add(Calendar.DATE, mainSubscription.getNextInvoiceDayOfPeriod() - 1);
        
        while (cal.getTime().after(actualEventDate)) {
        	cal.add(MapPeriodToCalendar.map(mainSubscription.getSubscriptionPeriod().getPeriodUnit().getId()), 
            		-mainSubscription.getSubscriptionPeriod().getValue());
        }

        return cal.getTime();
    }

    private boolean isMainSubscriptionUsed(Integer entityId) {
        PreferenceBL preferenceBL = new PreferenceBL();
        try {
            preferenceBL.set(entityId, Constants.PREFERENCE_USE_CURRENT_ORDER);
        } catch (EmptyResultDataAccessException e) {
            // default preference will be used
            }
        
        return preferenceBL.getInt() != 0;
	}

	/**
     * Creates a new one-time order for the given active since date.
     * @param activeSince active since date
     * @param currencyId currency of order
     * @param entityId company id of order
     * @return new order
     */
    public Integer create(Date activeSince, Integer currencyId, Integer entityId) {
        OrderDTO currentOrder = new OrderDTO();
        currentOrder.setCurrency(new CurrencyDTO(currencyId));

        // notes
        try {
            EntityBL entity = new EntityBL(entityId);
            ResourceBundle bundle = ResourceBundle.getBundle("entityNotifications", entity.getLocale());
            currentOrder.setNotes(bundle.getString("order.current.notes"));
        } catch (Exception e) {
            throw new SessionInternalError("Error setting the new order notes", CurrentOrder.class, e);
        } 

        currentOrder.setActiveSince(activeSince);
        
        // create the order
        if (order == null) {
            order = new OrderBL();
        }

        order.set(currentOrder);
        order.addRelationships(userId, Constants.ORDER_PERIOD_ONCE, currencyId);

        return order.create(entityId, null, currentOrder);
    }
}
