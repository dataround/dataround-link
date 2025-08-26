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

/**
 * SQLServer JDBC connector
 * 
 * @author yuehan124@gmail.com
 * @since 2025-06-09
 */
@Slf4j
public class SQLServerJdbcConnector extends JdbcConnector {

    private final String name = "SQLServer";

    // SQL Server system databases
    private static final Set<String> SYSTEM_DATABASES = new HashSet<>(Arrays.asList(
            "master",
            "model",
            "msdb",
            "tempdb",
            "resource",
            "distribution",
            "reportserver",
            "reportservertempdb"));

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
        // For SQL Server: catalog = database name, schema = null (get tables from all schemas)
        return getTablesWithParams(database, null, "%", new String[]{"TABLE", "VIEW"});
    }

    @Override
    public List<String> doGetTables(String database, String tableNamePattern) {
        // For SQL Server: catalog = database name, schema = null (get tables from all schemas)
        return getTablesWithParams(database, null, tableNamePattern, new String[]{"TABLE", "VIEW"});
    }

    @Override
    public List<TableField> doGetTableFields(String database, String table) {
        // For SQL Server: catalog = database name, schema = null (search in all schemas)
        // This will find the table regardless of which schema it's in
        return getTableFieldsWithParams(database, null, table, "%");
    }

    @Override
    public List<TableField> doGetTableFields(String database, String table, String columnNamePattern) {
        // For SQL Server: catalog = database name, schema = null (search in all schemas)
        // This will find the table regardless of which schema it's in
        return getTableFieldsWithParams(database, null, table, columnNamePattern);
    }

}
