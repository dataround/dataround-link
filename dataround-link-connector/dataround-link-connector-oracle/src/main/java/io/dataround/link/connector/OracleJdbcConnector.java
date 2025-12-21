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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import io.dataround.link.common.utils.ConnectorNameConstants;

import java.util.HashSet;

/**
 * Oracle JDBC connector
 * 
 * @author yuehan124@gmail.com
 * @since 2025-06-09
 */
@Slf4j
public class OracleJdbcConnector extends JdbcConnector {

    private final String name = ConnectorNameConstants.ORACLE;

    // Oracle system databases
    private static final Set<String> SYSTEM_DATABASES = new HashSet<>(Arrays.asList(
            "sys",
            "system",
            "sysaux",
            "temp",
            "users",
            "undotbs1",
            "apex_030200",
            "ctxsys",
            "dbsnmp",
            "dip",
            "flows_files",
            "hr",
            "mdsys",
            "oracle_ocm",
            "outln",
            "scott",
            "wmsys",
            "xdb",
            "xs$null"));

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public List<String> doGetDatabases() {
        List<String> databases = new ArrayList<>();
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            // Oracle doesn't have catalogs, use schemas instead
            try (ResultSet rs = metaData.getSchemas()) {
                while (rs.next()) {
                    String schemaName = rs.getString("TABLE_SCHEM");
                    if (schemaName != null && !SYSTEM_DATABASES.contains(schemaName.toLowerCase())) {
                        databases.add(schemaName);
                    }
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
        // For Oracle: catalog = null, schema = database name (user/schema name, usually uppercase)
        return getTablesWithParams(null, database.toUpperCase(), "%", new String[]{"TABLE", "VIEW"});
    }

    @Override
    public List<String> doGetTables(String database, String tableNamePattern) {
        // For Oracle: catalog = null, schema = database name (user/schema name, usually uppercase)
        return getTablesWithParams(null, database.toUpperCase(), tableNamePattern, new String[]{"TABLE", "VIEW"});
    }

    @Override
    public List<TableField> doGetTableFields(String database, String table) {
        // For Oracle: catalog = null, schema = database name (user/schema name, usually uppercase)
        return getTableFieldsWithParams(null, database.toUpperCase(), table, "%");
    }

    @Override
    public List<TableField> doGetTableFields(String database, String table, String columnNamePattern) {
        // For Oracle: catalog = null, schema = database name (user/schema name, usually uppercase)
        return getTableFieldsWithParams(null, database.toUpperCase(), table, columnNamePattern);
    }

}
