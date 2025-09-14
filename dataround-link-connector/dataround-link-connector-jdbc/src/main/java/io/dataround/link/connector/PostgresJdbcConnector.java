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
 * PostgreSQL JDBC connector
 * 
 * @author yuehan124@gmail.com
 * @since 2025-06-09
 */
@Slf4j
public class PostgresJdbcConnector extends JdbcConnector {

    private final String name = "PostgreSQL";

    // PostgreSQL system databases
    private static final Set<String> SYSTEM_DATABASES = new HashSet<>(Arrays.asList(
            "information_schema",
            "pg_catalog",
            "pg_toast",
            "pg_temp_1",
            "pg_toast_temp_1",
            "postgres",
            "template0",
            "template1"));

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
        // If you want to support cross-database references, you can uncomment the following code
        // List<String> databases = new ArrayList<>();
        // // Use PostgreSQL system catalog to get all databases
        // String sql = "SELECT datname FROM pg_database WHERE datistemplate = false AND datallowconn = true";
        // try (Connection conn = dataSource.getConnection();
        //         PreparedStatement stmt = conn.prepareStatement(sql);
        //         ResultSet rs = stmt.executeQuery()) {
        //     while (rs.next()) {
        //         String dbName = rs.getString("datname");
        //         // Filter out system databases
        //         if (!SYSTEM_DATABASES.contains(dbName.toLowerCase())) {
        //             databases.add(dbName);
        //         }
        //     }
        // } catch (SQLException e) {
        //     log.warn("Failed to get PostgreSQL databases using system catalog, Falling back to standard JDBC getCatalogs() method", e.getMessage());
        // }
        // return databases;
    }

    @Override
    public List<String> doGetTables(String database) {
        // For PostgreSQL: catalog = database name, schema = "public" (default schema)
        return getTablesWithParams(database, null, "%", new String[] { "TABLE", "VIEW" });
    }

    @Override
    public List<String> doGetTables(String database, String tableNamePattern) {
        // For PostgreSQL: catalog = database name, schema = "public" (default schema)
        return getTablesWithParams(database, null, tableNamePattern, new String[] { "TABLE", "VIEW" });
    }

    @Override
    public List<TableField> doGetTableFields(String database, String table) {
        // For PostgreSQL: catalog = database name, schema = "public" (default schema)
        return getTableFieldsWithParams(database, null, table, "%");
    }

    @Override
    public List<TableField> doGetTableFields(String database, String table, String columnNamePattern) {
        // For PostgreSQL: catalog = database name, schema = "public" (default schema)
        return getTableFieldsWithParams(database, null, table, columnNamePattern);
    }
 
}
