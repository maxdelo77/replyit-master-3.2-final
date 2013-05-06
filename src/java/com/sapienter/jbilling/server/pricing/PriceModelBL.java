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

package com.sapienter.jbilling.server.pricing;

import com.sapienter.jbilling.common.CommonConstants;
import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.common.SessionInternalError;
import com.sapienter.jbilling.server.item.CurrencyBL;
import com.sapienter.jbilling.server.pricing.db.PriceModelDTO;
import com.sapienter.jbilling.server.pricing.db.PriceModelStrategy;
import com.sapienter.jbilling.server.pricing.util.AttributeUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author Brian Cowdery
 * @since 06-08-2010
 */
public class PriceModelBL {

    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(PriceModelBL.class));

    /**
     * Returns the given PriceModelDTO entity as a WS object
     *
     * @param dto PriceModelDTO to convert
     * @return plan price as a WS object, null if dto is null
     */
    public static PriceModelWS getWS(PriceModelDTO dto) {
        return dto != null ? new PriceModelWS(dto) : null;
    }

    /**
     * Returns the given list of PriceModelDTO entities as WS objects.
     *
     * @param dtos list of PriceModelDTO to convert
     * @return plan prices as WS objects, or an empty list if source list is empty.
     */
    public static List<PriceModelWS> getWS(List<PriceModelDTO> dtos) {
        if (dtos == null)
            return Collections.emptyList();

        List<PriceModelWS> ws = new ArrayList<PriceModelWS>(dtos.size());
        for (PriceModelDTO planPrice : dtos)
            ws.add(getWS(planPrice));
        return ws;
    }

    /**
     * Returns the given pricing time-line sorted map of PriceModelDTO entities as WS objects.
     *
     * @param dtos map of PriceModelDTO to convert
     * @return plan prices as WS objects, or an empty map if source map is empty.
     */
    public static SortedMap<Date, PriceModelWS> getWS(SortedMap<Date, PriceModelDTO> dtos) {
        SortedMap<Date, PriceModelWS> ws = new TreeMap<Date, PriceModelWS>();

        for (Map.Entry<Date, PriceModelDTO> entry : dtos.entrySet())
            ws.put(entry.getKey(), getWS(entry.getValue()));

        return ws;
    }

    /**
     * Returns the given WS object as a PriceModelDTO entity. This method
     * does not perform any saves or updates, it only converts between the
     * two data structures.
     *
     * @param ws web service object to convert
     * @return PriceModelDTO entity, null if ws is null
     */
    public static PriceModelDTO getDTO(PriceModelWS ws) {
        if (ws != null) {
            PriceModelDTO root = null;
            PriceModelDTO model = null;

            for (PriceModelWS next = ws; next != null; next = next.getNext()) {
                if (model == null) {
                    model = root = new PriceModelDTO(next, new CurrencyBL(next.getCurrencyId()).getEntity());
                } else {
                    model.setNext(new PriceModelDTO(next, new CurrencyBL(next.getCurrencyId()).getEntity()));
                    model = model.getNext();
                }
            }

            return root;
        }
        return null;
    }

    /**
     * Returns the given list of WS objects as a list of PriceModelDTO entities.
     *
     * @param ws list of web service objects to convert
     * @return list of converted PriceModelDTO entities, or an empty list if source list is empty.
     */
    public static List<PriceModelDTO> getDTO(List<PriceModelWS> ws) {
        if (ws == null)
            return Collections.emptyList();

        List<PriceModelDTO> dto = new ArrayList<PriceModelDTO>(ws.size());
        for (PriceModelWS price : ws)
            dto.add(getDTO(price));
        return dto;
    }

    /**
     * Returns the given pricing time-line sorted map of WS objects as a list of PriceModelDTO entities.
     *
     * @param ws map of web service objects to convert
     * @return map of converted PriceModelDTO entities, or an empty map if source is empty.
     */
    public static SortedMap<Date, PriceModelDTO> getDTO(SortedMap<Date, PriceModelWS> ws) {
        SortedMap<Date, PriceModelDTO> dto = new TreeMap<Date, PriceModelDTO>();

        for (Map.Entry<Date, PriceModelWS> entry : ws.entrySet())
            dto.put(entry.getKey(), getDTO(entry.getValue()));

        return dto;
    }


    /**
     * Validates that the given pricing model has all the required attributes and that
     * the given attributes are of the correct type.
     *
     * @param models pricing models to validate
     * @throws SessionInternalError if attributes are missing or of an incorrect type
     */
    public static void validateAttributes(Collection<PriceModelDTO> models) throws SessionInternalError {
        List<String> errors = new ArrayList<String>();

        for (PriceModelDTO model : models) {
            for (PriceModelDTO next = model; next != null; next = next.getNext()) {
                try {
                    AttributeUtils.validateAttributes(next.getAttributes(), next.getStrategy());
                } catch (SessionInternalError e) {
                    errors.addAll(Arrays.asList(e.getErrorMessages()));
                }
            }
        }

        if (!errors.isEmpty()) {
            throw new SessionInternalError("Price model attributes failed validation.",
                                           errors.toArray(new String[errors.size()]));
        }
    }

    public static void validateAttributes(PriceModelDTO model) throws SessionInternalError {
        validateAttributes(Arrays.asList(model));
    }

    /**
     * Validates that the given pricing model WS object has all the required attributes and that
     * the given attributes are of the correct type.
     *
     * @param models pricing model WS objects to validate
     * @throws SessionInternalError if attributes are missing or of an incorrect type
     */
    public static void validateWsAttributes(Collection<PriceModelWS> models) throws SessionInternalError {
        List<String> errors = new ArrayList<String>();

        for (PriceModelWS model : models) {
            for (PriceModelWS next = model; next != null; next = next.getNext()) {
                try {
                    PriceModelStrategy type = PriceModelStrategy.valueOf(next.getType());
                    AttributeUtils.validateAttributes(next.getAttributes(), type.getStrategy());
                } catch (SessionInternalError e) {
                    errors.addAll(Arrays.asList(e.getErrorMessages()));
                }
            }
        }

        if (!errors.isEmpty()) {
            throw new SessionInternalError("Price model attributes failed validation.",
                                           errors.toArray(new String[errors.size()]));
        }
    }

    public static void validateWsAttributes(PriceModelWS model) {
        validateWsAttributes(Arrays.asList(model));
    }


    /**
     * Searches through the list of PriceModelDTO objects for the price that is active
     * on the given date.
     *
     * If the given date is null, or if the closest date could not be determined,
     * the first price will be returned.
     *
     * @param prices price models to search through
     * @param date date to find price for
     * @return found price for date, or null if no price found
     */
    public static PriceModelDTO getPriceForDate(SortedMap<Date, PriceModelDTO> prices, Date date) {
        if (prices == null || prices.isEmpty()) {
        	LOG.debug("prices null or empty.");
            return null;
        }

        if (date == null) {
        	LOG.debug("returning first price from the prices list");
            return prices.get(prices.firstKey());
        }

        // list of prices in ordered by start date, earliest first
        // return the model with the closest start date
        Date forDate = CommonConstants.EPOCH_DATE;
        if (prices.firstKey().before(CommonConstants.EPOCH_DATE) ) {
        	//Additionall, Epoch Date is irrelavent in the this case
        	forDate= prices.firstKey();
        } 
        LOG.debug("First key " + prices.firstKey() + ", Price required for " + forDate);
        
        for (Date start : prices.keySet()) {
            if (start != null && start.after(date)) {
            	LOG.debug(start + " is after expected price date of " + date);
                break;
            }

            forDate = start;
        }
        LOG.debug("For date is set to " + forDate + ", returning: " + (forDate != null ? prices.get(forDate) : prices.get(prices.firstKey())) );
        return forDate != null ? prices.get(forDate) : prices.get(prices.firstKey());
    }

    /**
     * Searches through the list of PriceModelWS objects for the price that is active
     * on the given date.
     *
     * If the given date is null, or if the closest date could not be determined,
     * the first price will be returned.
     *
     * @param prices price models to search through
     * @param date date to find price for
     * @return found price for date, or null if no price found
     */
    public static PriceModelWS getWsPriceForDate(SortedMap<Date, PriceModelWS> prices, Date date) {
        if (prices == null || prices.isEmpty()) {
            return null;
        }

        if (date == null) {
            return prices.get(prices.firstKey());
        }

        // list of prices in ordered by start date, earliest first
        // return the model with the closest start date
        Date forDate = null;
        for (Date start : prices.keySet()) {
            if (start != null && start.after(date))
                break;

            forDate = start;
        }

        return forDate != null ? prices.get(forDate) : prices.get(prices.firstKey());
    }
}
