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

package io.dataround.link.job.config.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSONObject;

import io.dataround.link.entity.Connector;
import io.dataround.link.entity.enums.TableWriteTypeEnum;
import io.dataround.link.common.utils.ConnectorNameConstants;
import io.dataround.link.entity.res.FieldMapping;
import io.dataround.link.entity.res.JobRes;
import io.dataround.link.entity.res.TableMapping;
import io.dataround.link.utils.BeanConvertor;

/**
 * JDBC connector job configuration generator.
 * Handles configuration generation for JDBC-based connectors (MySQL, Oracle,
 * PostgreSQL, etc.).
 *
 * @author yuehan124@gmail.com
 * @since 2025-09-07
 */
@Component
public class JdbcJobConfigGenerator extends AbstractJobConfigGenerator {

    @Override
    public boolean supports(Connector connector) {
        return connector.getPluginName().startsWith("JDBC");
    }

    @Override
    public List<JSONObject> generateSourceConfig(GeneratorContext context) {
        JobRes jobVo = context.getJobVo();
        List<TableMapping> tableMappings = jobVo.getTableMapping();
        Map<String, String> sourceMap = BeanConvertor.connection2Map(context.getSourceConnection(), context.getSourceConnector());

        List<JSONObject> sources = new ArrayList<>();

        for (TableMapping table : tableMappings) {
            JSONObject source = new JSONObject(sourceMap);
            source.put("plugin_name", "Jdbc");
            source.put("connection_check_timeout_sec", 30);
            source.put("parallelism", 1);
            source.put("result_table_name", sourceResultTableName(table.getSourceTable(), jobVo.getId(), context));
            source.put("query", generateSourceQuery(table, context.getSourceConnector().getName()));
            sources.add(source);
        }

        return sources;
    }

    @Override
    public List<JSONObject> generateSinkConfig(GeneratorContext context) {
        JobRes jobVo = context.getJobVo();
        List<TableMapping> tableMappings = jobVo.getTableMapping();
        Map<String, String> targetMap = BeanConvertor.connection2Map(context.getTargetConnection(), context.getTargetConnector());

        List<JSONObject> sinks = new ArrayList<>();

        for (TableMapping table : tableMappings) {
            List<String> primaryKeys = table.getPrimaryKeyFields();
            JSONObject sink = new JSONObject(targetMap);
            sink.put("plugin_name", "Jdbc");
            sink.put("connection_check_timeout_sec", 30);
            sink.put("batch_size", 1000);
            sink.put("max_commit_attempts", 3);
            sink.put("max_retries", 1);
            // jdbc connector support upsert by primary key
            if (doUpsert(table)) {
                sink.put("primary_keys", primaryKeys.toArray());
                sink.put("enable_upsert", !primaryKeys.isEmpty() ? true : false);
            } else {
                // can speed up the job by disabling upsert
                sink.put("enable_upsert", false);
            }
            // the options('database') are required because ['generate_sink_sql' == true] is true
            sink.put("database", table.getTargetDbName());
            sink.put("source_table_name", prevStepResultTableName(context));
            sink.put("table", table.getTargetTable());
            sink.put("generate_sink_sql", true);
            sinks.add(sink);
        }

        return sinks;
    }

    private String generateSourceQuery(TableMapping tableMapping, String dialect) {
        String query = StringUtils.EMPTY;
        String tableName = getFullTableName(tableMapping.getSourceDbName(), tableMapping.getSourceTable());
        List<String> fields = getSourceFields(tableMapping.getSorteFieldMappings(), dialect);
        if (fields.isEmpty()) {
            query = "SELECT * FROM " + tableName;
        } else {
            query = "SELECT " + String.join(",", fields) + " FROM " + tableName;
        }
        String where = whereClause(tableMapping);
        return StringUtils.isEmpty(where) ? query : query + " " + where;
    }

    public boolean doUpsert(TableMapping tableMapping) {
        return TableWriteTypeEnum.UPSERT.getCode() == tableMapping.getWriteType();
    }

    public List<String> getSourceFields(List<FieldMapping> sorteFieldMappings, String dialect) {
        List<String> fields = new ArrayList<>();
        for (FieldMapping fm : sorteFieldMappings) {
            String fieldName = fm.getSourceFieldName();
            if (!fieldName.equals(fm.getTargetFieldName())) {
                fieldName = sqlDialect(dialect, fieldName) + " AS " + sqlDialect(dialect, fm.getTargetFieldName());
            } else {
                fieldName = sqlDialect(dialect, fieldName);
            }
            fields.add(fieldName);
        }
        return fields;
    }

    private String whereClause(TableMapping tableMapping) {
        if (StringUtils.isBlank(tableMapping.getWhereClause())) {
            return StringUtils.EMPTY;
        }
        String where = tableMapping.getWhereClause().trim();
        if (!where.toUpperCase().startsWith("WHERE")) {
            where = "WHERE " + where;
        }
        return where;
    }

    public String getFullTableName(String dbName, String tableName) {
        return dbName + "." + tableName;
    }

    private String sqlDialect(String dialect, String field) {
        switch (dialect) {
            case ConnectorNameConstants.MYSQL:
            case ConnectorNameConstants.MYSQL_CDC:
                return "`" + field + "`";
            case ConnectorNameConstants.ORACLE:
                return "\"" + field + "\"";
            case ConnectorNameConstants.POSTGRESQL:
                return "\"" + field + "\"";
            case ConnectorNameConstants.SQLSERVER:
                return "[" + field + "]";
            case ConnectorNameConstants.TIDB:
                return "`" + field + "`";
            default:
                return field;
        }
    }
}
