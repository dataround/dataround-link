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

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

import io.dataround.link.common.utils.ConnectorNameConstants;

/**
 * ClickHouse JDBC connector
 * ClickHouse is a column-oriented OLAP database
 * 
 * @author yuehan124@gmail.com
 * @since 2026-04-25
 */
@Slf4j
public class ClickHouseJdbcConnector extends JdbcConnector {

    private final String name = ConnectorNameConstants.CLICKHOUSE;

    // ClickHouse system databases
    private static final Set<String> SYSTEM_DATABASES = new HashSet<>(Arrays.asList(
            "information_schema",
            "information_schema_utf8",
            "system"
    ));

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public List<String> doGetDatabases() {
        // call parent method to get all databases, then filter out system databases
        return super.doGetDatabases().stream()
                .filter(db -> !SYSTEM_DATABASES.contains(db.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public List<String> doGetTables(String database) {
        // For ClickHouse: catalog = null, schema = database name
        return getTablesWithParams(null, database, "%", new String[]{"TABLE", "VIEW"});
    }

    @Override
    public List<String> doGetTables(String database, String tableNamePattern) {
        // For ClickHouse: catalog = null, schema = database name
        return getTablesWithParams(null, database, tableNamePattern, new String[]{"TABLE", "VIEW"});
    }

    @Override
    public List<TableField> doGetTableFields(String database, String table) {
        // For ClickHouse: catalog = null, schema = database name
        return getTableFieldsWithParams(null, database, table, "%");
    }

    @Override
    public List<TableField> doGetTableFields(String database, String table, String columnNamePattern) {
        // For ClickHouse: catalog = null, schema = database name
        return getTableFieldsWithParams(null, database, table, columnNamePattern);
    }

}
