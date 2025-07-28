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
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

/**
 * Hive connector implementation
 * @author yuehan124@gmail.com
 * @since 2025-06-09
 */
@Slf4j
public class HiveConnector extends AbstractTableConnector {

    private final String name = "Hive";
    private Connection connection;
    private String driverClassName;
    private String host;
    private Integer port;
    private String username;
    private String password;
    private String url;

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void doInitialize() throws Exception {
        Map<String, String> props = getParam().getConfig();
        host = getParam().getHost();
        port = getParam().getPort();
        username = getParam().getUser();
        password = getParam().getPassword();
        // Validate required properties
        if (!props.containsKey("url")) {
            url = "jdbc:hive2://" + host + ":" + port + "/default";
        } else {
            url = props.get("url");
        }
        driverClassName = props.getOrDefault("driver", "org.apache.hive.jdbc.HiveDriver");
        // Load Hive JDBC driver
        Class.forName(driverClassName);
        // Initialize connection
        connection = DriverManager.getConnection(url, username, password);
        log.info("Successfully connected to Hive: {}", url);
    }

    @Override
    public List<String> doGetDatabases() {
        List<String> databases = new ArrayList<>();
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            try (ResultSet rs = metaData.getCatalogs()) {
                while (rs.next()) {
                    databases.add(rs.getString(1));
                }
            }
        } catch (SQLException e) {
            log.error("Failed to get Hive databases", e);
            throw new RuntimeException("Failed to get Hive databases", e);
        }
        return databases;
    }

    @Override
    public List<String> doGetTables(String database) {
        List<String> tables = new ArrayList<>();
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            try (ResultSet rs = metaData.getTables(database, null, "%", new String[]{"TABLE", "VIEW"})) {
                while (rs.next()) {
                    tables.add(rs.getString("TABLE_NAME"));
                }
            }
        } catch (SQLException e) {
            log.error("Failed to get Hive tables and views", e);
            throw new RuntimeException("Failed to get Hive tables and views", e);
        }
        return tables;
    }

    @Override
    public List<String> doGetTables(String database, String schema) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'doGetTables'");
    }


    @Override
    public List<TableField> doGetTableFields(String database, String table) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'doGetTableFields'");
    }

    @Override
    public List<TableField> doGetTableFields(String database, String table, String schema) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'doGetTableFields'");
    }
    
    @Override
    public boolean doTestConnectivity() {
        try {
            return connection != null && !connection.isClosed() && connection.isValid(5);
        } catch (SQLException e) {
            log.error("Failed to test Hive connectivity", e);
            return false;
        }
    }

    @Override
    public void close() throws Exception {
        if (connection != null) {
            connection.close();
        }
    }
} 