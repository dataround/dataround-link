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
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import io.dataround.link.common.utils.ConnectorNameConstants;
import lombok.extern.slf4j.Slf4j;

/**
 * Hive connector implementation
 * 
 * @author yuehan124@gmail.com
 * @since 2025-06-09
 */
@Slf4j
public class HiveConnector extends JdbcConnector {

    private final String name = ConnectorNameConstants.HIVE;
    
    // Hive system databases that should be filtered out
    private static final Set<String> SYSTEM_DATABASES = new HashSet<>(Arrays.asList(
        "information_schema",
        "sys"
    ));

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void doInitialize() throws Exception {
        Map<String, String> props = getParam().getConfig();
        int timeout = Integer.parseInt(props.getOrDefault("timeout", "30"));
        DriverManager.setLoginTimeout(timeout);
        // Call parent initialization to setup connection pool
        super.doInitialize();
        log.info("Successfully initialized Hive connector: {}", props.get("url"));
    }

    @Override
    public List<String> doGetDatabases() {
        // Call parent method to get all databases, then filter out system databases
        return super.doGetDatabases().stream()
                .filter(db -> !SYSTEM_DATABASES.contains(db.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public List<String> doGetTables(String database) {
        // For Hive: catalog = null, schema = database name
        return getTablesWithParams(null, database, "%", new String[]{"TABLE", "VIEW"});
    }

    @Override
    public List<String> doGetTables(String database, String tableNamePattern) {
        // For Hive: catalog = null, schema = database name, with table name pattern
        return getTablesWithParams(null, database, tableNamePattern, new String[]{"TABLE", "VIEW"});
    }

    @Override
    public List<TableField> doGetTableFields(String database, String table) {
        // For Hive: catalog = null, schema = database name
        return getTableFieldsWithParams(null, database, table, "%");
    }

    @Override
    public List<TableField> doGetTableFields(String database, String table, String columnNamePattern) {
        // For Hive: catalog = null, schema = database name, with column name pattern
        return getTableFieldsWithParams(null, database, table, columnNamePattern);
    }
    
    @Override
    public boolean doTestConnectivity() {
        try (Connection conn = dataSource.getConnection()) {
            // Test with a longer timeout for Hive as it might be slower
            return conn.isValid(10);
        } catch (SQLException e) {
            log.error("Failed to test Hive connectivity", e);
            return false;
        }
    }
} 