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
import io.dataround.link.entity.Connection;
import io.dataround.link.entity.res.FieldMapping;
import io.dataround.link.entity.res.JobRes;
import io.dataround.link.entity.res.TableMapping;
import io.dataround.link.utils.BeanConvertor;

/**
 * JDBC connector job configuration generator.
 * Handles configuration generation for JDBC-based connectors (MySQL, Oracle, PostgreSQL, etc.).
 *
 * @author yuehan124@gmail.com
 * @since 2025-09-07
 */
@Component
public class JdbcJobConfigGenerator implements JobConfigGenerator {

    @Override
    public boolean supports(Connector connector) {
        return connector.getPluginName().startsWith("JDBC");
    }

    @Override
    public List<JSONObject> generateSourceConfig(JobRes jobVo, Connection connection, Connector connector) {
        List<TableMapping> tableMappings = jobVo.getTableMapping();
        Map<String, String> sourceMap = BeanConvertor.connection2Map(connection, connector);

        List<JSONObject> sources = new ArrayList<>();
        
        for (TableMapping table : tableMappings) {
            JSONObject source = new JSONObject();
            source.put("plugin_name", "Jdbc");
            source.put("connection_check_timeout_sec", 30);
            source.put("parallelism", 1);
            source.put("result_table_name", tmpTableName(table.getSourceTable(), jobVo.getId()));
            source.put("query", query(table.getSourceTable(), jobVo));
            source.putAll(sourceMap);
            sources.add(source);
        }
        
        return sources;
    }

    @Override
    public List<JSONObject> generateSinkConfig(JobRes jobVo, Connection connection, Connector connector) {
        List<TableMapping> tableMappings = jobVo.getTableMapping();
        Map<String, String> targetMap = BeanConvertor.connection2Map(connection, connector);

        List<JSONObject> sinks = new ArrayList<>();
        
        for (TableMapping table : tableMappings) {
            JSONObject sink = new JSONObject();
            sink.put("plugin_name", "Jdbc");
            sink.put("connection_check_timeout_sec", 30);
            sink.put("batch_size", 1000);
            sink.put("max_commit_attempts", 3);
            sink.put("max_retries", 1);
            sink.put("support_upsert_by_query_primary_key_exist", false);
            sink.put("source_table_name", tmpTableName(table.getSourceTable(), jobVo.getId()));
            sink.put("database", table.getTargetDbName());
            sink.put("table", table.getTargetTable());
            sink.put("generate_sink_sql", true);
            sink.putAll(targetMap);
            sinks.add(sink);
        }
        
        return sinks;
    }

    private String query(String tableName, JobRes jobVo) {
        String query = StringUtils.EMPTY;
        List<String> fields = fields(tableName, jobVo);
        if (fields.isEmpty()) {
            query = "SELECT * FROM " + tableName;
        } else {
            query = "SELECT " + String.join(",", fields) + " FROM " + tableName;
        }
        String where = whereClause(tableName, jobVo);
        return StringUtils.isEmpty(where) ? query : query + " " + where;
    }

    private List<String> fields(String tableName, JobRes jobVo) {
        List<String> fields = new ArrayList<>();
        for (TableMapping tableMapping : jobVo.getTableMapping()) {
            if (tableMapping.getSourceTable().equals(tableName)) {
                List<FieldMapping> fieldMappings = tableMapping.getFieldMapping();
                for (FieldMapping fm : fieldMappings) {
                    String fieldName = fm.getSourceFieldName();
                    fields.add(sqlDialect(null, fieldName));
                }
                break;
            }
        }
        return fields;
    }

    private String whereClause(String tableName, JobRes jobVo) {
        String where = StringUtils.EMPTY;
        for (TableMapping tableMapping : jobVo.getTableMapping()) {
            if (tableMapping.getSourceTable().equals(tableName)) {
                where = tableMapping.getWhereClause();
                if (StringUtils.isNotEmpty(where) && !where.toUpperCase().startsWith("WHERE")) {
                    where = "WHERE " + where;
                }
                break;
            }
        }
        return where;
    }

    private String sqlDialect(String dialect, String field) {
        return field;
    }

    private String tmpTableName(String tableName, Long jobId) {
        return "Table_" + tableName + "_" + jobId;
    }
}
