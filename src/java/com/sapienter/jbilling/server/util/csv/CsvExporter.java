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

package com.sapienter.jbilling.server.util.csv;

import au.com.bytecode.opencsv.CSVWriter;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.server.util.converter.BigDecimalConverter;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.List;

/**
 * CsvExporter
 *
 * @author Brian Cowdery
 * @since 03/03/11
 */
public class CsvExporter<T extends Exportable> implements Exporter<T> {

    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(CsvExporter.class));

    /** The maximum safe number of exportable elements to processes.  */
    public static final Integer MAX_RESULTS = 10000;

    static {
        ConvertUtils.register(new BigDecimalConverter(), BigDecimal.class);
    }

    private Class<T> type;

    private CsvExporter(Class<T> type) {
        this.type = type;
    }

    /**
     * Factory method to produce a new instance of CsvExporter for the given type.
     *
     * @param type type of exporter
     * @param <T> type T
     * @return new exporter of type T
     */
    public static <T extends Exportable> CsvExporter<T> createExporter(Class<T> type) {
        return new CsvExporter<T>(type);
    }

    public Class<T> getType() {
        return type;
    }

    public String export(List<? extends Exportable> list) {
        String[] header;

        // list can be empty, instantiate a new instance of type to
        // extract the field names for the CSV header
        try {
            header = type.newInstance().getFieldNames();
        } catch (InstantiationException e) {
            LOG.debug("Could not produce a new instance of " + type.getSimpleName() + " to build CSV header.");
            return null;

        } catch (IllegalAccessException e) {
            LOG.debug("Constructor of " + type.getSimpleName() + " is not accessible to build CSV header.");
            return null;
        }

        StringWriter out = new StringWriter();
        CSVWriter writer = new CSVWriter(out);
        writer.writeNext(header);

        for (Exportable exportable : list) {
            for (Object[] values : exportable.getFieldValues()) {
                writer.writeNext(convertToString(values));
            }
        }

        try {
            writer.close();
            out.close();
        } catch (IOException e) {
            LOG.debug("Writer cannot be closed, exported CSV may be missing data.");
        }

        return out.toString();
    }

    public String[] convertToString(Object[] objects) {
        String[] strings = new String[objects.length];

        int i = 0;
        for (Object object : objects) {
            if (object != null) {
                Converter converter = ConvertUtils.lookup(object.getClass());
                if (converter != null) {
                    strings[i++] = converter.convert(object.getClass(), object).toString();
                } else {
                    strings[i++] = object.toString();
                }
            } else {
                strings[i++] = "";
            }
        }

        return strings;
    }
}
