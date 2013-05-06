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

package com.sapienter.jbilling.server.util.sql;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Utilities for working directly with databases through JDBC connections and hand written SQL queries.
 *
 * It is recommended that you instantiate a JDBC connection using the Spring
 * {@link org.springframework.jdbc.datasource.DataSourceUtils} class instead of creating one manually.
 *
 * These methods expect the given connection to be open and accessible. None of these methods
 * will close the given connection - you must remember to close the connection in your enclosing class!
 * 
 * @author Brian Cowdery
 * @since 27-09-2010
 */
public class JDBCUtils {

    private static final Logger LOG = Logger.getLogger(JDBCUtils.class);

    /**
     * Returns the case-corrected table name for the given case-insensitive table name.
     * If the table does not exist, then this method will return null.
     *
     * @param connection connection to use for validating column name case
     * @param table table to validate
     * @return case-corrected table name
     * @throws java.sql.SQLException if connection fails or meta-data could not be retrieved
     */
    public static String correctTableName(Connection connection, String table) throws SQLException {

        LOG.debug("Calling correctTableName with parameters: " + connection + " and table: " + table);

        if (table == null)
            return null;

        List<String> corrected = correctTableNames(connection, new String[] { table });

        LOG.debug("Done Calling correctTableName with parameters: " + connection + " and table: " + table + "Result list: " + corrected);

        return !corrected.isEmpty() ? corrected.get(0) : null;
    }

    /**
     * Returns a list of case-corrected table names for the given case insensitive array
     * of table names. If the table does not exist, then it will be omitted from
     * the returned list.
     *
     * @param connection connection to use for validating table name case
     * @param tables tables to validate
     * @return list of case-corrected tables names
     * @throws java.sql.SQLException if connection fails or meta-data could not be retrieved
     */
    public static List<String> correctTableNames(Connection connection, String[] tables) throws SQLException {

        LOG.debug("Calling multi correctTableNames with parameters: " + connection + " and tables: " + tables);

        List<String> dbTables = getAllTableNames(connection);
        List<String> corrected = new ArrayList<String>(tables.length);

        for (String table : tables)
            for (String dbTable : dbTables)
                if (table.equalsIgnoreCase(dbTable))
                    corrected.add(dbTable);

        LOG.debug("Done Calling multi correctTableNames with parameters: " + connection + " and tables: " + tables + " Returning result: " + corrected);

        return corrected;
    }

    /**
     * Returns a list of all table names in the database schema accessible by
     * the given connection.
     *
     * @param connection connection
     * @return list of table names
     * @throws java.sql.SQLException if connection fails or meta-data could not be retrieved
     */
    public static List<String> getAllTableNames(Connection connection) throws SQLException {
        List<String> tables = new ArrayList<String>();
        
        ResultSet rs = connection.getMetaData().getTables(null, null, null, null);
        while (rs.next()) tables.add(rs.getString(3));
        rs.close();

        return tables;
    }

    /**
     * Returns the case-corrected column name for the given case-insensitive column name.
     * If the column does not exist on the given table, then this method will return null.
     *
     * @param connection connection to use for validating column name case
     * @param tableName table that contains the given columns
     * @param column column to validate
     * @return case-corrected column name
     * @throws java.sql.SQLException if connection fails or meta-data could not be retrieved
     */
    public static String correctColumnName(Connection connection, String tableName, String column) throws SQLException {

        LOG.debug("Calling correctColumnName with parameters: " + connection + " table: " + tableName + " and column: " + column);

        if (column == null) 
            return null;

        List<String> corrected = correctColumnNames(connection, tableName, new String[] { column });

        LOG.debug("Done Calling correctColumnName with parameters: " + connection + " table: " + tableName + " and column: " + column + " Result list" + corrected);

        return !corrected.isEmpty() ? corrected.get(0) : null;
    }

    /**
     * Returns a list of case-corrected column names for the given case insensitive array
     * of column names. If the column does not exist, then it will be omitted from
     * the returned list.
     *
     * @param connection connection to use for validating column name case
     * @param tableName table that contains the given columns
     * @param columns columns to validate
     * @return list of case-corrected column names
     * @throws java.sql.SQLException if connection fails or meta-data could not be retrieved
     */
    public static List<String> correctColumnNames(Connection connection, String tableName, String[] columns)
            throws SQLException {

        LOG.debug("Calling multiple correctColumnNames with parameters: " + connection + " table: " + tableName + " and columns: " + columns);
        
        List<String> dbColumns = getAllColumnNames(connection, tableName);
        List<String> corrected = new ArrayList<String>(columns.length);

        for (String column : columns)
            for (String dbColumn : dbColumns)
                if (column.equalsIgnoreCase(dbColumn))
                    corrected.add(dbColumn);

        LOG.debug("Done Calling multiple correctColumnNames with parameters: " + connection + " table: " + tableName + " and columns: " + columns + " Result: " + corrected);

        return corrected;
    }

    /**
     * Returns a list of all column names of the given table.
     *
     * @param connection connection
     * @param tableName table name
     * @return list of column names
     * @throws java.sql.SQLException if connection fails or meta-data could not be retrieved
     */
    public static List<String> getAllColumnNames(Connection connection, String tableName) throws SQLException {
        List<String> columns = new ArrayList<String>();

        ResultSet rs = connection.getMetaData().getColumns(null, null, tableName, null);
        while (rs.next()) columns.add(rs.getString("COLUMN_NAME"));
        rs.close();

        return columns;
    }


    /**
     * Converts a given piece of text into a proper, all-lowercase database object name. All
     * non-alphanumeric characters are removed and spaces or CamelCase delimited strings are
     * converted to underscores.
     *
     * Examples:
     * <literal>
     *     "somePropertyName" = "some_property_name"
     *      "A bit of text."  = "a_bit_of_text"
     * </literal>
     *      *
     * @param text text to convert
     * @return database object name
     */
    public static String toDatabaseObjectName(String text) {
        String[] tokens = StringUtils.splitByCharacterTypeCamelCase(text.replaceAll("[_. ]", ""));
        return StringUtils.join(tokens, "_").toLowerCase();
    }

    /***
     * Converts a string into a appropriate database object name with underscore in between.
     *
     * Examples :
     * HelloHowAreYou = hello_how_are_you
     * Hello How Are You = hello_how_are_you
     * hello how are you = hello_how_are_you
     * hello.how.are.you = hello_how_are_you
     * hello       how     are     you = hello_how_are_you
     * Hello       How     Are     You = hello_how_are_you
     * Hello_How_Are_You = hello_how_are_you
     * hello_how_are_you = hello_how_are_you
     *
     *
     * @param text
     * @return
     */
    public static String getDatabaseObjectName(String text){
        String[] tokens = StringUtils.splitByCharacterTypeCamelCase(text.replaceAll("[. ]", "_"));
        text = StringUtils.join(tokens, "_").toLowerCase();
        tokens = text.split("_");
        List<String> newTokens = new ArrayList<String>();
        for( String token : tokens){
            if(!StringUtils.isEmpty(token))
                newTokens.add(token);
        }
        
        return StringUtils.join(newTokens, "_").toLowerCase();
    }
}
