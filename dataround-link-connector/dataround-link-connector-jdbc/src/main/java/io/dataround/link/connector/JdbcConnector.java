/*
 * Copyright (C) 2025 yuehan124@gmail.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.dataround.link.connector;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import lombok.extern.slf4j.Slf4j;

/**
 * JDBC common connector
 * @author yuehan124@gmail.com
 * @date 2025-06-09
 */
@Slf4j
public class JdbcConnector extends AbstractTableConnector {

    private final String name = "JDBC";
    // JDBC connection pool
    protected DataSource dataSource;
    // JDBC connection properties
    private String driver;
    private String url;
    private String user;
    private String password;

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void doInitialize() throws Exception {
        Map<String, String> props = getParam().getConfig();
        // Validate required properties
        if (!props.containsKey("driver") || !props.containsKey("url")) {
            throw new IllegalArgumentException("Missing required properties: driver, url");
        }
        this.driver = props.get("driver");
        this.url = props.get("url");
        this.user = getParam().getUser();
        this.password = getParam().getPassword();
        // Load JDBC driver
        Class.forName(driver);
        // Initialize connection pool
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(driver);
        config.setJdbcUrl(url);
        config.setUsername(user);
        config.setPassword(password);
        // Set additional properties
        props.forEach((key, value) -> {
            if (!key.equals("driver") && !key.equals("url") &&
                !key.equals("username") && !key.equals("password") && !key.equals("libDir")) {
                config.addDataSourceProperty(key, value);
            }
        });
        
        dataSource = new HikariDataSource(config);
        // Test connection
        try (Connection conn = dataSource.getConnection()) {
            log.info("Successfully connected to database: {}", url);
        }
    }

    @Override
    public boolean doTestConnectivity() {
        try (Connection conn = dataSource.getConnection()) {
            // 5 seconds timeout
            return conn.isValid(5);
        } catch (SQLException e) {
            log.error("Failed to test connectivity", e);
            return false;
        }
    }

    @Override
    public List<String> doGetDatabases() {
        List<String> databases = new ArrayList<>();
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            try (ResultSet rs = metaData.getCatalogs()) {
                while (rs.next()) {
                    databases.add(rs.getString(1));
                }
            }
        } catch (SQLException e) {
            log.error("Failed to get databases", e);
            throw new RuntimeException("Failed to get databases", e);
        }
        return databases;
    }

    
    @Override
    public List<String> doGetTables(String database) {        
        return getTablesWithParams(null, database, "%", new String[]{"TABLE", "VIEW"});
    }

    @Override
    public List<String> doGetTables(String database, String tableNamePattern) {
        return getTablesWithParams(null, database, tableNamePattern, new String[]{"TABLE", "VIEW"});
    }

    /**
     * Protected method for subclasses to get tables with specific catalog and schema parameters
     * @param catalog the catalog name; null means get tables from all catalogs
     * @param schema the schema name; null means get tables from all schemas  
     * @param tableNamePattern the table name pattern; "%" means all tables
     * @param types the table types to include (e.g., "TABLE", "VIEW")
     * @return list of table names
     */
    protected List<String> getTablesWithParams(String catalog, String schema, String tableNamePattern, String[] types) {
        List<String> tables = new ArrayList<>();
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            try (ResultSet rs = metaData.getTables(catalog, schema, tableNamePattern, types)) {
                while (rs.next()) {
                    tables.add(rs.getString("TABLE_NAME"));
                }
            }
        } catch (SQLException e) {
            log.error("Failed to get tables and views with catalog={}, schema={}", catalog, schema, e);
            throw new RuntimeException("Failed to get tables and views", e);
        }
        return tables;
    }

    @Override
    public List<TableField> doGetTableFields(String database, String table) {
        return getTableFieldsWithParams(null, database, table, "%");
    }

    @Override
    public List<TableField> doGetTableFields(String database, String table, String columnNamePattern) {
        return getTableFieldsWithParams(null, database, table, columnNamePattern);
    }

    /**
     * Protected method for subclasses to get table fields with specific catalog and schema parameters
     * @param catalog the catalog name; null means get fields from all catalogs
     * @param schema the schema name; null means get fields from all schemas
     * @param table the table name
     * @param columnNamePattern the column name pattern; "%" means all columns
     * @return list of table fields
     */
    protected List<TableField> getTableFieldsWithParams(String catalog, String schema, String table, String columnNamePattern) {
        List<TableField> fields = new ArrayList<>();
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();            
            // Get primary keys
            List<String> primaryKeys = new ArrayList<>();
            try (ResultSet pkRs = metaData.getPrimaryKeys(catalog, schema, table)) {
                while (pkRs.next()) {
                    primaryKeys.add(pkRs.getString("COLUMN_NAME"));
                }
            }            
            // Get columns
            try (ResultSet rs = metaData.getColumns(catalog, schema, table, columnNamePattern)) {
                while (rs.next()) {
                    TableField field = new TableField();
                    String columnName = rs.getString("COLUMN_NAME");
                    field.setName(columnName);
                    field.setType(rs.getString("TYPE_NAME"));
                    field.setNullable(rs.getInt("NULLABLE") == DatabaseMetaData.columnNullable);
                    field.setComment(rs.getString("REMARKS"));
                    // Set primary key flag
                    field.setPrimaryKey(primaryKeys.contains(columnName));
                    fields.add(field);
                }
            }
        } catch (SQLException e) {
            log.error("Failed to get table fields with catalog={}, schema={}, table={}", catalog, schema, table, e);
            throw new RuntimeException("Failed to get table fields", e);
        }
        return fields;
    }

    @Override
    public void close() throws Exception {
        if (dataSource != null) {
            ((HikariDataSource) dataSource).close();
        }
    }
}
